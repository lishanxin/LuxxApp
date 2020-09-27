package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.CommonMeshCommand;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.model.SigMeshModel;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.LuxxMusicColor;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.sqlite.SceneDao;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.telink.model.TelinkOperation;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.RGBConfigUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.ColorCircle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 用设置灯或者灯组的界面 （在主界面直接进入此界面）
 *
 * @author zhaojunfeng
 * @date 2015-07-15
 */
// TODO LEE 灯控界面
public class LightRGBDetailActivity extends BaseActivity implements
        View.OnClickListener {
    public static final String LAYOUT_RESOURCEID = "rgblight_layout";
    private static final String TAG = "LightRGBDetailActivity";
    protected static final int CODE_FOR_WRITE_PERMISSION = 10;
    // 界面跳转时的请求码
    protected final static int REQUEST_CODE_SETTING = 2000;
    protected final static int REQUEST_CODE_EFFECT = 2001;
    protected final static int REQUEST_CODE_TIMER = 2002;
    // 开关命令超时
    protected final static int MSG_SEND_CANDLE = 3;
    protected final static int MSG_SWITCH_TIMEOUT = 2;
    protected final static int MSG_TIME_OUT = 1;
    protected final static int LOADING_MAX_TIME = 10000;
    // 界面跳转到场景模式设置界面时的请求码
    public final static int REQUEST_SCENE_CODE = 2001;
    boolean isVoiceOn = false;
    public int gpx = 0;
    // 延迟发送冷暖色命令
    protected final static int DELEY_TIME = 300;
    // 发送第一条冷暖色命令的起始时间
    protected long START_TIME = 0;
    // 标题名称
    protected TextView tvTitle;
    // 标题图片
    protected ImageView ivTitle;
    // 标题的中的返回按钮
    protected Button backBtn;
    // 开关状态
    protected CheckBox switcher;
    // 亮度调节器
    protected SeekBar whiteBar;
    // 冷暖度的布局
//    protected RelativeLayout ledLayout;
    // 颜色圈
    protected ColorCircle colorCircle;
    // 当前颜色显示的组件
    protected TextView tvCurrentColor;
    protected TextView test;
    // 白光滚动轴左边的图片
    protected ImageView ivLeftOnBright;
    // 白光滚动轴右边的图片
    protected ImageView ivRightOnBright;
    //	private ImageButton moreBtn;
    protected LinearLayout colorSelectorLayout;
    protected LinearLayout scene1Layout, scene2Layout, scene3Layout, scene4Layout, scene5Layout;
    protected LinearLayout candle_light;
    protected TextView tvSceneName1, tvSceneName2, tvSceneName3, tvSceneName4, tvSceneName5;
    protected ImageView ivSelected1, ivSelected2, ivSelected3, ivSelected4, ivSelected5;
    protected ImageView ivLoadding1, ivLoadding2, ivLoadding3, ivLoadding4, ivLoadding5;
    protected AnimationDrawable anima1, anima2, anima3, anima4, anima5;

    // 当前颜色值
    protected int currentColor = 0;
    // 传递过来的pisbase对象
    protected PISxinColor infor;
    protected PISxinColor inforX;
    // 某个组的信息
    protected PISBase groupInfor = null;
    // 设置按钮
    protected Button settingBtn;
    // 保存按钮
    protected Button saveBtn;
    // 特效按钮
    protected Button effectsBtn;
    // 切换按钮
    protected Button rgbwBtn;
    // 加载框
    protected ImageView ivLoading;
    // 加载动画
    protected AnimationDrawable anima;
    // 是否是分组的
    protected boolean isGroup = false;
    // 组id
    protected short groupId = -1;
    protected PISManager manager;
    // 是否是第一次进入该界面
//    protected boolean isFirst = true;
    // 开关状态改变是否发送命令
//    protected boolean isSended = true;
    // 蓝牙管理器
    protected MeshController controller;
    // 是否是定时器界面跳转过来
//    protected boolean isTimer;
    // 需要提交定时器的命令字
//    protected short cmd;
    // 需要提交定时器的命令内容
//    protected byte[] cmdContent;
    // 最近一次保存的命令字
//    protected short tempCmd;
    // 最近一次保存的内容
//    protected byte[] tempCmdContent;
    // 缓存管理器
//    protected CacheManager cacheManager;
    // 常用的几种场景模式
    protected List<SceneBean> scenes;
    // 存放场景模式的dao类
    protected SceneDao mSceneDao;
    // 被选中的中场景模式
    protected int selectedIndex = -1;
    // 统计开关命令ack返回的数量
    protected int countOnStatus = 0;
    // 统计设置颜色命令ack返回的数量
    protected int countOnRGBW = 0;
    //传值的pisbase对象的piskeystring字符串
//    protected String key = null;
    boolean candle_onoff = false;

    private boolean isTelink = false;
    private boolean isTelinkGroup = false;
    private int telinkAddress = 0;
    private Group telinkGroup;
    private DeviceInfo deviceInfo;
    //    private SparseBooleanArray lumEleInfo;
//    private SparseBooleanArray tempEleInfo;
    private int hslEleAdr;
//    private List<Integer> onOffEleAdrList;

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SWITCH_TIMEOUT:
                    hideLoading();
                    ToastUtils.showToast(LightRGBDetailActivity.this,
                            R.string.switch_set_failed);
                    break;
                case MessageModel.MSG_SEND_ORDER:
                    sendRGBOrder(false);
                    break;
                case MessageModel.MSG_SEND_CANDLE:
                    setCandle(candle_onoff);
                    break;
                case MSG_TIME_OUT:
//				if (!isGroup
//						|| (isGroup && (groupInfor != null
//						&& ((groupInfor.infors != null
//						&& groupInfor.infors.size() > countOnStatus))
//						|| (null == groupInfor.infors || groupInfor.infors.size() == 0)))) {
                    stopLoadding(false);
                    setEnable(true);
//				}
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResid = 0;
        if (savedInstanceState == null || savedInstanceState.getInt(LAYOUT_RESOURCEID) == 0)
            layoutResid = R.layout.activity_lightrgbdetail;
        else {
            layoutResid = savedInstanceState.getInt(LAYOUT_RESOURCEID);
        }
        setContentView(layoutResid);
        manager = PISManager.getInstance();
        controller = MeshController.getInstance(this);
//		cacheManager = CacheManager.getInstance();
//		mSceneDao = new SceneDao(this);
        initView();
        setData();
        setView();
        setListener();
        if (isTelinkGroup) {
            // TODO LEE 灯组的初始化
            initTelinkView();
        } else if (isTelink) {
            getNodeStatus();
            initTelinkView();
        }
    }

    /**
     * 获取传值
     */
    private void setData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String key = intent.getStringExtra("keystring");
            // TODO LEE 新增sdk
            if (bundle != null) {
                isTelink = bundle.getBoolean(TelinkApiManager.IS_TELINK_KEY, false);
                isTelinkGroup = bundle.getBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, false);
                telinkAddress = bundle.getInt(TelinkApiManager.TELINK_ADDRESS, 0);
                if (!isTelinkGroup && isTelink) {
                    deviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
                    if (deviceInfo == null) finish();
                    hslEleAdr = deviceInfo.getTargetEleAdr(SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId);
                } else if (isTelinkGroup) {
                    telinkGroup = TelinkGroupApiManager.getInstance().getGroupByAddress(telinkAddress);
                    if (telinkGroup != null) {
                        key = telinkGroup.PISKeyString;
                        PISBase tempInfor = manager.getPISObject(key);
                        hslEleAdr = telinkGroup.address;
                        if (tempInfor == null) {
                            key = null;
                            finish();
                        }
                    }
                }
            }

            activityMode = intent.getIntExtra(MessageModel.ACTIVITY_MODE, 0);

            setPisData(key);

        }
    }

    private void setPisData(String key) {
        if (key != null) {
            infor = (PISxinColor) manager.getPISObject(key);
            // NextApp.tw
            if (infor.getServiceId() != 0) {
                if (infor.getT1() == 0x10 && infor.getT2() == 0x05) {
                    candle_light.setVisibility(View.VISIBLE);

                } else {

                    candle_light.setVisibility(View.GONE);
                }
            } else {
                if (infor.getGroupObjects().size() > 0) {
                    if (infor.getGroupObjects().get(0).getT1() == 0x10 && infor.getGroupObjects().get(0).getT2() == 0x05) {
                        candle_light.setVisibility(View.VISIBLE);

                    } else {

                        candle_light.setVisibility(View.GONE);
                    }
                } else {
                    candle_light.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 发送RGB命令
     *
     * @param isScened
     * 是否是场景
     */
    private static final int LIGHT_MODE_RGB = 0;
    private static final int LIGHT_MODE_WHITE = 1;
    private static final int LIGHT_MODE_MIXED = 2;
    private int mCurrentRGBWMode = LIGHT_MODE_WHITE;

    private void sendRGBOrder(boolean isScened) {
        countOnRGBW = 0;
        if (infor == null && !isTelink) {
//            isFirst = false;
            return;
        }
        if (!switcher.isChecked()) {
            switcher.setChecked(true);
        }
        if (mCurrentRGBWMode == LIGHT_MODE_WHITE) {
            rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal_nocolor);
        } else {
            rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal);
        }
        float currentWhite = whiteBar.getProgress();
        int[] colors = Utils.getRGB(currentColor);

        if (currentWhite < RGBConfigUtils.MAX_VALUE / 20)
            currentWhite = RGBConfigUtils.MAX_VALUE / 20;
        colors[0] = (int) (colors[0] * (currentWhite / RGBConfigUtils.MAX_VALUE));
        colors[1] = (int) (colors[1] * (currentWhite / RGBConfigUtils.MAX_VALUE));
        colors[2] = (int) (colors[2] * (currentWhite / RGBConfigUtils.MAX_VALUE));

        int tempColor = Color.rgb(colors[0], colors[1], colors[2]);
        tvCurrentColor.setBackgroundColor(tempColor);
        if (isTelink) {
            if (isTelinkGroup && infor != null) {
                setPisColor(colors, isScened, currentWhite);
            }
            if (mCurrentRGBWMode == LIGHT_MODE_WHITE) {
                TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, CommonMeshCommand.getYellowCommand((currentWhite / RGBConfigUtils.MAX_VALUE)));
            } else {
                TelinkApiManager.getInstance().setDevicesColor(hslEleAdr, colors);
            }
        } else {
            setPisColor(colors, isScened, currentWhite);
        }

    }

    private void setPisColor(int[] colorsSource, boolean isScened, float currentWhiteSource) {
        float currentWhite = currentWhiteSource;
        int[] colors = new int[3];
        colors[0] = colorsSource[0];
        colors[1] = colorsSource[1];
        colors[2] = colorsSource[2];
        switch (mCurrentRGBWMode) {
            case LIGHT_MODE_RGB:
                currentWhite = 0;
                break;
            case LIGHT_MODE_WHITE:
                colors[0] = 0;
                colors[1] = 0;
                colors[2] = 0;

                break;
        }
        PipaRequest req = infor.commitLightColor(colors[0], colors[1], colors[2],
                (int) currentWhite, true);
        if (isScened) {
            req.setRetry(1);
            req.NeedAck = true;
            req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {

                }

                @Override
                public void onRequestResult(PipaRequest req) {
                    setEnable(true);
                    if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED) {
                        stopLoadding(false);
                    } else
                        stopLoadding(true);
                }
            });
        }
        if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
            saveBtn.setVisibility(View.VISIBLE);
            lastRequest = req;
        }
        infor.request(req);

        candle_onoff = false;
        mHandler.sendEmptyMessage(MessageModel.MSG_SEND_CANDLE);
    }

    private void getNodeStatus() {
        if (deviceInfo == null || deviceInfo.nodeInfo == null
                || deviceInfo.nodeInfo.cpsData == null || deviceInfo.nodeInfo.cpsData.lowPowerSupport()) {
            //skip lpn
            return;
        }
        int modelId = SigMeshModel.SIG_MD_LIGHT_CTL_S.modelId;
        int modelEleAdr = deviceInfo.getTargetEleAdr(modelId);
        String desc = null;

        if(MeshService.getInstance() == null){
            return;
        }
        if (modelEleAdr != -1) {
            MeshService.getInstance().getCtl(modelEleAdr, 1, null);
            return;
        }

        modelId = SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId;
        modelEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (modelEleAdr != -1) {
            MeshService.getInstance().getHSL(modelEleAdr, 1, null);
            return;
        }

        modelId = SigMeshModel.SIG_MD_LIGHTNESS_S.modelId;
        modelEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (modelEleAdr != -1) {
            MeshService.getInstance().getLightness(modelEleAdr, 1, null);
            return;
        }

        modelId = SigMeshModel.SIG_MD_G_ONOFF_S.modelId;
        modelEleAdr = deviceInfo.getTargetEleAdr(modelId);
        if (modelEleAdr != -1) {
            MeshService.getInstance().getOnOff(modelEleAdr, 1, null);
        }
    }

    private void setAnimaDrawable() {
        anima1 = (AnimationDrawable) ivLoadding1.getBackground();
        anima2 = (AnimationDrawable) ivLoadding2.getBackground();
        anima3 = (AnimationDrawable) ivLoadding3.getBackground();
        anima4 = (AnimationDrawable) ivLoadding4.getBackground();
        anima5 = (AnimationDrawable) ivLoadding5.getBackground();
        anima = (AnimationDrawable) ivLoading.getBackground();
    }

    /**
     * 设置加载动画
     */
    protected void showLoading() {
        ivLoading.jumpDrawablesToCurrentState();
        ivLoading.setVisibility(View.VISIBLE);
        if (anima != null) {
            anima.start();
        }
    }

    /**
     * 设置加载动画
     */
    protected void hideLoading() {
        ivLoading.setVisibility(View.GONE);
        if (anima != null) {
            anima.stop();
        }
    }

    protected void startLoadding() {
        switch (selectedIndex) {
            case 0:
                ivLoadding1.setVisibility(View.VISIBLE);
                anima1.start();
                break;
            case 1:
                ivLoadding2.setVisibility(View.VISIBLE);
                anima2.start();
                break;
            case 2:
                ivLoadding3.setVisibility(View.VISIBLE);
                anima3.start();
                break;
            case 3:
                ivLoadding4.setVisibility(View.VISIBLE);
                anima4.start();
                break;
            case 4:
                ivLoadding5.setVisibility(View.VISIBLE);
                anima5.start();
                break;
        }
        mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, LOADING_MAX_TIME);
    }

    private void stopLoadding(boolean success) {
        if (selectedIndex == 0) {
            if (anima1 != null) {
                anima1.stop();
            }
            ivLoadding1.setVisibility(View.GONE);
            if (success) {
                ivSelected1.setImageResource(R.drawable.icon_selected);
                ivSelected2.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected3.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected4.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected5.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            } else {
                ivSelected1.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            }
        } else if (selectedIndex == 1) {
            if (anima2 != null) {
                anima2.stop();
            }
            ivLoadding2.setVisibility(View.GONE);
            if (success) {
                ivSelected2.setImageResource(R.drawable.icon_selected);
                ivSelected1.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected3.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected4.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected5.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            } else {
                ivSelected2.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            }
        } else if (selectedIndex == 2) {
            if (anima3 != null) {
                anima3.stop();
            }
            ivLoadding3.setVisibility(View.GONE);
            if (success) {
                ivSelected3.setImageResource(R.drawable.icon_selected);
                ivSelected1.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected2.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected4.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected5.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            } else {
                ivSelected3.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            }
        } else if (selectedIndex == 3) {
            if (anima4 != null) {
                anima4.stop();
            }
            ivLoadding4.setVisibility(View.GONE);
            if (success) {
                ivSelected4.setImageResource(R.drawable.icon_selected);
                ivSelected1.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected2.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected3.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected5.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));

            } else {
                ivSelected4.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            }
        } else if (selectedIndex == 4) {
            if (anima5 != null) {
                anima5.stop();
            }
            ivLoadding5.setVisibility(View.GONE);
            if (success) {
                ivSelected5.setImageResource(R.drawable.icon_selected);
                ivSelected1.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected2.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected3.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                ivSelected4.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            } else {
                ivSelected5.setImageDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
            }
        } else {
            ivSelected1.setImageDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
            ivSelected2.setImageDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
            ivSelected3.setImageDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
            ivSelected4.setImageDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
            ivSelected5.setImageDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
        }
    }

    private void setEnable(boolean enable) {
        scene1Layout.setEnabled(enable);
        scene2Layout.setEnabled(enable);
        scene3Layout.setEnabled(enable);
        scene4Layout.setEnabled(enable);
        scene5Layout.setEnabled(enable);
        candle_light.setEnabled(enable);
    }

    /**
     * 设置监听器
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setListener() {
        backBtn.setOnClickListener(this);
        effectsBtn.setOnClickListener(this);
        rgbwBtn.setOnClickListener(this);
//		moreBtn.setOnClickListener(this);
        ivLeftOnBright.setOnClickListener(this);
        ivRightOnBright.setOnClickListener(this);
        scene1Layout.setOnClickListener(this);
        scene2Layout.setOnClickListener(this);
        scene3Layout.setOnClickListener(this);
        scene4Layout.setOnClickListener(this);
        scene5Layout.setOnClickListener(this);
        candle_light.setOnClickListener(this);
        if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
            saveBtn.setOnClickListener(this);
        } else {
            settingBtn.setOnClickListener(this);
        }
        final SparseArray<Integer> colorList = new SparseArray<>();
        colorList.put(R.id.light_color1_loadding, 0xFF0000);
        colorList.put(R.id.light_color2_loadding, 0xff9c00);
        colorList.put(R.id.light_color3_loadding, 0xffff00);
        colorList.put(R.id.light_color4_loadding, 0x00ff00);
        colorList.put(R.id.light_color6_loadding, 0x0000ff);
        colorList.put(R.id.light_color5_loadding, 0x00ffff);
        colorList.put(R.id.light_color7_loadding, 0xff00ff);

        for (int i = 0; i < colorList.size(); i++) {
            View colorView = (ImageView) findViewById(colorList.keyAt(i));
            if (colorView instanceof ImageView) {
                colorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getId();
                        mCurrentRGBWMode = LIGHT_MODE_RGB;
                        currentColor = colorList.get(v.getId());
                        tvCurrentColor.setBackgroundColor(currentColor);
                        mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                    }
                });
            }
        }
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (buttonView.isPressed()) {
                    if (isTelink) {
                        TelinkApiManager.getInstance().setSwitchLightOnOff(hslEleAdr, isChecked);
                    }
                    if (infor == null) return;
//                    PipaRequest req = infor.commitCandleLight(isChecked);
                    PipaRequest req = infor.commitLightOnOff(isChecked);

                    req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                            mHandler.removeMessages(MSG_SWITCH_TIMEOUT);
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            hideLoading();
                            if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                ToastUtils.showToast(LightRGBDetailActivity.this,
                                        R.string.switch_set_failed);
                            }
                        }
                    });
                    if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
                        saveBtn.setVisibility(View.VISIBLE);
                        lastRequest = req;
                    } else {
                        showLoading();
                        mHandler.sendEmptyMessageDelayed(MSG_SWITCH_TIMEOUT, 5000);
                    }
                    infor.request(req);

//                    if ( candle_onoff == true )
//                    {
//                        setCandle(false);
//                    }
                    candle_onoff = false;
                    mHandler.sendEmptyMessage(MessageModel.MSG_SEND_CANDLE);
                }
            }
        });
        whiteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.isPressed()) {
                    START_TIME = 0;
                    mHandler.removeMessages(MessageModel.MSG_SEND_ORDER);
                    mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBar.isPressed()) {
                    START_TIME = System.currentTimeMillis();
                    mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (seekBar.isPressed()) {
                    if (progress == 0)
                        seekBar.setProgress(1);
                    if (System.currentTimeMillis() - START_TIME >= DELEY_TIME) {
                        START_TIME = System.currentTimeMillis();
                        mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                    }
                }
            }
        });
        colorCircle.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                selectedIndex = -1;
//                isFirst = false;
                mCurrentRGBWMode = LIGHT_MODE_RGB;
                if (action == MotionEvent.ACTION_DOWN
                        || action == MotionEvent.ACTION_MOVE) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    ColorCircle circleView = (ColorCircle) v;
                    int pixelColor;
                    try {
                        pixelColor = circleView.getPixelColorAt(x, y);
                    } catch (Exception e) {
                        return true;
                    }

                    if (Color.TRANSPARENT != pixelColor && Color.WHITE != pixelColor) {
                        tvCurrentColor.setBackgroundColor(pixelColor);
                        int maxColor = 0;
                        int[] colors = Utils.getRGB(pixelColor);
                        for (int i : colors) {
                            if (i > maxColor)
                                maxColor = i;
                        }

                        if (System.currentTimeMillis() - START_TIME >= DELEY_TIME) {
                            START_TIME = System.currentTimeMillis();
                            if (currentColor != pixelColor) {
                                currentColor = pixelColor;
                                mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                            }
                        }
                    } else
                        return false;
                    if (Color.alpha(pixelColor) < 0xFF) {
                        return true;
                    }
                    circleView.setCursor(x, y);
                    circleView.invalidate();
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    START_TIME = 0;
                    mHandler.removeMessages(MessageModel.MSG_SEND_ORDER);
                    mHandler.sendEmptyMessage(MessageModel.MSG_SEND_ORDER);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    /**
     * 初始化组件
     */
    private void initView() {

        test = (TextView) findViewById(R.id.light_candle_text);
        tvTitle = (TextView) findViewById(R.id.title_name);
        ivTitle = (ImageView) findViewById(R.id.title_logo_center);
        backBtn = (Button) findViewById(R.id.title_back);
        saveBtn = (Button) findViewById(R.id.title_finished);
        settingBtn = (Button) findViewById(R.id.title_setting);
        switcher = (CheckBox) findViewById(R.id.light_switch);
        whiteBar = (SeekBar) findViewById(R.id.light_bright_seekbar);
        colorCircle = (ColorCircle) findViewById(R.id.light_colorcircle);
        tvCurrentColor = (TextView) findViewById(R.id.light_current);
        effectsBtn = (Button) findViewById(R.id.light_effects);

        rgbwBtn = (Button) findViewById(R.id.light_mode);
//        settingBtn = (Button) findViewById(R.id.title_setting);
//        ledLayout = (RelativeLayout) findViewById(R.id.lightdetail_led_layout);
        ivLeftOnBright = (ImageView) findViewById(R.id.light_bright_left);
        ivRightOnBright = (ImageView) findViewById(R.id.light_bright_right);
        ivSelected1 = (ImageView) findViewById(R.id.light_scene1_selected);
        ivSelected2 = (ImageView) findViewById(R.id.light_scene2_selected);
        ivSelected3 = (ImageView) findViewById(R.id.light_scene3_selected);
        ivSelected4 = (ImageView) findViewById(R.id.light_scene4_selected);
        ivSelected5 = (ImageView) findViewById(R.id.light_scene5_selected);

        ivLoadding1 = (ImageView) findViewById(R.id.light_scene1_loadding);
        ivLoadding2 = (ImageView) findViewById(R.id.light_scene2_loadding);
        ivLoadding3 = (ImageView) findViewById(R.id.light_scene3_loadding);
        ivLoadding4 = (ImageView) findViewById(R.id.light_scene4_loadding);
        ivLoadding5 = (ImageView) findViewById(R.id.light_scene5_loadding);

        scene1Layout = (LinearLayout) findViewById(R.id.light_scene1_layout);
        scene2Layout = (LinearLayout) findViewById(R.id.light_scene2_layout);
        scene3Layout = (LinearLayout) findViewById(R.id.light_scene3_layout);
        scene4Layout = (LinearLayout) findViewById(R.id.light_scene4_layout);
        scene5Layout = (LinearLayout) findViewById(R.id.light_scene5_layout);

        candle_light = (LinearLayout) findViewById(R.id.light_candle_layout);

        tvSceneName1 = (TextView) findViewById(R.id.light_sceneName1);
        tvSceneName2 = (TextView) findViewById(R.id.light_sceneName2);
        tvSceneName3 = (TextView) findViewById(R.id.light_sceneName3);
        tvSceneName4 = (TextView) findViewById(R.id.light_sceneName4);
        tvSceneName5 = (TextView) findViewById(R.id.light_sceneName5);

        ivLoading = (ImageView) findViewById(R.id.light_loadding);
        colorSelectorLayout = (LinearLayout) findViewById(R.id.llColorSelector);


//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//
//		ViewGroup.LayoutParams param = colorSelectorLayout.getLayoutParams();
//		param.height = (int)((dm.xdpi - 15)/colorSelectorLayout.getChildCount());
//		colorSelectorLayout.setLayoutParams(param);
        setAnimaDrawable();
    }

    /**
     * 设置标题的组件
     */
    private void setView() {
//		ledLayout.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        ivTitle.setVisibility(View.VISIBLE);
        ivTitle.setImageResource(R.drawable.titlebar_logo_luxx);
//		if (null == infor.getName() || "".equals(infor.getName())) {
//			tvTitle.setVisibility(View.GONE);
//            ivTitle.setVisibility(View.VISIBLE);
//            ivTitle.setImageResource(R.drawable.titlebar_logo_luxx);
//		} else {
//            tvTitle.setVisibility(View.VISIBLE);
//            ivTitle.setVisibility(View.GONE);
//			tvTitle.setText(infor.getName());
//		}
        updateView();
        backBtn.setVisibility(View.VISIBLE);

        if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
            settingBtn.setVisibility(View.GONE);
            saveBtn.setBackgroundColor(Color.TRANSPARENT);
            saveBtn.setText(R.string.finish);
        } else {
            settingBtn.setVisibility(View.VISIBLE);
        }
        saveBtn.setVisibility(View.GONE);

        if (infor instanceof LuxxMusicColor) {
            LuxxMusicColor musicInfor = (LuxxMusicColor) infor;
            if (musicInfor.isSpeakerEnable())
                effectsBtn.setBackgroundResource(R.drawable.btn_music_pause_selector);
            else
                effectsBtn.setBackgroundResource(R.drawable.btn_music_play_selector);
        } else if (deviceInfo != null && deviceInfo.isMusicDevice()) {
            effectsBtn.setVisibility(View.VISIBLE);
        } else {
            effectsBtn.setVisibility(View.GONE);
        }
        setWhiteBar();

    }

    private void initTelinkView() {
        if (deviceInfo != null) {
            int onOff = deviceInfo.getOnOff();
            switcher.setChecked(onOff == 1);
        } else if (telinkGroup != null) {
            switcher.setChecked(TelinkGroupApiManager.getInstance().isGroupOn(telinkGroup));
        }

        whiteBar.setProgress((int) (RGBConfigUtils.MAX_VALUE * 0.68));
    }


    /**
     * 设置白光滚动轴的最大值
     */
    private void setWhiteBar() {
        whiteBar.setMax(RGBConfigUtils.MAX_VALUE);
    }

    /**
     * 更新界面
     */
    private void updateView() {
        if (infor != null) {
            List<PISBase> gObjs = infor.getGroupObjects();
            if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP && (gObjs == null || gObjs.size() == 0)) {
                return;
            }
            int white = 0;
            byte[] rgbBytes = infor.getColorBytes(false);
            if (rgbBytes != null) {
                currentColor = Color.rgb(rgbBytes[0] & 0xFF, rgbBytes[1] & 0xFF, rgbBytes[2] & 0xFF);
                white = rgbBytes[3] & 0xFF;
                tvCurrentColor.setBackgroundColor(currentColor);
                int maxColor = 0;
                for (byte color : rgbBytes) {
                    if ((color & 0xFF) > maxColor)
                        maxColor = color & 0xFF;
                }
                whiteBar.setProgress(maxColor);
            }

            if (white == 0) {
                mCurrentRGBWMode = LIGHT_MODE_WHITE;
                rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal_nocolor);
            } else {
                mCurrentRGBWMode = LIGHT_MODE_RGB;
                rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal);
            }

            switcher.setChecked(infor.getLightStatus() == PISxinColor.XINCOLOR_STATUS_ON);
            tvTitle.setText(infor.getName() == null ? "" : infor.getName());
            try {
                setPisData(infor.getPISKeyString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setEffectMode(final int idx) {
//        if ( candle_onoff == true)
//        {
//            setCandle(false);
//        }
        if(infor == null) return;
        PipaRequest req = infor.commitLightEffect(idx);
        req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
            @Override
            public void onRequestStart(PipaRequest req) {
                selectedIndex = idx;
                startLoadding();
                if (!switcher.isChecked()) {
                    switcher.setChecked(true);
                }
            }

            @Override
            public void onRequestResult(PipaRequest req) {
                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                    stopLoadding(true);
                else
                    stopLoadding(false);
            }
        });
        if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
            lastRequest = req;
            saveBtn.setVisibility(View.VISIBLE);
        }
        infor.request(req);
        candle_onoff = false;
        mHandler.sendEmptyMessage(MessageModel.MSG_SEND_CANDLE);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
//        isFirst = false;
        switch (v.getId()) {
            case R.id.light_bright_left:
                setProgress(true);
                sendRGBOrder(false);
                break;
            case R.id.light_bright_right:
                setProgress(false);
                sendRGBOrder(false);
                break;
            // 返回按钮
            case R.id.title_back:
                finish();
                overridePendingTransition(R.anim.anim_in_from_left,
                        R.anim.anim_out_to_right);
                break;
            case R.id.light_scene1_layout:
                setEffectMode(0);
                break;
            case R.id.light_scene2_layout:
                setEffectMode(1);

                break;
            case R.id.light_scene3_layout:
                setEffectMode(2);
                break;

            case R.id.light_scene4_layout:
                setEffectMode(3);
                break;
            case R.id.light_scene5_layout:
                try {
                    if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION)
                        break;
                    intent = new Intent(LightRGBDetailActivity.this,
                            LightTimerListActivity.class);
                    intent.putExtra(MessageModel.PISBASE_KEYSTR,
                            infor.getPISKeyString());
                    startActivityForResult(intent, REQUEST_CODE_TIMER);
                    overridePendingTransition(R.anim.anim_in_from_right,
                            R.anim.anim_out_to_left);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.light_candle_layout:
                candle_onoff = switcher.isChecked();
                if (candle_onoff) {

                    if (isTelink) {
                        TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, CommonMeshCommand.getCandleCommand(candle_onoff));
                    }
                    if (infor == null) return;
                    PipaRequest req = infor.commitLightOnOff(false);
                    infor.request(req);
                    switcher.setChecked(false);
                }else{
                    if (isTelink) {
                        TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, CommonMeshCommand.getCandleCommand(candle_onoff));
                    }
                }

                setCandle(candle_onoff);
//                mHandler.sendEmptyMessage(MessageModel.MSG_SEND_CANDLE);
                break;
            case R.id.title_finished:
                save();
                break;
            case R.id.title_setting:
                // TODO LEE 灯控设置界面,//绑定灯组操作

                intent = new Intent(LightRGBDetailActivity.this,
                        LightSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(TelinkApiManager.TELINK_ADDRESS, telinkAddress);
                bundle.putBoolean(TelinkApiManager.IS_TELINK_KEY, isTelink);
                bundle.putBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, isTelinkGroup);
                intent.putExtras(bundle);
                if (infor != null) {
                    intent.putExtra(MessageModel.PISBASE_KEYSTR,
                            infor.getPISKeyString());
                }
                startActivityForResult(intent, REQUEST_CODE_SETTING);
                overridePendingTransition(R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);

                break;
            case R.id.light_effects:
                if (isTelink) {
                    isVoiceOn = !isVoiceOn;
                    TelinkApiManager.getInstance().setCommonCommand(hslEleAdr, CommonMeshCommand.getVoiceOnOffCommand(isVoiceOn));
                    if (infor == null) return;
                }
//                intent = new Intent(LightRGBDetailActivity.this,
//                        LightEffectsActivity.class);
//                intent.putExtra(MessageModel.PISBASE_KEYSTR,
//                        infor.getPISKeyString());
//                startActivityForResult(intent, REQUEST_CODE_EFFECT);
//                overridePendingTransition(R.anim.anim_in_from_right,
//                        R.anim.anim_out_to_left);
                if (!(infor instanceof LuxxMusicColor))
                    break;
                final LuxxMusicColor musicInfor = (LuxxMusicColor) infor;
                PipaRequest playreq;
                if (musicInfor.isSpeakerEnable()) {
                    playreq = musicInfor.commitBtAudioStatus(false);
                } else {
                    playreq = musicInfor.commitBtAudioStatus(true);
                }
                playreq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                    @Override
                    public void onRequestStart(PipaRequest req) {
                        showLoading();
                    }

                    @Override
                    public void onRequestResult(PipaRequest req) {
                        hideLoading();
                        if (musicInfor.isSpeakerEnable())
                            effectsBtn.setBackgroundResource(R.drawable.btn_music_pause_selector);
                        else
                            effectsBtn.setBackgroundResource(R.drawable.btn_music_play_selector);
                        if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED)
                            Toast.makeText(LightRGBDetailActivity.this,
                                    R.string.retry_tips, Toast.LENGTH_SHORT).show();
                    }
                });
                playreq.setRetry(3);

                if (activityMode == Constant.REQUEST_CODE_TIMER_ACTION) {
                    lastRequest = playreq;
                    saveBtn.setVisibility(View.VISIBLE);
                }
                infor.request(playreq);
                break;
            case R.id.light_mode:
//                if (mCurrentRGBWMode == LIGHT_MODE_WHITE) {
//                    mCurrentRGBWMode = LIGHT_MODE_RGB;
//                    rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal);
//                }
//                else {
//                    mCurrentRGBWMode = LIGHT_MODE_WHITE;
//                    rgbwBtn.setBackgroundResource(R.drawable.icon_btn_effect_normal_nocolor);
//                }

                currentColor = 0xf5b94d;
                mCurrentRGBWMode = LIGHT_MODE_WHITE;
                sendRGBOrder(false);
                break;
        }
    }

    private void setCandle(boolean isOn)        // NextApp.tw
    {
//        candle_onoff = isOn;
//        PipaRequest req = infor.commitCandleLight(isOn);
//        infor.request(req);
        if (infor == null) return;
        PipaRequest req;

        if (infor.getServiceId() == 0) {
            List<PISBase> srvs = infor.getGroupObjects();
            for (PISBase srv : srvs) {
                inforX = (PISxinColor) srv;

                if (inforX.getT1() == 0x10 && inforX.getT2() == 0x05) {
                    if (isOn == true) {
                        req = inforX.commitLightOnOff(false);
                        inforX.request(req);
                    }
                    candle_onoff = isOn;
                    req = inforX.commitCandleLight(isOn);
                    inforX.request(req);
                }
            }
        } else {
//            if ( isOn == true && switcher.isChecked() == true )
//            {
//                req = infor.commitLightOnOff(false);
//                infor.request(req);
//            }

            if (infor.getT1() == 0x10 && infor.getT2() == 0x05) {
                candle_onoff = isOn;
                req = infor.commitCandleLight(isOn);
                infor.request(req);
            }
        }

//        infor.getGroupObjects().get(0)
    }

    /**
     * 设置进度条的进度
     *
     * @param isLeft 是左边点击还是右边
     */
    private void setProgress(boolean isLeft) {
        int progress = whiteBar.getProgress();
        int max = whiteBar.getMax();
        if (isLeft) {
            progress -= max / 50;
        } else {
            progress += max / 50;
        }
        if (progress < 1) {
            progress = 1;
        } else if (progress > max) {
            progress = max;
        }
        whiteBar.setProgress(progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i(TAG, "onActivityResult(): resultCode = " + resultCode
                + ", requestCode = " + requestCode);
//        if (requestCode == Constant.REQUEST_CODE_RGB_EFFECTS
//                && resultCode == RESULT_OK) {
//            cmd = data.getShortExtra("cmd", (short) 0);
//            cmdContent = data.getByteArrayExtra("cmdContent");
//        } else
        if (requestCode == REQUEST_SCENE_CODE && resultCode == RESULT_OK) {
            SceneBean bean = (SceneBean) data.getSerializableExtra("scene");
            if (bean.t1 == PISConstantDefine.PIS_MAJOR_LIGHT
                    && bean.t2 == PISConstantDefine.PIS_LIGHT_LIGHT
                    && infor != null) {
                whiteBar.setProgress(bean.bright);
                currentColor = bean.rgb;
                tvCurrentColor.setBackgroundColor(bean.rgb);
            }
//            setScenes();
        } else if (requestCode == REQUEST_CODE_SETTING) {
            if (data == null) return;
            int result = data.getIntExtra("result", 0);
            if (result == 3) {//该组已经被删除
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取场景信息，如果缓存中不存在则从数据库中取，如果缓存中存在则直接使用数据
     */
//	private void setScenes() {
//		if (null == cacheManager.getLEDUsedScenes()
//				|| null == cacheManager.getRGBUsedScenes()) {
//			cacheManager.setScenes(mSceneDao.getAllScenes(SharePreferenceUtils
//					.getInstance(this).getCurrentUser()));
//		}
//		scenes = cacheManager.getRGBUsedScenes();
//		if (null == scenes) {
//			scenes = new ArrayList<SceneBean>();
//		}
//		addDefaultScenes();
//		setScenesView();
//	}

//	private void addDefaultScenes() {
//		int size = 4 - scenes.size();
//		if (size > 0) {
//			for (int i = 0; i < size; i++) {
//				SceneBean bean = new SceneBean();
//				bean.t1 = PISConstantDefine.PIS_MAJOR_LIGHT;
//				bean.t2 = PISConstantDefine.PIS_LIGHT_COLOR;
//				if (i == 0) {
//					bean.sceneName = getString(R.string.effect_spa);
//					bean.picture = "5";
//					bean.bright = 0;
//					bean.coldwarm = 0;
//					bean.rgb = 0xff0000ff;
//				} else if (i == 1) {
//					bean.sceneName = getString(R.string.effect_sunrise);
//					bean.picture = "6";
//					bean.bright = 0;
//					bean.coldwarm = 0;
//					bean.rgb = 0xffff0000;
//				} else if (i == 2) {
//					bean.sceneName = getString(R.string.effect_random);
//					bean.picture = "7";
//					bean.bright = 0;
//					bean.coldwarm = 0;
//					bean.rgb = 0xffffa800;
//				} else if (i == 3) {
//					bean.sceneName = getString(R.string.effect_breath);
//					bean.picture = "8";
//					bean.bright = 0;
//					bean.coldwarm = 0;
//					bean.rgb = Color.WHITE;
//				} else if (i == 4) {
//					bean.sceneName = getString(R.string.effect_night);
//					bean.picture = "8";
//					bean.bright = 0;
//					bean.coldwarm = 0;
//					bean.rgb = Color.WHITE;
//				}
//				bean.used = i;
//				bean.user = SharePreferenceUtils.getInstance(this)
//						.getCurrentUser();
//				bean.id = (int) mSceneDao.insertScene(bean);
//				if (size == 4) {
//					scenes.add(bean);
//				} else {
//					scenes.add(i, bean);
//				}
//			}
//			cacheManager.setScenes(mSceneDao.getAllScenes(SharePreferenceUtils
//					.getInstance(this).getCurrentUser()));
//		}
//	}

//    private void setScenesView() {
//        if (null == scenes || scenes.size() == 0) {
//            scene1Layout.setVisibility(View.INVISIBLE);
//            scene2Layout.setVisibility(View.INVISIBLE);
//            scene3Layout.setVisibility(View.INVISIBLE);
//            scene4Layout.setVisibility(View.INVISIBLE);
//			scene5Layout.setVisibility(View.INVISIBLE);
//            tvSceneName1.setText("");
//            tvSceneName2.setText("");
//            tvSceneName3.setText("");
//            tvSceneName4.setText("");
//			tvSceneName5.setText("");
//        } else {
//            int size = scenes.size();
//            if (size == 1) {
//                setSceneBtn(scene1Layout, tvSceneName1, ivSelected1,
//                        scenes.get(0));
//                setSceneBtn(scene2Layout, tvSceneName2, ivSelected2, null);
//                setSceneBtn(scene3Layout, tvSceneName3, ivSelected3, null);
//                setSceneBtn(scene4Layout, tvSceneName4, ivSelected4, null);
//            } else if (size == 2) {
//                setSceneBtn(scene1Layout, tvSceneName1, ivSelected1,
//                        scenes.get(0));
//                setSceneBtn(scene2Layout, tvSceneName2, ivSelected2,
//                        scenes.get(1));
//                setSceneBtn(scene3Layout, tvSceneName3, ivSelected3, null);
//                setSceneBtn(scene4Layout, tvSceneName4, ivSelected4, null);
//            } else if (size == 3) {
//                setSceneBtn(scene1Layout, tvSceneName1, ivSelected1,
//                        scenes.get(0));
//                setSceneBtn(scene2Layout, tvSceneName2, ivSelected2,
//                        scenes.get(1));
//                setSceneBtn(scene3Layout, tvSceneName3, ivSelected3,
//                        scenes.get(2));
//                setSceneBtn(scene4Layout, tvSceneName4, ivSelected4, null);
//            } else if (size >= 4) {
//                setSceneBtn(scene1Layout, tvSceneName1, ivSelected1,
//                        scenes.get(0));
//                setSceneBtn(scene2Layout, tvSceneName2, ivSelected2,
//                        scenes.get(1));
//                setSceneBtn(scene3Layout, tvSceneName3, ivSelected3,
//                        scenes.get(2));
//                setSceneBtn(scene4Layout, tvSceneName4, ivSelected4,
//                        scenes.get(3));
//            }else if (size >= 5) {
//                setSceneBtn(scene1Layout, tvSceneName1, ivSelected1,
//                        scenes.get(0));
//                setSceneBtn(scene2Layout, tvSceneName2, ivSelected2,
//                        scenes.get(1));
//                setSceneBtn(scene3Layout, tvSceneName3, ivSelected3,
//                        scenes.get(2));
//                setSceneBtn(scene4Layout, tvSceneName4, ivSelected4,
//                        scenes.get(3));
//                setSceneBtn(scene5Layout, tvSceneName5, ivSelected5,
//                        scenes.get(4));
//			}
//        }
//    }
    @SuppressWarnings("deprecation")
//    private void setSceneBtn(LinearLayout ib, TextView tv, ImageView iv,
//                             SceneBean bean) {
//        if (bean != null) {
//            ib.setVisibility(View.VISIBLE);
//            tv.setText(bean.sceneName == null ? "" : bean.sceneName);
//        } else {
//            ib.setVisibility(View.INVISIBLE);
//            tv.setText("");
//            iv.setBackground(new ColorDrawable(Color.TRANSPARENT));
//        }
//        ib.setTag(bean);
//    }

    /**
     * 保存命令内容和命令字
     */
    private void save() {
        if (lastRequest != null) {
            try {
                Intent intent = new Intent();
                intent.putExtra(MessageModel.PIPAREQUEST_DATA, lastRequest.RequestData);
                setResult(RESULT_OK, intent);
                finish();
//                ToastUtils.showToast(this, R.string.scene_detail_save_success);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缓存定时器需要的开关命令和命令内容
     */
//    private void setTimerCmdOnSwitch() {
//        tempCmd = PISxinColor.PIS_CMD_LIGHT_ONOFF_SET;
//        tempCmdContent = new byte[1];
//        if (switcher.isChecked()) {
//            tempCmdContent[0] = (byte) (0x01 & 0x0f);
//        } else {
//            tempCmdContent[0] = (byte) (0x00 & 0x0f);
//        }
//    }

    /**
     * 缓存定时器需要的颜色命令和命令内容
     */
//    private void setTimerCmdOnColor(int red, int green, int blue, int white) {
//        tempCmdContent = null;
//        tempCmd = PISxinColor.PIS_CMD_LIGHT_RGBW_SET;
//		tempCmdContent = PISLightRGB.getCmdContentOnColor(red, green, blue,
//				white);
//	}
    @Override
    protected void onResume() {
        super.onResume();
//		setScenes();

        updateView();
        if (!isTelink) {
            pisUpdateDataFromDevice();
        }
    }

    /**
     * Pis从设备更新数据
     */
    private void pisUpdateDataFromDevice() {

        PipaRequest req = infor.updateLightStatus();
        req.setRetry(2);
        req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
            @Override
            public void onRequestStart(PipaRequest req) {

            }

            @Override
            public void onRequestResult(PipaRequest req) {
                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                    updateView();
            }
        });
        infor.request(req);

        if (infor.getName() == null || infor.getName().length() == 0) {
            PipaRequest nameReq = infor.updatePISInfo();
            nameReq.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(PipaRequest req) {

                }

                @Override
                public void onRequestResult(PipaRequest req) {
                    if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                        updateView();
                }
            });
            infor.request(nameReq);
        }

        // NextApp.tw
        if (infor.getServiceId() != 0) {
            if (infor.getT1() == 0x10 && infor.getT2() == 0x05) {
                candle_light.setVisibility(View.VISIBLE);

            } else {

                candle_light.setVisibility(View.GONE);
            }
        } else {
            if (infor.getGroupObjects().size() > 0) {
                if (infor.getGroupObjects().get(0).getT1() == 0x10 && infor.getGroupObjects().get(0).getT2() == 0x05) {
                    candle_light.setVisibility(View.VISIBLE);

                } else {

                    candle_light.setVisibility(View.GONE);
                }
            } else {
                candle_light.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearData();
    }

    /**
     * 清理数据
     */
    protected void clearData() {
//        isTimer = false;
        mHandler.removeMessages(MSG_SWITCH_TIMEOUT);
//        cmdContent = null;
//        tempCmdContent = null;
        if (scenes != null) {
            scenes.clear();
            scenes = null;
        }
        colorCircle.destoryView();

        anima = null;
        anima1 = null;
        anima2 = null;
        anima3 = null;
        anima4 = null;
    }


    /**
     * 移除开关设置超时的消息
     */
//    private void removeMessageOnStatus() {
//        if (mHandler != null) {
//            mHandler.removeMessages(MSG_SWITCH_TIMEOUT);
//        }
//    }
}
