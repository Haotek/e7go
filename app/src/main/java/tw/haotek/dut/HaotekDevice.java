package tw.haotek.dut;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.st_LanSearchInfo2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.util.Check;
import c.min.tseng.util.Create;
import c.min.tseng.util.ThrowingRunnable;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.C;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.device.GetDeviceInfoCommand;
import tw.haotek.command.HaotekW.device.SetDeviceDateCommand;
import tw.haotek.command.HaotekW.device.SetDeviceTimeCommand;
import tw.haotek.command.HaotekW.module.video.CheckVideoRecStateCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoRecordingCommand;
import tw.haotek.command.Tutk.GetDeviceInfoTCommand;
import tw.haotek.command.Tutk.SearchWiFiAPTCommand;
import tw.haotek.command.Tutk.SetDeviceDateTCommand;
import tw.haotek.command.Tutk.SetDeviceTimeTCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.video.CheckVideoRecStateTCommand;
import tw.haotek.command.Tutk.video.StartVideoRecordingTCommand;
import tw.haotek.command.Tutk.video.StopVideoRecordingTCommand;
import tw.haotek.dut.data.DeviceInfo;
import tw.haotek.managers.DatabaseManager;
import tw.haotek.p2pAgent.TutkAgent;

/**
 * Created by Neo on 2015/11/27.
 */
//public class HaotekDevice extends Device implements IRegisterIOTCListener {//FIXME  change to use command not IOTCListener
public class HaotekDevice extends Device {
    private static final String TAG = HaotekDevice.class.getSimpleName();
    protected final Object mModuleLock = new Object();
    private final Object mLoginLock = new Object();
    private String mHardwareVersion;
    private ArrayList<Module> mModules = new ArrayList<>();
    private ExecutorService mNetworkTaskExecutor = Executors.newSingleThreadExecutor();

    private static class SharedPreferencesDataEnclosure {
        public String username;
        public String password;
        public String ssid;
        public String mac;
        public String uid;

        public SharedPreferencesDataEnclosure(String username, String password, String ssid, String mac, String uid) {
            this.username = username;
            this.password = password;
            this.ssid = ssid;
            this.mac = mac;
            this.uid = uid;
        }
    }

    //FIXME   for IRegisterIOTCListener
    private Camera mAgent;//FIXME
    //FIXME
    public static final int REQUEST_CODE_LOGIN = 7;
    public static final int REQUEST_CODE_LOGIN_QUIT = 8;
    public int LastAudioMode;
    private int mMonitorIndex = -1;
    private int mEventCount = 0;
    private int cbSize = 0;
    private int nIsSupportTimeZone = 0;
    private int nGMTDiff = 0;
    private byte[] szTimeZoneString = new byte[256];
    private boolean bIsMotionDetected;
    private boolean bIsIOAlarm;
    public int mEventNotification;
    //    private List<AVIOCTRLDEFs.SStreamDef> mStreamDefs = Collections.synchronizedList(new ArrayList<AVIOCTRLDEFs.SStreamDef>());//FIXME
    protected static int mSessionChannel = -1;
    public List<AVIOCTRLDEFs.SWifiAp> mAPlist = new ArrayList<AVIOCTRLDEFs.SWifiAp>();//FIXME

    private static final SimpleDateFormat sFull = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public HaotekDevice() {
        super();
    }

    public static HaotekDevice fromHaotekDiscovery(ScanResult result) {
        final DeviceManager manager = DeviceManager.getDeviceManager();
//        Device device = manager.getDeviceByAPBSSID(result.BSSID);
        Device device = manager.getDeviceByMac(result.BSSID);
        if (device == null) {
            device = new HaotekDevice();//FIXME
        }
        if (!(device instanceof HaotekDevice)) {
            manager.removeDevice(device);
            device = null;
            device = new HaotekDevice();//FIXME remove old device
        }
        ((HaotekDevice) device).updateFromWiFiInfo(result);
        return (HaotekDevice) device;
    }

    public static HaotekDevice fromHaotekTDiscovery(st_LanSearchInfo2 iotc_Lan_Search_Result) {
        final DeviceManager manager = DeviceManager.getDeviceManager();
        Log.d(TAG, "Show UID  : " + new String(iotc_Lan_Search_Result.UID));
//        Device device = manager.getDeviceByUID(new String(iotc_Lan_Search_Result.UID));//chexk device
        Device device = null;
        final String[] rawaname = new String(iotc_Lan_Search_Result.DeviceName).split(",");
        if (rawaname.length > 2) {
            device = manager.getDeviceByMac(rawaname[0]);//chexk device
        }
        if (device == null) {
            device = new HaotekDevice();
        }
        if (!(device instanceof HaotekDevice)) {
            manager.removeDevice(device);
            device = null;
            device = new HaotekDevice();//FIXME remove old device
        }
        ((HaotekDevice) device).updateFromTutk(iotc_Lan_Search_Result);
        return (HaotekDevice) device;
    }

    public static HaotekDevice fromDB(String apbssid, String uid) {
        Log.d(TAG, "fromDB  Show  MAC : " + apbssid + "_UID : " + uid);
        final DeviceManager manager = DeviceManager.getDeviceManager();
//        Device device = manager.getDeviceByAPBSSID(result.BSSID);
        Device device = manager.getDeviceByMac(apbssid);
        if (device == null) {
            device = new HaotekDevice();//FIXME
        }
        if (!(device instanceof HaotekDevice)) {
            manager.removeDevice(device);
            device = null;
            device = new HaotekDevice();//FIXME remove old device
        }
        return (HaotekDevice) device;
    }

    private void updateFromWiFiInfo(ScanResult result) {
        Log.d(TAG, "updateFromWiFiInfo ");
        setInetAddress("192.168.1.254");//FIXME  need RTSP WiFi discovry
        synchronized (mUpdateLock) {
            setDeviceName(result.SSID);
            setAPModeBSSID(result.BSSID);
            setMACAddress(result.BSSID);
            setAPModeWiFiCFG(Create.CreateWifiInfo(result.SSID, "", Check.CheckWifiInfoType(result.capabilities.replace("[", "").replace("]", "_"))));
        }
        dispatchChange(false, "update_device_basic_data_from_wifiinfo");
    }

    private void updateFromTutk(st_LanSearchInfo2 iotc_Lan_Search_Result) {
        Log.d(TAG, "updateFromTutk  ");

        final String[] para = HaotekApplication.getContext().getResources().getStringArray(R.array.test_para);//FIXME
        synchronized (mUpdateLock) {
            if (iotc_Lan_Search_Result != null) {
                setUID(new String(iotc_Lan_Search_Result.UID));
                setDeviceP2PAgent();
                setInetAddress(new String(iotc_Lan_Search_Result.IP));
                setModelName(new String(iotc_Lan_Search_Result.DeviceName));
                final String[] rawaname = new String(iotc_Lan_Search_Result.DeviceName).split(",");
                if (rawaname.length > 2) {
                    setMACAddress(rawaname[0]);//FIXME tutk no command ?
                    setModelName(rawaname[1]); //FIXME  Devicename ? Modelname ? port for what ? now get "" !!!!
                    setFirmwareVersion(rawaname[2]);
                    final String[] omac = rawaname[0].split(":");
                    setDeviceName(rawaname[1] + "-" + omac[3] + omac[4] + omac[5]);
                    setAPModeBSSID(rawaname[0]);
                }
                setAPModePassword(para[0]);
                setUsername(para[1]);//FIXME  for auto login
                setPassword(para[2]);
                //FIXME  for auto login
                final long db_id = DatabaseManager.getDatabaseManager().addDevice(getAPModeBSSID(), getMACAddress(), getUID(), getDeviceName(), getAPModePassword(), getUsername(), getPassword(), 0, 0, true);
                Log.d(TAG, "db_id  : " + db_id);
                mEventNotification = 3;
            }
        }

        dispatchChange(false, "update_device_basic_data_from_tutk_local_scan");
    }

    @Override
    public void setUsername(String username) {
        super.setUsername(username);
        saveToSharedPreferences();
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        saveToSharedPreferences();
    }

    @Override
    public void setUID(String uid) {
        super.setUID(uid);
        saveToSharedPreferences();
    }

    @Override
    public Object getDeviceP2PAgent() {
        //FIXME  how to return mAgent  ?
        return mAgent;
    }

    @Override
    public void setDeviceP2PAgent() {
        if (mAgent == null) {
            mAgent = new Camera();
            Log.d(TAG, this + "setDeviceP2PAgent() : " + mAgent);
            Log.d(TAG, this + "SID ?  : " + mAgent.getMSID());
            //FIXME for test
//            TutkAgent yy = new TutkAgent(this);
            TutkAgent yy = new TutkAgent();
            Log.d(TAG, this + " new Agent SID ?  : " + yy.getSID());
//            yy.setUID(getUID());
//            Log.d(TAG, this + " new Agent SID ?  : " + yy.getSID());
//            Log.d(TAG, this + " new Agent  Login result :   " + yy.login());
        }
    }

    @Override
    protected void resetCredentials(boolean quiet) {

    }

    @Override
    public Module getModule(int position) {
        return null;
    }

    @Override
    public int getModuleCount() {
        synchronized (mModuleLock) {
            return mModules.size();
        }
    }

    @Override
    public String getModelDescription() {
        return null;
    }

    @Override
    public Module getModuleById(int id) {
        synchronized (mModuleLock) {
            for (Module module : mModules) {
                if (module.getModuleID() == id) {
                    return module;
                }
            }
        }
        return null;
    }

    @Override
    public Module getModuleByIndex(int index) {
        synchronized (mModuleLock) {
            if (index >= getModuleCount()) {
                return null;
            }
            return mModules.get(index);
        }
    }

    @Override
    public String getSerialNumber() {
        return null;
    }

    @Override
    public void registerP2PAgent() {//FIXME    this is tutk p2p login !!!
        if (mAgent == null) {
            setDeviceP2PAgent();
        }
        Log.d(TAG, "@ registerP2PAgent  UID :  " + getUID());
        mAgent.connect(getUID());
        mAgent.start(Camera.DEFAULT_AV_CHANNEL, getUsername(), getPassword());
//        mAgent.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
//        mAgent.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
//        mAgent.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
        Log.d(TAG, "Set Agent ");
        Log.d(TAG, this + "registerP2PAgent SID ?  : " + mAgent.getMSID());
    }

    @Override
    public void unregisterP2PAgent() {
        if (mAgent != null) {
            Log.d(TAG, mAgent + "  unregisterIOTCListener  ");
//            mAgent.unregisterIOTCListener(this);
            dispatchChange(false, "unregisterP2PAgent");
        }
    }

    @Override
    public boolean isLoggedIn() {
        return mLogin;
//        return mConnecting == CONNECTION_STATE_CONNECTED;//FIXME
    }

    @Override
    public void setLogin(boolean login) {//FIXME
        if (mLogin != login) {
            synchronized (mLoginLock) {
                mLogin = login;
            }
            //FIXME  for test
            DeviceManager.getDeviceManager().makeDeviceSeen(this);
            mObservable.dispatchChange(false);
        }
    }

    @Override
    public int getConnectState() {
        return mConnecting;
    }

    @Override
    public void setConnectState(int state) {
        if (mConnecting != state) {
            Log.d(TAG, "setConnectState state : " + state);
            synchronized (mUpdateLock) {
                mConnecting = state;
//                setLogin(state == CONNECTION_STATE_CONNECTED);
            }
            dispatchChange(false, "connectstate");
        }
    }

    @Override
    public void reLogin(OnLoginResultListener listener) {
        resetCredentials(true);
        login(listener);
    }

    public void login(final OnLoginResultListener listener) {
        executeNetworkTask(new Runnable() {
            @Override
            public void run() {
                loginBlocking(listener);
            }
        });
    }

    public Future<?> executeNetworkTask(Runnable task) {
        return mNetworkTaskExecutor.submit(task);
    }

    @Override
    public Future<?> executeNetworkTaskWithLoginGuard(final ThrowingRunnable task) {
        return executeNetworkTask(new Runnable() {
            @Override
            public void run() {
                try {
                    loginGuard(task);
                } catch (IOException ex) {
                    Log.wtf(TAG, "executeNetworkTaskWithLoginGuard(): loginGuard failed for " + getMACAddress() + " wrong password?", ex);
                } catch (Exception ex) {
                    Log.wtf(TAG, "executeNetworkTaskWithLoginGuard(): loginGuard failed for " + getMACAddress() + " by some unknown exception, This really shouldn't happen!", ex);
                }
            }
        });
    }

    public void loginGuard(ThrowingRunnable runnable) throws Exception {//FIXME TUTK login use tutk jni code
        try {
            loginBlocking(null);
            runnable.run();
        } catch (Exception ex) {
            resetCredentials(true);
            loginBlocking(null);
            runnable.run();
        }
    }

    private void loginBlocking(final OnLoginResultListener listener) {
        final WifiManager wifimanager = (WifiManager) HaotekApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        if (isLoggedIn()) {
            return;
        }
//        final WifiConfiguration srcWc = this.getAPModeWiFiCFG();//FIXME if multi Device will let App change multi WiFi
//        if (srcWc != null) {
//            srcWc.networkId = wifimanager.addNetwork(srcWc);
//            wifimanager.enableNetwork(srcWc.networkId, true);
//            final boolean b = wifimanager.reconnect();
//            if (!b) {
//                wifimanager.reconnect(); //FIXME retry
//            } else {
//                wifimanager.saveConfiguration();
//                dispatchChange(false, "login");
//                mLogin = true;
//            }
//        }

        try {
            if (mAgent != null && getUID() != null) {
                Log.d(TAG, "TutkLogin");
                registerP2PAgent();
//                mAgent.connect(getUID());
//                mAgent.start(Camera.DEFAULT_AV_CHANNEL, getUsername(), getPassword());
//                Log.d(TAG, "Set Agent ");
                //FIXME up command not need  ? Tutk will auto re login
                if (listener != null) {
                    listener.dispatchLoginResult(HaotekDevice.this, null);
                }
                dispatchChange(false, "loginp2p");
            }
        } catch (Exception ex) {
            if (listener != null) {
                listener.dispatchLoginResult(HaotekDevice.this, ex);
            }
        }
    }

    @Override
    public void saveToSharedPreferences() {
//        Log.d(TAG, "saveToSharedPreferences");
        final Context context = HaotekApplication.getContext();
        if (context != null) {
            final SharedPreferences prefs = context.getSharedPreferences(C.SHAREDPREFERENCES_DEVICES, Context.MODE_MULTI_PROCESS);
            final SharedPreferences.Editor editor = prefs.edit();
            if (getUsername() == null || getPassword() == null) {
                editor.remove(getAPModeBSSID());
                editor.remove(getMACAddress());
                editor.remove(getUID());
            } else {
                editor.putString(getMACAddress(), sGson.toJson(new SharedPreferencesDataEnclosure(getUsername(), getPassword(), getAPModeBSSID(), getMACAddress(), getUID())));//FIXME  search by MAC or SSID ?
            }
            editor.apply();
        }
    }

    @Override
    public void restoreFromSharedPreferences() {
        Log.d(TAG, "restoreFromSharedPreferences");
        final Context context = HaotekApplication.getContext();
        if (context != null) {
            final SharedPreferences prefs = context.getSharedPreferences(C.SHAREDPREFERENCES_DEVICES, Context.MODE_MULTI_PROCESS);//FIXME  search by MAC or SSID ?
            String serialized = prefs.getString(getMACAddress(), null);
//            Log.d(TAG, "get  SharedPreferences by Mac : " + serialized);
            if (serialized == null) {
                serialized = prefs.getString(getUID(), null);
//                Log.d(TAG, "get  SharedPreferences by uid : " + serialized);
                if (serialized == null) {
                    serialized = prefs.getString(getAPModeBSSID(), null);
//                    Log.d(TAG, "get  SharedPreferences by BSSID : " + serialized);
                    if (serialized == null) {
                        return;
                    }
                }
            }
            SharedPreferencesDataEnclosure enclosure = sGson.fromJson(serialized, SharedPreferencesDataEnclosure.class);
            synchronized (mUpdateLock) {
                setUsername(enclosure.username);
                setPassword(enclosure.password);
                setUID(enclosure.uid);
            }
            resetCredentials(false);
        }
    }

    @Override
    public Future<?>[] fetchEverything() {
        return new Future[]{
                fetchDeviceSettings(),
                fetchModuleProfiles(),
//                fetchDeviceSyncTime(),//FIXME   if use  SyncTime will no stream !!!!  but we need sync time
        };
    }

    public Future<?> fetchModuleProfiles() {
        return executeNetworkTaskWithLoginGuard(
                new ThrowingRunnable() {
                    @Override
                    public void run() throws IOException {
                        fetchModuleProfilesBlocking();
                    }
                });
    }

    protected void fetchModuleProfilesBlocking() throws IOException {
        final ArrayList<Module> modules = new ArrayList<>();
        synchronized (mUpdateLock) {
            final Resources resources = HaotekApplication.getContext().getResources();
            final String[] product = resources.getStringArray(R.array.haotek_product_list);
//                final int count = product.length;
//                int oem = R.array.default_fake_module_list;
            int oem = R.array.gitup_fake_module_list;
//                for (int i = 0; i < count; ++i){//FIXME  get fake module list
//                    final String si = product[i];
//                    if (si.equalsIgnoreCase(getVendorName())) {
//                        oem = R.array.tywin_fake_module_list;
//                    }
//                }
            final String[] para = resources.getStringArray(oem);
            for (int i = 0; i < para.length; ++i) {
                final String si = para[i];
                gentFakeModule(true, si, modules);
            }
        }
        synchronized (mModuleLock) {
            clearModule(true);
            for (Module module : modules) {
                addModule(module, true);
            }
            mModules.trimToSize();
        }
        dispatchChange(false, "basicsetting");
        for (Module module : mModules) {
            module.fetchEverything();
        }
//        final ArrayList<Module> modules = new ArrayList<>();
//        GetModuleProfilesCommand cmd = new GetModuleProfilesCommand(this, HaotekCommand.GET_Info);
//        final GetModuleProfilesCommand.Response response = (GetModuleProfilesCommand.Response) cmd.run();
//        Log.d(TAG, "response.mList.size  : " + response.mList.size());
//        synchronized (mUpdateLock) {
//            for (ModuleState state : response.mList) {//FIXME
//                int moduleint = Integer.valueOf(state.mModuleName);
//                Log.d(TAG," module int :  "+moduleint );
//                switch (moduleint) {
//                    case WiFiCommandDefine.MOVIE_REC_SIZE:
//                        gentFakeModule(true, "Video", modules);
//                        break;
//                    case WiFiCommandDefine.MOVIE_AUDIO:
//                        gentFakeModule(true, "Audio", modules);
//                        break;
//                    case WiFiCommandDefine.GET_CARD_STATUS:
//                        gentFakeModule(true, "Storage", modules);
//                        break;
//                    case WiFiCommandDefine.MOVIE_GSENSOR_SENS:
//                        gentFakeModule(true, "GSensor", modules);
//                        break;
//                    case WiFiCommandDefine.GET_BATTERY_LEVEL:
//                        gentFakeModule(true, "Battery", modules);
//                        break;
//                }
//            }
//        }
//        synchronized (mModuleLock) {
//            clearModule(true);
//            for (Module module : modules) {
//                addModule(module, true);
//            }
//            mModules.trimToSize();
//        }
//        dispatchChange(false, "moduleprofiles");
//        for (Module module : mModules) {
//            module.fetchEverything();
//        }
    }

    public Future<?> fetchDeviceSettings() {
        return executeNetworkTaskWithLoginGuard(
                new ThrowingRunnable() {
                    @Override
                    public void run() throws IOException {
                        try {
                            fetchDeviceSettingsBlocking();
                        } catch (Exception e) {
                        }
                        try {
                            fetchDeviceSettingsTBlocking();
                        } catch (Exception e) {
                        }
                    }
                });
    }

    protected void fetchDeviceSettingsBlocking() throws IOException {
        Log.d(TAG, "fetchDeviceSettingsBlocking()  ");
        GetDeviceInfoCommand cmd = new GetDeviceInfoCommand(this, HaotekCommand.GET_Info);
        final GetDeviceInfoCommand.Response response = (GetDeviceInfoCommand.Response) cmd.run();
        synchronized (mUpdateLock) {
            this.setModelName(response.mList.mModel);
            this.setMACAddress(response.mList.mMAC.toUpperCase());
            this.setAPModeBSSID(response.mList.mMAC.toUpperCase());
            this.setUID(response.mList.mUID);
            this.setFirmwareVersion(response.mList.mFirmwareVersion);
            this.setP2PMode(response.mList.mMode);
        }
        final long db_id = DatabaseManager.getDatabaseManager().addDevice(getAPModeBSSID(), getMACAddress(), getUID(), getDeviceName(), getAPModePassword(), getUsername(), getPassword(), 0, 0, true);
        Log.d(TAG, "db_id  : " + db_id);
    }

    protected void fetchDeviceSettingsTBlocking() throws IOException {
        Log.d(TAG, "fetchDeviceSettingsTBlocking()  ");
        GetDeviceInfoTCommand tcmd = new GetDeviceInfoTCommand(this);
        tcmd.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                final DeviceInfo list = (DeviceInfo) result;//FIXME
                if (list != null && list.mModel != null && list.mMAC != null && list.mUID != null && list.mFirmwareVersion != null && list.mMode != null) {//FIXME
                    synchronized (mUpdateLock) {
                        HaotekDevice.this.setModelName(list.mModel);
                        HaotekDevice.this.setMACAddress(list.mMAC.toUpperCase());
                        HaotekDevice.this.setAPModeBSSID(list.mMAC.toUpperCase());
                        HaotekDevice.this.setUID(list.mUID);
                        HaotekDevice.this.setFirmwareVersion(list.mFirmwareVersion);
                        HaotekDevice.this.setP2PMode(list.mMode);
                    }
                    final long db_id = DatabaseManager.getDatabaseManager().addDevice(HaotekDevice.this.getAPModeBSSID(), HaotekDevice.this.getMACAddress(), HaotekDevice.this.getUID(), HaotekDevice.this.getDeviceName(), HaotekDevice.this.getAPModePassword(), HaotekDevice.this.getUsername(), HaotekDevice.this.getPassword(), 0, 0, true);
                    Log.d(TAG, "db_id  : " + db_id);
                }
            }
        });
        tcmd.run();

        SearchWiFiAPTCommand searchcmd = new SearchWiFiAPTCommand(this);
        searchcmd.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                mAPlist = (List<AVIOCTRLDEFs.SWifiAp>) result;//FIXME
                Log.d(TAG, "Show AP List Size  : " + mAPlist.size());
            }
        });
        searchcmd.run();
    }

    private Future<?> fetchDeviceSyncTime() {
        return executeNetworkTaskWithLoginGuard(
                new ThrowingRunnable() {
                    @Override
                    public void run() throws IOException {
                        try {
                            fetchDeviceSyncTimeBlocking();
                        } catch (Exception e) {
                        }
                        try {
                            fetchDeviceSyncTimeTBlocking();
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void fetchDeviceSyncTimeBlocking() throws IOException {
        Log.d(TAG, "fetchDeviceSyncTimeBlocking()  ");
        final String[] alltime = sFull.format(System.currentTimeMillis()).split("_");
        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(this, HaotekCommand.GET_Info);
        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(this, HaotekCommand.GET_Info, 0);
        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(this, HaotekCommand.GET_Info, 1);
        final SetDeviceDateCommand cmdd = new SetDeviceDateCommand(this, HaotekCommand.GET_Info, alltime[0]);
        final SetDeviceTimeCommand cmdt = new SetDeviceTimeCommand(this, HaotekCommand.GET_Info, alltime[1]);
        final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
        if (gresponse.mValue.equals("1")) {
            stop.run();
            final SetDeviceDateCommand.Response responsed = (SetDeviceDateCommand.Response) cmdd.run();
            final SetDeviceTimeCommand.Response responset = (SetDeviceTimeCommand.Response) cmdt.run();
            start.run();
        }
    }

    private void fetchDeviceSyncTimeTBlocking() throws IOException {
        Log.d(TAG, "fetchDeviceSyncTimeTBlocking()  ");
        final String[] alltime = sFull.format(System.currentTimeMillis()).split("_");
        final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(this);
        final StopVideoRecordingTCommand stopt = new StopVideoRecordingTCommand(this, 0);
        stopt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
            }
        });
        final StartVideoRecordingTCommand startt = new StartVideoRecordingTCommand(this, 1);
        startt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
            }
        });
        final SetDeviceDateTCommand sdate = new SetDeviceDateTCommand(this, alltime[0]);
        sdate.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {

            }
        });
        final SetDeviceTimeTCommand stime = new SetDeviceTimeTCommand(this, alltime[1]);
        stime.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {

            }
        });
        checkt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                final String[] rawdata = (String[]) result;
                final String[] data = rawdata[0].split(",");
                if (data[2].equals("1")) {
                    stopt.run();
                }
                sdate.run();
                stime.run();
                if (data[2].equals("1")) {
                    startt.run();
                }
            }
        });
        checkt.run();
    }

    public List<AVIOCTRLDEFs.SWifiAp> getmAPlist() {//FIXME
        return mAPlist;
    }

    private void gentFakeModule(boolean generate, String moduleType, ArrayList<Module> modules) {
        if (generate) {
            final Module module = HaotekModule.fromFakedataesponse(this, moduleType);
            if (module != null) {
                final int moduleid = modules.size();
                module.setModuleID(moduleid);
                modules.add(module);
            }
        }
    }

    public void addModule(Module module, boolean quiet) {
        boolean notify = false;
        synchronized (mModuleLock) {
            if (!mModules.contains(module)) {
                try {
                    module.registerContentObserver(mObserver);
                } catch (IllegalStateException ignored) {
                }
                mModules.add(module);
                if (!quiet) {
                    notify = true;
                }
            }
        }
        if (notify) {
            dispatchChange(false, "addmodule");
        }
    }

    public void removeModule(Module module, boolean quiet) {
        boolean notify = false;
        synchronized (mModuleLock) {
            module.unregisterContentObserver(mObserver);
            if (mModules.remove(module) && !quiet) {
                notify = true;
            }
        }
        if (notify) {
            dispatchChange(false, "removemodule");
        }
    }

    protected void clearModule(boolean quiet) {
        boolean notify = false;
        synchronized (mModuleLock) {
            for (Module module : mModules) {
                module.unregisterContentObserver(mObserver);
                notify = true;
            }
            mModules.clear();
        }
        if (notify) {
            dispatchChange(false, "clearmodule");
        }
    }

    @Override
    public void fetchPolicy() {

    }

    @Override
    public void fetchSchedule() {

    }

    @Override
    public void fetchInternet() {

    }

    @Override
    public void fetchWLan() {

    }

    @Override
    public void fetchNetWork() {

    }

    @Override
    public void fetchGroup() {

    }

    @Override
    public void fetchSiteSurvey() {

    }

    @Override
    public void fetchGetSystemLog() {

    }

    @Override
    public void fetchDeviceTime() {

    }

    @Override
    public void pushCleanSystemLog() {

    }

    @Override
    public void pushFWUpdate() {

    }

    @Override
    public void reboot() {

    }

    @Override
    public void factoryReset() {

    }
}
