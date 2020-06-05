package com.telink.sig.mesh.model;

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
    private static int VD_USR_LIGHT_REPORT_NOACK = 0xE6;
    private static int BIBOO_CMD_VOICE_ENABLE = 0x01;  		      //音箱开关
    private static int BIBOO_CMD_RGB_SET = 0x02;			 // RGB设置
    private static int BIBOO_CMD_YELLOW_SET = 0x03;			 //黄光设置
    private static int BIBOO_CMD_CANDLE_SET = 0x04;			 //-蜡烛光设置

    private static int BIBOO_CMD_ON = 0X01;
    private static int BIBOO_CMD_OFF = 0X00;


    public static byte[] getCandleCommand(){
        byte[] result = new byte[2];
        result[0] = (byte)VD_USR_LIGHT_REPORT_NOACK;
        result[1] = (byte) (BIBOO_CMD_CANDLE_SET << 4| BIBOO_CMD_ON);
        return result;
    }

    public static byte[] getYellowCommand(){
        byte[] result = new byte[2];
        result[0] = (byte)VD_USR_LIGHT_REPORT_NOACK;
        result[1] = (byte) (BIBOO_CMD_YELLOW_SET << 4| BIBOO_CMD_ON);
        return result;
    }

    public static byte[]  getVoiceOnOffCommand(boolean isOn){
        byte[] result = new byte[2];
        result[0] = (byte)VD_USR_LIGHT_REPORT_NOACK;
        if(isOn){
            result[1] = (byte) (BIBOO_CMD_VOICE_ENABLE << 4| BIBOO_CMD_ON);
        }else{
            result[1] = (byte) (BIBOO_CMD_VOICE_ENABLE << 4| BIBOO_CMD_OFF);
        }
        return result;
    }
}
