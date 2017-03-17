package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.photo.SetPhotoResolutionCommand;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dutskin.IPhoto;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekPhoto extends HaotekModule implements IPhoto {
    private static final String TAG = HaotekPhoto.class.getSimpleName();
    private static int sViewType = -1;
    private int mResolution;
    private boolean mDateStamp;
    private int mTimeLapse;
    private int mColorStyle;
    private int mWhiteBalance;
    private int mEV;

    public HaotekPhoto(Device device) {
        super(device);
        mViewBuilder = new IPhoto.ViewBuilder(this);
    }

    public static void initializeClass() {
        synchronized (Module.sSubClassInitializationLock) {
            sViewType = Module.sSimpleViewTypeCount++;
        }
    }

    @Override
    public Future<?> fetchSettings() {
        return executeNetworkTask(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchCurrentValueBlocking();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void fetchCurrentValueBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueBlocking");
//        GetPhotoStateCommand cmd = new GetPhotoStateCommand(this.getDevice(), HaotekCommand.GET_Info);
//        final GetPhotoStateCommand.Response response = (GetPhotoStateCommand.Response) cmd.run();
//        Log.d(TAG, "response.mList.size  : " + response.mList.size());
//        for (ModuleState state : response.mList) {
//            int moduleint = Integer.valueOf(state.mModuleName);
//            Log.d(TAG, "moduleint " + moduleint);
//            Log.d(TAG, "moduleint  value " + state.mState);
//            switch (moduleint) {
//
////                case WiFiCommandDefine.MOVIE_REC_SIZE:
////                    setVideResolution(Integer.valueOf(state.mState));
////                    break;
////                case WiFiCommandDefine.MOVIE_CYCLIC_REC:
////                    setLoopRecording(Integer.valueOf(state.mState));
////                    break;
////                case WiFiCommandDefine.MOVIE_HDR:
////                    setWDR(state.mState.equals("1"));
////                    break;
////                case WiFiCommandDefine.MOVIE_EV:
////                    setEV(Integer.valueOf(state.mState));
////                    break;
////                case WiFiCommandDefine.MOTION_DET:
////                    setMotionDetect(state.mState.equals("1"));
////                    break;
////                case WiFiCommandDefine.GET_TIME_LAPSE:
////                    setTimeLapse(Integer.valueOf(state.mState));
////                    break;
////                case WiFiCommandDefine.SET_POWER_OFF_TIME:
////                    setSelfTimer(Integer.valueOf(state.mState));
////                    break;
//            }
//        }

//        GetVideoTimeLapseCommand cmdti = new GetVideoTimeLapseCommand(this.getDevice(), HaotekCommand.GET_Info);
//        final GetVideoTimeLapseCommand.Response responseti = (GetVideoTimeLapseCommand.Response) cmdti.run();
//        Log.d(TAG, "response.mList.size  : " + responseti.mList.size());
    }

    @Override
    protected Future<?> fetchModuleSchedule() {
        return null;
    }

    @Override
    protected Future<?> pushModuleSchedule() {
        return null;
    }

    @Override
    public int getSimpleViewType() {
        return sViewType;
    }

    @Override
    public String getModuleDescription() {
        return null;
    }

    @Override
    public int getImageResolution() {
        return mResolution;
    }

    @Override
    public void setImageResolution(int resolution) {
        mResolution = resolution;
        getDevice().executeNetworkTask(new Runnable() {
            //            mNetworkTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SetPhotoResolutionCommand cmd = new SetPhotoResolutionCommand(getDevice(), HaotekCommand.GET_Info, getEV());
                    final SetPhotoResolutionCommand.Response response = (SetPhotoResolutionCommand.Response) cmd.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
        });
    }

    @Override
    public boolean getDateStamp() {
        return false;
    }

    @Override
    public void setDateStamp(boolean stamp) {
    }

    @Override
    public int getTimeLapse() {
        return mTimeLapse;
    }

    @Override
    public void setTimeLapse(int timelapse) {
        mTimeLapse = timelapse;
    }

    @Override
    public int getEV() {
        return mEV;
    }

    @Override
    public void setEV(int ev) {
        mEV = ev;
    }

    @Override
    public int getColorStyle() {
        return mColorStyle;
    }

    @Override
    public void setColorStyle(int colorstyle) {
        mColorStyle = colorstyle;
    }

    @Override
    public int getWhiteBalance() {
        return 0;
    }

    @Override
    public void setWhiteBalance(int whitebalance) {
    }

    @Override
    public int getCustomizationWhiteBalanceW() {
        return 0;
    }

    @Override
    public void setCustomizationWhiteBalanceW(int cw) {
    }

    @Override
    public int getCustomizationWhiteBalanceR() {
        return 0;
    }

    @Override
    public void setCustomizationWhiteBalanceR(int cwr) {
    }

    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    @Override
    public void pushSettingsBlocking(OnSettingsPushedListener listener) {//FIXME
        try {
            listener.dispatchPushResult(this, null);
        } catch (Exception ex) {
            listener.dispatchPushResult(this, ex);
        }
    }
}
