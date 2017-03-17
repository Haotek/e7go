package tw.haotek;

import c.min.tseng.BuildConfig;
import c.min.tseng.DefaultApplication;
import tw.haotek.discovery.HaotekDiscovery;
import tw.haotek.discovery.HaotekTDiscovery;

/**
 * Created by Neo on 2015/11/23.
 */
public class HaotekApplication extends DefaultApplication {
    private static final String TAG = HaotekApplication.class.getSimpleName();
    private static final String RECIPIENT = BuildConfig.AUTHOR_EMAIL;
    private HaotekDiscovery mHaotekDiscovery;
    private HaotekTDiscovery mHaotekTDiscovery;

    @Override
    public void onCreate() {
        mHaotekDiscovery = new HaotekDiscovery(this);
        mHaotekTDiscovery = new HaotekTDiscovery(this);
        mStartDiscoveryRunnable = new Runnable() {//FIXME   set Runnable
            @Override
            public void run() {
//            ComponentName receiver = new ComponentName(getApplicationContext(), NetworkChangeReceiver.class);
//            getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                if (!mHaotekDiscovery.isRunning()) {
                    mHaotekDiscovery.start();//FIXME TUTK how /where start & stop  ?
                }
                if (!mHaotekTDiscovery.isRunning()) {
                    mHaotekTDiscovery.start();//FIXME TUTK how /where start & stop  ?
                }
            }
        };
        mStopDiscoveryRunnable = new Runnable() {//FIXME   set Runnable
            @Override
            public void run() {
                mHaotekDiscovery.stop();
                mHaotekTDiscovery.stop();
//            ComponentName receiver = new ComponentName(getApplicationContext(), NetworkChangeReceiver.class);
//            getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        };
        super.onCreate();
//        Log.d(TAG, "Application onCreate");
    }
}
