package tw.haotek.command.HaotekW.module.video;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2015/12/9.
 */
public class CheckVideoRecStateCommand extends HaotekCommand {
    private static final String TAG = CheckVideoRecStateCommand.class.getSimpleName();

    public CheckVideoRecStateCommand(HaotekDevice device, int CommandType) {
        super(device, CommandType);
    }

    @Override
    protected String getAction() {
//        return "?custom=1&cmd=3014";
        return "?custom=1&cmd=" + WiFiCommandDefine.WIFIAPP_CMD_GET_REC_STATUS;
    }

    public static class Response extends HaotekCommand.Response {
        public String mValue;
    }

    protected Response dispatchResponse(String soap) {
        super.dispatchResponse(soap);
        Response response = new Response();
        Log.d(TAG, "Soap : " + soap);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(soap));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    Log.d(TAG, " tagName : " + tagName);
                    switch (tagName) {
                        case "Value":
                            final String value = xpp.nextText();
//                            Log.d(TAG,"nextText()  "+xpp.nextText());
                            response.mValue = value;
                            break;
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
