package net.senink.seninkapp.telink.api;

import android.app.Activity;
import android.content.Intent;

import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.light.MeshController;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.util.TelinkLog;

import net.senink.seninkapp.MyApplication;

/**
 * @author: Li Shanxin
 * @date: 2020/2/20
 * @description:
 */
public class TelinkApiManager implements EventListener<String> {
    private static final String TAG = TelinkApiManager.class.getSimpleName();
    private static TelinkApiManager instance;
    private boolean isServiceCreated = false;

    public static TelinkApiManager getInstance(){
        if(instance == null){
            synchronized (TelinkApiManager.class){
                if(instance == null){
                    instance = new TelinkApiManager();
                }
            }
        }
        return instance;
    }

    public void startMeshService(Activity activity){
        Intent serviceIntent = new Intent(activity, MeshService.class);
        activity.startService(serviceIntent);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_EMPTY, this);
        MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_CREATE, this);
        MyApplication.getInstance().addEventListener(MeshController.EVENT_TYPE_SERVICE_DESTROY, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_PROCESSING, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_ERROR_BUSY, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);
    }


    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case MeshController.EVENT_TYPE_SERVICE_CREATE:
                TelinkLog.d(TAG + "#EVENT_TYPE_SERVICE_CREATE");
                isServiceCreated = true;
                break;
            case MeshController.EVENT_TYPE_SERVICE_DESTROY:
                TelinkLog.d(TAG + "-- service destroyed event");
                break;
        }
    }

}
