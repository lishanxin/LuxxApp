package net.senink.piservice.pinm.PINMoBLE.interfaces;

import android.os.DeadObjectException;

/**
 * Defines an interface that Fragments can use to communicate with the Activity.
 * 
 */
public interface BlueToothLightListener {

	/**
	 * Discover devices that are advertising to be associated.
	 */
	public void discoverDevices(boolean enabled, AssociationListener listener);

	/**
	 * Associate a device.
	 * 
	 * @param uuidHash
	 *            31-bit uuid hash of device.
	 * @param shortCode
	 *            Device's short code.
	 * @return True if deviceHash matches short code deviceHash, false if it
	 *         didn't match and association was cancelled.
	 */
	public boolean associateDevice(int uuidHash, String shortCode);

	/**
	 * Reset the currently selected device (remove association).
	 */
	public void removeDevice(RemovedListener listener);

	/**
	 * Remove device locally without sending a message to remove association on
	 * remote device.
	 * 
	 */
	public void removeDeviceLocally(RemovedListener listener);

	/**
	 * Set security settings.
	 * 
	 * @param networkKeyPhrase
	 *            The phrase used to generate the network key.
	 */
	public void setSecurity(String networkKeyPhrase);

	/**
	 * send an order.
	 * 
	 * @param deviceID
	 *            the id of device.
	 * @param data
	 *            the content of an order
	 */
	public void sendOrder(int deviceID, byte[] data) throws DeadObjectException;

	/**
	 * Control if a devices attention mechanism (e.g. flashing a light) is
	 * enabled or not.
	 * 
	 * @param enabled
	 *            True if attention should be enabled.
	 */
	public void setAttentionEnabled(boolean enabled);

	/**
	 * Get info from a device using the Data model.
	 */
	public void getDeviceData(DataListener listener);

	/**
	 * Enable or disable continuous scanning for BLE adverts (and hence mesh
	 * packets). If this is disabled then scanning for BLE adverts is only
	 * enabled for a fixed time period after a packet is sent.
	 * 
	 * @param ennabled
	 */
	public void setContinuousScanning(boolean ennabled);

	/**
	 * Ask to the Activity for posting a Runnable method in a handler.
	 * 
	 * @param checkScanInfoRunnable
	 */
	public void postRunnable(Runnable checkScanInfoRunnable);

	/**
	 * Ask to the Activity for remove a Runnable method from a handler.
	 * 
	 * @param checkScanInfoRunnable
	 */
	public void removeRunnable(Runnable checkScanInfoRunnable);

	/**
	 * Get the models supported and store in the database.
	 * 
	 * @param listener
	 */
	public void requestModelsSupported(InfoListener listener);

}
