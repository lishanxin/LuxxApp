package net.senink.seninkapp.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.OnlineStatusEvent;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;

import net.senink.seninkapp.adapter.MixLightListAdapter;
import net.senink.seninkapp.telink.SharedPreferenceHelper;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.home.TelinkDataRefreshEntry;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 蓝牙灯列表的界面
 *
 * @author zhaojunfeng
 * @date 2015-07-09
 */
public class LightControlFragment extends Fragment implements EventListener<String> {
    public static final String TAG = "LightControlFragment";

    private View mRootView;
    // 显示灯列表的组件
    private ListView deviceListView;
    // 显示插排列表的组件
    private PullToRefreshListView mRefreshListView;
    // 列表组件的适配器
    private MixLightListAdapter deviceAdapter;
    private PISManager manager;
    private Handler mHandler = null;
    private Handler mCycleHandler = new Handler();
    private static final int REFRESH_PIS_DEVICES = 0X1234;

    private int isRefersh = 0;
    // 灯列表
    private List<GeneralDeviceModel[]> mLights;

    public LightControlFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_lightcontrol,
                    container, false);
            mRefreshListView = (PullToRefreshListView) mRootView
                    .findViewById(R.id.light_control_devicelist);
            mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            deviceListView = mRefreshListView.getRefreshableView();
        }
        if (null == deviceAdapter) {
            deviceAdapter = new MixLightListAdapter((HomeActivity) getActivity(), null);
            deviceListView.setAdapter(deviceAdapter);
        }

        setData();
        setListener();
        checkTelinkGroups();
        return mRootView;
    }

    @Override
    public void onDetach() {
        if (mHandler != null) {
            mHandler.removeMessages(2000);
        }
        super.onDetach();
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.removeMessages(2000);
        }
        super.onPause();
    }

    private void setData() {
//		if (manager != null) {
//			mcm = manager.getMCSObject();
//		}
    }

    public Handler getHandler() {
        return mHandler;
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1000:
                        mRefreshListView.onRefreshComplete();
                        setLightAdapter(null);
                        refreshListView();
                        LogUtils.i("CCCCC", "handlemessage");
                        break;
                    case 2000:
                        manager.DiscoverAll();
                        if (isRefersh++ < 2 || mLights.size() == 0)
                            mHandler.sendEmptyMessageDelayed(2000, 2000);
                        else
                            isRefersh = 0;
                        break;
                    case REFRESH_PIS_DEVICES:
                        mRefreshListView.setRefreshing();
                        break;
                }
            }
        };
    }


    /**
     * 设置监听器
     */
    private void setListener() {
        mRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                        manager = PISManager.getInstance();
                        if (null == manager)
                            return;
                        try {
                            manager.DiscoverAll();
                            final PISMCSManager mcm = manager.getMCSObject();
                            if (mcm == null)
                                return;
                            mcm.request(mcm.getGroupInfos());
                            PipaRequest reqSrv = mcm.updateBindedDevices();
                            reqSrv.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                                @Override
                                public void onRequestStart(PipaRequest req) {

                                }

                                @Override
                                public void onRequestResult(PipaRequest req) {
                                    setLightAdapter(null);
                                    refreshListView();
                                }
                            });
                            mcm.request(reqSrv);

                            if (mLights == null)
                                return;
                            for (GeneralDeviceModel[] srvs : mLights) {
                                for (GeneralDeviceModel generalSrv : srvs) {
                                    PISBase srv = generalSrv.getPisBase();
                                    if (generalSrv.isTelink()) {

                                    } else if (srv != null && srv.ServiceType != PISBase.SERVICE_TYPE_GROUP) {
                                        srv.request(srv.updatePISInfo());
                                        List<PISBase> grps = srv.getGroupObjects();
                                        if (grps == null || grps.size() == 0) {
                                            srv.request(srv.updateGroupInfo());
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                        } finally {
                            mHandler.sendEmptyMessageDelayed(1000, 2000);
                        }

                    }

                });

        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_CTL_STATUS_NOTIFY, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_LIGHTNESS_STATUS_NOTIFY, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_LEVEL_STATUS, this);
        MyApplication.getInstance().addEventListener(OnlineStatusEvent.ONLINE_STATUS_NOTIFY, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DEVICE_OFFLINE, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_RESET, this);

        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);

    }

    private void checkTelinkGroups() {
        List<Group> groups = TelinkGroupApiManager.getInstance().getTelinkGroups();
        for (Group group : groups) {
            TelinkGroupApiManager.getInstance().deletePISGroup(group.PISKeyString, new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {

                }

                @Override
                public void onRequestResult(PipaRequest req) {
                    // todo lee 删除开始
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity != null) {
                setHandler();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DeviceController callback interface.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeEventListener(this);
        mCycleHandler.removeCallbacksAndMessages(null);
    }

    int index = 0;
    boolean cycleTestStarted = false;
    private Runnable cycleTask = new Runnable() {
        @Override
        public void run() {
//            MeshService.getInstance().cmdOnOff(index % 2 == 0 ? 3 : 4, (byte) 1, (byte) ((index / 2) % 2), 1);
            MeshService.getInstance().setOnOff(index % 2 == 0 ? 3 : 4, (byte) ((index / 2) % 2), true, 1, 0, (byte) 0, null);
            index++;
            mCycleHandler.postDelayed(this, 2 * 1000);
        }
    };
    private static final String TAG_ALL_ON = "ALL_ON";
    private static final String TAG_ALL_OFF = "ALL_OFF";
    private long startTime;

    @Override
    public void performed(Event<String> event) {
        if (event.getType().equals(MeshEvent.EVENT_TYPE_DISCONNECTED) || event.getType().equals(MeshEvent.EVENT_TYPE_DEVICE_OFFLINE)
                || event.getType().equals(MeshEvent.EVENT_TYPE_MESH_RESET)
                || event.getType().equals(NotificationEvent.EVENT_TYPE_DEVICE_LEVEL_STATUS)) {
            refreshUI();
        } else if (event.getType().equals(CommandEvent.EVENT_TYPE_CMD_COMPLETE)) {
            CommandEvent commandEvent = (CommandEvent) event;
            long during = System.currentTimeMillis() - startTime;
            if (TAG_ALL_ON.equals(commandEvent.getMeshCommand().tag)) {
                saveLog("All On during:" + during + "ms");
            } else if (TAG_ALL_OFF.equals(commandEvent.getMeshCommand().tag)) {
                saveLog("All Off during:" + during + "ms");
            }
        } else {
            if (event instanceof NotificationEvent) {
                if (((NotificationEvent) event).isStatusChanged()) {
                    refreshUI();
                }
            } else if (event instanceof OnlineStatusEvent) {
                refreshUI();
            }
        }
    }

    private void saveLog(String action) {
        MyApplication.getInstance().saveLog(" --test-- " + action);
    }

    private void refreshUI() {
        EventBus.getDefault().post(new TelinkDataRefreshEntry());
    }

    Set<String> pisKeyStringSet = new HashSet<>();
    /**
     * 刷新列表
     *
     * @param deviceList
     */
    public void setLightAdapter(List<GeneralDeviceModel[]> deviceList) {
        if (null == manager) {
            manager = PISManager.getInstance();
        }
        if (deviceList != null){
            mLights = deviceList;
        }

        refreshListView();
        mHandler.sendEmptyMessageDelayed(2000, 2000);
    }


    public void refreshListView() {
        // TODO LEE 灯组列表刷新
        if (deviceAdapter != null && mLights != null) {
            deviceAdapter.setList((ArrayList<GeneralDeviceModel[]>) mLights, false);
            deviceAdapter.notifyDataSetChanged();
        }
    }
}
