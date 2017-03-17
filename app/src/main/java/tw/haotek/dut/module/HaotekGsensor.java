package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.gsensor.GetGsensorStateCommand;
import tw.haotek.command.HaotekW.module.gsensor.SetGsensorStateCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.gsensor.GetGsensorStateTCommand;
import tw.haotek.command.Tutk.gsensor.SetGsensorStateTCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dut.data.ModuleState;
import tw.haotek.dutskin.IGsensor;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekGsensor extends HaotekModule implements IGsensor {
    private static final String TAG = HaotekGsensor.class.getSimpleName();
    private static int sViewType = -1;
    private int mState;

    public HaotekGsensor(Device device) {
        super(device);
        mViewBuilder = new IGsensor.ViewBuilder(this);
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
        GetGsensorStateCommand cmd = new GetGsensorStateCommand(this.getDevice(), HaotekCommand.GET_Info);
        final GetGsensorStateCommand.Response response = (GetGsensorStateCommand.Response) cmd.run();
        Log.d(TAG, "response.mList.size  : " + response.mList.size());
        for (ModuleState state : response.mList) {
            int moduleint = Integer.valueOf(state.mModuleName);
            switch (moduleint) {
                case WiFiCommandDefine.MOVIE_GSENSOR_SENS:
                    setState(Integer.valueOf(state.mState));
                    break;
            }
        }
    }

    private void fetchCurrentValueTBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueTBlocking");
        GetGsensorStateTCommand cmdt = new GetGsensorStateTCommand(this.getDevice());
        cmdt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  Response : " + result);
                final String[] statearray = (String[]) result;
                for (String state : statearray) {
                    final int moduleint = Integer.valueOf(state.split(",")[0]);
                    final String sstate = state.split(",")[1];
                    switch (moduleint) {
                        case WiFiCommandDefine.MOVIE_GSENSOR_SENS:
                            setState(Integer.valueOf(sstate));
                            break;
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
    public int getState() {
        return mState;
    }

    @Override
    public void setState(int state) {
        mState = state;
    }

    @Override
    public void pushSettingsBlocking(OnSettingsPushedListener listener) {
        try {
            final SetGsensorStateCommand gs = new SetGsensorStateCommand(getDevice(), HaotekCommand.GET_Info, getState());
            final SetGsensorStateCommand.Response responsegs = (SetGsensorStateCommand.Response) gs.run();
            listener.dispatchPushResult(this, null);
        } catch (Exception ex) {
            listener.dispatchPushResult(this, ex);
        }

        try {
            SetGsensorStateTCommand gs = new SetGsensorStateTCommand(getDevice(), getState());
            gs.setResponseListener(new TutkCommand.ResponseListener() {
                @Override
                public void dispatchResponse(Object result) {
                }
            });
            gs.run();
        } catch (Exception ex) {
            Log.d(TAG, "Exception" + ex);
        }
    }
}
