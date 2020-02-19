package net.senink.piservice.http;

import android.app.Activity;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wensttu on 2016/7/8.
 */
public abstract class HttpRequest {
    public static final int ConnectTimeout = 30 * 1000;
    public Exception error;
    public int errorCode = 0;
    public String errorMessage = null;

    public enum RESULT_TYPE {
        JSON, IMAGE, UNKNOW;
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

    public interface OnPipaRequestStatusListener {
        /*
         * 请求开始后调用
         */
        void onRequestStart(HttpRequest req);

        /*
         * 请求完成后调用，并返回执行结果
         */
        void onRequestResult(HttpRequest req);
    }

    /**
     * 用来处理返回结果数据，需要子类来实现
     * 子类在实现该函数的时候，应该将返回的结果放置在对象的结果属性中
     * @param status 请求执行状态
     * @param data   请求返回数据，根据不同的RESULT_TYPE，其返回数据是不同的
     * @param req    请求对象
     * @return  返回数据处理的结果，正常情况下返回0，如果处理异常，则返回错误码
     */
    public abstract int onProcess(REQUEST_STATUS status, Object data, HttpRequest req);

    /**
     * 远程执行方法，需要子类来实现
     */
    public abstract Object remote_execute();


    /**
     * 本地执行方法，需要子类来实现
     */
    public abstract Object local_execute();

    protected String mUrl;
    protected List<NameValuePair> mPostList;
    protected RESULT_TYPE mResultType;

    /**
     * 构造函数
     * @param url       包含基本的URL以及所需要的GET字段，不包含user及pkey字段
     * @param postList  需要执行POST方法的参数对列表
     * @param type      结果类型，目前支持返回JSON及Image
     */
    public HttpRequest(String url, List<NameValuePair> postList, RESULT_TYPE type){
        super();

        mUrl = url;
        mPostList = postList;
        mResultType = type;
    }

    public String getUrl(){
        return mUrl;
    }

    public List<NameValuePair> getPostList(){
        return mPostList;
    }

    public RESULT_TYPE getResultType(){
        return mResultType;
    }

    private OnPipaRequestStatusListener mListener = null;

    public void setOnPipaRequestStatusListener(OnPipaRequestStatusListener l) {
        mListener = l;
    }


    /**
     * 返回Request执行完成状况，其执行在UI线程
     * @param activity 应该为在Application整个生存周期都存在的activity
     */
    public void report_finished(Activity activity) {
        if (mListener != null && activity != null) {
            final HttpRequest req = this;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onRequestResult(req);
                }
            });
        }
    }

    /**
     * 报告Request开始执行，其执行在UI线程
     * @param activity
     */
    public void report_start(Activity activity ) {
        if (mListener != null && activity != null) {
            final HttpRequest req = this;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListener.onRequestStart(req);
                }
            });
        }
    }

    protected static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH",
                Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        sdf.format(date);
        return sdf.format(date);
    }
    protected static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    protected static String getPKey(String password) {
        return MD5(MD5(MD5("android3t6i") + getNowDate()) + password); // MD5(MD5(password)));
    }
}
