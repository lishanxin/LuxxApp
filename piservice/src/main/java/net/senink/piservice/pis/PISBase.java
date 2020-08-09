package net.senink.piservice.pis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;
import net.senink.piservice.PISConstantDefine;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.IllegalBlockSizeException;

/**
 * Created by wensttu on 2016/6/17.
 */
public class PISBase implements Serializable {
    private static final String TAG = "PISBase";
    private static final long serialVersionUID = 5450846490156306667L;
    private static final int requestParallelCount = 5;
    private static final int alive_timeout_spen = 60 * 1000;

    /**
     * 所有的PISBASE所支持的公共命令、消息及事件
     */
    public final static byte PIS_CMD_SRVINFO_GET = (byte) 0x80; // 获取服务基本信息
    public final static byte PIS_CMD_SRVINFO_SET = (byte) 0x81; // 设置服务基本信息
    public final static byte PIS_CMD_GROUP_SET = (byte) 0x82; // 控制服务加入组
    public final static byte PIS_CMD_GROUP_GET = (byte) 0x83; // 获取服务属于哪个组
    public final static byte PIS_CMD_GROUP_UNSET = (byte) 0x84; // 控制服务脱离组
    public final static byte PIS_CMD_ALIAS_GET = (byte) 0x85; // 获取服务宏命令信息
    public final static byte PIS_CMD_ALIAS_SET = (byte) 0x86; // 设置服务宏命令信息
    public final static byte PIS_CMD_ALIAS_DEL = (byte) 0x87; // 删除服务宏命令信息
    public final static byte PIS_CMD_ALIAS_DO = (byte) 0x88; // 运行宏命令
    /**
     * PISBASE可订阅的消息
     */
    public final static byte PIS_MSG_INSIDE = (byte) 0x20;      // PIPA内部消息
    public final static byte PIS_MSG_SRVINFO = (byte) 0x21;     // 服务基本信息
    public final static byte PIS_MSG_ALIAS_INFO = (byte) 0x23;  // 服务宏命令信息
    /**
     * 服务状态枚举
     */
    public final static int SERVICE_STATUS_INVAILD = -1; //无效的服务
    public final static int SERVICE_STATUS_OFFLINE = 0; // 离线
    public final static int SERVICE_STATUS_INITING = 1; // 正在初始化
    public final static int SERVICE_STATUS_READY = 2;   // 服务器重置状态
    public final static int SERVICE_STATUS_ONLINE = 3;  // 在线状态

    /**
     * 服务类型
     */
    public final static int SERVICE_TYPE_UNKNOW = 0;
    public final static int SERVICE_TYPE_SYSTEM = 1;
    public final static int SERVICE_TYPE_GROUP = 2;
    public final static int SERVICE_TYPE_SERVICE = 3;
    public final static int SERVICE_TYPE_DEVICE = 4;
    public final static int SERVICE_TYPE_SMARTCELL = 5;


    public class PipaAliasInfo {
        int Alias = 0;
        byte Command = 0;
        byte DataLength = 0;
        byte[] Data;
    }

    public class PISCommandAckData {
        public byte Command;
        public byte[] data;

        public PipaRequestData rawRequestData;
    }


    /**
     * 成员字段
     */
    public int ServiceType;         //服务类型
    public int PMLevel;            // 当前电源等级，其范围为S0 - S4(关闭服务|深度睡眠|睡眠|低功耗运行|正常运行。


    protected byte[] mMacAddress;


    public ArrayList<PICommandProperty> Commands;// 集合类型，只读，所支持的命令属性的集合
    public ArrayList<PICommandProperty> Messages;// 集合类型，只读，所支持的消息属性的集合
    public transient ArrayList<PipaAliasInfo> AliasInfos;


    private PIAddress mPipaAddr = null;
    private PIServiceInfo mServiceInfo = null;

    private transient Object requestLock = new Object();
    private transient List<PipaRequest> mRequestExecutingList = new ArrayList<>();// 集合类型，存储了当前服务正在执行的命令或消息。
    private transient List<PipaRequest> mRequestReserveList = new ArrayList<>();    //后备列表，存放等待执行的PipaRequest
    private transient PISDevice mDeviceObject;

    public transient long LastAliveTime;    // 服务最后活跃时间
    public transient long LastExeTime;    // 服务最后执行时间
    public transient int PairID;            // 配对ID号

    private transient int Status;            // 取值来自PIS_STATUS类中定义的常量），服务状态。

    //保存所在的组ID
    private List<Integer> GroupIds;
    //保存所具有的服务地址（MAC+ServiceID)
    private List<ServiceInfoOfGroup> SigList = new ArrayList<ServiceInfoOfGroup>();
    /**
     * 构造函数
     */

    private PISBase(){
        super();
    }
    public PISBase(PIAddress addr, PIServiceInfo srvInfo, int srvType) {
        super();
        if (addr == null || srvInfo == null)
            throw new IllegalArgumentException("PipaAddress or ServiceInfo can't be null");

        Commands = new ArrayList<PICommandProperty>();
        Messages = new ArrayList<PICommandProperty>();
        AliasInfos = new ArrayList<PipaAliasInfo>();

        Commands.add(new PICommandProperty(PIS_CMD_SRVINFO_GET,
                "获取服务信息",
                "Get Service Infomation",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true,
                120));

        Commands.add(new PICommandProperty(PIS_CMD_SRVINFO_SET,
                "设置服务信息",
                "Set Service Infomation",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Commands.add(new PICommandProperty(PIS_CMD_GROUP_SET,
                "设置组",
                "控制服务加入组",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Commands.add(new PICommandProperty(PIS_CMD_GROUP_GET,
                "获取组",
                "获取服务属于哪个组",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Commands.add(new PICommandProperty(PIS_CMD_GROUP_UNSET,
                "删除组",
                "控制服务脱离组",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        Status = SERVICE_STATUS_OFFLINE;
        ServiceType = srvType;
        mServiceInfo = new PIServiceInfo(srvInfo.getBytes());
        mPipaAddr = new PIAddress(addr.getBytes());
        LastAliveTime = System.currentTimeMillis();
        GroupIds = new ArrayList<>();
    }

    public PICommandProperty getCommandProperty(int command){
        for(PICommandProperty prop : Commands){
            if (prop.PICommand == command){
                return prop;
            }
        }

        return null;
    }

    public synchronized void setStatus(int newStatus){
        if (newStatus > PISBase.SERVICE_STATUS_ONLINE ||
                newStatus < PISBase.SERVICE_STATUS_INVAILD)
            return;
        if (newStatus == Status)
            return;
        Status = newStatus;

        if (this.ServiceType == PISBase.SERVICE_TYPE_SERVICE && this.getDeviceObject() != null){
            this.getDeviceObject().setStatus(newStatus);
        }

        try {
            Context context = PISManager.getDefaultContext();
            if (context == null)
                return;
            Intent intent = new Intent(PISManager.PISERVICE_INFO_CHANGE_ACTION);
            intent.putExtra(PISManager.PIS_ExtraObject, getPISKeyString());
            context.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public synchronized int getStatus(){
        if (ServiceType == PISBase.SERVICE_TYPE_GROUP) {
            Status = SERVICE_STATUS_OFFLINE;
            List<PISBase> srvs = getGroupObjects();
            for(PISBase srv : srvs){
                if (srv.getStatus() > Status)
                    Status = srv.getStatus();
            }
        }
        return Status;
    }
    public int getT1() {
        return (mServiceInfo.T1 & 0xFF);
    }
    public int getT2(){
        return (mServiceInfo.T2 & 0xFF);
    }
    public int getIntegerType(){
        return (getT1())|((getT2())<<8);
    }
    public int getServiceId(){
        return mServiceInfo.getServiceID();
    }
    public int getPanId(){
        return mPipaAddr.getPanId();
    }
    public int getShortAddr(){
        return mPipaAddr.getShortAddr();
    }
    public int getLocation(){
        return mServiceInfo.Location;
    }
    public void setLocation(byte[] loc){
        mServiceInfo.Location = (byte)ByteUtilLittleEndian.getInt(loc);
    }
    public void setLocation(int loc){
        mServiceInfo.Location = (byte)(loc & 0xFF);
    }
    public String getName(){
        return mServiceInfo.Name;
    }
    public void setName(String strName){
        mServiceInfo.Name = strName;
    }

    public PISDevice getDeviceObject(){
        if (mDeviceObject != null)
            return mDeviceObject;

        List<PISDevice> devs = PISManager.getInstance().PIDevicesWithQuery(null,
                PISManager.EnumDevicesQueryBaseonNone);
        for (PISDevice dev : devs){
            if (dev.getPanId() == this.getPanId() &&
                    dev.getShortAddr() == this.getShortAddr() &&
                    ((dev.getServiceId())&0xFFE0) == ((this.getServiceId())&0xFFE0)){
                mDeviceObject = dev;
                break;
            }
        }
        return mDeviceObject;
    }
    // 得到对象在PISMap里的KEY
    public String getPISKeyString() {
        return String.format("%08x-%08x-%04x", getPanId(), getShortAddr(), getServiceId());
    }

    public static String getKeyString(int pid, int saddr, int sid){
        return String.format("%08x-%08x-%04x", pid, saddr, sid);
    }

    /**
     * 序列化对象输出函数
     * @param out 输出流
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException{
        out.defaultWriteObject();
//        out.writeObject(ServiceType);
//        switch (ServiceType){
//            case SERVICE_TYPE_GROUP:
//                //生成ID列表，并保存
//                break;
//            case SERVICE_TYPE_SYSTEM:
//                //系统类型无需保存，每次自动生成
//                break;
//            case SERVICE_TYPE_SMARTCELL:
//            case SERVICE_TYPE_SERVICE: {
//                //保存寄主设备的MAC地址
////                if (getDeviceObject() != null)
////                    mMacAddress = getDeviceObject().getMacByte();
//                if (mMacAddress == null)
//                    mMacAddress = new byte[8];
//                out.writeObject(mMacAddress);
//
//                //保存组信息
//            }
//            break;
//            case SERVICE_TYPE_DEVICE:
//                out.writeObject(mMacAddress);
//                break;
//        }
    }

    /**
     * 反序列化对象输入函数
     * @param in 输入流
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException{
        in.defaultReadObject();

        PairID = 0;
        Status = PISBase.SERVICE_STATUS_OFFLINE;
        mRequestExecutingList = new ArrayList<PipaRequest>();
        mRequestReserveList = new ArrayList<PipaRequest>();
        LastAliveTime = System.currentTimeMillis();
        requestLock = new Object();
        if (GroupIds == null)
            GroupIds = new ArrayList<>();
        if (SigList == null)
            SigList = new ArrayList<>();
    }

    public PISCommandAckData buildAckData(byte[] ackBytes, PipaRequestData rawData){
        if (ackBytes.length < 3)
            return null;

        byte[] lenBytes = new byte[2];
        System.arraycopy(ackBytes, 1, lenBytes, 0, 2);
        int len = ByteUtilLittleEndian.getInt(lenBytes);

        if (ackBytes.length < (len+3))
            return null;

        PISCommandAckData result = new PISCommandAckData();
        result.Command = ackBytes[0];
        if (len > 1000) {
            throw new IllegalArgumentException("超过预期的内存分配要求[" + len + "]");
        }
        result.data = new byte[len];
        System.arraycopy(ackBytes, 3, result.data, 0, len);
        result.rawRequestData = rawData;

        return result;
    }

    /**
     * 私有方法
     */
    public void pairing() {
        LogUtils.i(TAG, "PISBase[" + getPISKeyString() + "] pairing");
        try {
            PisInterface.pisPairing(mServiceInfo, mPipaAddr);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 组请求方法
     * 如何处理需要ack或者重发的组请求？
     * 为每一个服务构建一个虚假的PIPA_REQUEST对象，使其被用来验证应答包
     * 该虚假对象有几个特点，1. 具有虚假标志，不会被真正发送；2. 变更其回调BLOCK，在该BLOCK中验证是否所有的调用被应答
     * 真正的请求仍然以组的方式发送，而不是以单个服务的方式发送。
     */
    private int groupRequest(PipaRequest req) {

        //1. 查找到该组下的所有服务
        List<PISBase> groupServices = this.getGroupObjects(); // this.Services;
        if (groupServices == null || groupServices.size() == 0)
            return PipaRequest.REQUEST_SEND_FAILED;
        if (!req.NeedAck){
            return this.serviceRequest(req);
        }

        //2. 构建虚假请求对象
        int hr = this.serviceRequest(req);
        if (hr != PipaRequest.REQUEST_SEND_SUCCESS)
            return hr;

        ArrayList<PipaRequest>  groupReqs = new ArrayList<PipaRequest>();
        for (PISBase srv : groupServices){
            if (srv.getStatus() != SERVICE_STATUS_ONLINE)
                continue;
            PipaRequest srvReq = new PipaRequest(srv, req);
            srvReq.isVirtual = true;
            srvReq.RetryTimes = req.RetryTimes;
            srvReq.originalReq = req;
            srvReq.NeedAck = true;

            srvReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {
                }

                @Override
                public void onRequestResult(PipaRequest srvReq) {
                    try {
                        if (srvReq.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            return;
                        ArrayList<PipaRequest> gReqs = srvReq.originalReq.groupRequests;
                        if (gReqs.contains(srvReq)) {
                            for (PipaRequest r : gReqs) {
                                synchronized (r.object.requestLock) {
                                    r.object.mRequestExecutingList.remove(r);
                                }
                            }
                            gReqs.clear();
                            synchronized (requestLock) {
                                if (mRequestExecutingList.contains(srvReq.originalReq))
                                    mRequestExecutingList.remove(srvReq.originalReq);
                            }
                            LogUtils.i(TAG, "remove ------------> groupRequest() onRequestResult");
                            srvReq.originalReq.report_finished((Activity) PISManager.getDefaultActivityContext());
                        }
                    }catch (Exception e){
                        srvReq.errorCode = PipaRequest.REQUEST_RESULT_ERROR_OBJECT;
                        srvReq.originalReq.report_finished((Activity) PISManager.getDefaultActivityContext());
                    }
                }
            });

            if (PipaRequest.REQUEST_SEND_SUCCESS == srv.serviceRequest(srvReq))
                groupReqs.add(srvReq);
        }
        if (groupReqs.size() == 0) {
            req.NeedAck = false;
        }
        req.groupRequests = groupReqs;
        return hr;
    }

    private int serviceRequest(@NonNull PipaRequest req){
        int ret = PipaRequest.REQUEST_SEND_FAILED;
        req.object = this;
        PICommandProperty vaildProp = null;

        List<PICommandProperty> PropertyList;

        if (req.Type == PipaRequest.REQUEST_TYPE.SUBSCRIBE)
            PropertyList = Messages;
        else
            PropertyList = Commands;

        for (PICommandProperty prop : PropertyList) {
            if (prop.PICommand == req.RequestData.Command) {
                vaildProp = prop;
                break;
            }
        }
        if (null == vaildProp)
            return ret;

        if (req.isVirtual){
            synchronized (requestLock) {
                req.ExecuteTime = System.currentTimeMillis();
                mRequestExecutingList.add(req);
            }
            this.LastExeTime = System.currentTimeMillis();
            return PipaRequest.REQUEST_SEND_SUCCESS;
        }
        if (!req.NeedAck)
            return req.execute();

        /*
         * 如果该请求已经被发送，则不再重新发送，而只是有赖于重发机制（必要的时候需要比较命令参数，如果
         * 命令参数不同，则该请求放入请求排队序列，在当前命令执行完成（或者超时）后，再得到执行）
         */
        synchronized (requestLock) {
            for (PipaRequest checkReq : mRequestExecutingList) {
                if (checkReq.equals(req)) {
                    return ret;
                }
            }
        }
        req.object = this;
        //req.Type = PipaRequest.REQUEST_TYPE.REQUEST;

        mRequestReserveList.add(req);

        if (((this.getDeviceObject() != null &&
                this.getDeviceObject().getStatus() == SERVICE_STATUS_ONLINE) ||
                this.getT1() == 0x0 ||
                this.ServiceType == PISBase.SERVICE_TYPE_GROUP) &&
                mRequestExecutingList.size() < requestParallelCount) {
            //检查_pisExecutingRequests中不能有同样Command存在
            boolean isSameCommand = false;
            PipaRequestData waitReqData = req.RequestData;
            synchronized (requestLock) {
                for (PipaRequest chkReq : mRequestExecutingList) {
                    PipaRequestData chkReqData = chkReq.RequestData;
                    if (waitReqData.Command == chkReqData.Command) {
                        isSameCommand = true;
                        break;
                    }
                }
            }
            if (!isSameCommand) {
                synchronized (requestLock) {
                    mRequestReserveList.remove(req);
                    mRequestExecutingList.add(req);
                }
                ret = req.execute();
                this.LastExeTime = req.ExecuteTime;
            }
        } else
            ret = PipaRequest.REQUEST_SEND_SUCCESS;

        return ret;
    }
    /**
     * @param req 是否把未返回ack数据的请求加入集合里面
     * @return 0-执行成功， 1-等待执行，其它为错误码
     */
    public int request(PipaRequest req) {
        if (req.RequestData == null)
            throw new IllegalArgumentException("RequestData can't be null");

        if (req.PairID == this.PairID){
            if (this.ServiceType == SERVICE_TYPE_GROUP)
                return this.groupRequest(req);
            else
                return this.serviceRequest(req);
        }else{
            PISManager pm = PISManager.getInstance();
            //查找根据pairID查找对应的服务
            List<PISBase> allSrv = PISManager.getInstance().AllObjects();
            for (PISBase targetObj : allSrv){
                if (targetObj.PairID == req.PairID)
                    return targetObj.request(req);
            }
        }
        return PipaRequest.REQUEST_SEND_FAILED;
    }

    public int subscribe(byte msgType, boolean isSubscribe) {
        return request(
                new PipaRequest(this, PipaRequest.REQUEST_TYPE.SUBSCRIBE, isSubscribe,
                        new PipaRequestData(msgType, 0, null), true)
        );
    }

    public synchronized PipaRequest getExecutingRequest(byte cmd) {
        PipaRequest req = null;
        synchronized (requestLock) {
            for (int i = 0; i < mRequestExecutingList.size(); i++) {
                req = mRequestExecutingList.get(i);
                if (req.RequestData.Command == cmd)
                    break;
                else
                    req = null;
            }
        }
        return req;
    }

    // mark - private method
    public void RequestClear(){
        synchronized (requestLock) {
            mRequestExecutingList.clear();
            mRequestReserveList.clear();
        }
    }

    public void reportErrorForInvaildObject(){

        for (PipaRequest req : mRequestExecutingList) {
            req.errorCode = PipaRequest.REQUEST_RESULT_ERROR_CANCEL;
            req.report_finished((Activity) PISManager.getDefaultActivityContext());
        }
        LogUtils.i(TAG, "reportErrorForInvaildObject()");
        synchronized (requestLock) {
            mRequestExecutingList.clear();
        }
        for (PipaRequest req : mRequestReserveList) {
            req.errorCode = PipaRequest.REQUEST_RESULT_ERROR_CANCEL;
            req.report_finished((Activity) PISManager.getDefaultActivityContext());
        }
        synchronized (requestLock) {
            mRequestReserveList.clear();
        }

    }

    private void checkPIServiceMessage(PipaRequest req) {
        checkPIServiceCommand(req);
    }



    private void checkPIServiceCommand(PipaRequest req) {
        switch (req.Status) {
            case DOING: {   //执行中，判断有否超时
                if (System.currentTimeMillis() - req.ExecuteTime > req.ExecuteTimeout) {
                    synchronized (requestLock) {
                        mRequestExecutingList.remove(req);
                    }
                    LogUtils.i(TAG, "remove ------------> checkPIServiceCommand() Timeout");

                    if (req.RetryTimes-- > 0 && req.object.ServiceType != PISBase.SERVICE_TYPE_GROUP) { //重新排队以获取执行的机会
                        synchronized (requestLock) {
                            mRequestReserveList.add(req);
                        }
                        req.Status = PipaRequest.REQUEST_STATUS.TIMEOUT;
                    } else {
                        req.Status = PipaRequest.REQUEST_STATUS.INVALID;
                        req.errorCode = (-2);    //超时
                        req.report_finished((Activity)PISManager.getDefaultActivityContext());
                    }
                }
            }
            break;
            case DONE: {
                synchronized (requestLock) {
                    if (mRequestExecutingList.contains(req))
                        mRequestExecutingList.remove(req);
                }
                LogUtils.i(TAG, "remove ------------> checkPIServiceCommand() DONE");
            }
            break;
            case TIMEOUT:  //已超时，判断是否需要重新执行
            case IDLE:     //等待执行，判断当前是否有正在执行的操作，如果没有，则执行该操作
                req.execute();
                this.LastExeTime = req.ExecuteTime;
                break;
            case INVALID:  //无效的操作，则将该操作删除
                synchronized (requestLock) {
                    if (mRequestExecutingList.contains(req))
                        mRequestExecutingList.remove(req);
                }
                LogUtils.i(TAG, "remove ------------> checkPIServiceCommand() INVALID");
            default:
                break;
        }
    }

    private void processRequestAck(PISCommandAckData ackData, int result) {
        for (PipaRequest req : mRequestExecutingList) {
            PipaRequestData reqData = req.RequestData;

            if ((reqData.Command & 0xFF) == (ackData.Command & 0xFF)) {
                if (result == 0)
                    req.Status = PipaRequest.REQUEST_STATUS.DONE;
                else {
                    req.errorCode = (-1);
                    req.Status = PipaRequest.REQUEST_STATUS.INVALID;
                }
                req.report_finished((Activity) PISManager.getDefaultActivityContext());

            }
        }
    }

    private void processRequestForRawData(PISCommandAckData ackData) {
        synchronized (requestLock) {
            for (PipaRequest req : mRequestExecutingList) {
                PipaRequestData reqData = req.RequestData;
                if (reqData.Command == ackData.Command) {
                    ackData.rawRequestData = req.RequestData;
                    req.responsePara = ackData.data;
                    break;
                }
            }
        }
    }

    public void onPerProcess(int msg, int hPara, Object lPara) {

        switch (msg) {
            //涓嶆敮鎸佺殑娑堟伅
            case PISConstantDefine.PIPA_EVENT_DISCOVERY:
            case PISConstantDefine.PIPA_EVENT_ALIVE:
            case PISConstantDefine.PIPA_EVENT_COMMAND:
            case PISConstantDefine.PIPA_EVENT_PAIR:
                break;
            case PISConstantDefine.PIPA_EVENT_PAIR_ACK:
//                if (this.PairID != 0 && Status == SERVICE_STATUS_INITING) {
//                    this.Initialization();
//                }
                break;
            case PISConstantDefine.PIPA_EVENT_SS:
            case PISConstantDefine.PIPA_EVENT_SS_ACK:
                break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE:
            case PISConstantDefine.PIPA_EVENT_CMD_ACK:
                this.processRequestForRawData((PISCommandAckData) lPara);
                break;
            default:
                LogUtils.w(TAG, "Unkonw Message Type:" + msg);
                break;
        }

        LastAliveTime = System.currentTimeMillis();
        this.onProcess(msg, hPara, lPara);
    }

    private void processServiceInfo(byte[] ackData) {
        byte[] serviceID = new byte[2];
        System.arraycopy(ackData, 0, serviceID, 0, 2);
        int mSrvId = ByteUtilLittleEndian.getInt(serviceID);
        byte mT1 = ackData[2];
        byte mT2 = ackData[3];
        if (mT2 != mServiceInfo.T2 || mT1 != mServiceInfo.T1 || mSrvId != mServiceInfo.getServiceID())
            return;
        mServiceInfo.Location = ackData[4];

        byte[] nameByteArray = new byte[16];
        System.arraycopy(ackData, 5, nameByteArray, 0, 16);
        mServiceInfo.Name = ByteUtilBigEndian.ByteToString(nameByteArray);
        if (null != mServiceInfo.Name && !mServiceInfo.Name.isEmpty()) {
            mServiceInfo.Name = mServiceInfo.Name.trim();
        }
    }

    private void processAliasInfo(byte[] ackData) {
        PipaAliasInfo ai = new PipaAliasInfo();
        byte[] bArrAlias = new byte[2];
        // 读出存储在前面的int型数据
        System.arraycopy(ackData, 0, bArrAlias, 0, 2);
        ai.Alias = ByteUtilBigEndian.byteArrToShort(bArrAlias);
        ai.Command = ackData[2];
        ai.DataLength = ackData[3];
        ai.Data = new byte[ai.DataLength];
        System.arraycopy(ackData, 4, ai.Data, 0, ai.DataLength);
        AliasInfos.add(ai);
    }

    private void processGroupInfo(byte[] ackData) {
        GroupIds.clear();
        if (ackData.length == 0)
            return;
        int groupCnt = ackData[0];
        for (int i = 0; i < groupCnt; i++) {
            byte[] byteGid = new byte[2];
            System.arraycopy(ackData, i * 2 + 1, byteGid, 0, 2);
            int gid = ByteUtilLittleEndian.byteArrToShort(byteGid);
            GroupIds.add(gid);
        }
    }

    protected  void onProcess(int msg, int hPara, Object lPara) {

        switch (msg) {
            //涓嶆敮鎸佺殑娑堟伅
            case PISConstantDefine.PIPA_EVENT_DISCOVERY:
            case PISConstantDefine.PIPA_EVENT_ALIVE:
            case PISConstantDefine.PIPA_EVENT_COMMAND:
            case PISConstantDefine.PIPA_EVENT_PAIR:
            case PISConstantDefine.PIPA_EVENT_PAIR_ACK:
            case PISConstantDefine.PIPA_EVENT_SS:
                break;
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData) lPara;
                switch (hPara) {
                    case PIS_MSG_SRVINFO:
                        processServiceInfo(ackData.data);
                        break;
                    case PIS_MSG_ALIAS_INFO:
                        processAliasInfo(ackData.data);
                        break;
                }
            }
            break;
            case PISConstantDefine.PIPA_EVENT_SS_ACK:
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData) lPara;
                switch (ackData.Command) {
                    case PIS_CMD_SRVINFO_GET: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            this.processServiceInfo(ackData.data);
                        }
                    }
                    break;
                    case PIS_CMD_SRVINFO_SET: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            this.processServiceInfo(ackData.rawRequestData.Data);
                        }
                    }
                    break;
                    case PIS_CMD_GROUP_SET: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            int gid = ByteUtilLittleEndian.byteArrToShort(ackData.rawRequestData.Data);
                            if (!GroupIds.contains(gid))
                                GroupIds.add(gid);
                        }
                        break;
                    }
                    case PIS_CMD_GROUP_GET: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            processGroupInfo(ackData.data);
                        }
                        break;
                    }
                    case PIS_CMD_GROUP_UNSET: {
                        if (hPara == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            int gid = ByteUtilLittleEndian.byteArrToShort(ackData.rawRequestData.Data);
                            if (GroupIds.contains(gid))
                                GroupIds.remove(Integer.valueOf(gid));
                        }
                        break;
                    }
                    case PIS_CMD_ALIAS_GET:
                        break;
                    case PIS_CMD_ALIAS_SET:
                        processAliasInfo(ackData.rawRequestData.Data);
                        break;
                    case PIS_CMD_ALIAS_DEL:
                        break;
                    case PIS_CMD_ALIAS_DO:

                        break;
                }
                processRequestAck(ackData, hPara);
            }
            break;
            default:
                LogUtils.w(TAG, "Unkonw Message Type:" + msg);
                break;
        }
    }


    /**
     * 公有方法
     */
    public void Initialization() {
        if (this.ServiceType == PISBase.SERVICE_TYPE_SYSTEM ||
                this.ServiceType == PISBase.SERVICE_TYPE_GROUP)
            this.Status = PISBase.SERVICE_STATUS_ONLINE;
        else if (ServiceType == SERVICE_TYPE_SERVICE){
            if (this.getDeviceObject() == null)
                return;
            if (this.mMacAddress == null)
                return;
            this.Status = PISBase.SERVICE_STATUS_READY;
        }

    }

    public void Uninitialized() {

    }

    public void checkPiserviceRequest() {
        ArrayList<PipaRequest> executingList;
        ArrayList<PipaRequest> reserveList;
        synchronized (requestLock) {
            executingList = new ArrayList<PipaRequest>(mRequestExecutingList);
            reserveList = new ArrayList<PipaRequest>(mRequestReserveList);
        }
        if (executingList.size() == 0){
            this.LastAliveTime = System.currentTimeMillis();
        }
        if ((this.LastExeTime - this.LastAliveTime) > alive_timeout_spen) {
            LogUtils.w(TAG, String.format("[0x%x,0x%x]判断为离线:服务响应超时[%s]",
                    mServiceInfo.T1,
                    mServiceInfo.T2,
                    this.getPISKeyString()));
            this.Status = PISBase.SERVICE_STATUS_OFFLINE;
            this.reportErrorForInvaildObject();
//            if (this.ServiceType == SERVICE_TYPE_GROUP)
//                this.reportErrorForInvaildObject();
            return;
        }

        for (PipaRequest req : executingList) {
            switch (req.Type) {
                case REQUEST:
                    this.checkPIServiceCommand(req);
                    break;
                case SUBSCRIBE:
                    this.checkPIServiceMessage(req);
                    break;
                default:
                    break;
            }
        }
        for (PipaRequest req : reserveList){
            if (executingList.size() >= PipaRequest.Executing_Max)
                break;
            //检查_pisExecutingRequests中不能有同样Command存在
            boolean isSameCommand = false;
            PipaRequestData waitReqData = req.RequestData;
            synchronized (requestLock) {
                for (PipaRequest chkReq : mRequestExecutingList) {
                    PipaRequestData chkReqData = chkReq.RequestData;
                    if (waitReqData.Command == chkReqData.Command) {
                        isSameCommand = true;
                        break;
                    }
                }
            }
            if (!isSameCommand) {
                synchronized (requestLock) {
                    mRequestReserveList.remove(req);
                    mRequestExecutingList.add(req);
                }
                req.execute();
                this.LastExeTime = req.ExecuteTime;
            }
        }
    }


    public PipaRequest setAliasInfo(PipaAliasInfo aliasInfo) {
        byte[] reqBuffer = new byte[aliasInfo.DataLength + 4];
        byte[] aliasByte = ByteUtilLittleEndian.getBytes((short) aliasInfo.Alias);

        System.arraycopy(aliasByte, 0, reqBuffer, 0, 2);
        reqBuffer[2] = aliasInfo.Command;
        reqBuffer[3] = aliasInfo.DataLength;
        System.arraycopy(aliasInfo.Data, 0, reqBuffer, 4, aliasInfo.DataLength);

        return new PipaRequest(this, PIS_CMD_ALIAS_SET, reqBuffer, aliasInfo.DataLength + 4, true);
    }

    public PipaRequest getAliasInfo(int aliasNumber) {
        byte[] reqBuffer = ByteUtilLittleEndian.getBytes((short) aliasNumber);

        return new PipaRequest(this, PIS_CMD_ALIAS_GET, reqBuffer, 2, true);
    }

    public PipaRequest updatePISInfo() {
        return new PipaRequest(this, PIS_CMD_SRVINFO_GET, null, 0, true);
    }

    public PipaRequest commitPISInfo(PIServiceInfo srvInfo){
        if (srvInfo == null)
            return null;
        return new PipaRequest(this, PIS_CMD_SRVINFO_SET, srvInfo.getBytes(), srvInfo.getBytes().length, true);
    }


    /**
     * 组相关, 以下方法仅在该对象类型为GROUP时有效
     */
    private class ServiceInfoOfGroup{
        public byte[] DeviceMac;
        public int ServiceId;

        public ServiceInfoOfGroup(byte[] mac, int srvid){
            DeviceMac = mac;
            ServiceId = srvid;
        }
        public boolean equals(ServiceInfoOfGroup sig){
            if (DeviceMac.equals(sig.DeviceMac) && ServiceId == sig.ServiceId)
                return true;
            else
                return false;
        }
    }

    public int getGroupId(){
        return this.getShortAddr();
    }

    public void addGroupObject(byte[] mac, int srvid){
        if (ServiceType != SERVICE_TYPE_GROUP)
            return;
        ServiceInfoOfGroup sig = new ServiceInfoOfGroup(mac, srvid);
        if (!SigList.contains(sig))
            SigList.add(sig);
    }
    public void removeGroupObject(byte[] mac, int srvid){
        if (ServiceType != SERVICE_TYPE_GROUP)
            return;
        ServiceInfoOfGroup sig = new ServiceInfoOfGroup(mac, srvid);
        if (SigList.contains(sig))
            SigList.remove(sig);
    }

    /**
     * 以下方法在该对象类型为SERVICE时有效
     */

    public PipaRequest updateGroupInfo(){
        return new PipaRequest(this, PIS_CMD_GROUP_GET, null, 0, true);
    }

    public PipaRequest addToGroup(PISBase group){
        if(ServiceType == SERVICE_TYPE_GROUP)
            return null;

        byte[] groupByte = new byte[2];
        groupByte[0] = (byte)(group.getGroupId() & 0xFF);
        groupByte[1] = (byte)((group.getGroupId() & 0xFF00) >> 8);

        return new PipaRequest(this, PIS_CMD_GROUP_SET, groupByte, 2, true);
    }

    public PipaRequest removeFromGroup(PISBase group){
//        if(ServiceType == SERVICE_TYPE_GROUP)
//            return null;
//
        byte[] groupByte = new byte[2];
        groupByte[0] = (byte)(group.getGroupId() & 0xFF);
        groupByte[1] = (byte)((group.getGroupId() & 0xFF00) >> 8);

        return new PipaRequest(this, PIS_CMD_GROUP_UNSET, groupByte, 2, true);
    }

    public boolean containsGroupObject(PISBase obj){
        if (ServiceType == SERVICE_TYPE_GROUP){
            if (obj.GroupIds.contains(this.getGroupId()))
                return true;
            ServiceInfoOfGroup sig = new ServiceInfoOfGroup(obj.mMacAddress, obj.getServiceId());
            if (SigList.contains(sig))
                return true;
        }else{
            if (GroupIds.contains(obj.getShortAddr()))
                return true;
        }
        return false;
    }

    private transient PISBase parentObject = null;
    public PISBase getGroupParent(){
        Class parent = null;
        List<PISBase> srvs = getGroupObjects();
        PIServiceInfo srvInfo = null;
        PIAddress addr = null;
        if (srvs == null || srvs.size() == 0){
            return null;
        }

        if ((getT2() != 0xFF || getT1() != 0xFF) && parentObject == null) {
            addr = new PIAddress(this.getPanId(), this.getShortAddr());
            srvInfo = new PIServiceInfo((byte) this.getT1(), (byte) this.getT2(), this.getServiceId());
            parentObject = PISManager.CreateServiceObject(addr, srvInfo);
            parentObject.ServiceType = PISBase.SERVICE_TYPE_GROUP;
            parentObject.PairID = PairID;
        }

        for (PISBase srv : srvs){
            Class<?> srvCls = srv.getClass();
            if (parent == null) {
                parent = srvCls;
                srvInfo = new PIServiceInfo((byte)srv.getT1(), (byte)srv.getT2(), this.getServiceId());
            }
            else{
                if (srvCls == parent){
                    continue;
                }
                if (srvCls.isAssignableFrom(parent)){
                    parent = srvCls;
                    srvInfo = new PIServiceInfo((byte)srv.getT1(), (byte)srv.getT2(), this.getServiceId());
                }
            }
        }

        if (parentObject != null && parentObject.getClass() == parent)
            return parentObject;

        if (srvInfo == null)
            return parentObject;

        Constructor pisCon = null;
        try {
            addr = new PIAddress(this.getPanId(), this.getShortAddr());

            srvInfo.Name = this.getName();
            srvInfo.Location = (byte)(this.getLocation() & 0xFF);
            if (parent != null)
                pisCon = parent.getConstructor(PIAddress.class, PIServiceInfo.class);
            if (pisCon != null)
                parentObject = (PISBase) pisCon.newInstance(addr, srvInfo);

            parentObject.ServiceType = PISBase.SERVICE_TYPE_GROUP;
            parentObject.PairID = PairID;
            mServiceInfo.T1 = srvInfo.T1;
            mServiceInfo.T2 = srvInfo.T2;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return parentObject;
    }

    public List<PISBase> getGroupObjects(){
        List<PISBase> allObjs;
        List<PISBase> resultArray = new ArrayList<PISBase>();

        if (ServiceType == SERVICE_TYPE_GROUP){
//            allObjs = PISManager.getInstance().PIServicesWithQuery(
//                    this.getT1() | ((this.getT2()&0xFF) << 8),
//                    PISManager.EnumServicesQueryBaseonType);
            allObjs = PISManager.getInstance().AllServices();
        }else{
//            allObjs = PISManager.getInstance().PIGroupsWithQuery(
//                    this.getT1() | ((this.getT2()&0xFF) << 8),
//                    PISManager.EnumGroupsQueryBaseonType);
            allObjs = PISManager.getInstance().AllGroups();
        }
        for (PISBase obj : allObjs){
            if (this.containsGroupObject(obj))
                resultArray.add(obj);
        }
        return resultArray;
    }



}
