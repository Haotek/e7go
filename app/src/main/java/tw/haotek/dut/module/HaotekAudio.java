package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.audio.GetAudioRecodingStateCommand;
import tw.haotek.command.HaotekW.module.audio.SetAudioRecordingStateCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.audio.GetAudioRecodingStateTCommand;
import tw.haotek.command.Tutk.audio.SetAudioRecodingStateTCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dut.data.ModuleState;
import tw.haotek.dutskin.IAudio;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekAudio extends HaotekModule implements IAudio {
    private static final String TAG = HaotekAudio.class.getSimpleName();
    private static int sViewType = -1;
    private boolean mRecordingAudio;

    public HaotekAudio(Device device) {
        super(device);
        mViewBuilder = new IAudio.ViewBuilder(this);
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
        GetAudioRecodingStateCommand cmd = new GetAudioRecodingStateCommand(this.getDevice(), HaotekCommand.GET_Info);
        final GetAudioRecodingStateCommand.Response response = (GetAudioRecodingStateCommand.Response) cmd.run();
        Log.d(TAG, "response.mList.size  : " + response.mList.size());
        for (ModuleState state : response.mList) {
            int moduleint = Integer.valueOf(state.mModuleName);
            switch (moduleint) {
                case WiFiCommandDefine.MOVIE_AUDIO:
                    setRecordingAudio(state.mState.equals("1"));
                    break;
            }
        }
    }

    private void fetchCurrentValueTBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueTBlocking");
        GetAudioRecodingStateTCommand cmdt = new GetAudioRecodingStateTCommand(this.getDevice());
        cmdt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  Response : " + result);
                final String[] statearray = (String[]) result;
                for (String state : statearray) {
                    final int moduleint = Integer.valueOf(state.split(",")[0]);
                    final String sstate = state.split(",")[1];
                    switch (moduleint) {
                        case WiFiCommandDefine.MOVIE_AUDIO:
                            setRecordingAudio(sstate.equals("1"));
                            break;
                    }
                }
            }
        });
        cmdt.run();
    }

    private int boolToInt(boolean b) {
        return b ? 1 : 0;
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
    public boolean getRecordingAudio() {
        return mRecordingAudio;
    }

    @Override
    public void setRecordingAudio(boolean state) {
        mRecordingAudio = state;
    }

    @Override
    public void pushSettingsBlocking(OnSettingsPushedListener listener) {
        try {
            SetAudioRecordingStateCommand au = new SetAudioRecordingStateCommand(getDevice(), HaotekCommand.GET_Info, boolToInt(getRecordingAudio()));
            final SetAudioRecordingStateCommand.Response responseau = (SetAudioRecordingStateCommand.Response) au.run();
            listener.dispatchPushResult(this, null);
        } catch (Exception ex) {
            listener.dispatchPushResult(this, ex);
        }

        try {
            SetAudioRecodingStateTCommand set = new SetAudioRecodingStateTCommand(getDevice(), boolToInt(getRecordingAudio()));
            set.setResponseListener(new TutkCommand.ResponseListener() {
                @Override
                public void dispatchResponse(Object result) {
                    Log.d(TAG, "dispatchResponse Show  Response : " + result);
                }
            });
            set.run();
        } catch (Exception ex) {
            listener.dispatchPushResult(this, ex);
        }
    }
}
