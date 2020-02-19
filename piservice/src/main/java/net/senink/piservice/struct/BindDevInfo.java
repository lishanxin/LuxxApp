package net.senink.piservice.struct;

import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;

/**
 * Created by wensttu on 2016/7/21.
 */
public class BindDevInfo {
    public byte[] MacAddr;
    public byte[] ClassId;
    public byte[] BleId;

    public BindDevInfo(byte[] mac, byte[] cls, byte[] bid) {
        MacAddr = mac;
        ClassId = cls;
        BleId = bid;
    }

    public BindDevInfo(byte[] data) {
        MacAddr = new byte[8];
        System.arraycopy(data, 0, MacAddr, 0, 8);
        ClassId = new byte[6];
        System.arraycopy(data, 8, ClassId, 0, 6);
        BleId = new byte[2];
        System.arraycopy(data, 14, BleId, 0, 2);
    }

    public String getMacString() {
        return ByteUtilBigEndian.macAddrToStrWithoutSeparator(MacAddr);
    }
    public void setMacString(String macStr){
        if (macStr == null)
            return;
        byte[] mac = ByteUtilBigEndian.macStrToStringByte(macStr, 8);
        if (mac != null)
            MacAddr = mac;
    }

    public String getClassString() {
        return ByteUtilBigEndian.classIdToStr(ClassId);
    }
    public void setClassId(String clsStr){
        if (clsStr == null)
            return;
        byte[] clsBytes = ByteUtilBigEndian.StringToByte2(clsStr, 6);
        if (clsBytes != null)
            ClassId = clsBytes;
    }

    public int getBleId() {
        return ByteUtilLittleEndian.getInt(BleId);
    }
    public void setBleId(int bid){
        byte[] bidBytes = ByteUtilLittleEndian.getBytes(bid);
        if (bidBytes != null)
            BleId = bidBytes;
    }
}
