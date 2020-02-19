package net.senink.piservice.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.telephony.TelephonyManager;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.UserInfo;
import net.senink.piservice.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * Created by wensttu on 2016/7/9.
 */
public class HttpUserInfo {
    public static final String KEYSTRING_BLEID = "bleId";
    public static final String KEYSTRING_BLEKEY = "bleKey";

    /**
     * 获取蓝牙ID及Key的Request
     */
    public class bleHttpRequest extends JsonHttpRequest{
        public String password;
        public String bleKey;
        public int bleId;
        public bleHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        /**
         * 本地执行方法
         */
        @Override
        public JSONObject local_execute(){
            int bleID = 0;
            String bleKey = null;
            JSONObject result = null;

            try {
                //如果bleID不存在，则从0x7000~0x7100之间随机生成一个作为bleID，并保存在本地
                Random random=new Random();
                bleID = 0x7000 + random.nextInt(0x100);
                //使用pwd作为
                String pwd = PISManager.getInstance().getUserObject().pwd;
                bleKey = MD5(MD5(pwd));

                String jsonFormat = "{\"state\":true,\"data\":{\"bleId\":%d,\"bleMac\":\"\",\"classId\":\"\",\"bleKey\":\"%s\"},\"dataCount\":4,\"error\":0,\"errorInfo\":null}";
                String jsonStr = String.format(Locale.getDefault(), jsonFormat, bleID, bleKey);
                try {
                    result = new JSONObject(jsonStr);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req){
            if (!(data instanceof JSONObject)){
                throw new IllegalArgumentException("need JSONObject class");
            }
            JSONObject obj = (JSONObject)data;
            try {
                if (obj != null && obj.has("state")) {
                    if (obj.getBoolean("state")) {
                        if (obj.has("data")) {
                            JSONObject object = obj.getJSONObject("data");
                            if (object != null) {
                                if (object.has(KEYSTRING_BLEID)) {
                                    bleId = object.getInt(KEYSTRING_BLEID);
                                }
                                if (object.has(KEYSTRING_BLEKEY)) {
                                    bleKey = object.getString(KEYSTRING_BLEKEY);
                                }
                                req.errorCode = PipaRequest.REQUEST_RESULT_SUCCESSED;
                            }
                        }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }
    }

    public class userInfoHttpRequest extends JsonHttpRequest{
        private UserInfo mUserInfo = null;

        public userInfoHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        public UserInfo getUserInfo(){
            return mUserInfo;
        }

        /**
         * 本地执行方法
         */
        @Override
        public JSONObject local_execute(){
            JSONObject result = null;

            try {
                UserInfo uInfo = PISManager.getInstance().getUserObject();
                try {
                    String jsonFormat = "{\"state\":true,\"data\":" +
                            "{\"id\":\"%d\",\"groupId\":\"%d\",\"loginUser\":\"%s\",\"nickName\":%s," +
                            "\"openid\":%s,\"nickSrc\":\"%s\",\"chineseName\":%s,\"email\":\"%s\","+
                            "\"tel\":\"%s\",\"mobile\":%s,\"msn\":%s,\"qq\":%s,\"address\":%s," +
                            "\"companyName\":%s,\"message\":%s,\"visible\":\"%d\",\"prices\":\"%f\"," +
                            "\"nickSrcMid\":\"%s\"},\"dataCount\":18,\"error\":0,\"errorInfo\":null}";
                    if (uInfo != null){
                        String jsonStr = String.format(Locale.getDefault(),
                                jsonFormat,
                                uInfo.id,
                                uInfo.groupId,
                                uInfo.loginUser,
                                uInfo.nickName,
                                uInfo.openId,
                                uInfo.nickSrc,
                                uInfo.chineseName,
                                uInfo.email,
                                uInfo.tel,
                                uInfo.mobile,
                                uInfo.msn,
                                uInfo.qq,
                                uInfo.address,
                                uInfo.companyName,
                                uInfo.message,
                                uInfo.visible,
                                uInfo.prices,
                                uInfo.nickSrcMid);

                        result = new JSONObject(jsonStr);

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req) {
            if (!(data instanceof JSONObject)){
                throw new IllegalArgumentException("need JSONObject class");
            }
            try {
                JSONObject obj = (JSONObject) data;
                if (obj != null && !obj.isNull("data")) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    mUserInfo = new UserInfo();
                    if (!jsonObject.isNull("msn")) {
                        mUserInfo.msn = jsonObject.getString("msn");
                    }
                    if (!jsonObject.isNull("visible")) {
                        mUserInfo.visible = jsonObject.getInt("visible");
                    }
                    if (!jsonObject.isNull("nickSrcMid")) {
                        mUserInfo.nickSrcMid = jsonObject.getString("nickSrcMid");
                    }
                    if (!jsonObject.isNull("chineseName")) {
                        mUserInfo.chineseName = jsonObject.getString("chineseName");
                    }
                    if (!jsonObject.isNull("tel")) {
                        mUserInfo.tel = jsonObject.getString("tel");
                    }
                    if (!jsonObject.isNull("companyName")) {
                        mUserInfo.companyName = jsonObject.getString("companyName");
                    }
                    if (!jsonObject.isNull("message")) {
                        mUserInfo.message = jsonObject.getString("message");
                    }
                    if (!jsonObject.isNull("id")) {
                        mUserInfo.id = jsonObject.getInt("id");
                    }
                    if (!jsonObject.isNull("groupId")) {
                        mUserInfo.groupId = jsonObject.getInt("groupId");
                    }
                    if (!jsonObject.isNull("address")) {
                        mUserInfo.address = jsonObject.getString("address");
                    }
                    if (!jsonObject.isNull("email")) {
                        mUserInfo.email = jsonObject.getString("email");
                    }
                    if (!jsonObject.isNull("nickName")) {
                        mUserInfo.nickName = jsonObject.getString("nickName");
                    }
                    if (!jsonObject.isNull("nickSrc")) {
                        mUserInfo.nickSrc = jsonObject.getString("nickSrc");
                    }
                    if (!jsonObject.isNull("loginUser")) {
                        mUserInfo.loginUser = jsonObject.getString("loginUser");
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }
    }

    public class mobVerificationHttpRequest extends JsonHttpRequest{
        private String mobile = null;
        private String verificationCode = null;

        public mobVerificationHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        public String getMobile(){
            return mobile;
        }

        public String getVerificationCode(){
            return verificationCode;
        }
        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req) {
            if (!(data instanceof JSONObject)){
                throw new IllegalArgumentException("need JSONObject class");
            }
            try {
                JSONObject object = ((JSONObject)data).getJSONObject("data");
                if (object != null) {
                    if (object.has("phoneNum")) {
                        mobile = object
                                .getString("phoneNum");
                    }
                    if (object.has("mobCode")) {
                        verificationCode = object.getString("mobCode");
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }
    }

    /**
     * 注册用户的HttpRequest
     */
    public class registerHttpRequest extends JsonHttpRequest{
        private UserInfo mUserInfo = null;


        public registerHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        public UserInfo getUserInfo(){
            return mUserInfo;
        }

        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req){
            if (!(data instanceof JSONObject)){
                throw new IllegalArgumentException("need JSONObject class");
            }
            try {
                JSONObject obj = (JSONObject) data;
                if (obj != null && !obj.isNull("data")) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    mUserInfo = new UserInfo();
                    if (!jsonObject.isNull("msn")) {
                        mUserInfo.msn = jsonObject.getString("msn");
                    }
                    if (!jsonObject.isNull("visible")) {
                        mUserInfo.visible = jsonObject.getInt("visible");
                    }
                    if (!jsonObject.isNull("nickSrcMid")) {
                        mUserInfo.nickSrcMid = jsonObject.getString("nickSrcMid");
                    }
                    if (!jsonObject.isNull("chineseName")) {
                        mUserInfo.chineseName = jsonObject.getString("chineseName");
                    }
                    if (!jsonObject.isNull("tel")) {
                        mUserInfo.tel = jsonObject.getString("tel");
                    }
                    if (!jsonObject.isNull("companyName")) {
                        mUserInfo.companyName = jsonObject.getString("companyName");
                    }
                    if (!jsonObject.isNull("message")) {
                        mUserInfo.message = jsonObject.getString("message");
                    }
                    if (!jsonObject.isNull("id")) {
                        mUserInfo.id = jsonObject.getInt("id");
                    }
                    if (!jsonObject.isNull("groupId")) {
                        mUserInfo.groupId = jsonObject.getInt("groupId");
                    }
                    if (!jsonObject.isNull("address")) {
                        mUserInfo.address = jsonObject.getString("address");
                    }
                    if (!jsonObject.isNull("email")) {
                        mUserInfo.email = jsonObject.getString("email");
                    }
                    if (!jsonObject.isNull("nickName")) {
                        mUserInfo.nickName = jsonObject.getString("nickName");
                    }
                    if (!jsonObject.isNull("nickSrc")) {
                        mUserInfo.nickSrc = jsonObject.getString("nickSrc");
                    }
                    if (!jsonObject.isNull("loginUser")) {
                        mUserInfo.loginUser = jsonObject.getString("loginUser");
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }
    }

    public class modifyPasswordHttpRequest extends JsonHttpRequest{
        public modifyPasswordHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req) {
            if (!(data instanceof JSONObject)){
                throw new IllegalArgumentException("need JSONObject class");
            }
            try {
                JSONObject obj = ((JSONObject)data).getJSONObject("data");
                if (obj != null && obj.has("state")) {
                    if (!obj.getBoolean("state")) {
                        if (obj.has("error")) {
                            req.errorCode = obj.getInt("error");
                        }
                        if (obj.has("errorInfo")) {
                            req.errorMessage = obj.getString("errorInfo");
                        }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }
    }
    /**
     * 获取Android手机上面的UUID
     *
     * @param context
     * @return
     */
    public static String getUUID(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = ""
                + android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }


    /**
     * 获取蓝牙key及id
     * @return bleHttpRequest
     */
    public HttpRequest updateBleInfo(Context context){
        String url = null;
        List<NameValuePair> postList = null;
        if(PISManager.getInstance().hasCloudConnect()) {
            url = "http://115.29.11.191/webapi/bleInfoNew.php?op=list";
            String uuid = getUUID(context);
            postList = new ArrayList<NameValuePair>();

            postList.add(new NameValuePair("uuid", uuid));
        }
        return new bleHttpRequest(url, postList);
    }

    public HttpRequest updateUserInfo(){
        String url = "http://115.29.11.191/webapi/member_app.php?op=list";

        return new userInfoHttpRequest(url, null);

    }

    public HttpRequest updateVerificationCode(String mobile){
        String url = "http://115.29.11.191/webapi/member_app.php?op=getmobcode";
        List<NameValuePair> postList = new ArrayList<NameValuePair>();
        //如果value设置为null，则该字段将会被忽略
        postList.add(new NameValuePair("user", null));
        //如果设置了与默认字段同样的Pair，则该字段会被所设置的字段替代
        postList.add(new NameValuePair("pkey", "000000"));
        postList.add(new NameValuePair("tel", mobile));

        return new mobVerificationHttpRequest(url, postList);
    }

    public HttpRequest commitUserRegister(String mobile, String newPwd, String verCode){
        String url = "http://115.29.11.191/webapi/member_app.php?op=mobreg";
        List<NameValuePair> postList = new ArrayList<NameValuePair>();
        postList.add(new NameValuePair("user", mobile));
        postList.add(new NameValuePair("tel", mobile));
        postList.add(new NameValuePair("newpwd", newPwd));
        postList.add(new NameValuePair("pkey", verCode));

        return new registerHttpRequest(url, postList);
    }

    public HttpRequest commitNewPossword(String mobile, String newPwd, String verCode){
        String url = "http://115.29.11.191/webapi/member_app.php?op=mobeditpwd";
        List<NameValuePair> postList = new ArrayList<NameValuePair>();
        postList.add(new NameValuePair("tel", mobile));
        postList.add(new NameValuePair("newpwd", newPwd));
        postList.add(new NameValuePair("pkey", verCode));

        return new modifyPasswordHttpRequest(url, postList);
    }

    public class HeaderImageRequest extends StreamHttpRequest {
        public Bitmap image;
        public HeaderImageRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }


        @Override
        public int onProcess(REQUEST_STATUS status, Object data, HttpRequest req) {
            if (!(data instanceof JSONObject)) {
                throw new IllegalArgumentException("need JSONObject class");
            }

            return 0;
        }
    }

    public HttpRequest updateUserHeaderImage(String url){
        if (url == null)
            throw new NullPointerException("url can't be null");

        return new HeaderImageRequest(url, null);
    }
}
