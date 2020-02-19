package net.senink.seninkapp.ui.entity;

public class ConnectedPort {
	private String connPortName;
	private String totalData;
	private String dataSpeed;
	private int connPortIconID;

	/**
	 * @param title
	 * @param introduce
	 * @param picture
	 */
	public ConnectedPort(String connPortName, String totalData,
			String dataSpeed, int connPortIconID) {
		super();
		this.connPortName = connPortName;
		this.totalData = totalData;
		this.dataSpeed = dataSpeed;
		this.connPortIconID = connPortIconID;
	}

	public ConnectedPort() {
		super();
	}

	public String getConnPortName() {
		return connPortName;
	}

	public void setConnPortName(String connPortName) {
		this.connPortName = connPortName;
	}

	public String getTotalData() {
		return totalData;
	}

	public void setTotalData(String totalData) {
		this.totalData = totalData;
	}

	public String getDataSpeed() {
		return dataSpeed;
	}

	public void setDataSpeed(String dataSpeed) {
		this.dataSpeed = dataSpeed;
	}

	public int getConnPortIconID() {
		return connPortIconID;
	}

	public void setConnPortIconID(int connPortIconID) {
		this.connPortIconID = connPortIconID;
	}
}
