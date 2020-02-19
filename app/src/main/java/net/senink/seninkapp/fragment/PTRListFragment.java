package net.senink.seninkapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.LightListAdapter;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

/**
 * Created by wensttu on 2016/7/17.
 */
public class PTRListFragment extends Fragment {
    protected View mRootView;
    // 显示列表的组件
    protected ListView deviceListView;
    // 显示列表的组件
    protected PullToRefreshListView mRefreshListView;
    // 列表组件的适配器
    protected LightListAdapter deviceAdapter;
    protected PISManager manager;

    protected Handler mHandler = null;

    private int rootResourceId = 0;
    private int ptrResourceId = 0;

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
        return mRootView;
    }

    public void setRefreshListener(PullToRefreshBase.OnRefreshListener<ListView> l){
        mRefreshListView.setOnRefreshListener(l);
    }

    public void setAdapter(BaseAdapter adapter){
        deviceListView.setAdapter(adapter);
    }
}
