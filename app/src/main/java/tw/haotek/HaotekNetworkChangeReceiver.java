package tw.haotek;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import c.min.tseng.NetworkChangeReceiver;
import tw.haotek.discovery.HaotekDiscovery;

/**
 * Created by Neo on 2015/11/27.
 */
public class HaotekNetworkChangeReceiver extends NetworkChangeReceiver {
    private static final String TAG = HaotekNetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) { //FIXME not work !!!
        Log.d(TAG, "onReceive()"); //FIXME   Tutk UDP Discovery need put here ?
        HaotekDiscovery haotekDiscovery = new HaotekDiscovery(context);
        haotekDiscovery.start();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        dispatchConnectivityState(cm.getActiveNetworkInfo());
    }
}
