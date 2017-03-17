package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import c.min.tseng.R;
import c.min.tseng.SmartHome.C;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.ui.MapLayoutManager;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.adapter.FullScreenAdapter;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2016/1/18 0018.
 */
public class FullScreenFragment extends Fragment implements HaotekCallback {
    private static final String TAG = FullScreenFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Device mDevice;
    private FullScreenAdapter mAdapter;

    public FullScreenFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, " onAttach(Context)");
        mParentFragment = getParentFragment();
        FragmentManager manager = getFragmentManager();
        final Bundle arguments = getArguments();
        if (arguments == null) {
            Log.d(TAG, "onAttach arguments == null  ");
            if (!manager.popBackStackImmediate()) {
                Log.d(TAG, "popBackStackImmediate");
                ((MainFragment) mParentFragment).setChildFragment(new DeviceListFragment(), false, false);//FIXME
            }
            return;
        }
        Log.d(TAG, "Get MAC : " + arguments.getString(C.ARGUMENT_DEVICE_MAC));
        Log.d(TAG, "Get BSSID : " + arguments.getString(C.ARGUMENT_DEVICE_APBSSID));
        mDevice = DeviceManager.getDeviceManager().getDeviceByAPBSSID(arguments.getString(C.ARGUMENT_DEVICE_APBSSID));
        if (mDevice == null) {
            if (arguments.getString(C.ARGUMENT_DEVICE_MAC) == null || arguments.getString(C.ARGUMENT_DEVICE_MAC).isEmpty()) {
                mDevice = DeviceManager.getDeviceManager().getDeviceByUID(arguments.getString(C.ARGUMENT_DEVICE_UID));
            }
            mDevice = DeviceManager.getDeviceManager().getDeviceByMac(arguments.getString(C.ARGUMENT_DEVICE_MAC));
        }
        if (mDevice == null) {
            mDevice = DeviceManager.getDeviceManager().getDeviceByUID(arguments.getString(C.ARGUMENT_DEVICE_UID));
            if (mDevice == null) {
                getActivity().onBackPressed();
                return;
            }
        }
        mAdapter = new FullScreenAdapter(mDevice, this);
        onHiddenChanged(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate (Bundle)");
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView (LayoutInflater)");//FIXME chang fullScreen
        final Context context = container.getContext();
        View child = inflater.inflate(R.layout.fragment_fullscreen, container, false);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        final RecyclerView liveviewlayout = (RecyclerView) child.findViewById(R.id.liveviewlayout);
        liveviewlayout.setLayoutManager(new MapLayoutManager(context, MapLayoutManager.HORIZONTAL, new int[][]{
                new int[]{0}
        }));
        liveviewlayout.setAdapter(mAdapter);
        liveviewlayout.setOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            final MapLayoutManager lm = (MapLayoutManager) recyclerView.getLayoutManager();
                            if (!lm.isSnappedToFirstVisibleItem()) {
                                recyclerView.smoothScrollToPosition(lm.findOptimalFirstVisibleItemPosition());
                            }
                        }
                    }
                }
        );
        ////
//        c.min.tseng.ui.AutoVLCTextureVideoView video = (c.min.tseng.ui.AutoVLCTextureVideoView) child.findViewById(R.id.video);
//        Log.d(TAG, "Show IP : " + mDevice.getInetAddress());
//        final Uri myUri = Uri.parse("rtsp://" + mDevice.getInetAddress() + "/xxx.mov");//FIXME  VLC use Uri
//        video.stePath(myUri);
//        video.setLandscape(true);
//        video.setRotation(90);
//        final Matrix txform = new Matrix();
//        video.getTransform(txform);
//        video.setScaleX((float)(16f/9f));
//        video.setScaleY((float)(9f/16));
//        video.setTransform(txform);
        return child;
    }

    @Override
    public void onResume() {
        final Activity activity = getActivity();
        final ActionBar actionbar = ((AppCompatActivity) activity).getSupportActionBar();
        actionbar.show();
        HaotekApplication.startDiscovery();
//        mAdapter.startMonitor();
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        HaotekApplication.stopDiscovery();
//        mAdapter.stopMonitor();
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void selectItem(Device device, int type) {

    }

    @Override
    public void removeCallbacks(Runnable runnable) {

    }

    @Override
    public void post(Runnable runnable) {

    }

    @Override
    public void postDelayed(Runnable runnable, int delayMillis) {

    }
}
