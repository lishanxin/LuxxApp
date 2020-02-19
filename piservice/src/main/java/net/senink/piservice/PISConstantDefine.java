package net.senink.piservice;

import android.os.Environment;

import java.io.File;

public class PISConstantDefine {
	// xuhui add
	public static final int INVALID_NET_ID = 0xFFFF;
	/**
	 * 网络连接状态
	 */
	// 网络不可用
	public final static int PINM_STATUS_INVALID = -1;
	// 网络正在初始化
	public final static int PIPA_NET_STATUS_INITING = 0x01;
	// 已连接
	public final static int PIPA_NET_STATUS_CONNECTED = 0x02;
	// 已断开
	public final static int PIPA_NET_STATUS_DISCONNECTED = 0x03;
	// 连接中
	public final static int PIPA_NET_STATUS_CONNECTING = 0x04;
	// 验证失败，帐号或密码错误
	public final static int PIPA_NET_STATUS_USER_PASSWORD_ERROR = 0x10;
	// 连接超时
	public final static int PIPA_NET_STATUS_CONNECT_TIMEOUT = 0x11;

	/**
	 *  PINM网络类型
	 */
	// 本地UDP连接
	public final static int PIPA_NET_PINMOverUC  = 0;
	// MicroCloud远程连接
	public final static int PIPA_NET_PINMOverMC  = 1;
	// BLE4.0蓝牙连接
	public final static int PIPA_NET_PINMOverBLE = 2;

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

	public final static int PIS_TIMEOUT = 30 * 1000;// 服务超时

	public final static int REQUEST_TIMEOUT = 5 * 1000;// 请求超时

	public final static int LOGIN_TIMEOUT = 2 * 1000;// 登陆超时

	public final static int MIAN_TIMER_DURATION = 1000;// 应用TIMER间隔

	// /获取服务关联信息
	public final static int PIS_CMD_LINK_GET = 0x82;
	// /设置服务关联信息
	public final static int PIS_CMD_LINK_SET = 0x83;
	// /删除服务关联信息
	public final static int PIS_CMD_LINK_DEL = 0x84;

	/**
	 * 系统及设备类
	 * 
	 */
	public final static int PIS_MAJOR_SYSTEM = 0x00;
	/**Micro Cloud 服务*/
	public final static int PIS_SYSTEM_CLOUD = 0x06;

	/**升级服务*/
	public final static int PIS_SYSTEM_UPDATE = 0x04;
	/**设备服务*/
	public final static int PIS_SYSTEM_DEVICE = 0x00;
	/**智能元*/
	public final static int PIS_SYSTEM_SMARTCELL = 0x10;

//	public final static int PIS_DEVICE_T1 = 0x00;
//	public final static int PIS_DEVICE_T2 = 0x00;
//
//	public final static int PIS_UPDATE_T1 = 0x00;
//	public final static int PIS_UPDATE_T2 = 0x04;
//
//	public final static int PIS_MCM_T1 = 0x00;
//	public final static int PIS_MCM_T2 = 0x06;
//
//
//	public final static int PIS_SMART_CELL_T1 = 0x00;
//	public final static int PIS_SMART_CELL_T2 = 0x10;

	/**
	 * 电工类
	 */
	public final static int PIS_MAJOR_ELECTRICIAN = 0x10;

	/**普通插口*/
	public final static int PIS_ELECTRICIAN_NORMAL = 0x01;
	/**带功率统计插口*/
	public final static int PIS_ELECTRICIAN_SWITCH_POWER = 0x02;

	/**
	 * 网络类
	 */
	public final static int PIS_MAJOR_NETWORK  = 0x70;
	/**CSRMES网桥*/
	public final static int PIS_NETWORK_CSRMESH = 0x01;

//	public final static int PIS_GW_CONFIG_T1 = 0x00;
//	public final static int PIS_GW_CONFIG_T2 = 0x07;
	/**
	 * 照明类
	 */
	public final static int PIS_MAJOR_LIGHT  = 0x10;
	/**RGBW四色灯*/
	public final static int PIS_LIGHT_COLOR  = 0x03;
	/**双色灯*/
	public final static int PIS_LIGHT_LIGHT = 0x04;
	/**单色灯*/
	public final static int PIS_LIGHT_COLORLIGHT  = 0x05;
	public final static int PIS_LIGHT_CANDLE  = 0x05;


	/**
	 * 遥控器
	 */
	public final static int PIS_MAJOR_REMOTER = 0x40;
	public final static int PIS_REMOTER_KEY16 = 0x01;

	/**
	 * 可穿戴
	 */
	public final static int PIS_MAJOR_WEARABLE = 0x80;
	/**智能鞋垫遥控器*/
	public final static int PIS_WEARABLE_INSOLE = 0x01;
//	public final static int PIS_INSOLE_T2 = 0x01;

	/**
	 * 多媒体类
	 */
	public final static int PIS_MAJOR_MULTIMEDIA = 0x13;
	/**音乐灯*/
	public final static int PIS_MULTIMEDIA_COLOR = 0x03;
	public final static int PIS_MULTIMEDIA_COLORLIGHT = 0x05;

	/**
	 * 未知
	 */
	public final static int PIS_MAJOR_GROUP = 0xFF;
	public final static int PIS_GROUP_UNKNOW = 0xFF;
	
	public final static int PIS_TYPE_DEVICE = 0;// 属于系统类，通过其可以获取设备信息或进行一些设备层面的操作，包括对设备的共有属性进行配置。
	public final static int PIS_TYPE_UPDATE = 1;// 属于系统类，通过PISUpdate在服务器上检测其他设备的固件是否有版本更新，及将固件更新给具体的设备。
	public final static int PIS_TYPE_MCM = 2;// 属于系统类，主要负责服务器数据管理的相应功能。
	public final static int PIS_TYPE_GW_CONFIG = 3;// 属于设备类，通过PISGWConfig配置网关数据。
	public final static int PIS_TYPE_SMART_CELL = 4;// 属于系统类，主要负责智能元管理的相应功能。
	public final static int PIS_TYPE_SWITCH_LIGHT_ENABLE = 5;// 属于设备类，主要负责处理开关控制服务的相应功能(可调光)
	public final static int PIS_TYPE_SWITCH_LIGHT_DISABLE = 6;// 属于设备类，主要负责处理开关控制服务的相应功能(不可调光)
	public final static int PIS_TYPE_UNKNOW = 7;// 未知的设备类型，当T1和T2不在当前系统定义值中时，就使用该类代替，不支持命令与消息。
	// xuhui add end

	/**
	 * PIPA服务连接状态
	 */
	public final static int PINMDisconnect = 0;
	public final static int PINMConnecting = 1;
	public final static int PINMConnected = 2;
	public final static int PINMInvalid = 3;

	public final static int SE_SYS_MSG_BASE = 0X500;
	/**
	 * \def SE_SYS_MSG_INIT \brief notify the application after the core is the
	 * end of initing.
	 */
	public final static int SE_SYS_MSG_INIT = (SE_SYS_MSG_BASE + 1);

	public static final int PIS_PM_LEVEL_NORMAL = 0;
	public static final int PIS_PM_LEVEL_S1 = 1;
	public static final int PIS_PM_LEVEL_S2 = 2;
	public static final int PIS_PM_LEVEL_S3 = 3;
	public static final int PIS_PM_LEVEL_OFF = 4;

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
	public final static String PKG_NAME = "com.senink.seninkapp";

	/**
	 * Database Authority
	 */
	public final static String DB_AUTHORITY = "com.senink.seninkapp";

	/**
	 * 查询服务的条件
	 */
	public final static int SERVICES_QUERY_ON_NONE = 0;
	public final static int SERVICES_QUERY_ON_VALID = 1;
	public final static int SERVICES_QUERY_ON_LOCATION = 2;
	public final static int SERVICES_QUERY_ON_DEVICE = 3;
	public final static int SERVICES_QUERY_ON_NAME = 4;
	public final static int SERVICES_QUERY_ON_TYPE = 5;
	public final static int SERVICES_QUERY_ON_SMART_CELL = 6;

	/**
	 * 查询设备的条件
	 */
	public final static int DEVICES_QUERY_ON_NONE = 0;
	public final static int DEVICES_QUERY_ON_LOCATION = 1;
	public final static int DEVICES_QUERY_ON_SERVICE = 2;
	public final static int DEVICES_QUERY_ON_NAME = 3;
	public final static int DEVICES_QUERY_ON_CLASS = 4;

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
