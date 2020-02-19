package net.senink.piservice.struct;

import java.io.Serializable;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PipaRequestData implements Serializable{
    public byte Command;
    public int Length;
    public byte[] Data;

    public PipaRequestData() {
        // TODO Auto-generated constructor stub
        Command = 0;
        Length = 0;
        Data = null;
    }

    public PipaRequestData(byte cmd, int length, byte[] data) {
        Command = cmd;
        Length = length;
        Data = data;
    }

    public boolean equals(PipaRequestData reqData) {
        boolean isEqualed = (Command == reqData.Command &&
                Length == reqData.Length);
        if (Data == null && Data == reqData.Data)
            return isEqualed;
        else if (Data != null && reqData.Data != null) {
            return (isEqualed && Data.equals(reqData.Data));
        }
        return isEqualed;
    }
}
