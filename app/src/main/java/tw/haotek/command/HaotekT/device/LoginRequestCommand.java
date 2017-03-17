package tw.haotek.command.HaotekT.device;

import java.io.IOException;

import tw.haotek.command.HaotekT.HaotekTCommand;
import tw.haotek.dut.HaotekDevice;

/**
 * Created by Neo on 2016/1/30 0030.
 */
public class LoginRequestCommand extends HaotekTCommand {
    private static final String TAG = LoginRequestCommand.class.getSimpleName();

    public LoginRequestCommand(HaotekDevice device) {
        super(device);
    }

    @Override
    protected String getAction() {
        return null;
    }

    @Override
    public Response run() throws IOException {
        final String address = mDevice.getInetAddress();
        final String uid = mDevice.getUID();
        final int sid = mAgent.getSID();
        try {
            final int rec = mAgent.IOTC_Connect_ByUID_Parallel(mUID, mSID); //FIXME  rec > 0 is ok SUCCESS
        } catch (IllegalArgumentException ex) {
            try {

//            } catch (IllegalStateException | EOFException ex2) {
            } catch (IllegalStateException ex2) {
//                throw new HaotekTException("Http returned null response... " + address, request, ex2);
            }
        }

//        if (envelope.bodyIn instanceof SoapObject) { // SoapObject = SUCCESS
//            return dispatchSoapResponse((SoapObject) envelope.bodyIn);
//        } else if (envelope.bodyIn instanceof SoapFault) { // SoapFault = FAILURE
//            throw (SoapFault) envelope.bodyIn;
//        }
        throw new RuntimeException("Shouldn't get here!!! " + address);
    }
}
