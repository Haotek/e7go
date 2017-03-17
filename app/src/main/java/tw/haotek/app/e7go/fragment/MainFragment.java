package tw.haotek.app.e7go.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import c.min.tseng.R;

/**
 * Created by Neo on 2015/12/20.
 */
public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainFragment.class.getSimpleName();
    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragmen_main, container, false);
        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        final TextView title = (TextView) mToolbar.findViewById(R.id.title);
        title.setVisibility(View.GONE);
        final ImageView logo = (ImageView) mToolbar.findViewById(R.id.logo);
//        logo.setImageResource(R.drawable.logo);
        logo.setVisibility(View.GONE);
        final Button left = (Button) mToolbar.findViewById(R.id.left);
        left.setVisibility(View.GONE);
        final Button right = (Button) mToolbar.findViewById(R.id.right);
        right.setVisibility(View.GONE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) root.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        final Resources resources = HaotekApplication.getContext().getResources();
//        final String[] supportlist = resources.getStringArray(R.array.support_packagename_list);
//        final int[] headershow = resources.getIntArray(R.array.support_packagename_navigation_header);//FIXME header Visibility int
//        final String packag = HaotekApplication.getContext().getPackageName();
//        final int count = Math.min(supportlist.length, headershow.length);
        if (navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);
            RelativeLayout headerbg = (RelativeLayout) header.findViewById(R.id.headerbg);
            ImageView userimage = (ImageView) header.findViewById(R.id.userimage);
            TextView username = (TextView) header.findViewById(R.id.username);
            TextView userinfo = (TextView) header.findViewById(R.id.userinfo);
        }

//        Fragment fragment = null;
//        if (savedInstanceState == null) {
//            Bundle arguments = getArguments();
//            if (arguments != null) {
//                final int layout = arguments.getInt(C.ARGUMENT_CONTENT_FRAGMENT);
//                switch (layout) {
//                    case R.layout.scenes_fragment:
//                        fragment = new ScenesFragment();
//                        break;
//                }
//            } else {
//                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                final int layout = prefs.getInt(C.PREFS_FAVORITE_FRAGMENT, R.layout.stupid_button_fragment);
//                switch (layout) {
//                    case C.FRAGMENT_SCHEDULE_LIST:
//                        fragment = new ScheduleListFragment();
//                        break;
//                    case C.FRAGMENT_DEVICE_LIST:
//                        fragment = new DeviceListFragment();
//                        break;
//                    case C.FRAGMENT_SCENE:
//                        fragment = new ScenesFragment();
//                        break;
//                    case C.FRAGMENT_CAMERA_TILE:
//                        fragment = new CameraTileFragment();
//                        break;
//                    case C.FRAGMENT_STUPID_BUTTON:
//                    default:
//                        fragment = new StupidButtonFragment();
//                }
//            }
//        } else {
//            fragment = getChildFragmentManager().findFragmentById(R.id.container);
//        }
        Fragment fragment = new RegisteredFragment();
        if (fragment != null) {
            fragment.onHiddenChanged(false);
            setChildFragment(fragment, true, true);
        }
        return root;
    }

    public void setChildFragment(Fragment fragment, boolean stack, boolean anim) {
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (anim) {
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            }
            transaction.replace(R.id.container, fragment);
            if (stack) {
                Log.d(TAG, "setChildFragment() addToBackStack : " + fragment.getClass().getSimpleName());
                transaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            transaction.commit();
        } catch (IllegalStateException ex) {
            Log.d(TAG, "setChildFragment(): illegal state: " + ex.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_web) {
//            fragment = new MyMediaFragment();
        } else if (id == R.id.nav_product) {
//            fragment = new FacebookFragment();
        } else if (id == R.id.nav_support) {
//            fragment = new SupportFragment();
        } else if (id == R.id.nav_info) {
//            fragment = new SettingsFragment();
        }

        if (fragment != null) {
            setChildFragment(fragment, false, false);
        }
        DrawerLayout drawer = getDrawerLayout();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

