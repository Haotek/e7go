package tw.haotek.app.e7go.fragment;

import android.animation.LayoutTransition;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import c.min.tseng.R;
import c.min.tseng.SmartHome.C;
import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import c.min.tseng.managers.DeviceManager;
import tw.haotek.HaotekApplication;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.device.RebootCommand;
import tw.haotek.command.HaotekW.device.SetDeviceDateCommand;
import tw.haotek.command.HaotekW.device.SetDeviceTimeCommand;
import tw.haotek.command.HaotekW.device.SetFactoryDefaultCommand;
import tw.haotek.command.HaotekW.device.SetRFPairingCommand;
import tw.haotek.command.HaotekW.module.video.CheckVideoRecStateCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoRecordingCommand;
import tw.haotek.command.Tutk.RebootTCommand;
import tw.haotek.command.Tutk.SetDeviceDateTCommand;
import tw.haotek.command.Tutk.SetDeviceTimeTCommand;
import tw.haotek.command.Tutk.SetFactoryDefaultTCommand;
import tw.haotek.command.Tutk.SetRFPairingTCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.video.CheckVideoRecStateTCommand;
import tw.haotek.command.Tutk.video.StartVideoRecordingTCommand;
import tw.haotek.command.Tutk.video.StopVideoRecordingTCommand;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/7 0007.
 */
public class DeviceDetailFragment extends Fragment {
    private static final String TAG = DeviceDetailFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;
    private Device mDevice;
    private ListView mModuleDetailList;
    private ModuleDetailListAdapter mAdapter;
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
            mHandler.post(mUpdateRunnable);
        }
    };

    final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
        }
    };
    //FIXME for set Date & Time command
    private static final SimpleDateFormat sFull = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public DeviceDetailFragment() {
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
//            mTitle.setText(R.string.settings);
        }

        final Bundle arguments = getArguments();
        if (arguments == null) {
            FragmentManager manager = getFragmentManager();
            Log.d(TAG, "onAttach arguments == null  ");
            if (!manager.popBackStackImmediate()) {
                Log.d(TAG, "popBackStackImmediate");
                ((MainFragment) mParentFragment).setChildFragment(new LiveViewFragment(), false, false);//FIXME
            }
            return;
        }
        Log.d(TAG, "Get MAC : " + arguments.getString(C.ARGUMENT_DEVICE_MAC));
        Log.d(TAG, "Get SSID : " + arguments.getString(C.ARGUMENT_DEVICE_APBSSID));
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
//                ((Main) getActivity()).onBackPressed();
                getActivity().onBackPressed();
                return;
            }
        }
//        mAdapter = new ModuleDetailAdapter(mDevice);
//        final long db_id = DatabaseManager.getDatabaseManager().addDevice(mDevice.getAPModeBSSID(), mDevice.getMACAddress(), mDevice.getUID(), "Model", mDevice.getAPModePassword(), mDevice.getUsername(), mDevice.getPassword(), 0, 0, true);
//        Log.d(TAG, "db_id  : " + db_id);
        mAdapter = new ModuleDetailListAdapter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (mDevice == null) {
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View child = inflater.inflate(R.layout.fragment_devicedetail, container, false);
        mModuleDetailList = (ListView) child.findViewById(android.R.id.list);
        mModuleDetailList.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            mModuleDetailList.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        return child;
    }

    @Override
    public void onResume() {
        super.onResume();
        HaotekApplication.startDiscovery();
        mDevice.registerContentObserver(mContentObserver);
        mDevice.fetchEverything();
//        mDevice.reLogin(new Device.OnLoginResultListener(mHandler) {
//            @Override
//            public void onLoginSuccess(Device device) {
//                if (device.getModuleCount() > 0) {
//                    for (int i = 0; i < device.getModuleCount(); ++i) {
//                        device.getModuleByIndex(i).fetchEverything();
//                    }
//                } else {
//                    device.fetchEverything();
//                }
//            }
//
//            @Override
//            public void onLoginFailed(Device device, Exception ex) {
//
//            }
//        });
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
//            mTitle.setText(R.string.settings);
        }
    }

    @Override
    public void onPause() {
        HaotekApplication.stopDiscovery();
        mDevice.unregisterContentObserver(mContentObserver);
        super.onPause();
    }

    private class ModuleDetailListAdapter extends BaseAdapter {
        private final String TAG = ModuleDetailListAdapter.class.getSimpleName();
        @Expose(serialize = false, deserialize = false)
        private ExecutorService mNetworkTaskExecutor = Executors.newSingleThreadExecutor(); //FIXME

        //FIXME for test Command
        private String[] mAllTimes = sFull.format(System.currentTimeMillis()).split("_");

        @Override
        public int getCount() {
            Log.d(TAG, "Size : " + mDevice.getModuleCount());
//            return mDevice.getModuleCount();
            return mDevice.getModuleCount() + 1;
        }

        @Override
        public Module getItem(int position) {
            if (position >= mDevice.getModuleCount()) {
                return null;
            }
            return mDevice.getModuleByIndex(position);
        }

        @Override
        public long getItemId(int position) {
            if (position == mDevice.getModuleCount()) {
                return android.R.id.empty;
            }
            return getItem(position).getModuleID();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            return getItem(position).getSimpleView(convertView, parent);
            return (position == mDevice.getModuleCount()) ?
                    getPingPongView(convertView, parent) :
                    getItem(position).getSimpleView(convertView, parent);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mDevice.getModuleCount()) {
                return Module.getSimpleViewTypeCount();
            }
            return getItem(position).getSimpleViewType();
        }

        @Override
        public int getViewTypeCount() {
            return Module.getSimpleViewTypeCount() + 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private View getPingPongView(View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_devicedetail_default, parent, false);
                convertView.setTag(R.id.tag_view_holder, new ViewHolder(convertView));
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag(R.id.tag_view_holder);
            holder.searchapdescription.setText(R.string.aplist);
            holder.searchap.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_single_choice, null);
                    }
                    final CheckedTextView text = (CheckedTextView) convertView;
                    text.setTextSize(12);
//                    text.setText(getItem(position));
                    return convertView;
                }
            });
            holder.synctimedescription.setText(R.string.synctime);
            holder.synctimeexpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.synctimebtlayout.getVisibility() == View.GONE) {
                        holder.synctimebtlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.synctimebtlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.synctimeconfirm.setText(R.string.startsynctime);
            holder.synctimeconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "synctimeconfirm");
                    mNetworkTaskExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand((HaotekDevice) mDevice);
                                final StopVideoRecordingTCommand stopt = new StopVideoRecordingTCommand((HaotekDevice) mDevice, 0);
                                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {
                                    }
                                });
                                final StartVideoRecordingTCommand startt = new StartVideoRecordingTCommand((HaotekDevice) mDevice, 1);
                                startt.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {
                                    }
                                });
                                final SetDeviceDateTCommand sdate = new SetDeviceDateTCommand((HaotekDevice) mDevice, mAllTimes[0]);
                                sdate.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {

                                    }
                                });
                                final SetDeviceTimeTCommand stime = new SetDeviceTimeTCommand((HaotekDevice) mDevice, mAllTimes[1]);
                                stime.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {

                                    }
                                });
                                checkt.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {
                                        final String[] rawdata = (String[]) result;
                                        final String[] data = rawdata[0].split(",");
                                        if (data[2].equals("1")) {
                                            stopt.run();
                                        }
                                        sdate.run();
                                        stime.run();
                                        if (data[2].equals("1")) {
                                            startt.run();
                                        }
                                    }
                                });
                                checkt.run();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info);
                                final SetVideoRecordingCommand stop = new SetVideoRecordingCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info, 0);
                                final SetVideoRecordingCommand start = new SetVideoRecordingCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info, 1);
                                final SetDeviceDateCommand cmdd = new SetDeviceDateCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info, mAllTimes[0]);
                                final SetDeviceTimeCommand cmdt = new SetDeviceTimeCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info, mAllTimes[1]);
                                final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                                if (gresponse.mValue.equals("1")) {
                                    stop.run();
                                    final SetDeviceDateCommand.Response responsed = (SetDeviceDateCommand.Response) cmdd.run();
                                    final SetDeviceTimeCommand.Response responset = (SetDeviceTimeCommand.Response) cmdt.run();
                                    start.run();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });


            holder.rfpairingdescription.setText(R.string.rfpairing);
            holder.rfpairingexpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.rfpairingbtlayout.getVisibility() == View.GONE) {
                        holder.rfpairingbtlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.rfpairingbtlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.rfpairingconfirm.setText(R.string.startpair);
            holder.rfpairingconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNetworkTaskExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SetRFPairingCommand cmd = new SetRFPairingCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info);
                                final SetRFPairingCommand.Response response = (SetRFPairingCommand.Response) cmd.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                SetRFPairingTCommand rfpair = new SetRFPairingTCommand((HaotekDevice) mDevice);
                                rfpair.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {

                                    }
                                });
                                rfpair.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });


            holder.rebootdescription.setText(R.string.reboot);
            holder.rebootexpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.rebootbtlayout.getVisibility() == View.GONE) {
                        holder.rebootbtlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.rebootbtlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.rebootconfirm.setText(R.string.rebootnow);
            holder.rebootconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNetworkTaskExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                RebootCommand cmd = new RebootCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info);
                                final RebootCommand.Response response = (RebootCommand.Response) cmd.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                RebootTCommand reboott = new RebootTCommand((HaotekDevice) mDevice);
                                reboott.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {

                                    }
                                });
                                reboott.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            holder.factorydefaultdescription.setText(R.string.factorydefault);
            holder.factorydefaultexpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.factorydefaultbtlayout.getVisibility() == View.GONE) {
                        holder.factorydefaultbtlayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.factorydefaultbtlayout.setVisibility(View.GONE);
                    }
                }
            });
            holder.factorydefaultconfirm.setText(R.string.factorydefault);
            holder.factorydefaultconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNetworkTaskExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SetFactoryDefaultCommand cmd = new SetFactoryDefaultCommand((HaotekDevice) mDevice, HaotekCommand.GET_Info);
                                final SetFactoryDefaultCommand.Response response = (SetFactoryDefaultCommand.Response) cmd.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                SetFactoryDefaultTCommand fd = new SetFactoryDefaultTCommand((HaotekDevice) mDevice);
                                fd.setResponseListener(new TutkCommand.ResponseListener() {
                                    @Override
                                    public void dispatchResponse(Object result) {

                                    }
                                });
                                fd.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView searchapdescription;//Function name
            private Spinner searchap;
            private TextView synctimedescription;
            private ImageButton synctimeexpand;
            private View synctimebtlayout;
            private Button synctimeconfirm;
            private TextView rfpairingdescription;
            private ImageButton rfpairingexpand;
            private View rfpairingbtlayout;
            private Button rfpairingconfirm;
            private TextView rebootdescription;
            private ImageButton rebootexpand;
            private View rebootbtlayout;
            private Button rebootconfirm;
            private TextView factorydefaultdescription;
            private ImageButton factorydefaultexpand;
            private View factorydefaultbtlayout;
            private Button factorydefaultconfirm;

            public ViewHolder(View view) {
                View searchaplistlayout = (View) view.findViewById(R.id.searchaplistlayout);
                searchapdescription = (TextView) searchaplistlayout.findViewById(R.id.description);
                searchap = (Spinner) searchaplistlayout.findViewById(R.id.spinner_backoff);

                View synctimelayout = (View) view.findViewById(R.id.synctimelayout);
                synctimedescription = (TextView) synctimelayout.findViewById(R.id.function);
                synctimeexpand = (ImageButton) synctimelayout.findViewById(R.id.expand);
                synctimebtlayout = (View) synctimelayout.findViewById(R.id.buttonlayout);
                synctimeconfirm = (Button) synctimebtlayout.findViewById(R.id.confirm);

                View rfpairinglayout = (View) view.findViewById(R.id.rfpairinglayout);
                rfpairingdescription = (TextView) rfpairinglayout.findViewById(R.id.function);
                rfpairingexpand = (ImageButton) rfpairinglayout.findViewById(R.id.expand);
                rfpairingbtlayout = (View) rfpairinglayout.findViewById(R.id.buttonlayout);
                rfpairingconfirm = (Button) rfpairingbtlayout.findViewById(R.id.confirm);

                View rebootlayout = (View) view.findViewById(R.id.rebootlayout);
                rebootdescription = (TextView) rebootlayout.findViewById(R.id.function);
                rebootexpand = (ImageButton) rebootlayout.findViewById(R.id.expand);
                rebootbtlayout = (View) rebootlayout.findViewById(R.id.buttonlayout);
                rebootconfirm = (Button) rebootbtlayout.findViewById(R.id.confirm);

                View factorydefaultlayout = (View) view.findViewById(R.id.factorydefaultlayout);
                factorydefaultdescription = (TextView) factorydefaultlayout.findViewById(R.id.function);
                factorydefaultexpand = (ImageButton) factorydefaultlayout.findViewById(R.id.expand);
                factorydefaultbtlayout = (View) factorydefaultlayout.findViewById(R.id.buttonlayout);
                factorydefaultconfirm = (Button) factorydefaultbtlayout.findViewById(R.id.confirm);
            }
        }
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
