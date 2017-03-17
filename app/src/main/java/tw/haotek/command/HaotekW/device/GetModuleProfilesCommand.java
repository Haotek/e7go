package tw.haotek.command.HaotekW.device;

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
public class GetModuleProfilesCommand extends HaotekCommand {
    private static final String TAG = GetModuleProfilesCommand.class.getSimpleName();

    public GetModuleProfilesCommand(HaotekDevice device, int CommandType) {
        super(device, CommandType);
    }

    @Override
    protected String getAction() {
//        return "?custom=1&cmd=3002";
//        return "?custom=1&cmd=" + WiFiCommandDefine.SUPPORT_QUERY;
        return "?custom=1&cmd=" + WiFiCommandDefine.GET_CURRENT_STATE;
    }

    public static class Response extends HaotekCommand.Response {
        public ArrayList<ModuleState> mList = new ArrayList<>();
    }

    protected Response dispatchResponse(String soap) {
        super.dispatchResponse(soap);
        Response response = new Response();
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
                    switch (tagName) {
                        case "Cmd":
                            info = new ModuleState();
                            info.mModuleName = xpp.nextText();
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
