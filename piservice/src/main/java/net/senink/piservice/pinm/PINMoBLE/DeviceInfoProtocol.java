/******************************************************************************
 *  Copyright (C) Cambridge Silicon Radio Limited 2015
 *
 *  This software is provided to the customer for evaluation
 *  purposes only and, as such early feedback on performance and operation
 *  is anticipated. The software source code is subject to change and
 *  not intended for production. Use of developmental release software is
 *  at the user's own risk. This software is provided "as is," and CSR
 *  cautions users to determine for themselves the suitability of using the
 *  beta release version of this software. CSR makes no warranty or
 *  representation whatsoever of merchantability or fitness of the product
 *  for any particular purpose or use. In no event shall CSR be liable for
 *  any consequential, incidental or special damages whatsoever arising out
 *  of the use of or inability to use this software, even if the user has
 *  advised CSR of the possibility of such damages.
 *
 ******************************************************************************/

package net.senink.piservice.pinm.PINMoBLE;

import com.csr.mesh.DataModelApi;

public class DeviceInfoProtocol {

	private static byte REQUEST_INFO_OPCODE = 0x01;
	// private static byte SET_INFO_OPCODE = 0x03;
	private static byte RESET_INFO_OPCODE = 0x04;

	/**
	 * Resets device info. Any further device info requests returns default
	 * string.
	 * 
	 * @param deviceId
	 */
	static public void resetDeviceInfo(int deviceId) {

		byte[] data = { RESET_INFO_OPCODE, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// we send the data using blocks so we don't need acknowledged packet.
		DataModelApi.sendData(deviceId, data, false);
	}

	/**
	 * Used to request device information. It would be delivered by streaming.
	 * 
	 * @param deviceId
	 */
	static public void requestDeviceInfo(int deviceId) {

		byte[] data = { REQUEST_INFO_OPCODE, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// we send the data using blocks so we don't need acknowledged packet.
		DataModelApi.sendData(deviceId, data, false);
	}

	/**
	 * Used to request device information. It would be delivered by streaming.
	 * 
	 * @param deviceId
	 * @param deviceInfo
	 */
	static public void setDeviceInfo(int deviceId, String deviceInfo) {

		/*
		 * 
		 * byte [] data = {SET_INFO_OPCODE,2,'h','o','\0',0,0,0,0,0}; // we send
		 * the data using blocks so we don't need acknowledged packet.
		 * DataModelApi.sendData(deviceId, data, false);
		 */
	}
}
