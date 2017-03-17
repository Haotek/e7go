package tw.haotek.dut.module;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.HaotekW.module.battery.GetBatteryLevelCommand;
import tw.haotek.command.Tutk.TutkCommand;
import tw.haotek.command.Tutk.battery.GetBatteryLevelTCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekModule;
import tw.haotek.dut.data.ModuleState;
import tw.haotek.dutskin.IBattery;

/**
 * Created by Neo on 2015/11/30.
 */
public final class HaotekBattery extends HaotekModule implements IBattery {
    private static final String TAG = HaotekBattery.class.getSimpleName();
    private static int sViewType = -1;
    private int mState;

    public HaotekBattery(Device device) {
        super(device);
        mViewBuilder = new IBattery.ViewBuilder(this);
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
        GetBatteryLevelCommand cmd = new GetBatteryLevelCommand(this.getDevice(), HaotekCommand.GET_Info);
        final GetBatteryLevelCommand.Response response = (GetBatteryLevelCommand.Response) cmd.run();
        Log.d(TAG, "response.mList.size  : " + response.mList.size());
        for (ModuleState state : response.mList) {
            int moduleint = Integer.valueOf(state.mModuleName);
            switch (moduleint) {
                case WiFiCommandDefine.GET_BATTERY_LEVEL:
                    setState(Integer.valueOf(state.mState));
                    break;
            }
        }
    }

    private void fetchCurrentValueTBlocking() throws IOException {
        Log.d(TAG, "fetchCurrentValueTBlocking");
        GetBatteryLevelTCommand cmdt = new GetBatteryLevelTCommand(this.getDevice());
        cmdt.setResponseListener(new TutkCommand.ResponseListener() {
            @Override
            public void dispatchResponse(Object result) {
                Log.d(TAG, "Show  Response : " + result);
                final String[] statearray = (String[]) result;
                for (String state : statearray) {
                    final int moduleint = Integer.valueOf(state.split(",")[0]);
                    switch (moduleint) {
                        case WiFiCommandDefine.GET_BATTERY_LEVEL:
                            final String sstate = state.split(",")[2];
                            setState(Integer.valueOf(sstate));
                            Log.d(TAG, "setState()   " + sstate);
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
}
