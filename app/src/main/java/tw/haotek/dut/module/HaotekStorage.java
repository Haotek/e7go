package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.storage.FormatFlashCommand;
import tw.haotek.command.HaotekW.module.storage.GetCardStatusCommand;
import tw.haotek.command.HaotekW.module.storage.GetDiskFreeSpaceCommand;
import tw.haotek.command.HaotekW.module.storage.GetEvenFilelistCommand;
import tw.haotek.command.HaotekW.module.storage.GetFilelistCommand;
import tw.haotek.command.HaotekW.module.video.SetVideoRecordingCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.stroage.FormatFlashTCommand;
import tw.haotek.command.Tutk.stroage.GetCardStatusTCommand;
import tw.haotek.command.Tutk.stroage.GetVideoFileListTCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dut.data.EventFilelist;
import tw.haotek.dut.data.EventInfo;
import tw.haotek.dut.data.Filelist;
import tw.haotek.dut.data.ModuleState;
import tw.haotek.dutskin.IStorage;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekStorage extends HaotekModule implements IStorage {
    private static final String TAG = HaotekStorage.class.getSimpleName();
    private static int sViewType = -1;
    private int mState;
    private int mSpace;
    public ArrayList<Filelist> mFileList = new ArrayList<>(); //FIXME
    public ArrayList<EventFilelist> mEvenFileList = new ArrayList<>(); //FIXME

    public HaotekStorage(Device device) {
        super(device);
        mViewBuilder = new IStorage.ViewBuilder(this);
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
        final SetVideoRecordingCommand stop = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 0);
        final SetVideoRecordingCommand start = new SetVideoRecordingCommand(getDevice(), HaotekCommand.GET_Info, 1);
        final GetCardStatusCommand cmd = new GetCardStatusCommand(this.getDevice(), HaotekCommand.GET_Info);
        final GetCardStatusCommand.Response response = (GetCardStatusCommand.Response) cmd.run();
        for (ModuleState state : response.mList) {
            int moduleint = Integer.valueOf(state.mModuleName);
            switch (moduleint) {
                case WiFiCommandDefine.GET_CARD_STATUS:
                    setState(Integer.valueOf(state.mValue));
                    break;
            }
        }

//        final CheckVideoRecStateCommand check = new CheckVideoRecStateCommand(getDevice(), HaotekCommand.GET_Info);
//        final CheckVideoRecStateCommand.Response gresponse = (CheckVideoRecStateCommand.Response) check.run();
//        Log.d(TAG, "Show Recording ? " + gresponse.mValue);
//        if (gresponse.mValue.equals("1")) {
//            stop.run();
//        }
        GetFilelistCommand cmdfl = new GetFilelistCommand(this.getDevice(), HaotekCommand.GET_Info);//FIXME
        final GetFilelistCommand.Response responsefl = (GetFilelistCommand.Response) cmdfl.run();
        mFileList = responsefl.mList;
        Log.d(TAG, "File List  count : " + mFileList.size());

        GetEvenFilelistCommand cmdefl = new GetEvenFilelistCommand(this.getDevice(), HaotekCommand.GET_Info);//FIXME
        final GetEvenFilelistCommand.Response responseefl = (GetEvenFilelistCommand.Response) cmdefl.run();
        mEvenFileList = responseefl.mList;
        Log.d(TAG, "Even File List  count : " + mEvenFileList.size());

        if (getState() > 0) {
            GetDiskFreeSpaceCommand cmdfs = new GetDiskFreeSpaceCommand(this.getDevice(), HaotekCommand.GET_Info);
            final GetDiskFreeSpaceCommand.Response responsefs = (GetDiskFreeSpaceCommand.Response) cmdfs.run();
            for (ModuleState state : responsefs.mList) {
                int moduleint = Integer.valueOf(state.mModuleName);
                switch (moduleint) {
                    case WiFiCommandDefine.GET_DISK_FREE_SPACE:
                        setVolume(Integer.valueOf(state.mValue));
                        break;
                }
            }
        }
//        if (gresponse.mValue.equals("1")) {
//            start.run();
//        }
    }

    private void fetchCurrentValueTBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueTBlocking");
        GetCardStatusTCommand cmdt = new GetCardStatusTCommand(this.getDevice());
        cmdt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  GetCardStatusTCommand Response : " + result);
                final String[] statearray = (String[]) result;
                for (String state : statearray) {
                    final int moduleint = Integer.valueOf(state.split(",")[0]);
                    switch (moduleint) {
                        case WiFiCommandDefine.GET_CARD_STATUS:
                            final String sstate = state.split(",")[2];
                            setState(Integer.valueOf(sstate));
                            Log.d(TAG, "setState () : " + sstate);
                            break;
                    }
                }
            }
        });
        cmdt.run();

        GetVideoFileListTCommand cmdtf = new GetVideoFileListTCommand(this.getDevice());
        cmdtf.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  GetVideoFileListTCommand Response : " + result);
                final List<EventInfo> rawlist = (List<EventInfo>) result;
                Log.d(TAG, "Show File size : " + rawlist.size());
                mFileList.clear();
                for (EventInfo info : rawlist) {
                    Filelist list = new Filelist();
                    list.mPath = info.mPath;
                    final String[] tran = info.mPath.replace("\\", "").split("MOVIE");//FIXME    \DCIM\MOVIE\2015_0101_120445_465.MOV get name
                    list.mName = tran[1];

                    setFileList(list);
                }
            }
        });
        cmdtf.run();

//        GetEvenFilelistTCommand cmdte = new GetEvenFilelistTCommand(this.getDevice());
//        cmdte.setResponseListener(new TutkCommand.ResponseListener() {
//            @Override
//            public void dispatchResponse(Object result) {
//                Log.d(TAG, "Show  GetEvenFilelistTCommand Response : " + result);
////                final String[] statearray = (String[]) result;
////                for (String state : statearray) {
////                    final int moduleint = Integer.valueOf(state.split(",")[0]);
////                    final String sstate = state.split(",")[1];
////                    switch (moduleint) {
////                        case WiFiCommandDefine.MOVIE_AUDIO:
////                            setRecordingAudio(sstate.equals("1"));
////                            break;
////                    }
////                }
//            }
//        });
//        cmdte.run();
//
//        GetDiskFreeSpaceTCommand cmdtd = new GetDiskFreeSpaceTCommand(this.getDevice());
//        cmdtd.setResponseListener(new TutkCommand.ResponseListener() {
//            @Override
//            public void dispatchResponse(Object result) {
//                Log.d(TAG, "Show  GetDiskFreeSpaceTCommand Response : " + result);
////                final String[] statearray = (String[]) result;
////                for (String state : statearray) {
////                    final int moduleint = Integer.valueOf(state.split(",")[0]);
////                    final String sstate = state.split(",")[1];
////                    switch (moduleint) {
////                        case WiFiCommandDefine.MOVIE_AUDIO:
////                            setRecordingAudio(sstate.equals("1"));
////                            break;
////                    }
////                }
//            }
//        });
//        cmdtd.run();
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
    public int getVolume() {
        return mSpace;
    }

    @Override
    public void setVolume(int volume) {
        mSpace = volume;
    }

    @Override
    public int getTotalVolume() {
        return 0;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public void setState(int state) {
        mState = state;
    }

    public ArrayList<Filelist> getFileList() {
        return mFileList;
    }


    public void setFileList(Filelist list) {
        mFileList.add(list);
        dispatchChange(false, "filelist");
    }

    @Override
    public void setFormat(boolean format) {
        getDevice().executeNetworkTask(new Runnable() {
            //            mNetworkTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final FormatFlashCommand check = new FormatFlashCommand(getDevice(), HaotekCommand.GET_Info, 1);
                    final FormatFlashCommand.Response gresponse = (FormatFlashCommand.Response) check.run();

                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }

                //FIXME  Tutk
                try {
                    FormatFlashTCommand cmdf = new FormatFlashTCommand(getDevice(), 1);
                    cmdf.setResponseListener(new TutkCommand.ResponseListener() {
                        @Override
                        public void dispatchResponse(Object result) {

                        }
                    });
                    cmdf.run();
                } catch (Exception ex) {
                    Log.d(TAG, "Exception" + ex);
                }
            }
        });


    }

    @Override
    public void pushSettingsBlocking(OnSettingsPushedListener listener) {
//        try {
//            GetFilelistCommand cmdfl = new GetFilelistCommand(this.getDevice(), HaotekCommand.GET_Info);//FIXME
//            final GetFilelistCommand.Response responsefl = (GetFilelistCommand.Response) cmdfl.run();
//            mFileList = responsefl.mList;
//            Log.d(TAG, "File List  count : " + mFileList.size());
//            listener.dispatchPushResult(this, null);
//        } catch (Exception ex) {
//            listener.dispatchPushResult(this, ex);
//        }
    }
}
