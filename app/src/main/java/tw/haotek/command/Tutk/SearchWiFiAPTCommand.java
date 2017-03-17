package tw.haotek.command.Tutk;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Packet;

import java.util.ArrayList;
import java.util.List;

import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/17 0017.
 */
public class SearchWiFiAPTCommand extends TutkCommand {
    private static final String TAG = SearchWiFiAPTCommand.class.getSimpleName();

    public SearchWiFiAPTCommand(HaotekDevice device) {
        super(device);
    }

    @Override
    protected int getActionID() {
        return WiFiCommandDefine.WIFIAPP_CMD_GET_DEVICE_INFO;
    }

    @Override
    protected String getAction() {
        String req = "custom=1&cmd=" + getActionID();
        Log.d(TAG, "custom command  : " + req);
        return "custom=1&cmd=" + getActionID();
    }

    @Override
    public void run() {
        Log.d(TAG, "run()");
//        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, Convert.sendWiFiCmdRequest(0, 0, 0, getActionID(), 1, 0, getAction().length(), getAction()));
        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ, new byte[4]);
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
        //FIXME  avIOCtrlMsgType  like : AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP / AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_RESP
//        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
        if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_RESP) {
            final List<AVIOCTRLDEFs.SWifiAp> result = new ArrayList<AVIOCTRLDEFs.SWifiAp>();//FIXME
            int cnt = Packet.byteArrayToInt_Little(data, 0);
            int size = AVIOCTRLDEFs.SWifiAp.getTotalSize();
            result.clear();
            if (cnt > 0 && data.length >= 40) {
                int pos = 4;
                for (int i = 0; i < cnt; i++) {
                    if ((i * size + pos) >= data.length) {
                        break;
                    }
                    byte[] ssid = new byte[32];
                    System.arraycopy(data, i * size + pos, ssid, 0, 32);
                    byte mode = data[i * size + pos + 32];
                    byte enctype = data[i * size + pos + 33];
                    byte signal = data[i * size + pos + 34];
                    byte status = data[i * size + pos + 35];
                    result.add(new AVIOCTRLDEFs.SWifiAp(ssid, mode, enctype, signal, status));
                }
            }
            if (mResponseListener != null) {
                mResponseListener.dispatchResponse(result);
            }
            mAgent.unregisterIOTCListener(this);
        }
    }
}
