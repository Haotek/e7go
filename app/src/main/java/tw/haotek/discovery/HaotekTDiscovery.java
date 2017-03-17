package tw.haotek.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.st_LanSearchInfo2;

import java.util.ArrayList;
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
public class HaotekTDiscovery extends DefaultDiscovery {
    private static final String TAG = HaotekTDiscovery.class.getSimpleName();

    public HaotekTDiscovery(Context context) {
        super(context);
    }

    @Override
    public void connect() {
//        Camera.init();//FIXME   init TUTK  if not use will can not call JNI
        IOTCAPIs.IOTC_DeInitialize();
        IOTCAPIs.IOTC_Initialize2(0);
        mSendExecutor = Executors.newSingleThreadExecutor();
        mSendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    checkDeviceDB();//FIXME  get once   login   Device
                    sendDiscoveryRequest();
                    break;
                }
            }
        });
    }

    public Future<?>[] sendDiscoveryRequest() {
        Future<?>[] futures = new Future<?>[10];
        if (mSendExecutor == null) {
            mSendExecutor = Executors.newSingleThreadExecutor();
        }
        for (int i = 0; i < 1; ++i) {
            if (mSendExecutor == null) {
                continue;
            }
            futures[i] = mSendExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    fetchAllWifiDiscovery();
                }
            });
        }

        return futures;
    }

    private void fetchAllWifiDiscovery() {
        onResponse(IOTCAPIs.IOTC_Lan_Search2(new int[20], 10000));
    }

    private void checkDeviceDB() {//FIXME  get once   login   Device
        final String[] para = HaotekApplication.getContext().getResources().getStringArray(R.array.test_para);
        final DeviceManager manager = DeviceManager.getDeviceManager();
        final SQLiteDatabase db = DatabaseManager.getDatabaseManager().getReadableDatabase();
        final Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE, new String[]{"dev_mac", "dev_uid"}, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            final int ssidindex = cursor.getColumnIndexOrThrow("dev_mac");
            final int uidindex = cursor.getColumnIndexOrThrow("dev_uid");
            final String apssid = cursor.getString(ssidindex);
            final String uid = cursor.getString(uidindex);
            HaotekDevice device = HaotekDevice.fromDB(apssid, uid);
            if (device.getMACAddress() == null || device.getUID() == null) {
                device.setUID(uid);
                final String[] omac = apssid.split(":");
                device.setDeviceName(omac[3] + omac[4] + omac[5]);
                device.setMACAddress(apssid);
                device.setAPModePassword(para[0]);//FIXME GitUp only wifi  set AP mode PW @ here ?
                device.setUsername(para[1]);//FIXME  for auto login
                device.setPassword(para[2]);
            }
            DeviceManager.getDeviceManager().addDevice(device);
            DeviceManager.getDeviceManager().makeDeviceSeen(device);
            device.registerP2PAgent();//FIXME
            device.fetchEverything();
        }
        cursor.close();
    }


    public void onResponse(st_LanSearchInfo2[] tutklocalresponse) {
        Log.d(TAG, "onResponse");
        if (tutklocalresponse != null) {
            for (st_LanSearchInfo2 tutk : tutklocalresponse) {
                HaotekDevice device = HaotekDevice.fromHaotekTDiscovery(tutk);
//                DeviceManager.getDeviceManager().syncDeviceInfo(device);
//                if (!DeviceManager.getDeviceManager().isDeviceAtList(device)) {
                DeviceManager.getDeviceManager().addDevice(device);
                DeviceManager.getDeviceManager().makeDeviceSeen(device);
//                }
            }
        }
    }

    @Override
    public void disconnect() {
        if (mSendExecutor != null) {
            mSendExecutor.shutdownNow();
            mSendExecutor = null;
        }
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
