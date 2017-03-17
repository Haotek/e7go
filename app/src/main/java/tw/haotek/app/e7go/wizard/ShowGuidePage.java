package tw.haotek.app.e7go.wizard;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

import c.min.tseng.ui.wizardpager.ModelCallbacks;
import c.min.tseng.ui.wizardpager.Page;
import c.min.tseng.ui.wizardpager.PageViewCallbacks;
import c.min.tseng.ui.wizardpager.ReviewItem;
import tw.haotek.app.e7go.fragment.ShowGuidePageFragment;


/**
 * Created by Neo on 2015/11/8.
 */
public class ShowGuidePage extends Page {
    private static final String TAG = ShowGuidePage.class.getSimpleName();

    public ShowGuidePage(ModelCallbacks callbacks, PageViewCallbacks pcallbacks, String title) {
        super(callbacks, pcallbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return ShowGuidePageFragment.createWithPageCallback(getKey(), getPageViewCallbacks());
    }

    public ShowGuidePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

}
