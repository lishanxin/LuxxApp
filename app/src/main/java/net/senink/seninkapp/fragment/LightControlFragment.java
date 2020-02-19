package net.senink.seninkapp.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.LightListAdapter;

import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;

/**
 * 蓝牙灯列表的界面
 *
 * @author zhaojunfeng
 * @date 2015-07-09
 */
public class LightControlFragment extends Fragment {
    public static final String TAG = "LightControlFragment";

    private View mRootView;
    // 显示灯列表的组件
    private ListView deviceListView;
    // 显示插排列表的组件
    private PullToRefreshListView mRefreshListView;
    // 列表组件的适配器
    private LightListAdapter deviceAdapter;
    private PISManager manager;
    private Handler mHandler = null;

    private int isRefersh = 0;
    // 灯列表
    private List<PISBase[]> mLights;

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
            deviceAdapter = new LightListAdapter((HomeActivity) getActivity(), null);
            deviceListView.setAdapter(deviceAdapter);
        }

        setData();
        setListener();
        return mRootView;
    }

    @Override
    public void onDetach() {
        if (mHandler != null){
            mHandler.removeMessages(2000);
        }
        super.onDetach();
    }

    @Override
    public void onPause() {
        if (mHandler != null){
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
                            for (PISBase[] srvs : mLights) {
                                for (PISBase srv : srvs) {
                                    if (srv != null && srv.ServiceType != PISBase.SERVICE_TYPE_GROUP) {
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

    /**
     * 刷新列表
     *
     * @param deviceList
     */
    public void setLightAdapter(ArrayList<PISBase[]> deviceList) {
        if (null == manager) {
            manager = PISManager.getInstance();
        }
        if (deviceList != null)
            mLights = deviceList;

        refreshListView();
        mHandler.sendEmptyMessageDelayed(2000, 2000);
    }


    public void refreshListView() {
        // TODO LEE 灯组列表刷新
        if (deviceAdapter != null && mLights != null) {
            deviceAdapter.setList((ArrayList<PISBase[]>) mLights, false);
            deviceAdapter.notifyDataSetChanged();
        }
    }
}
