package tw.haotek.command.HaotekT;

import java.io.IOException;

/**
 * Created by Neo on 2016/1/30 0030.
 */
public class HaotekTException extends IOException {
    private static final String TAG = HaotekTException.class.getSimpleName();
    //    SoapObject mSoapObject = null;
    HaotekTCommand.Response mResponse = null;

    public HaotekTException() {
        super();
    }

    public HaotekTException(String message) {
        super(message);
    }

    public HaotekTException(String message, Throwable cause) {
        super(message, cause);
    }

    public HaotekTException(Throwable cause) {
        super(cause);
    }

//    public HaotekTException(String message, SoapObject soap) {
//        this(message, soap, null, null);
//    }
//
//    public HaotekTException(String message, SoapObject soap, Throwable cause) {
//        this(message, soap, null, cause);
//    }

//    public HaotekTException(String message, SoapObject soap, HaotekTCommand.Response response) {
//        this(message, soap, response, null);
//    }
//
//    public HaotekTException(String message, SoapObject soap, HaotekTCommand.Response response, Throwable cause) {
//        super(message, cause);
//        mSoapObject = soap;
//        mResponse = response;
//    }

//    public SoapObject getSoapObject() {
//        return mSoapObject;
//    }

    public HaotekTCommand.Response getResponse() {
        return mResponse;
    }
}
