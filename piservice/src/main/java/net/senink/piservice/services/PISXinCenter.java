package net.senink.piservice.services;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wensttu on 2016/6/23.
 */
public class PISXinCenter extends PISBase {

    private static final String TAG = "PISXinCenter";
    private static final int APINFO_BYTE_LENGTH = 52;

    public static final byte PIS_CMD_AP_SSID_KEY_GET = (byte)0x90;//获取网桥当前的SSID和KEY
    public static final byte PIS_CMD_AP_SSID_KEY_SET = (byte)0x91;//修改当前网桥SSID和KEY
    public static final byte PIS_CMD_AP_INFO_GET     = (byte)0x92;//获取网桥的IP地址、子网掩码、网关地址
    public static final byte PIS_CMD_GW_BUFFER_GET   = (byte)0x93;//获取网桥缓存功能是否开启
    public static final byte PIS_CMD_GW_BUFFER_SET   = (byte)0x94;//设置是否开启网桥缓存功能
    public static final byte PIS_CMD_BLUE_CONFI_GET  = (byte)0x95;/*当手机配置好网桥蓝牙之后，手机上显示网桥服务之时，应该主动给网桥发送此命令，
                                                   以便网桥的WiFi模块获知Bluetooth模块已经建立好连接*/
    public static final byte PIS_CMD_AP_NAME_GET     = (byte)0x96;/*获取当前网桥所在区域的所有AP信息，每一个AP信息占有34bytes，最多获取前10个新号最强的AP信息。
                                                   DataStructe : 32bytes:AP名 ； 1Byte:加密方式; 1Bytes:信号强度*/
    public static final byte PIS_CMD_GW_NETWORK_TYPE_GET = (byte)0x97;/* 获取网关连接的方式 0 : 有线连接 ； 1 : 无线连接*/
    public static final byte PIS_CMD_AP_COUNT_GET    = (byte)0x98;/**获取当前桥接器的所扫描到的AP数量*/

    public static final byte PIS_MSG_AP_CONFIG_STATE        = (byte)0x30;//设置SSID+Key后，AP的状态消息
    public static final byte PIS_MSG_BLUELIGHT_ONLINE_STATE = (byte)0x31;//检测灯的服务在不在线

    public PISXinCenter(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_AP_SSID_KEY_GET,
                "获取网桥当前的SSID和KEY",
                "获取网桥当前的SSID和KEY",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_AP_SSID_KEY_SET,
                "修改当前网桥SSID和KEY",
                "修改当前网桥SSID和KEY",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_AP_INFO_GET,
                "获取网络状态",
                "获取网桥的IP地址、子网掩码、网关地址",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_GW_BUFFER_GET,
                "缓存",
                "获取网桥缓存功能是否开启",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_GW_BUFFER_SET,
                "设置缓存",
                "设置是否开启网桥缓存功能",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_BLUE_CONFI_GET,
                "蓝牙是否就绪",
                "当手机配置好网桥蓝牙之后，手机上显示网桥服务之时，应该主动给网桥发送此命令，以便网桥的WiFi模块获知Bluetooth模块已经建立好连接",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_AP_NAME_GET,
                "获取AP",
                "获取当前网桥所在区域的所有AP信息",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_GW_NETWORK_TYPE_GET,
                "连接方式",
                "获取网关连接的方式 0 : 有线连接 ； 1 : 无线连接",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_AP_COUNT_GET,
                "AP数量",
                "获取当前桥接器的所扫描到的AP数量",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Messages.add(new PICommandProperty(PIS_MSG_AP_CONFIG_STATE,
                "AP状态",
                "设置SSID+Key后，AP的状态消息",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
        Messages.add(new PICommandProperty(PIS_MSG_BLUELIGHT_ONLINE_STATE,
                "状态消息",
                "检测灯的服务在不在线",
                PipaRequest.REQUEST_TYPE.SUBSCRIBE,
                true));
    }

    private boolean CacheEnabled = false;
    public boolean getCacheStatus(){
        return CacheEnabled;
    }
    public void setCacheStatus(boolean isEnable){
        CacheEnabled = isEnable;
    }
    private byte[] ApInfoBytes;

    public void setApInfo(byte[] apBytes){
        if (apBytes == null || apBytes.length != APINFO_BYTE_LENGTH)
            return;
        ApInfoBytes = apBytes;
    }

    public String getSsid(){
        if (ApInfoBytes == null || ApInfoBytes.length != APINFO_BYTE_LENGTH)
            return null;

        byte[] ssidBytes = new byte[32];
        System.arraycopy(ApInfoBytes, 0, ssidBytes, 0, 32);

        return ByteUtilLittleEndian.getString(ssidBytes);
    }

    public String getApKey(){
        if (ApInfoBytes == null || ApInfoBytes.length != APINFO_BYTE_LENGTH)
            return null;

        byte[] keyBytes = new byte[20];
        System.arraycopy(ApInfoBytes, 32, keyBytes, 0, 20);

        return ByteUtilLittleEndian.getString(keyBytes);
    }

    private byte[] APInfoEx;
    public void setApInfoEx(byte[] apBytes){
        if (apBytes == null)
            return;
        APInfoEx = apBytes;
    }
    public int getApLinkType(){
        if (APInfoEx == null)
            return 0xFF;
        return APInfoEx[0];
    }
    public String getApName(){
        if (APInfoEx == null)
            return "";

        String apName;
        if (APInfoEx[0] == 0){
            apName = "Ethernet";
        }else{
            byte[] nameBytes = new byte[32];
            System.arraycopy(APInfoEx, 1, nameBytes, 0, 32);
            apName = ByteUtilLittleEndian.getString(nameBytes);
        }
        return apName;
    }

    private byte[] APIpInfo;
    public void setApIPInfo(byte[] ipBytes){
        if (ipBytes == null || ipBytes.length != 12)
            return;
        APIpInfo = ipBytes;
    }

    public int getIpAddress(){
        if (APIpInfo == null || APIpInfo.length != 12)
            return 0;
        byte[] ipBytes = new byte[4];
        System.arraycopy(APIpInfo, 0, ipBytes, 0, 4);

        return ByteUtilLittleEndian.getInt(ipBytes);
    }
    public int getIpMask(){
        if (APIpInfo == null || APIpInfo.length != 12)
            return 0;
        byte[] ipBytes = new byte[4];
        System.arraycopy(APIpInfo, 4, ipBytes, 0, 4);

        return ByteUtilLittleEndian.getInt(ipBytes);
    }
    public int getIpGateway(){
        if (APIpInfo == null || APIpInfo.length != 12)
            return 0;
        byte[] ipBytes = new byte[4];
        System.arraycopy(APIpInfo, 8, ipBytes, 0, 4);

        return ByteUtilLittleEndian.getInt(ipBytes);
    }

    private int ApCount = 0;
    public int getApCount(){
        return ApCount;
    }
    public void setApCount(byte[] cntBytes){
        if (cntBytes == null || cntBytes.length != 1)
            return;
        ApCount = cntBytes[0];
        if (ApCount > 0)
            ApBytesList = new ArrayList<byte[]>(ApCount);
    }

    private List<byte[]> ApBytesList = null;
    public String getApName(int idx){
        if (ApBytesList == null || idx < 0 || idx >= ApBytesList.size())
            return null;
        byte[] apBytes = ApBytesList.get(idx);
        byte[] nameBytes = new byte[19];
        System.arraycopy(apBytes, 1, nameBytes, 0, 19);

        return ByteUtilLittleEndian.getString(nameBytes);
    }
    public int getRssi(int idx){
        if (ApBytesList == null || idx < 0 || idx >= ApBytesList.size())
            return -100;
        byte[] apBytes = ApBytesList.get(idx);
        return 18 * ((apBytes[0] & (0x3 << 4))>>4);
    }
    public int getEncyptMode(int idx){
        if (ApBytesList == null || idx < 0 || idx >= ApBytesList.size())
            return 0xFF;
        byte[] apBytes = ApBytesList.get(idx);
        return (apBytes[0] & (0x1<<7))>>7;
    }

    private int ApStatus;
    public int getApStatus(){
        return ApStatus;
    }

    /**
     * PIPA消息处理
     */

    private void processGetAPBriefs(byte[] apBytes){
        if (apBytes.length < 20 || ApBytesList == null)
            return;
        ApBytesList.add(apBytes);
    }
    private void processNetworkTypeInfo(byte[] nwBytes){
        if (nwBytes == null)
            return;
        APInfoEx = nwBytes;
    }
    private void processMsgApConfigState(byte[] stBytes){
        if (stBytes == null || stBytes.length != 1)
            return;
        if (stBytes[0] < 0 || stBytes[0] > 3)
            return;
        ApStatus = stBytes[0];
    }
    private void processMsgBlueLightOnlineState(byte[] stBytes){
        if (stBytes == null || stBytes.length != 6)
            return;

        byte[] sbytes = new byte[2];

        System.arraycopy(stBytes, 0, sbytes, 0, 2);
        int saddr = ByteUtilLittleEndian.getInt(sbytes);
        System.arraycopy(stBytes, 2, sbytes, 0, 2);
        int panid = ByteUtilLittleEndian.getInt(sbytes);
        System.arraycopy(stBytes, 4, sbytes, 0, 2);
        int srvid = ByteUtilLittleEndian.getInt(sbytes);

        PISBase pis = PISManager.getInstance().getPISObject(PISBase.getKeyString(panid, saddr, srvid));

        if (pis != null){
            pis.setStatus(PISBase.SERVICE_STATUS_OFFLINE);
        }
    }
    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_AP_SSID_KEY_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null ||
                                ackData.data.length != APINFO_BYTE_LENGTH)
                            break;
                        this.setApInfo(ackData.data);
                        break;
                    }
                    case PIS_CMD_AP_SSID_KEY_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null ||
                                ackData.rawRequestData.Data.length != APINFO_BYTE_LENGTH)
                            break;
                        this.setApInfo(ackData.rawRequestData.Data);
                        break;
                    }
                    case PIS_CMD_AP_INFO_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setApIPInfo(ackData.data);
                        break;
                    }
                    case PIS_CMD_GW_BUFFER_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setCacheStatus(ackData.data[0]==1);
                        break;
                    }
                    case PIS_CMD_GW_BUFFER_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null ||
                                ackData.rawRequestData.Data == null)
                            break;
                        this.setCacheStatus(ackData.rawRequestData.Data[0] == 1);
                        break;
                    }
                    case PIS_CMD_BLUE_CONFI_GET:{
                        LogUtils.i(TAG, "Notify WiFi(9331) that bluetooth had completed configuration");
                        break;
                    }
                    case PIS_CMD_AP_COUNT_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.setApCount(ackData.data);
                        break;
                    }
                    case PIS_CMD_AP_NAME_GET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.processGetAPBriefs(ackData.data);
                        break;
                    }
                    case PIS_CMD_GW_NETWORK_TYPE_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.processNetworkTypeInfo(ackData.data);
                        break;
                    }
                    default:
                        break;
                }
            }
            break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_MSG_AP_CONFIG_STATE:{
                        this.processMsgApConfigState(ackData.data);
                        break;
                    }
                    case PIS_MSG_BLUELIGHT_ONLINE_STATE:{
                        this.processMsgBlueLightOnlineState(ackData.data);
                        break;
                    }
                    default:
                        break;
                }
            }
            break;
            default:
                break;
        }
        super.onProcess(msg, hPara, lPara);
    }

    /**
     * 公有方法
     */

    /**
     * 获取SSID和KEY
     * @return
     */
    public PipaRequest updateAPSSIDAndKey(){
        return new PipaRequest(this, PIS_CMD_AP_SSID_KEY_GET, null, 0, true);
    }

    /**
     * 修改SSID及Key
     * @param ssid  SSID
     * @param key   Key
     * @return PipaRequest
     */
    public PipaRequest commitAPSSIDAndKey(String ssid, String key){
        byte[] dataBytes = new byte[64];

        System.arraycopy(ssid.getBytes(), 0, dataBytes, 0, (ssid.getBytes().length>32)?32:ssid.getBytes().length);
        System.arraycopy(key.getBytes(), 0, dataBytes, 32, (key.getBytes().length>20)?20:key.getBytes().length);

        return new PipaRequest(this, PIS_CMD_AP_SSID_KEY_SET, dataBytes, 52, true);
    }

    /**
     * 获取AP信息
     * @return
     */
    public PipaRequest updateAPInfos(){
        return new PipaRequest(this, PIS_CMD_AP_INFO_GET, null, 0, true);
    }

    /**
     * 获取缓存使能状态
     * @return
     */
    public PipaRequest updateAPCacheStatus(){
        return new PipaRequest(this, PIS_CMD_GW_BUFFER_GET, null, 0, true);
    }

    /**
     * 设置缓存使能状态
     * @param isEnable 是否使能缓存
     * @return
     */
    public PipaRequest commitAPCacheStatus(boolean isEnable){
        byte[] dataBytes = new byte[1];

        dataBytes[0] = (byte)(isEnable?1:0);

        return new PipaRequest(this, PIS_CMD_GW_BUFFER_SET, dataBytes, 1, true);
    }

    /**
     * 通知网关，蓝牙已经完成配置
     * @return
     */
    public PipaRequest commitBTConfigCompleted(){
        return new PipaRequest(this, PIS_CMD_BLUE_CONFI_GET, null, 0, true);
    }

    /**
     * 获取周边的WIFI接入点的数量
     * @return
     */
    public PipaRequest updateAroundAPCount(){
        return new PipaRequest(this, PIS_CMD_AP_COUNT_GET, null, 0, true);
    }

    /**
     * 获取指定接入点的信息
     * @param idx 接入点的索引
     * @return
     */
    public PipaRequest updateAPBriefs(int idx){
        byte[] dataBytes = new byte[1];

        dataBytes[0] = (byte)idx;
        return new PipaRequest(this, PIS_CMD_AP_NAME_GET, dataBytes, 1, true);
    }

    /**
     * 获取连接类型
     * @return
     */
    public PipaRequest updateLinkType(){
        return new PipaRequest(this, PIS_CMD_GW_NETWORK_TYPE_GET, null, 0, true);
    }


}
