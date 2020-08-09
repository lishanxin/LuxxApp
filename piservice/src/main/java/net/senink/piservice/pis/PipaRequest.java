package net.senink.piservice.pis;

import android.app.Activity;
import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.piservice.util.LogUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wensttu on 2016/6/17.
 */
public class PipaRequest implements Serializable{
    private final int defaultExecuteTimeout = 3 * 1000;

    public enum REQUEST_TYPE {
        DISCOVER, REQUEST, SUBSCRIBE, HTTP, UNKNOW;
    }

    public enum REQUEST_STATUS {
        // /请求正等待被执行
        IDLE,
        // /请求正在被执行，正在等待执行结果
        DOING,
        // /在规定时间内未获得执行结果，请求超时
        TIMEOUT,
        // 请求已完成
        DONE,
        // 请求失败
        FAILED,
        // /请求已经失效
        INVALID
    }



    public boolean IsSubscribe;            // 是否已订阅，只有REQUEST_TYPE为SUBSCRIBE才有
    public int PairID;
    public transient PISBase object;
    public REQUEST_TYPE Type;              // 类型
    public REQUEST_STATUS Status;          // 当前状态

    public PipaRequestData RequestData;    // 数据部分
    public transient PISBase.PISCommandAckData ackData;

    public transient long ExecuteTime;                // 最后的执行时间
    public long ExecuteTimeout;             // 允许的执行最大时间
    public int RetryTimes = 1;              // 重试次数

    /*! 对于virtual的request，其并不真正发送请求，只是用来进行确认 */
    public boolean isVirtual = false;
    /*! 对于组请求，其需要多个virtual的Request来确认组内的每一个PIService都响应了请求*/
    public ArrayList<PipaRequest> groupRequests;

    public PipaRequest originalReq;
    public byte[] responsePara;
    public Object userData = null;
    /**
     * errorCode:
     * 0, REQUEST_RESULT_COMPLETE         - 成功
     * -1, REQUEST_RESULT_ERROR,           - 错误
     * -2, REQUEST_RESULT_ERROR_TIMEOUT    - 超时
     * -3, REQUEST_RESULT_ERROR_COMMAND    - 错误的request类型
     * -4, REQUEST_RESULT_ERROR_CANCEL     - 请求取消
     * -5，REQUEST_RESULT_ERROR_PARAMTER   - 错误的参数
     * -6, REQUEST_RESULT_ERROR_OBJECT     - PISBase对象不可用
     **/
    public static final int REQUEST_RESULT_SUCCESSED = 0;
    public static final int REQUEST_RESULT_FAILED = -1;
    public static final int REQUEST_RESULT_ERROR_TIMEOUT = -2;
    public static final int REQUEST_RESULT_ERROR_COMMAND = -3;
    public static final int REQUEST_RESULT_ERROR_CANCEL = -4;
    public static final int REQUEST_RESULT_ERROR_PARAMTER = -5;
    public static final int REQUEST_RESULT_ERROR_OBJECT = -6;
    public static final int REQUEST_RESULT_ERROR_OFFLINE = -7;

    public int errorCode = 0;
    public String errorMessage;
    public Exception error;

    private static final String TAG = "PipaRequest";
    private long mTimeout = 3000; //允许的超时时间（单位：毫秒）

    public static final int Executing_Max = 5;

    public enum WEBSERVICE_ACTION {
        WEB_ADD,        // 新增，通过WebService 接口添加数据。
        WEB_DEL,        // 删除，通过WebService 接口删除数据。
        WEB_UDPATE,     // 修改，通过WebService 接口更新数据。
        WEB_QUERY       // 查询，通过WebService 接口查询数据。
    }

    public static final int REQUEST_SEND_SUCCESS = 0;
    public static final int REQUEST_SEND_FAILED = 1;
    public static final int REQUEST_EXISED = 2;

    // 判断请求是否生效
    public static final int REQUEST_RESULT_COMPLETE = 0;
    public static final int REQUEST_RESULT_ERROR = 1;
    public static final int REQUEST_RESULT_TIMEOUT = 2;
    /*
     * 用于标记消息是否需要ACK flag : 0-不需要 1-需要
     */
    public boolean NeedAck = false;

    public PipaRequest(PISBase obj, PipaRequestData data, boolean needAck) {
        PairID = obj.PairID;
        object = obj;
        RequestData = data;
        Status = REQUEST_STATUS.IDLE;
        NeedAck = needAck;
        ExecuteTimeout = defaultExecuteTimeout;
    }

    public PipaRequest(PISBase obj, byte cmd, byte[] data, int len, boolean ack) {
        RequestData = new PipaRequestData(cmd, len, data);
        PairID = obj.PairID;
        object = obj;
        Status = REQUEST_STATUS.IDLE;
        Type = REQUEST_TYPE.REQUEST;
        IsSubscribe = NeedAck = ack;
        ExecuteTimeout = defaultExecuteTimeout;
    }

    public PipaRequest(PISBase obj, REQUEST_TYPE type, boolean isSubscribe, PipaRequestData data, boolean needAck) {
        PairID = obj.PairID;
        object = obj;
        RequestData = data;
        Type = type;
        Status = REQUEST_STATUS.IDLE;
        IsSubscribe = isSubscribe;
        NeedAck = needAck;
        ExecuteTimeout = defaultExecuteTimeout;
    }

    public PipaRequest(PISBase obj, PipaRequest req) {
        PairID = req.PairID;
        object = obj;
        RequestData = req.RequestData;
        Type = req.Type;
        Status = req.Status;
        IsSubscribe = req.IsSubscribe;
        ExecuteTime = req.ExecuteTime;
        ExecuteTimeout = req.ExecuteTimeout;
    }

    public void setRetry(int retry) {
        RetryTimes = retry;
    }

    public interface OnPipaRequestStatusListener {
        /*
         * 请求发送成功后调用
         */
        void onRequestStart(PipaRequest req);

        /*
         * 收到服务器端的响应后调用通知UI，并返回执行结果
         */
        void onRequestResult(PipaRequest req);
    }

    private OnPipaRequestStatusListener mListener = null;

    public void setOnPipaRequestStatusListener(OnPipaRequestStatusListener l) {
        mListener = l;
    }

    /**
     * 执行请求.(PIPA下的远程请求）
     */
    public int execute() {
        int ret = REQUEST_SEND_FAILED;
//        if (this.isVirtual){
//            this.ExecuteTime = System.currentTimeMillis();
//            return PipaRequest.REQUEST_SEND_SUCCESS;
//        }
        PISManager pm  = PISManager.getInstance();

        if (pm.hasCloudConnect())
            ret = remote_execute();
        else {
            if (pm != null)
                ret = pm.Request(this);
        }
        return ret;
    }

    public int remote_execute(){
        int ret = REQUEST_SEND_FAILED;
        if (PairID == 0 || Status == REQUEST_STATUS.DOING || object == null) {
            errorCode = (-5);
            Status = REQUEST_STATUS.INVALID;
            return ret;
        }
//        if ((System.currentTimeMillis() - ExecuteTime > mTimeout) || (--RetryTimes) < 0) {
//            Status = REQUEST_STATUS.TIMEOUT;
//            return ret;
//        }
        Status = REQUEST_STATUS.DOING;
        ExecuteTime = System.currentTimeMillis();
        if (isVirtual)
            return REQUEST_SEND_SUCCESS;

        switch (Type) {
            case DISCOVER:
                ret = PisInterface.pisDiscover(0, null);
                break;
            case REQUEST:
                LogUtils.i(TAG, "send data to pairid=" + PairID);
                ret = PisInterface.pisRequest(PairID, RequestData.Command,
                        RequestData.Length, RequestData.Data, NeedAck ? 1 : 0);
                if (NeedAck)
                    report_start((Activity)PISManager.getDefaultActivityContext());
                break;
            case SUBSCRIBE:
                ret = PisInterface.pisSubscribe(PairID, RequestData.Command,
                        IsSubscribe);
                if (NeedAck)
                    report_start((Activity)PISManager.getDefaultActivityContext());
                break;
            default:
                ret = (-3);    //错误的request类型
                break;
        }

        if (ret == REQUEST_SEND_SUCCESS) {
            this.ExecuteTime = System.currentTimeMillis();
        } else {
            errorCode = ret;
            Status = REQUEST_STATUS.INVALID;
        }

        return ret;
    }
    /**
     * 当APP为本地版本的时候，PISManager.hasCloudConnect=false，则相关请求都会调用本地方法
     * 如果该PipaRequest支持本地方法，则重载此方法即可。
     * @return
     */
    public int local_execute(){
        return remote_execute();
    }

    public boolean equals(PipaRequest req) {
        return (PairID == req.PairID &&
                Type == req.Type &&
                IsSubscribe == req.IsSubscribe &&
                RequestData.equals(req.RequestData));

    }

    public void report_finished(Activity activity) {
//        Activity activity = (Activity)PISManager.getApplicationContext();
        if (mListener != null && activity != null) {
            final PipaRequest req = this;
            if (req.isVirtual){
                try {
                    mListener.onRequestResult(req);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mListener.onRequestResult(req);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void report_start(Activity activity ) {
        if (mListener != null && activity != null) {
            final PipaRequest req = this;
            if (req.isVirtual) {
                try {
                    mListener.onRequestStart(req);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mListener.onRequestStart(req);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    }

    private int reponseMessage = 0;
    private int reponseCode = 0;
    private byte[] reponseData = null;

    public int getReponseMessage(){
        return reponseMessage;
    }

    public int getReponseCode(){
        return reponseCode;
    }

    public byte[] getReponseData(){
        return reponseData;
    }
    public void setResponsePara(int msg, int resultCode, byte[] ackData){
        reponseMessage = msg;
        reponseCode = resultCode;
        reponseData = ackData;

    }

    public static byte[] buildAckData(byte cmd, int len, byte[] data){
        if (data != null && data.length != len)
            throw new IllegalArgumentException("data length not match");
        if (len > 1000)
            throw new IllegalArgumentException("data length too long[" + len + "]");

        byte[] ackBytes = new byte[3 + len];
        ackBytes[0] = cmd;
        ackBytes[1] = (byte)(len & 0xFF);
        ackBytes[2] = (byte)((len & 0xFF00) >> 8);

        if (len > 0)
            System.arraycopy(data, 0, ackBytes, 3, len);

        return ackBytes;

    }
}