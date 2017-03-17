package tw.haotek.command.HaotekT;

import java.io.IOException;

import tw.haotek.dut.HaotekDevice;
import tw.haotek.p2pAgent.TutkAgent;

/**
 * Created by Neo on 2016/1/29 0029.
 */
public abstract class HaotekTCommand {
    private static final String TAG = HaotekTCommand.class.getSimpleName();
//IOTCApis error code===================================================================================
    /**
     * The function is performed successfully.
     */
    public static final int IOTC_ER_NoERROR = 0;

    /**
     * IOTC servers have no response, probably caused by many types of Internet connection issues.
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_SERVER_NOT_RESPONSE)
     */
    public static final int IOTC_ER_SERVER_NOT_RESPONSE = -1;

    /**
     * IOTC masters cannot be resolved their domain name, probably caused
     * by network connection or DNS setting issues.
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_FAIL_RESOLVE_HOSTNAME)
     */
    public static final int IOTC_ER_FAIL_RESOLVE_HOSTNAME = -2;

    /**
     * IOTC module is already initialized. It is not necessary to re-initialize.
     */
    public static final int IOTC_ER_ALREADY_INITIALIZED = -3;

    /**
     * IOTC module fails to create Mutexs when doing initialization. Please
     * check if OS has sufficient Mutexs for IOTC platform.
     */
    public static final int IOTC_ER_FAIL_CREATE_MUTEX = -4;

    /**
     * IOTC module fails to create threads. Please check if OS has ability
     * to create threads for IOTC module.
     */
    public static final int IOTC_ER_FAIL_CREATE_THREAD = -5;

    /**
     * IOTC module fails to create sockets. Please check if OS supports socket service
     */
    public static final int IOTC_ER_FAIL_CREATE_SOCKET = -6;

    /**
     * IOTC module fails to set up socket options.
     */
    public static final int IOTC_ER_FAIL_SOCKET_OPT = -7;

    /**
     * IOTC module fails to bind sockets
     */
    public static final int IOTC_ER_FAIL_SOCKET_BIND = -8;

    /**
     * The specified UID is not licensed.
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_UNLICENSE)
     */
    public static final int IOTC_ER_UNLICENSE = -10;

    /**
     * The device is already login successfully
     */
    public static final int IOTC_ER_LOGIN_ALREADY_CALLED = -11;

    /**
     * IOTC module is not initialized yet. Please use IOTC_Initialize() or
     * IOTC_Initialize2() for initialization.
     */
    public static final int IOTC_ER_NOT_INITIALIZED = -12;

    /**
     * The specified timeout has expired during the execution of some IOTC
     * module service. For most cases, it is caused by slow response of remote
     * site or network connection issues
     */
    public static final int IOTC_ER_TIMEOUT = -13;

    /**
     * The specified IOTC session ID is not valid
     */
    public static final int IOTC_ER_INVALID_SID = -14;

    /**
     * The specified device's name is not unknown to the IOTC servers
     */
    public static final int IOTC_ER_UNKNOWN_DEVICE = -15;

    /**
     * IOTC module fails to get the local IP address
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_FAIL_GET_LOCAL_IP)
     */
    public static final int IOTC_ER_FAIL_GET_LOCAL_IP = -16;

    /**
     * The device already start to listen for connections from clients. It is
     * not necessary to listen again.
     */
    public static final int IOTC_ER_LISTEN_ALREADY_CALLED = -17;

    /**
     * The number of IOTC sessions has reached maximum.
     * Please use IOTC_Set_Max_Session_Number() to set up the max number of IOTC sessions
     */
    public static final int IOTC_ER_EXCEED_MAX_SESSION = -18;

    /**
     * IOTC servers cannot locate the specified device, probably caused by
     * disconnection from the device or that device does not login yet.
     */
    public static final int IOTC_ER_CAN_NOT_FIND_DEVICE = -19;

    /**
     * The client is connecting to a device. It is prohibited to connect again.
     */
    public static final int IOTC_ER_CONNECT_IS_CALLING = -20;

    /**
     * The remote site already closes this IOTC session.
     * Please call IOTC_Session_Close() to release IOTC session resource in locate site.
     */
    public static final int IOTC_ER_SESSION_CLOSE_BY_REMOTE = -22;

    /**
     * This IOTC session is disconnected because remote site has no any response
     * after a specified timeout expires.
     */
    public static final int IOTC_ER_REMOTE_TIMEOUT_DISCONNECT = -23;

    /**
     * The client fails to connect to a device because the device is not listening for connections.
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_DEVICE_NOT_LISTENING)
     */
    public static final int IOTC_ER_DEVICE_NOT_LISTENING = -24;

    /**
     * The IOTC channel of specified channel ID is not turned on before transferring data.
     */
    public static final int IOTC_ER_CH_NOT_ON = -26;

    /**
     * A client stops connecting to a device by calling IOTC_Connect_Stop()
     */
    public static final int IOTC_ER_FAIL_CONNECT_SEARCH = -27;

    /**
     * Too few masters are specified when initializing IOTC module.
     * Two masters are required for initialization at minimum.
     */
    public static final int IOTC_ER_MASTER_TOO_FEW = -28;

    /**
     * A client fails to pass certification of a device due to incorrect key.
     */
    public static final int IOTC_ER_AES_CERTIFY_FAIL = -29;

    /**
     * The number of IOTC channels for a IOTC session has reached maximum, say, MAX_CHANNEL_NUMBER.
     */
    public static final int IOTC_ER_SESSION_NO_FREE_CHANNEL = -31;

    /**
     * ??? All tcp port 80, 443, 8000, 8080 cant use
     */
    public static final int IOTC_ER_TCP_TRAVEL_FAILED = -32;

    /**
     * Cannot connect to IOTC servers in TCP
     * See [Troubleshooting](..\Troubleshooting\index.htm#IOTC_ER_TCP_CONNECT_TO_SERVER_FAILED)
     */
    public static final int IOTC_ER_TCP_CONNECT_TO_SERVER_FAILED = -33;

    /**
     * A client wants to connect to a device in non-secure mode while that device
     * supports secure mode only.
     */
    public static final int IOTC_ER_CLIENT_NOT_SECURE_MODE = -34;

    /**
     * A client wants to connect to a device in secure mode while that device does
     * not support secure mode.
     */
    public static final int IOTC_ER_CLIENT_SECURE_MODE = -35;

    /**
     * A device does not support connection in secure mode
     */
    public static final int IOTC_ER_DEVICE_NOT_SECURE_MODE = -36;

    /**
     * A device does not support connection in non-secure mode
     */
    public static final int IOTC_ER_DEVICE_SECURE_MODE = -37;

    /**
     * The IOTC session mode specified in IOTC_Listen2(), IOTC_Connect_ByUID2()
     * or IOTC_Connect_ByName2() is not valid.
     * Please see IOTCConnectionMode for possible modes.
     */
    public static final int IOTC_ER_INVALID_MODE = -38;

    /**
     * A device stops listening for connections from clients.
     */
    public static final int IOTC_ER_EXIT_LISTEN = -39;

    /**
     * The specified device does not support advance function
     * (TCP relay and P2PTunnel module)
     */
    public static final int IOTC_ER_NO_PERMISSION = -40;

    /**
     * Network is unreachable, please check the network settings
     */
    public static final int IOTC_ER_NETWORK_UNREACHABLE = -41;

    /**
     * A client fails to connect to a device via relay mode
     */
    public static final int IOTC_ER_FAIL_SETUP_RELAY = -42;

    /**
     * A client fails to use UDP relay mode to connect to a device
     * because UDP relay mode is not supported for that device by IOTC servers
     */
    public static final int IOTC_ER_NOT_SUPPORT_RELAY = -43;

    /**
     * No IOTC server information while device login or client connect
     * because no IOTC server is running or not add IOTC server list
     */
    public static final int IOTC_ER_NO_SERVER_LIST = -44;

    /**
     * The connecting device duplicated loggin and may unconnectable.
     */
    public static final int IOTC_ER_DEVICE_MULTI_LOGIN = -45;

    /**
     * The arguments passed to a function is invalid.
     */
    public static final int IOTC_ER_INVALID_ARG = -46;

    /**
     * The remote device not support partial encoding
     */
    public static final int IOTC_ER_NOT_SUPPORT_PE = -47;

    /**
     * The remote device no more free session can be connected.
     */
    public static final int IOTC_ER_DEVICE_EXCEED_MAX_SESSION = -48;

    /**
     * The function call is a blocking call and was called by other thread.
     */
    public static final int IOTC_ER_BLOCKED_CALL = -49;

    /**
     * The session was closed.
     */
    public static final int IOTC_ER_SESSION_CLOSED = -50;

    /**
     * Remote doesn't support this function.
     */
    public static final int IOTC_ER_REMOTE_NOT_SUPPORTED = -51;

    /**
     * The function is aborted by related function.
     */
    public static final int IOTC_ER_ABORTED = -52;

    /**
     * The buffer size exceed maximum packet size.
     */
    public static final int IOTC_ER_EXCEED_MAX_PACKET_SIZE = -53;

    /**
     * Server does not support this feature.
     */
    public static final int IOTC_ER_SERVER_NOT_SUPPORT = -54;

    /**
     * Cannot find a path to write data
     */
    public static final int IOTC_ER_NO_PATH_TO_WRITE_DATA = -55;

    /**
     * Start function is not called
     */
    public static final int IOTC_ER_SERVICE_IS_NOT_STARTED = -56;

    /**
     * Already in processing
     */
    public static final int IOTC_ER_STILL_IN_PROCESSING = -57;

    /**
     * Out of memory
     */
    public static final int IOTC_ER_NOT_ENOUGH_MEMORY = -58;

    /**
     * All Server response can not find device
     */
    public static final int IOTC_ER_DEVICE_OFFLINE = -90;

    protected static final int LOGIN_TIMEOUT_SEC = 5;
    protected HaotekDevice mDevice;
    protected TutkAgent mAgent;
    protected String mUID;
    protected int mSID = -1;
    protected int mAVIndex = -1;

    public HaotekTCommand(HaotekDevice device) {
        mDevice = device;
        mAgent = (TutkAgent) mDevice.getDeviceP2PAgent();
        mUID = mDevice.getUID();
        mSID = mAgent.getSID();
    }

    public HaotekDevice getDevice() {
        return mDevice;
    }

    public void setDevice(HaotekDevice device) {
        mDevice = device;
    }

    protected abstract String getAction();

    public int getConnectIndex() {
        return mAVIndex;
    } //FIXME  public or  protected ?

    public void setConnectIndex(int avindex) {
        mAVIndex = avindex;
    }

    public static class Response {
        public int rResult;
    }

    public Response run() throws IOException {
        final String address = mDevice.getInetAddress();
        final String uid = mDevice.getUID();
        final int sid = mAgent.getSID();


        try {
            ////        avSendIOCtrl(int avIndex, int ioType, byte[] ioCtrlBuf, int ioCtrlBufSize);
////        AVAPIs.avSendIOCtrl(avIndex, 0xFF,new byte[2], 2);
//            AVAPIs.avSendIOCtrl(mAgent.getConnectIndex(), 0xFF, new byte[2], 2);
//            AVAPIs.avSendIOCtrl(mAgent.getConnectIndex(), 0x1FF, new byte[8], 8);//FIXME Start Vidoe
//            AVAPIs.avSendIOCtrl(mAgent.getConnectIndex(), 0x1FF, new byte[8], 8);//FIXME Start Vidoe
//            AVAPIs.avSendIOCtrl(mAgent.getConnectIndex(), 0x300, new byte[8], 8);//FIXME Start Audio
//        } catch (IllegalArgumentException | EOFException ex) {
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

    public interface OnCommandCompletedListener {
//        void onCommandCompleted(HNAPCommand command, String result, Response response);
    }
}
