package tw.haotek.app.e7go.wizard;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import c.min.tseng.ui.wizardpager.ModelCallbacks;
import c.min.tseng.ui.wizardpager.Page;
import c.min.tseng.ui.wizardpager.PageViewCallbacks;
import c.min.tseng.ui.wizardpager.ReviewItem;
import tw.haotek.app.e7go.fragment.ChoiceAPModeDevicePageFragment;


/**
 * Created by Neo on 2015/11/8.
 */
public class ChoiceDevicePage extends Page {
    private static final String TAG = ChoiceDevicePage.class.getSimpleName();
    protected ArrayList<String> mChoices = new ArrayList<String>();

    public ChoiceDevicePage(ModelCallbacks callbacks, PageViewCallbacks pcallbacks, String title) {
        super(callbacks, pcallbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return ChoiceAPModeDevicePageFragment.createWithPageCallback(getKey(), getPageViewCallbacks());
    }

    public ChoiceDevicePage setChoices(String... choices) {
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    public ChoiceDevicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }
}
