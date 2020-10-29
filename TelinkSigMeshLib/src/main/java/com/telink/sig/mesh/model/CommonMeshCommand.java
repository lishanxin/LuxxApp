package com.telink.sig.mesh.model;

import com.telink.sig.mesh.util.X64Trans;

/**
 * 新增自定义命令opcode:  VD_USR_LIGHT_REPORT_NOACK	 0xE6
 *
 * A）灯控DATA数据包结构：
 * #define VD_USR_DATA_LEN 6
 * typedef struct{
 * 	u8 Cmd;
 * 	u8 User_def[VD_USR_DATA_LEN];
 * 	u8 tid;
 * }vd_rc_key_report_t;
 *
 * typedef enum {
 * 	BIBOO_CMD_VOICE_ENABLE = 0x01,  		      //音箱开关
 * 	BIBOO_CMD_RGB_SET = 0x02,				 // RGB设置
 * 	BIBOO_CMD_YELLOW_SET = 0x03,			 //黄光设置
 * 	BIBOO_CMD_CANDLE_SET = 0x04,			 //-蜡烛光设置
 * }LIGHT_TYPE_CTRL_COMMAND_e;
 */
public class CommonMeshCommand {
//    private static int VD_USR_LIGHT_REPORT_NOACK = 0xE6;
    private static int BIBOO_CMD_VOICE_ENABLE = 0x01;  		      //音箱开关
    private static int BIBOO_CMD_RGB_SET = 0x02;			 // RGB设置
    private static int BIBOO_CMD_YELLOW_SET = 0x03;			 //黄光设置
    private static int BIBOO_CMD_CANDLE_SET = 0x04;			 //-蜡烛光设置

    private static int BIBOO_CMD_ON = 0X01;
    private static int BIBOO_CMD_OFF = 0X00;
    private static byte[] defaultPre = {(byte) 0x00, (byte) 0x01}; // 前置默认值
    private static final int paramLength = 10;


    //设置闪烁模式cmd
    private static int TWINKLE_CMD = 0x05;
    public static int EFFECT_SUNRICE_MODE = 0x00;
    public static int EFFECT_BREATHING_MODE = 0x01;
    public static int EFFECT_RANDOM_MODE = 0x02;
    public static int EFFECT_SPA_MODE = 0x03;


    //同步RTC时间戳
    private static int SYNC_TIME_CMD = 0X06;

    //时钟设置
    private static int CLOCK_SET_CMD = 0X07;



    public static byte[] getCandleCommand(boolean isOn){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte) BIBOO_CMD_CANDLE_SET;
        if(isOn){
            result[index++] = (byte) ( BIBOO_CMD_ON);
        }else{
            result[index++] = (byte) ( BIBOO_CMD_OFF);
        }
        return result;
    }

    public static byte[] getYellowCommand(float whiteRate){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte)BIBOO_CMD_YELLOW_SET;
        int white = (int)(whiteRate * 255);
        result[index++] = (byte) (white & 0xff);
//        if(isOn){
//            result[index++] = (byte) ( BIBOO_CMD_ON);
//        }else{
//            result[index++] = (byte) ( BIBOO_CMD_OFF);
//        }
        return result;
    }

    public static byte[]  getVoiceOnOffCommand(boolean isOn){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte)BIBOO_CMD_VOICE_ENABLE;
        if(isOn){
            result[index++] = (byte) ( BIBOO_CMD_ON);
        }else{
            result[index++] = (byte) ( BIBOO_CMD_OFF);
        }
        return result;
    }

    public static byte[] getRGBCommand(int r, int g, int b){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte)BIBOO_CMD_RGB_SET;
        result[index++] = (byte)r;
        result[index++] = (byte)g;
        result[index++] = (byte)b;

        return result;
    }

    public static byte[] getTwinkleCommand(int mode){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte)TWINKLE_CMD;
        result[index] = (byte)mode;
        return result;
    }


    public static byte[] getSyncTimeCommand(){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte) SYNC_TIME_CMD;
        String time16 = X64Trans.DeciamlToThirtySix(System.currentTimeMillis() / 1000, 16);;
        String timeFormat = String.format("%8s", time16).replace(" ", "0");
        result[index++] = (byte)(int)Integer.valueOf(timeFormat.substring(0, 2), 16);
        result[index++] = (byte)(int)Integer.valueOf(timeFormat.substring(2, 4), 16);
        result[index++] = (byte)(int)Integer.valueOf(timeFormat.substring(4, 6), 16);
        result[index++] = (byte)(int)Integer.valueOf(timeFormat.substring(6), 16);
        return result;
    }

    /**
     * 闹钟设置的命令
     * @param enable 是否开启闹钟
     * @param mode 闹钟模式
     * @param num 闹钟编号
     * @param hours 闹钟时针
     * @param minutes 闹钟分针
     * @param command 闹钟开启的命令
     * @return 闹钟命令
     */
    public static byte[] getClockSetCommand(boolean enable, int mode, int num, int hours, int minutes, byte[] command){
        byte[] result = new byte[paramLength];
        int index = 0;
        result[index++]=defaultPre[0];
        result[index++]=defaultPre[1];
        result[index++] = (byte) CLOCK_SET_CMD;
        byte enableByte = 0;
        if(enable){
            enableByte =(byte)(1 << 7);
        }
        byte modeByte = (byte)(mode << 6) ;
        byte numByte =(byte) num;

        result[index++] = (byte) (enableByte|modeByte|numByte);
        result[index++] = (byte) (hours);
        result[index++] = (byte) (minutes);
        for(int i = 0; i < command.length; i++){
            if(i < 3) continue;
            result[index++] = (byte) (command[i]);
        }
        return result;
    }
}
