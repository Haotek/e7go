package tw.haotek.app.e7go.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import c.min.tseng.R;
import c.min.tseng.dut.Device;
import c.min.tseng.ui.wizardpager.Page;
import c.min.tseng.ui.wizardpager.PageViewCallbacks;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.adapter.APModeDeviceAdapter;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2015/11/8.
 */
public class ChoiceAPModeDevicePageFragment extends Fragment implements HaotekCallback {
    private static final String TAG = ChoiceAPModeDevicePageFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";
    private Fragment mParentFragment;
    private APModeDeviceAdapter mAdapter;
    private PageViewCallbacks mCallbacks;
    private String mKey;
    private Page mPage;
    private List<String> mCheckItemlist;
    private List<Boolean> mCheckItem;

    public static ChoiceAPModeDevicePageFragment createWithPageCallback(String key, PageViewCallbacks callback) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        ChoiceAPModeDevicePageFragment fragment = new ChoiceAPModeDevicePageFragment(callback);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "Context onAttach");
        mAdapter = new APModeDeviceAdapter(this);
        mParentFragment = getParentFragment().getParentFragment();
        Log.d(TAG, " ParentFragment : " + mParentFragment);
    }

    public ChoiceAPModeDevicePageFragment(PageViewCallbacks callback) {
        setPageViewCallbacks(callback);
    }

    public void setPageViewCallbacks(PageViewCallbacks callback) {
        mCallbacks = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View childview = inflater.inflate(R.layout.fragment_choiceapmodedevicepage, container, false);
        RecyclerView recyclerview = (RecyclerView) childview.findViewById(R.id.recyclerview);
        final LinearLayoutManager manager = new LinearLayoutManager(container.getContext());
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(mAdapter);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        return childview;
    }

    @Override
    public void onResume() {
        super.onResume();
        HaotekApplication.startDiscovery();
        mAdapter.startMonitor();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        HaotekApplication.stopDiscovery();
        super.onPause();
        mAdapter.stopMonitor();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void selectItem(Device device, int type) {
        ((MainFragment) mParentFragment).setChildFragment(getTransFragment(device, type), false, false);
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

    private Fragment getTransFragment(Device device, int type) {
        Fragment fragment = null;
        if (type == 2) {
            fragment = new LiveViewFragment();
            fragment.setArguments(device.getBundleKry());
        }
        return fragment;
    }
}
