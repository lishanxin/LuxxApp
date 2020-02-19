package net.senink.piservice.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.SparseArray;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PICommandProperty;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.util.ByteUtilLittleEndian;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wensttu on 2016/7/26.
 */
public class PISXinInsoles extends PISBase {

    public static final byte PIS_CMD_INSOLE_HEAT_ONOFF = (byte)0x90;
    public static final byte PIS_CMD_INSOLE_TEMP_SET   = (byte)0x91;
    public static final byte PIS_CMD_INSOLE_TEMP_GET   = (byte)0x92;
    public static final byte PIS_CMD_INSOLE_GET_STATE  = (byte)0x93;
    public static final byte PIS_CMD_INSOLE_MODE_SET   = (byte)0x94;
    public static final byte PIS_CMD_INSOLE_TIME_SYN   = (byte)0x95;
    public static final byte PIS_CMD_INSOLE_CURTIME_GET      = (byte)0x96;
    public static final byte PIS_CMD_INSOLE_TIMEACTION_SET   = (byte)0x97;
    public static final byte PIS_CMD_INSOLE_TIMEACTION_UNSET = (byte)0x98;
    public static final byte PIS_CMD_INSOLE_TIMEACTION_GET   = (byte)0x99;
    public static final byte PIS_CMD_INSOLE_STEP_GET_BYDAY   = (byte)0x9A;
    public static final byte PIS_CMD_INSOLE_STEP_GET_BYHOUR  = (byte)0x9B;

    private SparseArray<Integer> mStepsByDay = null;
    private SparseArray<Integer> mStepsByHour = null;
    private SparseArray<byte[]> mTimers = null;

    private byte mMode = 0;
    private byte mHeatStatus = 0;
    private byte[] mTemperature = null;
    private byte[] mStateBytes = null;


    public PISXinInsoles(PIAddress addr, PIServiceInfo srvInfo){
        super(addr, srvInfo, SERVICE_TYPE_SERVICE);

        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_HEAT_ONOFF,
                "加热开关",
                "设置是否开始加热",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TEMP_SET,
                "温度设置",
                "设置加热的目标温度",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TEMP_GET,
                "获取温度",
                "获取当前的目标温度",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_GET_STATE,
                "状态获取",
                "获取当前的状态",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_MODE_SET,
                "设置工作模式",
                "设置当前的工作模式，包括手动，恒温，智能三种模式",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TIME_SYN,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_CURTIME_GET,
                "获取时间",
                "获取鞋垫的当前时间",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TIMEACTION_SET,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TIMEACTION_UNSET,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_TIMEACTION_GET,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_STEP_GET_BYDAY,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));
        Commands.add(new PICommandProperty(PIS_CMD_INSOLE_STEP_GET_BYHOUR,
                "同步时间",
                "设置鞋垫的时间，使之与当前时间同步",
                PipaRequest.REQUEST_TYPE.REQUEST,
                true));

//        tasks = new SparseArray<PISXinInsole.TimeAction>();
        mStepsByDay = new SparseArray<>();
        mStepsByHour = new SparseArray<>();
        mTimers = new SparseArray<>();

    }

    /**
     * 开始/停止加热
     *
     * @param isOn   是否打开
     */
    public PipaRequest commitStatus(boolean isOn, boolean ack) {
        byte[] data = new byte[1];
        data[0] = (byte) ((isOn ? 0x1 : 0x0) & 0xFF);

        return new PipaRequest(this, PIS_CMD_INSOLE_HEAT_ONOFF, data, 1, ack);
    }

    /**
     * 获取状态
     *
     */
    public PipaRequest updateStatus() {
        return new PipaRequest(this, PIS_CMD_INSOLE_GET_STATE, null, 0, true);
    }


    /**
     * 设置温度
     *
     * @param low
     *            温度下限
     * @param high
     *            温度上限
     * @param ack
     *            是否有信息返回
     *
     */
    public PipaRequest commitTemperature(byte low, byte high, boolean ack) {
        byte[] data = new byte[2];
        data[0] = low;
        data[1] = high;

        return new PipaRequest(this, PIS_CMD_INSOLE_TEMP_SET, data, 2, ack);
    }

    /**
     * 获取之前设置的温度
     *
     */
    public PipaRequest getTemperature() {
        return new PipaRequest(this, PIS_CMD_INSOLE_TEMP_GET, null, 0, true);
    }

    /**
     * 设置模式
     *
     * @param mode
     *            工作模式
     * @param ack
     *            是否有信息返回
     */
    public PipaRequest commitMode(byte mode, boolean ack) {
        byte[] data = new byte[1];
        data[0] = mode;

        return new PipaRequest(this, PIS_CMD_INSOLE_MODE_SET, data, 1, ack);

    }

    /**
     * 对时
     *
     * @param time
     *            时间(秒为单位)
     * @param ack
     *            是否有消息返回
     */
    public PipaRequest syncTime(int time, boolean ack) {
        byte[] data = new byte[4];
        byte[] times = ByteUtilLittleEndian.getBytes(time);

        System.arraycopy(times, 0, data, 0, times.length);

        return new PipaRequest(this, PIS_CMD_INSOLE_TIME_SYN, times, 4, ack);
    }

    /**
     * 设置定时任务
     *
     * @param taskId
     *            定时任务ID, 0xFF则表示为新增，否则将会覆盖指定id的定时器
     * @param time
     *            以秒为单位
     * @param isHeated
     *            是否加热
     * @param isRepeat
     *           是否重复
     * @param isRun
     *            是否运行
     * @param ack
     *            是否有信息返回
     */
    public PipaRequest commitTimer(byte taskId, int time, boolean isHeated, boolean isRepeat,
                         boolean isRun, boolean ack) {
        byte action = (byte)(isHeated?0x01:0x0);
        byte repeat = (byte)(isRepeat?0x01:0x0);
        byte valid = (byte)(isRun?0x01:0x0);
        byte[] data = new byte[6];
        data[0] = (byte) ((valid << 5) | (repeat << 4) | (taskId & 0x0F));
        byte[] times = ByteUtilLittleEndian.getBytes(time);
        System.arraycopy(times, 0, data, 1, times.length);
        data[5] = action;

        return new PipaRequest(this, PIS_CMD_INSOLE_TIMEACTION_SET, data, data.length, ack);
    }

    /**
     * 取消定时任务
     *
     * @param taskId
     *            要取消的定时任务0xFF，表示取消全部
     * @param ack
     *            是否有消息返回
     */
    public PipaRequest removeTimer(byte taskId, boolean ack) {
        byte[] data = new byte[1];
        data[0] = taskId;

        return new PipaRequest(this, PIS_CMD_INSOLE_TIMEACTION_UNSET, null, 0, ack);
    }

    /**
     * 获取定时任务
     *
     */
    public PipaRequest updateTimer() {

        return new PipaRequest(this, PIS_CMD_INSOLE_TIMEACTION_GET, null, 0, true);
    }

    /**
     * 获取以天为单位的步数，共7天
     *
     */
    public PipaRequest updateStepsByDays() {
        return new PipaRequest(this, PIS_CMD_INSOLE_STEP_GET_BYDAY, null, 0, true);
    }

    /**
     * 获取当天以小时为单位的步数，共24小时
     *
     */
    public PipaRequest getStepsByHours() {
        return new PipaRequest(this, PIS_CMD_INSOLE_STEP_GET_BYHOUR, null, 0, true);
    }

    private void processTimerData(byte[] dataBytes){
        if (mTimers == null)
            mTimers = new SparseArray<>();
        mTimers.clear();

        if (dataBytes != null && dataBytes.length >= 7) {
            int count = dataBytes[0];
            for (int i = 0; i < count; i++) {
                if ((i + 1) * 6 < dataBytes.length) {
                    byte[] infos = new byte[6];
                    System.arraycopy(dataBytes, i * 6 + 1, infos, 0, infos.length);
                    mTimers.put(infos[0]&0x0F, infos);
                }
            }
        }
    }

    private void processStepsByDayData(byte[] dataBytes){
        if (mStepsByDay == null)
            mStepsByDay = new SparseArray<>();

        if (dataBytes != null && dataBytes.length >= 1) {
            int count = dataBytes[0];
            for (int i = 0; i < count; i++) {
                if ((i + 1) * 4 < dataBytes.length) {
                    byte[] infos = new byte[4];
                    System.arraycopy(dataBytes, i * 4 + 1, infos, 0, infos.length);
                    int steps = ByteUtilLittleEndian.getInt(infos);
                    int curDay = (int)(System.currentTimeMillis()/(3600*24*1000));

                    mStepsByDay.put(curDay, steps);
                }
            }
        }
    }

    private void processStepsByHourData(byte[] dataBytes){
        if (mStepsByHour == null)
            mStepsByHour = new SparseArray<>();

        if (dataBytes != null && dataBytes.length >= 2) {
            int len = dataBytes.length / 2;
            for (int i = 0; i < len; i++) {
                byte[] infos = new byte[2];
                System.arraycopy(dataBytes, i * 2, infos, 0, infos.length);
                int steps = ByteUtilLittleEndian.getShort(infos);
                int curHour = (int)(System.currentTimeMillis()/(3600000));

                mStepsByHour.put(curHour, steps);
            }
        }
    }


    @Override
    protected void onProcess(int msg, int hPara, Object lPara) {
        switch (msg) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK: {
                PISCommandAckData ackData = (PISCommandAckData)lPara;
                switch (ackData.Command) {
                    case PIS_CMD_INSOLE_HEAT_ONOFF: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;

                        mHeatStatus = ackData.rawRequestData.Data[0];
                    }
                        break;
                    case PIS_CMD_INSOLE_TEMP_SET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        mTemperature = ackData.rawRequestData.Data;
                    }
                        break;
                    case PIS_CMD_INSOLE_TEMP_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        mTemperature = ackData.data;
                    }
                        break;
                    case PIS_CMD_INSOLE_GET_STATE: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        mStateBytes = ackData.data;
                    }
                        break;
                    case PIS_CMD_INSOLE_MODE_SET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        mMode = ackData.rawRequestData.Data[0];
                    }
                        break;
                    case PIS_CMD_INSOLE_TIME_SYN: {

                    }
                    break;
                    case PIS_CMD_INSOLE_CURTIME_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                    }
                    break;
                    case PIS_CMD_INSOLE_TIMEACTION_SET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;

                        byte tid = (byte)(ackData.rawRequestData.Data[0] & 0xF);
                        mTimers.put(tid, ackData.rawRequestData.Data);
                    }
                    break;
                    case PIS_CMD_INSOLE_TIMEACTION_UNSET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED ||
                                ackData.rawRequestData == null)
                            break;
                        byte tid = ackData.rawRequestData.Data[0];
                        mTimers.remove(tid);
                    }
                    break;
                    case PIS_CMD_INSOLE_TIMEACTION_GET: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        processTimerData(ackData.data);
                    }
                    break;
                    case PIS_CMD_INSOLE_STEP_GET_BYDAY: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        processStepsByDayData(ackData.data);

                    }
                    break;
                    case PIS_CMD_INSOLE_STEP_GET_BYHOUR: {
                        if (hPara != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            break;
                        processStepsByHourData(ackData.data);
                    }
                    break;
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


    public class InsoleService extends Service {
        //为日志工具设置标签
        private final String TAG = "InsoleService";

        private LocalBinder mBinder = new LocalBinder();

        private HashMap<String, PISXinInsoles> InsolePairList;

        private final int MSG_INSOLE_SERVICE_CHECK = 1000;

        @SuppressLint("HandlerLeak")
        protected Handler mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case MSG_INSOLE_SERVICE_CHECK:

                        break;
                    default:
                        break;
                }
            }

        };
        //该服务不存在需要被创建时被调用，不管startService()还是bindService()都会启动时调用该方法
        @Override
        public void onCreate() {
            InsolePairList = new HashMap<>();


            super.onCreate();
        }

        @Override
        public void onStart(Intent intent, int startId) {


            super.onStart(intent, startId);
        }
        @Override
        public void onDestroy() {
//            for (int i = 0; i < HashMap.size(); i++){
//                PinmInterface pinm = pinmList.valueAt(i);
//                if (pinm != null && pinm.getStatus() == PinmInterface.PINM_CONNECT_STATUS_CONNECTED)
//                    pinm.disconnect();
//            }

            super.onDestroy();
        }
        //其他对象通过bindService 方法通知该Service时该方法被调用
        @Override
        public IBinder onBind(Intent intent) {
            try {
                if (mBinder == null)
                    mBinder = new LocalBinder();

            }catch (Exception e){
                e.printStackTrace();
            }
            return mBinder;
        }
        //其它对象通过unbindService方法通知该Service时该方法被调用
        @Override
        public boolean onUnbind(Intent intent) {

            return super.onUnbind(intent);
        }

        public void putInsole(PISXinInsoles insole){
            if (InsolePairList == null)
                InsolePairList = new HashMap<>();
            if (InsolePairList.containsKey(insole.getPISKeyString()))
                return;

            InsolePairList.put(insole.getPISKeyString(), insole);

            mHandler.removeMessages(MSG_INSOLE_SERVICE_CHECK);
            mHandler.sendEmptyMessage(MSG_INSOLE_SERVICE_CHECK);
        }

        public PISXinInsoles getInsolePair(PISXinInsoles insole){
//            if (pinmList == null)
//                return null;
//            try {
//                return pinmList.get(PINM_TYPE);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            return null;
        }


        public class LocalBinder extends Binder {
            public InsoleService getService() {
                // Return this instance of LocalService so clients can call public methods
                return InsoleService.this;
            }
        }

    }
}
