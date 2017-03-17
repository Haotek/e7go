package tw.haotek.app.e7go.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import c.min.tseng.R;
import tw.haotek.app.e7go.C;
import tw.haotek.app.e7go.Main;

/**
 * Created by Neo on 2015/10/23.
 */
public class WelcomeFragment extends Fragment {
    private static final String TAG = WelcomeFragment.class.getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "Show this app package name way 1  : " + context.getPackageName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final Context context = container.getContext();
        View root = inflater.inflate(R.layout.fragment_welcom, container, false);
        final RelativeLayout welcome = (RelativeLayout) root.findViewById(R.id.welcome);
        welcome.setBackgroundResource(R.drawable.splash_bg);
//        final Resources resources = HaotekApplication.getContext().getResources();
//        final String[] supportlist = resources.getStringArray(R.array.support_packagename_list);
//        final TypedArray supportlistbg = resources.obtainTypedArray(R.array.support_packagename_iconres);
//        final String packag = HaotekApplication.getContext().getPackageName();
//        final int count = Math.min(supportlist.length, supportlistbg.length());
//        for (int i = 0; i < count; ++i) {
//            final String pname = supportlist[i];
//            if (pname.equalsIgnoreCase(packag)) {
//                welcome.setBackgroundResource(supportlistbg.getResourceId(i, R.drawable.splash_bg));
//            }
//        }

        final ImageView splashiv = (ImageView) root.findViewById(R.id.splashiv);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.welcome_anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                splashiv.setVisibility(View.INVISIBLE);
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                final boolean isregister = prefs.getBoolean(C.IS_REGISTERED, false);

                if (isregister) {
                    ((Main) getActivity()).gotoFragment(new MainFragment(), false);
                } else {
                    ((Main) getActivity()).gotoFragment(new FirstStartFragment(), false);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        splashiv.startAnimation(anim);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
