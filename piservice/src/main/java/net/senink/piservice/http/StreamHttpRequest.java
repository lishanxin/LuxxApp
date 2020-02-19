package net.senink.piservice.http;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.struct.UserInfo;

import org.json.JSONArray;
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

/**
 * Created by wensttu on 2016/7/11.
 */
public abstract class StreamHttpRequest extends HttpRequest {

    /**
     * 构造函数
     * @param url       包含基本的URL以及所需要的GET字段，不包含user及pkey字段
     * @param postList  需要执行POST方法的参数对列表
     */
    public StreamHttpRequest(String url, List<NameValuePair> postList){
        super(url, postList, RESULT_TYPE.JSON);
    }

    /**
     * 远程执行方法
     */
    @Override
    public InputStream remote_execute() throws NullPointerException{
        if(mUrl == null)
            throw new NullPointerException("url is null");

        InputStream resultObject = null;

        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                return conn.getInputStream();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultObject;
    }

    /**
     * 本地执行方法，需要子类来实现
     */
    @Override
    public InputStream local_execute(){
        return this.remote_execute();
    }

}
