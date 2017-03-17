package tw.haotek.command.Tutk;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import c.min.tseng.util.Convert;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.dut.data.DeviceInfo;

/**
 * Created by Neo on 2016/1/17 0017.
 */
public class GetDeviceInfoTCommand extends TutkCommand {
    private static final String TAG = GetDeviceInfoTCommand.class.getSimpleName();

    public GetDeviceInfoTCommand(HaotekDevice device) {
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
        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, Convert.sendWiFiCmdRequest(0, 0, 0, getActionID(), 1, 0, getAction().length(), getAction()));
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
        //FIXME  avIOCtrlMsgType  like : AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP / AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_RESP
//        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
        if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_RESP) {
            final int cmdid = byteArrayToInt_Little(data, 12);
            if (cmdid == getActionID()) {
                Log.d(TAG, "Here Get WiFi Command Response CMDID :  " + cmdid);
                final byte[] resultbyte = new byte[972];//FIXME size ?
                System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
                Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));
                DeviceInfo result = null;//FIXME  null
                final String[] rawstring = Convert.byteArray2String(resultbyte).split(",");//FIXME  if use 3014 will get more ,
                final String soap = rawstring[2];
                Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));//FIXME  sometime not xml tag
                try {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(soap));
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = xpp.getName();
                            Log.d(TAG, " tagName : " + tagName);
                            switch (tagName) {
                                case "FirmwareVersion":
                                    result = new DeviceInfo();
                                    result.mFirmwareVersion = xpp.nextText();
                                    break;
                                case "Model":
                                    result.mModel = xpp.nextText();
                                    break;
                                case "MAC":
                                    result.mMAC = xpp.nextText();
                                    break;
                                case "UID":
                                    result.mUID = xpp.nextText();
                                    break;
                                case "STREAMTYPE":
                                    result.mMode = xpp.nextText();
                                    break;
                            }
                        }
                        eventType = xpp.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mResponseListener != null) {
                    mResponseListener.dispatchResponse(result);
                }
                mAgent.unregisterIOTCListener(this);
            }
        }
    }
}
