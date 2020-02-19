package net.senink.seninkapp.ui.constant;

/**
 * 用于存常用路径的类
 * 
 * @author zhaojunfeng
 * @date 2015-10-30
 * 
 */
public class Constant {

	// sdcard中文件保存目录
	public static final String SAVE_PATH = "/senink/";
    //从重置界面跳转回来是否重置密码成功
	public static boolean isSuccessed = false;
	//发送命令超过n毫秒没有消息返回
	public static int TIMEOUT_TIME = 15000;
	//上谷的公司名称
	public final static String COMPANY_SOGOOD = "SO GOOD";
	//定时器编辑界面跳转的code值
	public final static int REQUEST_CODE_ADDTIMER = 2;
	//定时器条件设置界面跳转的code值
	public final static int REQUEST_CODE_TIMER_CONDITION = 3;
	//定时器设备与动作界面跳转的code值
	public final static int REQUEST_CODE_TIMER_ACTION = 4;
	//定时器设备与动作界面跳转到其他界面的code值
	public final static int REQUEST_CODE_TIMER_CMD = 5;
	//从rgb灯界面跳转到特效设置界面
	public final static int REQUEST_CODE_RGB_EFFECTS = 6;
	//从忘记密码界面跳转到重置密码界面
	public final static int REQUEST_CODE_RESET_PWD = 7;
	//从跑马灯界面跳转到跑马灯颜色设置界面
	public final static int REQUEST_CODE_MARQUEE_COLOR = 8;
	//从跑马灯界面跳转到跑马灯添加界面
	public final static int REQUEST_CODE_MARQUEE_LIGHT = 9;
	//从跑马灯界面跳转到跑马灯添加组界面
	public final static int REQUEST_CODE_MARQUEE_ADDGROUP = 10;
	//从插排详情界面跳转到修改名称界面
	public final static int REQUEST_CODE_SWITCH_MODIFYNAME = 11;
	//从智能鞋垫定时器列表界面跳转到定时器添加界面
	public final static int REQUEST_CODE_TIMERLIST_ADDTIMER = 12;
	/**
	 * 统计数据的恒量
	 */
	// 手机串号
	public static final String PHONE_ID = "p_phone_id";
	// 蓝牙通讯记录ID(自增量)
	public static final String BLE_COM_ID = "p_ble_com_id";
	// 请求连接时间
	public static final String CONNECT_TIME_START = "p_connet_time_start";
	// 请求连接结束时间
	public static final String CONNECT_TIME_END = "p_connet_time_end";
	// 连接结果(成功或失败)
	public static final String CONNECT_RESULT = "p_connet_result";
	// 失败原因
	public static final String CONNECT_FAILED_REASON = "p_connet_false_reason";
	// 请求连接耗时(无论成功或失败都要有值)
	public static final String CONNECT_COST_TIME = "p_connet_continue";
	// 断开时间
	public static final String DISCONNECT_TIME = "p_connet_off_time";
	// 断开原因(如失败则断开时间和断开原因则不需要填写)
	public static final String DISCONNECT_REASON = "p_connet_off_reason";
	// 本次通讯时长
	public static final String CONNECT_TIME = "p_connet_long";
	// 记录数据时间
	public static final String RECORD_TIME = "p_connet_time";
	// 发送PIPA数据包个数
	public static final String COUNT_ON_SEND_DATA = "p_send_pipa_packets_count";
	// 接收PIPA数据包个数
	public static final String COUNT_ON_RECIEVER_DATA = "p_receive_pipa_packets_count";
	// 发送PIPA数据总量
	public static final String SUM_ON_SEND_DATA = "p_send_pipa_packets_sum";
	// 接收PIPA数据总量
	public static final String SUM_ON_RECIEVE_DATA = "p_receive_pipa_packets_sum";
	
}
