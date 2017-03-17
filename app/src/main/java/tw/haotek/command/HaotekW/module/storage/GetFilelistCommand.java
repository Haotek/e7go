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
import tw.haotek.dut.data.Filelist;

/**
 * Created by Neo on 2015/12/9.
 */
public class GetFilelistCommand extends HaotekCommand {
    private static final String TAG = GetFilelistCommand.class.getSimpleName();

    public GetFilelistCommand(HaotekDevice device, int CommandType) {
        super(device, CommandType);
    }

    @Override
    protected String getAction() {
//        return "?custom=1&cmd=3015";
        return "?custom=1&cmd=" + WiFiCommandDefine.GET_FILE_LIST;
    }

    public static class Response extends HaotekCommand.Response {
        public ArrayList<Filelist> mList = new ArrayList<>();
    }

    protected Response dispatchResponse(String soap) {
        super.dispatchResponse(soap);
        Response response = new Response();
        Log.d(TAG, "Soap : " + soap);
        ArrayList<Filelist> list = new ArrayList<>();
        list.clear();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(soap));
            int eventType = xpp.getEventType();
            Filelist info = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    Log.d(TAG, " tagName : " + tagName);
                    switch (tagName) {
                        case "NAME":
                            info = new Filelist();
                            info.mName = xpp.nextText();
                            break;
                        case "FPATH":
//                            info.mPath = xpp.nextText().replace("A:\\", "http://192.168.1.254/");
                            //                            Log.d(TAG,"Show Full path : "+ xpp.nextText());
//                            A:\DCIM\MOVIE\2015_0101_120445_465.MOV
//                              info.mPath = xpp.nextText().replace("A:\\", "");
                            info.mPath = xpp.nextText().replace("A:", "").replace("\\", "/");
                            break;
                        case "SIZE":
                            info.mSize = xpp.nextText();
                            break;
                        case "TIMECODE":
                            info.mTimecode = xpp.nextText();
                            break;
                        case "TIME":
                            info.mTime = xpp.nextText();
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
