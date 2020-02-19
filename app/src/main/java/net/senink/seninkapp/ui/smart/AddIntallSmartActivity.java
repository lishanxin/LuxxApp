package net.senink.seninkapp.ui.smart;

import java.util.List;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.view.CusPullNewListView;

/**
 * 
 * @author liss
 * 
 */

public class AddIntallSmartActivity extends BaseActivity {

	private CusPullNewListView mListViewControll;
//	private AddInteligentAdapter mAdapter;
//	private TextView mTitleTextView;
	private Handler handler = new Handler();
	private ProgressBar loading;
	private View backBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_intelligent);
		initView();
	}

	private void initView() {
		mListViewControll = (CusPullNewListView) findViewById(R.id.listview_controll);
		backBt = (ImageView) findViewById(R.id.back);
		backBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		mListViewControll.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
//				SubCategoryHolder holder = (SubCategoryHolder) view.getTag();
				link("http://www.hao123.com");
			}

		});
		loading = (ProgressBar) findViewById(R.id.loading);
		requestData();
		mListViewControll.setonRefreshListener(new CusPullNewListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				requestData();
			}

			@Override
			public void onStartRefresh() {
				
			}
		});
//		PISManager.getInstance().setOnPISChangedListener(this);
//		PISManager.getInstance().setOnPipaRequestStatusListener(this);
	}
/**
 * 先定义List
 */
	List<SmartCellItem> list;
	AddInteligentAdapter adapter;

	private void requestData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				/**
				 * CY修改通过web过来的智能元信息
				 */
				list = HttpUtils.getSmartCellItem(
						AddIntallSmartActivity.this, SharePreferenceUtils
								.getInstance(AddIntallSmartActivity.this)
								.getCurrentUser());
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter = new AddInteligentAdapter(
								AddIntallSmartActivity.this, list);
						mListViewControll.setAdapter(adapter);
						loading.setVisibility(View.GONE);
						mListViewControll.onRefreshComplete();
					}
				});
			}
		}).start();
	}

	private void link(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}
//
//	@Override
//	public void onRequestStart(PISBase pis, int reqType) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onRequestResult(PISBase pis, int reqType, int result) {
//
//		if (pis == null) {
//			return;
//		}
//		if (pis instanceof PISSmartCell) {
//			switch (reqType) {
//			case PISSmartCell.PIS_CMD_SMART_GET_STAT:
//			case PISSmartCell.PIS_CMD_SMART_STOP:
//			case PISSmartCell.PIS_CMD_SMART_START:
//				break;
//			}
//		} else if (pis instanceof PISDevice) {
//			switch (reqType) {
//			case PISDevice.PIS_CMD_DEVICE_INS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					Toast.makeText(this, R.string.install_smart_succ,
//							Toast.LENGTH_SHORT).show();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(this, R.string.install_smart_fail,
//							Toast.LENGTH_SHORT).show();
//				}
//				break;
//			case PISDevice.PIS_CMD_DEVICE_UNS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					refreshValue();
//					Toast.makeText(this, R.string.del_smart_succ,
//							Toast.LENGTH_SHORT).show();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(this, R.string.del_smart_fail,
//							Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
//
//		} else if (pis instanceof PISMCSManager) {
//
//			switch (reqType) {
//			case PISMCSManager.PIS_CMD_DEVICE_INS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					Toast.makeText(this, R.string.install_smart_succ,
//							Toast.LENGTH_SHORT).show();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(this, R.string.install_smart_fail,
//							Toast.LENGTH_SHORT).show();
//				}
//				break;
//			case PISMCSManager.PIS_CMD_DEVICE_UNS_SMART:
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {// 成功
//					refreshValue();
//					Toast.makeText(this, R.string.del_smart_succ,
//							Toast.LENGTH_SHORT).show();
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//					Toast.makeText(this, R.string.del_smart_fail,
//							Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
//		}
//
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		// TODO Auto-generated method stub
//		if (pis instanceof PISSmartCell) {
//			PISSmartCell pc = (PISSmartCell) pis;
//			Log.d("test", "onPIServiceInfoChange guid ==>" + pc.getGUID());
//			try {
//				for (int j = 0; j < list.size(); j++) {
//					SmartCellItem sc = list.get(j);
//					if (pc.getGUID().equals(sc.guid)) {
//						sc.isInstall = true;
//						sc.pc = pc;
//						sc.instanceID = pc.getInstanceID();
//						sc.pisSmartCell = pc;
//						break;
//					}
//				}
//				if (adapter != null) {
//					adapter.notifyDataSetChanged();
//				}
//			} catch (Exception e) {
//				net.senink.seninkapp
//			}
//		}
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//	}
//
//	private void refreshValue() {
//		// try {
//		// ArrayList<PISBase> pisBases = PISManager.getInstance()
//		// .getPisSmartCellList();
//		// if (list != null && list.size() > 0) {
//		// for (int i = 0; i < list.size(); i++) {
//		// SmartCellItem cellItem = list.get(i);
//		// for (int j = 0; j < pisBases.size(); j++) {
//		// PISSmartCell pc = (PISSmartCell) pisBases.get(j);
//		// if (pc.getGUID().equals(cellItem.guid)) {
//		// cellItem.isInstall = true;
//		// cellItem.pc = pc;
//		// cellItem.instanceID = pc.getInstanceID();
//		// cellItem.pisSmartCell = pc;
//		// break;
//		// }
//		// }
//		// }
//		// AddInteligentAdapter adapter = new AddInteligentAdapter(
//		// AddIntallSmartActivity.this, list);
//		// mListViewControll.setAdapter(adapter);
//		// }
//		//
//		// } catch (Exception e) {
//		// net.senink.seninkapp
//		// }
//		requestData();
//	}

}