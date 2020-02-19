package net.senink.seninkapp.ui.home;

import java.util.List;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.struct.PipaServiceInfo;
import net.senink.seninkapp.ui.activity.SwitchDetailActivity;
import net.senink.seninkapp.ui.entity.DeviceTypeName;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.view.CusPullListView;
import net.senink.seninkapp.ui.view.CusPullListView.OnRefreshListener;
import net.senink.seninkapp.ui.view.TopNavArea;

public class DeviceNameListsActivity extends BaseActivity implements
		OnClickListener {
	private TopNavArea topNavArea;
	private Button rightBt;
	private CusPullListView listView;
	private Handler handler = new Handler();
	private ProgressBar loading;
	private PISManager pm;
	RegistedDevicelistAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_name_list_activity);
		initView();

	}

	private void initView() {
		pm = PISManager.getInstance();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
		topNavArea = (TopNavArea) findViewById(R.id.icon_search_top);
		topNavArea.setTitle(getResources().getString(R.string.set_device_name));
		rightBt = (Button) findViewById(R.id.back);
		rightBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0,
				0, 0);
		rightBt.setOnClickListener(this);
		rightBt.setText("");
		listView = (CusPullListView) findViewById(R.id.she_bei_list);
		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View view,
					int arg2, int arg3, long arg4) {
				try {
//					PipaServiceInfo pip = new PipaServiceInfo(
//							SwitchDetailActivity.pisSwitch);
//					TextView tv = (TextView) view.findViewById(R.id.she_bei_list_lebal);
//					String name = String.valueOf(tv.getText());
//					if (name != null && name.length() > 16) {
//						name = name.substring(0, 16);
//					}
//					pip.setName(name);
//					SwitchDetailActivity.pisSwitch.setPISInfo(pip, true);
//					SwitchDetailActivity.pisSwitch.mName = name;
//					finish();
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
				return false;
			}
		});
		listView.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				requestData();
			}

			@Override
			public void onStartRefresh() {
				// TODO Auto-generated method stub

			}

		});
		loading = (ProgressBar) findViewById(R.id.loading);
		requestData();
	}

	List<DeviceTypeName> list;

	private void requestData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				list = HttpUtils
						.getDeviceNameList(DeviceNameListsActivity.this);
				handler.post(new Runnable() {

					@Override
					public void run() {
						listView.setGroupIndicator(null);
						if (list != null && list.size() > 0) {
							adapter = new RegistedDevicelistAdapter(
									DeviceNameListsActivity.this, list);
							listView.setAdapter(adapter);
							for (int i = 0; i < list.size(); i++) {
								listView.expandGroup(i);
							}

						}
						loading.setVisibility(View.GONE);
						listView.onRefreshComplete();
					}
				});
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			ziDiDialog();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	public class RegistedDevicelistAdapter extends BaseExpandableListAdapter {
		private Context context;
		private List<DeviceTypeName> devices;

		public RegistedDevicelistAdapter(Context context,
				List<DeviceTypeName> devices) {
			this.context = context;
			this.devices = devices;
		}

		@Override
		public int getGroupCount() {
			if (devices != null) {
				return devices.size();
			}
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			int count = devices.get(groupPosition).names.size();
			return count;
		}

		@Override
		public DeviceTypeName getGroup(int groupPosition) {
			if (devices != null) {
				return devices.get(groupPosition);
			}
			return null;
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			return devices.get(groupPosition).names.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = new TextView(context);
			tv.setTextSize(20);
			tv.setText(getGroup(groupPosition).groupname);
			tv.setBackgroundColor(0xffdddddd);
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout layout1 = new LinearLayout(context);
			layout1.setOrientation(LinearLayout.VERTICAL);
			layout1.setGravity(Gravity.CENTER_VERTICAL);
			layout.addView(layout1, LinearLayout.LayoutParams.MATCH_PARENT, 100);
			TextView tv = new TextView(context);
			tv.setId(R.id.she_bei_list_lebal);
			tv.setText(getChild(groupPosition, childPosition));
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setPadding(10, 0, 0, 0);
			layout1.addView(tv, LinearLayout.LayoutParams.MATCH_PARENT, 100);
			return layout;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	EditText nameEd;
	Spinner spinner;
	private ProgressDialog progressDialog;
	private int curSelectItem = -1;

	private void ziDiDialog() {
		if (list == null) {
			return;
		}
		String[] m = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				curSelectItem = list.get(i).groupid;
			}
			m[i] = list.get(i).groupname;
		}
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		nameEd = new EditText(this);
		nameEd.setHint(getResources().getString(R.string.hide_device_name));
		spinner = new Spinner(this);
		layout.addView(spinner);
		layout.addView(nameEd);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, m);

		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				curSelectItem = list.get(arg2).groupid;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.input_alert))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(layout)
				.setPositiveButton(getResources().getString(R.string.ok),
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (progressDialog != null) {
									progressDialog.show();
									updateData();
								}
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						null).show();
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.updating));

	}

	private void updateData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String name = String.valueOf(nameEd.getText()).trim();
					if (name != null && name.length() > 16) {
						name = name.substring(0, 16);
					}
					final boolean flag = HttpUtils.addDeviceName(
							DeviceNameListsActivity.this, name, curSelectItem);
					handler.post(new Runnable() {

						@Override
						public void run() {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							// LocationListsActivity.this.finish();
							if (flag) {
								requestData();
							}
						}
					});
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}
		}).start();
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
//		// TODO Auto-generated method stub
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
//
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
//		// TODO Auto-generated method stub
//
//	}
}
