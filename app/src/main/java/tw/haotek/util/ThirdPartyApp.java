package tw.haotek.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import tw.haotek.HaotekApplication;

/**
 * Created by Neo on 2016/1/20 0020.
 */
public class ThirdPartyApp {
    private static final String TAG = ThirdPartyApp.class.getSimpleName();

    public static Resources getThirdPartyAppResources(String apppackagename) {
        Resources resources = null;
        if (resources == null) {
            PackageManager manager = HaotekApplication.getContext().getPackageManager();
            try {
                resources = manager.getResourcesForApplication(apppackagename);
            } catch (PackageManager.NameNotFoundException ex) {
                Log.d(TAG, "getResources(): " + ex.getMessage());
            }

            if (resources == null)
                try {
                    resources = manager.getResourcesForApplication(apppackagename + ".debug");//FIXME Debugversion
                } catch (PackageManager.NameNotFoundException ex) {
                    Log.d(TAG, "getResources(): " + ex.getMessage());
                }

            if (resources == null)
                resources = HaotekApplication.getContext().getResources();
        }
        return resources;
    }

    public static boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = HaotekApplication.getContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAppInstall(String packageName) {
        final PackageManager pManager = HaotekApplication.getContext().getPackageManager();
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            pManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void installApk(String filefullname) {//FIXME
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        AssetManager assets = HaotekApplication.getContext().getAssets();
        try {
            InputStream ss = assets.open(filefullname);
            InputStream is = getClass().getResourceAsStream("/assets/AsrService.apk");//FIXME

            FileOutputStream fos = HaotekApplication.getContext().openFileOutput(filefullname, Context.MODE_PRIVATE + Context.MODE_WORLD_READABLE);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File f = new File(HaotekApplication.getContext().getFilesDir().getPath() + "/" + filefullname);
        intent.setDataAndType(Uri.fromFile(f), type);
        HaotekApplication.getContext().startActivity(intent);
    }

    public boolean isStartService(String serviceName) {
        ActivityManager mActivityManager = (ActivityManager) HaotekApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> currentService = mActivityManager.getRunningServices(100);
        final String igrsClassName = serviceName;//FIXME like "com.iflytek.asr.AsrService";
        final boolean b = igrsBaseServiceIsStart(currentService, igrsClassName);
        return b;
    }

    public boolean igrsBaseServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList, String className) {
        for (int i = 0; i < mServiceList.size(); i++) {
            if (className.equals(mServiceList.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
