package tw.haotek.command.HaotekW.device;

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
import tw.haotek.dut.data.ModuleState;

/**
 * Created by Neo on 2015/12/9.
 */
public class SetDeviceTimeCommand extends HaotekCommand {
    private static final String TAG = SetDeviceTimeCommand.class.getSimpleName();
    private String mTime;

    public SetDeviceTimeCommand(HaotekDevice device, int CommandType, String time) {
        super(device, CommandType);
        mTime = time;
    }

    @Override
    protected String getAction() {
//        return "?custom=1&cmd=3006&str=21:09:33";// 0:photo mode 1:movie mode 2:playback mode
        return "?custom=1&cmd=" + WiFiCommandDefine.SET_TIME + "&str=" + mTime;
    }

    public static class Response extends HaotekCommand.Response {
        public ArrayList<ModuleState> mList = new ArrayList<>();
    }

    protected Response dispatchResponse(String soap) {
        super.dispatchResponse(soap);
        Response response = new Response();
        Log.d(TAG, "Soap : " + soap);
        ArrayList<ModuleState> list = new ArrayList<>();
        list.clear();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(soap));
            int eventType = xpp.getEventType();
            ModuleState info = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    Log.d(TAG, " tagName : " + tagName);
                    switch (tagName) {
                        case "Cmd":
                            info = new ModuleState();
                            info.mModuleName = xpp.nextText();
                            break;
                        case "Status":
                            info.mState = xpp.nextText();
                            list.add(info);
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

        response.mList = list;
        return response;
    }
}
