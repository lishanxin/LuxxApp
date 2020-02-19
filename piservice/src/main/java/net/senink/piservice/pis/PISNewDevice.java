package net.senink.piservice.pis;

/**
 * Created by wensttu on 2016/7/7.
 */

/**
 @enum
 @abstract PISMeshDevice配置过程中配置状态的变化，未配置时默认为idle
 @constant PISNewDevMcsBinding 正在绑定账户到MCS服务
 @constant PISNewDevMcsBinded 绑定账户到MCS服务成功
 @constant PISNewDevMcsBindFailed 绑定账户到MCS服务失败
 @constant PISNewDeviceIDGetting 正在获取设备唯一标识号
 @constant PISNewDeviceIDGot 已经获得设备唯一标识号
 @constant PISNewDeviceIDGetFailed 获取设备唯一标示号失败
 @constant PISNewDevAssociating 正在配置
 @constant PISNewDevAssociated 配置成功
 @constant PISNewDevAssociateFailed 配置失败
 @constant PISNewDevRemoving 删除中
 @constant PISNewDevRemoved 删除设备成功
 @constant PISNewDevRemoveFailed 删除设备失败
 @constant PISNewDeviceIDReleasing 正在释放设备唯一标识号
 @constant PISNewDeviceIDReleased 已经释放设备唯一标识号
 @constant PISNewDeviceIDReleaseFailed 释放设备唯一标示号失败
 @constant PISNewDevUnBinding 正在解除绑定
 @constant PISNewDevUnBinded 已经解除绑定
 @constant PISNewDevUnBindFailed 解除绑定失败
 */
public abstract class PISNewDevice {
    public static final int PISNewDevIdle = 0x1;                 /**< 空闲*/
    /**************************************************************************
     * 绑定设备
     **************************************************************************/
    /** Step 1. 绑定到MCS*/
    public static final int PISNewDevMcsBinding = 0x2;           /**< 正在绑定账户到MCS服务*/
    public static final int PISNewDevMcsBinded = 0x3;            /**< 绑定账户到MCS服务成功*/
    public static final int PISNewDevMcsBindFailed = 0x4;        /**< 绑定账户到MCS服务失败*/
    /** Step 2. 获取设备ID*/
    public static final int PISNewDeviceIDGetting = 0x5;         /**< 正在获取设备唯一标识号*/
    public static final int PISNewDeviceIDGot = 0x6;             /**< 已经获得设备唯一标识号*/
    public static final int PISNewDeviceIDGetFailed = 0x7;       /**< 获取设备唯一标示号失败*/
    /** Step 3. 通过CSR配置*/
    public static final int PISNewDevAssociating = 0x8;          /**< 正在配置*/
    public static final int PISNewDevAssociated = 0x9;           /**< 配置成功*/
    public static final int PISNewDevAssociateFailed = 0xa;      /**< 配置失败*/
    /**************************************************************************
     * 移除设备
     **************************************************************************/
    /** Step 1. 通过CSR移除*/
    public static final int PISNewDevRemoving = 0xb;             /**< 删除中*/
    public static final int PISNewDevRemoved = 0xc;              /**< 删除设备成功*/
    public static final int PISNewDevRemoveFailed = 0xd;         /**< 删除设备失败*/
    /** Step 2. 释放设备ID*/
    public static final int PISNewDeviceIDReleasing = 0xe;       /**< 正在释放设备唯一标识号*/
    public static final int PISNewDeviceIDReleased = 0xf;        /**< 已经释放设备唯一标识号*/
    public static final int PISNewDeviceIDReleaseFailed = 0x10;   /**< 释放设备唯一标示号失败*/
    /** Step 2. 通过MCS接触绑定*/
    public static final int PISNewDevUnBinding = 0x11;            /**< 正在解除绑定*/
    public static final int PISNewDevUnBinded = 0x12;             /**< 已经接触绑定*/
    public static final int PISNewDevUnBindFailed = 0x13;          /**< 接触绑定失败*/


    public static final int PISNewOptDataUnReady = 0x14;          /**< 数据未准备好*/
    public static final int PISNewOptDataReady = 0x15;            /**< 数据已准备好*/
    public static final int PISNewOptDataInvalid = 0x16;          /**< 数据无效 */
    public static final int PISNewOptSelected = 0x17;             /**< 已选中*/
    public static final int PISNewOptUnSelected = 0x18;           /**< 未选中*/

    public int timeout;
    public String macAddress;                   /**< 设备蓝牙芯片的Mac地址*/
    public String classID;                      /**< 设备对应的ClassID*/
    public String bindPasswd;                   /**< 设备发布时在服务端设定的入口密码*/

    public int associationStepsCompleted;  /**< 已经配置完的的步数*/
    public int associationStepsTotal;      /**< 配置的总步数*/

    public int associateStatus;      /**< 设备配置时的状态:UI监控状态用*/
    public int operateStatus;        /**< 当前的操作状态*/
    public int associateProgress;          /**< 配置完成的进度：0 ~ 10*/


    public abstract void startAssociation();
    public abstract void stopAssociation();

    public PISNewDevice(){
        macAddress = null;
        associationStepsCompleted = 0;
        associationStepsTotal = 0;
        associateStatus = PISNewDevIdle;
        operateStatus = PISNewOptDataUnReady;
    }

}
