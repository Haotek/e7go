package tw.haotek.command.Tutk;

import android.graphics.Bitmap;
import android.util.Log;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/15 0015.
 */
//FIXME  Temp Use  will mixed into HaotekCommand
//public abstract class TutkCommand implements IRegisterIOTCListener, Runnable {
public abstract class TutkCommand implements IRegisterIOTCListener {
    private static final String TAG = TutkCommand.class.getSimpleName();
    protected HaotekDevice mDevice;
    protected Camera mAgent;
    protected ResponseListener mResponseListener;

    public interface ResponseListener {
        //        void dispatchResponse(String result);
        void dispatchResponse(Object result);
    }

    public void setResponseListener(ResponseListener listener) {
        this.mResponseListener = listener;
    }

    public TutkCommand(HaotekDevice device) {
        mDevice = device;
        mAgent = (Camera) mDevice.getDeviceP2PAgent();
        if (mAgent != null) {
            mAgent.registerIOTCListener(this);
        }

//        Log.d(TAG, "Show name" + getDevice().getUsername());
//        Log.d(TAG,"Show password "+getDevice().getPassword());
//        Log.d(TAG, mAgent + "  connect  ");
//        mAgent.connect(getDevice().getUID());
//        Log.d(TAG, mAgent+ "  start  ");
//        mAgent.start(Camera.DEFAULT_AV_CHANNEL, getDevice().getUsername(),getDevice().getPassword());
//        Log.d(TAG,"Set Agent ");

//        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTFILE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlListFileReq
//                .parseConent(0, 0, System.currentTimeMillis(), (byte) 0, (byte) 0, AVIOCTRLDEFs.AVIOCTRL_PHOTO_FILE));
//        String req = "custom=1&cmd=3014";//FIXME not work ?!!!
//        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, AVIOCTRLDEFs.SMsgAVIoctrlWifiCmdReq.parseContent
//                (0, 0, 0, 3014, 1, 0, req.length(), req));
//        Log.d(TAG, "custom command  : " + req);
    }

    public HaotekDevice getDevice() {
        return mDevice;
    }

    public void setDevice(HaotekDevice device) {
        mDevice = device;
    }

    protected abstract String getAction();

    protected abstract int getActionID();

//    public void run() {
//        Log.d(TAG, "getResponse");
//        Log.d(TAG, "run()");
//        final int actionid = 9003;
//        String req = "custom=1&cmd=" + actionid;
//        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, Convert.sendWiFiCmdRequest(0, 0, 0, 3014, 1, 0, req.length(), req));
//        Log.d(TAG, "custom command  : " + req);
//
//    }

    public abstract void run();

    @Override
    public void receiveFrameData(Camera camera, int i, Bitmap bitmap) {
        Log.d(TAG, "Device _ " + mDevice.getMACAddress() + " -Get   receiveFrameData :");
    }

    @Override
    public void receiveFrameDataForMediaCodec(Camera camera, int i, byte[] bytes, int i1, int i2, byte[] bytes1, boolean b, int i3) {
        Log.d(TAG, "Device _ " + mDevice.getMACAddress() + "Get   receiveFrameDataForMediaCodec :");
    }

    @Override
    public void receiveFrameInfo(Camera camera, int i, long l, int i1, int i2, int i3, int i4) {
        Log.d(TAG, "Device _ " + mDevice.getMACAddress() + "Get  receiveFrameInfo");
    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {
//        return dispatchResponse(body);
        Log.d(TAG, "Device _ " + mDevice.getMACAddress() + "Get   receiveSessionInfo__resultCode :" + resultCode);
        switch (resultCode) {
            case Camera.CONNECTION_STATE_NONE:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_NONE");
                break;
            case Camera.CONNECTION_STATE_CONNECTING:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_CONNECTING");
                break;
            case Camera.CONNECTION_STATE_CONNECTED:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_CONNECTED");
                break;
            case Camera.CONNECTION_STATE_DISCONNECTED:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_DISCONNECTED");
                break;
            case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_UNKNOWN_DEVICE");
                break;
            case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_WRONG_PASSWORD");
                break;
            case Camera.CONNECTION_STATE_TIMEOUT:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_TIMEOUT");
                break;
            case Camera.CONNECTION_STATE_UNSUPPORTED:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_UNSUPPORTED");
                break;
            case Camera.CONNECTION_STATE_CONNECT_FAILED:
                Log.d(TAG, "SessionInfo CONNECTION_STATE_CONNECT_FAILED");
                break;
        }
    }

    @Override
    public void receiveChannelInfo(final Camera camera, int sessionChannel, int resultCode) {
//        return dispatchResponse(body);
        Log.d(TAG, "Device _ " + mDevice.getMACAddress() + "Get   receiveChannelInfo  SessionChannel : " + sessionChannel + "__resultCode :" + resultCode);
//        getDevice().setConnectSessionChannel(sessionChannel);
        switch (resultCode) {
            case Camera.CONNECTION_STATE_NONE:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_NONE");
                break;
            case Camera.CONNECTION_STATE_CONNECTING:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_CONNECTING");
                break;
            case Camera.CONNECTION_STATE_CONNECTED:
                Log.d(TAG, " ChannelInfo CONNECTION_STATE_CONNECTED");
                break;
            case Camera.CONNECTION_STATE_DISCONNECTED:
                Log.d(TAG, " ChannelInfo CONNECTION_STATE_DISCONNECTED");
                break;
            case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_UNKNOWN_DEVICE");
                break;
            case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_WRONG_PASSWORD");
                break;
            case Camera.CONNECTION_STATE_TIMEOUT:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_TIMEOUT");
                break;
            case Camera.CONNECTION_STATE_UNSUPPORTED:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_UNSUPPORTED");
                break;
            case Camera.CONNECTION_STATE_CONNECT_FAILED:
                Log.d(TAG, "ChannelInfo CONNECTION_STATE_CONNECT_FAILED");
                break;
        }
    }

    protected static final int byteArrayToInt_Little(byte byt[], int nBeginPos) {
        return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos + 1]) << 8 | (0xff & byt[nBeginPos + 2]) << 16 | (0xff & byt[nBeginPos + 3]) << 24;
    }

//    @Override
//    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
//        //FIXME  avIOCtrlMsgType  like : AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP / AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_RESP
//        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
////        AVIOCTRLDEFs.SMsgAVIoctrlWifiCmdResp.fillContent(data);
////        byte[] resultbyte = new byte[AVIOCTRLDEFs.SMsgAVIoctrlWifiCmdResp.nDataLength];
////        System.arraycopy(AVIOCTRLDEFs.SMsgAVIoctrlWifiCmdResp.response, 0, resultbyte, 0,AVIOCTRLDEFs.SMsgAVIoctrlWifiCmdResp.nDataLength);//FIXME
//
////        Convert.WiFiCmdResponse.fillContent(data);//FIXME
////        byte[] resultbyte = new byte[Convert.WiFiCmdResponse.nDataLength];//FIXME
////        System.arraycopy(來源陣列，起始索引值，目的陣列，起始索引值，複製長度);
////        System.arraycopy(Convert.WiFiCmdResponse.response, 0, resultbyte, 0, Convert.WiFiCmdResponse.nDataLength);//FIXME
////        System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
//
////        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();//FIXME not work
//
//        final byte[] resultbyte = new byte[972];//FIXME size ?
//        System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
//        final String result = Convert.byteArray2String(resultbyte);
//        Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));
//        mResponseListener.dispatchResponse(result);
//        mAgent.unregisterIOTCListener(this);
////        dispatchResponse(Convert.byteArray2String(resultbyte, ""));
////        return dispatchResponse(body);
//    }


}
