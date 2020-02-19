package net.senink.seninkapp.ui.fusion;

import java.io.File;
import android.os.Environment;

public class FusionField {

	public final static int SE_SYS_MSG_BASE = 0X500;
	/**
	 * \def SE_SYS_MSG_INIT \brief notify the application after the core is the
	 * end of initing.
	 */
	public final static int SE_SYS_MSG_INIT = (SE_SYS_MSG_BASE + 1);

	public final static int PIS_MSG_SRVINFO = 0x21;
	public final static int PIS_MSG_ALIAS_INFO = 0x22;
	public final static int PIS_MSG_LINK_INFO = 0x23;
	public final static int PIS_MSG_PM_INFO = 0x24;

	public static final int PIS_PM_LEVEL_NORMAL = 0;
	public static final int PIS_PM_LEVEL_S1 = 1;
	public static final int PIS_PM_LEVEL_S2 = 2;
	public static final int PIS_PM_LEVEL_S3 = 3;
	public static final int PIS_PM_LEVEL_OFF = 4;

	// 所有的PIServices所支持的公共命令、消息及事件
	// /获取服务基本信息
	public final static int PIS_CMD_SRVINFO_GET = 0x80;
	// /设置服务基本信息
	public final static int PIS_CMD_SRVINFO_SET = 0x81;
	// /获取服务关联信息
	public final static int PIS_CMD_LINK_GET = 0x82;
	// /设置服务关联信息
	public final static int PIS_CMD_LINK_SET = 0x83;
	// /删除服务关联信息
	public final static int PIS_CMD_LINK_DEL = 0x84;
	// /获取服务宏命令信息
	public final static int PIS_CMD_ALIAS_GET = 0x85;
	// /设置服务宏命令信息
	public final static int PIS_CMD_ALIAS_SET = 0x86;
	// /删除服务宏命令信息
	public final static int PIS_CMD_ALIAS_DEL = 0x87;
	// /运行宏命令
	public final static int PIS_CMD_ALIAS_DO = 0x88;
	// /获取服务电源管理信息
	public final static int PIS_CMD_PM_GET = 0x89;
	// /设置服务电源管理信息
	public final static int PIS_CMD_PM_SET = 0x8A;

	/**
	 * PIPA_MESSAGE 定义了PISservice 所支持的消息常量值，为int类型
	 */
	public final static int PIPA_EVENT_BASE = 0x10;
	public final static int PIPA_EVENT_DISCOVERY = (PIPA_EVENT_BASE + 0x00);
	public final static int PIPA_EVENT_ALIVE = (PIPA_EVENT_BASE + 0x01);
	public final static int PIPA_EVENT_PAIR = (PIPA_EVENT_BASE + 0x02);
	public final static int PIPA_EVENT_SS = (PIPA_EVENT_BASE + 0x03);
	public final static int PIPA_EVENT_COMMAND = (PIPA_EVENT_BASE + 0x04);
	public final static int PIPA_EVENT_MESSAGE = (PIPA_EVENT_BASE + 0x05);
	public final static int PIPA_EVENT_PAIR_ACK = (PIPA_EVENT_BASE + 0x07);
	public final static int PIPA_EVENT_SS_ACK = (PIPA_EVENT_BASE + 0x08);
	public final static int PIPA_EVENT_CMD_ACK = (PIPA_EVENT_BASE + 0x09);
	public final static int PIPA_EVENT_TRACK = (PIPA_EVENT_BASE + 0x0A);

	public final static int SE_PIPA_HANDLE_INVALID = 0;// HTTP HANDLE INVALID

	/**
	 * piswitch
	 */
	public final static int PIS_CMD_LCS_GET = 0x90;
	public final static int PIS_CMD_LCS_SET = 0x91;
	public final static int PIS_CMD_POWER_GET = 0x92;
	public final static int PIS_CMD_POWER_CALCFREQ_SET = 0x93;
	public final static int PIS_CMD_POWER_CALCFREQ_GET = 0x94;
	public final static int PIS_MSG_POWER = 0x30;
	public final static int PIS_MSG_LCS_STATUS = 0x31;

	/**
	 * PISMcm
	 */

	public final static int PIS_CMD_MCM_SET_ACCESSINFO = 0x90;
	public final static int PIS_CMD_SMART_NEW = 0x92;
	public final static int PIS_CMD_SMART_DEL = 0x93;

	public final static int PIS_CMD_MCM_DEVBIND = 0x9C; // 绑定设备
	public final static int PIS_CMD_MCM_DEVUNBIND = 0x9D; // 解绑设备

	/**
	 * PISUpdate
	 * 
	 */
	public final static int PIS_CMD_FWUPDATE_CHECK = 0x90;
	public final static int PIS_CMD_FWUPDATE_GET = 0x91;
	public final static int PIS_CMD_FWUPDATE_UPDATE = 0x92;
	public final static int PIS_MSG_FWUPDATE_NEW = 0x30;

	/**
	 * PISDevice
	 */
	public final static int PIS_CMD_DEVICE_INFO_GET = 0x90;
	public final static int PIS_CMD_DEVICE_INFO_SET = 0x91;
	public final static int PIS_CMD_DEVICE_POWER_CAPACITY_GET = 0x95;
	public final static int PIS_CMD_DEVICE_WHOAMI = 0x93;
	public final static int PIS_CMD_DEVICE_REPLACE = 0X94;
	public final static int PIS_CMD_DEVICE_REBOOT = 0x96;
	public final static int PIS_CMD_DEVICE_CHPWD = 0xA0;
	public final static int PIS_MSG_FWUPDATE_PROGRESS = 0x30;
	public final static int PIS_MSG_FWUPDATE_COMPLETED = 0x31;

	/**
	 * PISConfig
	 */
	// public final static int PIS_CMD_CONFIG_SET_WAN = 0x90;
	// public final static int PIS_CMD_CONFIG_GET_WAN = 0x91;
	// public final static int PIS_CMD_CONFIG_SET_LAN = 0x92;
	// public final static int PIS_CMD_CONFIG_GET_LAN = 0x93;
	// public final static int PIS_CMD_CONFIG_GET_AP = 0x94;
	// public final static int PIS_CMD_CONFIG_SET_AP = 0x95;
	// public final static int PIS_CMD_CONFIG_GET_APSEC = 0x96;
	// public final static int PIS_CMD_CONFIG_SET_APSEC = 0x97;
	// public final static int PIS_CMD_CONFIG_SET_PIAUTH = 0x98;
	// public final static int PIS_CMD_CONFIG_REBOOT = 0x9A;
	// public final static int PIS_CMD_CONFIG_FACTORYRESET = 0x9B;
	// public final static int PIS_CMD_CONFIG_DEVBIND = 0x9C;
	// public final static int PIS_CMD_CONFIG_DEVUNBIND = 0x9B;
	// public final static int PIS_CMD_CONFIG_SET_ADDR = 0x9E;

	public final static int PIS_CMD_CONFIG_SET_WAN = 0x90;
	public final static int PIS_CMD_CONFIG_GET_WAN = 0x91;
	public final static int PIS_CMD_CONFIG_SET_LAN = 0x92;
	public final static int PIS_CMD_CONFIG_GET_LAN = 0x93;
	public final static int PIS_CMD_CONFIG_SET_APMODE = 0x94;
	public final static int PIS_CMD_CONFIG_GET_APMODE = 0x95;

	public final static int PIS_CMD_CONFIG_GET_CLILIST = 0x96;
	public final static int PIS_CMD_CONFIG_GET_CLIFLOWINFO = 0x97;
	public final static int PIS_CMD_CONFIG_SET_PIAUTH = 0x98;
	public final static int PIS_CMD_CONFIG_DEVBINDGET = 0x99; // 获取绑定的设备列表
	public final static int PIS_CMD_CONFIG_REBOOT = 0x9A;
	public final static int PIS_CMD_CONFIG_FACTORYRESET = 0x9B;
	public final static int PIS_CMD_CONFIG_DEVBIND = 0x9C;
	public final static int PIS_CMD_CONFIG_DEVUNBIND = 0x9D;
	public final static int PIS_CMD_CONFIG_SET_ADDR = 0x9E;

	public final static int PIS_CMD_CONFIG_ADD_NETBWGRP = 0xA0;
	public final static int PIS_CMD_CONFIG_DEL_NETBWGRP = 0xA1;
	public final static int PIS_CMD_CONFIG_GET_NETBWGRP = 0xA2;
	public final static int PIS_CMD_CONFIG_ADD_NETBWUSR = 0xA3;
	public final static int PIS_CMD_CONFIG_DEL_NETBWUSR = 0xA4;
	public final static int PIS_CMD_CONFIG_GET_NETBWUSR = 0xA5;
	public final static int PIS_CMD_CONFIG_ADD_NETBLACKUSR = 0xA6;
	public final static int PIS_CMD_CONFIG_DEL_NETBLACKUSR = 0xA7;
	public final static int PIS_CMD_CONFIG_GET_NETBLACKUSR = 0xA8;
	public final static int PIS_CMD_CONFIG_ADD_NETWHITEUSR = 0xA9;
	public final static int PIS_CMD_CONFIG_DEL_NETWHITEUSR = 0xAA;
	public final static int PIS_CMD_CONFIG_GET_NETWHITEUSR = 0xAB;
	public final static int PIS_CMD_CONFIG_SET_NETPOLICY = 0xAC;
	public final static int PIS_CMD_CONFIG_GET_NETPOLICY = 0xAD;
	public final static int PIS_CMD_CONFIG_SET_NETBWTOTAL = 0xAE;
	public final static int PIS_CMD_CONFIG_GET_NETBWTOTAL = 0xAF;

	public final static int PIS_MSG_CONFIG_FLOWINFO = 0x30; // 流量信息
	public final static int PIS_MSG_CONFIG_CLICHANGED = 0x31; // 客户端接入/退出信息

	// 已知常量值
	public final static String TD_MODE_WIRED = "wired";
	public final static String TD_MODE_3G = "3g";
	public final static String TD_MODE_4G = "4g";
	public final static String TD_MODE_AUTO = "auto";

	public final static String AP_MODE_STANDARD = "sta";
	public final static String AP_MODE_REPEATER = "rep";
	public final static String AP_MODE_BRIDGE = "bri";

	public final static String AP_SECMOD_NONE = "None";
	public final static String AP_SECMOD_WPA = "WPA";
	public final static String AP_SECMOD_WEP = "WEP";

	public final static byte SENINK_DEVICE_CACHE_MIN = 0;
	public final static byte SENINK_DEVICE_CACHE_TYPE_FF = 1;
	public final static byte SENINK_DEVICE_CACHE_TYPE_BF = 2;
	public final static byte SENINK_DEVICE_CACHE_TYPE_STRANGER = 3;
	public final static byte SENINK_DEVICE_CACHE_TYPE_CACHE_MAX = 4;
	public final static byte SENINK_DEVICE_CACHE_UNKNOWN = 5;

	/**
	 * PIBase派生类区分
	 * 
	 */

	public final static int T1_PISMcm = 0x00;
	public final static int T2_PISMcm = 0x06;

	public final static int T1_PISUpdate = 0x00;
	public final static int T2_PISUpdate = 0x04;

	public final static int T1_PISDevice = 0x00;
	public final static int T2_PISDevice = 0x00;

	public final static int T1_PISGWConfig = 0x00;
	public final static int T2_PISGWConfig = 0x07;

	public final static int T1_PISSwitch = 0x10;
	public final static int T2_PISSwitch_y = 0x01;// 可调光
	public final static int T2_PISSwitch_n = 0x02;// 不可调光

	public final static int T1_PISSmartMeta = 0x00;
	public final static int T2_PISSmartMeta = 0x10;

	public static final String ROOT = Environment.getExternalStorageDirectory()
			+ File.separator;
	public static final String LOGS_CACHE = ROOT + "senink/logs/";

	public static final int UPDATE_ADDR = 1001;
	public static final int SET_SERVICE = 1002;
	public static final int SET_LCS = 1003;
	public static final int SET_POWER = 1004;
	public static final int SET_LCS_SUCCESS = 1005;
	public static final int GET_SWITCH_NAME = 1006;
	public static final int GET_SERVICE = 1007;

	// public final static int UPDATE_PAIR = 1003;

	public final static int PISERVICE_CHANGE = 1008;
	public final static int PIDEVICE_CHANGE = 1009;
	public final static int PIDEVICE_CLASSINFO_CHANGE = 1010;
	public final static int PISERVICE_CHANGE_LIST = 1011;
	public final static int PISERVICE_CHANGE_NAME = 1012;
	// workingmode wireless safe
	public final static int WIRELESS_SAFE_NONE = 1013;
	public final static int WIRELESS_SAFE_WPA = 1014;
	public final static int WIRELESS_SAFE_WEP = 1015;

	public final static int REQUEST_CODE = 1016;
	public final static int MESSAGE_GET = 1017;
	public final static int ROUTER_LIST = 1018;

	// router
	public final static int ROUTER_UPDATE_CLIENTINFOR = 1019;
	public final static int ROUTER_UPDATE_BRAND = 1020;
	public final static int ROUTER_UPDATE_WIRELESS = 1021;

	public final static int ROUTER_UPDATE_WIFISETTING = 1022;

	public final static int ROUTER_SETTING_COMPLETE = 1023;

	public final static int UPDATE_NOTIFICATION_PAIR = 1024;// 通知扫描界面正在配对设备服务

	public final static int GO_MAIN_PAGE = 1024;// 提示新增设备成功，并进入主界面

	public final static int DEL_SMART = 1025;

	public final static int GET_TIME_PERIOD = 1026;

	public final static int GET_TOTAL_TIME = 1027;

	public final static int GET_CUREENT_TIME = 1028;
	public final static int SMART_GET_LIST = 1029;// 获取受控设备列表

	/**
	 * 有网络信号
	 */
	public final static int ACTION_NETWORK_UP = 0x100e;

	/**
	 * 无网络信号
	 */
	public final static int ACTION_NETWORK_DOWN = 0x100f;

	/**
	 * 屏幕亮
	 */
	public final static int ACTION_SCREEN_ON = 0x1010;

	/**
	 * 屏幕黑
	 */
	public final static int ACTION_SCREEN_OFF = 0x1011;

	/**
	 * 解锁
	 */
	public final static int ACTION_USER_PRESENT = 0x1012;

	/**
	 * 屏幕是开着的，如果是锁屏此值为false
	 */
	public static boolean isScreenUnLocked = true;

	/**
	 * 标志网络是否连接上
	 */
	public static boolean isNetworkConntected = false;

	/**
	 * 网络发生变化
	 */
	public final static int ACTION_NETWORK_CHANGE = 0x10016;

	public final static int DISCOVERY_SERVICE = 0x10017;

	/**
	 * Package Name
	 */
	public final static String PKG_NAME = "com.jindegege.senink";

	/**
	 * Database Authority
	 */
	public final static String DB_AUTHORITY = "com.jindegege.senink";

	// public final static int CPIS_CREATING = 0; // /对象创建时的状态
	// public final static int CPIS_READY = 1; // /配对成功后的状态
	// public final static int CPIS_SUBSCRIBE_COMPLATED = 2; // /所有消息完成订阅后
	// public final static int CPIS_COMMAND_COMPLATED = 3; // /所有测试命令完成后
	// public final static int CPIS_OFFLINE = 4; // /在连续操作超时后的状态
	// public final static int CPIS_DESTORY = 5; // /对象开始释放的状态

	public final static int EnumServiceStatusInvalid = 0;
	public final static int EnumServiceStatusInline = 1;
	public final static int EnumServiceStatusReady = 2;
	public final static int EnumServiceStatusInit = 3;
	public final static int EnumServiceStatusBusy = 4;
	public final static int EnumServiceStatusOffline = 5;
	public final static int EnumServiceStatusDestory = 6;

	public final static int EnumServicesQueryBaseonNone = 0;
	public final static int EnumServicesQueryBaseonLocation = 1;
	public final static int EnumServicesQueryBaseonDevice = 2;
	public final static int EnumServicesQueryBaseonName = 3;
	public final static int EnumServicesQueryBaseonType = 4;

	public final static int EnumDevicesQueryBaseonNone = 0;
	public final static int EnumDevicesQueryBaseonLocation = 1;
	public final static int EnumDevicesQueryBaseonService = 2;
	public final static int EnumDevicesQueryBaseonName = 3;
	public final static int EnumDevicesQueryBaseonClass = 4;

	public final static int PIS_STATUS_INIT = 0x0;
	public final static int PIS_STATUS_CONNECTED = 0x1;// 已连线，但未配对
	public final static int PIS_STATUS_READY = 0x2;// 已配对，可以操作
	public final static int PIS_STATUS_STOP = 0x3;// pi服务已停止
	public final static int PIS_STATUS_OFFLINE = 0x4;// pi服务已离线
	public final static int PIS_STATUS_ERROR = 0x5;// 服务异常

	/**
	 * PIPA服务连接状态
	 */
	public final static int PINMDisconnect = 0;
	public final static int PINMConnecting = 1;
	public final static int PINMConnected = 2;
	public final static int PINMInvalid = 3;

	public final static int TIME = 6 * 1000;// the peroid of refreshing

	public final static int LOGINTIME = 5 * 1000;

	public final static int DISCOVER = 2 * 10000;

	public static long m_lastExcuteTime;

	public static final int sizeOfInt = 4;
	/*
	 * setting
	 */
	public final static int SETTING_GET_USERINFOR = 1601;

	public final static String ACTION_PI_DETAILSOFSERVICE = "com.jindegege.senink.ACTION_DETAILSOFSERVICE";
	public final static String ACTION_PI_DELETE_DETAILSOFSERVICE = "com.jindegege.senink.ACTION_DELETE_DETAILSOFSERVICE";
	public final static String DETAILSERVICE = "PIDetailServiceFragment";
	public final static String USERINFOR = "UserInforFragment";
	// the url of user
	public static final String USER_NEW_URL = "http://www.touchome.net/webapi/member_app.php?op=add&";
	public static final String USER_EDIT_URL = "http://www.touchome.net/webapi/member_app.php?op=edit&";
	public static final String USER_GET_URL = "http://www.touchome.net/webapi/member_app.php?op=list&";
	public static final boolean DEBUG = true;
	public static final String TAG = "Ryan";
	public static final String NULL = "null";
	public static final String ENCODE_UTF = "UTF-8";
	// device
	public static final String DEVICE_LIST = "http://www.touchome.net/webapi/termInfoList.php?";

	public static final String FamilyControl = "000010000001"; // 家长控制
	public static final String FlowControl = "000010000002"; // 流量控制
	public static final String BandWidthControl = "000010000003"; // 带宽控制
	public static final String VideoSpeedy = "000010000004"; // 视频加速
	public static final String GameSpeedy = "000010000005"; // 游戏加速
	public static final String AdvertisementIntercept = "000010000006"; // 广告拦截
	public static final String UnknowSmartMeta = "000010000000"; // 未知智能元类型

}
