package net.senink.seninkapp.ui.entity;

public class RegisterDeviceInfo {
	private String registerDeviceName;
	private boolean registerDeviceStatus;
	private int registerDeviceIconID;

	/**
	 * @param registerDeviceName
	 * @param registerDeviceStatus
	 * @param registerDeviceIconID
	 */
	public RegisterDeviceInfo(String registerDeviceName,
			boolean registerDeviceStatus, int registerDeviceIconID) {
		super();
		this.registerDeviceName = registerDeviceName;
		this.registerDeviceStatus = registerDeviceStatus;
		this.registerDeviceIconID = registerDeviceIconID;
	}

	public RegisterDeviceInfo() {
		super();
	}

	public String getRegisterDeviceName() {
		return registerDeviceName;
	}

	public void setRegisterDeviceName(String registerDeviceName) {
		this.registerDeviceName = registerDeviceName;
	}

	public boolean getRegisterDeviceStatus() {
		return registerDeviceStatus;
	}

	public void setRegisterDeviceStatus(boolean registerDeviceStatus) {
		this.registerDeviceStatus = registerDeviceStatus;
	}

	public int getRegisterDeviceIconID() {
		return registerDeviceIconID;
	}

	public void setRegisterDeviceIconID(int registerDeviceIconID) {
		this.registerDeviceIconID = registerDeviceIconID;
	}

}
