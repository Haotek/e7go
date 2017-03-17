package tw.haotek.command.HaotekW;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.util.List;

import c.min.tseng.dut.Device;
import tw.haotek.util.Integer;

/**
 * Created by Neo on 2015/12/6.
 */
public abstract class HaotekCommand {
    private static final String TAG = HaotekCommand.class.getSimpleName();
    public static final int GET_Info = 1;            // Get
    public static final int SET_Info = 2;            // Set
    protected int mCommandType;
    protected Device mDevice;
    private static OkHttpClient sHttp = new OkHttpClient();

    public HaotekCommand(Device device, int CommandType) {
        mDevice = device;
        mCommandType = CommandType;
    }

    public Device getDevice() {
        return mDevice;
    }

    public void setDevice(Device device) {
        mDevice = device;
    }

    protected abstract String getAction();

    protected static boolean getSoapPropertyAsBoolean(Object object) {
        String str = getSoapPropertyAsString(object);
        return str.equals("1") || str.equals("true");
    }

    protected static long getSoapPropertyAsLong(Object object) {
        return Long.parseLong(getSoapPropertyAsString(object));
    }

    protected static int getSoapPropertyAsInteger(Object object) {
        return Integer.tryParseInt(getSoapPropertyAsString(object), 0);
    }

    protected static int[] getSoapPropertyAsIntegerArray(Object object) {
        SoapObject soap = (SoapObject) object;
        int count = soap.getPropertyCount();
        int[] array = new int[count];
        for (int i = 0; i < count; ++i)
            array[i] = getSoapPropertyAsInteger(soap.getProperty(i));
        return array;
    }

    protected static String[] getSoapPropertyAsStringArray(Object object) {
        final SoapObject soap = (SoapObject) object;
        int count = soap.getPropertyCount();
        String[] array = new String[count];
        for (int i = 0; i < count; ++i)
            array[i] = getSoapPropertyAsString(soap.getProperty(i));
        return array;
    }

    private static String _toString(List<HeaderProperty> headers) {
        StringBuilder strbuf = new StringBuilder();
        strbuf.append("{");
        for (HeaderProperty header : headers)
            strbuf
                    .append(header.getKey())
                    .append(" = ")
                    .append(header.getValue())
                    .append(", ");
        strbuf.append("}");
        return strbuf.toString();
    }

    protected static String getSoapPropertyAsString(Object object) {
        String str = object.toString();
        return str.equals("anyType{}") ? "" : str;
    }

    public static class Response {
    }

    protected Response dispatchResponse(String soap) {
        return new Response();
    }

    public Response run() throws IOException {
        Log.d(TAG, "Show IP : " + mDevice.getInetAddress());//FIXME sometime  ip is null !!!!!
        Request request = null;
        if (mCommandType == GET_Info) {
            request = new Request.Builder()
                    .url("http://" + mDevice.getInetAddress() + getAction())
                    .get()
                    .build();
        } else if (mCommandType == SET_Info) {
            request = new Request.Builder()
                    .url("http://" + mDevice.getInetAddress() + getAction())
//					.post() //FIXME Post For Set
                    .build();
        }

        Call call = sHttp.newCall(request);
        com.squareup.okhttp.Response response = call.execute();
        final String address = "http://" + mDevice.getInetAddress() + getAction();
        String body = response.body().string();
        Log.d(TAG, "HTTP remote: " + address);
        Log.d(TAG, "Action = " + getAction());
        Log.d(TAG, "request = " + request);
        Log.d(TAG, "HaotekCommand Response body :  " + body);
        if (body.contains("<div")) {
            return dispatchResponse("");//FIXME  if tutk mode  wificommand not over tutk will get html
        }
        if (response.isSuccessful()) {
            body = body.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>", "");
            return dispatchResponse(body);
        }
        return dispatchResponse("fail");//FIXME
    }
}
