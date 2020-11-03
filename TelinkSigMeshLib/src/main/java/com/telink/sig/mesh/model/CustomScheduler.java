package com.telink.sig.mesh.model;


import java.io.Serializable;

/**
 * 设置时钟
 *  命令格式：0x 07 AA BB CC DD EE FF GG
 * 功能：设置闹钟
 *     AA: bit7：0为Disable, 1为Enable闹钟
 * bit6：0 单次执行  1 重复执行
 * bit5~bit3:保留
 * bit2~bit0：闹钟编号（取0~7），可设置8组闹钟
 *     BB: 闹钟时间小时部分
 *     CC: 闹钟时间分钟部分
 *              DD ~ GG:  灯控指令集，参考以上固定命令格式
 *              DD: cmd   EE~GG:灯控参数
 * 例如：0号闹钟18:30打开循环控制RGB(黄色) AA[C0]:1100 0000
 * 07 C0‬ 12‬ 1E‬ 02 FF FF 00
 */
public class CustomScheduler implements Serializable {
    public static final int CYCLE_NONE = 0;
    public static final int CYCLE_DAY = 1;
    private boolean enable;
    private int mode; // 闹钟重复模式
    private int num; // 闹钟编号
    private int hours;//BB
    private int minutes;//CC
    private byte[] action;//DD ~ GG

    private byte[] wholeCommand;//0x 07 AA BB CC DD EE FF GG

    public CustomScheduler(byte[] wholeCommand) throws Exception {
        if(wholeCommand.length < 10){
            throw new Exception("Custom Scheduler Command length need 10, but is: " + wholeCommand.length);
        }
        this.wholeCommand = wholeCommand;

        byte AA = wholeCommand[3];
        enable = (AA & (byte)(1 << 7)) != 0;
        mode = AA & (byte)(1 << 6);
        num = AA & 0b00000111;

        hours = wholeCommand[4];
        minutes = wholeCommand[5];

        action = new byte[4];
        System.arraycopy(wholeCommand, 6, action, 0, action.length);
    }

    public byte[] getWholeCommand(){
        return wholeCommand;
    }

    public boolean getEnable(){
        return enable;
    }

    public void setEnable(boolean isEnable){
        byte enableByte = 0;
        if(isEnable){
            enableByte =(byte)(1 << 7);
        }

        byte AA = (byte) (enableByte|mode|num);
        wholeCommand[3] = AA;
    }

    public int getMode(){
        return mode;
    }

    public int getNum(){
        return num;
    }

    public int getHours(){
        return hours;
    }

    public int getMinutes(){
        return minutes;
    }

    public byte[] getAction(){
        return action;
    }


}
