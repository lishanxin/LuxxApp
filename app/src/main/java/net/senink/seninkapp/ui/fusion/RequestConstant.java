package net.senink.seninkapp.ui.fusion;

import java.io.File;
import android.os.Environment;

public class RequestConstant {

	public static final String MAIN_TAB_ACTIVITY = ".ui.activity.MainTab";
	public static final String CHATTING_ACTIVITY = ".chats.activity.Chatting";
	public static final String CHATS_ACTIVITY = ".ui.activity.ChatsActivity";

	public static final short RECORD_VOICE_TIME = 30;

	public static final short START_YEAR = 1900;

	// public static final String SERVICE = "r99999999";
	/**
	 * 关于官方帐号的
	 */
	public static final String INFINIX = "infinix";
	public static final String TECNO = "tecno";
	public static final String ITEL = "itel";

	public static final String PALMCHAT_ID = "r99999999";
	public static final String TECNO_ID = "r99999980";
	public static final String INFINIX_ID = "r99999981";
	public static final String ITEL_ID = "r99999982";
	public static final String CARLCARE_ID = "r99999970";

	public static final String PALMCHAT_TEAM = "Palmchat Team";
	public static final String TECNO_MOBIEL = "Tecno Mobile";
	public static final String INFINIX_MOBIEL = "Infinix Mobile";
	public static final String ITEL_MOBIEL = "itel Mobile";
	public static final String CARLCARE_SERVICE = "Carlcare Service";
	public static final String SERVICE_FRIENDS = "r";
	public static final String SERVICE_NAME = "Palmchat Team";

	/* 爱尔兰服务器(176开头或者54开头为爱尔兰服务器) */
	// public static String diapatchUrl = "http://176.34.150.202";//standard
	// public static String rootUrl = "http://54.246.149.100:8088";//第一台服务器
	// //"http://54.246.204.207:8088";//第二台服务器
	// public static String mediaRootUrl = "http://54.246.154.122:34599";
	// public static String avatarRootUrl = "http://54.246.144.245:34588";

	// public static String diapatchUrl = "http://176.34.150.202";

	/* 深圳服务器(218开头为深圳服务器) */
	// public static String diapatchUrl = "http://218.17.157.95:8088";
	// public static String rootUrl = "http://218.17.157.95:8088";
	// public static String mediaRootUrl = "http://218.17.157.95:34599";
	// public static String avatarRootUrl = "http://218.17.157.95:34588";

	public static final String HEAD_SMALL = "40x40";
	public static final String HEAD_MIDDLE = "64x64";
	public static final String HEAD_LARGE = "320x320";

	public static boolean vered = false;
	public static final String PACKAGE_PATH = ".ui.activity.";

	public static final class FormatType {
		public static final byte FRIEND_LIST_ACTION = 1;
		public static final byte FRIEND_LIST_ACTION_ADD = 2;
		public static final byte FRIEND_LIST_ACTION_DELETE = 3;
	}

	// public static class RequestCode {
	// public final static int SUCCESS = 0; /**成功*/
	// public final static int DISPATCH = -66; /**dispatch*/
	// public final static int PHONE_BINDED = -69; /**手機號碼已經被綁定*/
	// }

	public static final String ROOT = Environment.getExternalStorageDirectory()
			+ File.separator;
	public static final String UPDATE_APK = ROOT + "afmobi/apk/";
	public static final String IMAGE_CACHE = ROOT + "afmobi/palmchat/img/";
	public static final String VOICE_CACHE = ROOT + "afmobi/palmchat/voice/";
	public static final String AVATAR_CACHE = ROOT + "afmobi/palmchat/avatar/";
	public static final String OBJECT_CACHE = ROOT + "afmobi/palmchat/object/";
	public static final String DB_CACHE = ROOT + "afmobi/palmchat/db/";
	public static final String IMAGE2_CACHE = ROOT + "afmobi/palmchat/img2/";
	public static final String CAMERA_CACHE = ROOT + "afmobi/palmchat/camera/";
	public static final String LOGS_CACHE = ROOT + "afmobi/palmchat/logs/";;
	public static final String ACCOUNT = "account";
	public static final String LAUNCHER_MODE = "LAUNCHER_MODE";
	public static final String SETTING = "setting";
	public static final String DEVICE = "device";
	public static final String STATISTIC = "statistic";
	public static final String KEY_ATTR1 = "attr1";
	public static final String AUTO_START = "auto_start";
	public static final int AV = 0;
	public static final int AVATAR_COUNT = 4;

	// chatting room list icon
	public static final int ICON_DEFAULT = 0; // 使用預設圖標
	// public static final int ICON_FOOTBALL = 1;//足球圖標
	// public static final int ICON_FEMALE = 2; //女性人數大於等於30個人
	// public static final int ICON_HOT = 3; //在線人數超過100個人

	public static final int ICON_BOTH_SEXES = 1;// 两性性别图标
	public static final int ICON_SINGLE = 2;// 高跟鞋
	public static final int ICON_ROMANCE = 3;// 爱心
	public static final int ICON_MATRUE = 4;// 戒指
	public static final int ICON_CAMPUS = 5;// 信封
	public static final int ICON_LOVE100 = 6;// 玫瑰花
	public static final int ICON_FOOTBALL = 7;// 足球
	public static final String ANDROID = "Android";
	public static final String _OP = "_op";
	public static final String _UC = "_uc";

	public static String filename;
	static {
		mk(IMAGE2_CACHE);
		mk(UPDATE_APK);
		mk(IMAGE_CACHE);
		mk(VOICE_CACHE);
		mk(AVATAR_CACHE);
		mk(OBJECT_CACHE);
		mk(DB_CACHE);
		mk(CAMERA_CACHE);
		mk(LOGS_CACHE);
	}

	private static void mk(String file) {
		File f = new File(file);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	// 检查SD卡状态
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static boolean lowMemory;
	public static boolean terminate;
	public static String dcc = "86";
	public static String desAfid;
	public static boolean offlineStatusChange;
	public static boolean onlineStatus;

}
