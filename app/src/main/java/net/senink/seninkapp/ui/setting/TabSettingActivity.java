package net.senink.seninkapp.ui.setting;

import java.io.File;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.HttpUserInfo;
import net.senink.piservice.http.JsonHttpRequest;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.struct.UserInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.BuildConfig;
import net.senink.seninkapp.Foreground;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
// TODO LEE 账号设置界面
public class TabSettingActivity extends BaseActivity implements OnClickListener {
	private TextView tvTitle;
	private ImageView ivTitle;
	private View userArea, seBeiArea, oneZhiNengArea,
			parentAccuArea, aboutArea, exitBt;
	private TextView user_name;
	public static UserInfo userInfo;
	private ImageView imageView;
	private Button btnVersion;
    private String userName = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_setting_activity);
		initView();
		setListener();
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		ivTitle = (ImageView) findViewById(R.id.title_logo);
		userArea = findViewById(R.id.user_info);
		seBeiArea = findViewById(R.id.se_bei_manage);
		oneZhiNengArea = findViewById(R.id.one_zhi_neng);
		parentAccuArea = findViewById(R.id.parent_accu);
		aboutArea = findViewById(R.id.about);
		exitBt = findViewById(R.id.bottom_exit);
		imageView = (ImageView) findViewById(R.id.user_icon);
		user_name = (TextView) findViewById(R.id.user_name);
		btnVersion = (Button) findViewById(R.id.about_btn);

		btnVersion.setText(BuildConfig.VERSION_NAME);
		tvTitle.setText(R.string.tab_setting_title);
		tvTitle.setVisibility(View.VISIBLE);
		ivTitle.setVisibility(View.VISIBLE);
		userName = SharePreferenceUtils.getInstance(this).getCurrentUser();

		user_name.setText(userName);
		userInfo = PISManager.getInstance().getUserObject();

		HttpUserInfo httpuInfo = new HttpUserInfo();
		HttpRequest req = httpuInfo.updateUserInfo();
		req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(HttpRequest req) {

			}

			@Override
			public void onRequestResult(HttpRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED){
					HttpUserInfo.userInfoHttpRequest uReq = (HttpUserInfo.userInfoHttpRequest)req;
					UserInfo uInfo = uReq.getUserInfo();
					if (uInfo.nickName != null && uInfo.nickName != "")
						user_name.setText(uInfo.nickName);
				}

			}
		});
		PISHttpManager.getInstance(TabSettingActivity.this).request(req);


//		if (userInfo != null && CacheManager.getHeadBitmap() != null) {
//			imageView.setImageBitmap(CacheManager.getHeadBitmap());
//		}else{
//			new Thread(runnable).start();
//		}
	}

	private void setListener() {
		imageView.setOnClickListener(this);
		userArea.setOnClickListener(this);
		seBeiArea.setOnClickListener(this);
		oneZhiNengArea.setOnClickListener(this);
		if (BuildConfig.ManufacturerID==0) {
			parentAccuArea.setOnClickListener(this);
		}
//		aboutArea.setOnClickListener(this);
		exitBt.setOnClickListener(this);
	}
	
	@Override
	public void onBackPressed() {
//		PISManager.getInstance().Stop();
		finish();
		moveTaskToBack(true);
//		overridePendingTransition(R.anim.anim_in_from_left,
//				R.anim.anim_out_to_right);
	}

	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			userInfo = HttpUtils.getUserInfoItem(TabSettingActivity.this,
					SharePreferenceUtils.getInstance(TabSettingActivity.this)
					.getCurrentUser());
			CacheManager.setUserInfor(userInfo);
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (userInfo != null) {
//						user_name.setText(userInfo.loginUser);
						new Thread(new LoadIcon(imageView, userInfo.loginUser,
								userInfo.nickSrc)).start();
					}
				}
			});

		}
	};

	class LoadIcon implements Runnable {
		private String iconUrl;
		private ImageView imageView;
		Bitmap bit;
		String iconName;

		public LoadIcon(ImageView imageView, String iconName, String iconUrl) {
			this.iconUrl = iconUrl;
			this.imageView = imageView;
			this.iconName = iconName;
		}

		@Override
		public void run() {
			if (iconUrl != null) {
				try {
					bit = HttpUtils.getImage(TabSettingActivity.this, iconName, iconUrl);
					int radius = (bit.getWidth() < bit.getHeight() ? bit
							.getWidth() : bit.getHeight()) / 2;
					bit = SDCardUtils.getCroppedRoundBitmap(bit, radius);
					handler.post(new Runnable() {

						@Override
						public void run() {
							imageView.setImageBitmap(bit);
						}
					});
					CacheManager.setHeadBitmap(bit);
				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}
		}

	}

	private Handler handler = new Handler();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_info:
			if (PISManager.getInstance().hasCloudConnect()) {
				startActivity(new Intent(this, UserInfoActivity.class));
				overridePendingTransition(R.anim.anim_in_from_right,
						R.anim.anim_out_to_left);
			}
			break;
		case R.id.se_bei_manage:
			startActivity(new Intent(this, DeviceListActivity.class));
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		case R.id.parent_accu:
			startActivity(new Intent(this, ParentAccuActivity.class));
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		case R.id.one_zhi_neng:
			break;
//		case R.id.about:
//			startActivity(new Intent(this, AboutActivity.class));
//			overridePendingTransition(R.anim.anim_in_from_right,
//					R.anim.anim_out_to_left);
//			break;
		case R.id.bottom_exit:
			exitAlert();
			break;
		case R.id.user_icon:
//			chooseIconDialog();
			break;
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	private void chooseIconDialog() {
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.icon_choose))
				.setItems(
						new String[] {
								getResources().getString(R.string.gallery),
								getResources().getString(R.string.camera) },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									gallery();
									break;
								case 1:
									camera();
									break;
								}
								dialog.dismiss();
							}
						}).show();
	}

	private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	/* 头像名称 */
	private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
	private File tempFile;

	/*
	 * 从相册获取
	 */
	public void gallery() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * 从相机获取
	 */
	public void camera() {
		// 激活相机
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			tempFile = new File(Environment.getExternalStorageDirectory(),
					PHOTO_FILE_NAME);
			// 从文件中创建uri
			Uri uri = Uri.fromFile(tempFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
		startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
	}

	/*
	 * 剪切图片
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);

		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/*
	 * 判断sdcard是否被挂载
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// Bitmap bitmap;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			// 从相册返回的数据
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}

		} else if (requestCode == PHOTO_REQUEST_CAREMA) {
			// 从相机返回的数据
			if (hasSdcard()) {
				crop(Uri.fromFile(tempFile));
			}
		} else if (requestCode == PHOTO_REQUEST_CUT) {
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				new Thread(new UpdateUserLogo(bitmap)).start();
			}
			try {
				// 将临时文件删除
				if (tempFile != null)
					tempFile.delete();
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	class UpdateUserLogo implements Runnable {
		Bitmap bit = null;

		public UpdateUserLogo(Bitmap bit) {
			this.bit = bit;
		}

		@Override
		public void run() {
			try {
				final boolean flag = HttpUtils.uploadUserLogo(
						TabSettingActivity.this, bit);

				if (flag) {
					try {
						if (userInfo != null) {
							userName = userInfo.loginUser;
						}
						File file = new File(getFilesDir() + "/"+ userName);
						if (file.exists()) {
							file.delete();
						}
					} catch (Exception e) {
						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
					}
					runnable.run();
				}
				if (bit != null && !bit.isRecycled()) {
					bit.recycle();
				}
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}

		}
	};

	public void exitAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(
				new String[] {
						getResources().getString(R.string.only_exit),
						getResources()
								.getString(R.string.exit_and_clear_record) },
				new DialogInterface.OnClickListener() {

					@SuppressWarnings("static-access")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// SharePreferenceUtils.getInstance(
						// TabSettingActivity.this).putChecked(false);
						switch (which) {
						case 0:
							/**仅仅去除当前用户指示，同时保存当前用户信息*/
							PISManager.SavePISManagerObject(PISManager.getInstance(),
									SharePreferenceUtils.getUserDataPath(TabSettingActivity.this,
											SharePreferenceUtils.FILENAME_PISMANAGER,
											PISManager.getInstance().getUserObject().loginUser));
							PISManager.getInstance().release();

							SharePreferenceUtils.getInstance(TabSettingActivity.this).unsetCurrentUser();
//							SharePreferenceUtils.setBackGroundRun(TabSettingActivity.this, false);
							Foreground.exitToActivity(TabSettingActivity.this, HomeActivity.class);
							break;
						case 1:
							/**清除当前账号的所有信息*/
							SharePreferenceUtils.getInstance(TabSettingActivity.this).removeUser(
									PISManager.getInstance().getUserObject().loginUser);
							PISManager.getInstance().release();

							Foreground.exitToActivity(TabSettingActivity.this, HomeActivity.class);
							break;
						}
					}

				});

		builder.create().show();

	}

}
