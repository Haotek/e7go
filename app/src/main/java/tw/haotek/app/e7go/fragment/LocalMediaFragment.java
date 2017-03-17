package tw.haotek.app.e7go.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import c.min.tseng.R;
import tw.haotek.app.e7go.adapter.LocalMediaAdapter;

/**
 * Created by Neo on 2015/12/27.
 */
public class LocalMediaFragment extends Fragment {
    private static final String TAG = LocalMediaFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;

    public LocalMediaFragment() {
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
            mTitle.setText(R.string.video_folder);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        View child = inflater.inflate(R.layout.fragment_mymedia, container, false);
        RecyclerView homerecyclerview = (RecyclerView) child.findViewById(R.id.medialayout);
        final LinearLayoutManager mymedialinear = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        homerecyclerview.setLayoutManager(mymedialinear);
//        homerecyclerview.setAdapter(new LocalMediaAdapter(context));
        homerecyclerview.setAdapter(new LocalMediaAdapter());
        return child;
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
            mTitle.setText(R.string.video_folder);
        }
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

}
