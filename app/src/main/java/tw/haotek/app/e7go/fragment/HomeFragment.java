package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import c.min.tseng.R;
import c.min.tseng.managers.DeviceManager;
import tw.haotek.app.e7go.C;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.managers.DatabaseManager;

/**
 * Created by Neo on 2015/10/27.
 */

interface HomeCallback {
    void selectRecycleItem(int position);
}

public class HomeFragment extends Fragment implements HomeCallback {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;


    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, " onAttach(Context)");
        mParentFragment = getParentFragment();
        final Toolbar toolbar = ((MainFragment) mParentFragment).getToolbar();
        if (toolbar != null) {
            mLogo = (ImageView) toolbar.findViewById(R.id.logo);
//            mLogo.setImageResource(R.drawable.logo);
            mLogo.setVisibility(View.GONE);
            mLeft = (Button) toolbar.findViewById(R.id.left);
            mLeft.setVisibility(View.GONE);
            mRight = (Button) toolbar.findViewById(R.id.right);
            mRight.setVisibility(View.GONE);
            mTitle = (TextView) toolbar.findViewById(R.id.title);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(R.string.connect_to_device);
            mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick");
//                    ((MainFragment) mParentFragment).setChildFragment(new DeviceListFragment(), true, false);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addseenDeviceFromDB();//FIXME resotre Device from DB but username / password ?
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        View child = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homerecyclerview = (RecyclerView) child.findViewById(R.id.homerecyclerview);
        final LinearLayoutManager homelinear = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        homerecyclerview.setLayoutManager(homelinear);
        homerecyclerview.setAdapter(new HomeFunctionAdapter(this));
        return child;
    }

    private class HomeFunctionAdapter extends RecyclerView.Adapter<HomeFunctionAdapter.ViewHolder> {
        private final String TAG = HomeFunctionAdapter.class.getSimpleName();
        private int[] mFunctionName;
        private int[] mFunctionIcon;
        private HomeCallback mCallBack;

        public HomeFunctionAdapter(HomeCallback callback) {//FIXME
            mCallBack = callback;
            mFunctionName = new int[]{R.string.media_description, R.string.new_media, R.string.video_subdescription};
            mFunctionIcon = new int[]{R.drawable.bg_mymedia, R.drawable.bg_the_new, R.drawable.bg_the_best};//FIXME from http?
        }

        @Override
        public int getItemCount() {
            return mFunctionName.length;
        }

        @Override
        public HomeFunctionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_fuction, parent, false);
            ViewHolder vh = new ViewHolder(child);
            return vh;
        }

        @Override
        public void onBindViewHolder(final HomeFunctionAdapter.ViewHolder holder, final int position) {
            holder.function.setText(mFunctionName[position]);
            holder.functionbg.setBackgroundResource(mFunctionIcon[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.selectRecycleItem(position);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout functionbg;
            TextView function;

            public ViewHolder(View itemView) {
                super(itemView);
                functionbg = (RelativeLayout) itemView.findViewById(R.id.functionbg);
                function = (TextView) itemView.findViewById(R.id.functiondes);
            }
        }
    }

    private void addseenDeviceFromDB() {
        final DeviceManager manager = DeviceManager.getDeviceManager();
        final DatabaseManager dbmanager = DatabaseManager.getDatabaseManager();//FIXME　
        SQLiteDatabase db = dbmanager.getReadableDatabase();
        Cursor cursor = db.query(true, DatabaseManager.TABLE_DEVICE, new String[]{"dev_ap_ssid"}, null, null, null, null, null, null);
        final int count = cursor.getCount();
        while (cursor.moveToNext()) {
            final int index = cursor.getColumnIndexOrThrow("dev_ap_ssid");
            final String apssid = cursor.getString(index);
            final HaotekDevice device = new HaotekDevice();
            device.setAPModeBSSID(apssid);
            device.setDeviceName(apssid);
            manager.addDevice(device);
        }
        cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        final Activity activity = getActivity();
        final ActionBar actionbar = ((AppCompatActivity) activity).getSupportActionBar();
        actionbar.show();
        final Toolbar toolbar = ((MainFragment) mParentFragment).getToolbar();
        if (toolbar != null) {
            mLogo = (ImageView) toolbar.findViewById(R.id.logo);
//            mLogo.setImageResource(R.drawable.logo);
            mLogo.setVisibility(View.GONE);
            mLeft = (Button) toolbar.findViewById(R.id.left);
            mLeft.setVisibility(View.GONE);
            mRight = (Button) toolbar.findViewById(R.id.right);
            mRight.setVisibility(View.GONE);
            mTitle = (TextView) toolbar.findViewById(R.id.title);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(R.string.connect_to_device);
            mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick");
//                    ((MainFragment) mParentFragment).setChildFragment(new DeviceListFragment(), true, false);
                }
            });
        }
        PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .edit()
                .putInt(C.PREFS_FAVORITE_FRAGMENT, C.FRAGMENT_DEVICE_LIST)
                .apply();


    }

    @Override
    public void onPause() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected item id :  " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_settings:
                Fragment fragment = new DeviceListFragment();
                ((MainFragment) mParentFragment).setChildFragment(fragment, true, false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectRecycleItem(int position) {
        Log.d(TAG, "selectRecycleItem position :  " + position);
        switch (position) {
            case 0:
                Fragment fragment = new MyMediaFragment();
                ((MainFragment) mParentFragment).setChildFragment(fragment, true, false);
                break;
        }
    }
}
