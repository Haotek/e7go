package tw.haotek.command.HaotekT.device;

import android.util.Log;

import com.tutk.IOTC.AVAPIs;

import java.io.IOException;

import tw.haotek.command.HaotekT.HaotekTCommand;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/30 0030.
 */
public class LoginCommand extends HaotekTCommand {
    private static final String TAG = LoginCommand.class.getSimpleName();
    private String mSessionCookie;
    private String mLoginPassword;

    public LoginCommand(HaotekDevice device) {
        super(device);
    }

    @Override
    protected String getAction() {
        return null;
    }

    @Override
    public Response run() throws IOException {
        final String address = mDevice.getInetAddress();
        final String uid = mUID;
        final int sid = mAgent.getSID();

        Log.d(TAG, "Show UID : " + mUID);
        Log.d(TAG, "Show SID : " + mSID);

        AVAPIs.avInitialize(4);
////        mAVIndex = AVAPIs.avClientStart2(mSID, mDevice.getUsername(), mDevice.getPassword(), 5, new int[1], 0, new int[1]);
//        mAVIndex = AVAPIs.avClientStart2(mSID, mUsername, mPassword, 3, new int[1], 0, new int[1]);
//        Log.d(TAG, " AVIndex   : " + mAVIndex);
//        return rec;
        try {
            final int mAVIndex = AVAPIs.avClientStart2(mSID, mDevice.getUsername(), mDevice.getPassword(), 3, new int[1], 0, new int[1]); //FIXME mAVIndex> 0 is ok SUCCESS
            setConnectIndex(mAVIndex);
        } catch (IllegalArgumentException ex) {
            try {

//            } catch (IllegalStateException | EOFException ex2) {
            } catch (IllegalStateException ex2) {
//                throw new HaotekTException("Http returned null response... " + address, request, ex2);
            }
        }

//        if (envelope.bodyIn instanceof SoapObject) { // SoapObject = SUCCESS
//            return dispatchSoapResponse((SoapObject) envelope.bodyIn);
//        } else if (envelope.bodyIn instanceof SoapFault) { // SoapFault = FAILURE
//            throw (SoapFault) envelope.bodyIn;
//        }
        throw new RuntimeException("Shouldn't get here!!! " + address);
    }
}
