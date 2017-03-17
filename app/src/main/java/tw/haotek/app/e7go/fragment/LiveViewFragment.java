package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import c.min.tseng.R;
import c.min.tseng.SmartHome.C;
import c.min.tseng.dut.Device;
import c.min.tseng.managers.DeviceManager;
import c.min.tseng.ui.MapLayoutManager;
import c.min.tseng.util.Create;
import tw.haotek.HaotekApplication;
import tw.haotek.app.e7go.adapter.LivingVideoAdapter;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.video.TriggerEventCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.video.TriggerEventTCommand;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.util.HaotekCallback;

/**
 * Created by Neo on 2016/1/6 0006.
 */
public class LiveViewFragment extends Fragment implements HaotekCallback {
    private static final String TAG = LiveViewFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Device mDevice;
    private LivingVideoAdapter mAdapter;
    private int mMode = 0;//FIXME   0: video  1:photo 2:timelapse
    private static Picasso sPicasso;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;

    //FIXME for set Date & Time command
    @Expose(serialize = false, deserialize = false)
    private ExecutorService mNetworkTaskExecutor = Executors.newSingleThreadExecutor(); //FIXME

    private static Picasso getPicasso(Context context) {
        if (sPicasso == null) {
            sPicasso = Picasso.with(context);
        }
        return sPicasso;
    }

    //FIXME need bundle Device MACADDRESS
    public LiveViewFragment() {
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(TAG, " onInflate(Activity)");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParentFragment = getParentFragment();
        FragmentManager manager = getFragmentManager();
//        boolean result =  manager.popBackStackImmediate("AddNewDeviceFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        Log.d(TAG, "result of popBackStackImmediate on childFM is: " + result);
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
        mAdapter = new LivingVideoAdapter(mDevice, this);
        final WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiConfiguration srcWc = mDevice.getAPModeWiFiCFG();
        if (srcWc != null && !mDevice.getP2PMode()) {//FIXME
            srcWc.preSharedKey = Create.quoteNonHex(mDevice.getAPModePassword(), 64);//FIXME if NONE/WEP ?
            srcWc.networkId = wifimanager.addNetwork(srcWc);
            wifimanager.enableNetwork(srcWc.networkId, true);
            final boolean b = wifimanager.reconnect();
            if (!b) {
                wifimanager.reconnect(); //FIXME retry
            } else {
//                mDevice.fetchEverything();
                wifimanager.saveConfiguration();
            }
        }
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
            mTitle.setText(mDevice.getDeviceName());
        }

        onHiddenChanged(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        View child = inflater.inflate(R.layout.fragment_liveview, container, false);

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

        final ImageView mirror = (ImageView) child.findViewById(R.id.mirror);
        mirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mirror  onClick  need living view mirror ");//FIXME
            }
        });

        final ImageView rotation = (ImageView) child.findViewById(R.id.rotation);
        rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "rotation  onClick  need living view mirror ");//FIXME
            }
        });

        final ImageView fullscreen = (ImageView) child.findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment) mParentFragment).setChildFragment(getTransFragment(mDevice, 7), true, false);//FIXME
            }
        });
        final ImageView album = (ImageView) child.findViewById(R.id.album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment) mParentFragment).setChildFragment(getTransFragment(mDevice, 5), true, false);//FIXME
            }
        });
        final ImageView setting = (ImageView) child.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment) mParentFragment).setChildFragment(getTransFragment(mDevice, 6), true, false);//FIXME
            }
        });

        final ImageView cloud = (ImageView) child.findViewById(R.id.cloud);
        if (mDevice.getP2PMode()) {
            cloud.setVisibility(View.VISIBLE);
        }


        final ImageView rec = (ImageView) child.findViewById(R.id.rec);
        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TriggerEventCommand cmd = new TriggerEventCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info, 32);//FIXME  32 :PHOTO  64 :MOVIE
                            final TriggerEventCommand.Response response = (TriggerEventCommand.Response) cmd.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            TriggerEventTCommand trig = new TriggerEventTCommand((HaotekDevice) mDevice, 32);//FIXME  32 :PHOTO  64 :MOVIE
                            trig.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {

                                }
                            });
                            trig.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        return child;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        final Activity activity = getActivity();
        final ActionBar actionbar = ((AppCompatActivity) activity).getSupportActionBar();
        actionbar.show();
        HaotekApplication.startDiscovery();
        mAdapter.startMonitor();
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
            mTitle.setText(mDevice.getDeviceName());
        }
        Log.d(TAG, "onResume()");

        super.onResume();
    }

    @Override
    public void onPause() {
        HaotekApplication.stopDiscovery();
        mAdapter.stopMonitor();
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        Log.d(TAG, "onSaveInstanceState() Bundle : ");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
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
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
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

    private Fragment getTransFragment(Device device, int type) {
        Fragment fragment = null;
        if (type == 5) {
            fragment = new SDMediaFragment();
        }
        if (type == 6) {
            device.fetchEverything();
            fragment = new DeviceDetailFragment();
        }
        if (type == 7) {
            fragment = new FullScreenFragment();
        }
        fragment.setArguments(device.getBundleKry());
        return fragment;
    }
}

