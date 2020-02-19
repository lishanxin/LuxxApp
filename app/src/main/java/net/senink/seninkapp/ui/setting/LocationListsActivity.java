package net.senink.seninkapp.ui.setting;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSwitch;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.struct.PipaServiceInfo;
import net.senink.seninkapp.ui.activity.SwitchDetailActivity;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.LocationName;
import net.senink.seninkapp.ui.setting.LocationListsActivity.SheBeiListsAdater.Holder;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.view.BottomNavArea;
import net.senink.seninkapp.ui.view.CusPullNewListView;

public class LocationListsActivity extends BaseActivity implements
		OnClickListener {
	private TextView tvTitle;
	private Button backBtn;
	private ImageButton addBtn;
	private CusPullNewListView listView;
	private Handler handler = new Handler();
	private ProgressBar loading;
	private PISManager pm;
	SheBeiListsAdater adapter;
	private PISDevice pisDevice;

	// public static curType
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_list_activity);
		initView();
		setListener();
		setData();
	}

	private void setData() {
		if (getIntent() != null && getIntent().hasCategory("mac")) {
//			pisDevice = (PISDevice) PISManager.cacheMap.get(getIntent()
//					.getStringExtra("mac"));
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			if (hasFocus) {
				BottomNavArea bn = (BottomNavArea) findViewById(R.id.icon_search_bottom);
				bn.updateSelectState(getIntent().getStringExtra("tab"));
			}
		} catch (Exception e) {
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	private void initView() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.updating));
		pm = PISManager.getInstance();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
		tvTitle = (TextView) findViewById(R.id.title_name);
		tvTitle.setText(R.string.location_title);
		backBtn = (Button) findViewById(R.id.title_back);
		addBtn = (ImageButton) findViewById(R.id.title_add);
		backBtn.setVisibility(View.VISIBLE);
		addBtn.setVisibility(View.VISIBLE);
		loading = (ProgressBar) findViewById(R.id.loading);
		requestData();
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		listView = (CusPullNewListView) findViewById(R.id.she_bei_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
//				try {
//					pisDevice.mLocation = Byte.parseByte(((Holder) view
//							.getTag()).loctiaon.location);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				try {
//					SwitchDetailActivity.curLocation = ((Holder) view.getTag()).loctiaon.name;
//				} catch (Exception e) {
//
//				}
//
//				try {
//					if (pisDevice != null) {
//						PipaServiceInfo pip = new PipaServiceInfo(pisDevice);
//						Integer iO = Integer.parseInt(((Holder) view.getTag()).loctiaon.location);
//						pip.mLocation = iO.byteValue();
//						pisDevice.setPISInfo(pip, true);
//					}
//					if (SwitchDetailActivity.pisSwitch != null) {
//						PipaServiceInfo pip = new PipaServiceInfo(
//								SwitchDetailActivity.pisSwitch);
//						Integer iO = Integer.parseInt(((Holder) view.getTag()).loctiaon.location);
//						pip.mLocation = iO.byteValue();
//						SwitchDetailActivity.pisSwitch.setPISInfo(pip, true);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				if (progressDialog != null) {
					progressDialog.setMessage(getResources().getString(
							R.string.updating));
					progressDialog.show();
				}
			}
		});
		listView.setonRefreshListener(new CusPullNewListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				requestData();
			}

			@Override
			public void onStartRefresh() {
				// TODO Auto-generated method stub

			}
		});
	}

	private void requestData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final List<LocationName> list = HttpUtils
						.getLocationAndSave(LocationListsActivity.this);
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter = new SheBeiListsAdater(
								LocationListsActivity.this, list);
						listView.setAdapter(adapter);
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
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.title_add:
			ziDiDialog();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	public class SheBeiListsAdater extends BaseAdapter {
		List<LocationName> list;

		public SheBeiListsAdater(Context context, List<LocationName> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public LocationName getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			LocationName location = getItem(position);
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(LocationListsActivity.this)
						.inflate(R.layout.location_list_item, null);
				holder.nameTv = (TextView) convertView.findViewById(R.id.name);
				holder.positionTv = (TextView) convertView
						.findViewById(R.id.position);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.nameTv.setText(location.name);
			holder.icon.setVisibility(View.GONE);
			holder.loctiaon = location;
			return convertView;
		}

		class Holder {
			TextView nameTv;
			TextView positionTv;
			ImageView icon;
			LocationName loctiaon;
		}

	}

	EditText nameEd;
	EditText indexEd;
	EditText floorEd;
	private ProgressDialog progressDialog;

	private void ziDiDialog() {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		nameEd = new EditText(this);
		nameEd.setHint(getResources().getString(R.string.room_name));
		indexEd = new EditText(this);
		indexEd.setHint(getResources().getString(R.string.location_bian_hao));
		indexEd.setInputType(InputType.TYPE_CLASS_NUMBER);
		floorEd = new EditText(this);
		floorEd.setHint(getResources().getString(R.string.floor));
		floorEd.setInputType(InputType.TYPE_CLASS_NUMBER);
		layout.addView(nameEd);
		layout.addView(indexEd);
		layout.addView(floorEd);
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

	}

	private void updateData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpUtils.addRoomType(
							LocationListsActivity.this,
							String.valueOf(nameEd.getText()).trim(),
							Integer.parseInt(indexEd.getText().toString()
									.trim()),
							Integer.parseInt(floorEd.getText().toString()
									.trim()));
					handler.post(new Runnable() {

						@Override
						public void run() {
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							// LocationListsActivity.this.finish();
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
//		try {
//			if (PISBase.PIS_CMD_SRVINFO_SET == reqType) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					if (SwitchDetailActivity.pisSwitch != null) {
//						SwitchDetailActivity.pisSwitch.getPISInfo();
//					}
//					if (pisDevice != null) {
//						pisDevice.getPISInfo();
//					}
//
//				} else {
//					if (progressDialog != null && progressDialog.isShowing()) {
//						progressDialog.dismiss();
//					}
//					if (result == PipaRequest.REQUEST_RESULT_ERROR) {
//						Toast.makeText(LocationListsActivity.this,
//								R.string.operator_error, Toast.LENGTH_SHORT)
//								.show();
//					} else if (result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//						Toast.makeText(LocationListsActivity.this,
//								R.string.link_timeout, Toast.LENGTH_SHORT)
//								.show();
//					}
//				}
//			} else if (PISBase.PIS_CMD_SRVINFO_GET == reqType) {
//				if (progressDialog != null && progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
//				if (pis instanceof PISSwitch) {
//					SwitchDetailActivity.pisSwitch = (PISSwitch) pis;
//				}
//				if (pis instanceof PISDevice) {
//					pisDevice = (PISDevice) pis;
//				}
//				Intent it = new Intent();
//				it.putExtra(MessageModel.LOCATION, pis.mLocation);
//				setResult(RESULT_OK, it);
//				finish();
//				overridePendingTransition(R.anim.anim_in_from_left,
//						R.anim.anim_out_to_right);
//			}
//		} catch (Exception e) {
//			net.senink.seninkapp
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
