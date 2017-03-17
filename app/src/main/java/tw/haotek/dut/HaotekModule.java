package tw.haotek.dut;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Future;

import c.min.tseng.dut.Device;
import c.min.tseng.dut.Module;
import tw.haotek.dut.module.HaotekAudio;
import tw.haotek.dut.module.HaotekBattery;
import tw.haotek.dut.module.HaotekGsensor;
import tw.haotek.dut.module.HaotekPhoto;
import tw.haotek.dut.module.HaotekStorage;
import tw.haotek.dut.module.HaotekVideo;

/**
 * Created by Neo on 2015/11/29.
 */
public abstract class HaotekModule extends Module {
    private static final String TAG = HaotekModule.class.getSimpleName();
    protected final Object mFunctionLock = new Object();
    private String mHardwareVersion;

    //    private ArrayList<Function> mFunctions = new ArrayList<>();
    protected HaotekModule(Device device) {
        super(device);
    }

    public static void initializeClasses() {
        HaotekVideo.initializeClass();
        HaotekPhoto.initializeClass();
        HaotekAudio.initializeClass();
        HaotekGsensor.initializeClass();
        HaotekStorage.initializeClass();
        HaotekBattery.initializeClass();
    }

    @Override
    public HaotekDevice getDevice() {
        return (HaotekDevice) super.getDevice();
    }

    public static Module fromFakedataesponse(Device device, String imodule) {
        HaotekModule module = null;
        switch (imodule) {
            case "Video"://FIXME  different  Quality different Module
                module = new HaotekVideo(device);
                break;
            case "Photo"://FIXME  different  Quality different Module
                module = new HaotekPhoto(device);
                break;
            case "Audio"://FIXME  different  Quality different Module
                module = new HaotekAudio(device);
                break;
            case "Storage":
                module = new HaotekStorage(device);
                break;
            case "GSensor":
                module = new HaotekGsensor(device);
                break;
            case "Battery":
                module = new HaotekBattery(device);
                break;
            default: // for all unknown modules.
                Log.d(TAG, " fromFakedataesponse: unknown module type \"" + imodule + "\", using UnknownTemplateModule.");
                module = null;
                break;
        }
        if (module != null) {
            module.updateFakedataResponse(imodule);
        }
        return module;
    }

    public void updateFakedataResponse(String mdoule) {
        mNickName = mdoule;
        setIsBuiltIn(true);
    }

    @Override
    protected Future<?> executeNetworkTask(Runnable task) {
//        Log.d(TAG, "@ HaotekModule executeNetworkTask");
        return mNetworkTaskExecutor.submit(task);
//        return getDevice().executeNetworkTask(task);
    }

    @Override
    public Future<?>[] fetchEverything() {
        return new Future[]{
                fetchSettings(),
//                fetchFakeSettings(),
        };
    }

    public abstract Future<?> fetchSettings();

    public Future<?> fetchFakeSettings() {
//        return getDevice().executeNetworkTaskWithLoginGuard(new ThrowingRunnable() {
//            @Override
//            public void run() throws IOException {
//                fetchSettingsBlocking();
//            }
//        });
        return executeNetworkTask(new Runnable() {
            @Override
            public void run() {
//                fetchFakeFunctionBlocking();
            }
        });
    }

//    public void fetchFakeFunctionBlocking() {
//        final ArrayList<Function> functions = new ArrayList<>();
//        synchronized (mUpdateLock) {//FIXME Test   what function ???
//            final Resources resources = HaotekApplication.getContext().getResources();
//            final String[] functionlist = resources.getStringArray(R.array.haotek_product_list);
//            for (int i = 0; i < functionlist.length; ++i) {
//                final String si = functionlist[i];
//                gentFakeFunction(true, si, functions);
//            }
//        }
//        synchronized (mFunctionLock) {
//            clearFunction(true);
//            for (Function function : functions) {
//                addFunction(function, true);
//            }
//            mFunctions.trimToSize();
//        }
//        dispatchChange(false, "basicsetting");
//        for (Function function : mFunctions) {
//            function.fetchEverything();
//        }
//    }

//    private void gentFakeFunction(boolean generate, String functionType, ArrayList<Function> functions) {
//        if (generate) {
//            final Function function = TutkFunction.fromFakedataesponse(this.getDevice(), this, functionType);
////            if (module != null) {
////                final int moduleid = modules.size();
////                module.setModuleID(moduleid);
////                modules.add(module);
////            }
//        }
//    }

//    public void addFunction(Function function, boolean quiet) {
//        boolean notify = false;
//        synchronized (mFunctionLock) {
//            if (!mFunctions.contains(function)) {
//                try {
//                    function.registerContentObserver(mObserver);
//                } catch (IllegalStateException ignored) {
//                }
//                mFunctions.add(function);
//                if (!quiet) {
//                    notify = true;
//                }
//            }
//        }
//        if (notify) {
//            dispatchChange(false, "addfunction");
//        }
//    }
//
//    public void removeModule(Function function, boolean quiet) {
//        boolean notify = false;
//        synchronized (mFunctionLock) {
//            function.unregisterContentObserver(mObserver);
//            if (mFunctions.remove(function) && !quiet) {
//                notify = true;
//            }
//        }
//        if (notify) {
//            dispatchChange(false, "removefunction");
//        }
//    }
//
//    protected void clearFunction(boolean quiet) {
//        boolean notify = false;
//        synchronized (mFunctionLock) {
//            for (Function function : mFunctions) {
//                function.unregisterContentObserver(mObserver);
//                notify = true;
//            }
//            mFunctions.clear();
//        }
//        if (notify) {
//            dispatchChange(false, "clearfunction");
//        }
//    }

//    public Future<?> fetchCurrentValue() {
//        return getDevice().executeNetworkTaskWithLoginGuard(
//                new ThrowingRunnable() {
//                    @Override
//                    public void run() throws Exception {
//                        fetchCurrentValueBlocking();
//                    }
//                });
//    }

    protected abstract void fetchCurrentValueBlocking() throws IOException;

    public final Future<?> pushSettings(final OnSettingsPushedListener listener) {
        return executeNetworkTask(new Runnable() {
            @Override
            public void run() {
                pushSettingsBlocking(listener);
            }
        });
    }

    public void pushSettingsBlocking(OnSettingsPushedListener listener) {
        Log.w(TAG, String.format("pushSettings(): Not implemented in this module: %1$s", getClass().getName()));
        listener.dispatchPushResult(
                HaotekModule.this,
                new UnsupportedOperationException(String.format("Not implemented in this module: %1$s", getClass().getName()))
        );
    }
}
