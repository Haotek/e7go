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

interface MyMediaCallback {
    void selectRecycleItem(int position);
}

/**
 * Created by Neo on 2015/12/21.
 */
public class MyMediaFragment extends Fragment implements MyMediaCallback {
    private static final String TAG = MyMediaFragment.class.getSimpleName();
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;

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
            mTitle.setText(R.string.media_description);
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
        homerecyclerview.setAdapter(new MyMediaFunctionAdapter(this));
        return child;
    }

    private class MyMediaFunctionAdapter extends RecyclerView.Adapter<MyMediaFunctionAdapter.ViewHolder> {
        private final String TAG = MyMediaFunctionAdapter.class.getSimpleName();
        private int[] mFunctionName;
        private int[] mFunctionIcon;
        private MyMediaCallback mCallBack;

        public MyMediaFunctionAdapter(MyMediaCallback callback) {//FIXME
            mCallBack = callback;
//            mFunctionName = new int[]{R.string.remote_media, R.string.video_folder, R.string.emergency_folder, R.string.collection_folder};
//            mFunctionIcon = new int[]{R.drawable.ic_mymedia_sd, R.drawable.ic_mymedia_video, R.drawable.ic_mymedia_event, R.drawable.ic_mymedia_fav};//FIXME from http?
            mFunctionName = new int[]{R.string.video_folder, R.string.emergency_folder, R.string.collection_folder};
            mFunctionIcon = new int[]{R.drawable.ic_mymedia_video, R.drawable.ic_mymedia_event, R.drawable.ic_mymedia_fav};//FIXME from http?
        }

        @Override
        public int getItemCount() {
            return mFunctionName.length;
        }

        @Override
        public MyMediaFunctionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mymedia_function, parent, false);
            ViewHolder vh = new ViewHolder(child);
            return vh;
        }

        @Override
        public void onBindViewHolder(final MyMediaFunctionAdapter.ViewHolder holder, final int position) {
            holder.mymedia_fuction.setText(mFunctionName[position]);
            holder.mymedia_fuction_icon.setImageResource(mFunctionIcon[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.selectRecycleItem(position);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mymedia_fuction_icon;
            TextView mymedia_fuction;

            public ViewHolder(View itemView) {
                super(itemView);
                mymedia_fuction_icon = (ImageView) itemView.findViewById(R.id.mymedia_fuction_icon);
                mymedia_fuction = (TextView) itemView.findViewById(R.id.mymedia_fuction);
            }
        }
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
            mTitle.setText(R.string.media_description);
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

    @Override
    public void selectRecycleItem(int position) {
        Log.d(TAG, "selectRecycleItem position :  " + position);
        switch (position) {
            case 0:
                Fragment fragment = new LocalMediaFragment();
                ((MainFragment) mParentFragment).setChildFragment(fragment, true, false);
                break;
        }
    }
}
