package tw.haotek.app.e7go.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import c.min.tseng.R;
import c.min.tseng.util.WeChatUtil;
import tw.haotek.app.e7go.C;

/**
 * Created by Neo on 2015/12/21.
 */

public class RegisteredFragment extends Fragment {
    private static final String TAG = RegisteredFragment.class.getSimpleName();
    private Context mContext;
    private Fragment mParentFragment;
    private Button mLeft;
    private Button mRight;
    private TextView mTitle;
    private ImageView mLogo;
    private IWXAPIEventHandler mWeChatEventHandler;
    private IWXAPI wechatapi;
    private static OkHttpClient sHttp = new OkHttpClient();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
            mTitle.setText(R.string.login);
        }
        WeChatUtil.getWeChatUtil().getWeChatAPI(context, C.WECHAT_APP_ID);
        WeChatUtil.getWeChatUtil().registerApptoWeChat();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final boolean isregister = prefs.getBoolean(C.IS_REGISTERED, false);
        if (isregister) {
            ((MainFragment) mParentFragment).setChildFragment(new HomeFragment(), false, false);//FIXME  need error handler
//            final String token = prefs.getString(C.WECHAT_TOKEN, "");
//            final String uuid = telephonyManager.getDeviceId();
//            new Thread() {
//                public void run() {
//                    try {
//                        final TrustManager[] trustAllCerts = new TrustManager[]{
//                                new X509TrustManager() {
//                                    @Override
//                                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                                    }
//
//                                    @Override
//                                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                                    }
//
//                                    @Override
//                                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                                        return null;
//                                    }
//                                }
//                        };
//                        final SSLContext sslContext = SSLContext.getInstance("SSL");
//                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//                        sHttp.setSslSocketFactory(sslSocketFactory);
//                        sHttp.setHostnameVerifier(new HostnameVerifier() {
//                            @Override
//                            public boolean verify(String hostname, SSLSession session) {
//                                return true;
//                            }
//                        });
//                        final Request request = new Request.Builder()
//                                .url(" https://cloud.e7show.cn/auth/wechat?cmd=auth&platform=android&uuid=" + uuid + "&code=" + token)
//                                .get()
//                                .build();
//                        final Call call = sHttp.newCall(request);
//                        final com.squareup.okhttp.Response response = call.execute();
//                        final String body = response.body().string();
//                        if (response.isSuccessful()) {
//                            JSONObject jsonObj;
//                            String authstate = "";
//                            try {
//                                jsonObj = new JSONObject(body);
//                                authstate = jsonObj.getString("AUTH_STATUS");
//                                if (authstate.equals("OK")) {
//                                    ((MainFragment) mParentFragment).setChildFragment(new HomeFragment(), false, false);//FIXME  need error handler
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (KeyManagementException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        final View child = inflater.inflate(R.layout.fragment_registered, container, false);
        final RelativeLayout login_wechat_layout = (RelativeLayout) child.findViewById(R.id.login_wechat_layout);
        login_wechat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatUtil.getWeChatUtil().login("e7go_get_wechat_auth");
            }
        });
        final TextView tryit = (TextView) child.findViewById(R.id.tryit);
        tryit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainFragment) mParentFragment).setChildFragment(new HomeFragment(), false, false);//FIXME
            }
        });
        return child;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        final boolean is_reg = prefs.getBoolean(C.IS_REGISTERED, false);
        final boolean is_wechat_token = prefs.getBoolean(C.IS_WECHAT_TOKEN, false);
        final TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (!is_reg && is_wechat_token) {
            final String token = prefs.getString(C.WECHAT_TOKEN, "");
            final String uuid = telephonyManager.getDeviceId();
            new Thread() {
                public void run() {
                    try {
                        final TrustManager[] trustAllCerts = new TrustManager[]{
                                new X509TrustManager() {
                                    @Override
                                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                    }

                                    @Override
                                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                    }

                                    @Override
                                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                        return null;
                                    }
                                }
                        };
                        final SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                        sHttp.setSslSocketFactory(sslSocketFactory);
                        sHttp.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                        Request request = new Request.Builder()
                                .url(" https://cloud.e7show.cn/auth/wechat?cmd=login&platform=android&uuid=" + uuid + "&code=" + token)
                                .get()
                                .build();
                        Call call = sHttp.newCall(request);
                        com.squareup.okhttp.Response response = call.execute();
                        String body = response.body().string();
                        if (response.isSuccessful()) {
                            JSONObject jsonObj;
                            String authstate = "";
                            try {
                                jsonObj = new JSONObject(body);
                                authstate = jsonObj.getString("AUTH_STATUS");
                                Log.d(TAG, "Show AUTH_STATUS : " + authstate);
                                if (authstate.equals("OK")) {
                                    prefs.edit()
                                            .putBoolean(C.IS_REGISTERED, true)
                                            .apply();
                                    ((MainFragment) mParentFragment).setChildFragment(new HomeFragment(), false, false);//FIXME
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }
    }
}
