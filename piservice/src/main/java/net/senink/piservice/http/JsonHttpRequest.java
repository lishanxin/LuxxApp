package net.senink.piservice.http;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by wensttu on 2016/7/8.
 */
public abstract class JsonHttpRequest extends HttpRequest {
    /**
     * 远程执行方法
     */
    @Override
    public JSONObject remote_execute() throws NullPointerException{
        if(mUrl == null)
            throw new NullPointerException("url is null");

        JSONObject resultObject = null;
        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection conn;

        try {
            UserInfo uInfo = PISManager.getInstance().getUserObject();

            String urlStr = mUrl;
            /**如果存在pkey或者user字段的 NameValuePair，则使用该pair替代*/
            NameValuePair userPair = containsKey(mPostList, "user");
            if (userPair == null) {
                if (uInfo != null && uInfo.loginUser != null)
                    urlStr += "&user=" + uInfo.loginUser;
//                throw new NullPointerException("invaild user info");
            } else if (userPair.getValue() != null && userPair.getValue().length() > 0) {
                urlStr += "&user=" + userPair.getValue();
            }
            NameValuePair pkeyPair = containsKey(mPostList, "pkey");
            if (pkeyPair == null)
                urlStr += "&pkey=" + getPKey(PISManager.getInstance().getUserObject().getMD5Passwd());
            else if (pkeyPair.getValue() != null && pkeyPair.getValue().length() > 0)
                urlStr += "&pkey=" + getPKey(MD5(MD5(pkeyPair.getValue())));

            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(ConnectTimeout);
            conn.setReadTimeout(ConnectTimeout);

            if (mPostList != null) {
                conn.setRequestMethod("POST");
                os = conn.getOutputStream();
                String postStr = "";
                for (NameValuePair pair : mPostList){
                    //如果是属于默认字段，则不会被POST出去
                    if (pair.getName().compareTo("user") != 0 && pair.getName().compareTo("pkey") != 0)
                        postStr += pair.toString();
                }
                os.write(postStr.getBytes("UTF-8"));// 将post数据发出去
            }
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(inputStreamReader);// 读字符串用的。
                String inputLine;
                String result = "";
                // 使用循环来读取获得的数据，把数据都村到result中了
                while (((inputLine = reader.readLine()) != null)) {
                    result += inputLine;
                }
                reader.close();// 关闭输入流
                conn.disconnect();

                resultObject = new JSONObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultObject;
    }

    /**
     * 本地执行方法，需要子类来实现
     */
    @Override
    public JSONObject local_execute(){
        return this.remote_execute();
    }

    /**
     * 构造函数
     * @param url       包含基本的URL以及所需要的GET字段，不包含user及pkey字段
     * @param postList  需要执行POST方法的参数对列表
     */
    public JsonHttpRequest(String url, List<NameValuePair> postList){
        super(url, postList, RESULT_TYPE.JSON);
    }


    private NameValuePair containsKey(List<NameValuePair> postList, String keyString){
        if (postList == null)
            return null;

        for (NameValuePair pair : postList){
            if (pair.getName().compareTo(keyString) == 0)
                return pair;
        }
        return null;
    }

}
