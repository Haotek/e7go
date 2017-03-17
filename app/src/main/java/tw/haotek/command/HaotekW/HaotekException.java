package tw.haotek.command.HaotekW;

import android.util.ArrayMap;

import java.io.IOException;

/**
 * Created by Neo on 2015/12/8.
 */
public class HaotekException extends IOException {
    private static final String TAG = HaotekException.class.getSimpleName();
    ArrayMap<String, String> mKvp = null;
    HaotekCommand.Response mResponse = null;

    public HaotekException() {
        super();
    }

    public ArrayMap<String, String> getArrayMap() {
        return mKvp;
    }

    public HaotekCommand.Response getResponse() {
        return mResponse;
    }

    public HaotekException(String message) {
        super(message);
    }

    public HaotekException(String message, Throwable cause) {
        super(message, cause);
    }

    public HaotekException(Throwable cause) {
        super(cause);
    }

    //    public NIPCAException(String message, SoapObject soap, NIPCACommand.Response response, Throwable cause) {
//        super(message, cause);
    public HaotekException(String message, ArrayMap<String, String> kvp, HaotekCommand.Response response, Throwable cause) {
        super(message, cause);
        mKvp = kvp;
        mResponse = response;
    }

    public HaotekException(String message, ArrayMap<String, String> kvp) {
        this(message, kvp, null, null);
    }

    public HaotekException(String message, ArrayMap<String, String> kvp, Throwable cause) {
        this(message, kvp, null, cause);
    }

    public HaotekException(String message, ArrayMap<String, String> kvp, HaotekCommand.Response response) {
        this(message, kvp, response, null);
    }
}
