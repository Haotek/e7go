package tw.haotek.command.Tutk.stroage;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import c.min.tseng.util.TimeMethod;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.dut.data.PhotoInfo;

/**
 * Created by Neo on 2016/1/17 0017.
 */
public class GetPhotoFileListTCommand extends TutkCommand {
    private static final String TAG = GetPhotoFileListTCommand.class.getSimpleName();

    public GetPhotoFileListTCommand(HaotekDevice device) {
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
        return WiFiCommandDefine.GET_FILE_LIST;
    }

    @Override
    public void run() {//FIXME  use Tutk command  not WiFiCommand over Tutk
        Log.d(TAG, "run()");
//        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_WIFICMD_REQ, Convert.sendWiFiCmdRequest(0, 0, 0, getActionID(), 1, 0, getAction().length(), getAction()));//FIXME WiFiCommand over Tutk
        final long stopTime = System.currentTimeMillis();
//        final String string_date = "12-December-2015";

//        final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
//        Date d = null;
//        try {
//            d = f.parse(string_date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        final long starttime = d.getTime();
        long starttime = 0;
        try {
            starttime = TimeMethod.StringTolong("201512010000");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //FIXME
        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTFILE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlListFileReq.parseConent(0, starttime, stopTime, (byte) 0, (byte) 0, AVIOCTRLDEFs.AVIOCTRL_PHOTO_FILE));
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
//        final byte[] resultbyte = new byte[972];//FIXME size ?
//        System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
//        final String[] result = Convert.byteArray2String(resultbyte).split("\n");//FIXME  if use 3014 will get more ,
//        Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));

        final List<PhotoInfo> result = new ArrayList<PhotoInfo>();
        if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTFILE_RESP) {
            final int end = data[9];
            final int cnt = data[10];
            final byte type = data[11];
            if (cnt > 0 && type == AVIOCTRLDEFs.AVIOCTRL_PHOTO_FILE) {//FIXME Photo
                final int pos = 12;
                final int size = AVIOCTRLDEFs.SAvFile.getTotalSize();
                result.clear();
                for (int i = 0; i < cnt; i++) {
                    final byte[] t = new byte[8];
                    System.arraycopy(data, i * size + pos, t, 0, 8);
                    AVIOCTRLDEFs.STimeDay time = new AVIOCTRLDEFs.STimeDay(t);
                    final byte event = data[i * size + pos + 8];
                    final byte status = data[i * size + pos + 9];
                    final byte length = data[i * size + pos + 11];
                    String path = new String(data, i * size + pos + 12, length).replace("\\", "/");
                    String[] filter = path.split("\\\\");
                    final int path_end = filter.length;
                    result.add(new PhotoInfo(event, time, status, path));
                    Log.d(TAG, "add photo list path : " + path);
                }
            }
            if (mResponseListener != null) {
                mResponseListener.dispatchResponse(result);
            }
            mAgent.unregisterIOTCListener(this);
        }
    }
}


