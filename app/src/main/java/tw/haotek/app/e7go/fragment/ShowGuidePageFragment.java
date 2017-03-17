package tw.haotek.app.e7go.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import c.min.tseng.R;
import c.min.tseng.ui.AutoTextureVideoView;
import c.min.tseng.ui.wizardpager.Page;
import c.min.tseng.ui.wizardpager.PageViewCallbacks;
import tw.haotek.app.e7go.Main;


/**
 * Created by Neo on 2015/11/8.
 */
public class ShowGuidePageFragment extends Fragment {
    private static final String TAG = ShowGuidePageFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";
    private PageViewCallbacks mCallbacks;
    protected String mKey;
    protected Page mPage;

    public static ShowGuidePageFragment createWithPageCallback(String key, PageViewCallbacks callback) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        ShowGuidePageFragment fragment = new ShowGuidePageFragment(callback);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowGuidePageFragment(PageViewCallbacks callback) {
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
        View childview = inflater.inflate(R.layout.fragment_showguidepage, container, false);
        TextView show_guide_text = (TextView) childview.findViewById(R.id.show_guide_text);
        AutoTextureVideoView show_guide_movie = (AutoTextureVideoView) childview.findViewById(R.id.show_guide_movie);
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/gitup.mp4");
            if (!file.exists()) {
                ((Main) getActivity()).copyGuideFile();
            }
            show_guide_movie.setVideoPath(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childview;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
