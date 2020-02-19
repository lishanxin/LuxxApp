package net.senink.seninkapp.ui.entity;

import net.senink.piservice.struct.UserInfo;

import java.io.Serializable;

/**
 * 用于手机验证码返回数据的基类
 * 
 * @author zhaojunfeng
 * @date 2015-10-27
 */
public class MobCodeInfor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 手机号码
	public String telNumber;
	// 手机验证码
	public String mobCode;
	// 错误码
	public int errorCode;
	// 错误原因
	public String errorInfor;
	// 返回数据是否成功
	public boolean state;
    //时间
	public long time;
	//注册成功时返回的用户信息
	public UserInfo userInfor;
	public String infor;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("telNumber = ").append(telNumber).append(",mobCode = ")
				.append(mobCode);
		sb.append(",state = ").append(state).append(",errorCode = ")
				.append(errorCode);
		sb.append(",errorInfor = ").append(errorInfor);
		sb.append(",infor = ").append(infor);
		sb.append(",time = ").append(time);
		return sb.toString();
	}
}
