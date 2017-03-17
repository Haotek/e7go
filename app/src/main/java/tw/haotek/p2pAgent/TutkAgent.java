package tw.haotek.p2pAgent;

import android.util.Log;

import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.St_SInfo;

/**
 * Created by Neo on 2016/1/29 0029.
 */
public class TutkAgent extends IOTCAPIs { //FIXME  the Agent like okhttp
    private static final String TAG = TutkAgent.class.getSimpleName();
    //    protected Device mDevice;
//    protected String mUsername;
//    protected String mPassword;
//    protected String mUID;
    protected int mSID = -1;
    public static int mIOTCInitResult = IOTCAPIs.IOTC_ER_TIMEOUT;
    protected St_SInfo m_stSInfo = new St_SInfo();

    //    public TutkAgent(Device device) {
//    public TutkAgent(String name, String pw) {
    public TutkAgent() {
//        mDevice = device;
//        mUsername = name;
//        mPassword = pw;
        final int rec = IOTC_Initialize2(0);
        while (true) {
            if (rec == IOTCAPIs.IOTC_ER_NoERROR || rec == IOTCAPIs.IOTC_ER_ALREADY_INITIALIZED) {
                mIOTCInitResult = rec;
                getSID();
                break;
            } else {
                IOTC_DeInitialize();
            }
        }
    }

    public void unInitIOTC() {
        IOTC_DeInitialize();
    }

    public int getSID() {
        mSID = IOTC_Get_SessionID();
        Log.d(TAG, "Show SID  : " + mSID);
        return mSID;
    }
}
