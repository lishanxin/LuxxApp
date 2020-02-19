package net.senink.seninkapp.ui.entity;

import java.io.Serializable;

/**
 * 连接BLE的deviceId和key的基类
 * 
 * @author zhaojunfeng
 * @date 2015-10-14
 * 
 */
public class BLEInfor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 错误信息
	public String errorInfor;
	/*
	 * 错误码 >0（绑定成功,且返回时bleid值） 0（无错误）， -1（参数错误），
	 * -2（系统错误/数据库错误/程序错误/底层接口异常；报-2错误请联系我）， -3（用户不存在）， -4（记录不存），
	 * -5（记录已存在，新增时唯一字段如已有相同关键词，则报-5）， -6（不允许的或无效的参数值，如超出范围的自定义或系统定义值），
	 * -7（pkey无效）， -1999（未知错误）
	 */
	public int errorCode;
	// 是否获取成功
	public boolean state;
	// 设备id
	public int deviceId;

	public String bleKey;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("state = ").append(state).append(", deviceId = ")
				.append(deviceId).append(", bleKey = ").append(bleKey);
		sb.append(", errorCode = ").append(errorCode).append(", errorInfor = ");
		if (errorInfor != null) {
			sb.append(errorInfor);
		} else {
			sb.append("null");
		}
		return sb.toString();
	}
}
