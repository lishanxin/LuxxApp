package net.senink.piservice.services;

import android.util.SparseArray;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.util.LogUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wensttu on 2016/6/24.
 */
public class PISXinRemoter extends PISBase {
    private static final String TAG = "PISXinRemoter";

    private transient SparseArray<byte[]> keyByteList;

    public static final byte PIS_CMD_REMOTER_KEY_SET = (byte)0x90; /**< 设置按键*/
    public static final byte PIS_CMD_REMOTER_KEY_GET = (byte)0x91; /**< 获取按键*/
    public static final byte PIS_CMD_REMOTER_SLEEP   = (byte)0x92; /**< 设置遥控器进入睡眠模式*/

    public PISXinRemoter(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_REMOTER_KEY_SET,
                "设置按键",
                "设置按键",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_REMOTER_KEY_GET,
                "获取按键",
                "获取按键",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_REMOTER_SLEEP,
                "设置遥控器进入睡眠模式",
                "设置遥控器进入睡眠模式",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

        keyByteList = new SparseArray<>();
    }

    public PISBase getKeyobject(int idx){
        if (idx < 0 || idx > 3)
            throw new IllegalArgumentException("the idx value ["+idx+"] outside range");
        byte[] keyBytes = keyByteList.get(idx);
        if (keyBytes != null) {
            int saddr = 0;
            int srvid = 0;
            int panid = (keyBytes[1] & 0xFF);
            if (panid == 0xFF) {
                panid = 0x100;
                saddr = (keyBytes[2] & 0xFF) | ((keyBytes[3] & 0xFF) << 8);
                srvid = (keyBytes[4] & 0xFF) | ((keyBytes[5] & 0xFF) << 8);
            } else {//组的地址格式 panid = 0xFFFF, shortaddr = groupid, serviceid = 0;
                saddr = panid;
                panid = 0xFFFF;
            }

            return PISManager.getInstance().getPISObject(
                    PISBase.getKeyString(panid, saddr, srvid));
        }
        return  null;
    }
    /**
     * 总长度18Byte
     * U8 num;      //键索引( < (KEY_NUM_MAX+ CMDKEY_NUM_MAX))
     * U8 value;    //键值（组ID(< 0xFF)或设备ID；为设备ID时，value赋值为0xFF）
     * U16 shortAddr;  //PIPA地址的shortAddr(value为设备ID时有效，ELSE置零)
     * U16 srvID;  //服务ID(value为设备ID时有效，ELSE置零)
     * U8 cmd;      //命令字(num >= KEY_NUM_MAX时有效, ELSE置零)
     * U8 len;     //PARAM长 <=10(num >= KEY_NUM_MAX时有效, ELSE置零)
     * U8 *pParam;  //命令所带的参数
     * @param idx
     * @return
     */
    public PipaRequest getRequestObject(int idx){
        PipaRequest result = null;
        if (idx < 0 || idx > 3)
            throw new IllegalArgumentException("the idx value ["+idx+"] outside range");
        byte[] keyBytes = keyByteList.get(idx);
        if (keyBytes != null){
            int saddr = 0;
            int srvid = 0;
            int panid = (keyBytes[1] & 0xFF);
            if (panid == 0xFF){
                panid = 0x100;
                saddr = (keyBytes[2] & 0xFF) | ((keyBytes[3] & 0xFF) << 8);
                srvid = (keyBytes[4] & 0xFF) | ((keyBytes[5] & 0xFF) << 8);
            }else{//组的地址格式 panid = 0xFFFF, shortaddr = groupid, serviceid = 0;
                saddr = panid;
                panid = 0xFFFF;
            }

            PISBase srv = PISManager.getInstance().getPISObject(
                    PISBase.getKeyString(panid, saddr, srvid));
            if (srv == null)
                return result;
            byte cmd = keyBytes[6];
            byte len = keyBytes[7];
            byte[] paraData = new byte[10];
            System.arraycopy(keyBytes, 8, paraData, 0, keyBytes.length-8);
            result = new PipaRequest(srv, cmd, paraData, len, false);
        }
        return result;
    }

    /**
     * public Interface
     */

    /**
     * 获取指定索引的按键信息
     * @param index 索引值
     * @return PipaRequest
     */
    public PipaRequest updateKeyInfo(int index){
        byte[] dataBytes = new byte[1];
        dataBytes[0] = (byte)(index & 0xFF);

        return new PipaRequest(this, PIS_CMD_REMOTER_KEY_GET, dataBytes, 1, true);
    }


    public PipaRequest commitKeyInfo(int index, int panid, int saddr, int srvid, int cmd, byte[] para){
        int len = 8;
        if (para != null)
            len += (para.length + 2);
        byte[] databytes = new byte[8 + len];

        databytes[0] = (byte)(index & 0xFF);
        if (panid == 0xFFFF) { //表示为组播
            databytes[1] = (byte)(saddr & 0xFF);
            databytes[2] = (byte)0;
            databytes[3] = (byte)0;
            databytes[4] = (byte)0;
            databytes[5] = (byte)0;
        }else{
            databytes[1] = (byte)0xFF;
            databytes[2] = (byte)(saddr & 0xFF);
            databytes[3] = (byte)((saddr & 0xFF00) >> 8);
            databytes[4] = (byte)(srvid & 0xFF);
            databytes[5] = (byte)((srvid & 0xFF00) >> 8);
        }
        databytes[6] = (byte)(cmd & 0xFF);
        if (para == null)
            databytes[7] = 0;
        else
            databytes[7] = (byte)(len-10);

        if (para != null && para.length > 0 && para.length < 11){
            databytes[7] = (byte)(para.length & 0xFF);
            System.arraycopy(para, 0, databytes, 8, para.length);
            len += para.length;
        }

        return new PipaRequest(this, PIS_CMD_REMOTER_KEY_SET, databytes, len, true);
    }

    public PipaRequest commitKeyInfo(int index, PISBase pis, PipaRequest req){
        if (pis == null || req == null)
            return null;

        return commitKeyInfo(index,
                pis.getPanId(),
                pis.getShortAddr(),
                pis.getServiceId(),
                req.RequestData.Command,
                req.RequestData.Data);
    }

    public PipaRequest commitRemoteSleep(){
        return new PipaRequest(this, PIS_CMD_REMOTER_SLEEP, null, 0, true);
    }

    public void processReceiveDataByRemoter(byte[] ackData){
        if (ackData == null || ackData.length < 8)
            return;

        int keyIndex = ackData[0];
        byte[] keyBytes;
        try {
            keyBytes = keyByteList.get(keyIndex);
        }catch (Exception e){
            keyBytes = null;
        }
        if (keyBytes == null){
            keyBytes = new byte[18];
            keyByteList.put(keyIndex, keyBytes);
        }
        System.arraycopy(ackData, 0, keyBytes, 0, ackData.length);
    }
    /**
     * PIPA 消息处理函数
     * @param msg   消息
     * @param hPara 高位参数
     * @param lPara 低位参数
     */
    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK:{
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_REMOTER_KEY_GET:{
                        this.processReceiveDataByRemoter(ackData.data);
                        break;
                    }
                    case PIS_CMD_REMOTER_KEY_SET:{
                        LogUtils.i(TAG, "设置按键成功!");
                        this.processReceiveDataByRemoter(ackData.rawRequestData.Data);
                        break;
                    }
                    case PIS_CMD_REMOTER_SLEEP: {
                        LogUtils.i(TAG, "设置遥控睡眠成功!");
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

}
