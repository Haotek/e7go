package tw.haotek.app.e7go.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.managers.DeviceObserver;
import c.min.tseng.util.Create;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.device.SetDeviceDateCommand;
import tw.haotek.command.HaotekW.device.SetDeviceTimeCommand;
import tw.haotek.command.HaotekW.module.video.CheckVideoRecStateCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoRecordingCommand;
import tw.haotek.command.Tutk.SetDeviceDateTCommand;
import tw.haotek.command.Tutk.SetDeviceTimeTCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.video.CheckVideoRecStateTCommand;
import tw.haotek.command.Tutk.video.StartVideoRecordingTCommand;
import tw.haotek.command.Tutk.video.StopVideoRecordingTCommand;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.managers.DatabaseManager;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2015/11/28.
 */
public class APModeDeviceAdapter extends RecyclerView.Adapter<APModeDeviceAdapter.ViewHolder> {
    private final String TAG = APModeDeviceAdapter.class.getSimpleName();
    private HaotekCallback mCallback;
    private int mCount;
    private Handler mHandler = new Handler();
    private DeviceObserver mDeviceObserver = new DeviceObserver() {
        @Override
        public void onDeviceAdded(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceRemoved(Device device) {

        }

        @Override
        public void onDeviceMadeSeen(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceMadeUnseen(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDevicesChanged() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCount = DeviceManager.getDeviceManager().getUnseenDeviceCount();
                    Log.d(TAG, "Show onDevicesChanged UnseenDeviceCount :  " + mCount);
                    Log.d(TAG, "Show onDevicesChanged  seenDeviceCount :  " + DeviceManager.getDeviceManager().getSeenDeviceCount());
                    Log.d(TAG, "Show onDevicesChanged  DeviceCount :  " + DeviceManager.getDeviceManager().getDeviceCount());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDeviceContentChanged(Device device, Uri uri) {
            onDevicesChanged();
        }
    };

    //FIXME for set Date & Time command
    @Expose(serialize = false, deserialize = false)
    private ExecutorService mNetworkTaskExecutor = Executors.newSingleThreadExecutor(); //FIXME

    private static final SimpleDateFormat sFull = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public APModeDeviceAdapter(HaotekCallback callback) {
        super();
        this.mCallback = callback;
        mDeviceObserver.onDevicesChanged();
    }

    public void startMonitor() {
        DeviceManager.getDeviceManager().registerObserver(mDeviceObserver);
    }

    public void stopMonitor() {
        DeviceManager.getDeviceManager().unregisterObserver(mDeviceObserver);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    @Override
    public APModeDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_single_choice, parent, false);
        ViewHolder vh = new ViewHolder(child);
        return vh;
    }

    @Override
    public void onBindViewHolder(final APModeDeviceAdapter.ViewHolder holder, int position) {
        try {
            final Context context = holder.itemView.getContext();
            final Resources resources = context.getResources();
            final String[] para = resources.getStringArray(R.array.test_para);
            final WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final Device device = DeviceManager.getDeviceManager().getUnseenDevice(position);
            holder.ssid.setText(device.getDeviceName());
            holder.ssid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.ssid.setChecked(true);
                    device.setAPModePassword(para[0]);
                    final WifiConfiguration srcWc = device.getAPModeWiFiCFG();
                    if (srcWc != null) {
                        srcWc.preSharedKey = Create.quoteNonHex(para[0], 64);//FIXME if NONE/WEP ?
                        device.setAPModeWiFiCFG(srcWc);
//                        device.reLogin(new Device.OnLoginResultListener(new Handler()) {
//                            @Override
//                            public void onLoginSuccess(Device device) {
//                                final DatabaseManager dbmanager = DatabaseManager.getDatabaseManager();//FIXME　
//                                final long db_id = dbmanager.addDevice(device.getAPModeBSSID(), device.getMACAddress(), device.getUID(), "Model", device.getAPModePassword(), device.getUsername(), device.getPassword(), 0, 0, true);
//                                Log.d(TAG, "db_id  : " + db_id);
//                                mCallback.selectItem(device, 2);
//                                Log.d(TAG, "onLoginSuccess()");
//                                device.fetchEverything();
//                            }
//
//                            @Override
//                            public void onLoginFailed(Device device, Exception ex) {
//                                Log.d(TAG, "onLoginFailed()");
////                            device.setPassword(null);
////                            HaotekApplication.makeToast(
////                                    Toast.LENGTH_LONG,
//////                               R.string.pincode_dialog_login_failed
////                                    ex.getLocalizedMessage()
////                            ).show();
//                            }
//                        });
                        srcWc.networkId = wifimanager.addNetwork(srcWc);
                        wifimanager.enableNetwork(srcWc.networkId, true);
                        final boolean b = wifimanager.reconnect();
                        if (!b) {
                            wifimanager.reconnect(); //FIXME retry
                        } else {
                            DeviceManager.getDeviceManager().makeDeviceSeen(device);
                            wifimanager.saveConfiguration();
                            device.setUsername(para[1]);
                            device.setPassword(para[2]);
                            final long db_id = DatabaseManager.getDatabaseManager().addDevice(device.getAPModeBSSID(), device.getMACAddress(), device.getUID(), device.getDeviceName(), device.getAPModePassword(), device.getUsername(), device.getPassword(), 0, 0, true);
                            Log.d(TAG, "db_id  : " + db_id);
                            // //FIXME for  SyncTime  but  synctime need stop stream !!!  the rtsp will  stop  !!!!
                            final String[] mAllTimes = sFull.format(System.currentTimeMillis()).split("_");
                            mNetworkTaskExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand((HaotekDevice) device);
                                        final StopVideoRecordingTCommand stopt = new StopVideoRecordingTCommand((HaotekDevice) device, 0);
                                        stopt.setResponseListener(new TutkCommand.ResponseListener() {
                                            @Override
                                            public void dispatchResponse(Object result) {
                                            }
                                        });
                                        final StartVideoRecordingTCommand startt = new StartVideoRecordingTCommand((HaotekDevice) device, 1);
                                        startt.setResponseListener(new TutkCommand.ResponseListener() {
                                            @Override
                                            public void dispatchResponse(Object result) {
                                                mCallback.selectItem(device, 2);
                                            }
                                        });
                                        final SetDeviceDateTCommand sdate = new SetDeviceDateTCommand((HaotekDevice) device, mAllTimes[0]);
                                        sdate.setResponseListener(new TutkCommand.ResponseListener() {
                                            @Override
                                            public void dispatchResponse(Object result) {

                                            }
                                        });
                                        final SetDeviceTimeTCommand stime = new SetDeviceTimeTCommand((HaotekDevice) device, mAllTimes[1]);
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


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand((HaotekDevice) device, HaotekCommand.GET_Info);
                                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand((HaotekDevice) device, HaotekCommand.GET_Info, 0);
                                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand((HaotekDevice) device, HaotekCommand.GET_Info, 1);
                                        final SetDeviceDateCommand cmdd = new SetDeviceDateCommand((HaotekDevice) device, HaotekCommand.GET_Info, mAllTimes[0]);
                                        final SetDeviceTimeCommand cmdt = new SetDeviceTimeCommand((HaotekDevice) device, HaotekCommand.GET_Info, mAllTimes[1]);
                                        final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                                        if (gresponse.mValue.equals("1")) {
                                            stop.run();
                                            final SetDeviceDateCommand.Response responsed = (SetDeviceDateCommand.Response) cmdd.run();
                                            final SetDeviceTimeCommand.Response responset = (SetDeviceTimeCommand.Response) cmdt.run();
                                            final SetVideoRecordingCommand.Response startresponse = (SetVideoRecordingCommand.Response) start.run();
                                            if (startresponse.mList.mState.equals("0")) {
                                                mCallback.selectItem(device, 2);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //
                            device.fetchEverything();
                        }
                    }

                }
            });
//            holder.itemView.setOnClickListener(new View.OnClickListener() {//FIXME  the way is show dialog ask password !!! GitUp need  ? or AutoLogin?
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        } catch (IndexOutOfBoundsException ignored) {
            Log.d(TAG, "Show Exception " + ignored);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView ssid;

        public ViewHolder(View itemView) {
            super(itemView);
            ssid = (CheckedTextView) itemView;
        }
    }
}
