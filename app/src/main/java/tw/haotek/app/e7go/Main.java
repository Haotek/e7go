package tw.haotek.app.e7go;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import c.min.tseng.R;
import tw.haotek.app.e7go.fragment.MainFragment;
import tw.haotek.app.e7go.fragment.WelcomeFragment;

/**
 * Created by Neo on 2015/11/26.
 */
public class Main extends AppCompatActivity {
    private static final String TAG = Main.class.getSimpleName();
    Fragment mPendingFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (savedInstanceState != null) {
            Log.d(TAG, "OnCreate->SavedInstanceState not null");
        } else {
            Log.d(TAG, "OnCreate->SavedInstanceState null");
            gotoFragment(new WelcomeFragment(), false);
//                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//                final int layout = prefs.getInt(C.PREFS_FAVORITE_FRAGMENT, R.layout.fragment_welcom);
//                if (layout == R.layout.fragment_welcom) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.root, new WelcomeFragment()).commit();
////                gotoFragment(new WelcomeFragment(), false);
//                } else {
//                    gotoFragment(new MainFragment(), false);
////                gotoFragment(new FirstStartFragment(), false);
////                getSupportFragmentManager().beginTransaction().replace(R.id.root, new WelcomeFragment()).commit();
//                }

        }


//        gotoFragment(new MainFragment(), false);
        copyGuideFile();
    }

    public void gotoFragment(Fragment fragment, boolean anim) {
        try {
            mPendingFragment = fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (anim) {
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            }
            transaction
                    .replace(R.id.root, mPendingFragment)
                    .commit();
            mPendingFragment = null;
        } catch (IllegalStateException ex) {
            Log.d(TAG, "gotoFragment(): illegal state: " + ex.getMessage());
        }
    }

    public void copyGuideFile() {
        AssetManager assetManager = this.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("gitup.mp4");
            String copyfile = Environment.getExternalStorageDirectory() + "/gitup.mp4";
            out = new FileOutputStream(copyfile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private String getFileName(String file) {
        return getFilesDir() + "/" + file;
    }

    public void copyGuideFiletoData() {
        AssetManager assetManager = this.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open("gitup.mp4");
            String fileName = "gitup.mp4";
            out = openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        final FragmentManager childFM = fragment.getChildFragmentManager();
        final Fragment childfragment = childFM.findFragmentById(R.id.container);
        Log.d(TAG, "onBackPressed(): " + fragment);
        Log.d(TAG, "onBackPressed() Child fragment : " + childfragment);
        if (fragment != null) {
            if (fragment instanceof MainFragment) {
                final DrawerLayout drawer = ((MainFragment) fragment).getDrawerLayout();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }
            }

            Log.d(TAG, "onBackPressed(): child back stack = " + fragment.getChildFragmentManager().getBackStackEntryCount());
//            if (fragment.getChildFragmentManager().getBackStackEntryCount() != 0) {
            if (childFM.getBackStackEntryCount() == 1) {
                final WifiManager wifimanager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
                wifimanager.disconnect();
            }

            if (childFM.getBackStackEntryCount() > 1) {
                for (int i = 0; i < childFM.getBackStackEntryCount(); ++i) {
                    String ii = childFM.getBackStackEntryAt(i).getName();
                    Log.d(TAG, "Show BackStack Fragment  index : " + i + "_name : " + ii);
                }
                Log.d(TAG, "Show pop Child Fragment : " + childfragment.getClass().getSimpleName());
                boolean result = childFM.popBackStackImmediate(childfragment.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Log.d(TAG, "result of popBackStackImmediate on childFM is: " + result);
                result = childFM.popBackStackImmediate("AddNewDeviceFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Log.d(TAG, "result of popBackStackImmediate on childFM is: " + result);
                return;
            }


        }
        Log.d(TAG, "onBackPressed(): back stack = " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

