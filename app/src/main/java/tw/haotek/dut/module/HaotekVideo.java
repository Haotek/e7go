package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.video.CheckVideoRecStateCommand;
import tw.haotek.command.HaotekW.module.video.GetVideoStateCommand;
import tw.haotek.command.HaotekW.module.video.SetCyclicRecordingCommand;
import tw.haotek.command.HaotekW.module.video.SetHDRWDRCommand;
import tw.haotek.command.HaotekW.module.video.SetMotionDetectCommand;
import tw.haotek.command.HaotekW.module.video.SetTimeLapseCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoEVCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoRecordingCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoResolutionCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.video.CheckVideoRecStateTCommand;
import tw.haotek.command.Tutk.video.GetVideoStateTCommand;
import tw.haotek.command.Tutk.video.SetCyclicRecordingTCommand;
import tw.haotek.command.Tutk.video.SetHDRWDRTCommand;
import tw.haotek.command.Tutk.video.SetMotionDetectTCommand;
import tw.haotek.command.Tutk.video.SetVideoEVTCommand;
import tw.haotek.command.Tutk.video.SetVideoFequencyTCommand;
import tw.haotek.command.Tutk.video.SetVideoRecordingTCommand;
import tw.haotek.command.Tutk.video.SetVideoResolutionTCommand;
import tw.haotek.command.Tutk.video.SetVideoTimeLapseTCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dut.data.ModuleState;
import tw.haotek.dutskin.IVideo;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekVideo extends HaotekModule implements IVideo {
    private static final String TAG = HaotekVideo.class.getSimpleName();
    private static int sViewType = -1;
    private boolean isRecording;
    private int mResolution;
    private int mLoop;
    private boolean mDateStamp;
    private int mTimeLapse;
    private int mSelfTimer;
    private int mFOV;
    private boolean mWDR;
    private int mWhiteBalance;
    private int mEV;
    private boolean mMotion;
    private int mFequency;

    public HaotekVideo(Device device) {
        super(device);
        mViewBuilder = new IVideo.ViewBuilder(this);
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
                try {
                    fetchCurrentValueTBlocking();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void fetchCurrentValueBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueBlocking");
        GetVideoStateCommand cmd = new GetVideoStateCommand(this.getDevice(), HaotekCommand.GET_Info);
        final GetVideoStateCommand.Response response = (GetVideoStateCommand.Response) cmd.run();
        Log.d(TAG, "response.mList.size  : " + response.mList.size());
        for (ModuleState state : response.mList) {
            int moduleint = Integer.valueOf(state.mModuleName);
            switch (moduleint) {
                case WiFiCommandDefine.MOVIE_REC_SIZE:
                    setVideoResolution(Integer.valueOf(state.mState), true);
                    break;
                case WiFiCommandDefine.MOVIE_CYCLIC_REC:
                    setLoopRecording(Integer.valueOf(state.mState), true);
                    break;
                case WiFiCommandDefine.MOVIE_HDR:
                    setWDR(state.mState.equals("1"), true);
                    break;
                case WiFiCommandDefine.MOVIE_EV:
                    setEV(Integer.valueOf(state.mState), true);
                    break;
//                case WiFiCommandDefine.MOTION_DET:
//                    setMotionDetect(state.mState.equals("1"));
//                    break;
//                case WiFiCommandDefine.GET_TIME_LAPSE:
//                    setTimeLapse(Integer.valueOf(state.mState));
//                    break;
//                case WiFiCommandDefine.SET_POWER_OFF_TIME:
//                    setSelfTimer(Integer.valueOf(state.mState));
//                    break;
            }
        }

//        GetVideoTimeLapseCommand cmdti = new GetVideoTimeLapseCommand(this.getDevice(), HaotekCommand.GET_Info);
//        final GetVideoTimeLapseCommand.Response responseti = (GetVideoTimeLapseCommand.Response) cmdti.run();
//        Log.d(TAG, "response.mList.size  : " + responseti.mList.size());
//        for (ModuleState state : responseti.mList) {
//            int moduleint = Integer.valueOf(state.mModuleName);
//            switch (moduleint) {
//                case WiFiCommandDefine.GET_TIME_LAPSE:
//                    setTimeLapse(Integer.valueOf(state.mState));
//                    break;
//            }
//        }
    }

    private void fetchCurrentValueTBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueTBlocking");
        GetVideoStateTCommand cmdt = new GetVideoStateTCommand(this.getDevice());
        cmdt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  Response : " + result);
                final String[] statearray = (String[]) result;
                for (String state : statearray) {
                    final int moduleint = Integer.valueOf(state.split(",")[0]);
                    final String sstate = state.split(",")[1];
                    switch (moduleint) {
                        case WiFiCommandDefine.MOVIE_REC_SIZE:
                            setVideoResolution(Integer.valueOf(sstate), true);
                            break;
                        case WiFiCommandDefine.MOVIE_CYCLIC_REC:
                            setLoopRecording(Integer.valueOf(sstate), true);
                            break;
                        case WiFiCommandDefine.MOVIE_HDR:
                            setWDR(sstate.equals("1"), true);
                            break;
                        case WiFiCommandDefine.MOVIE_EV:
                            setEV(Integer.valueOf(sstate), true);
                            break;
                        case WiFiCommandDefine.MOTION_DET:
                            setMotionDetect(sstate.equals("1"), true);
                            break;
                        case WiFiCommandDefine.WIFIAPP_CMD_SET_FREQUENCY:
                            setVideoFequency(Integer.valueOf(sstate), true);
                            break;
//                case WiFiCommandDefine.GET_TIME_LAPSE:
//                    setTimeLapse(Integer.valueOf(state.mState));
//                    break;
//                        case WiFiCommandDefine.SET_POWER_OFF_TIME:
//                            setSelfTimer(Integer.valueOf(sstate));
//                            break;
                    }
                }
            }
        });
        cmdt.run();
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
    public int getVideoResolution() {
        return mResolution;
    }

    @Override
    public void setVideoResolution(int resolution, boolean isDefault) {
        Log.d(TAG, "new State : " + resolution);
        Log.d(TAG, "current  State : " + mResolution);
        Log.d(TAG, "Is Default ?  : " + isDefault);
        if (mResolution != resolution) {
            Log.d(TAG, "setVideoResolution");
            synchronized (mUpdateLock) {
                mResolution = resolution;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            Log.d(TAG, "Show Recording ? " + gresponse.mValue);
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            final SetVideoResolutionCommand cmd = new SetVideoResolutionCommand(getDevice(), HaotekCommand.GET_Info, getVideoResolution());
                            final SetVideoResolutionCommand.Response response = (SetVideoResolutionCommand.Response) cmd.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }

                        //FIXME  Tutk
                        final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                        stopt.setResponseListener(new TutkCommand.ResponseListener() {
                            @Override
                            public void dispatchResponse(Object result) {
                            }
                        });
                        final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                        startt.setResponseListener(new TutkCommand.ResponseListener() {
                            @Override
                            public void dispatchResponse(Object result) {
                            }
                        });
                        final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                        final SetVideoResolutionTCommand set = new SetVideoResolutionTCommand(getDevice(), getVideoResolution());
                        try {
                            checkt.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                    Log.d(TAG, "dispatchResponse");
                                    final String[] rawdata = (String[]) result;
                                    final String[] data = rawdata[0].split(",");
                                    if (data[2].equals("1")) {
                                        stopt.run();
                                    }

                                    set.setResponseListener(new TutkCommand.ResponseListener() {
                                        @Override
                                        public void dispatchResponse(Object result) {
                                        }
                                    });
                                    set.run();
                                    if (data[2].equals("1")) {
                                        startt.run();
                                    }
                                }
                            });
                            checkt.run();
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });
            }

            dispatchChange(false, "resolution");
        }
    }


    @Override
    public int getLoopRecording() {
        return mLoop;
    }

    @Override
    public void setLoopRecording(int looprec, boolean isDefault) {
        Log.d(TAG, "new State : " + looprec);
        Log.d(TAG, "current  State : " + mLoop);
        if (mLoop != looprec) {
            Log.d(TAG, "setLoopRecording");
            synchronized (mUpdateLock) {
                mLoop = looprec;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            SetCyclicRecordingCommand cmdcy = new SetCyclicRecordingCommand(getDevice(), HaotekCommand.GET_Info, getLoopRecording());
                            final SetCyclicRecordingCommand.Response responsecy = (SetCyclicRecordingCommand.Response) cmdcy.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });

                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetCyclicRecordingTCommand setc = new SetCyclicRecordingTCommand(getDevice(), getLoopRecording());
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            setc.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            setc.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }

            }
            dispatchChange(false, "looprec)");
        }
    }

    @Override
    public boolean getDateStamp() {
        return false;
    }

    @Override
    public void setDateStamp(boolean stamp, boolean isDefault) {

    }

    @Override
    public int getTimeLapse() {
        return mTimeLapse;
    }

    @Override
    public void setTimeLapse(int timelapse, boolean isDefault) {

        Log.d(TAG, "new State : " + timelapse);
        Log.d(TAG, "current  State : " + mTimeLapse);
        if (mTimeLapse != timelapse) {
            Log.d(TAG, "setTimeLapse");
            synchronized (mUpdateLock) {
                mTimeLapse = timelapse;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            SetTimeLapseCommand cmdwdr = new SetTimeLapseCommand(getDevice(), HaotekCommand.GET_Info, getTimeLapse());
                            final SetTimeLapseCommand.Response responsewdr = (SetTimeLapseCommand.Response) cmdwdr.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });

                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetVideoTimeLapseTCommand sett = new SetVideoTimeLapseTCommand(getDevice(), getTimeLapse());
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            sett.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            sett.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
            dispatchChange(false, "timelapse");
        }
    }

    @Override
    public int getSelfTimer() {
        return mSelfTimer;
    }

    @Override
    public void setSelfTimer(int self, boolean isDefault) {
        mSelfTimer = self;
    }

    @Override
    public int getFOV() {
        return 0;
    }

    @Override
    public void setFOV(int fov, boolean isDefault) {

    }

    @Override
    public boolean getWDR() {
        return mWDR;
    }

    @Override
    public void setWDR(boolean wdr, boolean isDefault) {
        Log.d(TAG, "new State : " + wdr);
        Log.d(TAG, "current  State : " + mWDR);
        if (mWDR != wdr) {
            Log.d(TAG, "setWDR");
            synchronized (mUpdateLock) {
                mWDR = wdr;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            SetHDRWDRCommand cmdwdr = new SetHDRWDRCommand(getDevice(), HaotekCommand.GET_Info, boolToInt(getWDR()));
                            final SetHDRWDRCommand.Response responsewdr = (SetHDRWDRCommand.Response) cmdwdr.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });


                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetHDRWDRTCommand setw = new SetHDRWDRTCommand(getDevice(), boolToInt(getWDR()));
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            setw.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            setw.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
            dispatchChange(false, "ev");
        }
    }

    @Override
    public int getEV() {
        return mEV;
    }

    @Override
    public void setEV(int ev, boolean isDefault) {
        Log.d(TAG, "new State : " + ev);
        Log.d(TAG, "current  State : " + mEV);
        if (mEV != ev) {
            Log.d(TAG, "setEV");
            synchronized (mUpdateLock) {
                mEV = ev;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            SetVideoEVCommand cmdev = new SetVideoEVCommand(getDevice(), HaotekCommand.GET_Info, getEV());
                            final SetVideoEVCommand.Response responseev = (SetVideoEVCommand.Response) cmdev.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });

                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetVideoEVTCommand sete = new SetVideoEVTCommand(getDevice(), getEV());
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            sete.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            sete.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
            dispatchChange(false, "ev");
        }
    }

    @Override
    public int getWhiteBalance() {
        return 0;
    }

    @Override
    public void setWhiteBalance(int whitebalance, boolean isDefault) {

    }

    @Override
    public int getCustomizationWhiteBalanceW() {
        return 0;
    }

    @Override
    public void setCustomizationWhiteBalanceW(int cw, boolean isDefault) {

    }

    @Override
    public int getCustomizationWhiteBalanceR() {
        return 0;
    }

    @Override
    public void setCustomizationWhiteBalanceR(int cwr, boolean isDefault) {

    }

    @Override
    public int getFunctionLock() {
        return 0;
    }

    @Override
    public void setFunctionLock(int lock, boolean isDefault) {

    }

    @Override
    public boolean getMotionDetect() {
        return mMotion;
    }

    @Override
    public void setMotionDetect(boolean motion, boolean isDefault) {
        Log.d(TAG, "setMotionDetect");
        if (mMotion != motion) {
            synchronized (mUpdateLock) {
                mMotion = motion;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            SetMotionDetectCommand cmdmo = new SetMotionDetectCommand(getDevice(), HaotekCommand.GET_Info, boolToInt(getMotionDetect()));
                            final SetMotionDetectCommand.Response responsemo = (SetMotionDetectCommand.Response) cmdmo.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }
                    }
                });

                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetMotionDetectTCommand setm = new SetMotionDetectTCommand(getDevice(), boolToInt(getMotionDetect()));
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            setm.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            setm.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
            dispatchChange(false, "motion)");
        }
    }

    @Override
    public int getVideoFequency() {
        return mFequency;
    }

    @Override
    public void setVideoFequency(int fequency, boolean isDefault) {
        Log.d(TAG, "new State : " + fequency);
        Log.d(TAG, "current  State : " + mFequency);
        if (mFequency != fequency) {
            Log.d(TAG, "setVideoFequency");
            synchronized (mUpdateLock) {
                mFequency = fequency;
            }
            if (!isDefault) {
                getDevice().executeNetworkTask(new Runnable() {
                    //            mNetworkTaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
                        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
                        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
                        try {
                            final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
                            if (gresponse.mValue.equals("1")) {
                                stop.run();
                            }
                            final SetVideoResolutionCommand cmd = new SetVideoResolutionCommand(getDevice(), HaotekCommand.GET_Info, getVideoFequency());
                            final SetVideoResolutionCommand.Response response = (SetVideoResolutionCommand.Response) cmd.run();
                            if (gresponse.mValue.equals("1")) {
                                start.run();
                            }
                        } catch (Exception ex) {
                            Log.d(TAG, "Exception" + ex);
                        }


                    }
                });

                //FIXME  Tutk
                final SetVideoRecordingTCommand stopt = new SetVideoRecordingTCommand(getDevice(), 0);
                stopt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final SetVideoRecordingTCommand startt = new SetVideoRecordingTCommand(getDevice(), 1);
                startt.setResponseListener(new TutkCommand.ResponseListener() {
                    @Override
                    public void dispatchResponse(Object result) {
                    }
                });
                final CheckVideoRecStateTCommand checkt = new CheckVideoRecStateTCommand(getDevice());
                final SetVideoFequencyTCommand setf = new SetVideoFequencyTCommand(getDevice(), getVideoFequency());
                try {
                    checkt.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {
                            Log.d(TAG, "dispatchResponse");
                            final String[] rawdata = (String[]) result;
                            final String[] data = rawdata[0].split(",");
                            if (data[2].equals("1")) {
                                stopt.run();
                            }

                            setf.setResponseListener(new TutkCommand.ResponseListener() {
                                @Override
                                public void dispatchResponse(Object result) {
                                }
                            });
                            setf.run();
                            if (data[2].equals("1")) {
                                startt.run();
                            }
                        }
                    });
                    checkt.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
            dispatchChange(false, "fequency");
        }
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
