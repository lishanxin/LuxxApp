package net.senink.piservice.struct;

import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;

/**
 * Created by wensttu on 2016/6/17.
 */

public class PISDeviceInfo {
    private static final long serialVersionUID = 3226801801336417741L;
    private static final int DEV_INFO_TOTAL_SIZE = 36;
    private static final int MAC_ADDR_SIZE = 8;
    private static final int LOCATION_SIZE = 2;
    private static final int CLASS_ID_SIZE = 6;
    private static final int FW_VERSION_SIZE = 4;

    public PISDeviceInfo(byte[] mac, byte[] classid, byte[] version) {
        _macAddr = mac;
        _clsid = classid;
        _version = version;
    }

    private byte[] _macAddr;
    private short _location;
    private byte[] _clsid;
    private byte[] _version;

    public String getMacString() {
        if (_macAddr == null) {
            return "";
        } else {
            return ByteUtilBigEndian.ByteToString(_macAddr);

        }
    }

    public byte[] getMacBytes(){
        return _macAddr.clone();
    }

    public String getClassIDString(){
        if (_clsid == null)
            return "";
        else
            return ByteUtilBigEndian.classIdToStr(_clsid);
    }

    public byte[] getClassIDBytes(){
        return _clsid.clone();
    }

    public String getVersionString(){
        if (_version == null)
            return "";
        else {
            byte majorVer = _version[0];
            byte minorVer = _version[1];
            byte[] buildByte = new byte[2];
            System.arraycopy(_version, 3, buildByte, 0, 2);
            int buildVer = ByteUtilLittleEndian.byteArrToShort(buildByte);
            return String.format("%d.%d.%d", majorVer, minorVer, buildVer);
        }
    }

    public byte[] getVersionBytes(){
        return _version;
    }

    public int getVersionInteger() {
        if (_version == null)
            return 0;
        else {
            byte majorVer = _version[0];
            byte minorVer = _version[1];
            byte[] buildByte = new byte[2];
            System.arraycopy(_version, 3, buildByte, 0, 2);
            int buildVer = ByteUtilLittleEndian.byteArrToShort(buildByte);
            return majorVer * 16777215 + minorVer * 65535 + buildVer;
        }
    }

}
