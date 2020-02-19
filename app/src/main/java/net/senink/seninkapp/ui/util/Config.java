package net.senink.seninkapp.ui.util;

public class Config {
	public final static String HTTP_HEADER = "http://";// Senink服务器IP地址
	// private static final String mHostIp = "115.29.11.191";//TouchHome服务器IP地址
	// private static final String mHostIp = "192.168.1.82";// test服务器IP地址
//	public final static String SERVER_NAME = "121.199.24.18";// Senink服务器IP地址
	public static String SERVER_NAME = "115.29.11.191";// 服务器IP地址
	public final static String HOST_IP = "115.29.11.191";// 服务器IP地址
//	public static String SERVER_NAME = "pipa.senink.net";// 服务器IP地址
//	public final static String HOST_IP = "121.199.24.18";// 服务器IP地址
//	public final static String SERVER_NAME = "pipa.senink.net";// 域名,云端服务器所在地
	public final static String URL_BASE = "https://pipa.senink.net";// 域名,云端服务器所在地
	public final static String USER_INFO_URL = "/senink/cn/member_app.php?";// 用户信息操作接口
	public final static String POWER_STATICS_URL = "/senink/senink_home_sdk/sta tistics/getData.php?/";// 功率数据统计接口
	public final static String POWER_ANALYZE_URL = "/senink/Senink_home_SDK/sam ple_code/gateway.php?";// 功率分析数据接口
	public final static String AURVE_GRAPH_URL = "/senink/cn/line_app.php?";// 功率数据图表曲线图接口
	public final static String BAR_GRAPH_URL = "/senink/cn/line1_app.php?";// 功率数据图表柱状图接口
	public final static String PIE_CHART_URL = "/senink/cn/line2_app.php?";// 功率数据图表饼状图接口
	public final static String ROOMTYPE_URL = "/Senink_home_SDK/sample_code/roomType.php?";
	//功率数据统计
	public final static String POWER_DATA1_URL = "/webapi/getData.php?op=list&user=";
	//功率数据统计
	public final static String POWER_DATA2_URL = "/webapi/getData2.php?op=list&user="; 
	//绑定蓝牙设备
	public final static String BIND_BLUETOOTH_DEVICE = "/webapi/bleInfoNew.php?op=single";
	//获取deviceid和key
	public final static String BIND_BLUETOOTH_INFOR = "/webapi/bleInfoNew.php?op=list";
	//已经绑定设备列表
	public static final String DEVICES_LIST_URL = "/webapi/termInfoList.php?op=list";
	//获取手机验证码的url
	public static final String TELEPHONE_MOBCODE_URL = "/webapi/member_app.php?op=getmobcode&";
	//检测版本升级的url
	public static final String UPGRADE_APK_URL = "/webapi/member_app.php?op=updateapp";
	//忘记密码url
	public final static String FORGET_PASSWORD_URL = "/webapi/member_app.php?op=forgetpass&email=";
	//通过手机验证码修改密码的url
	public static final String MODIFY_PASSWORD_ONCODE_URL = "/webapi/member_app.php?op=editPass&";
	//用手机号码注册的url
	public static final String REGISTER_MOBILE_USER_URL = "/webapi/member_app.php?op=mobreg&";
	//修改密码中获取验证码后设置密码的url
	public static final String MODIFY_PASSWORD_SETPWD_URL = "/webapi/member_app.php?op=mobeditpwd&";
	//用户注册
	public static final String REGISTER_USER_URL = "/webapi/member_app.php?op=add&";
	//位置的集合
	public static final String GET_LOCATION="/webapi/roomType.php?op=list&";
	//用户信息
	public final static String GET_USER_INFO = "/webapi/member_app.php?op=list&user="; 
	//更新头像
	public static final String MODIFY_USER_LOGO = "/webapi/member_app.php?op=edit";
	//修改密码
	public static final String MODIFY_PASSWORD = "/webapi/member_app.php?op=editPass&";
	//智能元list
	public final static String SMART_CELL = "/webapi/smartCell.php?op=list&user="; 
	//增加房间类型
	public static final String ADD_ROOMTYPE= "/webapi/roomType.php?op=add";
	//增加设备名称
	public static final String ADD_DEVICE_NAME= "/webapi/pisname.php?op=add";
	//获取类型列表
	public static final String GET_TYPE= "/webapi/service.php?op=list";
	//获取设备名称
	public static final String GET_DEVICE_NAMES= "/webapi/pisname.php?op=list";
	//注册url
	public final static String REGISTER_URL = "/webapi/member_app.php?";
	//设置详情url
	public static final String SHE_BEI_DETAIL = "/webapi/termInfo.php?";
	//授权子账户url
	public static final String YAO_QING_CHILD_ACCU = "/webapi/member_app.php?op=edit&targetType=insert";
    //获取授权子列表的url
	public static final String GET_CHILD_LIST= "/webapi/member_app.php?op=listChild";
	//获取授权父列表的url
	public static final String GET_PARENT_LIST= "/webapi/member_app.php?op=listParent";
	//授权url
	public static final String DEL_SHOUQUAN= "/webapi/member_app.php?op=edit&targetType=";
	//根据mac地址获取这个设备的Jason字符串
	public final static String GET_SCAN_DEVICE_INFO = "/webapi/get_dimension.php?"; 
	//修改用户信息
	public final static String EDIT_USER_INFOR_URL = "/webapi/member_app.php?op=edit&";
}
