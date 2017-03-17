package tw.haotek.command.HaotekW;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;

/**
 * Created by Neo on 2015/12/9.
 */
public abstract class HaotekModuleCommand extends HaotekCommand {
    private static final String TAG = HaotekModuleCommand.class.getSimpleName();
    protected Module mModule;

    public Module getModule() {
        return mModule;
    }

    private HaotekModuleCommand(Device device, int CommandType) {
        super(device, CommandType);
    }

    public HaotekModuleCommand(Device device, int CommandType, Module module) {
        this(device, CommandType);
        mModule = module;
    }
}
