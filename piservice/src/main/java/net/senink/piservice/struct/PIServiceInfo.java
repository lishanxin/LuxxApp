package net.senink.piservice.struct;

import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.piservice.util.ByteUtilLittleEndian;

import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/17.
 */
public class PIServiceInfo  implements Serializable {
    private static final long serialVersionUID = 7608617343996243628L;
    private int ServiceID;
    public byte T1;
    public byte T2;
    public byte Location;
    public String Name;

    public PIServiceInfo(byte t1, byte t2, int srvid){
        ServiceID = srvid;
        T1 = t1;
        T2 = t2;
    }
    public PIServiceInfo(PIServiceInfo srvInfo) {
        this.ServiceID = srvInfo.ServiceID;
        this.T1 = srvInfo.T1;
        this.T2 = srvInfo.T2;
        this.Location = srvInfo.Location;
        this.Name = srvInfo.Name;
    }

    public PIServiceInfo(byte[] bytes){
        if (bytes.length != 21)
            return;
        byte[] sidBytes = new byte[2];
        System.arraycopy(bytes, 0, sidBytes, 0, 2);
        ServiceID = ByteUtilLittleEndian.getInt(sidBytes);
        T1 = bytes[2];
        T2 = bytes[3];
        Location = bytes[4];
        byte[] NameBytes = new byte[16];
        System.arraycopy(bytes, 5, NameBytes, 0, 16);
        Name = ByteUtilBigEndian.ByteToString(NameBytes);
    }

    public PIServiceInfo() {
        // TODO Auto-generated constructor stub
    }

    public int getServiceID(){
        return ServiceID;
    }

    public byte[] getBytes() {
        byte[] buf = new byte[21];
        byte[] tmp = ByteUtilLittleEndian.getBytes(ServiceID);
        buf[0] = tmp[0];
        buf[1] = tmp[1];
        buf[2] = T1;
        buf[3] = T2;
        buf[4] = Location;
        if (Name != null){
            byte[] NameBytes = Name.getBytes();
            System.arraycopy(NameBytes, 0, buf, 5, NameBytes.length>16?16:NameBytes.length);
        }

        return buf;
    }

}
