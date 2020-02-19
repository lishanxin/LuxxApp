package net.senink.seninkapp.ui.entity;

import java.io.Serializable;
import net.senink.seninkapp.ui.util.ByteUtilBigEndian;
import net.senink.seninkapp.ui.util.ByteUtilLittleEndian;

/**
 * 定时器任务的类
 * @author zhaojunfeng
 * @date 2015-12-31
 */


public class TimerTaskInfor implements Serializable {

		/**
		 * uid
		 */
		private static final long serialVersionUID = -8185533881197527737L;
		// 任务id
		public byte taskId;
		// mac
		public String macAddr;
		// 服务id
		public short serviceId;
		// 时间值
		public byte[] time;
		// 任务状态
		public boolean status;
		// 命令字
		public byte cmd;
		// 命令长度
		public short cmdLength;
		// 命令内容
		public byte[] cmdData;
		
		public TimerTaskInfor(byte id,String mac,short servId,byte[] times,byte cmd, byte[] cmdLength, byte[] content) {
			taskId = id;
			time = times;
			this.serviceId = servId;
			this.macAddr = mac;
			this.cmd = cmd;
			this.cmdLength = ByteUtilLittleEndian.byteArrToShort(cmdLength);
			cmdData = content;
		}
		
		public TimerTaskInfor(byte id, byte[] macs, byte[] serIds, byte[] times,
				byte mStatus, byte cmd, byte[] cmdLength, byte[] content) {
			taskId = id;
			macAddr = ByteUtilBigEndian.macAddrToStr(macs);
			serviceId = ByteUtilLittleEndian.byteArrToShort(serIds);
			time = times;
			if (mStatus == 0) {
				status = false;
			} else {
				status = true;
			}
			this.cmd = cmd;
			this.cmdLength = ByteUtilLittleEndian.byteArrToShort(cmdLength);
			cmdData = content;
		}

		public TimerTaskInfor(byte[] data) {
			setData(data);
		}

		public void setData(byte[] data) {
			// { 任务ID(1Byte)+ 设备MAC(8Byte)+ ServiceID(2Byte)+时间值(20Byte)
			// +状态(1Byte)+ 命令字(1Byte)+命令长度(2Byte)+命令内容(由命令长度确定)…}
			if (data != null && data.length >= 11) {
				int len = data.length;
				taskId = data[0];
				byte[] servIds = new byte[2];
				byte[] addresses = new byte[8];
				System.arraycopy(data, 1, addresses, 0, 8);
				System.arraycopy(data, 9, servIds, 0, 2);
				serviceId = ByteUtilLittleEndian.byteArrToShort(servIds);
				macAddr = ByteUtilBigEndian.ByteToMacString(addresses);
				if (len >= 31) {
					time = new byte[20];
					System.arraycopy(data, 11, time, 0, 20);
				}
				if (len >= 32) {
					status = (data[31] == 0 ? false : true);
				}
				if (len >= 33) {
					cmd = data[32];
				}
				if (len >= 35) {
					byte[] lens = new byte[2];
					System.arraycopy(data, 33, lens, 0, 2);
					cmdLength = ByteUtilLittleEndian.byteArrToShort(lens);
				}
				if (len > 35) {
					cmdData = new byte[len - 35];
					System.arraycopy(data, 35, cmdData, 0, cmdData.length);
				}
			} else {
				taskId = 0;
				macAddr = null;
				serviceId = 0;
				time = null;
				status = false;
				cmd = 0;
				cmdData = null;
				cmdLength = 0;
			}
		}
}
