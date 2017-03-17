package tw.haotek.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import c.min.tseng.R;
import c.min.tseng.discovery.DefaultDiscovery;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import tw.haotek.HaotekApplication;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.managers.DatabaseManager;

/**
 * Created by Neo on 2015/11/27.
 */
public class HaotekDiscovery extends DefaultDiscovery {
    private static final String TAG = HaotekDiscovery.class.getSimpleName();
    private WifiManager mWifiManager;
    private BroadcastReceiver mReceiver;

    public HaotekDiscovery(Context context) {
        super(context);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void connect() {
        mWifiManager.setWifiEnabled(true);
        mSendExecutor = Executors.newSingleThreadExecutor();
        mSendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    checkDeviceDB();//FIXME  get once   login   Device
                    sendDiscoveryRequest();
                    break;
                }
            }
        });
    }

    public Future<?>[] sendDiscoveryRequest() {
        Future<?>[] futures = new Future<?>[1];
        if (mSendExecutor == null) {
            mSendExecutor = Executors.newSingleThreadExecutor();
        }
        futures[0] = mSendExecutor.submit(new Runnable() {
            @Override
            public void run() {
                fetchAllWifiDiscovery();
            }
        });
        return futures;
    }

    private void fetchAllWifiDiscovery() {
        final Resources resources = mContext.getResources();
        final String[] supportlist = resources.getStringArray(R.array.haotek_product_ssid_list);
        final List<ScanResult> scanResults = new ArrayList<ScanResult>();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final List<ScanResult> scanresults = mWifiManager.getScanResults();
                for (ScanResult result : scanresults) {
                    for (String supp : supportlist) {
                        Log.d(TAG, "Show BSSID : " + result.BSSID);
                        Log.d(TAG, "Show SSID : " + result.SSID);
                        Log.d(TAG, "Show Support : " + supp);
                        if (result.SSID.contains(supp) && !scanResults.contains(result)) {
                            scanResults.add(result);
                            Log.d(TAG, " Scan Results size  : " + scanResults.size());
                            onResponse(result);
                        }
                    }
                }
            }
        };
        mContext.registerReceiver(mReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    private void checkDeviceDB() {//FIXME  get once   login   Device
        final String[] para = HaotekApplication.getContext().getResources().getStringArray(R.array.test_para);
        final DeviceManager manager = DeviceManager.getDeviceManager();
        final SQLiteDatabase db = DatabaseManager.getDatabaseManager().getReadableDatabase();
//        Cursor cursor = db.query(true, DatabaseManager.TABLE_DEVICE, new String[]{"dev_ap_ssid"}, null, null, null, null, null, null);
        final Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE, new String[]{"dev_ap_ssid", "dev_uid"}, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            final int ssidindex = cursor.getColumnIndexOrThrow("dev_ap_ssid");
            final int uidindex = cursor.getColumnIndexOrThrow("dev_uid");
            final String apssid = cursor.getString(ssidindex);
            final String uid = cursor.getString(uidindex);
//            Device device = manager.getHaotekDeviceByAPBSSID(apssid);
//            if (device == null) {
//                device = new HaotekDevice();//FIXME
//            }
//            if (uid!=null){
//                device.setUID(uid);
//            }
//            device.setAPModeBSSID(apssid);
//            device.setMACAddress(apssid);
//            device.setAPModePassword(para[0]);//FIXME GitUp only wifi  set AP mode PW @ here ?
//            device.setUsername(para[1]);//FIXME  for auto login
//            device.setPassword(para[2]);
//            device.setDeviceP2PAgent();
//            manager.addDevice(device);//FIXME
        }
        cursor.close();
    }

    public void onResponse(ScanResult result) {
        HaotekDevice device = HaotekDevice.fromHaotekDiscovery(result);
//        if (!DeviceManager.getDeviceManager().isDeviceAtList(device)) {
        DeviceManager.getDeviceManager().addDevice(device);
//        }
    }

    @Override
    public void disconnect() {
        if (mSendExecutor != null) {
//            Log.v(TAG, "disconnect(): shutting down send executor...");
            mSendExecutor.shutdownNow();
            mSendExecutor = null;
        }
        if (mContext != null && mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
//        Log.v(TAG, "disconnect(): bye~");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences.getBoolean(key, true)) {
            start();
        } else {
            stop();
            final DeviceManager manager = DeviceManager.getDeviceManager();
            final int count = manager.getDeviceCount();
            ArrayList<Device> devices = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                Device device = manager.getDeviceByIndex(i);
                if (device instanceof HaotekDevice) {
                    devices.add(device);
                }
            }
            for (Device device : devices) {
                manager.removeDevice(device);
            }
        }
    }
}
