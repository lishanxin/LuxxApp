package net.senink.piservice.http;

import android.content.Context;
import android.content.SharedPreferences;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Created by wensttu on 2016/7/9.
 */
public class HttpDeviceInfo {
    public static final String KEYSTRING_CLASSID = "classid";
    public static final String KEYSTRING_MAC = "mac";
    public static final String KEYSTRING_BLEID = "bleId";

    private HashMap<String, String> devInfos = new HashMap<String,String>();
    /**
     * 根据MAC地址，获取设备详细信息
     */
    public class classidHttpRequest extends JsonHttpRequest{
        public String macAddr;
        public String classId;
        public classidHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        /**
         * 本地执行方法
         */
        @Override
        public JSONObject local_execute(){
            JSONObject result = null;

            if (devInfos == null)
                devInfos = new HashMap<String, String>();
            try {
                if (devInfos.containsKey(this.macAddr.toUpperCase()))
                    result = new JSONObject(devInfos.get(this.macAddr.toUpperCase()));
            }catch (JSONException e){
                e.printStackTrace();
                result = null;
            }

            if (result != null)
                return result;

            try {
                String jsonFormat = "{\"state\":true,\"data\":[{\"mac\":\"%s\"," +
                        "\"classid\":\"%s\",\"user\":null,\"userId\":null,\"location\":null," +
                        "\"version\":null,\"name\":null,\"passwd\":null,\"passwdDefault\":null," +
                        "\"termID\":null,\"title\":\"bleDevice\",\"model\":null," +
                        "\"distributor\":\"senink\",\"sourced\":null," +
                        "\"content\":\"s:0:\\\"\\\";\"," +
                        "\"img1\":\"http:\\/\\/115.29.11.191\\/uploads\\/product\\/20160129170703_9945.png\"," +
                        "\"icon\":\"http:\\/\\/115.29.11.191\\/uploads\\/\",\"mader\":\"11\"}]," +
                        "\"dataCount\":1,\"error\":0,\"errorInfo\":null}";
                String jsonStr = String.format(Locale.getDefault(), jsonFormat, this.macAddr, PISManager.CLASSID_BLEMESH_GERENAL);
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
        public JSONObject remote_execute() {
            JSONObject result = null;
            if (devInfos == null)
                devInfos = new HashMap<String, String>();
            try {
                if (devInfos.containsKey(this.macAddr.toUpperCase()))
                    result = new JSONObject(devInfos.get(this.macAddr.toUpperCase()));
            }catch (JSONException e){
                e.printStackTrace();
                result = null;
            }
            if (result != null)
                return result;

            return super.remote_execute();
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
                        if (obj.has("data") && obj.getJSONArray("data").length() > 0) {
                            JSONArray objects = obj.getJSONArray("data");
                            for (int i=0; i<objects.length(); i++){
                                JSONObject object = objects.getJSONObject(i);
                                if (object.has(KEYSTRING_CLASSID) && object.has(KEYSTRING_MAC)) {
                                    String mMac = object.getString(KEYSTRING_MAC).toUpperCase();
                                    String mCls = object.getString(KEYSTRING_CLASSID);
                                    if (this.macAddr.toUpperCase().compareTo(mMac) == 0){
                                        classId = mCls;
                                    }
                                    //将相关信息保存下来，以便于后续直接使用
                                    saveMacClassIdPair(mMac, obj.toString());
                                }
                            }
                        }
                    }
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            return 0;
        }

        private void saveMacClassIdPair(String mac, String classId){
            if (devInfos != null)
                devInfos.put(mac, classId);
            LogUtils.i("HttpDeviceInfo", mac + "\n" + classId);
        }
    }

    public classidHttpRequest updateClassId(String mac){
        String url = "http://115.29.11.191/webapi/termInfoList.php?op=list";
        List<NameValuePair> postList = new ArrayList<NameValuePair>();
        postList.add(new NameValuePair(KEYSTRING_MAC, mac));

        classidHttpRequest req = new classidHttpRequest(url, postList);
        req.macAddr = mac;

        return req;
    }


    /**
     * 根据MAC地址，获取设备详细信息
     */
    public class bleDeviceIdHttpRequest extends JsonHttpRequest{
        public String macAddr;
        public int bleId;
        public bleDeviceIdHttpRequest(String url, List<NameValuePair> postList){
            super(url, postList);
        }

        /**
         * 本地执行方法
         */
        @Override
        public JSONObject local_execute(){
            Integer bleID = 0;
            JSONObject result = null;

            try {
                // 存储替换：未来需要更好的存储方式
                SharedPreferences pref = PISManager.getDefaultContext().getSharedPreferences("DeviceInfoList", Context.MODE_PRIVATE);
                Set<String> bleids = pref.getStringSet("bleIdList", null);
                bleID = pref.getInt(this.macAddr, 0);

                if (bleids == null)
                    bleids = new HashSet<String>();

                //从本地存储中获取已分配的ID号，如果重复则重新分配
                if (bleID == 0){
                    Random random=new Random();
                    do{
                        // 如果bleID不存在，则从0x7101~0x7FFF之间随机生成一个作为bleID，并保存在本地
                        bleID = 0x8200 + random.nextInt(0x5FF);
                    }while(bleids.contains(bleID.toString()));
                    bleids.add(bleID.toString());

                    //将数据回存
                    SharedPreferences.Editor prefEditor = pref.edit();
                    prefEditor.putStringSet("bleIdList", bleids);
                    prefEditor.putInt(this.macAddr, bleID);
                    prefEditor.apply();
                }


                this.bleId = bleID;

                String jsonFormat = "{\"data\":{\"bleId\":%d,\"Passwd\":\"\"," +
                        "\"classId\":\"\",\"bleMac\":\"%s\"},\"state\":true," +
                        "\"dataCount\":4,\"errorInfo\":null,\"error\":0}";
                String jsonStr = String.format(Locale.getDefault(), jsonFormat,
                        bleID,
                        this.macAddr);
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
                            if (object.has(KEYSTRING_BLEID) && object.has("bleMac")) {
                                this.macAddr = object.getString("bleMac");
                                this.bleId = object.getInt(KEYSTRING_BLEID);
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

    public bleDeviceIdHttpRequest updateBleDeviceId(String mac){
        String url = "http://115.29.11.191/webapi/bleInfoNew.php?op=single";
        List<NameValuePair> postList = new ArrayList<NameValuePair>();
        postList.add(new NameValuePair("macAddress", mac));

        bleDeviceIdHttpRequest req = new bleDeviceIdHttpRequest(url, postList);
        req.macAddr = mac;

        return req;
    }


}
