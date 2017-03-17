package tw.haotek.command.Tutk.stroage;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;

import c.min.tseng.util.Convert;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/17 0017.
 */
public class GetDiskFreeSpaceTCommand extends TutkCommand {
    private static final String TAG = GetDiskFreeSpaceTCommand.class.getSimpleName();

    public GetDiskFreeSpaceTCommand(HaotekDevice device) {
        super(device);
    }

    @Override
    protected String getAction() {
        final String req = "custom=1&cmd=" + getActionID();
        Log.d(TAG, "custom command  : " + req);
        return req;
    }

    @Override
    protected int getActionID() {
        return WiFiCommandDefine.GET_DISK_FREE_SPACE;
    }

    @Override
    public void run() {
        Log.d(TAG, "run()");
        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, Convert.sendWiFiCmdRequest(0, 0, 0, getActionID(), 1, 0, getAction().length(), getAction()));
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
//        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
        if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_RESP) {
            final int cmdid = byteArrayToInt_Little(data, 12);
            if (cmdid == getActionID()) {
                Log.d(TAG, "Here Get WiFi Command Response CMDID :  " + cmdid);
                final byte[] resultbyte = new byte[972];//FIXME size ?
                System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
                Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));
                final String[] result = Convert.byteArray2String(resultbyte).split("\n");//FIXME  if use 3014 will get more ,
                if (mResponseListener != null) {
                    mResponseListener.dispatchResponse(result);
                }
                mAgent.unregisterIOTCListener(this);
            }
        }
    }
}

