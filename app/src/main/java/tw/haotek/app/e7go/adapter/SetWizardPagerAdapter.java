package tw.haotek.app.e7go.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import tw.haotek.app.e7go.fragment.Page1Fragment;
import tw.haotek.app.e7go.fragment.Page2Fragment;
import tw.haotek.app.e7go.fragment.Page3Fragment;
import tw.haotek.app.e7go.fragment.Page4Fragment;
import tw.haotek.app.e7go.fragment.Page5Fragment;

/**
 * Created by Neo on 2015/12/21.
 */
public class SetWizardPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = SetWizardPagerAdapter.class.getSimpleName();
    private ArrayList<Fragment> mExplorerFragment = new ArrayList();

    public SetWizardPagerAdapter(FragmentManager fm) {
        super(fm);
        mExplorerFragment.add(new Page1Fragment());
        mExplorerFragment.add(new Page2Fragment());
        mExplorerFragment.add(new Page3Fragment());
        mExplorerFragment.add(new Page4Fragment());
        mExplorerFragment.add(new Page5Fragment());
    }

    @Override
    public int getCount() {//FIXME
        return mExplorerFragment.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mExplorerFragment.get(position);
    }
}
