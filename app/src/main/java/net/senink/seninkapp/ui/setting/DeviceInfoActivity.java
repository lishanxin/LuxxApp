package net.senink.seninkapp.ui.setting;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISUpdate;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.entity.DeviceInfo;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;

public class DeviceInfoActivity extends BaseActivity implements
		OnClickListener {
	// 界面跳转时的请求码
	public final static int REQUEST_CODE = 2000;
	private static final int MSG_GET_DEVICEINFOR_SUCCESS = 10;
	private Button backBtn;
	private TextView tvTitle;
	private ImageView ivTitle;
	private ImageView device_icon;
	private TextView device_name, position_name, version, mac, maker,
			xing_hao, product;
	private Button hard_version_right, unbond;
	private ProgressDialog progressDialog;
	private ProgressBar loading;
	private PISManager pm;
	private PISDevice pisDevice;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == MSG_GET_DEVICEINFOR_SUCCESS) {
				setViews();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_info_activity);
		initView();
		setData();
		setListener();
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		ivTitle = (ImageView) findViewById(R.id.title_logo_center);
		backBtn = (Button)findViewById(R.id.title_back);
		device_icon = (ImageView) findViewById(R.id.device_icon);
		device_name = (TextView) findViewById(R.id.device_name);
		position_name = (TextView) findViewById(R.id.position_name);
		hard_version_right = (Button) findViewById(R.id.hard_version_right);
		version = (TextView) findViewById(R.id.version);
		mac = (TextView) findViewById(R.id.mac);
		unbond = (Button) findViewById(R.id.unbond);
		maker = (TextView) findViewById(R.id.maker);
		xing_hao = (TextView) findViewById(R.id.xing_hao);
		product = (TextView) findViewById(R.id.product);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.loading));
		loading = (ProgressBar) findViewById(R.id.loading);
		
		loading.setVisibility(View.GONE);
//		tvTitle.setText(R.string.device_info_title);
		tvTitle.setVisibility(View.GONE);
		ivTitle.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
		
		pm = PISManager.getInstance();
//		pm.setOnPISChangedListener(this);
//		pm.setOnPipaRequestStatusListener(this);
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		hard_version_right.setOnClickListener(this);
		unbond.setOnClickListener(this);
		findViewById(R.id.position_area).setOnClickListener(this);
		findViewById(R.id.hard_version_area).setOnClickListener(this);
	}

	private void setData() {
		Intent intent = getIntent();
		pisDevice = (PISDevice)pm.getPISObject(intent.getStringExtra("keystring"));
	}

	private void setViews() {
		String positionName = setLocation();
//		if (pisDevice.deviceInfor != null) {
//			if (!TextUtils.isEmpty(pisDevice.deviceInfor.classid)) {
//				String path = SharePreferenceUtils.getValue(this, pisDevice.deviceInfor.classid);
//				if (!TextUtils.isEmpty(pisDevice.deviceInfor.classid)) {
//					device_icon.setImageBitmap(BitmapFactory.decodeFile(path));
//				} else {
//					new Thread(new LoadIcon(device_icon,
//							pisDevice.deviceInfor.img1)).start();
//				}
//			}
//			if (pisDevice.deviceInfor.title != null){
//				device_name.setText(pisDevice.deviceInfor.title);
//			}
//			maker.setText(pisDevice.deviceInfor.mader);
//			xing_hao.setText(pisDevice.deviceInfor.model);
//			product.setText(pisDevice.deviceInfor.sourced);
//		}
		try {
			if (pisDevice != null && pisDevice.getMacString() != null) {
				device_icon.setImageResource(
						ProductClassifyInfo.getProductResourceId(pisDevice.getClassString()));
				device_name.setText(pisDevice.getPIServices().get(0).getName());
				mac.setText(pisDevice.getMacString().replaceAll(".{2}(?!$)", "$0-"));
				version.setText(pisDevice.getVersionString());
			}
		}
		catch (IndexOutOfBoundsException e){
			device_name.setText(R.string.unknown);
			mac.setText(R.string.unknown);
			version.setText(R.string.unknown);
		}
		catch (Exception e){
			PgyCrashManager.reportCaughtException(this, e);
		}
	}

	private String setLocation() {
		String positionName = null;
		if (pisDevice != null) {
			positionName = SharePreferenceUtils
					.getLocationValue(this, String.valueOf(pisDevice.getLocation()));
		}
		if (null == positionName || "".equals(positionName)) {
			positionName = getResources().getString(R.string.no_know);
//			if (pisDevice.deviceInfor != null && pisDevice.deviceInfor.location != null) {
//				positionName = pisDevice.deviceInfor.location;
//			}
		}
		return positionName;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//			pisDevice.mLocation = data.getByteExtra(MessageModel.LOCATION,(byte)0);
			position_name.setText(setLocation());
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setViews();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hard_version_area:
			LogUtils.i("test", "update");
//			if (loading.getVisibility() == View.GONE
//					&& (pisDevice != null && pisDevice.hasNewVersion() && pisDevice
//							.getFwUpdateProcess() == 0)) {
//				LogUtils.i("test", "update  run");
//				loading.setVisibility(View.VISIBLE);
//				boolean result = pm.PisDeviceUpdate(pisDevice.getMacAddr(),
//						pisDevice.getNewFwVersion1(),
//						pisDevice.getNewFwVersion2(),
//						pisDevice.getNewFwVersion3(),
//						pisDevice.getNewFwVersionLen());
//				if (result) {
//					pisDevice.subscibeFwUpdateCompleted();
//					pisDevice.subscibeFwUpdateProgress();
//				}
//			}

			break;
		case R.id.unbond:
			
			break;
		case R.id.position_area:
			if (pisDevice != null){
//				PISManager.cacheMap.put(pisDevice.getMacAddr(), pisDevice);
//			    Intent it = new Intent(this, LocationListsActivity.class);
//			    it.putExtra("mac",pisDevice.macAddr);
//				startActivityForResult(it, REQUEST_CODE);
//				overridePendingTransition(R.anim.anim_in_from_right,
//						R.anim.anim_out_to_left);
			}
			break;
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
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
//
//			if (reqType == PISUpdate.PIS_CMD_FWUPDATE_CHECK) {
//				if (result == PipaRequest.REQUEST_RESULT_COMPLETE) {
//					if (pis instanceof PISUpdate) {
//						// pisDevice = (PISDevice) pis;
//						if (pisDevice.hasNewVersion()) {
//							version.setText(getResources().getString(
//									R.string.alert_version_update)
//									+ pisDevice.getNewFwVersion());
//						}
//					}
//				} else if (result == PipaRequest.REQUEST_RESULT_ERROR
//						|| result == PipaRequest.REQUEST_RESULT_TIMEOUT) {
//
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onNetworkStatusChange(int netConnStatus) {
//
//	}
//
//	@Override
//	public void onFirmwareInfoChange() {
//
//	}
//
//	@Override
//	public void onPIServiceInfoChange(PISBase pis) {
//		try {
//			if (pis instanceof PISDevice
//					&& ((PISDevice) pis).getMacAddr().equals(
//							pisDevice.getMacAddr())) {
//				pisDevice = (PISDevice) pis;
//				if (pisDevice.getFwUpdateProcess() > 0) {
//					LogUtils.i("test", "no  no  send update ........");
//					version.setText(getResources()
//							.getString(R.string.update_to)
//							+ pisDevice.getFwUpdateProcess() + "%");
//					if (pisDevice.getFwUpdateProcess() == 100) {
//						pm.setAllPisOffline();
//						loading.setVisibility(View.GONE);
//						version.setText(getResources().getString(
//								R.string.update_complete));
//
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void onPIDevicesChange() {
//
//	}
//
//	@Override
//	public void onPIServicesChange(PISBase pis, boolean isNew) {
//
//	}

	Handler handler = new Handler();

	private class DeviceInforThread extends Thread{

		@Override
		public void run() {
			if (pisDevice != null) {
//				ArrayList<DeviceInfo> list = HttpUtils.getDeviceInfoDetial(DeviceInfoActivity.this, null, pisDevice.getClassID(), "", "","");
//			    if (list != null && list.size() > 0) {
//					pisDevice.deviceInfor = list.get(0);
//					mHandler.sendEmptyMessage(MSG_GET_DEVICEINFOR_SUCCESS);
//				}
			}
		}
	}
	class LoadIcon implements Runnable {
		private String iconUrl;
		private ImageView imageView;
		Bitmap bit;

		public LoadIcon(ImageView imageView, String iconUrl) {
			this.iconUrl = iconUrl;
			this.imageView = imageView;
		}

		@Override
		public void run() {
			if (iconUrl != null) {
				bit = HttpUtils.getImage(DeviceInfoActivity.this,
						String.valueOf(iconUrl.hashCode()), iconUrl);
				if (bit != null) {
					int radius = (bit.getWidth() < bit.getHeight() ? bit
							.getWidth() : bit.getHeight()) / 2;
					bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
					handler.post(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(bit);
						}
					});
				}
			}
		}
	}
}
