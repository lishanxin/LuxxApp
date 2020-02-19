package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.SceneGridViewAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISLightRGB;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
import net.senink.seninkapp.sqlite.SceneDao;
import net.senink.seninkapp.ui.cache.CacheManager;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.gridview.DynamicGridView;
import net.senink.seninkapp.ui.util.RGBConfigUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;

/**
 * 设置场景模式的界面
 * 
 * @author zhaojunfeng
 * @data 2015-11-24
 * 
 */

public class SceneSettingActivity extends BaseActivity implements
		OnClickListener {
	private final static int MSG_TIME_OUT = 1;
	private final static int LOADING_MAX_TIME = 30000;
	private TextView tvTitle;
	private Button backBtn;
	private ImageButton addBtn;
	private CacheManager cacheManager;
	private DynamicGridView gridView;
	private SceneGridViewAdapter adapter;
	private TextView tvTip;
	private SceneDao mSceneDao;
	/**
	 * t1和t2与pisbase对象的mT1和mT2保持一致
	 */
	private int t1 = 0;
	private int t2 = 0;
	private PISXinLight inforOnLED;
	private PISxinColor inforOnRGB;
//	private boolean isLED;
//	private boolean isGroup;
	// 组id
//	private short groupId = -1;
	// 被选中的下标
	private int selectedIndex;
	private PISManager manger;
	private boolean isLoading = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_TIME_OUT:
				stopAnima(false);
				isLoading = false;
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scenesetting);
		cacheManager = CacheManager.getInstance();
		manger = PISManager.getInstance();
		mSceneDao = new SceneDao(this);
		initView();
		setData();
		setListener();
	}

	private void setData() {
		Intent intent = getIntent();
		if (intent == null)
			return;
		String key = intent.getStringExtra(MessageModel.ACTIVITY_VALUE);
		PISBase infor = PISManager.getInstance().getPISObject(key);
		if (infor == null)
			return;
		this.t1 = infor.getT1();
		this.t2 = infor.getT2();
		if (infor instanceof PISxinColor){
			inforOnRGB = (PISxinColor)infor;
		}
		else if (infor instanceof PISXinLight){
			inforOnLED = (PISXinLight)infor;
		}
	}

	private void stopAnima(boolean success) {
		if (adapter.getCount() > selectedIndex && selectedIndex >= 0) {
			View view = gridView.getChildAt(selectedIndex);
			View ivLoading = view.findViewById(R.id.scene_item_loadding);
			if (ivLoading != null) {
				AnimationDrawable anima = (AnimationDrawable) ivLoading
						.getBackground();
				anima.stop();
				ivLoading.setVisibility(View.GONE);
			}
			if (success) {
				setVisiabilityOnSelected();
			}
		}
	}

	private void setVisiabilityOnSelected() {
		View view = gridView.getChildAt(selectedIndex);
		View ivSelected = view.findViewById(R.id.scene_item_selected);
		if (ivSelected != null) {
			ivSelected.setVisibility(View.VISIBLE);
		}
		List<Object> objs = adapter.getItems();
		int size = objs.size();
		for (int i = 0; i < size; i++) {
			SceneBean bean = (SceneBean) objs.get(i);
			if (i == selectedIndex) {
				bean.selected = true;
			} else {
				if (bean.selected) {
					view = gridView.getChildAt(i);
					ivSelected = view.findViewById(R.id.scene_item_selected);
					if (ivSelected != null) {
						ivSelected.setVisibility(View.GONE);
					}
				}
				bean.selected = false;
			}
		}
	}

	/**
	 * 发送RGB命令
	 */
	private void sendRGBOrder(int currentWhite, int currentColor) {
		if (inforOnRGB == null)
			return;
		int[] colors = null;
		if (currentWhite >= 255) {
			colors = RGBConfigUtils.getRGBFromWhite(currentWhite);
			currentWhite = 255;
			currentColor = 0;
		} else {
			if (currentWhite == 0 && currentColor == 0) {
				currentWhite = 2;
			}
			colors = Utils.getRGB(currentColor);
		}
		PipaRequest req = inforOnRGB.commitLightColor(colors[0], colors[1], colors[2], currentWhite, true);
		req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(PipaRequest req) {

			}

			@Override
			public void onRequestResult(PipaRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
					stopAnima(true);
				} else {
					stopAnima(false);
				}
				mHandler.removeMessages(MSG_TIME_OUT);
				isLoading = false;
			}
		});
		req.NeedAck = true;
		inforOnRGB.request(req);

	}

	/**
	 * 发送冷暖色命令
	 */
	private void sendColdWarmOrder(int white, int progress) {
		if (inforOnLED == null)
			return;
		if (white < 2) {
			white = 2;
		}
		int warm = white * progress / 255;
		int cold = white - warm;

		PipaRequest req = inforOnLED.commitLightCW(cold, warm, true);
		req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
			@Override
			public void onRequestStart(PipaRequest req) {

			}

			@Override
			public void onRequestResult(PipaRequest req) {
				if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
					stopAnima(true);
				} else {
					stopAnima(false);
				}
				mHandler.removeMessages(MSG_TIME_OUT);
				isLoading = false;
			}
		});
		req.NeedAck = true;
		inforOnLED.request(req);
	}

	private void setListener() {
		addBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				gridView.startEditMode(position);
				return true;
			}
		});

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!isLoading) {
					isLoading = true;
					selectedIndex = position;
					SceneBean bean = (SceneBean) adapter.getItem(position);
					View ivLoading = view.findViewById(R.id.scene_item_loadding);
					View ivSelected = view.findViewById(R.id.scene_item_selected);
					if (ivSelected != null) {
						ivSelected.setVisibility(View.GONE);
					}
					if (ivLoading != null) {
						ivLoading.setVisibility(View.VISIBLE);
						AnimationDrawable anima = (AnimationDrawable) ivLoading
								.getBackground();
						anima.start();
					}
					if (inforOnLED != null) {
						sendColdWarmOrder(bean.bright, bean.coldwarm);
					}
					else if (inforOnRGB != null){
						sendRGBOrder(bean.bright, bean.rgb);
					}
					mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, LOADING_MAX_TIME);
				}
			}
		});
		gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
			@Override
			public void onDragStarted(int position) {

			}

			@Override
			public void onDragPositionsChanged(int oldPosition, int newPosition) {
				SceneBean oldBean = (SceneBean) adapter.getItem(oldPosition);
				SceneBean newBean = (SceneBean) adapter.getItem(newPosition);
				if (oldPosition < 4 && newPosition >= 4) {
					newBean.used = 0;
					oldBean.used = 1;
					long result = mSceneDao.upgradeScene(newBean);
					if (result > 0) {
						cacheManager.addScene(newBean);
					}
					result = mSceneDao.upgradeScene(oldBean);
					if (result > 0) {
						cacheManager.addScene(oldBean);
					}
				} else if (oldPosition >= 4 && newPosition < 4) {
					newBean.used = 1;
					oldBean.used = 0;
					long result = mSceneDao.upgradeScene(newBean);
					if (result > 0) {
						cacheManager.addScene(newBean);
					}
					result = mSceneDao.upgradeScene(oldBean);
					if (result > 0) {
						cacheManager.addScene(oldBean);
					}
				}
			}
		});
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title_name);
		backBtn = (Button) findViewById(R.id.title_back);
		addBtn = (ImageButton) findViewById(R.id.title_add);
		gridView = (DynamicGridView) findViewById(R.id.scenesetting_gridview);
		tvTip = (TextView) findViewById(R.id.scenesetting_tip);
		backBtn.setVisibility(View.VISIBLE);
		addBtn.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.allscenes);
	}

	@Override
	public void onBackPressed() {
		if (gridView.isEditMode()) {
			gridView.stopEditMode();
		} else {
			backBtn.performClick();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			refreshScenes();
			isLoading = false;
			finish();
			overridePendingTransition(R.anim.anim_in_from_left,
					R.anim.anim_out_to_right);
			break;

		case R.id.title_add:
			if (adapter.getCount() >= 12) {
				ToastUtils.showToast(this, R.string.scene_setting_out);
				return;
			}
			Intent intent = new Intent(this, SceneDetailActivity.class);
			intent.putExtra("t1", t1);
			intent.putExtra("t2", t2);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_in_from_right,
					R.anim.anim_out_to_left);
			break;
		}
	}

	private void refreshScenes() {
		List<Object> list = adapter.getItems();
		if (list.size() > 0) {
			int size = list.size();
			for (int i = 0; i < size; i++) {
				SceneBean bean = (SceneBean) list.get(i);
				bean.used = i;
				mSceneDao.upgradeScene(bean);
			}
			cacheManager.setScenes(mSceneDao.getAllScenes(SharePreferenceUtils
					.getInstance(this).getCurrentUser()));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshGridView();
//		manger.setOnPipaRequestStatusListener(this);
	}

	@Override
	protected void onPause() {
//		manger.setOnPipaRequestStatusListener(null);
		super.onPause();
	}

	private void refreshGridView() {
		List<SceneBean> beans = null;
		if (inforOnRGB != null) {
			beans = cacheManager.getRGBScenes();
		}
		else if (inforOnLED != null) {
			beans = cacheManager.getLEDScenes();
		}
		if (null == beans) {
			beans = new ArrayList<SceneBean>();
		}
		adapter = new SceneGridViewAdapter(this, beans, tvTip, 3);
		gridView.setAdapter(adapter);
	}
}
