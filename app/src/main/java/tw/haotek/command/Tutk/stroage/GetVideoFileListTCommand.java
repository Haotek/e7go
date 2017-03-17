package tw.haotek.command.Tutk.stroage;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;

import java.util.ArrayList;
import java.util.List;

import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.dut.data.EventInfo;

/**
 * Created by Neo on 2016/1/17 0017.
 */
public class GetVideoFileListTCommand extends TutkCommand {
    private static final String TAG = GetVideoFileListTCommand.class.getSimpleName();

    public GetVideoFileListTCommand(HaotekDevice device) {
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
        //FIXME
        mAgent.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTFILE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlListFileReq.parseConent(0, 0, stopTime, (byte) 0, (byte) 0, AVIOCTRLDEFs.AVIOCTRL_VIDEO_FILE));
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {
        Log.d(TAG, "Get  receiveIOCtrlData: " + sessionChannel + "__avIOCtrlMsgType :" + avIOCtrlMsgType + "__byte data :" + data);
//        final byte[] resultbyte = new byte[972];//FIXME size ?
//        System.arraycopy(data, 28, resultbyte, 0, Convert.byteArrayToInt_Little(data, 24));
//        final String[] result = Convert.byteArray2String(resultbyte).split("\n");//FIXME  if use 3014 will get more ,
//        Log.d(TAG, "Show data to String :  " + Convert.byteArray2String(resultbyte));

        final List<EventInfo> result = new ArrayList<EventInfo>();
        if (avIOCtrlMsgType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTFILE_RESP) {
            final int end = data[9];
            final int cnt = data[10];
            final byte type = data[11];
            if (cnt > 0 && type == AVIOCTRLDEFs.AVIOCTRL_VIDEO_FILE) {//FIXME Video
                final int pos = 12;
                final int size = AVIOCTRLDEFs.SAvFile.getTotalSize();
                Log.d(TAG, "Show File total size : " + size);
                result.clear();
                for (int i = 0; i < cnt; i++) {
                    byte[] t = new byte[8];
                    System.arraycopy(data, i * size + pos, t, 0, 8);
                    AVIOCTRLDEFs.STimeDay time = new AVIOCTRLDEFs.STimeDay(t);
                    final byte event = data[i * size + pos + 8];
                    final byte status = data[i * size + pos + 9];
//                    type = data[i * size + pos + 10];
                    final byte length = data[i * size + pos + 11];
                    String path = new String(data, i * size + pos + 12, length).replace("\\", "/");
                    boolean hasFile = false;
                    String[] filter = path.split("\\\\");
                    int path_end = filter.length;
                    Log.d(TAG, "add  Video list path : " + path);
//                     \DCIM\MOVIE\2016_0118_121808_464.MOV
//                     \DCIM\MOVIE\2016_0118_120308_459.MOV
//                     \DCIM\MOVIE\2016_0118_120608_460.MOV
//                     \DCIM\MOVIE\2016_0118_120908_461.MOV
//                     \DCIM\MOVIE\2016_0118_121208_462.MOV
//                     \DCIM\MOVIE\2016_0118_121508_463.MOV
//                     \ACTION\MOVIE\2015_0101_114948_481.MOV
//                     \ACTION\MOVIE\2015_0101_115308_483.MOV
//                     \ACTION\MOVIE\2015_0101_113606_476.MOV
//                     \ACTION\MOVIE\2015_0101_113906_477.MOV
                    result.add(new EventInfo((int) event, time, (int) status, path));
                }
            }
            if (mResponseListener != null) {
                mResponseListener.dispatchResponse(result);
            }
            mAgent.unregisterIOTCListener(this);
        }
    }
}


