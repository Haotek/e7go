package tw.haotek.app.e7go.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.managers.DeviceObserver;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.C;
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
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2015/11/25.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {
    private static final String TAG = DeviceListAdapter.class.getSimpleName();
    private HaotekCallback mCallback;
    private ArrayList<Device> mDeviceList = new ArrayList<>();
    final DeviceObserver mDeviceObserver = new DeviceObserver() {
        @Override
        public void onDeviceAdded(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceRemoved(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDeviceMadeSeen(Device device) {
            Log.d(TAG, "onDeviceMadeSeen");
            onDevicesChanged();
        }

        @Override
        public void onDeviceMadeUnseen(Device device) {
            onDevicesChanged();
        }

        @Override
        public void onDevicesChanged() {
            Log.d(TAG, "onDevicesChanged  ");
            mCallback.removeCallbacks(mUpdateRunnable);
            mCallback.postDelayed(mUpdateRunnable, C.TIMEOUT_UI_POST_HOLDOFF);
        }

        @Override
        public void onDeviceContentChanged(Device device, Uri uri) {
            Log.d(TAG, "onDeviceContentChanged Uri : " + uri);
            onDevicesChanged();
        }
    };

    final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
//            mDeviceList.clear();
//            checkDeviceDB();
            checkDevice();
            notifyDataSetChanged();
        }
    };

    //FIXME for set Date & Time command
    @Expose(serialize = false, deserialize = false)
    private ExecutorService mNetworkTaskExecutor = Executors.newSingleThreadExecutor(); //FIXME

    private static final SimpleDateFormat sFull = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public DeviceListAdapter(HaotekCallback callback) {
        this.mCallback = callback;
        mDeviceObserver.onDevicesChanged();
    }

    private void checkDevice() {
        final DeviceManager manager = DeviceManager.getDeviceManager();
        final int count = manager.getSeenDeviceCount();
        Log.d(TAG, "Show seen count : " + count);
        mDeviceList.clear();
        for (int i = 0; i < count; ++i) {
            final Device device = manager.getSeenDevice(i);
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device);
            }
        }
    }

    public void startMonitor() {
        DeviceManager.getDeviceManager().registerObserver(mDeviceObserver);
    }

    public void stopMonitor() {
        DeviceManager.getDeviceManager().unregisterObserver(mDeviceObserver);
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDeviceList.isEmpty() || position >= mDeviceList.size()) {
            return R.layout.item_add_new_device;
        } else {
            return R.layout.item_devicelist;
        }
    }

    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View child = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case R.layout.item_devicelist:
                return new DeviceViewHolder(child);
            case R.layout.item_add_new_device:
                return new BlackViewHolder(child);
        }
        throw new IllegalArgumentException("no such view type...");
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case R.layout.item_devicelist:
                onBindDeviceViewHolder((DeviceViewHolder) holder, position);
                break;
            case R.layout.item_add_new_device:
                onBindBlankViewHolder((BlackViewHolder) holder, position);
                break;
        }
    }

    private void onBindBlankViewHolder(final BlackViewHolder holder, int position) {
        holder.add_new_device.setImageResource(R.drawable.ic_add_new);
        holder.add_new_device.setOnClickListener(new View.OnClickListener() {//FIXME  the touck go where ???
            @Override
            public void onClick(View v) {
                mCallback.selectItem(null, 4);
            }
        });
    }

    private void onBindDeviceViewHolder(final DeviceViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
//        final Resources resources = context.getResources();
//        final String[] products = resources.getStringArray(R.array.haotek_product_ssid_list);//FIXME for check Haotek Product
        final WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo info = wifimanager.getConnectionInfo();
//        if (position % 2 == 1) {//FIXME chang color
//            holder.itemView.setBackgroundColor(R.color.e7go_video_function_bg_color);
//        }else {
//            holder.itemView.setBackgroundColor(R.color.e7go_white);
//        }

        Log.d(TAG, "onBindDeviceViewHolder  DeviceList size : " + mDeviceList.size());
        final Device device = mDeviceList.get(position);
        if (device != null) {
            holder.title.setText(device.getDeviceName());
            Log.d(TAG, "device BSSID : " + device.getAPModeBSSID());
            Log.d(TAG, "Connect WifiInfo  BSSID : " + info.getBSSID());
//            if (device.isLoggedIn()) {//FIXME   SSID use WifiInfo ?
//                holder.state.setImageResource(android.R.drawable.button_onoff_indicator_on);
//            }
            if (info.getBSSID() != null && (info.getBSSID().equalsIgnoreCase(device.getAPModeBSSID()))) {
                Log.d(TAG, "WiFi Info BSSID :  " + info.getBSSID());
                Log.d(TAG, "Device BSSID :  " + device.getAPModeBSSID());
                holder.state.setImageResource(R.drawable.ic_state_on);
            }
            Log.d(TAG, "Show Device mode : " + device.getP2PMode());
            if (device.getP2PMode()) {
                Log.d(TAG, "TUTK/P2PMode");
                holder.cloud.setVisibility(View.VISIBLE);
                holder.state.setImageResource(R.drawable.ic_state_on);
                holder.cloudsetting.setVisibility(View.VISIBLE);
            }
//            if (device.getAPModeWiFiCFG().SSID != null && !device.getAPModeWiFiCFG().SSID.contains(device.getModelName())) {
////               for(String haotek:products){FIXME for check Haotek Product
////
////               }
//                final int pid = android.os.Process.myPid();
//                android.os.Process.killProcess(pid);
//                System.exit(0);
//            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Resources resources = HaotekApplication.getContext().getResources();
                final String[] para = resources.getStringArray(R.array.test_para);
                if (device.getAPModeWiFiCFG() != null || device.getP2PMode()) {//FIXME from db tutk autologin
                    device.setAPModePassword(para[0]);//FIXME GitUp only wifi  set AP mode PW @ here ?
//                device.setPassword(para[0]);//FIXME  SSID  device PW is AP pw but UID PW ?
                    device.setUsername(para[1]);//FIXME  for auto login
                    device.setPassword(para[2]);//FIXME SSID  device PW is AP pw but UID PW ?
//                    if (device.getDeviceP2PAgent()!=null){//FIXME  for tutk agent auto login
                    //                    }
//                device.registerP2PAgent();//FIXME
//                device.reLogin(new Device.OnLoginResultListener(new Handler()) {
//                    @Override
//                    public void onLoginSuccess(Device device) {
//                        mCallback.selectItem(device, 2);
//                        Log.d(TAG, "onLoginSuccess()");
//                        device.fetchEverything();
//                    }

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
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        Log.d(TAG, "onViewRecycled() ");
        super.onViewRecycled(holder);
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class BlackViewHolder extends ViewHolder {
        private ImageView add_new_device;

        public BlackViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            add_new_device = (ImageView) itemView.findViewById(R.id.add_new_device);
        }
    }

    public static class DeviceViewHolder extends ViewHolder {
        private ImageView icon;
        private ImageView cloud;
        private TextView title;
        private TextView mac;
        private ImageView state;
        private ImageView cloudsetting;

        public DeviceViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            cloud = (ImageView) itemLayoutView.findViewById(R.id.cloud);
            icon = (ImageView) itemLayoutView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            mac = (TextView) itemView.findViewById(R.id.mac);
            state = (ImageView) itemLayoutView.findViewById(R.id.state);
            cloudsetting = (ImageView) itemLayoutView.findViewById(R.id.cloudsetting);
        }
    }
}