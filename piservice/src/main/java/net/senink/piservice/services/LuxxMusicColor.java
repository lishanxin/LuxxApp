package net.senink.piservice.services;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;

/**
 * Created by wensttu on 2016/7/11.
 */
public class LuxxMusicColor extends PISxinColor {
    private static final byte PIS_CMD_MUSIC_STATUS_SET = (byte)0xAA;  //音乐开关
    private static final byte PIS_CMD_MUSIC_STATUS_GET = (byte)0xAB;  //获取Speaker状态

    private transient boolean btAudioEnable;
    public LuxxMusicColor(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo);

        Commands.add(new PICommandProperty(PIS_CMD_MUSIC_STATUS_SET,
                "音乐开关",
                "控制蓝牙音频设备的开启及关闭",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_MUSIC_STATUS_GET,
                "音箱状态",
                "获取蓝牙音频设备的当前状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));


    }


    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_MUSIC_STATUS_SET:{
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        if (ackData.rawRequestData.Data[0] == 0)
                            btAudioEnable = false;
                        else
                            btAudioEnable = true;
                        break;
                    }
                    case PIS_CMD_MUSIC_STATUS_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.data == null)
                            break;
                        if (ackData.data[0] == 0)
                            btAudioEnable = false;
                        else
                            btAudioEnable = true;
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

    /**
     * 公有方法
     */

    public PipaRequest commitBtAudioStatus(boolean enable){
        byte[] isOnByte = new byte[1];
        isOnByte[0] = (byte)(enable?1:0);
        return new PipaRequest(this, PIS_CMD_MUSIC_STATUS_SET, isOnByte, 1, true);
    }

    public PipaRequest fecthBtAudioStatus(){
        return new PipaRequest(this, PIS_CMD_MUSIC_STATUS_GET, null, 0, true);
    }

    public boolean isSpeakerEnable(){
        return btAudioEnable;
    }
}
