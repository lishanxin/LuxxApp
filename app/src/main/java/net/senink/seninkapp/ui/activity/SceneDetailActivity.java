package net.senink.seninkapp.ui.activity;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISConstantDefine;
import net.senink.seninkapp.sqlite.SceneDao;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.PictureUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.ColorCircle;

/**
 * 场景详情界面
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 */

public class SceneDetailActivity extends BaseActivity implements
		OnClickListener {
	// 按钮可点击
	private static final int MSG_BTN_ENABLE = 1;
	// 按钮可点击
	private static final int MSG_SAVE_SUCCESS = 2;
	// 按钮可点击
	private static final int MSG_SAVE_PICTURE_SUCCESS = 3;
	// 拍照
	private static final int PHOTO_REQUEST_CAREMA = 1;
	// 从相册中选择
	private static final int PHOTO_REQUEST_GALLERY = 2;
	// 剪切图片
	private static final int PHOTO_REQUEST_CUT = 3;
	private Button backBtn, saveBtn;
	private ImageButton cameraBtn, selectPictureBtn;
	private View titleLayout;
	// 背景布局
	private RelativeLayout titleBgLayout;
	// 冷暖色seekbar所在的布局
	private RelativeLayout coldWarmLayout;
	private TextView tvTitle;
	private SeekBar whiteBar, coldWarmBar;
	// 白光滚动轴左边的图片
	private ImageView ivLeftOnWhite;
	// 白光滚动轴右边的图片
	private ImageView ivRightOnWhite;
	// 冷暖色滚动轴左边的图片
	private ImageView ivLeftOnColdOrWarm;
	// 冷暖色滚动轴右边的图片
	private ImageView ivRightOnColdOrWarm;
	// 选色板
	private ColorCircle colorCircle;
	private SceneDao mSceneDao;
	private EditText etSceneName;
	// 选择图片的url
	private String picturePath;
	// 缓存管理器
	private CacheManager cacheManager;
	private static final String PICTURE_NAME = "temp_photo.jpg";
	// 选择图片的文件
	private File tempFile;
	// 传值
	private SceneBean infor;
	/**
	 * t1和t2与pisbase对象的mT1和mT2保持一致
	 */
	private int t1 = 0;
	private int t2 = 0;
	// 当前RGB的颜色值
	private int currentColor = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_BTN_ENABLE:
				saveBtn.setEnabled(true);
				break;

			case MSG_SAVE_SUCCESS:
				ToastUtils.showToast(SceneDetailActivity.this,
						R.string.scene_detail_save_success);
				removeMessages(MSG_BTN_ENABLE);
				setResult(RESULT_OK);
				backBtn.performClick();
				break;
			case MSG_SAVE_PICTURE_SUCCESS:
				setPicture(picturePath);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenedetail);
		mSceneDao = new SceneDao(this);
		cacheManager = CacheManager.getInstance();
		initView();
		setData();
		setView();
		setListener();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void setView() {
		if (infor != null) {
			if (!TextUtils.isEmpty(infor.sceneName)) {
				setDrawableOnScene(false);
			}
			etSceneName.setText(infor.sceneName == null ? "" : infor.sceneName);
			whiteBar.setProgress(infor.bright);
			if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
				coldWarmBar.setProgress(infor.coldwarm);
			} else if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
				currentColor = infor.rgb;
				coldWarmBar.setProgress(0);
			}
		}else{
			setDrawableOnScene(true);
		}
		setPicture(picturePath);
		if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
				&& t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
			colorCircle.setOnTouchListener(new OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setDrawableOnSceneName(true);
					etSceneName.setCursorVisible(false);
					int action = event.getAction();
					whiteBar.setProgress(0);
					if (action == MotionEvent.ACTION_DOWN
							|| action == MotionEvent.ACTION_MOVE) {
						int x = (int) event.getX();
						int y = (int) event.getY();
						ColorCircle circleView = (ColorCircle) v;
						int pixelColor = 0;
						try {
							pixelColor = circleView.getPixelColorAt(x, y);
						} catch (IndexOutOfBoundsException e) {
							return true;
						}
						if (Color.TRANSPARENT != pixelColor) {
							currentColor = pixelColor;
						}
						if (Color.alpha(pixelColor) < 0xFF) {
							return true;
						}
						circleView.setCursor(x, y);
						circleView.invalidate();
						return true;
					} else if (action == MotionEvent.ACTION_UP) {
						return true;
					} else {
						return false;
					}
				}
			});
			coldWarmLayout.setVisibility(View.GONE);
		}else if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
				&& t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
			coldWarmLayout.setVisibility(View.VISIBLE);
		}
	}

	@SuppressWarnings({ "deprecation" })
	private void setPicture(String path) {
		int width = titleBgLayout.getWidth();
		int height = titleBgLayout.getHeight();
		Bitmap bm = PictureUtils.getBitmap(path, width, height);
		if (!TextUtils.isEmpty(path) && new File(path).exists()) {
			bm = PictureUtils.getBitmap(path, width, height);
		}
		if (bm != null) {
			try {
				titleBgLayout.setBackground(new BitmapDrawable(getResources(),
						bm));
			} catch (Exception e) {
				titleBgLayout.setBackgroundDrawable(new BitmapDrawable(
						getResources(), bm));
			}
		} else {
			titleBgLayout.setBackgroundResource(PictureUtils.getDefaultPicture(path));
		}
	}

	private void setData() {
		if (getIntent() != null) {
			Intent intent = getIntent();
			if (intent.hasExtra("scene")) {
				infor = (SceneBean) intent.getSerializableExtra("scene");
				picturePath = infor.picture;
				t1 = infor.t1;
				t2 = infor.t2;
			}else{
				t1 = intent.getIntExtra("t1", 0);
				t2 = intent.getIntExtra("t2", 0);
			}
		}
	}

	private void setListener() {
		backBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		selectPictureBtn.setOnClickListener(this);
		cameraBtn.setOnClickListener(this);
		etSceneName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				etSceneName.setCursorVisible(true);
//				etSceneName.setText("");
			}
		});
        etSceneName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setDrawableOnScene(false);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (etSceneName.getText().length() == 0) {
					setDrawableOnScene(true);
				}
			}
		});		
		whiteBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				setDrawableOnSceneName(true);
				etSceneName.setCursorVisible(false);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setLeftOrRightIcon(progress);
			}
		});
		coldWarmBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				setDrawableOnSceneName(true);
				etSceneName.setCursorVisible(false);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setLeftOrRightOnWarmOrCold(progress);
			}
		});
	}

	private void setDrawableOnSceneName(boolean visiable){
		if (visiable) {
			Drawable icon = getResources().getDrawable(R.drawable.icon_write);
			icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
			etSceneName.setCompoundDrawables(null, null, icon, null);
			
		} else {
           etSceneName.setCompoundDrawables(null, null, null, null);
		}
	}
	
	private void setDrawableOnScene(boolean isBig){
		Drawable icon = null;
		if (isBig) {
			icon = getResources().getDrawable(R.drawable.icon_write_big);
		} else {
			icon = getResources().getDrawable(R.drawable.icon_write);
		}
		icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
		etSceneName.setCompoundDrawables(null, null, icon, null);
	}
	
	/**
	 * 设置白光滚动轴左边和右边的图片
	 * 
	 * @param progress
	 */
	private void setLeftOrRightIcon(int progress) {
		int max = whiteBar.getMax();
		if (progress >= (3 * max / 4)) {
			ivRightOnWhite.setBackgroundResource(R.drawable.icon_bright1);
		} else if ((progress > max / 2) && (progress < (3 * max / 4))) {
			ivRightOnWhite.setBackgroundResource(R.drawable.icon_bright1);
		} else if ((progress < max / 2) && (progress >= max / 4)) {
			ivLeftOnWhite.setBackgroundResource(R.drawable.icon_dark1);
		} else {
			ivLeftOnWhite.setBackgroundResource(R.drawable.icon_dark1);
		}
	}

	/**
	 * 设置冷暖色滚动轴左边和右边的图片
	 * 
	 * @param progress
	 */
	private void setLeftOrRightOnWarmOrCold(int progress) {
		int max = coldWarmBar.getMax();
		if (progress >= (3 * max / 4)) {
			ivRightOnColdOrWarm.setBackgroundResource(R.drawable.icon_warm1);
		} else if ((progress > max / 2) && (progress < (3 * max / 4))) {
			ivRightOnColdOrWarm.setBackgroundResource(R.drawable.icon_warm0);
		} else if ((progress < max / 2) && (progress >= max / 4)) {
			ivLeftOnColdOrWarm.setBackgroundResource(R.drawable.icon_cold0);
		} else {
			ivLeftOnColdOrWarm.setBackgroundResource(R.drawable.icon_cold1);
		}
	}

	private void initView() {
		backBtn = (Button) findViewById(R.id.title_back);
		saveBtn = (Button) findViewById(R.id.title_finished);
		tvTitle = (TextView) findViewById(R.id.title_name);
		saveBtn = (Button) findViewById(R.id.title_finished);
		titleLayout = findViewById(R.id.scenedetail_title);
		cameraBtn = (ImageButton) findViewById(R.id.scenedetail_camera);
		selectPictureBtn = (ImageButton) findViewById(R.id.scenedetail_selectpicture);
		whiteBar = (SeekBar) findViewById(R.id.scenedetail_bright_seekbar);
		coldWarmBar = (SeekBar) findViewById(R.id.scenedetail_coldwarm_seekbar);
		etSceneName = (EditText) findViewById(R.id.scenedetail_scenename);
		ivLeftOnWhite = (ImageView) findViewById(R.id.scenedetail_bright_left);
		ivRightOnWhite = (ImageView) findViewById(R.id.scenedetail_bright_right);
		ivLeftOnColdOrWarm = (ImageView) findViewById(R.id.scenedetail_coldwarm_left);
		ivRightOnColdOrWarm = (ImageView) findViewById(R.id.scenedetail_coldwarm_right);
		titleBgLayout = (RelativeLayout) findViewById(R.id.scenedetail_title_layout);
		coldWarmLayout = (RelativeLayout) findViewById(R.id.scenedetail_coldwarm_layout);
		colorCircle = (ColorCircle) findViewById(R.id.scenedetail_colorcircle);
		titleLayout.setBackgroundResource(R.drawable.icon_scene_top);
		tvTitle.setText(R.string.scene);
		saveBtn.setText(R.string.save);
		saveBtn.setBackgroundColor(Color.TRANSPARENT);
		backBtn.setVisibility(View.VISIBLE);
		saveBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		backBtn.performClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;
		case R.id.title_finished:
			save();
			break;
		case R.id.scenedetail_camera:
			camera();
			break;
		case R.id.scenedetail_selectpicture:
			selectPicture();
			break;
		}
	}

	/*
	 * 从相册获取
	 */
	public void selectPicture() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * 从相机获取
	 */
	public void camera() {
		// 激活相机
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (SDCardUtils.hasSdcard()) {
			tempFile = new File(Environment.getExternalStorageDirectory(),
					PICTURE_NAME);
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
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 500);
		intent.putExtra("outputY", 250);

		intent.putExtra("outputFormat", "JPEG");// 图片格式
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/**
	 * 保存场景
	 */
	private void save() {
		int coldWarmValue = coldWarmBar.getProgress();
		int bright = whiteBar.getProgress();
		String sceneName = etSceneName.getText().toString();
		if (TextUtils.isEmpty(sceneName)) {
			ToastUtils.showToast(this, R.string.scene_detail_name_isnull);
		} else if (coldWarmValue == 0 && bright == 0 
				&& t1 == PISConstantDefine.PIS_MAJOR_LIGHT
				&& t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
			ToastUtils.showToast(this, R.string.scene_detail_zero);
		} else if (currentColor == 0 && bright == 0 
				&& t1 == PISConstantDefine.PIS_MAJOR_LIGHT
				&& t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
			ToastUtils.showToast(this, R.string.scene_detail_rgb_zero);
		}else {
			sceneName = sceneName.trim();
			saveBtn.setEnabled(false);
			mHandler.sendEmptyMessageDelayed(MSG_BTN_ENABLE, 2000);
			if (null == infor) {
				infor = new SceneBean();
				infor.user = SharePreferenceUtils.getInstance(this)
						.getCurrentUser();
				infor.t1 = t1;
				infor.t2 = t2;
			}
			infor.sceneName = sceneName;
			if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& t2 == PISConstantDefine.PIS_LIGHT_LIGHT) {
				infor.coldwarm = coldWarmValue;
				infor.rgb = 0;
			} else if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
				infor.coldwarm = 0;
				infor.rgb = currentColor;
			}
			infor.bright = bright;
			//缓存不使用图片的路径
			String tempPath = null;
			if (!TextUtils.isEmpty(infor.picture) && ((!TextUtils.isEmpty(picturePath) && !picturePath.equals(infor.picture)) || TextUtils.isEmpty(picturePath))) {
				tempPath = new String(infor.picture);
			}
			infor.picture = picturePath;
			long result = 0;
			if (mSceneDao.exist(infor.id)) {
				result = mSceneDao.upgradeScene(infor);
			} else {
				result = mSceneDao.insertScene(infor);
				infor.id = (int) result;
			}
			if (result > 0) {
				//删除不使用的图片
				if(!TextUtils.isEmpty(tempPath) && new File(tempPath).exists()){
					new File(tempPath).delete();
				}
				cacheManager.addScene(infor);
				mHandler.sendEmptyMessage(MSG_SAVE_SUCCESS);
			} else {
				ToastUtils.showToast(this, R.string.scene_detail_save_failed);
			}
		}
	}

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
			if (SDCardUtils.hasSdcard()) {
				crop(Uri.fromFile(tempFile));
			}
		} else if (requestCode == PHOTO_REQUEST_CUT) {
			LogUtils.i("cut", "============cut ==============");
			// 从剪切图片返回的数据
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				LogUtils.i("cut", "============cut ==========bitmap = "
						+ (bitmap == null));
				new Thread(new savePictureThread(bitmap)).start();
			}
			try {
				// 将临时文件删除
				tempFile.delete();
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class savePictureThread implements Runnable {
		private Bitmap bm;

		public savePictureThread(Bitmap bm) {
			this.bm = bm;
		}

		@Override
		public void run() {
			String name = "";
			if (t1 == PISConstantDefine.PIS_MAJOR_LIGHT
					&& t2 == PISConstantDefine.PIS_LIGHT_COLOR) {
				name = "rgb_" + System.currentTimeMillis() + ".png";
			} else {
				name = "led_" + System.currentTimeMillis() + ".png";
			}
			picturePath = PictureUtils.saveBitmap(bm, name);
			LogUtils.i("cut", "============cut ==========picturePath = "
					+ picturePath);
			if (!TextUtils.isEmpty(picturePath)) {
				mHandler.sendEmptyMessage(MSG_SAVE_PICTURE_SUCCESS);
			}
			bm.recycle();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(MSG_SAVE_SUCCESS);
		mHandler.removeMessages(MSG_BTN_ENABLE);
//		try {
//			BitmapDrawable bd = (BitmapDrawable) titleBgLayout.getBackground();
//			bd.getBitmap().recycle();
//		} catch (Exception e) {
//            net.senink.seninkapp
//		}
	}
}
