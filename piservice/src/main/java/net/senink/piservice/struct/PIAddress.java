package net.senink.piservice.struct;

import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PIAddress implements Serializable {
    private int PanID;
    private int ShortAddr;

    public PIAddress(){
        super();
    }
    public PIAddress(int pid, int saddr){
        PanID = pid;
        ShortAddr = saddr;
    }

    public PIAddress(byte[] addr){
        PanID = (addr[2]&0xFF) + ((addr[3]&0xFF) << 8);
        ShortAddr = (addr[0]&0xFF) + ((addr[1]&0xFF) << 8);
    }

    public int getPanId(){
        return PanID;
    }
    public void setPanId(int pid){
        PanID = pid;
    }

    public int getShortAddr(){
        return ShortAddr;
    }
    public void setShortAddr(int saddr){
        ShortAddr = saddr;
    }

    public byte[] getBytes(){
        byte[] addrBytes = new byte[4];
        addrBytes[0] = (byte)(ShortAddr & 0xFF);
        addrBytes[1] = (byte)((ShortAddr & 0xFF00) >> 8);
        addrBytes[2] = (byte)(PanID & 0xFF);
        addrBytes[3] = (byte)((PanID & 0xFF00) >> 8);

        return addrBytes;
    }
}
