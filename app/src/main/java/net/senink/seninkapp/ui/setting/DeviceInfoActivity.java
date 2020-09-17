package net.senink.seninkapp.ui.setting;

import java.nio.ByteOrder;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.util.Arrays;
import com.telink.sig.mesh.util.MeshUtils;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISUpdate;
//import com.senink.seninkapp.core.PipaRequest;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.ui.IconUtil;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
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
	private PISBase infor;
	private PISDevice pisDevice;
	private DeviceInfo mTelinkDevice;
	private boolean isTelink;
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
		isTelink = intent.getBooleanExtra(TelinkApiManager.IS_TELINK_KEY, false);
		if(isTelink){
			int address = intent.getIntExtra(MessageModel.TELINK_BASE_KEYSTR, 0);
			mTelinkDevice = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(address);
			if(mTelinkDevice == null){
				finish();
			}
		}else{
			infor = pm.getPISObject(intent.getStringExtra(MessageModel.PISBASE_KEYSTR));
			if(infor != null){
				pisDevice = infor.getDeviceObject();
			}
		}
	}

	private void setViews() {
		try {
			if(isTelink){
				if(mTelinkDevice != null ){
					device_icon.setImageResource(IconUtil.getTelinkDeviceIconResource(mTelinkDevice));
					int vid = mTelinkDevice.nodeInfo.cpsData.vid;
					byte[] vb = MeshUtils.integer2Bytes(vid, 2, ByteOrder.LITTLE_ENDIAN);

					int pid = mTelinkDevice.nodeInfo.cpsData.pid;
					byte[] pb = MeshUtils.integer2Bytes(pid, 2, ByteOrder.LITTLE_ENDIAN);

					device_name.setText(mTelinkDevice.getDeviceName());
					mac.setText(mTelinkDevice.macAddress);
					version.setText(Arrays.bytesToHexString(pb, ":"));
				}
			}
			else if (pisDevice != null && pisDevice.getMacString() != null) {
				Drawable sld = IconUtil.getPISIcon(DeviceInfoActivity.this, infor);
				device_icon.setImageDrawable(sld);
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
			break;
		case R.id.unbond:
			
			break;
		case R.id.position_area:

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

	Handler handler = new Handler();

}
