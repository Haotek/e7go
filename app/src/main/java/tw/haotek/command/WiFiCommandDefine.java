package tw.haotek.command;

/**
 * Created by Neo on 2015/11/13.
 */
public class WiFiCommandDefine {
    /* WiFi command ID */
    //photo mode command
    public static final int CAPTURE = 1001;
    public static final int CAPTURE_SIZE = 1002;
    public static final int FREE_PIC_NUM = 1003;
    //movie mode command
    public static final int MOVIE_RECORD = 2001;//  0: off  1:on
    public static final int MOVIE_REC_SIZE = 2002; // 0:1080/30  1:720P/60 2:720/30
    public static final int MOVIE_CYCLIC_REC = 2003;// 0:OFF  1:3MIN  2:5MIN 3:10MIN  ??  2 3 5 10 ?/
    public static final int MOVIE_HDR = 2004;//  set  0: off  1:on
    public static final int MOVIE_EV = 2005;
    public static final int MOTION_DET = 2006;//  0: off  1:on
    public static final int MOVIE_AUDIO = 2007;//  0: off  1:on
    public static final int MOVIE_DATE_PRINT = 2008;//  0: off  1:on
    public static final int MOVIE_MAX_RECORD_TIME = 2009;//unit is second
    public static final int MOVIE_LIVEVIEW_SIZE = 2010;
    public static final int MOVIE_GSENSOR_SENS = 2011;// 0:OFF  1:LOW 2:MED 3:HIGH
    public static final int MOVIE_AUTO_RECORDING = 2012;//  0: off  1:on
    public static final int MOVIE_REC_BITRATE = 2013;//unit is byte/sec   //FIXME  not use
    public static final int MOVIE_LIVEVIEW_BITRATE = 2014;//unit is byte/sec   //FIXME  not use
    public static final int MOVIE_LIVEVIEW_START = 2015;//  0: off  1:on
    public static final int MOVIE_RECORDING_TIME = 2016;//unit is second
    public static final int MOVIE_REC_TRIGGER_RAWENC = 2017;//FIXME   Take  Recording Size PIC
    public static final int MOVIE_GET_RAWENC_JPG = 2018;//FIXME  Get  Recording Size PIC File
    public static final int MOVIE_REC_TIME_LAPSE = 2019;//FIXME  not in Doc // Start trigger Time lapse set
    public static final int GET_WDR = 2020;//FIXME  set WDR state
    public static final int GET_TIME_LAPSE = 2021;//FIXME  not in Doc get TIME_LAPSE state
    //setup command
    public static final int SET_MODE_CHANGE = 3001;// 0:photo mode 1:movie mode 2:playback mode
    /*Import   */ //FIXME maybe get Support command ???!!
    public static final int SUPPORT_QUERY = 3002;
    /*Import   */
    public static final int SET_SSID = 3003;//SSID max 32 bytes
    public static final int SET_PASSPHRASE = 3004;// max 26 bytes
    public static final int SET_DATE = 3005;// format yyyy-mm-dd
    public static final int SET_TIME = 3006;//format hh:mm:ss
    public static final int SET_POWER_OFF_TIME = 3007; // 0: on 1:3MIN 2:5MIN 3:10MIN
    public static final int SET_LANGUAGE = 3008;// 0:EN 1:FR 2:ES 3:PO 4:DE 5:IT 6:SC 7:TC 8:RU 9:JP
    public static final int SET_TV_FORMAT = 3009;// 0: NTSC 1:PAL
    public static final int FORMAT = 3010;// 0: nand 1:SD
    public static final int SET_SYS_RESET = 3011;
    public static final int GET_VERSION = 3012;
    public static final int FWUPDATE = 3013;
    /*Import   */ //FIXME  the int get Device erery state
    public static final int GET_CURRENT_STATE = 3014;
    /*Import*/
    public static final int GET_FILE_LIST = 3015;
    public static final int GET_HEARTBEAT = 3016;//Check Device exixt ?
    public static final int GET_DISK_FREE_SPACE = 3017;
    public static final int SET_RECONNECT_WIFI = 3018;//FIXME  Doc error
    public static final int GET_BATTERY_LEVEL = 3019;// 0:full 1:med 2:low 3:empty 4:exhausted 5:charge
    public static final int GET_NOTIFY_STATUS = 3020; //FIXME  the command not http use socket port 3333
    public static final int SAVE_MENUINFO = 3021;//Save all setting to flash !!!
    public static final int GET_HW_CAPACITY = 3022; // FIXME  need defined
    public static final int REMOVE_CONNECT_USER = 3023;//only one user can connect at the same  notify by socket port 3333
    public static final int GET_CARD_STATUS = 3024; //  0: SD remove 1: SD inserted 2:SD locked
    public static final int GET_FW_DOWNLOAD_URL = 3025; //FIXME  ??? i dont know ???
    public static final int GET_UPDATE_FW_PATH = 3026;//FIXME  ??? i dont know ???
    public static final int UPLOAD_FILE = 3027;
    public static final int SET_PIP_STYLE = 3028;
    public static final int GET_SD_FILE = 3029;
    //playback command
    public static final int CMD_THUMB = 4001;//FIXME  ??? i dont know ???
    public static final int SCREEN = 4002;//FIXME  ??? i dont know ???
    public static final int DELETE_ONE = 4003;//FIXME  ??? i dont know ???
    public static final int DELETE_ALL_FILE = 4004;
    //Haotek command
    public static final int WIFIAPP_CMD_EVENT_LIST = 9000;
    public static final int WIFIAPP_CMD_EVENT_SET = 9001;//FIXME  32 :PHOTO  64 :MOVIE
    public static final int WIFIAPP_CMD_GET_REC_STATUS = 9002;
    public static final int WIFIAPP_CMD_GET_DEVICE_INFO = 9003;
    public static final int WIFIAPP_CMD_SYSTEM_REBOOT = 9005;
    public static final int WIFIAPP_CMD_GET_NOW_TIME_RECODING_FILE_NAME = 9089;
    public static final int WIFIAPP_CMD_GET_DATE_TIME = 9090;
    public static final int WIFIAPP_CMD_SET_RF_PAIR = 9091;
    public static final int WIFIAPP_CMD_SET_GPS = 9092;
    public static final int WIFIAPP_CMD_SET_BEEP_SOUND = 9093;
    public static final int WIFIAPP_CMD_SET_LDWS = 9094;
    public static final int WIFIAPP_CMD_SET_TPMS = 9095;
    public static final int WIFIAPP_CMD_SET_SCREEN_SAVE = 9096;
    public static final int WIFIAPP_CMD_SET_FREQUENCY = 9097;
    public static final int WIFIAPP_CMD_SET_OSD = 9098;
    public static final int WIFIAPP_CMD_CAR_MODE = 9200;
    public static final int WIFIAPP_CMD_MOVIE_PARKINGUID_LEVEL = 9201;
    public static final int WIFIAPP_CMD_MOVIE_TIMELAPSE_REC = 9202;

    public static final int WIFIAPP_CMD_GET_PHOTO_EV = 9400;
    public static final int WIFIAPP_CMD_GET_PHOTO_DATE_STAMP = 9401;
    public static final int WIFIAPP_CMD_GET_PHOTO_ISO = 9402;
    public static final int WIFIAPP_CMD_GET_PHOTO_QUALITY = 9403;
    public static final int WIFIAPP_CMD_GET_PHOTO_SHARPNESS = 9404;
    public static final int WIFIAPP_CMD_GET_PHOTO_COLOR_EFFECT = 9405;
}
