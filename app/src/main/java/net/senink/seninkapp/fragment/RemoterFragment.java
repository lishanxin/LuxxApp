package net.senink.seninkapp.fragment;

import java.util.ArrayList;
import java.util.List;

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
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.RemotersAdapter;

import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.Mode;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.OnRefreshListener;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;

/**
 * 蓝牙灯列表的界面
 * 
 * @author zhaojunfeng
 * @date 2015-08-27
 * 
 */
public class RemoterFragment extends Fragment {
	public static final String TAG = "RemoterFragment";
	private View mRootView;
	// 显示遥控器列表的组件
	private PullToRefreshListView mRefreshListView;
	private ListView listView;
	// 列表组件的适配器
	private RemotersAdapter deviceAdapter;

	private PISManager manager;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1000) {
				mRefreshListView.onRefreshComplete();
//				ArrayList<PISBase[]> list = SortUtils.sortRemoters(manager
//						.getPisDeviceList());
//				setRemoterAdapter(list);
			}
		}
	};

	public RemoterFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_lightremoter,
					container, false);
			mRefreshListView = (PullToRefreshListView) mRootView
					.findViewById(R.id.remoter_list);
			mRefreshListView.setMode(Mode.PULL_FROM_START);
			listView = mRefreshListView.getRefreshableView();
			if (null == deviceAdapter) {
				deviceAdapter = new RemotersAdapter(
						(HomeActivity) getActivity(), null);
			}
			listView.setAdapter(deviceAdapter);
			setListener();
		}
		return mRootView;
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		mRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						manager = PISManager.getInstance();
						manager.DiscoverAll();
						mHandler.sendEmptyMessageDelayed(1000, 1000);
					}

				});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 刷新列表
	 * 
	 * @param deviceList
	 */
	public void setRemoterAdapter(ArrayList<PISBase[]> deviceList) {
		if (null == deviceAdapter) {
			deviceAdapter = new RemotersAdapter((HomeActivity) getActivity(),
					deviceList);
			if (null != listView) {
				listView.setAdapter(deviceAdapter);
			}
		} else {
			deviceAdapter.setList(deviceList);
			deviceAdapter.notifyDataSetChanged();
		}
	}

	public void setRemoterAdapter() {
		if (null != deviceAdapter) {
			deviceAdapter.notifyDataSetChanged();
		}
	}
}
