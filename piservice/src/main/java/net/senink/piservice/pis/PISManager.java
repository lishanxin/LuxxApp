package net.senink.piservice.pis;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import net.senink.piservice.R;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.jniutil.PisInterface;
import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.PinmOverBLE;
import net.senink.piservice.pinm.PINMoMC.PinmOverMC;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pinm.PinmService;
import net.senink.piservice.services.PISXinCenter;
import net.senink.piservice.services.PISXinColorLight;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.LuxxMusicColor;
import net.senink.piservice.services.PISXinRemoter;
import net.senink.piservice.services.PISXinSpeakerColorLight;
import net.senink.piservice.services.PISXinSwitch;
import net.senink.piservice.services.PISxinColor;
import net.senink.piservice.struct.PIAddress;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.piservice.struct.PipaRequestData;
import net.senink.piservice.struct.UserInfo;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.piservice.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wensttu on 2016/6/20.
 */
public class PISManager implements Serializable {
    private static final long serialVersionUID = 7057871429328943859L;
    private static final String TAG = "PISManager";
    private static boolean hasRegistPISClient = false;

    private static LocalBroadcastManager mLBManager = null;
    private static Context mContext;
    private static PISHttpManager httpManager = null;
    private static ScheduledExecutorService mPISExecutor = null;
    private static Runnable mTimerRunnable = null;
    private static Thread mManagerThread = null;
    /**
     * 存放 RequestFuntureTask
     */
    private static List<RequestFuntureTask> mRequestTaskList = null;

    /**
     * 线程管理器
     */
    private static ExecutorService mRequestExecutor = null;
    /**
     *  智能元查询条件
     */
    // 无条件
//    public final static int EnumSmartCellQueryBaseonNone     = 0;
//    // 条件为位置
//    public final static int EnumSmartCellQueryBaseonLocation = 1;
//    // 条件为名称
//    public final static int EnumSmartCellQueryBaseonName     = 2;
//    // 条件为关键字
//    public final static int EnumSmartCellQueryBaseonKeys     = 3;
//    // 条件为关键字
//    public final static int EnumSmartCellQueryBaseonDevice   = 4;

//    /**
//     *  设备类型定义（classid)
//     */
    public final static String CLASSID_BLEMESH_GERENAL= "000000000008";

    /**
     * 用来存放所有的PISBase及其派生类的实例对象
     */
    private transient HashMap<String, PISBase> piserviceMap = null;

    /**
     * 连接相关对象列表
     */
    private transient SparseArray<PinmInterface> mPinmList = new SparseArray<>();

    /**
     * 存放设备位置的集合
     */
    private transient SparseArray<String> mLocations = null;

    private transient PinmService mService = null;


    /**
     * 当PISManager所管理的服务对象发生改变的时候，将会发送该广播事件，该广播中附带两类数据
     * 1. 变化事件，表示是增加或者减少了服务对象列表 (1 - 增加, 0 - 减少)
     * 2. 对象列表，包含了所有被增加或减少的服务对象本身(List<PISBASE>)
     */
    public static final String PISERVICE_CHANGE_ACTION = "net.senink.pisbase.change";
    /**
     * 用来获取服务对象keyString列表的key
     */
    public static final String PIS_ExtraObject_List = "PIS_OBJECT_LIST";
    /**
     * 用来获取事件的key
     */
    public static final String CHANGE_ExtraEvent  = "PIS_EVENT";

    /**
     * 当某个服务对象的属性发生变化的时候，将会发送该广播事件，该广播中附带两类数据
     * 1. 属性列表，指出该服务对象的那些属性发生了变化
     * 2. 服务对象，指出属性发生了变化的服务对象 List<PISBase>
     */
    public static final String PISERVICE_INFO_CHANGE_ACTION = "net.senink.pisbase.info";
    /**
     * 获取服务对象keystring的key
     */
    public static final String PIS_ExtraObject = "PIS_OBJECT";
    /**
     * 获取属性列表的key
     */
//    public static final String INFOCHANGE_ExtraAttributes = "PIS_ATTRIBUTES";
//    public static final String SMART_CELL_OP_URL = "";// 智能元模版添加、修改、删除
//    public static final String SMART_CELL_QUERY_URL = "";// 智能元模版查询
    private boolean HasCloudConnect = false;
    private transient PISMCSManager mcsObject = null;
    private transient PISUpdate updateObject = null;


//    private transient ScheduledFuture<?> mTimerScheduled = null;

    private UserInfo mUserInfo;

    private static class PISManagerHolder{
        /**
         * 单例对象实例
         */
        static final PISManager mInstance = new PISManager();
    }
    /**
     * 获取单例对象
     * @return PISManager
     */
    public static PISManager getInstance() {
        return PISManagerHolder.mInstance;
    }

    public static PISManager getInstance(Activity act){
//        if (isRunning){
//            try {
//                mContext = act;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        mContext = act;
        return getInstance();
    }

    /**
     * 该函数应该在第一次使用PISManage的时候被调用，以便于构造PISManage对象
     * @param context   上下文环境
     * @param hasCloud  是否连接云端服务器
     * @return PISManager对象
     */

    public static PISManager getInstance(@NonNull Context context, @NonNull UserInfo user, boolean hasCloud) {
        LogUtils.i("TAG", "getInstance, NEW PISManager !");
        PISManager pm = PISManager.getInstance();
        PISManager.mContext = context;


        if (pm.mUserInfo != null && pm.mUserInfo.loginUser.compareTo(user.loginUser) != 0){
            //更新数据
            pm.clearPISObject();
            pm.mPinmList = new SparseArray<>();
            if (!hasRegistPISClient) {
                hasRegistPISClient = true;
                PisInterface.pisProcSet();
            }
        }
        pm.HasCloudConnect = hasCloud;
        pm.mUserInfo = user;
        pm.mcsObject = (PISMCSManager) PISManager.CreateServiceObject(
                new PIAddress(0x0, 0x1),
                new PIServiceInfo((byte) 0x0, (byte) 0x6, (byte) 0x1));
        if (pm.getPISObject(pm.mcsObject.getPISKeyString()) != null)
            pm.removePISObject(pm.mcsObject.getPISKeyString());
        pm.piserviceMap.put(pm.mcsObject.getPISKeyString(), pm.mcsObject);
        pm.mcsObject.setStatus(PISBase.SERVICE_STATUS_INITING);

        //检查是否完成初始化动作（绑定服务）
        try {
            if (pm.mService == null) {
                Intent bindIntent = new Intent(PISManager.getDefaultContext(), PinmService.class);
                PISManager.getDefaultContext().startService(bindIntent);
            }
        } catch (Exception exc) {
            Log.e(TAG, "bind pinm service threw exception!", exc);
        }

        if (pm.isAlive())
            pm.Stop();

        if (!hasRegistPISClient) {
            hasRegistPISClient = true;
            PisInterface.pisProcSet();
        }


        return pm;
    }

    public void release(){
        Stop();
        mContext = null;
        mUserInfo = null;
        piserviceMap.clear();
        mPinmList.clear();
    }

    public static LocalBroadcastManager getLBManager(){
        if (mLBManager == null) {
            mLBManager = LocalBroadcastManager.getInstance(mContext);
        }
        return mLBManager;
    }

    public static PISHttpManager getPISHttpManager(){
        if (httpManager == null){
            httpManager = PISHttpManager.getInstance((Activity)mContext);
        }
        return httpManager;
    }


    public static void StopTimerSchedule(){
        if (mPISExecutor != null){
            mPISExecutor.shutdown();
            mPISExecutor = null;
        }
    }

    public static void StartTimerSchedule(){
        if (mPISExecutor == null){
            mPISExecutor = Executors.newScheduledThreadPool(1);
        }
        mPISExecutor.scheduleWithFixedDelay(PISManager.getTimerRunnable(), 0, 500, TimeUnit.MILLISECONDS);
    }

    public static Runnable getTimerRunnable(){

        if (mTimerRunnable == null){
            mTimerRunnable = new Runnable(){
                @Override
                public void run() {
                    try {
                        onPismanagerTimer();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            };
        }
        return mTimerRunnable;
    }

    public static final Object requestTaskLock = new Object();
    public static List<RequestFuntureTask> getRequestTsakList(){
        if (mRequestTaskList == null){
            mRequestTaskList = new ArrayList<RequestFuntureTask>();
        }
        return mRequestTaskList;
    }

    /**
     * 线程管理器
     */
    public static ExecutorService getRequestExecutor(){
        if (mRequestExecutor == null){
            mRequestExecutor = Executors.newSingleThreadExecutor();
        }
        return mRequestExecutor;
    }

    private static boolean isRunning = false;

    private static Thread getManagerThread(){
        if (mManagerThread == null) {
            mManagerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //遍历集合,获取每个FutureTask执行的结果
                        try {
                            synchronized (requestTaskLock) {
                                List<RequestFuntureTask> chkList = PISManager.getRequestTsakList();
                                for (int i=0; i < chkList.size(); i++){
                                    RequestFuntureTask ft = chkList.get(i);
                                    PipaRequest req = ft.getRequest();
                                    try {
                                        //如果任务被取消，则返回错误
                                        if (ft.isCancelled()) {
                                            Object result = ft.get();
                                            req.errorCode = REQUEST_EXECUTE_CANCLE;
                                            if (PISManager.getRequestTsakList().contains(ft)) {
                                                PISManager.getRequestTsakList().remove(ft);
                                                i--;
                                            }
                                        } else if (ft.isDone()) {
                                            Object result = ft.get();
                                            if (result == null)
                                                req.errorCode = REQUEST_EXECUTE_UNKNOW;
                                            if (PISManager.getRequestTsakList().contains(ft)) {
                                                PISManager.getRequestTsakList().remove(ft);
                                                i--;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        req.errorCode = REQUEST_EXECUTE_EXCEPTION;
                                        req.errorMessage = e.getMessage();
                                        req.error = e;
                                        if (PISManager.getRequestTsakList().contains(ft)) {
                                            PISManager.getRequestTsakList().remove(ft);
                                            i--;
                                        }
                                    }
                                }
                            }
                        }catch(NullPointerException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mManagerThread = null;
                }
            }, "PISManagerDeamon");
        }
        return mManagerThread;
    }



    /**
     * Callback for changes to the state of the connection.
     */
    private transient ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder rawBinder) {
            try {
                mService = ((PinmService.LocalBinder) rawBinder).getService();
                if (mService != null) {
                    for (int i = 0; i < mPinmList.size(); i++) {
                        PinmInterface pif = mPinmList.valueAt(i);
                        if (mService.getPinmObject(pif.getConnectType()) == null) {
                            mService.registerPinmConnect(pif);
                        }
                        if (pif.getStatus() == PinmInterface.PINM_CONNECT_STATUS_CONNECTED) {
                            mService.updateParameter(pif.getConnectType(), pif.getParameter());
                        } else
                            mService.connect(pif.getConnectType(), pif.getParameter());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {

        }
    };

    /**
     * 构造函数
     */
    private PISManager(){
        piserviceMap = new HashMap<>();
        mPinmList = new SparseArray<>();
        if (!hasRegistPISClient) {
            hasRegistPISClient = true;
            PisInterface.pisProcSet();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        //关闭连接
//        Stop();
        //保存序列化对象
//        PISManager.SavePISManagerObject(mInstance, mInstance.mUserInfo.loginUser);
    }

    public static Context getDefaultContext(){
        return mContext;
    }

    /**
     * readResolve方法应对单例对象被序列化时候
     */
    private Object readResolve(){
        //将内容复制到当前单例对象
        List<PISBase> srvs = getPISObjectList();

        PISManager.getInstance().clearPISObject();
        PISManager.getInstance().copyPISObjectList(srvs);
        PISManager.getInstance().mUserInfo = mUserInfo;
        PISManager.getInstance().mPinmList = new SparseArray<>();
        PISManager.getInstance().HasCloudConnect = HasCloudConnect;
        for(PISBase srv : srvs){
            if (srv instanceof PISMCSManager)
                PISManager.getInstance().mcsObject = (PISMCSManager)srv;
            if (srv instanceof PISUpdate)
                PISManager.getInstance().updateObject = (PISUpdate)srv;
        }

        if (!hasRegistPISClient) {
            hasRegistPISClient = true;
            PisInterface.pisProcSet();
        }

        //检查是否完成初始化动作（绑定服务）
        try {
            Intent bindIntent = new Intent(PISManager.getDefaultContext(), PinmService.class);
            PISManager.getDefaultContext().startService(bindIntent);
        } catch (Exception exc) {
            Log.e(TAG, "bind pinm service threw exception!", exc);
        }

        return getInstance();
    }


    /**
     * 序列化对象输出函数
     * @param out 输出流
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException{
        out.defaultWriteObject();
        HashMap<String, PISBase> storeObjs = new HashMap<String, PISBase>();
        List<PISBase> allsrvs = PISManager.getInstance().AllObjects();
        PISMCSManager mcs = PISManager.getInstance().getMCSObject();
        for (PISBase pis : allsrvs){
            try {
                if (pis.ServiceType == PISBase.SERVICE_TYPE_SERVICE) {
                    if (pis.getDeviceObject() == null)
                        continue;
                    String macAddr = pis.getDeviceObject().getMacString();

                    if (macAddr == null)
                        continue;

                    if (!mcs.isBinded(macAddr) && pis.getStatus() != PISBase.SERVICE_STATUS_ONLINE)
                        continue;
                }
                storeObjs.put(pis.getPISKeyString(), pis);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        out.writeObject(storeObjs);
    }

    /**
     * 反序列化对象输入函数
     * @param in 输入流
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private synchronized void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        piserviceMap = (HashMap<String, PISBase>)in.readObject();
        for (PISBase pis : piserviceMap.values()){
            pis.setStatus(PISBase.SERVICE_STATUS_OFFLINE);
            if (pis instanceof PISMCSManager) {
                pis.setStatus(PISBase.SERVICE_STATUS_ONLINE);
                mcsObject = (PISMCSManager) pis;
            }
            if (pis instanceof PISUpdate) {
                pis.setStatus(PISBase.SERVICE_STATUS_ONLINE);
                updateObject = (PISUpdate) pis;
            }
        }
        isRunning = false;
    }

    public static void SavePISManagerObject(PISManager mgr, String storePath){
        if (storePath == null)
            throw new NullPointerException("目录不存在");

        File userFile = new File(storePath);
        try{
            if (userFile.exists())
                userFile.delete();
            if (!userFile.createNewFile())
                return;
        }catch (IOException e){
            LogUtils.w(TAG, e.getStackTrace().toString());
            return;
        }
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(userFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(mgr);
            LogUtils.i("SaveManager", "savePISManager(): try ios = ");
            oos.flush();
        } catch (Exception e) {
            LogUtils.w(TAG, e.getStackTrace().toString());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static PISManager loadPISManagerObject(@NonNull Context context, String storePath){
        PISManager obj = null;
        mContext = context;

        if (PISManager.getInstance().isAlive())
            PISManager.getInstance().Stop();

        if (storePath == null)
            throw new NullPointerException("目录不存在");

        File file = new File(storePath);
        if (!file.exists())
            return null;

        ObjectInputStream ois = null;
        try {
            FileInputStream fos = new FileInputStream(file);
            ois = new ObjectInputStream(fos);
            obj = (PISManager) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    public SparseArray<String> getLocations(){
        if (mLocations == null){
            mLocations = new SparseArray<>();
        }
        return mLocations;
    }

    public UserInfo getUserObject(){
        return mUserInfo;
    }

    public void setUserObject(UserInfo uInfo){
        if (uInfo == null)
            throw new NullPointerException("userinfo can't be null");
        if (mUserInfo == null){
            mUserInfo = uInfo;
            return;
        }
        if (uInfo.loginUser == null || uInfo.loginUser.compareTo(mUserInfo.loginUser) != 0){
            mUserInfo = uInfo;
            return;
        }
        mUserInfo.address = uInfo.address;
        mUserInfo.chineseName = uInfo.chineseName;
        mUserInfo.companyName = uInfo.companyName;
        mUserInfo.content = uInfo.content;
        mUserInfo.email = uInfo.email;
        mUserInfo.nickName = uInfo.nickName;
        mUserInfo.nickSrc = uInfo.nickSrc;
        mUserInfo.nickSrcMid = uInfo.nickSrcMid;
        mUserInfo.qq = uInfo.qq;
        mUserInfo.tel = uInfo.tel;
        mUserInfo.userIcon = uInfo.userIcon;
        mUserInfo.id = uInfo.id;
        mUserInfo.groupId = uInfo.groupId;
        mUserInfo.visible = uInfo.visible;
    }


    private static final int MAX_THREAD_COUNT = 5;
    /**
     * 任务被取消
     */
    public static final int REQUEST_EXECUTE_CANCLE = -1;
    /**
     * 返回未预期的结果
     */
    public static final int REQUEST_EXECUTE_UNKNOW = -2;

    /**
     * 发生异常，具体异常可以查看error属性
     */
    public static final int REQUEST_EXECUTE_EXCEPTION = -100;




    public class RequestFuntureTask<T> extends FutureTask<T> {
        private PipaRequest mRequest = null;
        RequestFuntureTask(Callable<T> callable, PipaRequest request){
            super(callable);
            mRequest = request;
        }

        PipaRequest getRequest(){
            return mRequest;
        }
    }



    public int Request(final PipaRequest req) throws IllegalArgumentException{
        /** 1. 检查req对象的完整性*/
        if (req == null)
            throw new IllegalArgumentException("Invild Parameter PipaHttpRequest");
//
//        if (!PISManager.getManagerThread().isAlive()) {
//
//            PISManager.getManagerThread().start();
//        }

        /**检查当前的任务列表中是否已经有相同的Request在运行*/
//        List<RequestFuntureTask> taskList = PISManager.getRequestTsakList();
//        for (RequestFuntureTask<Integer> task : mRequestTaskList){
//            PipaRequest request = task.getRequest();
//            if (request != null){
//                if (request.equals(req))
//                    return PipaRequest.REQUEST_EXISED;
//            }
//        }

        RequestFuntureTask<Integer> newTask = new RequestFuntureTask<Integer>(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                int resultObject = PipaRequest.REQUEST_RESULT_ERROR;
                if (mContext != null)
                    req.report_start((Activity)mContext);
                try {
                    if (PISManager.getInstance().hasCloudConnect()) {
                        resultObject = req.remote_execute();
                    } else {
                        resultObject = req.local_execute();

                        if (req.getReponseMessage() != 0) {
                            if (req.getReponseData() == null){
                                /**构建返回数据*/
                                byte[] ackBytes = PipaRequest.buildAckData(req.RequestData.Command,
                                        0,
                                        null);
                                req.setResponsePara(PISConstantDefine.PIPA_EVENT_CMD_ACK,
                                        req.getReponseCode(),
                                        ackBytes);
                            }
                            onProcess(req.getReponseMessage(),
                                    req.getReponseCode(),
                                    req.getReponseData(),
                                    req.PairID);
                        }
                    }
                }catch (Exception e){
                    req.errorCode = PipaRequest.REQUEST_RESULT_ERROR_OBJECT;
                    req.error = e;
                }

                return resultObject;
            }
        }, req);

        try {
            synchronized (requestTaskLock) {
                PISManager.getRequestExecutor().execute(newTask);
                PISManager.getRequestTsakList().add(newTask);
            }
        }catch (RejectedExecutionException e){
            e.printStackTrace();
        }

        return PipaRequest.REQUEST_SEND_SUCCESS;
    }

    /**
     * PISBase对象相关
     */
    public synchronized int getPISCount(){
        return piserviceMap.size();
    }

    public synchronized PISBase getPISObject(String key){
        return piserviceMap.get(key);
    }

    public synchronized PISBase getPISObject(int pairId) {
        PISBase obj = null;
        Iterator<Map.Entry<String, PISBase>> iter = piserviceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, PISBase> entry = iter.next();
            obj = entry.getValue();
            if (obj.PairID == pairId) // 服务列表里的服务对象的配对ID是否都是唯一的？
                break;
            else
                obj = null;
        }
        return obj;
    }

    public synchronized void putPISObject(PISBase object){
        if (object == null || object.getPISKeyString() == null)
            throw new NullPointerException("PISBase object is null");

        if (piserviceMap.containsKey(object.getPISKeyString()))
            return;

        piserviceMap.put(object.getPISKeyString(), object);
        if(object.ServiceType == PISBase.SERVICE_TYPE_GROUP){
            EventBus.getDefault().post(new PISBaseAdd(object));
        }
        if (object instanceof PISDevice) {
            List<PISBase> srvs = PIServicesWithQuery(object, EnumServicesQueryBaseonDevice);
            for (PISBase srv : srvs) {
                srv.mMacAddress = object.mMacAddress;
            }
        } else {
            List<PISDevice> devs = PIDevicesWithQuery(object, EnumDevicesQueryBaseonAddress);
            for (PISDevice dev : devs) {
                object.mMacAddress = dev.mMacAddress;
            }
        }

        //发送服务对象变化广播
        Intent intent = new Intent(PISManager.PISERVICE_CHANGE_ACTION);
        ArrayList<String> strKeys = new ArrayList<String>(1);
        strKeys.add(object.getPISKeyString());
        intent.putExtra(PISManager.PIS_ExtraObject_List, strKeys);
        intent.putExtra(PISManager.CHANGE_ExtraEvent, 1);
        mContext.sendBroadcast(intent);
    }

    public synchronized void removePISObject(PISBase obj){
        ArrayList<String> strKeys = new ArrayList<String>();
        if (obj == null || obj.getPISKeyString() == null)
            return;
        if (piserviceMap.containsKey(obj.getPISKeyString())) {
            piserviceMap.remove(obj.getPISKeyString());
            strKeys.add(obj.getPISKeyString());
        }
        if (obj instanceof PISDevice){
            //移除该device对象下的所有PIService
            for (PISBase srv : piserviceMap.values()){
                if (srv.getPanId() == obj.getPanId() &&
                        srv.getShortAddr() == obj.getShortAddr() &&
                        ((srv.getServiceId())&0xFFE0) == ((obj.getServiceId())&0xFFE0)){
                    strKeys.add(srv.getPISKeyString());
                }
            }
            for (String key : strKeys){
                piserviceMap.remove(key);
            }
        }
        //发送服务对象变化广播
        if (strKeys.size() > 0) {
            Intent intent = new Intent(PISManager.PISERVICE_CHANGE_ACTION);
            strKeys.add(obj.getPISKeyString());
            intent.putExtra(PISManager.PIS_ExtraObject_List, strKeys);
            intent.putExtra(PISManager.CHANGE_ExtraEvent, 0);
            mContext.sendBroadcast(intent);
        }
    }

    public void removePISObject(String key){
        if (key == null)
            return;
        PISBase obj = getPISObject(key);
        if (obj != null){
            removePISObject(obj);
        }
    }
    public synchronized void clearPISObject(){
        piserviceMap.clear();
    }

    public synchronized void copyPISObjectList(List<PISBase> list){
        if (list == null || list.size() == 0)
            return;
        String key;
        for(PISBase obj : list){
            key = obj.getPISKeyString();
            if (key == null)
                continue;
            if (!piserviceMap.containsKey(key))
                piserviceMap.put(key, obj);
        }
    }
    public synchronized List<PISBase> getPISObjectList(){
        return new ArrayList<>(piserviceMap.values());
    }
    /**
     * PINM连接相关信息
     */

    public int Connect(PinmInterface pinm, PinmInterface.PinmParameter para, Context context){
        PinmInterface pif = null;
        if (pinm == null)
            return PinmInterface.PINM_RESULT_NETWORK_INVAILD;

        if (context != null)
            mContext = context;

        pif = mPinmList.get(pinm.getConnectType());
        if (pif == null){
            mPinmList.put(pinm.getConnectType(), pinm);
        }
        if(mService != null){
            pif = mService.getPinmObject(pinm.getConnectType());
            if (pif != null){
                if (pif.getStatus() == PinmInterface.PINM_CONNECT_STATUS_CONNECTED){
                    mService.updateParameter(pinm.getConnectType(), para);
                }
                else
                    mService.connect(pinm.getConnectType(), para);
            }
            else{
                mService.registerPinmConnect(pinm);
                mService.connect(pinm.getConnectType(), para);
            }

        }
        else
            return PinmInterface.PINM_RESULT_NETWORK_INVAILD;

        return PinmInterface.PINM_RESULT_SUCCESSED;
    }

    public int Disconnect(PinmInterface pinm){
        if (pinm == null || mService == null)
            return PinmInterface.PINM_RESULT_FAILED;

        mService.disconnect(pinm.getConnectType());
        return PinmInterface.PINM_RESULT_SUCCESSED;
    }

    public synchronized int getPinmCount(){
        return mPinmList.size();
    }

    /**
     * 获取PINM连接对象
     * @param index PINM连接索引
     * @return
     */
    public synchronized PinmInterface getPinmObject(int index){
        if (index < 0 || index > mPinmList.size()-1)
            return null;
        return mPinmList.valueAt(index);
    }

    public synchronized PinmInterface getPinmObjectWithType(int type){
        PinmInterface pifResult = null;
        if (mService != null ){
            pifResult = mService.getPinmObject(type);
        }
//
//        if (mPinmList != null){
//            if (pifResult != null)
//                mPinmList.put(pifResult.getConnectType(), pifResult);
//            else
//                pifResult = mPinmList.get(type);
//        }

        return pifResult;
    }

    public synchronized SparseArray<PinmInterface> getPinmObjectList(){
        return mPinmList.clone();
    }

    public PISMCSManager getMCSObject(){
        return mcsObject;
    }

    public PISUpdate getUpdateObject(){
        return updateObject;
    }

    public boolean hasCloudConnect(){
        return HasCloudConnect;
    }

    public boolean isAlive(){
        return isRunning;
    }

    public synchronized void Start(){
        //检查PINM连接，必要的时候重建
        LogUtils.w(TAG, "the pismanager timer is start!!!!!!!!!!!!!!!!!!");
        if (isRunning || mContext == null)
            return;
        isRunning = true;

        //绑定连接服务
        try {
            Intent bindIntent = new Intent(PISManager.getDefaultContext(), PinmService.class);
            if (!mContext.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE))
                LogUtils.e(TAG, "bind pinm service failed!!!" + mContext);
            else
                LogUtils.e(TAG, "bind pinm service successed!!!" + mContext);
        } catch (Exception exc) {
            Log.e(TAG, "bind pinm service threw exception!", exc);
        }
        //启动http访问线程池
        PISHttpManager hm = getPISHttpManager();
        if (hm != null)
            hm.Start();
        //启动定时器
        try {
            PISManager.StartTimerSchedule();
        }catch (Exception e){
            e.printStackTrace();
        }
        //启动守护线程
        try {
            PISManager.getManagerThread().start();
        }catch (IllegalThreadStateException e){
            e.printStackTrace();
        }
    }

    public synchronized void Stop(){
        LogUtils.w(TAG, "the pismanager timer is shutdown!!!!!!!!!!!!!!!!!!");
        if (!isRunning)
            return;
        //停止守护线程
        isRunning = false;

        //关闭Timer
        PISManager.StopTimerSchedule();

        //关闭HTTP线程池
        PISHttpManager hm = getPISHttpManager();
        if (hm != null)
            hm.Stop();

        //解绑连接服务
        try {
            mContext.unbindService(mServiceConnection);
        } catch (Exception exc) {
            Log.e(TAG, "unbind pinm service threw exception!", exc);
        }

        //释放所有PipaRequest
        for (PISBase pis : piserviceMap.values()){
            pis.RequestClear();
        }
    }

    private transient long wakeupSpan = 5;
    private static void onPismanagerTimer(){
        PISManager pm = PISManager.getInstance();
        long mlastDiscoverTime = 0;
        Collection<PISBase> allSrvs = pm.getPISObjectList();
        SparseArray<PinmInterface> pifList = pm.getPinmObjectList();

        int cntNum = pm.getPinmCount();
        for (int i = 0; i < cntNum; i++){
            PinmInterface pinm = pm.getPinmObject(i);
            if (pinm == null)
                continue;
            if (pinm.hasNewStatus() && mContext != null){
                Intent intent = new Intent(PinmInterface.PINM_CONNECT_ACTION);
                intent.putExtra(PinmInterface.PINM_CONNECT_ExtraObject, pinm.getConnectType());
                intent.putExtra(PinmInterface.PINM_CONNECT_ExtraStatus, pinm.getStatus());
                mContext.sendBroadcast(intent);
            }
        }
        boolean isDiscovered = false;
        for (PISBase pis : allSrvs){
            for (int i = 0; i < pifList.size(); i++){
                PinmInterface pinm = pifList.valueAt(i);
                if (!pinm.isBelongTheConnect(pis))
                    continue;
                if (pinm.getStatus() != PinmInterface.PINM_CONNECT_STATUS_CONNECTED &&
                        pis.getStatus() != PISBase.SERVICE_STATUS_OFFLINE){
                    pis.setStatus(PISBase.SERVICE_STATUS_OFFLINE);
                    pis.RequestClear();
                    break;
                }
            }
            if ((pis instanceof PISDevice) && pis.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
                isDiscovered = true;

            /**组对象的PairID永远为0*/
            if (pis.PairID == 0){
                if (pis.ServiceType != PISBase.SERVICE_TYPE_GROUP)
                    pis.pairing();
                else
                    pis.PairID = 0xFFFF0000 | (0xFFFF&pis.getGroupId());
                continue;
            }

            if (pis instanceof PISDevice){
                PISDevice dev = (PISDevice)pis;
                long nowMS = System.currentTimeMillis();
                if (dev.getMacString() == null && dev.getStatus() == PISBase.SERVICE_STATUS_ONLINE){
                    dev.request(dev.updateDeviceInfo());
                }
                else if (dev.getMacByte() != null && dev.getStatus() != PISBase.SERVICE_STATUS_ONLINE){
                    byte[] classidBytes = dev.getClassByte();
                    if ((classidBytes[5] & 0x80) == 0x80 && (nowMS - pm.wakeupSpan) > 10000) {
                        pm.wakeupSpan = nowMS;
                        Date curDate = new Date();
                        Calendar nowDate = Calendar.getInstance();
                        nowDate.setTime(curDate);
                        int curSecond = nowDate.get(Calendar.HOUR_OF_DAY) * 3600 +
                                nowDate.get(Calendar.MINUTE) * 60 +
                                (1000 * 24 * 3600);
                        dev.request(dev.wakeupDevice(curSecond));
                    }
                }
            }

            switch (pis.getStatus())
            {
                case PISBase.SERVICE_STATUS_INITING:
                    pis.Initialization();
                    break;
                case PISBase.SERVICE_STATUS_INVAILD:
                case PISBase.SERVICE_STATUS_OFFLINE:
//                    pis.reportErrorForInvaildObject();
                    break;
                case PISBase.SERVICE_STATUS_READY:
                case PISBase.SERVICE_STATUS_ONLINE:
                    pis.checkPiserviceRequest();
                    break;
            }
        }

//        if (!isDiscovered &&
//                System.currentTimeMillis() - mlastDiscoverTime > 5000){
//            mlastDiscoverTime = System.currentTimeMillis();
//            //查找服务对象
//            PISManager.getInstance().DiscoverAll();
//        }

    }

    public void SyncDeviceTime(){
        List<PISDevice> devs = PIDevicesWithQuery(null, EnumDevicesQueryBaseonNone);
        Date curDate = new Date();
        Calendar nowDate = Calendar.getInstance();
        nowDate.setTime(curDate);
        int curSecond = nowDate.get(Calendar.HOUR_OF_DAY) * 3600 +
                nowDate.get(Calendar.MINUTE) * 60 +
                (1000 * 24 * 3600);
        for (PISDevice dev : devs){
            if (Math.abs(dev.getDeviceTime() - curSecond) < 300)
                continue;
            dev.request(dev.commitTime(curSecond));
            break;
        }
    }
    /**
     * 搜索所有的PIService
     */
    public void DiscoverAll(){
//        SyncDeviceTime();
        PISMCSManager mcs = PISManager.getInstance().getMCSObject();
        if (mcs != null)
            mcs.setStatus(PISBase.SERVICE_STATUS_ONLINE);
        PISUpdate up = PISManager.getInstance().getUpdateObject();
        if (up != null)
            up.setStatus(PISBase.SERVICE_STATUS_ONLINE);
        PisInterface.pisDiscover(0, null);
    }

    /**
     * 对全网进行请求的广播，目前暂时仅支持PINMoBLE网络
     * @param req PipaRequest Object
     */
    public int BroadcastRequest(@NonNull PipaRequest req){
        int broadcast_addr = 0xFFFF0000;

        if (req.RequestData == null)
            throw new NullPointerException("RequestData can't be null");

        return PisInterface.pisRequest(broadcast_addr,
                req.RequestData.Command,
                req.RequestData.Length,
                req.RequestData.Data, 1);
    }

    /**
     * 设备相关
     */
    // 无条件
    public final static int EnumDevicesQueryBaseonNone     = 0;
    // 有效的设备，该设备服务必须是‘EnumServiceStatusOnline’状态
    public final static int EnumDevicesQueryBaseonValid    = 1;
    // 条件是位置
    public final static int EnumDevicesQueryBaseonLocation = 2;
    // 条件是MAC地址
    public final static int EnumDevicesQueryBaseonMac      = 3;
    // 条件是名称
    public final static int EnumDevicesQueryBaseonName     = 4;
    // 条件是类型，也就是'classID'
    public final static int EnumDevicesQueryBaseonClass    = 5;
    // 条件是PISBase对象，通过比较地址获得对应的PISDevice对象
    public final static int EnumDevicesQueryBaseonAddress  = 6;


    public List<PISDevice> OnlineDevices(){
        return PIDevicesWithQuery(null, EnumDevicesQueryBaseonValid);
    }

    public List<PISDevice> AllDevices(){
        return PIDevicesWithQuery(null, EnumDevicesQueryBaseonNone);
    }

    public synchronized List<PISDevice> PIDevicesWithQuery(Object query, int option) throws NullPointerException{
        Collection<PISBase> mAllServices = piserviceMap.values();
        List<PISDevice> devs = new ArrayList<PISDevice>();
        for (PISBase pis : mAllServices){
            if (!(pis instanceof PISDevice))
                continue;
            PISDevice dev = (PISDevice)pis;
            switch(option){
                case EnumDevicesQueryBaseonNone:{
                    devs.add(dev);
                }
                break;
                case EnumDevicesQueryBaseonValid:
                    if (pis.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
                        devs.add(dev);
                    break;
                case EnumDevicesQueryBaseonLocation:
                    if (!(query instanceof Integer))
                        return null;
                    Integer loc = (Integer)query;
                    if (pis.getLocation() == loc)
                        devs.add(dev);
                    break;
                case EnumDevicesQueryBaseonMac:
                    if (query instanceof String){
                        if (dev.getMacString() == null)
                            break;
                        if (dev.getMacString().equalsIgnoreCase((String)query))
                            devs.add(dev);
                        break;
                    }
                    if (query instanceof byte[]){
                        if (dev.getMacByte().equals(query))
                            devs.add(dev);
                        break;
                    }
                    return null;
                case EnumDevicesQueryBaseonName:
                    if (!(query instanceof String))
                        break;
                    if (dev.getName().equals(query))
                        devs.add(dev);
                    break;
                case EnumDevicesQueryBaseonClass: {
                    if (query instanceof String) {
                        if (dev.getClassString().equalsIgnoreCase((String) query))
                            devs.add(dev);
                        break;
                    }
                    if (query instanceof byte[]) {
                        if (dev.getClassByte().equals(query))
                            devs.add(dev);
                        break;
                    }
                    break;
                }
                case EnumDevicesQueryBaseonAddress:{
                    if (query instanceof PISBase){
                        if (((PISBase) query).getPanId() == dev.getPanId() &&
                                ((PISBase) query).getShortAddr() == dev.getShortAddr() &&
                                (((PISBase) query).getServiceId() & 0xFFE0) == (dev.getServiceId()&0xFFE0)){
                            devs.add(dev);
                        }
                    }
                    break;
                }
            }
        }
        return devs;
    }

    /**
     * 功能服务相关
     */
    /**
     * 服务查询条件定义，为不同的服务提供不同的查询接口
     */
    // 无条件
    public final static int EnumServicesQueryBaseonNone     = 0;
    // 所有有效的服务
    public final static int EnumServicesQueryBaseonValid    = 1;
    //以位置为查询条件
    public final static int EnumServicesQueryBaseonLocation = 2;
    //以设备为查询条件
    public final static int EnumServicesQueryBaseonDevice   = 3;
    //以名称未查询条件
    public final static int EnumServicesQueryBaseonName     = 4;
    //以服务类型为查询条件
    public final static int EnumServicesQueryBaseonType     = 5;
    //模糊查询，依照输入的字符串，通过位置，名称及类型匹配对应的服务
    public final static int EnumServicesQueryBaseonString   = 6;
    //以Class为查询条件
    public final static int EnumServicesQueryBaseonClass    = 7;

    public List<PISBase> OnlineServices(){
        return PIServicesWithQuery(null, EnumServicesQueryBaseonValid);
    }

    public List<PISBase> AllServices(){
        return PIServicesWithQuery(null, EnumServicesQueryBaseonNone);
    }

    public synchronized List<PISBase> AllObjects(){
        return new ArrayList<PISBase>(piserviceMap.values());
    }

    public synchronized List<PISBase> PIServicesWithQuery(Object query, int option){
        Collection<PISBase> allSrvs;
        allSrvs = new ArrayList<PISBase>(piserviceMap.values());
        List<PISBase> resultArray = new ArrayList<PISBase>();

        for (PISBase pis : allSrvs) {
            if (pis.getT1() == 0x0 || pis.ServiceType == PISBase.SERVICE_TYPE_GROUP)
                continue;
            switch (option) {
                case EnumServicesQueryBaseonNone:
                    resultArray.add(pis);
                    break;
                case EnumServicesQueryBaseonValid: {
                    if (pis.getDeviceObject() == null || pis.getDeviceObject().getStatus() != PISBase.SERVICE_STATUS_ONLINE)
                        break;
                    resultArray.add(pis);
                    break;
                }
                case EnumServicesQueryBaseonLocation: {
                    if (!(query instanceof Integer))
                        return null;

                    if (pis.getLocation() == (Integer) query)
                        resultArray.add(pis);
                }
                break;
                case EnumServicesQueryBaseonDevice: {
                    if (!(query instanceof PISDevice))
                        return null;
                    PISDevice dev = pis.getDeviceObject();
                    if (dev == null)
                        break;
                    if (dev.equals(query))
                        resultArray.add(pis);
                }
                break;
                case EnumServicesQueryBaseonName: {
                    if (!(query instanceof String))
                        return null;
                    if (pis.getName().equals((String) query))
                        resultArray.add(pis);
                }
                break;
                case EnumServicesQueryBaseonType: {
                    if (!(query instanceof Integer))
                        return null;

                    Integer mPisType = pis.getIntegerType();
                    if (mPisType.equals(query))
                        resultArray.add(pis);
                }
                break;
                case EnumServicesQueryBaseonString: {

                }
                break;
                case EnumServicesQueryBaseonClass:{
                    Class queryCls = (Class)query;

                    if (queryCls == null)
                        resultArray.add(pis);
                    else if (queryCls.isAssignableFrom(pis.getClass()) || pis.getClass().isAssignableFrom(queryCls))
                        resultArray.add(pis);
                }
                default:
                    break;
            }
        }
        return resultArray;
    }

    /**
     * 组相关
     */

    //查询服务条件描述
    // 无条件
    public final static int EnumGroupsQueryBaseonNone    = 0;
    // 服务类型
    public final static int EnumGroupsQueryBaseonType     = 1;
    // 组ID
    public final static int EnumGroupsQueryBaseonGID     = 2;
    // Class
    public final static int EnumGroupsQueryBaseonClass   = 3;

    public List<PISBase> AllGroups(){
        return PIGroupsWithQuery(null, EnumGroupsQueryBaseonNone);
    }

    public List<PISBase> getGroups(int t1, int t2){
        Integer mPisType = (t1&0xFF) + ((t2&0xFF)<<8);
        return PIGroupsWithQuery(mPisType, EnumGroupsQueryBaseonType);
    }

    public synchronized List<PISBase> PIGroupsWithQuery(Object query, int option) {
        Collection<PISBase> allSrvs = piserviceMap.values();
        List<PISBase> resultArray = new ArrayList<PISBase>();
        for (PISBase pis : allSrvs){
            if (pis.ServiceType != PISBase.SERVICE_TYPE_GROUP)
                continue;

            switch (option){
                case EnumGroupsQueryBaseonNone:
                    resultArray.add(pis);
                    break;
                case EnumGroupsQueryBaseonType:
                    if (!(query instanceof Integer))
                        return null;
                    if (pis.getIntegerType() == (Integer)query)
                        resultArray.add(pis);
                    break;
                case EnumGroupsQueryBaseonGID:
                    if (!(query instanceof Integer))
                        return null;
                    if (pis.getShortAddr() == (Integer)query)
                        resultArray.add(pis);
                    break;
                case EnumGroupsQueryBaseonClass:{
                    if (!(query instanceof Class))
                        return null;
                    try {
                        if (pis.getGroupParent() == null){
                            resultArray.add(pis);
                            continue;
                        }
                        Class grpCls = pis.getGroupParent().getClass();
                        Class queryCls = (Class)query;

                        if (grpCls == null ||
                                queryCls.isAssignableFrom(grpCls) ||
                                grpCls.isAssignableFrom(queryCls))
                            resultArray.add(pis);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return resultArray;
    }

    /**
     * PIPA 相关
     */
    /**
     * 构建PISBase对象
     */
    private static final SparseArray<Class<?>> systemClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> lightClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> networkClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> multimediaClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> remoterClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> electricianClassArray = new SparseArray<Class<?>>();
    private static final SparseArray<Class<?>> wearableClassArray = new SparseArray<Class<?>>();

    private static final SparseArray<SparseArray<Class<?>>> pisClassArray = new SparseArray<SparseArray<Class<?>>>();

    static {
        /**系统*/
        systemClassArray.put(PISConstantDefine.PIS_SYSTEM_CLOUD, PISMCSManager.class);
        systemClassArray.put(PISConstantDefine.PIS_SYSTEM_DEVICE, PISDevice.class);
        systemClassArray.put(PISConstantDefine.PIS_SYSTEM_UPDATE, PISUpdate.class);
//        systemClassArray.put(PISConstantDefine.PIS_SYSTEM_SMARTCELL, PISSmartCellBase.class);

        /**照明*/
        lightClassArray.put(PISConstantDefine.PIS_LIGHT_COLOR, PISxinColor.class);
        lightClassArray.put(PISConstantDefine.PIS_LIGHT_LIGHT, PISXinLight.class);
        lightClassArray.put(PISConstantDefine.PIS_LIGHT_CANDLE, PISxinColor.class);     // NextApp.tw
//        lightClassArray.put(PISConstantDefine.PIS_LIGHT_COLORLIGHT, PISXinColorLight.class);
//        lightClassArray.put(PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER, PISXinSwitch.class);
//        lightClassArray.put(PISConstantDefine.PIS_LIGHT_MONO, PISXinMono.class);
//        lightClassArray.put(PISConstantDefine.PIS_LIGHT_COLOR_EXT, PISXinColorExt.class);

        /**网络*/
        networkClassArray.put(PISConstantDefine.PIS_NETWORK_CSRMESH, PISXinCenter.class);
        /**多媒体*/
        multimediaClassArray.put(PISConstantDefine.PIS_MULTIMEDIA_COLOR, LuxxMusicColor.class);
        multimediaClassArray.put(PISConstantDefine.PIS_MULTIMEDIA_COLORLIGHT, PISXinSpeakerColorLight.class);

        /**遥控器*/
        remoterClassArray.put(PISConstantDefine.PIS_REMOTER_KEY16, PISXinRemoter.class);
        /**电工*/
        electricianClassArray.put(PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER, PISXinSwitch.class);

//
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_SYSTEM, systemClassArray);
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_LIGHT, lightClassArray);
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_MULTIMEDIA, multimediaClassArray);
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_REMOTER, remoterClassArray);
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_NETWORK, networkClassArray);
        pisClassArray.put(PISConstantDefine.PIS_MAJOR_WEARABLE, wearableClassArray);
    }

    public static void RegisterPIServiceClass(int majorId, int subId, Class<?> cls){
        SparseArray<Class<?>> srvClsArray = pisClassArray.get(majorId);

        if(srvClsArray != null){
            srvClsArray.put(subId, cls);
        }
    }

    public static PISBase CreateServiceObject(PIAddress addr, PIServiceInfo srvInfo) {
        PISBase resultObject = null;
        if (pisClassArray != null && pisClassArray.get(srvInfo.T1) != null){
            try{
                Class pisClass = pisClassArray.get(srvInfo.T1).get(srvInfo.T2);
                Constructor pisCon = pisClass.getConstructor(PIAddress.class, PIServiceInfo.class);
                resultObject = (PISBase)pisCon.newInstance(addr, srvInfo);
            }catch(Exception e){
                e.printStackTrace();
                resultObject = null;
            }
        }

        if (resultObject == null){
            resultObject = new PISBase(addr, srvInfo, PISBase.SERVICE_TYPE_UNKNOW);
        }
        return resultObject;
    }


    public static final int PISERVICE_CATEGORY_UNKONW = 0x00;//未知
    public static final int PISERVICE_CATEGORY_LIGHT  = 0x05;//灯光类
    public static final int PISERVICE_CATEGORY_SOCKET = 0x02;//排插类
    public static final int PISERVICE_CATEGORY_BRIDGE = 0x01;//网桥类
    public static final int PISERVICE_CATEGORY_REMOTER= 0x06;//遥控类
    public static final int PISERVICE_CATEGORY_WEAR   = 0x07;//可穿戴

    public SparseIntArray getSupportCategories(){
        final SparseIntArray pisClasses = new SparseIntArray();

        pisClasses.put(0x10 | (0x02<<8), PISERVICE_CATEGORY_SOCKET);

        pisClasses.put(0x10 | (0x03<<8), PISERVICE_CATEGORY_LIGHT);
        pisClasses.put(0x10 | (0x04<<8), PISERVICE_CATEGORY_LIGHT);
        pisClasses.put(0x10 | (0x05<<8), PISERVICE_CATEGORY_LIGHT);     // NextApp.tw
        pisClasses.put(0x13 | (0x03<<8), PISERVICE_CATEGORY_LIGHT);

        pisClasses.put(PISConstantDefine.PIS_MAJOR_MULTIMEDIA |
                (PISConstantDefine.PIS_MULTIMEDIA_COLORLIGHT<<8),
                PISERVICE_CATEGORY_LIGHT);
        pisClasses.put(PISConstantDefine.PIS_MAJOR_LIGHT |
                (PISConstantDefine.PIS_LIGHT_COLORLIGHT << 8),
                PISERVICE_CATEGORY_LIGHT);

        pisClasses.put(0x70 | (0x01<<8), PISERVICE_CATEGORY_BRIDGE);

        pisClasses.put(0x40 | (0x01<<8), PISERVICE_CATEGORY_REMOTER);

        pisClasses.put(0x80 | (0x01<<8), PISERVICE_CATEGORY_WEAR);

        return pisClasses;
    }

    /**
     * 获取当前服务对象类型集
     * @return
     */
    public synchronized ArrayList<Integer> getOnlineCategories(){
        SparseIntArray allCategories = getSupportCategories();
        ArrayList<Integer> allCls = new ArrayList<>();

        Collection<PISBase> allSrvs = piserviceMap.values();
        for(PISBase pis : allSrvs){
            int cat = allCategories.get(pis.getIntegerType());
            if (cat != 0 && !allCls.contains(cat))
                allCls.add(cat);
        }

        return allCls;
    }

    public synchronized List<PISBase> getPISObjectWithCategory(int category){
        SparseIntArray allCategories = getSupportCategories();
        ArrayList<PISBase> result = new ArrayList<PISBase>();
        Collection<PISBase> allSrvs = piserviceMap.values();
        for(PISBase pis : allSrvs){
            int cat = allCategories.get(pis.getIntegerType());
            if (cat == 0)
                continue;
            if (category == cat)
                result.add(pis);
        }
        return result;
    }



    public void onProcess(int message, int hPara, byte[] lPara, int pairID)
            throws Exception {
        PISBase obj;
        String keyString;
        switch (message) {
            case PISConstantDefine.PIPA_EVENT_CMD_ACK:{
                if(lPara == null || lPara.length < 3)
                    break;
                try{
                    obj = getPISObject(pairID);
                    if (obj == null)
                        break;
                    obj.setStatus(PISBase.SERVICE_STATUS_ONLINE);

                    byte mCmd = lPara[0];
                    PipaRequest mReq = obj.getExecutingRequest(mCmd);
                    PipaRequestData rawData = null;
                    if (mReq != null)
                        rawData = mReq.RequestData;
                    PISBase.PISCommandAckData ackData = obj.buildAckData(lPara, rawData);
                    if (ackData != null){
                        if (mReq != null)
                            mReq.ackData = ackData;

                        obj.onPerProcess(message, hPara, ackData);
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                break;
            }
            case PISConstantDefine.PIPA_EVENT_MESSAGE:
                //找到对应的PISBase对象
                obj = getPISObject(pairID);
                if (obj == null)
                    break;
                obj.setStatus(PISBase.SERVICE_STATUS_ONLINE);
                try{
                    byte[] lenBytes = new byte[2];
                    System.arraycopy(lPara, 1, lenBytes, 0, 2);
                    int len = ByteUtilLittleEndian.getInt(lenBytes);

                    byte[] ackBytes = new byte[len + 3];
                    ackBytes[0] = (byte)hPara;
                    System.arraycopy(lPara, 1, ackBytes, 1, len+2);

                    PISBase.PISCommandAckData ackData = obj.buildAckData(ackBytes, null);
                    obj.onPerProcess(message, hPara, ackData);
                }catch(Exception ex){
                    break;
                }

                break;
            case PISConstantDefine.PIPA_EVENT_SS_ACK:
                if (null == lPara)
                    break;
                obj = getPISObject(pairID);
                obj.setStatus(PISBase.SERVICE_STATUS_ONLINE);
                PipaRequest req = obj.getExecutingRequest(lPara[0]);
                if (null == req)
                    break;
                obj.onPerProcess(message, hPara, req);
                break;
            case PISConstantDefine.PIPA_EVENT_ALIVE:{
                if (lPara == null)
                    break;
                final int byte_offset = 0;

                byte[] srvbytes = new byte[21];
                System.arraycopy(lPara, byte_offset, srvbytes, 0, 21);
                PIServiceInfo mSrvInfo = new PIServiceInfo(srvbytes);

                byte[] addrBytes = new byte[4];
                System.arraycopy(lPara, byte_offset + 21, addrBytes, 0, 4);
                PIAddress mSrvAddr = new PIAddress(addrBytes);


                keyString = PISBase.getKeyString(mSrvAddr.getPanId(),
                        mSrvAddr.getShortAddr(), mSrvInfo.getServiceID());
                obj = getPISObject(keyString);

                String strLog = String.format("PIPA_EVENT_ALIVE pid[0x%04x] saddr[0x%04x] srvid[0x%04x] t1[%02x] t2[%02x",
                        mSrvAddr.getPanId(),
                        mSrvAddr.getShortAddr(),
                        mSrvInfo.getServiceID(),
                        mSrvInfo.T1,
                        mSrvInfo.T2);
                LogUtils.i(TAG, strLog);
                if (obj == null || obj.getT1() != mSrvInfo.T1 || obj.getT2() != mSrvInfo.T2) {
                    obj = CreateServiceObject(mSrvAddr, mSrvInfo);// new PISBase(head);
                    if (obj != null) {
                        if (obj instanceof PISMCSManager)
                            this.mcsObject = (PISMCSManager) obj;
                        else if (obj instanceof PISUpdate)
                            this.updateObject = (PISUpdate) obj;

                        putPISObject(obj);
                        obj.setStatus(PISBase.SERVICE_STATUS_INITING);

                        LogUtils.i("PISManager", "PIPA_EVENT_ALIVE, ADD new PISBase!!!" + keyString);
                    }
                }else if (obj.getStatus() != PISBase.SERVICE_STATUS_ONLINE)
                    obj.setStatus(PISBase.SERVICE_STATUS_ONLINE);
                else
                    break;
            }
                break;
            case PISConstantDefine.PIPA_EVENT_PAIR_ACK: {
                if (null != lPara) {
                    final int byte_offset = 0;

                    byte[] srvbytes = new byte[21];
                    System.arraycopy(lPara, byte_offset, srvbytes, 0, 21);
                    PIServiceInfo mSrvInfo = new PIServiceInfo(srvbytes);

                    byte[] addrBytes = new byte[4];
                    System.arraycopy(lPara, byte_offset + 21, addrBytes, 0, 4);
                    PIAddress mSrvAddr = new PIAddress(addrBytes);

                    keyString = PISBase.getKeyString(mSrvAddr.getPanId(),
                            mSrvAddr.getShortAddr(), mSrvInfo.getServiceID());
                    obj = getPISObject(keyString);
                    String strLog = String.format("Pair[%x] ACK pid[0x%04x] saddr[0x%04x] srvid[0x%04x] t1[%02x] t2[%02x",
                            pairID,
                            mSrvAddr.getPanId(),
                            mSrvAddr.getShortAddr(),
                            mSrvInfo.getServiceID(),
                            mSrvInfo.T1,
                            mSrvInfo.T2);
                    LogUtils.i(TAG, strLog);
                    if (obj != null) {
                        obj.PairID = pairID;
                    }
                }
            }
                break;
            default:
                break;
        }
    }
}
