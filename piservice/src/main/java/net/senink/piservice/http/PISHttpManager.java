package net.senink.piservice.http;

import android.app.Activity;
import net.senink.piservice.pis.PISManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wensttu on 2016/7/8.
 */
public class PISHttpManager {
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

    /**
     * 存放 RequestFuntureTask
     */
    private List<RequestFuntureTask> mRequestTaskList = null;

    /**
     * 线程管理器
     */
    private ExecutorService mRequestExecutor = null;

    private static PISHttpManager mInstance;
    private static Activity mActivity;

    /**
     * 获取单例对象
     * @return PISManager
     */
    public static PISHttpManager getInstance(Activity activity) {
        if (mActivity == null)
            mActivity = activity;
        if (null == mInstance) {
            mInstance = new PISHttpManager(5);
        }
        return mInstance;
    }

    public class RequestFuntureTask<T> extends FutureTask<T>{
        private HttpRequest mRequest = null;
        RequestFuntureTask(Callable<T> callable, HttpRequest request){
            super(callable);

            mRequest = request;
        }

        HttpRequest getRequest(){
            return mRequest;
        }
    }

    private Boolean isRunning = false;
    private Thread mManagerThread = null;
    private Thread CreateManagerThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //遍历集合,获取每个FutureTask执行的结果
                    if (!isRunning)
                        return;

                    try {
                        List<RequestFuntureTask> chkList = new ArrayList<RequestFuntureTask>(mRequestTaskList);
                        if (chkList.size() == 0)
                            Thread.sleep(100);
                        for (RequestFuntureTask ft : chkList) {
                            HttpRequest req = ft.getRequest();
                            try {
                                //如果任务被取消，则返回错误
                                if (ft.isCancelled()) {
                                    Object result = ft.get();
                                    if (req != null) {
                                        req.errorCode = REQUEST_EXECUTE_CANCLE;
                                        req.report_finished(mActivity);
                                        mRequestTaskList.remove(ft);
                                    }
                                } else if (ft.isDone()) {
                                    Object result = ft.get();
                                    if (req != null) {
                                        if (result == null)
                                            req.errorCode = REQUEST_EXECUTE_UNKNOW;
                                        req.report_finished(mActivity);
                                        mRequestTaskList.remove(ft);
                                    }
                                }
                            } catch (Exception e) {
                                req.errorCode = REQUEST_EXECUTE_EXCEPTION;
                                req.errorMessage = e.getMessage();
                                req.error = e;
                                mRequestTaskList.remove(ft);
                                e.printStackTrace();
                            }
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        }, "PISHttpManager");
    }

    private PISHttpManager(int maxTaskCount){
//        if (maxTaskCount < 2){
//            mRequestExecutor = Executors.newSingleThreadExecutor();
//        }else{
//            mRequestExecutor = Executors.newFixedThreadPool(maxTaskCount);
//        }
//        mRequestTaskList = new ArrayList<RequestFuntureTask>();

        //创建一个任务来监控线程池的执行情况

    }

    public <T> void request(final HttpRequest req) throws NullPointerException{
        if (req == null)
            throw new NullPointerException("PipaHttpRequest object can't be null");
        if (!isRunning)
            Start();

        RequestFuntureTask<T> newTask = new RequestFuntureTask<T>(new Callable<T>() {

            @Override
            public T call() throws Exception {
                T resultObject = null;

                try {
                    if (PISManager.getInstance().hasCloudConnect()) {
                        resultObject = (T) req.remote_execute();
                    } else {
                        resultObject = (T) req.local_execute();
                    }

                    req.onProcess(HttpRequest.REQUEST_STATUS.DONE, resultObject, req);
                }catch (Exception e){
                    req.error = e;
                }

                return resultObject;
            }
        }, req);
        try {
            mRequestExecutor.execute(newTask);
            mRequestTaskList.add(newTask);
            if (mActivity != null) {
                req.report_start(mActivity);
            }
        }catch (RejectedExecutionException e){
            e.printStackTrace();
        }
    }

    public void Start() throws NullPointerException{
        //初始化所有的资源
        try {
            if (isRunning)
                return;
            isRunning = true;

            if (mRequestExecutor == null) {
                mRequestTaskList = new ArrayList<RequestFuntureTask>();
                mRequestExecutor = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
            }

            if (mManagerThread == null){
                mManagerThread = CreateManagerThread();
                mManagerThread.start();
            }
        }catch (IllegalThreadStateException e){
            e.printStackTrace();
        }
    }

    public void Stop() throws NullPointerException{
        if (!isRunning)
            return;
        isRunning = false;

        if (mManagerThread != null){
            mManagerThread = null;
        }
        if (mRequestExecutor != null){
            mRequestTaskList.clear();
            mRequestExecutor.shutdown();

            mRequestTaskList = null;
            mRequestExecutor = null;
        }
    }





}
