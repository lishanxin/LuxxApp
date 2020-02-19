/**
 * Created by wensttu on 2016/6/1.
 */
package net.senink.seninkapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.smart.SmartMainActivity;

import java.util.ArrayList;


public class Foreground implements Application.ActivityLifecycleCallbacks {

    private static Foreground instance;
    boolean wasBackground = false;
    boolean wasForeground = false;
    private Handler handler = new Handler();
    private Runnable check;
    private static String TAG = "Foreground";
    private static long CHECK_DELAY = 500;

    public static void init(Application app){
        if (instance == null){
            instance = new Foreground();
            app.registerActivityLifecycleCallbacks(instance);
        }
    }

    public static Foreground get(){
        return instance;
    }
    public interface Listener {
        void onBecameForeground();
        void onBecameBackground();
    }
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private ArrayList<Activity> activities = new ArrayList<Activity>();
    private ArrayList<Activity> aliveActivity = new ArrayList<>();

    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }
    private Foreground(){}

    public boolean isForeground(){
        return (activities.size()!=0);
    }

    public boolean isBackground(){
        return (activities.size()==0);
    }

    public static Activity getMainActivity(){
        Activity act = null;
        for (Activity activity : instance.activities) {
            if (activity instanceof HomeActivity) {
                act = activity;
                break;
            }
        }
        return act;
    }

    public static Activity getHomeActivity(){
        for (Activity act : instance.aliveActivity){
            if (act.getClass().equals(HomeActivity.class))
                return act;
        }
        return null;
    }

    public static void exitToHome(){
        for (Activity act : instance.aliveActivity) {
            if (!act.getClass().equals(HomeActivity.class))
                act.finish();
        }
    }
    public static void exitToActivity(Context from, Class activity) {
        for (Activity act : instance.aliveActivity) {
//            if (!act.getClass().equals(activity))
                act.finish();
        }
//        instance.activities.clear();
//        PISManager.getInstance().Stop();
        from.startActivity( new Intent(from, activity));
    }

    // TODO: implement the lifecycle callback methods!
    @Override
    public void onActivityResumed(Activity activity) {
        Log.i(TAG, "onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i(TAG, "onActivityPaused");
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState){
        Log.i(TAG, "onActivityCreated");
        aliveActivity.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity){
        Log.i(TAG, "onActivityStarted[" + activities.size() + "]");
        if (check != null)
            handler.removeCallbacks(check);

        if (activities.size()==0){
            wasForeground = true;
        }
        if (wasForeground){
            wasForeground = false;
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        }

        activities.add(activity);
    }

    @Override
    public void onActivityStopped(Activity activity){
        activities.remove(activity);

        if (activities.size() == 0){
            wasBackground = true;
        }
        Log.i(TAG, "onActivityStopped[" + activities.size() + "]");
        if (check != null)
            handler.removeCallbacks(check);
        if (wasBackground){
            wasBackground = false;
            Log.i(TAG, "went background");
            for (Listener l : listeners) {
                try {
                    l.onBecameBackground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        }
//
//        handler.postDelayed(check = new Runnable(){
//            @Override
//            public void run() {
//                if (wasBackground){
//                    wasBackground = false;
//                    Log.i(TAG, "went background");
//                    for (Listener l : listeners) {
//                        try {
//                            l.onBecameBackground();
//                        } catch (Exception exc) {
//                            Log.e(TAG, "Listener threw exception!", exc);
//                        }
//                    }
//                }
//            }
//        }, CHECK_DELAY);
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState){
        Log.i(TAG, "onActivitySaveInstanceState");
    }
    @Override
    public void onActivityDestroyed(Activity activity){
        aliveActivity.remove(activity);
        Log.i(TAG, "onActivityDestroyed");
    }
}

