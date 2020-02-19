package net.senink.piservice.pis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.struct.BindDevInfo;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PISMCSManager extends PISBase implements Serializable {
    private static final long serialVersionUID = 7863303627006425800L;
    private static final String TAG = "PISMCSManager";

    public static final byte PIS_CMD_MCM_SET_ACCESSINFO = (byte)0x90;
    public static final byte PIS_CMD_MCM_BIND_USER = (byte)0x91;
    public static final byte PIS_CMD_MCM_SCANDEVSVC = (byte)0x98;          //获取classid下的服务  参数：Classid(6bytes)
    public static final byte PIS_CMD_MCM_DEVBINDGET = (byte)0x99;          //获取绑定的设备列表
    public static final byte PIS_CMD_MCM_DEVBINDALL_GET = (byte)0x9A;      //获取绑定的设备列表 带MAC ClassID BleID 数据返回
    public static final byte PIS_CMD_MCM_SUBDEVBINDALL_GET = (byte)0x9B;   //获取指定用户的绑定表
    public static final byte PIS_CMD_MCM_DEVBIND = (byte)0x9C;             //设备Mac地址(8bytes)、绑定网关下的设备

    /**
     * 设备Mac地址(8bytes) 、解绑设备 、{0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff}解绑所有设备
     */
    public static final byte PIS_CMD_MCM_DEVUNBIND = (byte)0x9D;
    public static final byte PIS_CMD_MCM_SUBDEVBINDGET = (byte)0x9e;

    /**
     * MCM对账户下智能元的操作
     */
    public static final byte PIS_CMD_MCM_SMART_NEW = (byte)0xA2;//新增智能元
    public static final byte PIS_CMD_MCM_SMART_DEL = (byte)0xA3;//删除智能元
    /**
     * MCM增加对账户下的组信息控制
     */
    public static final byte PIS_CMD_MCM_GET_GROUP_NUM = (byte)0xA5;   //获取当前多少组
    public static final byte PIS_CMD_MCM_ADD_GROUP = (byte)0xA6;       //增加组
    public static final byte PIS_CMD_MCM_DEL_GROUP = (byte)0xA7;       //删除组
    public static final byte PIS_CMD_MCM_GET_GROUP_SRV = (byte)0xA8;   //获取组里分别有哪些设备
    public static final byte PIS_CMD_MCM_ADD_GROUP_SRV = (byte)0xA9;   //添加某设备的某服务到指定组
    public static final byte PIS_CMD_MCM_DEL_GROUP_SRV = (byte)0xAA;   //指定组删除某设备的某服务
    public static final byte PIS_CMD_MCM_CHG_GROUP = (byte)0xAB;       //修改指定组的信息

    public static final byte PIS_CMD_MCM_STORE_SET = (byte)0xAC;       //数据存储，不存在则插入，存在则修改
    public static final byte PIS_CMD_MCM_STORE_GET = (byte)0xAD;       //获取数据
    public static final byte PIS_CMD_MCM_STORE_GETN = (byte)0xAE;      //获取指定路径的指定数量的数据
    public static final byte PIS_CMD_MCM_STORE_DEL = (byte)0xAF;       //删除数据，若表达式为空则删除整个树
    public static final byte PIS_CMD_MCM_STORE_TREE_SET = (byte)0xB0;  //存储json树，如果不存在则插入，存在则修改
    public static final byte PIS_CMD_MCM_STORE_TREE_GET = (byte)0xB1;  //获取Json树
    public static final byte PIS_CMD_MCM_STORE_COUNT = (byte)0xB2;     //获取指定路径下的记录数量
    public static final byte PIS_CMD_MCM_STORE_KEYS = (byte)0xB3;      //获取指定路径下的键名集合
    public static final byte PIS_CMD_MCM_STORE_ISEXITS = (byte)0xB4;   //确认指定路径下的键名是否存在
    public static final byte PIS_CMD_MCM_STORE_TREE_ADD = (byte)0xB5;  //存储Json树

    /**
     * 添加数据
     *
     * @param 数据类型（16Byte）
     * @param 设备MAC（16Byte）
     * @param serviceID 4Byte）
     * @param 数据长度（4Byte）
     * @param 数据 （长度由数据长度指出）
     * @return ACK
     */
    public static final byte PIS_CMD_MCM_STEPTABLE_ADD = (byte)0xBB;
    /**
     * 按时间及数量查询
     *
     * @param 数据类型(16bytes)
     * @param 设备MAC(16bytes)
     * @param serviceID (4Byte)
     * @param 起始时间(32bytes,形如"2015-11-25T00:00:00.000+0800")
     * @param 数量(2bytes)
     * @param 单位时间类型(2bytes,0,1,2,3,4)
     */
    public static final byte PIS_CMD_MCM_STEPTABLE_GETN = (byte)0xBC;
    /**
     * 按时间段查询
     *
     * @param 数据类型(16bytes)
     * @param 设备MAC(16bytes)
     * @param serviceID(4Bytes)
     * @param 起始时间(32bytes,形如"2015-11-25T00:00:00.000+0800")
     * @param 结束时间(32bytes,)
     * @param 单位时间类型(2bytes)
     */
    public static final byte PIS_CMD_MCM_STEPTABLE_GET = (byte)0xBD;


    /**
     * 支持的消息
     */
    public static final byte PIS_MSG_MCM_GROUPADD = (byte)0x34;    //添加组
    public static final byte PIS_MSG_MCM_GROUPDEL = (byte)0x35;    //删除组
    public static final byte PIS_MSG_MCM_GROUPSRVADD = (byte)0x36; //添加组服务
    public static final byte PIS_MSG_MCM_GROUPSRVDEL = (byte)0x37; //删除组服务

    //绑定设备的MAC地址列表
    public ArrayList<String> mBindedDeviceMacList;
    //classid所对应的设备类型上的服务信息
    public HashMap<String, ArrayList<PIServiceInfo>> mServiceListByDeviceClass;

    /**
     * 构造函数
     * @param addr
     * @param srvInfo
     */
    public PISMCSManager(PIAddress addr, PIServiceInfo srvInfo) {
        super(addr, srvInfo, SERVICE_TYPE_SYSTEM);

        Commands.add(new PICommandProperty(PIS_CMD_MCM_SCANDEVSVC,
                "获取服务信息", "获取classid下的服务", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEVBIND,
                "绑定设备", "将一个设备绑定在当前用户下", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEVUNBIND,
                "解绑设备", "当设备从当前用户下解绑", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEVBINDGET,
                "获取绑定设备列表", "获取当前用户下所有绑定的设备列表", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEVBINDALL_GET,
                "获取绑定设备列表包含ClassID", "获取当前用户下所有绑定的设备列表包含ClassID", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_SUBDEVBINDGET,
                "获取指定用户的绑定表", "获取指定用户的绑定表", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_SUBDEVBINDALL_GET,
                "获取指定用户的绑定表包含ClassID", "获取指定用户的绑定表包含ClassID", PipaRequest.REQUEST_TYPE.REQUEST, true));
        //智能元操作
        Commands.add(new PICommandProperty(PIS_CMD_MCM_SMART_NEW,
                "安装智能元", "安装智能元", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_SMART_DEL,
                "删除智能元", "删除智能元", PipaRequest.REQUEST_TYPE.REQUEST, true));
        //组操作
        Commands.add(new PICommandProperty(PIS_CMD_MCM_GET_GROUP_NUM,
                "获取组信息", "当前用户的所有组信息", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_ADD_GROUP,
                "新增组", "给用户增加组", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEL_GROUP,
                "删除组", "删除用户下的某个组", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_GET_GROUP_SRV,
                "获取组内容", "获取组中的服务", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_ADD_GROUP_SRV,
                "增加服务到组", "增加服务到指定组", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_DEL_GROUP_SRV,
                "删除组中的服务", "删除指定组中的某服务", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_CHG_GROUP,
                "修改组信息", "修改指定组的相关信息", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_STEPTABLE_ADD,
                "计步存储", "计步存储", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_STEPTABLE_GETN,
                "获取计步数1", "根据时间和数量获取计步数", PipaRequest.REQUEST_TYPE.REQUEST, true));
        Commands.add(new PICommandProperty(PIS_CMD_MCM_STEPTABLE_GET,
                "获取计步数2", "根据时间段获取计步数", PipaRequest.REQUEST_TYPE.REQUEST, true));


        Messages.add(new PICommandProperty(PIS_MSG_MCM_GROUPADD,
                "组被新增", "添加组", PipaRequest.REQUEST_TYPE.SUBSCRIBE, true));
        Messages.add(new PICommandProperty(PIS_MSG_MCM_GROUPDEL,
                "组被删除", "组被删除", PipaRequest.REQUEST_TYPE.SUBSCRIBE, true));
        Messages.add(new PICommandProperty(PIS_MSG_MCM_GROUPSRVADD,
                "组增加服务", "添加服务到组", PipaRequest.REQUEST_TYPE.SUBSCRIBE, true));
        Messages.add(new PICommandProperty(PIS_MSG_MCM_GROUPSRVDEL,
                "组被删除服务", "删除组中的服务", PipaRequest.REQUEST_TYPE.SUBSCRIBE, true));

        mBindedDeviceMacList = new ArrayList<String>();
        initServiceList();
    }

    /**
     * 初始化服务信息列表
     */
    private void initServiceList(){
        mServiceListByDeviceClass = new HashMap<String, ArrayList<PIServiceInfo>>();
        //这里增加默认的服务信息
    }

    @Override
    public void Initialization() {
        super.Initialization();

        if (PISManager.getInstance().hasCloudConnect()) {
            PipaRequest req = this.updateBindedDevices();
            req.RetryTimes = 10;
            this.request(req);
        }
    }

    @Override
    public void Uninitialized() {
        super.Uninitialized();
    }


    /**
     * Command & Message Data process function
     */

    private void buildServiceWithDevice(PISDevice dev, ArrayList<PIServiceInfo> srvInfos){
        if (dev == null || srvInfos == null || srvInfos.size() == 0)
            return;

        for (PIServiceInfo si : srvInfos) {
            int mSrvId = si.getServiceID();
            byte mT1 = si.T1;
            byte mT2 = si.T2;
            byte mLocation = (byte)(si.Location & 0xFF);
            String mName = si.Name;
            int mShortAddr = dev.getShortAddr();
            int mPanID = dev.getPanId();

            String keyString = PISBase.getKeyString(mPanID, mShortAddr, mSrvId | (dev.getServiceId() & 0xFFE0));
            //构建PISDevice对象，并添加至PISManager对象中
            PISBase pis = PISManager.getInstance().getPISObject(keyString);
            if (pis != null && pis.getT1() == mT1 && pis.getT2() == mT2) {
                continue;
            }
            PISBase obj = PISManager.CreateServiceObject(
                    new PIAddress(mPanID, mShortAddr),
                    new PIServiceInfo(mT1, mT2, mSrvId));

            if (obj != null) {
                PISManager.getInstance().putPISObject(obj);
                obj.setStatus(PISBase.SERVICE_STATUS_INITING);

                LogUtils.i(TAG, "ADD new PISBase:" + keyString);
            }
        }
    }

    private void checkPIServiceOfDeviceForClassid(String classid, ArrayList<PIServiceInfo> srvInfos) {
        List<PISDevice> devs = PISManager.getInstance().AllDevices();
        LogUtils.i("PISMCSManager",
                "begin to check classid:" + classid + " with device:" + devs.size());
        try {
            for (int i = 0; i < devs.size(); i++) {
                PISDevice dev = devs.get(i);
                String devClassid = dev.getClassString();
                int srvSize = dev.getPIServices().size();

                if (devClassid.compareTo(classid) == 0 && srvSize == 0) {
                    LogUtils.i("PISMCSManager", "The device:" + dev.getPISKeyString());
                    buildServiceWithDevice(dev, srvInfos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtils.i("PISMCSManager", "end check");
    }

    private void processServiceOfClass(byte[] ackData) {
        byte[] countBytes = new byte[4];
        byte[] classidBytes = new byte[6];
        int count = 0;
        System.arraycopy(ackData, 0, countBytes, 0, 4);
        count = ByteUtilLittleEndian.byteArrToShort(countBytes);// .byteArrToInt(countBytes);
        System.arraycopy(ackData, 4 + count * 21, classidBytes, 0, 6);
        String strClsid = ByteUtilBigEndian.classIdToStr(classidBytes);

        synchronized (mServiceListByDeviceClass) {
            ArrayList<PIServiceInfo> srvs = mServiceListByDeviceClass.get(strClsid);
            if (srvs != null) {
                srvs.clear();
            } else {
                srvs = new ArrayList<PIServiceInfo>();
            }
            for (int i = 0; i < count; i++) {
                byte[] serviceID = new byte[2];
                // 读出存储在前面的int型数据
                System.arraycopy(ackData, 4 + i * 21, serviceID, 0, 2);
                int mSrvId = ByteUtilLittleEndian.byteArrToShort(serviceID);
                byte mT1 = ackData[4 + i * 21 + 2];
                byte mT2 = ackData[4 + i * 21 + 3];
                byte mLocation = ackData[4 + i * 21 + 4];
                PIServiceInfo si = new PIServiceInfo(mT1, mT2, mSrvId);
                byte[] mNameByteArray = new byte[16];
                System.arraycopy(ackData, 4 + i * 21 + 5, mNameByteArray, 0, 16);
                si.Name = ByteUtilBigEndian.ByteToString(mNameByteArray);
                if (null != si.Name && !si.Name.isEmpty()) {
                    si.Name = si.Name.trim();
                }
                srvs.add(si);
                //查找PIDevice实例对象，并填补其上的PIService
            }
            mServiceListByDeviceClass.put(strClsid, srvs);
            //生成设备上的服务对象
            checkPIServiceOfDeviceForClassid(strClsid, srvs);
        }
    }

    private void processBindMacList(byte[] ackData) {
        byte[] countBytes = new byte[4];
        System.arraycopy(ackData, 4, countBytes, 0, 4);
        int count = ByteUtilBigEndian.byteArrToInt(countBytes);
        synchronized (mBindedDeviceMacList) {
            mBindedDeviceMacList.clear();
            for (int i = 0; i < count; i++) {
                byte[] mac = new byte[8];
                System.arraycopy(ackData, i * 8 + 4, mac, 0, 8);
                mBindedDeviceMacList.add(ByteUtilBigEndian
                        .macAddrToStrWithoutSeparator(mac));
            }
        }
    }

    private void processBindDevList(byte[] ackData) {
        byte[] countBytes = new byte[4];
        System.arraycopy(ackData, 0, countBytes, 0, 4);
        int count = ByteUtilBigEndian.byteArrToInt(countBytes);

        for (int i = 0; i < count; i++) {
            byte[] bdiBytes = new byte[16];
            System.arraycopy(ackData, 4 + i * 16, bdiBytes, 0, 16);
            BindDevInfo bdi = new BindDevInfo(bdiBytes);

            int mPanID;
            int mShortAddr;
            int mSrvId = 1;
            byte mT1 = 0x0;
            byte mT2 = 0x0;
            String mName = "Device";
            if ((bdi.ClassId[5] & 0x8) == 0x8) { //蓝牙类型产品
                mShortAddr = bdi.getBleId();
                mPanID = 0x100;
            } else {
                byte[] virtualShortAddr = new byte[2];
                byte[] virtualPanid = new byte[2];
                System.arraycopy(bdi.MacAddr, 2, virtualShortAddr, 0, 2);
                System.arraycopy(bdi.MacAddr, 4, virtualPanid, 0, 2);
                mShortAddr = ByteUtilLittleEndian.byteArrToShort(virtualShortAddr);
                mPanID = ByteUtilLittleEndian.byteArrToShort(virtualPanid);
            }

            String keyString = PISBase.getKeyString(mPanID, mShortAddr, mSrvId);
            //构建PISDevice对象，并添加至PISManager对象中
            PISDevice obj = (PISDevice)PISManager.getInstance().getPISObject(keyString);
            if (obj == null || obj.getT1() != mT1 || obj.getT2() != mT2) {
                obj = (PISDevice) PISManager.CreateServiceObject(
                        new PIAddress(mPanID, mShortAddr),
                        new PIServiceInfo(mT1, mT2, mSrvId));
                obj.setMac(bdi.MacAddr);
                obj.setClassId(bdi.ClassId);
                obj.setName(mName);

                PISManager.getInstance().putPISObject(obj);
                obj.setStatus(PISBase.SERVICE_STATUS_INITING);
            }

            if (!mBindedDeviceMacList.contains(bdi.getMacString()))
                mBindedDeviceMacList.add(bdi.getMacString());
            //依照classid，获取设备上的服务信息，并创建对应服务对象
            ArrayList<PIServiceInfo> psiList = mServiceListByDeviceClass.get(obj.getClassString());
            if (psiList != null && psiList.size() > 0) {
                buildServiceWithDevice(obj, psiList);
            } else {
                //从服务器获取设备类型服务信息
                mServiceListByDeviceClass.put(obj.getClassString(), new ArrayList<PIServiceInfo>());
                PipaRequest req = getServiceInfoWithClassID(obj.getClassString());
                req.userData = obj;
                req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                    @Override
                    public void onRequestStart(PipaRequest req) {

                    }

                    @Override
                    public void onRequestResult(PipaRequest req) {
                        if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            PISDevice obj = (PISDevice) req.userData;
                            ArrayList<PIServiceInfo> psiList = mServiceListByDeviceClass.get(obj.getClassString());
                            if (psiList != null && psiList.size() > 0) {
                                buildServiceWithDevice(obj, psiList);
                            }
                        }
                    }
                });
                this.request(req);
            }
        }
    }

    /**
     * 更新所有分组信息
     *
     * @param ackData byte[]
     */

    private void processGetGroupInfosWithData(byte[] ackData) {
        if (ackData.length < 4)
            return;
        byte[] cntBytes = new byte[4];
        System.arraycopy(ackData, 0, cntBytes, 0, 4);
        int cnt = ByteUtilLittleEndian.getInt(cntBytes);

        if (ackData.length < (4 + cnt * 20))
            return;

        for (int i = 0; i < cnt; i++) {
            byte[] gidBytes = new byte[2];
            System.arraycopy(ackData, 4 + i * 20, gidBytes, 0, 2);
            int gid = ByteUtilLittleEndian.getInt(gidBytes);

            int t1 = ackData[4 + i * 20 + 2];
            int t2 = ackData[4 + i * 20 + 3];

            byte[] nameBytes = new byte[16];
            System.arraycopy(ackData, 4 + i * 20 + 4, nameBytes, 0, 16);
            String mName = ByteUtilBigEndian.ByteToString(nameBytes);

            //创建组对象，并添加至PISManager
            PIServiceInfo mSrv = new PIServiceInfo((byte) t1, (byte) t2, 0);
            mSrv.Name = mName + gid;
            PIAddress mAddr = new PIAddress(0xFFFF, gid);

            PISBase grp = PISManager.CreateServiceObject(mAddr, mSrv);
            grp.ServiceType = SERVICE_TYPE_GROUP;
            grp.setStatus(PISBase.SERVICE_STATUS_ONLINE);

            PISManager.getInstance().putPISObject(grp);
        }
    }

    private void processAddGroupServiceWithData(byte[] ackData) {
        byte[] gidBytes = new byte[2];
        System.arraycopy(ackData, 0, gidBytes, 0, 2);
        int gid = ByteUtilLittleEndian.getInt(gidBytes);
        PISBase grp = PISManager.getInstance().PIGroupsWithQuery(gid,
                PISManager.EnumGroupsQueryBaseonGID).get(0);
        if (grp == null)
            return;
        byte[] macBytes = new byte[8];
        System.arraycopy(ackData, 2, macBytes, 0, 8);
        byte[] sidBytes = new byte[2];
        System.arraycopy(ackData, 10, sidBytes, 0, 2);

        if (ackData[2] != (byte)(grp.getT1()&0xFF) || ackData[3] != (byte)(grp.getT2()&0xFF))
            return;
        int sid = ByteUtilLittleEndian.getInt(sidBytes);
        grp.addGroupObject(macBytes, sid);
    }

    private void processDeleteGroupServiceWithData(byte[] ackData) {
        byte[] gidBytes = new byte[2];
        System.arraycopy(ackData, 0, gidBytes, 0, 2);
        int gid = ByteUtilLittleEndian.getInt(gidBytes);
        PISBase grp = PISManager.getInstance().PIGroupsWithQuery(gid,
                PISManager.EnumGroupsQueryBaseonGID).get(0);
        if (grp == null)
            return;
        byte[] macBytes = new byte[8];
        System.arraycopy(ackData, 2, macBytes, 0, 8);
        byte[] sidBytes = new byte[2];
        System.arraycopy(ackData, 10, sidBytes, 0, 2);
        int sid = ByteUtilLittleEndian.getInt(sidBytes);
        grp.removeGroupObject(macBytes, sid);
    }

    private void processModifyGroupServiceWithData(byte[] ackData) {
        byte[] gidBytes = new byte[2];
        System.arraycopy(ackData, 0, gidBytes, 0, 2);
        int gid = ByteUtilLittleEndian.getInt(gidBytes);
        PISBase grp = PISManager.getInstance().PIGroupsWithQuery(gid,
                PISManager.EnumGroupsQueryBaseonGID).get(0);
        if (grp == null)
            return;
        if (ackData[2] != (byte)(grp.getT1()&0xFF) || ackData[3] != (byte)(grp.getT2()&0xFF))
            return;

        byte[] nameBytes = new byte[16];
        System.arraycopy(ackData, 4, nameBytes, 0, 16);
        String name = ByteUtilLittleEndian.getString(nameBytes).trim();
        grp.setName(name);
    }

    /**
     * 更新组内的服务信息
     */

    private void processServicesOfGroup(byte[] ackData) {
        byte[] numByte = new byte[4];
        System.arraycopy(ackData, 0, numByte, 0, 4);
        int num = ByteUtilLittleEndian.getInt(numByte);

        byte[] gidByte = new byte[2];
        System.arraycopy(ackData, 4, gidByte, 0, 2);
        int gid = ByteUtilLittleEndian.getInt(gidByte);
        List<PISBase> grps = PISManager.getInstance().PIGroupsWithQuery(gid, PISManager.EnumGroupsQueryBaseonGID);
        if (grps == null || grps.size() != 1)
            return;
        PISBase grp = grps.get(0);
        for (int i = 0; i < num; i++) {
            byte[] macBytes = new byte[8];
            System.arraycopy(ackData, 6 + i * 10, macBytes, 0, 8);
            byte[] sidBytes = new byte[2];
            System.arraycopy(ackData, 6 + i * 10 + 8, sidBytes, 0, 2);
            int sid = ByteUtilLittleEndian.getInt(sidBytes);
            grp.addGroupObject(macBytes, sid);
        }
    }

    /**
     * PIPA Callback Process
     */
    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK:
            case PISConstantDefine.PIPA_EVENT_MESSAGE: {
                PISCommandAckData ackData = (PISCommandAckData) lPara;
                switch (ackData.Command) {
                    case PIS_CMD_MCM_SCANDEVSVC: { //根据classid获取该设备类型下所有PI服务数据（有T1、T2、ServiceID和服务名称）
                        if (hPara != PipaRequest.REQUEST_RESULT_COMPLETE)
                            break;
                        if (ackData.data == null || ackData.data.length == 0 ||
                                ackData.rawRequestData == null || ackData.rawRequestData.Length == 0)
                            break;
                        if (ackData.data.length < 4)
                            break;

                        processServiceOfClass(ackData.data);

                    }
                    break;
                    case PIS_CMD_MCM_DEVBINDGET: {  //获取所有绑定设备列表只包含MAC地址
                        if (ackData.data != null && ackData.data.length >= 4) {
                            LogUtils.i(TAG, "PIS_CMD_MCM_DEVBINDGET success");
                            processBindMacList(ackData.data);
                        } else {
                            LogUtils.e(TAG, "dataSize ERROR !!! PIS_CMD_MCM_DEVBINDGET");
                        }
                    }
                    break;
                    case PIS_CMD_MCM_DEVBINDALL_GET: {  //获取所有绑定设备列表包含MAC地址，设备的classid和蓝牙ID
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        if (ackData.data == null || ackData.data.length == 0)
                            break;

                        processBindDevList(ackData.data);

                    }
                    break;
                    case PIS_CMD_MCM_DEVBIND: { //绑定设备
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData.Data == null)
                            break;
                        byte[] macBytes = new byte[8];
                        System.arraycopy(ackData.rawRequestData.Data, 0, macBytes, 0, 8);
                        String strMac = ByteUtilBigEndian.macAddrToStrWithoutSeparator(macBytes);
                        if (!mBindedDeviceMacList.contains(strMac))
                            mBindedDeviceMacList.add(strMac);

                    }
                    break;
                    case PIS_CMD_MCM_DEVUNBIND: { //解绑设备
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData.Data == null)
                            break;
                        String strMac = ByteUtilBigEndian.macAddrToStrWithoutSeparator(ackData.rawRequestData.Data);
                        if (mBindedDeviceMacList.contains(strMac))
                            mBindedDeviceMacList.remove(strMac);
                        try {
                            List<PISDevice> devs = PISManager.getInstance().PIDevicesWithQuery(
                                    strMac, PISManager.EnumDevicesQueryBaseonMac);
                            if (devs != null) {
                                for (PISDevice dev : devs)
                                    PISManager.getInstance().removePISObject(dev);
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                    case PIS_CMD_MCM_SMART_NEW: {
                    }
                    break;
                    case PIS_CMD_MCM_SMART_DEL:
                        break;
                    case PIS_CMD_MCM_GET_GROUP_NUM: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED) break;
                        this.processGetGroupInfosWithData(ackData.data);
                        break;
                    }
                    case PIS_CMD_MCM_ADD_GROUP: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null ||
                                ackData.rawRequestData.Data == null)
                            break;
                        byte[] grpBytes = new byte[ackData.rawRequestData.Data.length + 4];
                        System.arraycopy(ByteUtilLittleEndian.getBytes(1), 0, grpBytes, 0, 4);
                        System.arraycopy(ackData.data, 0, grpBytes, 4, 2);
                        System.arraycopy(ackData.rawRequestData.Data, 2, grpBytes, 6, ackData.rawRequestData.Data.length - 2);
                        processGetGroupInfosWithData(grpBytes);
                        break;
                    }
                    case PIS_CMD_MCM_DEL_GROUP: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null ||
                                ackData.rawRequestData.Data == null)
                            break;

                        int gid = ByteUtilLittleEndian.getInt(ackData.rawRequestData.Data);
                        List<PISBase> grps = PISManager.getInstance().PIGroupsWithQuery(gid, PISManager.EnumGroupsQueryBaseonGID);
                        for (PISBase grp : grps) {
                            PISManager.getInstance().removePISObject(grp);
                        }
                        break;
                    }
                    case PIS_CMD_MCM_GET_GROUP_SRV: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        this.processServicesOfGroup(ackData.data);
                        break;
                    }
                    case PIS_CMD_MCM_ADD_GROUP_SRV: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ((PISCommandAckData) lPara).rawRequestData == null ||
                                ((PISCommandAckData) lPara).rawRequestData.Data == null)
                            break;
                        this.processAddGroupServiceWithData(((PISCommandAckData) lPara).rawRequestData.Data);
                        break;
                    }
                    case PIS_CMD_MCM_DEL_GROUP_SRV: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ((PISCommandAckData) lPara).rawRequestData == null ||
                                ((PISCommandAckData) lPara).rawRequestData.Data == null)
                            break;
                        this.processDeleteGroupServiceWithData(((PISCommandAckData) lPara).rawRequestData.Data);
                        break;
                    }
                    case PIS_CMD_MCM_CHG_GROUP: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ((PISCommandAckData) lPara).rawRequestData == null ||
                                ((PISCommandAckData) lPara).rawRequestData.Data == null)
                            break;
                        this.processModifyGroupServiceWithData(((PISCommandAckData) lPara).rawRequestData.Data);
                    }
                    break;
                    case PIS_CMD_MCM_STEPTABLE_ADD:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ((PISCommandAckData) lPara).rawRequestData == null ||
                                ((PISCommandAckData) lPara).rawRequestData.Data == null) {
                            LogUtils.i(TAG, "Add steps to table  back, Successful!");
                        } else {
                            LogUtils.i(TAG, "Add steps to table  back, fail!");
                        }
                        break;
                    case PIS_CMD_MCM_STEPTABLE_GETN:
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ((PISCommandAckData) lPara).rawRequestData == null ||
                                ((PISCommandAckData) lPara).rawRequestData.Data == null) {
                            LogUtils.i(TAG, "Get steps from table by getn back, Successful!");
                        } else {
                            LogUtils.i(TAG, "Get steps from table by getn back, fail!");
                        }
                        break;
                    case PIS_CMD_MCM_STEPTABLE_GET:

                        break;
                    //以下是消息处理
                    case PIS_MSG_MCM_GROUPADD: { /** 添加组*/
                        this.processAddGroupServiceWithData(ackData.data);
                        break;
                    }
                    case PIS_MSG_MCM_GROUPDEL: { /** 删除组*/
                        this.processDeleteGroupServiceWithData(ackData.data);
                        break;
                    }
                    case PIS_MSG_MCM_GROUPSRVADD: { /** 添加组服务*/
                        byte[] grpBytes = new byte[6];
                        System.arraycopy(ByteUtilLittleEndian.getBytes(1), 0, grpBytes, 0, 4);
                        System.arraycopy(ackData.data, 0, grpBytes, 4, 2);
                        processGetGroupInfosWithData(grpBytes);
                        break;
                    }
                    case PIS_MSG_MCM_GROUPSRVDEL: { /** 删除组服务*/
                        int gid = ByteUtilLittleEndian.getInt(ackData.data);
                        List<PISBase> grps = PISManager.getInstance().PIGroupsWithQuery(gid, PISManager.EnumGroupsQueryBaseonGID);
                        for (PISBase grp : grps) {
                            PISManager.getInstance().removePISObject(grp);
                        }
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
        }
        super.onProcess(msg, hPara, lPara);
    }


    /**
     * public method
     */
    public boolean isBinded(String macStr){
        return mBindedDeviceMacList.contains(macStr);
    }

    /**
     * 智能元安装与卸载命令
     */
/**在云服务器上安装一个智能元应用*/
    public PipaRequest installSmartCell(String guid){
        return new PipaRequest(this, PIS_CMD_MCM_SMART_NEW,
                guid.getBytes(), 33, true);

    }
/**从服务器上删除一个智能元应用*/
    public PipaRequest uninstallSmartCell(String instanceId){
        return new PipaRequest(this, PIS_CMD_MCM_SMART_DEL,
                instanceId.getBytes(), 12, true);
    }

    /**
     * PIPA_Request 设备绑定、解绑、获取等命令
     */

    /**根据classid获取该类型设备下的功能服务信息*/
    public PipaRequest getServiceInfoWithClassID(String classid) {
        byte[] data = ByteUtilBigEndian.classIdStrToByte(classid, 6);
        return new PipaRequest(this,
                PIS_CMD_MCM_SCANDEVSVC,
                data,
                data.length,
                true);
    }

    public class devBindRequest extends PipaRequest{
        public devBindRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;
            byte[] macByte = new byte[8];

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");

            try {

                System.arraycopy(this.RequestData.Data, 0, macByte, 0, 8);

                SharedPreferences pref = PISManager.getDefaultContext().getSharedPreferences("DeviceInfoList", Context.MODE_PRIVATE);
                Set<String> macAddrs = pref.getStringSet("bindedMacList", null);

                if (macAddrs == null)
                    macAddrs = new HashSet<String>();
                if (mBindedDeviceMacList.size() == 0){
                    mBindedDeviceMacList.addAll(macAddrs);
                }
                String macStr = ByteUtilBigEndian.macAddrToStrWithoutSeparator(macByte);
                if (!macAddrs.contains(macStr)){
                    macAddrs.add(macStr);
                    mBindedDeviceMacList.add(macStr);
                    //将数据回存
                    SharedPreferences.Editor prefEditor = pref.edit();
                    prefEditor.putStringSet("bindedMacList", macAddrs);
                    prefEditor.apply();
                }

            }catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            /**构建返回数据，其组成为：command[1byte] + length[2byte] + data[length byte]*/

            byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                    macByte.length,
                    macByte);

            //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息

            this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                    result,
                    ackBytes);

            return result;
        }

    }
    public PipaRequest bindDevice(byte[] macByte, byte[] clsByte){
        byte[] dataBytes = new byte[30];

        System.arraycopy(macByte, 0, dataBytes, 0, 8);
        System.arraycopy(clsByte, 0, dataBytes, 8, 6);

        return new devBindRequest(this, PIS_CMD_MCM_DEVBIND, dataBytes, 30, true);
    }

    public class devUnbindRequest extends PipaRequest{
        public devUnbindRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");
            byte[] macByte = new byte[8];
            System.arraycopy(this.RequestData.Data, 0, macByte, 0, 8);

            /**构建返回数据，其组成为：command[1byte] + length[2byte] + data[length byte]*/
            SharedPreferences pref = PISManager.getDefaultContext().getSharedPreferences("DeviceInfoList", Context.MODE_PRIVATE);
            Set<String> macAddrs = pref.getStringSet("bindedMacList", null);

//            if (macAddrs == null)
////                macAddrs = new HashSet<String>();
////
////            if (mBindedDeviceMacList.size() == 0)
////                mBindedDeviceMacList.addAll(macAddrs);

            String macStr = ByteUtilBigEndian.macAddrToStrWithoutSeparator(macByte);
            if (macAddrs != null && macAddrs.contains(macStr)){
                macAddrs.remove(macStr);
                mBindedDeviceMacList.remove(macStr);
                //将数据回存
                SharedPreferences.Editor prefEditor = pref.edit();
                prefEditor.putStringSet("bindedMacList", macAddrs);
                prefEditor.apply();
            }

            byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                    macByte.length,
                    macByte);

            //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
            this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                    result,
                    ackBytes);

            return result;
        }

    }
    /**将一个设备从当前账号下解除绑定*/
    public PipaRequest unbindDevice(byte[] macByte){
        return new devUnbindRequest(this, PIS_CMD_MCM_DEVUNBIND, macByte, 8, true);
    }

    public class getBindedDeviceRequest extends PipaRequest{
        public getBindedDeviceRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;

            if (this.RequestData == null)
                throw new IllegalArgumentException("requestData be null");

            Integer count = 0;
            byte[] bidBytes = new byte[2];
            byte[] resultBytes = new byte[4 + 16*count];
            /**构建返回数据，其组成为：count[4byte] + [{mac[8byte] + classid[6 byte] + bleid[2byte]} ...]*/


            SharedPreferences pref = PISManager.getDefaultContext().getSharedPreferences("DeviceInfoList", Context.MODE_PRIVATE);
            Set<String> macAddrs = pref.getStringSet("bindedMacList", null);

            if (macAddrs == null)
                macAddrs = new HashSet<String>();

            for (String macStr : macAddrs){
                List<PISDevice> devs = PISManager.getInstance().PIDevicesWithQuery(
                        macStr, PISManager.EnumDevicesQueryBaseonMac);
                if (devs.size() == 1){
                    PISDevice dev = devs.get(0);
                    System.arraycopy(dev.getMacByte(), 0, resultBytes, 4 + (16*count), 8);
                    System.arraycopy(dev.getClassByte(), 0, resultBytes, 4 + (16*count) + 8, 6);
                    if ((dev.getClassByte()[5] & 0x8) == 0x8){
                        bidBytes[0] = (byte)(dev.getShortAddr() & 0xFF);
                        bidBytes[1] = (byte)((dev.getShortAddr() & 0xFF00)>>8);
                    }else{
                        bidBytes[0] = 0;
                        bidBytes[1] = 1;
                    }
                    System.arraycopy(bidBytes, 0, resultBytes, 4 + (16*count) + 14, 2);
                    count++;
                }
            }
            //最后填入数量
            resultBytes[0] = (byte)(count & 0xFF);
            resultBytes[1] = (byte)((count & 0xFF00)>>8);
            resultBytes[2] = (byte)((count & 0xFF0000)>>16);
            resultBytes[3] = (byte)((count & 0xFF000000)>>24);


            byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                    resultBytes.length,
                    resultBytes);

            //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
            this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                    result,
                    ackBytes);

            return result;
        }

    }
    /**获取当前账号下已经被绑定的设备列表 （暂时不提供本地版本）*/
    public PipaRequest updateBindedDevices(){
        return new getBindedDeviceRequest(this, PIS_CMD_MCM_DEVBINDALL_GET,
                null, 0, true);
    }

    public class bindedMacListRequest extends PipaRequest{
        public bindedMacListRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");

            /**构建返回数据，其组成为：{count[4Byte][{mac[8], ...]}*/
            try {
                SharedPreferences pref = PISManager.getDefaultContext().getSharedPreferences("DeviceInfoList", Context.MODE_PRIVATE);
                Set<String> macAddrs = pref.getStringSet("bindedMacList", null);

                byte[] dataBytes = null;
                if (macAddrs != null && macAddrs.size() > 0){
                    int count = macAddrs.size();
                    dataBytes = new byte[4 + count*8];
                    int i = 0;
                    for (String macStr : macAddrs){
                        byte[] macBytes = ByteUtilBigEndian.macStrToStringByte(macStr, 8);
                        System.arraycopy(macBytes, 0, dataBytes, 4 + i*8, 8);
                    }

                    dataBytes[0] = (byte)(count & 0xFF);
                    dataBytes[1] = (byte)((count & 0xFF00) >> 8);
                    dataBytes[2] = (byte)((count & 0xFF0000) >> 16);
                    dataBytes[3] = (byte)((count & 0xFF000000) >> 24);
                }else{
                    dataBytes = new byte[4];
                }

                byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                        dataBytes.length,
                        dataBytes);

                //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
                this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                        result,
                        ackBytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

    }
    public PipaRequest updateBindedMacList(){
        return new bindedMacListRequest(this, PIS_CMD_MCM_DEVBINDGET, null, 0, true);
    }


    /**获取用户名下所有设备的Mac地址*/
    public PipaRequest getBindedMacListWithUser(String username){
        byte[] userBytes = new byte[16];
        if (username.getBytes().length > 16){
            System.arraycopy(username.getBytes(), 0, userBytes, 0, 16);
        }else
            System.arraycopy(username.getBytes(), 0, userBytes, 0, username.getBytes().length);

        return new PipaRequest(this, PIS_CMD_MCM_SUBDEVBINDGET, userBytes, 16, true);
    }

    /**
     * PIPA_REQUEST 与组操作相关命令
     */
    public class getGroupInfoRequest extends PipaRequest{
        public getGroupInfoRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;

            if (this.RequestData == null || this.RequestData.Command != PIS_CMD_MCM_GET_GROUP_NUM)
                throw new IllegalArgumentException("requestData be null");

            /**构建返回数据，其组成为：{count[4Byte][{gid[2byte],T1[1byte], T2[1byte], name[16byte]}, ...]}*/
            try {
                List<PISBase> allGroups = PISManager.getInstance().AllGroups();
                byte[] groupBytes = null;
                if (allGroups != null && allGroups.size() > 0){
                    groupBytes = new byte[4 + allGroups.size() * 20];
                    for (int i = 0; i < allGroups.size(); i++){
                        PISBase grp = allGroups.get(i);
                        groupBytes[4 + i * 20 + 0] = (byte)(grp.getGroupId() & 0xFF);
                        groupBytes[4 + i * 20 + 1] = (byte)((grp.getGroupId() & 0xFF00) >> 8);
                        groupBytes[4 + i * 20 + 2] = (byte)(grp.getT1() & 0xFF);
                        groupBytes[4 + i * 20 + 3] = (byte)(grp.getT2() & 0xFF);
                        byte[] nameBytes = grp.getName().getBytes();
                        if (nameBytes != null)
                            System.arraycopy(nameBytes, 0, groupBytes, 4 + i * 20 + 4, nameBytes.length>16?16:nameBytes.length);
                    }
                }else{
                    groupBytes = new byte[4];
                    groupBytes[0] = 0;
                    groupBytes[1] = 0;
                    groupBytes[2] = 0;
                    groupBytes[3] = 0;
                }

                byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                        groupBytes.length,
                        groupBytes);

                //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
                this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                        result,
                        ackBytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

    }
    /** 获取当前账户的组信息集合 */
    public PipaRequest getGroupInfos(){
        return new getGroupInfoRequest(this, PIS_CMD_MCM_GET_GROUP_NUM, null, 0, true);
    }


    public class addGroupRequest extends PipaRequest{
        public addGroupRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_COMPLETE;

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");

            try {
                /**原始数据：groupid[2byte]+T1[1byte]+T2[1byte]+name[16byte]*/
                int gid = 1;
                byte T1 = this.RequestData.Data[2];
                byte T2 = this.RequestData.Data[3];
                byte[] nameBytes = new byte[16];
                System.arraycopy(this.RequestData.Data, 4, nameBytes, 0, 16);

                List<PISBase> allGroups = PISManager.getInstance().AllGroups();
                //groupid 的取值范围为[1 ~ 1000]
                if (allGroups != null && allGroups.size() > 0){
                    SparseArray<PISBase> groups = new SparseArray<>();
                    for (PISBase grp : allGroups){
                        groups.put(grp.getShortAddr(), grp);
                    }

                    while(groups.get(gid) != null){
                        gid++;
                    }
                }
//                PIAddress addr = new PIAddress(0xFFFF, gid);
//                PIServiceInfo srvInfo = new PIServiceInfo(T1, T2, 0);
//
//                PISBase grp = PISManager.getInstance().CreateServiceObject(addr, srvInfo);
//                grp.ServiceType = PISBase.SERVICE_TYPE_GROUP;
//                grp.PairID = grp.getShortAddr();
//                grp.Status = PISBase.SERVICE_STATUS_ONLINE;

                /**构建返回数据，其组成为：{count[4Byte][{gid[2byte],T1[1byte], T2[1byte], name[16byte]}, ...]}*/
                byte[] resultBytes = new byte[2];
//                if (grp != null){
////                    PISManager.getInstance().addToPisMap(grp);
//                    resultBytes[0] = (byte)(gid & 0xFF);
//                    resultBytes[1] = (byte)((gid & 0xFF00) >> 8);
//                }else{
//                    result = PipaRequest.REQUEST_RESULT_FAILED;
//                }

                resultBytes[0] = (byte)(gid & 0xFF);
                resultBytes[1] = (byte)((gid & 0xFF00) >> 8);

                byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                        resultBytes.length,
                        resultBytes);

                //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
                this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                        result,
                        ackBytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

    }
    /**增加组*/
    public PipaRequest addGroup(String grpName, int t1, int t2){
        byte[] dataBytes = new byte[20];
        dataBytes[2] = (byte)(t1 & 0xFF);
        dataBytes[3] = (byte)(t2 & 0xFF);
        System.arraycopy(grpName.getBytes(), 0, dataBytes, 4, grpName.getBytes().length>16?16:grpName.getBytes().length);

        return new addGroupRequest(this, PIS_CMD_MCM_ADD_GROUP, dataBytes, 20, true);
    }

    public class removeGroupRequest extends PipaRequest{
        public removeGroupRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_FAILED;

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");

            try {
                /**原始数据：groupid[2byte]+T1[1byte]+T2[1byte]+name[16byte]*/
                int gid = ByteUtilLittleEndian.getInt(this.RequestData.Data);
                List<PISBase> allGroups = PISManager.getInstance().AllGroups();

                if (allGroups != null && allGroups.size() > 0){
                    for (PISBase grp : allGroups){
                        if (grp.getGroupId() == gid){
                            result = PipaRequest.REQUEST_RESULT_COMPLETE;
                            break;
                        }
                        result = PipaRequest.REQUEST_RESULT_FAILED;
                    }
                }
                /**构建返回数据，无返回参数*/
                byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                        0,
                        null);
                //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
                this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                        result,
                        ackBytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

    }
    /**删除组*/
    public PipaRequest removeGroup(int gid){
        byte[] dataBytes = new byte[2];
        dataBytes[0] = (byte)(gid & 0xFF);
        dataBytes[1] = (byte)((gid & 0xFF00) >> 8);

        return new removeGroupRequest(this, PIS_CMD_MCM_DEL_GROUP, dataBytes, 2, true);

    }

    public class modifyGroupRequest extends PipaRequest{
        public modifyGroupRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
            super(obj, cmd, data, len, ack);
        }

        @Override
        public int local_execute(){
            int result = PipaRequest.REQUEST_RESULT_FAILED;

            if (this.RequestData == null || this.RequestData.Data == null)
                throw new IllegalArgumentException("requestData be null");

            try {
                /**原始数据：groupid[2byte]+T1[1byte]+T2[1byte]+name[16byte]*/

                byte T1 = this.RequestData.Data[2];
                byte T2 = this.RequestData.Data[3];
                byte[] nameBytes = new byte[16];
                System.arraycopy(this.RequestData.Data, 4, nameBytes, 0, 16);
                byte[] gidBytes = new byte[2];
                System.arraycopy(this.RequestData.Data, 0, gidBytes, 0, 2);
                int gid = ByteUtilLittleEndian.getInt(gidBytes);
                List<PISBase> allGroups = PISManager.getInstance().AllGroups();

                if (allGroups != null && allGroups.size() > 0){
                    for (PISBase grp : allGroups){
                        if (grp.getGroupId() == gid && grp.getT1() == (T1&0xFF) && grp.getT2() == (T2&0xFF)){
                            grp.setName(ByteUtilLittleEndian.getString(nameBytes));
                            result = PipaRequest.REQUEST_RESULT_COMPLETE;
                            break;
                        }
                        result = PipaRequest.REQUEST_RESULT_FAILED;
                    }
                }
                /**构建返回数据，无返回参数*/
                byte[] ackBytes = PipaRequest.buildAckData(this.RequestData.Command,
                        0,
                        null);
                //暂时依赖PISMCSManager来存储所绑定的设备信息，将来需要独立存储相关信息
                this.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                        result,
                        ackBytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

    }
    /**修改组信息*/
    public PipaRequest modifyGroup(int gid, String name, byte t1, byte t2){
        byte[] dataBytes = new byte[20];

        dataBytes[0] = (byte)(gid & 0xFF);
        dataBytes[1] = (byte)((gid & 0xFF00) >> 8);
        dataBytes[2] = t1;
        dataBytes[3] = t2;
        System.arraycopy(name.getBytes(), 0, dataBytes, 4, name.getBytes().length>16?16:name.getBytes().length);

        return new modifyGroupRequest(this, PIS_CMD_MCM_CHG_GROUP, dataBytes, 20, true);
    }

    /**获取指定组中分别有哪些服务*/
    public PipaRequest getServiceWithGroup(int gid){
        byte[] dataBytes = new byte[2];

        dataBytes[0] = (byte)(gid & 0xFF);
        dataBytes[1] = (byte)((gid & 0xFF00) >> 8);

        return new PipaRequest(this, PIS_CMD_MCM_GET_GROUP_SRV, dataBytes, 2, true);
    }

    /** 添加某设备的某个服务到指定组*/
    public PipaRequest addServiceWithGroup(PISBase pis, int gid){
        if (pis == null || pis.getDeviceObject() == null)
            throw new IllegalArgumentException("对象不能为空");
        List<PISBase> grps = PISManager.getInstance().PIGroupsWithQuery(gid, PISManager.EnumGroupsQueryBaseonGID);

        if (grps == null || grps.size() == 0)
            throw new IllegalArgumentException("错误的gid");

        PISBase grp = grps.get(0);
        if (grp.getT1() != pis.getT1() || grp.getT2() != pis.getT2())
            throw new IllegalArgumentException("组类型不一致");

        byte[] dataBytes = new byte[14];

        dataBytes[0] = (byte)(gid & 0xFF);
        dataBytes[1] = (byte)((gid & 0xFF00) >> 8);

        System.arraycopy(pis.getDeviceObject().getMacByte(), 0, dataBytes, 2, 8);
        dataBytes[10] = (byte)(pis.getServiceId() & 0xFF);
        dataBytes[11] = (byte)((pis.getServiceId() & 0xFF00) >> 8);

        dataBytes[12] = (byte)this.getT1();
        dataBytes[13] = (byte)this.getT2();

        return new PipaRequest(this, PIS_CMD_MCM_ADD_GROUP_SRV, dataBytes, 14, true);

    }

    /** 指定组删除某项服务*/
    public PipaRequest deleteServiceWithGroup(PISBase pis, int gid){
        if (pis == null || pis.getDeviceObject() == null)
            throw new IllegalArgumentException("对象不能为空");
        List<PISBase> grps = PISManager.getInstance().PIGroupsWithQuery(gid, PISManager.EnumGroupsQueryBaseonGID);
        if (grps == null || grps.size() == 0)
            throw new IllegalArgumentException("无效的组ID");

        byte[] dataByte = new byte[12];
        dataByte[0] = (byte)(gid & 0xFF);
        dataByte[1] = (byte)((gid & 0xFF00) >> 8);

        System.arraycopy(pis.getDeviceObject().getMacByte(), 0, dataByte, 2, 8);
        dataByte[10] = (byte)(pis.getServiceId() & 0xFF);
        dataByte[11] = (byte)((pis.getServiceId() & 0xFF00) >> 8);

        return new PipaRequest(this, PIS_CMD_MCM_DEL_GROUP_SRV, dataByte, 12, true);

    }

    /** 指定组删除某项已离线或解绑设备上的服务*/
    public PipaRequest deleteServiceWithGroup(byte[] mac, int srvid, int gid){
        if (mac == null || mac.length != 8)
            throw new IllegalArgumentException("MAC地址错误");

        byte[] dataByte = new byte[12];
        dataByte[0] = (byte)(gid & 0xFF);
        dataByte[1] = (byte)((gid & 0xFF00) >> 8);

        System.arraycopy(mac, 0, dataByte, 2, 8);
        dataByte[10] = (byte)(srvid & 0xFF);
        dataByte[11] = (byte)((srvid & 0xFF00) >> 8);

        return new PipaRequest(this, PIS_CMD_MCM_DEL_GROUP_SRV, dataByte, 12, true);
    }


    /**
     * PIPA_REQUEST 与计步数据存储相关命令
     */

    /**保存计步数据到MCSvr */
    public PipaRequest storeStepData(String dataPath, Object stepData){
        return null;
    }

    /**从MCSvr服务获取数据 时间及数量*/
    public PipaRequest getStepData(Object time, int count){
        return null;
    }

    /**从MCSvr服务获取数据 时间段*/
    public PipaRequest getStepData(Object startTime, Object endTime){
        return null;
    }


}
