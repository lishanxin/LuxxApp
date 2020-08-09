package net.senink.seninkapp;

import android.app.ActivityManager;
import android.content.Context;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by kee on 2018/5/15.
 */

public class MyActivityManager {
    private static final MyActivityManager ourInstance = new MyActivityManager();

    public static MyActivityManager getInstance() {
        return ourInstance;
    }

    private static final String TAG = "ActivityManager";

    private int frontCnt;


    private MyActivityManager() {
        frontCnt = 0;
    }

    public void onActivityStart() {
        frontCnt++;
    }

    public void onActivityStop() {
        frontCnt--;
    }

    public boolean isApplicationForeground() {
        return frontCnt > 0;
    }

    public void killApplication(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//        am.killBackgroundProcesses(BuildConfig.APPLICATION_ID);
        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}
