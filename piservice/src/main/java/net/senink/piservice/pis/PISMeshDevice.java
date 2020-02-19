package net.senink.piservice.pis;

/**
 * Created by wensttu on 2016/7/7.
 */
public class PISMeshDevice extends PISNewDevice {
    public int deviceHash;                  /**< Mesh网络中该对象的设备Hash*/
    public int rssi;                        /**< 信号强度*/
    public int deviceId;                    /**< Mesh网络中该对象的设备ID*/

    public String mShortName;               /**< 显示出来的短名:一般为蓝牙芯片名字的前9个字节*/
    public String macAddr;                  /**< MAC地址*/
    public String uuid;
    public int timeLastSeen;                /**< 最近见到它的时间*/


    public void startAssociation(){

    }
    public void stopAssociation(){

    }
}
