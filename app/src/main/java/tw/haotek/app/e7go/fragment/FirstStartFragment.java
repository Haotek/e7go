package tw.haotek.app.e7go.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutk.IOTC.IOTCAPIs;

import c.min.tseng.R;
import me.relex.circleindicator.CircleIndicator;
import tw.haotek.app.e7go.adapter.SetWizardPagerAdapter;

/**
 * Created by Neo on 2015/12/21.
 */
public class FirstStartFragment extends Fragment {
    private static final String TAG = FirstStartFragment.class.getSimpleName();
    private ViewPager mViewPager;
    private SetWizardPagerAdapter mAdapter;

    public FirstStartFragment() {
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new SetWizardPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_first_start, container, false);
        mViewPager = (ViewPager) root.findViewById(R.id.setupwizard);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setCurrentItem(0);
        CircleIndicator setupwizardindicator = (CircleIndicator) root.findViewById(R.id.setupwizardindicator);
        setupwizardindicator.setViewPager(mViewPager);
        return root;
    }

    private String getIOTCAPis() {

        byte[] bytVer = new byte[4];
        int[] lVer = new int[1];
        int ver;

        IOTCAPIs.IOTC_Get_Version(lVer);

        ver = (int) lVer[0];

        StringBuffer sb = new StringBuffer();
        bytVer[3] = (byte) (ver);
        bytVer[2] = (byte) (ver >>> 8);
        bytVer[1] = (byte) (ver >>> 16);
        bytVer[0] = (byte) (ver >>> 24);
        sb.append((int) (bytVer[0] & 0xff));
        sb.append('.');
        sb.append((int) (bytVer[1] & 0xff));
        sb.append('.');
        sb.append((int) (bytVer[2] & 0xff));
        sb.append('.');
        sb.append((int) (bytVer[3] & 0xff));

        return sb.toString();
    }

    public void onResume() {
        super.onResume();
    }
}

