package net.senink.seninkapp.ui.entity;

import net.senink.seninkapp.ui.activity.AddBlueToothDeviceActivity.Appearance;
import net.senink.seninkapp.ui.util.CommonUtils;

/**
 * 添加蓝牙设备的信息类
 * 
 * @author zhaojunfeng
 * @date 2015-10-15
 */

public class BlueToothBubble implements Comparable<BlueToothBubble> {

	private static final long TIME_SCANINFO_VALID = 5 * 1000; // 5 secs

	public String uuid;
	public int rssi;
	public int uuidHash;
	public long timeStamp;
	public int ttl;
	public Appearance appearance;
	// 产品的类id
	private String classId;
	// 设备id
	public int deviceId;
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
	/**
	 * 设备的类型 1：LED灯 2：网关 3:RGB灯 4：遥控器 5:排插
	 */
	public int type = 0;

	public BlueToothBubble(String uuid, int rssi, int uuidHash, int ttl) {
		this.uuid = uuid;
		this.rssi = rssi;
		this.uuidHash = uuidHash;
		this.ttl = ttl;
		updated();
	}

	public void updated() {
		this.timeStamp = System.currentTimeMillis();
	}

	public void setClassId(String classId) {
		this.classId = classId;
		this.type = CommonUtils.getDeviceTypeFromClassId(classId);
	}

	public String getClassId() {
		return classId;
	}

	@Override
	public int compareTo(BlueToothBubble info) {
		// return
		if (this.rssi > info.rssi)
			return -1;
		else if (this.rssi < info.rssi)
			return 1;
		return 0;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * This method check if the timeStamp of the last update is still valid or
	 * not (time<TIME_SCANINFO_VALID).
	 * 
	 * @return true if the info is still valid
	 */
	public boolean isInfoValid() {
		return ((System.currentTimeMillis() - this.timeStamp) < TIME_SCANINFO_VALID);
	}

	public void setAppearance(Appearance newAppearance) {
		appearance = newAppearance;
	}
}
