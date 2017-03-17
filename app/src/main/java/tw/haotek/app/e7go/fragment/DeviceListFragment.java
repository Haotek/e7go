package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.adapter.DeviceListAdapter;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2015/12/26.
 */
public class DeviceListFragment extends Fragment implements HaotekCallback {
    private static final String TAG = DeviceListFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Handler mHandler;
    private DeviceListAdapter mAdapter;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;

    public DeviceListFragment() {
        mHandler = new Handler(Looper.getMainLooper());
        mAdapter = new DeviceListAdapter(this);
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
            mTitle.setVisibility(View.GONE);
//            mTitle.setText(R.string.choose_a_device);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()");
        View child = inflater.inflate(R.layout.fragment_devicelist, container, false);
        RecyclerView devicelistlayout = (RecyclerView) child.findViewById(R.id.devicelistlayout);
        LinearLayoutManager manager = new LinearLayoutManager(container.getContext(), OrientationHelper.VERTICAL, false);
        devicelistlayout.setLayoutManager(manager);
        devicelistlayout.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return child;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Activity activity = getActivity();
        final ActionBar actionbar = ((AppCompatActivity) activity).getSupportActionBar();
        actionbar.show();
        HaotekApplication.startDiscovery();
        mAdapter.startMonitor();
        Log.d(TAG, "onResume()");
        final Toolbar toolbar = ((MainFragment) mParentFragment).getToolbar();
        if (toolbar != null) {
            mLogo = (ImageView) toolbar.findViewById(R.id.logo);
            mLogo.setVisibility(View.GONE);
            mLeft = (Button) toolbar.findViewById(R.id.left);
            mLeft.setVisibility(View.GONE);
            mRight = (Button) toolbar.findViewById(R.id.right);
            mRight.setVisibility(View.GONE);
            mTitle = (TextView) toolbar.findViewById(R.id.title);
            mTitle.setVisibility(View.GONE);
//            mTitle.setText(R.string.choose_a_device);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        HaotekApplication.stopDiscovery();
        super.onPause();
        mAdapter.stopMonitor();
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

    @Override
    public void removeCallbacks(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void postDelayed(Runnable runnable, int delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    @Override
    public void selectItem(Device device, int type) {
        ((MainFragment) mParentFragment).setChildFragment(getTransFragment(device, type), true, false);
    }

    private Fragment getTransFragment(Device device, int type) {
        Fragment fragment = null;
        if (type == 4) {
            fragment = new AddNewDeviceFragment();
        }
        if (type == 2) {
            fragment = new LiveViewFragment();
            fragment.setArguments(device.getBundleKry());
        }
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected item id :  " + item.getItemId());
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Fragment fragment = new DeviceListFragment();
//                ((MainFragment) mParentFragment).setChildFragment(fragment, true, false);
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }
}

