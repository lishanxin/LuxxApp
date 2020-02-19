package net.senink.seninkapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.senink.piservice.services.PISXinSwitch;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.SwitcherAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSwitch;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.view.LightScrollView;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.Mode;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase.OnRefreshListener;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;

/**
 * 插排的界面
 * 
 * @author zhoajunfeng
 * @date 2015-11-09
 */

public class SwitchFragment extends Fragment {

	public static final String TAG = "SwitchFragment";
	//刷新界面
    private final static int MSG_UPDATE_VIEW = 10;
    //获取开关状态成功
	protected static final int MSG_GET_STATUS_SUCCESS = 11;
	private View mRootView;
	// 显示插排列表的组件
	private PullToRefreshListView mRefreshListView;
	// 显示插排列表的组件
	private ListView listView;
	//滚动布局
	private LightScrollView scrollView;
	// 列表组件的适配器
	private SwitcherAdapter adapter;
	private PISManager manager;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1000) {
				adapter.notifyDataSetChanged();
				mRefreshListView.onRefreshComplete();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == mRootView) {
			mRootView = inflater.inflate(R.layout.fragment_switcher, container,
					false);
			mRefreshListView = (PullToRefreshListView) mRootView.findViewById(R.id.switcher_list);
			listView = mRefreshListView.getRefreshableView();
			mRefreshListView.setMode(Mode.PULL_FROM_START);
			setSwitchAdapter(null);
			setListener();
		}
		return mRootView;
	}

	private void setListener() {
		mRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> arg0) {
				manager = PISManager.getInstance();

				mHandler.sendEmptyMessageDelayed(1000, 2000);
				manager.DiscoverAll();
				ArrayList<PISBase[]> list = adapter.getList();
				if (list == null)
					return;
				for (PISBase[] bases : list) {
					for (PISBase base : bases) {
						base.request(base.updatePISInfo());
					}
				}
			}

		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/**
	 * refresh the list
	 * 
	 * @param list
	 */
	public void setSwitchAdapter(ArrayList<PISBase[]> list) {
		if (null == adapter) {
			adapter = new SwitcherAdapter(getActivity(), list);
			listView.setAdapter(adapter);
		} else {
			adapter.setList(list);
			adapter.notifyDataSetChanged();
		}
	}
}
