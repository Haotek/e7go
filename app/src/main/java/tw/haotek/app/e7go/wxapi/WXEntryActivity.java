package tw.haotek.app.e7go.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import tw.haotek.app.e7go.C;


/**
 * Created by Neo on 2016/1/9 0009.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private int mResult = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        WXAPIFactory.createWXAPI(this, C.WECHAT_APP_ID, true).handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Log.e(TAG, "onReq : " + req);
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onResp Type  : " + resp.getType());
        mResult = resp.getType();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                        Log.e("resp.errCode", "!!!" + sendResp.errCode);
                        Log.e("resp.state", "!!!" + sendResp.state);
                        Log.e("resp.token", "!!!" + sendResp.token);
                        //secret = getResources().getString(R.string.com_wechat_api_wechat_API_SECRET);
                        //appId = WeChatControl.getInstance().getAppKey(this);

                        //use http get to call two api
                        //"https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appId + "&secret=" + secret + "&code=" + toekn + "&grant_type=authorization_code";
                        //"https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid;


                        //官方文件寫錯，是token，不是code
                        //拿到了微信返回的code,立马再去请求access_token
                        //String code = ((SendAuth.Resp) resp).code;

                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                        PreferenceManager
                                .getDefaultSharedPreferences(this)
                                .edit()
                                .putBoolean(C.IS_WECHAT_TOKEN, true)
                                .putString(C.WECHAT_TOKEN, sendResp.token)
                                .apply();
                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        finish();
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Log.d(TAG, "onResume Show Result : " + mResult);
        if (mResult == RETURN_MSG_TYPE_LOGIN) {
            onBackPressed();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed() ");
    }
}
