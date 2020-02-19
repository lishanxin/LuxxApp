package net.senink.seninkapp.fragment;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.InsoleListAdapter;
//import com.senink.seninkapp.core.PISInsoleGroup;
//import com.senink.seninkapp.core.PISManager;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.view.RemoterListView;

/**
 * 智能穿戴的界面
 * 
 * @author zhaojunfeng
 * @date 2016-01-30
 * 
 */
public class InsoleFragment extends Fragment {
	public static final String TAG = "InsoleFragment";
	private View mRootView;
	// 显示遥控器列表的组件
	private RemoterListView listView;
	// 列表组件的适配器
	private InsoleListAdapter deviceAdapter;

	private PISManager manager;

	public InsoleFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.fragment_insole,
					container, false);
			listView = (RemoterListView) mRootView
					.findViewById(R.id.insole_list);
			if (null == deviceAdapter) {
				deviceAdapter = new InsoleListAdapter(
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
		listView.setonRefreshListener(new RemoterListView.OnRefreshListener() {

			@Override
			public void onStartRefresh() {

			}

			@Override
			public void onRefresh() {
				if (null == manager) {
					manager = PISManager.getInstance();
				}
				if (manager.getMCSObject() != null) {
					PipaRequest req = manager.getMCSObject().getGroupInfos();
					req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
						@Override
						public void onRequestStart(PipaRequest req) {

						}

						@Override
						public void onRequestResult(PipaRequest req) {

						}
					});
					manager.getMCSObject().request(req);
				}
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

//	/**
//	 * 刷新列表
//	 * 
//	 * @param deviceList
//	 */
//	public void setInsoleAdapter(ArrayList<PISBase[]> deviceList) {
//		if (null == deviceAdapter) {
//			deviceAdapter = new InsoleListAdapter((HomeActivity) getActivity(),
//					deviceList);
//			if (null != listView) {
//				listView.setAdapter(deviceAdapter);
//			}
//		} else {
//			deviceAdapter.setList(deviceList);
//			deviceAdapter.notifyDataSetChanged();
//		}
//	}
	
	public void setInsoleAdapter() {
//		ArrayList<PISInsoleGroup[]> groups = getList();
//		if (null == deviceAdapter) {
//			deviceAdapter = new InsoleListAdapter((HomeActivity) getActivity(), groups);
//		    listView.setAdapter(deviceAdapter);
//		}else{
//			deviceAdapter.setList(groups);
//			deviceAdapter.notifyDataSetChanged();
//		}
	}

//	private ArrayList<PISInsoleGroup[]> getList() {
//		ArrayList<PISInsoleGroup[]> groups = new ArrayList<PISInsoleGroup[]>();
//		SparseArray<PISInsoleGroup> infors = manager.getInsoleGroups();
//		if (infors != null && infors.size() > 0) {
//			int size = infors.size() / 4;
//			if (infors.size() % 4 > 0) {
//				size ++;
//			}
//			for (int i = 0; i < size; i++) {
//				PISInsoleGroup[] array = new PISInsoleGroup[4];
//				for (int j = 0; j < 4; j++) {
//					if (i * 4 + j < infors.size()) {
//						array[j] = infors.valueAt(j);
//					}else{
//						break;
//					}
//				}
//				groups.add(array);
//			}
//		}
//		return groups;
//	}
}
