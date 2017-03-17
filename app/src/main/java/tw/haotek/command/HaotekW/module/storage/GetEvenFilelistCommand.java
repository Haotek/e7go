package tw.haotek.command.HaotekW.module.storage;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import tw.haotek.command.HaotekW.HaotekCommand;
import tw.haotek.command.WiFiCommandDefine;
import tw.haotek.dut.HaotekDevice;
import tw.haotek.dut.data.EventFilelist;

/**
 * Created by Neo on 2015/12/9.
 */
public class GetEvenFilelistCommand extends HaotekCommand {
    private static final String TAG = GetEvenFilelistCommand.class.getSimpleName();

    public GetEvenFilelistCommand(HaotekDevice device, int CommandType) {
        super(device, CommandType);
    }

    @Override
    protected String getAction() {
//        return "?custom=1&cmd=9000";
        return "?custom=1&cmd=" + WiFiCommandDefine.WIFIAPP_CMD_EVENT_LIST;
    }

    public static class Response extends HaotekCommand.Response {
        public ArrayList<EventFilelist> mList = new ArrayList<>();
    }

    protected Response dispatchResponse(String soap) {
        super.dispatchResponse(soap);
        Response response = new Response();
        Log.d(TAG, "Soap : " + soap);
        ArrayList<EventFilelist> list = new ArrayList<>();
        list.clear();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(soap));
            int eventType = xpp.getEventType();
            EventFilelist info = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    Log.d(TAG, " tagName : " + tagName);
                    switch (tagName) {
                        case "TIME":
                            info = new EventFilelist();
                            info.mTime = xpp.nextText();
                            break;
                        case "TYPE":
                            info.mType = xpp.nextText();
                            break;
                        case "SEC":
                            info.mSEC = xpp.nextText();
                            break;
                        case "PREPATH":
                            info.mPREPATH = xpp.nextText();
                            break;
                        case "FPATH":
                            info.mPath = xpp.nextText().replace("A:\\", "").replace("\\", "/");
                            list.add(info);
                            break;

//                        case "ATTR":
//                            info.mAttr = xpp.nextText();
//                            list.add(info);
//                            break;
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Show list size : " + list.size());
        response.mList = list;
        return response;
    }
}
