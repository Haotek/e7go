package tw.haotek.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tw.haotek.HaotekApplication;

/**
 * Created by Neo on 2015/11/29.
 */
public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    protected static final DatabaseManager sDatabaseManager = new DatabaseManager();
    public static final String TABLE_DEVICE = "device";
    public static final String TABLE_DEVICE_CHANNEL = "device_channel";
    public static final String TABLE_DEVICE_CHANNEL_ALLOCATION_TO_MONITOR = "device_channel_to_monitor";
    public static final String TABLE_SEARCH_HISTORY = "search_history";
    public static final String TABLE_SNAPSHOT = "snapshot";
    public static final String TABLE_REMOVE_LIST = "remove_list";
    public static final String s_GCM_PHP_URL = "http://push.tutk.com/apns/apns.php";
    public static final String s_GCM_SYNC_PHP_URL = "http://push.tutk.com/apns/sync.php";
    public static final String Device_On_Cloud_URL = "http://p2pcamweb.tutk.com/DeviceCloud/api.php";
    public static final String s_Package_name = "com.tutk.p2pcamlive.2(Android)";
    public static final String s_Package_name_baidu = "baidu.tutk.p2pcamlive.2(Android)";
    public static final String s_GCM_sender = "935793047540";
    public static final boolean Not_Sync = false;
    // public static String s_GCM_IMEI = "";
    public static String s_GCM_token;
    public static int n_mainActivity_Status = 0;

    private static DatabaseHelper mDbHelper;

    public DatabaseManager() {
        mDbHelper = new DatabaseHelper(HaotekApplication.getContext());
        Log.d(TAG, "DatabaseManager ");
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        private final String TAG = DatabaseHelper.class.getSimpleName();
        private static final String DB_FILE = "device.db";
        private static final int DB_VERSION = 1;

        private static final String SQLCMD_CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE + "(" + "_id INTEGER NOT NULL PRIMARY KEY " +
                "AUTOINCREMENT, " + "dev_ap_ssid VARCHAR(40) NULL, " + "dev_mac VARCHAR(20) NULL, " + "dev_uid VARCHAR(20) NULL, " + "dev_model_name VARCHAR(30) NULL, " +
                "" + "dev_pwd VARCHAR(30) NULL, " + "view_acc VARCHAR(30) NULL, " + "view_pwd VARCHAR(30) NULL," + "event_notification INTEGER, " + "camera_channel INTEGER ," + " sync INTEGER" + ");";

        public DatabaseHelper(Context context) {
            super(context, DB_FILE, null, DB_VERSION);
            Log.d(TAG, "DatabaseHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate db");
            db.execSQL(SQLCMD_CREATE_TABLE_DEVICE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade db");
            db.execSQL("ALTER TABLE " + TABLE_DEVICE + " ADD COLUMN sync integer DEFAULT 0");
        }
    }

    public static DatabaseManager getDatabaseManager() {
        return sDatabaseManager;
    }

    public SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }

    public long addDevice(String dev_ap_ssid, String dev_mac, String dev_uid, String dev_model_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel, boolean sync) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dev_ap_ssid", dev_ap_ssid);
        values.put("dev_mac", dev_mac);
        values.put("dev_uid", dev_uid);
        values.put("dev_model_name", dev_model_name);
        values.put("dev_pwd", dev_pwd);
        values.put("view_acc", view_acc);
        values.put("view_pwd", view_pwd);
        values.put("event_notification", event_notification);
        values.put("camera_channel", channel);
        values.put("sync", sync);
        long ret = getID(dev_ap_ssid, dev_mac, dev_uid);
        //FIXME  use SQLiteDatabase.CONFLICT_IGNORE !!!!!
//        int id = (int) db.insertWithOnConflict(TABLE_DEVICE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
//        if (id == -1) {
//            db.update(TABLE_DEVICE, initialValues, "_id=?", new String[]{Integer.toString(id)});
//        }

        if (ret == -1) {
            Log.d(TAG, "db insert");
            db.insert(TABLE_DEVICE, null, values);
        } else {
            Log.d(TAG, "db update _id= " + ret);
            db.update(TABLE_DEVICE, values, "_id=?", new String[]{Long.toString(ret)});
        }
        db.close();
        return ret;
    }

    private int getID(String ssid, String mac, String uid) {
        Log.d(TAG, " getID  input  ssid : " + ssid + "_mac : " + mac + "_uid : " + uid);
        Cursor c = getReadableDatabase().query(TABLE_DEVICE, new String[]{"_id"}, "dev_ap_ssid =?", new String[]{ssid}, null, null, null, null);
        if (mac != null) {
            Log.d(TAG, " mac  not null : ");
            c = getReadableDatabase().query(TABLE_DEVICE, new String[]{"_id"}, "dev_ap_ssid =? OR dev_mac=?", new String[]{ssid, mac}, null, null, null, null);
        }
        if (uid != null) {
            Log.d(TAG, " uid  not null : ");
            c = getReadableDatabase().query(TABLE_DEVICE, new String[]{"_id"}, "dev_ap_ssid =? OR dev_uid=?", new String[]{ssid, uid}, null, null, null, null);
        }
        if (mac != null && uid != null) { //FIXME  Cannot bind argument at index 3 because the index is out of range.  The statement has 2 parameters.
            Log.d(TAG, " mac uid not null : ");
            Log.d(TAG, " Cursor SQLite  MAC : " + mac + "_UID : " + uid);
//            c =  getReadableDatabase().query(TABLE_DEVICE,new String[]{"_id"}, "dev_ap_ssid =? OR dev_mac=? OR dev_uid=?",new String[]{ssid,mac,uid},null,null,null,null);
//            c = getReadableDatabase().query(TABLE_DEVICE, new String[]{"_id"}, " dev_mac=? AND dev_uid=?", new String[]{ssid, mac, uid}, null, null, null, null);
        }

        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("_id"));
        }
        return -1;
    }

    public void updateDeviceInfoByDBID(long db_id, String dev_ap_ssid, String dev_mac, String dev_uid, String dev_model_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dev_ap_ssid", dev_ap_ssid);
        values.put("dev_mac", dev_mac);
        values.put("dev_uid", dev_uid);
        values.put("dev_model_name", dev_model_name);
        values.put("dev_pwd", dev_pwd);
        values.put("view_acc", view_acc);
        values.put("view_pwd", view_pwd);
        values.put("event_notification", event_notification);
        values.put("camera_channel", channel);
        db.update(TABLE_DEVICE, values, "_id = '" + db_id + "'", null);
        db.close();
    }

    public void updateDeviceBySSID(String dev_ap_ssid, String dev_mac, String dev_uid, String dev_model_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dev_mac", dev_mac);
        values.put("dev_uid", dev_uid);
        values.put("dev_model_name", dev_model_name);
        values.put("dev_pwd", dev_pwd);
        values.put("view_acc", view_acc);
        values.put("view_pwd", view_pwd);
        values.put("event_notification", event_notification);
        values.put("camera_channel", channel);
        db.update(TABLE_DEVICE, values, "dev_ap_ssid = '" + dev_ap_ssid + "'", null);
        db.close();
    }

    public void updateDeviceByMAC(String dev_ap_ssid, String dev_mac, String dev_uid, String dev_model_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dev_ap_ssid", dev_ap_ssid);
        values.put("dev_uid", dev_uid);
        values.put("dev_model_name", dev_model_name);
        values.put("dev_pwd", dev_pwd);
        values.put("view_acc", view_acc);
        values.put("view_pwd", view_pwd);
        values.put("event_notification", event_notification);
        values.put("camera_channel", channel);
        db.update(TABLE_DEVICE, values, "dev_mac = '" + dev_mac + "'", null);
        db.close();
    }

    public void updateDeviceByUID(String dev_ap_ssid, String dev_mac, String dev_uid, String dev_model_name, String dev_pwd, String view_acc, String view_pwd, int event_notification, int channel) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dev_ap_ssid", dev_ap_ssid);
        values.put("dev_mac", dev_mac);
        values.put("dev_model_name", dev_model_name);
        values.put("dev_pwd", dev_pwd);
        values.put("view_acc", view_acc);
        values.put("view_pwd", view_pwd);
        values.put("event_notification", event_notification);
        values.put("camera_channel", channel);
        db.update(TABLE_DEVICE, values, "dev_uid = '" + dev_uid + "'", null);
        db.close();
    }
}
