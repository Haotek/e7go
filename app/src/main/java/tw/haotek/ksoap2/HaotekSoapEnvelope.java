package tw.haotek.ksoap2;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Neo on 2015/12/9.
 */
public class HaotekSoapEnvelope extends SoapSerializationEnvelope {
    public HaotekSoapEnvelope(int version) {
        super(version);
    }

    @Override
    public void write(XmlSerializer writer) throws IOException {
        writer.startDocument("UTF-8", null);
        writer.startTag(env, "Function");
        writeBody(writer);
        writer.endTag(env, "Function");
    }
}
