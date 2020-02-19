package net.senink.seninkapp.ui.constant;

/**
 * 发送消息或者广播的常量
 * 
 * @author zhaojunfeng
 * 
 */
public class MessageModel {
	/******************* Message *************************/
	public final static int MSG_LIGHT_STATUS = 0x0001;
	// 得到信息
	public final static int MSG_GET_DEVICES = 0x0002;
	// 更新灯设置里面的某个灯所在组的信息
	public final static int MSG_ADDGROUP_ON_LIGHT = 0x0003;
	// 获取所有分组的信息
	public final static int MSG_SEND_ORDER = 0x0005;
	public final static int MSG_SEND_CANDLE = 0x000A;
	// 获取位置信息
	public final static int MSG_GET_LOCATIONS = 0x0006;
	// 获取闪烁信息成功
	public final static int MSG_GET_LOCATIONS_SUCCESS = 0x0008;
	// 获取led闪烁信息成功
	public final static int MSG_GET_LEDBLINK_SUCCESS = 0x0009;
	// wifi信息更新
	public final static int MSG_WIFIS_UPDATE = 0x00A0;
	// wifi设置超时
	public final static int MSG_WIFI_TIMEOUT = 0x00A1;
	// wifi设置超时最大时长
	public final static int MAX_TIME = 180000;

	public final static String TIMER_ID = "timer_id";
	// activity之间跳转的传值
	public final static String ACTIVITY_MODE = "activity_mode";
	public final static String ACTIVITY_VALUE = "key";
	public final static String PISBASE_KEYSTR = "keystring";
	public final static String PIPAREQUEST_OBJECT = "piparequest_object";
	public final static String PIPAREQUEST_DATA = "piparequest_data";
	// wifi设置界面返回到wifi界面之前是否修改过网关
	public final static String WIFI_REBOOT = "reboot";
	//位置
	public final static String LOCATION = "location";
	/****************** 广播的action ***********************/
	// 分组信息改变
	public final static String ACTION_DEVICE_GROUPS_CHANGE = "com.senink.seninkapp.DEVICE_GROUPS_CHANGE";
	// 改变位置
	public final static String ACTION_CHANGE_POSITION = "com.senink.seninkapp.CHANGE_POSITION";
}
