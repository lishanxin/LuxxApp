package net.senink.piservice.struct;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wensttu on 2016/9/2.
 */
public class LightTimerItem implements Serializable{
    public static final int CYCLE_NONE = 0;
    public static final int CYCLE_DAY = 1;
    public static final int CYCLE_MONTH = 2;

    public static final int INVAILD_TIMER_ID = 0xFFFFFFFF;

    private byte[] dataBytes;


    public LightTimerItem(int tid){
        dataBytes = new byte[8];
        dataBytes[0] = (byte)(tid & 0xF0);
    }
    public LightTimerItem(@NonNull byte[] data){
        dataBytes = new byte[data.length];

        System.arraycopy(data, 0, dataBytes, 0, data.length);
        LogUtils.i("PisDevice", "tid=" + (dataBytes[0]&0xF0) + "time" + getTimeString());
    }

    public LightTimerItem(int tid, int repeatType, int time, int srvid, @NonNull PipaRequestData reqData){
        dataBytes = new byte[8 + reqData.Length];

        dataBytes[0] = (byte)((repeatType & 0x3) | (tid & 0xF0));
        dataBytes[1] = (byte)(time & 0xFF);
        dataBytes[2] = (byte)((time & 0xFF00)>>8);
        dataBytes[3] = (byte)((time & 0xFF0000)>>16);
        dataBytes[4] = (byte)((time & 0xFF000000)>>24);
        dataBytes[5] = (byte)(srvid & 0xFF);
        dataBytes[6] = reqData.Command;
        dataBytes[7] = (byte)(reqData.Length & 0xFF);
        System.arraycopy(reqData.Data, 0, dataBytes, 8, reqData.Length);
    }

    public byte[] getBytes(){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        byte[] resultByte = new byte[dataBytes.length];
        System.arraycopy(dataBytes, 0, resultByte, 0, dataBytes.length);

        return resultByte;
    }

    public int getTimerId(){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

//        if ((dataBytes[0] & 0xF0) == 0xF0)
//            return 0xFFFFFFFF;
//        else
            return (dataBytes[0]&0xF0);
    }

    public int getRepeatType(){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        return (dataBytes[0]&0x3);
    }

    public void setRepeatType(int repeatType){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        dataBytes[0] = (byte)((dataBytes[0] & (~0x3)) | (repeatType & 0x3));
    }

    public String getTimeString(){
        long time = getTime();

        return String.format("%02d:%02d", (time%86400)/3600, (time%3600)/60);

    }
    public int getTime(){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        byte[] timerBytees = new byte[4];
        System.arraycopy(dataBytes, 1, timerBytees, 0, 4);

        return ByteUtilLittleEndian.getInt(timerBytees);
    }

    public void setTime(int time){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        byte[] timerBytes = ByteUtilLittleEndian.getBytes(time);

        System.arraycopy(timerBytes, 0, dataBytes, 1, 4);
    }

    public int getServiceId(){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        return dataBytes[5];
    }

    public void setServiceId(int srvid){
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        dataBytes[5] = (byte)(srvid & 0xFF);
    }

    public PipaRequestData getRequestData() throws ArrayIndexOutOfBoundsException{
        PipaRequestData reqData = null;
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");
        try {
            byte[] reqDataBytes = new byte[dataBytes[7]];
            System.arraycopy(dataBytes, 8, reqDataBytes, 0, dataBytes[7]);
            reqData = new PipaRequestData(dataBytes[6], dataBytes[7], reqDataBytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return reqData;
    }

    public void setRequestData(@NonNull PipaRequestData reqData){
        byte[] tempBytes = null;
        if (dataBytes == null)
            throw new IllegalArgumentException("data is null");

        if (dataBytes.length < (reqData.Length + 8)){
            tempBytes = new byte[reqData.Length + 8];
            System.arraycopy(dataBytes, 0, tempBytes, 0, 6);
            dataBytes = tempBytes;
        }
        dataBytes[6] = reqData.Command;
        dataBytes[7] = (byte)(reqData.Length & 0xFF);
        System.arraycopy(dataBytes, 8, reqData.Data, 0, reqData.Length);
    }
}
