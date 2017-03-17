package tw.haotek.app.e7go.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import c.min.tseng.R;
import tw.haotek.app.e7go.Main;

/**
 * Created by Neo on 2015/12/21.
 */
public class Page5Fragment extends Fragment {
    private static final String TAG = Page5Fragment.class.getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "Context onAttach");
        Log.d(TAG, " Active : " + getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View child = inflater.inflate(R.layout.fragment_page_five, container, false);
        final TextView firststart = (TextView) child.findViewById(R.id.firststart);
        firststart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Main) getActivity()).gotoFragment(new MainFragment(), false);
            }
        });
        return child;
    }
}
