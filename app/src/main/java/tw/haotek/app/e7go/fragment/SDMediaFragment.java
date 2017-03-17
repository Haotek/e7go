package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.SmartHome.C;
import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import c.min.tseng.managers.DeviceManager;
import tw.haotek.app.e7go.adapter.SDMediaAdapter;
import tw.haotek.dut.module.HaotekStorage;

/**
 * Created by Neo on 2016/1/5 0005.
 */
public class SDMediaFragment extends Fragment {
    private static final String TAG = SDMediaFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;
    private Device mDevice;
    private Bundle mArguments;
    private SDMediaAdapter mAdapter;
    private Handler mHandler;
    private ContentObserver mContentObserver = new ContentObserver(mHandler) {
        @Deprecated
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "onChange Uri" + uri);
            if (uri != null) {
                final String property = uri.getQueryParameter("property");
                if (property != null && property.equalsIgnoreCase("filelist")) {
                    mHandler.post(mUpdateRunnable);
                }
            }
        }
    };

    final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "UpdateRunnable");
            mAdapter.notifyDataSetChanged();
        }
    };

    public SDMediaFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, " onAttach(Context)");
        mParentFragment = getParentFragment();
        final Toolbar toolbar = ((MainFragment) mParentFragment).getToolbar();
        if (toolbar != null) {
            mLogo = (ImageView) toolbar.findViewById(R.id.logo);
            mLogo.setVisibility(View.GONE);
            mLeft = (Button) toolbar.findViewById(R.id.left);
            mLeft.setVisibility(View.GONE);
            mRight = (Button) toolbar.findViewById(R.id.right);
            mRight.setVisibility(View.GONE);
            mTitle = (TextView) toolbar.findViewById(R.id.title);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(R.string.remote_media);
        }
        mArguments = getArguments();
        if (mArguments == null) {
            FragmentManager manager = getFragmentManager();
            Log.d(TAG, "onAttach arguments == null  ");
//            if (!manager.popBackStackImmediate()) {
//                ((MainFragment) mParentFragment).setChildFragment(new LiveViewFragment(), false, false);//FIXME
//            }
            return;
        }
        Log.d(TAG, "Get MAC : " + mArguments.getString(C.ARGUMENT_DEVICE_MAC));
        Log.d(TAG, "Get BSSID : " + mArguments.getString(C.ARGUMENT_DEVICE_APBSSID));
        mDevice = DeviceManager.getDeviceManager().getDeviceByAPBSSID(mArguments.getString(C.ARGUMENT_DEVICE_APBSSID));
        if (mDevice == null) {
            if (mArguments.getString(C.ARGUMENT_DEVICE_MAC) == null || mArguments.getString(C.ARGUMENT_DEVICE_MAC).isEmpty()) {
                mDevice = DeviceManager.getDeviceManager().getDeviceByUID(mArguments.getString(C.ARGUMENT_DEVICE_UID));
            }
            mDevice = DeviceManager.getDeviceManager().getDeviceByMac(mArguments.getString(C.ARGUMENT_DEVICE_MAC));
        }
        if (mDevice == null) {
            mDevice = DeviceManager.getDeviceManager().getDeviceByUID(mArguments.getString(C.ARGUMENT_DEVICE_UID));
            if (mDevice == null) {
                getActivity().onBackPressed();
                return;
            }
        }
        final int moduleCount = mDevice.getModuleCount();
        for (int m = 0; m < moduleCount; ++m) {
            final Module module = mDevice.getModuleByIndex(m);
            if (module.getClass().isAssignableFrom(HaotekStorage.class)) {
                module.fetchEverything();//FIXME
                mAdapter = new SDMediaAdapter((HaotekStorage) module);//FIXME
                break;
            }
        }
        onHiddenChanged(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        View child = inflater.inflate(R.layout.fragment_mymedia, container, false);
        RecyclerView homerecyclerview = (RecyclerView) child.findViewById(R.id.medialayout);
        final LinearLayoutManager mymedialinear = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        homerecyclerview.setLayoutManager(mymedialinear);
        homerecyclerview.setAdapter(mAdapter);
        return child;
    }

    @Override
    public void onResume() {
        mDevice.registerContentObserver(mContentObserver);
        super.onResume();
        Log.d(TAG, "onResume()");
        final Activity activity = getActivity();
        final ActionBar actionbar = ((AppCompatActivity) activity).getSupportActionBar();
        actionbar.show();
        final Toolbar toolbar = ((MainFragment) mParentFragment).getToolbar();
        if (toolbar != null) {
            mLogo = (ImageView) toolbar.findViewById(R.id.logo);
            mLogo.setVisibility(View.GONE);
            mLeft = (Button) toolbar.findViewById(R.id.left);
            mLeft.setVisibility(View.GONE);
            mRight = (Button) toolbar.findViewById(R.id.right);
            mRight.setVisibility(View.GONE);
            mTitle = (TextView) toolbar.findViewById(R.id.title);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(R.string.remote_media);
        }
    }

    @Override
    public void onPause() {
        mDevice.unregisterContentObserver(mContentObserver);
        super.onPause();
        Log.d(TAG, "onPause()");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
