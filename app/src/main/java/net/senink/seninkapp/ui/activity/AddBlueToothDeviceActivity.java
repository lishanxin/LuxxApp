package net.senink.seninkapp.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.event.CommandEvent;
import com.telink.sig.mesh.event.Event;
import com.telink.sig.mesh.event.EventListener;
import com.telink.sig.mesh.event.MeshEvent;
import com.telink.sig.mesh.event.NotificationEvent;
import com.telink.sig.mesh.event.ScanEvent;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.http.HttpDeviceInfo;
import net.senink.piservice.http.HttpRequest;
import net.senink.piservice.http.PISHttpManager;
import net.senink.piservice.pinm.PINMoBLE.interfaces.AssociationListener;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.util.ByteUtilBigEndian;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.BlueLightAdapter;

//import net.senink.seninkapp.interfaces.AssociationListener;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.telink.model.TelinkOperation;
import net.senink.seninkapp.telink.view.DeviceProvisionListAdapter;
import net.senink.seninkapp.ui.entity.BlueToothBubble;
import net.senink.seninkapp.ui.util.LogUtils;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.StepsView;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 用于添加不同类型的设备
 * // TODO LEE 查找灯组操作
 *
 * @author zhaojunfeng
 * @date 2015-09-29
 */
public class AddBlueToothDeviceActivity extends BaseActivity implements
        View.OnClickListener, AssociationListener, EventListener<String> {
    public final static String TAG = "AddBlueToothDeviceActivity";
    public final static int MSG_START_BINDING = 10;
    public final static int MSG_START_CONFIGING = 11;
    public final static int MSG_CONFIG_SUCCESS = 12;
    public final static int MSG_CONFIG_FAILED = 13;
    public final static int MSG_LINE_INIT = 14;
    // 更新添加设备的列表
    public final static int MSG_UPDATEVIEW = 15;
    public static final int MSG_DEVBIND_SUCCESS = 16;
    public static final int MSG_DEVBIND_FAILED = 17;

    public final static int MSG_TELINK_START_BINDING = 18;
    public final static int MSG_TELINK_START_CONFIGING = 19;
    public final static int MSG_TELINK_CONFIG_SUCCESS = 20;
    public final static int MSG_TELINK_CONFIG_FAILED = 21;
    public final static int MSG_TELINK_LINE_INIT = 22;
    // 更新添加设备的列表
    public final static int MSG_TELINK_UPDATEVIEW = 23;
    public static final int MSG_TELINK_DEVBIND_SUCCESS = 24;
    public static final int MSG_TELINK_DEVBIND_FAILED = 25;
    public static final int MSG_TELINK_CONFIG_SUCCESS_AUTO_CONNECT = 29;


    // 返回按钮
    private Button backBtn;
    // 四个步骤的提示
    private TextView tvStep1, tvStep2, tvStep3, tvStep4;
    // 为绑定设备列表下的横线
    private View spaceView;
    // 动画组件
    private ImageView ivAnima1, ivAnima2, ivAnima3;
    // 动画
    private AnimationDrawable anima1, anima2, anima3;
    // 标题
    private TextView tvTitle;
    private ImageView ivTitle;
    // 未配置的蓝牙灯的列表
    private ListView listView;
    // 蓝牙控制器
    private MeshController controller;
    // 进度线
    private StepsView stepsView;
    // 进度线右边的根布局
    private RelativeLayout contentLayout;
    // 进度线右边的根布局的初始高度
    private int originalHeight = 0;
    // 当前进度的颜色值
    private int currentColor = 0xff9d9ca0;
    // key为mac,value为classid
    private Map<String, String> macs = new HashMap<String, String>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Appearance> mAppearances = new HashMap<Integer, Appearance>();
    // 用于存放扫描到而未按类过滤的蓝牙设备
    private SparseArray<BlueToothBubble> tempList = new SparseArray<BlueToothBubble>();
    // 用于存放按照指定类型过滤过的蓝牙设备
    private SparseArray<BlueToothBubble> blueToothDevices = new SparseArray<BlueToothBubble>();
    // 灯的适配器
    private BlueLightAdapter adapter;

    // Telink自动绑定标记
    public static final String TelinkAutoConnectKey = "autoTelinkConnect";
    private boolean isTelinkAutoConnect = false;
    private boolean isTelinkOnBinding = false;
    private int retryTime = 2;

    private PISManager manger;
    private PISMCSManager mcm;
    // 当前正在绑定的灯泡信息
    private BlueToothBubble selectedBubble;
    private List<String> classFilter;

    private RecyclerView telinkListView;

    private List<String> startGroupPISKey = new ArrayList<>();
    private List<Group> endGroups = new ArrayList<>();
    private Group autoConnectGroup = null;

    private long lastTelinkStatusUpdateTime = 0;
    private Runnable checkTelinkAutoConnectRunnable = new Runnable() {
        @Override
        public void run() {
            if(isTelinkOnBinding){
                lastTelinkStatusUpdateTime = SystemClock.elapsedRealtime();
                mHandler.postDelayed(checkTelinkAutoConnectRunnable, 1500);
            }else if(SystemClock.elapsedRealtime() - lastTelinkStatusUpdateTime > 15 * 1000){
                // 超过15秒就自动退出
                ToastUtils.showToast(getApplicationContext(),
                        R.string.addbubble_step2_tip);
                backBtn.performClick();
            }else{
                mHandler.postDelayed(checkTelinkAutoConnectRunnable, 1500);
            }
        }
    };
    /**
     * 添加设备的类型 1:led灯 2：网关 3:RGB灯 4：遥控器 6:智能鞋垫
     */
    private int type = 3;
    //是否正在配置中
    private boolean isConfiging = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 刷新新增蓝牙设备列表
                case MSG_UPDATEVIEW:
                    if (blueToothDevices != null && blueToothDevices.size() > 0 && !isConfiging) {
                        startAnima1();
                        setListViewVisible(View.VISIBLE);
                        adapter.setList(blueToothDevices);
                        adapter.notifyDataSetChanged();
                    } else {
                        stopAnima1();
                        setListViewVisible(View.GONE);
                    }
                    break;
                case MSG_TELINK_LINE_INIT: {
                    isConfiging = true;
                    setListViewVisible(View.GONE);
                    setSteps(0, true);
                    mHandler.removeMessages(AddBlueToothDeviceActivity.MSG_TELINK_START_BINDING);
                    mHandler.removeMessages(AddBlueToothDeviceActivity.MSG_TELINK_DEVBIND_FAILED);
                    mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_TELINK_START_BINDING);
                }
                break;
                case MSG_TELINK_START_BINDING: {
                    isTelinkOnBinding = true;
                    mHandler.removeMessages(MSG_TELINK_START_BINDING);
                    setListViewVisible(View.GONE);
                    startAnima2();
                }
                break;
                case MSG_TELINK_DEVBIND_SUCCESS: {
                    isTelinkOnBinding = true;
                    mHandler.removeMessages(MSG_TELINK_DEVBIND_SUCCESS);
                    isConfiging = true;
                    setSteps(1, true);
                    startAnima2();
                    setListViewVisible(View.GONE);
                    mHandler.sendEmptyMessage(MSG_TELINK_START_CONFIGING);
                }
                break;
                case MSG_TELINK_DEVBIND_FAILED: {
                    isTelinkOnBinding = false;
                    mHandler.removeMessages(MSG_TELINK_DEVBIND_FAILED);
                    isConfiging = false;
                    stopAnima2();
                    setSteps(1, false);
                    setListViewVisible(View.VISIBLE);
                }
                break;
                case MSG_TELINK_START_CONFIGING: {
                    isTelinkOnBinding = true;
                    stopAnima2();
                    startAnima3();
                    setSteps(2, true);
                }
                break;
                case MSG_TELINK_CONFIG_SUCCESS: {
                    isTelinkOnBinding = true;
                    mHandler.removeMessages(MSG_TELINK_CONFIG_SUCCESS);
                    stopAnima3();
                    setSteps(3, true);
                    isConfiging = false;
                    //考虑如何在新设备添加后直接转换为PISDevice置入列表中保存
                    configSuccess(false);
                }
                break;
                case MSG_TELINK_CONFIG_SUCCESS_AUTO_CONNECT:
                    if(isTelinkAutoConnect){
                        DeviceInfo bindTelinkDevice = (DeviceInfo) msg.obj;
                        if(autoConnectGroup != null){
                            isTelinkOnBinding = true;
                            TelinkGroupApiManager.getInstance().addDeviceToGroup(autoConnectGroup.address, bindTelinkDevice.meshAddress);
                        }
                    }
                    break;
                case MSG_TELINK_CONFIG_FAILED: {
                    isConfiging = false;
                    isTelinkOnBinding = false;
                    stopAnima3();
                    mHandler.removeMessages(MSG_TELINK_CONFIG_FAILED);
                    setListViewVisible(View.VISIBLE);
                    setSteps(3, false);
                }
                break;
                case MSG_LINE_INIT: {
                    isConfiging = true;
                    setListViewVisible(View.GONE);
                    setSteps(0, true);

                    selectedBubble = adapter.getSelectedBubble();
                    mcm = PISManager.getInstance().getMCSObject();
                    if (mcm == null || selectedBubble == null ||
                            selectedBubble.appearance == null ||
                            TextUtils.isEmpty(selectedBubble.appearance.macAddr)) {
                        mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_DEVBIND_FAILED);
                        break;
                    }
                    mHandler.removeMessages(AddBlueToothDeviceActivity.MSG_START_BINDING);
                    mHandler.removeMessages(AddBlueToothDeviceActivity.MSG_DEVBIND_FAILED);
                    mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_START_BINDING);
                    mHandler.sendEmptyMessageDelayed(
                            AddBlueToothDeviceActivity.MSG_DEVBIND_FAILED, 60000);

                    byte[] macBytes = ByteUtilBigEndian.hexStringToBytes(selectedBubble.appearance.macAddr);
                    byte[] clsBytes = ByteUtilBigEndian.hexStringToBytes(selectedBubble.getClassId());
                    PipaRequest req = mcm.bindDevice(macBytes, clsBytes);
                    req.userData = selectedBubble;
                    req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {

                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                //获取BLEID，并完成配置
                                mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_DEVBIND_SUCCESS);
                            } else
                                mHandler.sendEmptyMessage(AddBlueToothDeviceActivity.MSG_DEVBIND_FAILED);

                        }
                    });
                    mcm.request(req);

                }
                break;
                case MSG_DEVBIND_SUCCESS:
                    mHandler.removeMessages(MSG_DEVBIND_SUCCESS);
                    if (selectedBubble != null) {
                        final BlueToothBubble bubble = selectedBubble;
                        isConfiging = true;
                        HttpDeviceInfo httpDevice = new HttpDeviceInfo();
                        HttpRequest req = httpDevice.updateBleDeviceId(bubble.appearance.macAddr);

                        req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
                            @Override
                            public void onRequestStart(HttpRequest req) {

                            }

                            @Override
                            public void onRequestResult(HttpRequest req) {
                                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                    bubble.deviceId = ((HttpDeviceInfo.bleDeviceIdHttpRequest) req).bleId;
                                    Message message = mHandler
                                            .obtainMessage(MSG_START_CONFIGING, bubble);
                                    mHandler.sendMessage(message);
                                } else {
                                    mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
                                }
                            }
                        });
                        PISHttpManager.getInstance(AddBlueToothDeviceActivity.this).request(req);
                    }
                    setSteps(1, true);
                    startAnima2();
                    setVisiablityOnListView(false);
                    break;
                case MSG_DEVBIND_FAILED:
                    mHandler.removeMessages(MSG_DEVBIND_FAILED);
                    isConfiging = false;
                    if (adapter.getIndexOnSelected() >= 0) {
                        adapter.setResult(
                                listView.getChildAt(adapter.getIndexOnSelected()),
                                false, true);
                    }
                    stopAnima2();
                    setSteps(1, false);
                    setVisiablityOnListView(true);
                    break;
                case MSG_START_BINDING:
                    mHandler.removeMessages(MSG_START_BINDING);
                    setVisiablityOnListView(false);
                    startAnima2();
                    break;
                case MSG_START_CONFIGING:
                    stopAnima2();
                    startAnima3();
                    setSteps(2, true);
                    if (msg.obj != null) {
                        try {
                            BlueToothBubble infor = (BlueToothBubble) msg.obj;
                            if (infor != null) {
                                controller.setNextDeviceId(infor.deviceId);
                                controller.associateDevice(infor.uuidHash, null);
                            } else {
                                LogUtils.i(TAG, "ELSE infor == null");
                                mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
                            }
                        } catch (Exception e) {
                            LogUtils.i(TAG, "ERROR-> E = " + e.getMessage());
                            mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
                        }
                    } else {
                        LogUtils.i(TAG, "msg.obj == null");
                        mHandler.sendEmptyMessage(MSG_CONFIG_FAILED);
                    }
                    break;
                case MSG_CONFIG_SUCCESS:
                    mHandler.removeMessages(MSG_CONFIG_SUCCESS);
                    stopAnima3();
                    setSteps(3, true);
                    isConfiging = false;
                    //考虑如何在新设备添加后直接转换为PISDevice置入列表中保存
                    PISManager.getInstance().DiscoverAll();
                    configSuccess(true);
                    break;
                case MSG_CONFIG_FAILED:
                    isConfiging = false;
                    stopAnima3();
                    mHandler.removeMessages(MSG_DEVBIND_FAILED);
                    setVisiablityOnListView(true);
                    // 如果配对失败，则解绑mcm的设备
                    if (selectedBubble != null && mcm != null) {
//					mcm.unBindDevice(selectedBubble.appearance.macAddr, false);
                        mcm.request(mcm.unbindDevice(ByteUtilBigEndian.hexStringToBytes(selectedBubble.appearance.macAddr)));
                    }
                    adapter.setResult(
                            listView.getChildAt(adapter.getIndexOnSelected()),
                            false, true);
                    setSteps(3, false);
                    break;
            }
        }

    };
    private DeviceProvisionListAdapter telinkListAdapter;

    private void configSuccess(boolean isPis) {
        if (isPis) {
            if (adapter.getCount() == 1 && telinkListAdapter.getItemCount() == 0) {
                ToastUtils.showToast(getApplicationContext(),
                        R.string.addbubble_step2_tip);
                backBtn.performClick();
            } else {
                try {
                    if (adapter.getSelectedBubble() != null) {
                        blueToothDevices.remove(adapter.getSelectedBubble().uuidHash);
                        adapter.setList(blueToothDevices);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                }
                if (adapter.getCount() > 0) {
                    setVisiablityOnListView(true);
                } else {
                    setVisiablityOnListView(false);
                }
            }
        }else{
            if(isTelinkAutoConnect){
//                TelinkApiManager.getInstance().startScanTelink(isTelinkAutoConnect, mHandler);
            }else{
                if(telinkListAdapter.getItemCount() <= 1 && adapter.getCount() == 0){
                    ToastUtils.showToast(getApplicationContext(),
                            R.string.addbubble_step2_tip);
                    backBtn.performClick();
                }else{
                    TelinkApiManager.getInstance().startScanTelink(isTelinkAutoConnect, mHandler);
                }
            }

        }
        setListViewVisible(View.VISIBLE);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbubble);
        EventBus.getDefault().register(this);
        controller = MeshController.getInstance(this);
        manger = PISManager.getInstance();
//		threadPool = Executors.newFixedThreadPool(1);
        mcm = manger.getMCSObject();
//		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_SUCCESS, this);
//		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_FAIL, this);
//		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS, this);
//		MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_FAIL, this);
        setData();
        initView();
        setListener();

        if(isTelinkAutoConnect){
            refreshLastTelinkStatusUpdateTime();
            mHandler.postDelayed(checkTelinkAutoConnectRunnable, 1500);
            TelinkApiManager.getInstance().startAutoBindOnCreate(mHandler);
        }else{
            TelinkApiManager.getInstance().startScanTelinkOnCreate(isTelinkAutoConnect, mHandler);
        }
    }

    private void startAnima1() {
        if (null == anima1) {
            anima1 = (AnimationDrawable) ivAnima1.getBackground();
        }
        if (!anima1.isRunning()) {
            anima1.start();
            ivAnima1.setVisibility(View.VISIBLE);
        }
    }

    private void setListViewVisible(int visible) {
        if (visible == View.VISIBLE) {
            if (adapter.getCount() > 0) {
                listView.setVisibility(visible);
            }
            if (telinkListAdapter != null && telinkListAdapter.getItemCount() > 0) {
                telinkListView.setVisibility(visible);
            }
        } else {
            telinkListView.setVisibility(visible);
            listView.setVisibility(visible);
        }

    }

    private void stopAnima1() {
        if (anima1 != null) {
            anima1.stop();
        }
        ivAnima1.setVisibility(View.GONE);
    }

    private void startAnima2() {
        if (null == anima2) {
            anima2 = (AnimationDrawable) ivAnima2.getBackground();
        }
        anima2.start();
        ivAnima2.setVisibility(View.VISIBLE);
    }

    private void stopAnima2() {
        if (anima2 != null) {
            anima2.stop();
        }
        ivAnima2.setVisibility(View.GONE);
    }

    private void startAnima3() {
        if (null == anima3) {
            anima3 = (AnimationDrawable) ivAnima3.getBackground();
        }
        anima3.start();
        ivAnima3.setVisibility(View.VISIBLE);
    }

    private void stopAnima3() {
        if (anima3 != null) {
            anima3.stop();
        }
        ivAnima3.setVisibility(View.GONE);
    }

    /**
     * 获取界面跳转时的传值
     */
    private void setData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("classid")) {
            String[] classids = intent.getStringArrayExtra("classid");
            if (classids != null) {
                classFilter = Arrays.asList(classids);
            }

            type = intent.getIntExtra("type", 3);
            isTelinkAutoConnect = intent.getBooleanExtra(TelinkAutoConnectKey, false);
        }

        // 自动绑定操作
        if(isTelinkAutoConnect){
            startGroupPISKey.clear();
            endGroups.clear();
            autoConnectGroup = null;
            for (Group telinkGroup : TelinkGroupApiManager.getInstance().getTelinkGroups()) {
                startGroupPISKey.add(telinkGroup.PISKeyString);
            }

            try{
                int t1 = 0x10; int t2 = 0x03;
                // TODO LEE 添加灯组->往sdk里面添加灯组，界面收到消息后，刷新灯组
                mcm = PISManager.getInstance().getMCSObject();
                PipaRequest req = mcm.addGroup(getString(R.string.default_group_name), t1, t2);
                req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                    @Override
                    public void onRequestStart(PipaRequest req) {

                    }

                    @Override
                    public void onRequestResult(PipaRequest req) {
                        if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                            endGroups.addAll(TelinkGroupApiManager.getInstance().getTelinkGroups());
                            for (Group group : endGroups) {
                                if(startGroupPISKey.indexOf(group.PISKeyString) == -1){
                                    autoConnectGroup = group;
                                }
                            }
                            if(autoConnectGroup == null){
                                backBtn.performClick();
                            }
                        }else{
                            ToastUtils.showToast(AddBlueToothDeviceActivity.this, R.string.add_group_error_tip);
                            backBtn.performClick();
                        }
                    }
                });
                mcm.request(req);
            }catch (Exception e){
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        backBtn.performClick();
    }

    /*
     * 设置监听器
     */
    private void setListener() {
        backBtn.setOnClickListener(this);
        contentLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                int height = contentLayout.getHeight();
                if (originalHeight != height) {
                    originalHeight = height;
                    ViewGroup.LayoutParams param = stepsView.getLayoutParams();
                    param.height = originalHeight;
                    stepsView.setLayoutParams(param);
                    getLocationsOnSteps();
                }
            }
        });

        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_DISCONNECTED, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_DEVICE_ON_OFF_STATUS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_MESH_EMPTY, this);
        MyApplication.getInstance().addEventListener(com.telink.sig.mesh.light.MeshController.EVENT_TYPE_SERVICE_CREATE, this);
        MyApplication.getInstance().addEventListener(com.telink.sig.mesh.light.MeshController.EVENT_TYPE_SERVICE_DESTROY, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_PROCESSING, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_COMPLETE, this);
        MyApplication.getInstance().addEventListener(CommandEvent.EVENT_TYPE_CMD_ERROR_BUSY, this);
        MyApplication.getInstance().addEventListener(NotificationEvent.EVENT_TYPE_KICK_OUT_CONFIRM, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_AUTO_CONNECT_LOGIN, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_SUCCESS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_PROVISION_FAIL, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS, this);
        MyApplication.getInstance().addEventListener(MeshEvent.EVENT_TYPE_KEY_BIND_FAIL, this);
        MyApplication.getInstance().addEventListener(ScanEvent.DEVICE_FOUND, this);
    }

    /*
     * 初始化组件
     */
    private void initView() {
        backBtn = (Button) findViewById(R.id.title_back);
        tvTitle = (TextView) findViewById(R.id.title_name);
        ivTitle = (ImageView) findViewById(R.id.title_logo_center);
        listView = (ListView) findViewById(R.id.addbubble_list);
        telinkListView = (RecyclerView) findViewById(R.id.telink_list);
        stepsView = (StepsView) findViewById(R.id.addbubble_step);
        tvStep1 = (TextView) findViewById(R.id.addbubble_step1_tip);
        tvStep2 = (TextView) findViewById(R.id.addbubble_step2_tip);
        tvStep3 = (TextView) findViewById(R.id.addbubble_step3_tip);
        tvStep4 = (TextView) findViewById(R.id.addbubble_step4_tip);
        ivAnima1 = (ImageView) findViewById(R.id.addbubble_anima1);
        ivAnima2 = (ImageView) findViewById(R.id.addbubble_anima2);
        ivAnima3 = (ImageView) findViewById(R.id.addbubble_anima3);
        contentLayout = (RelativeLayout) findViewById(R.id.addbubble_content_layout);
        spaceView = findViewById(R.id.addbubble_space);
        setStepOneTip();
        setSteps(0, true);
        setTitle();
        setListView();
        startAnima1();


    }

    /**
     * 设置添加设备第一步提示内容
     */
    private void setStepOneTip() {
        tvStep1.setText(R.string.adddevice_searching);
//		if (type == 6) {
//			tvStep1.setText(R.string.addbubble_insole_tip);
//		} else if (type == 4) {
//			tvStep1.setText(R.string.addbubble_remoter_tip);
//		} else if (type == 3) {
//			tvStep1.setText(R.string.addbubble_colorlight_tip);
//		} else if (type == 2) {
//			tvStep1.setText(R.string.addbubble_bridge_tip);
//		} else {
//			tvStep1.setText(R.string.addbubble_step1_tip);
//		}
    }

    private void getLocationsOnSteps() {
        int[] a1 = new int[2];
        int[] a2 = new int[2];
        int[] a3 = new int[2];
        int[] a4 = new int[2];
        int distance = 0;
        tvStep2.getLocationInWindow(a2);
        tvStep3.getLocationInWindow(a3);
        tvStep4.getLocationInWindow(a4);
        stepsView.setLocation(0, 30);
        tvStep1.getLocationInWindow(a1);
        distance = a3[1] - a2[1];
        stepsView.setLocation(1, a2[1] - distance);
        stepsView.setLocation(2, a3[1] - distance);
        stepsView.setLocation(3, a4[1] - distance);
        stepsView.invalidate();

    }

    /**
     * @param index   进行到哪个一步，从0开始
     * @param success 当前的一步是否成功
     */
    private void setSteps(int index, boolean success) {
        tvStep1.setTextColor(currentColor);
        if (success) {
            if (index >= 1) {
                tvStep2.setTextColor(currentColor);
            } else {
                tvStep2.setTextColor(Color.GRAY);
            }
            if (index >= 2) {
                tvStep3.setTextColor(currentColor);
            } else {
                tvStep3.setTextColor(Color.GRAY);
            }
            if (index >= 3) {
                tvStep4.setTextColor(currentColor);
            } else {
                tvStep4.setTextColor(Color.GRAY);
            }
            tvStep2.setText(R.string.addbubble_step2_tip);
            tvStep4.setText(R.string.addbubble_step4_tip);
        } else {
            if (index > 1) {
                tvStep2.setTextColor(currentColor);
            } else {
                tvStep2.setTextColor(Color.RED);
            }
            if (index > 2) {
                tvStep3.setTextColor(currentColor);
            } else {
                tvStep3.setTextColor(Color.RED);
            }
            if (index > 3) {
                tvStep4.setTextColor(currentColor);
            } else {
                tvStep4.setTextColor(Color.RED);
            }
            if (index > 1) {
                tvStep2.setText(R.string.addbubble_step2_tip);
            } else {
                tvStep2.setText(R.string.addbubble_bind_failed);
            }
            tvStep4.setText(R.string.addbubble_config_failed);
        }
        stepsView.setResult(index, success);
    }

    /**
     * 设置listview是否可见
     */
    private void setVisiablityOnListView(boolean isVisiable) {
        if (isVisiable) {
            spaceView.setVisibility(View.GONE);
            setListViewVisible(View.VISIBLE);
        } else {
            spaceView.setVisibility(View.VISIBLE);
            setListViewVisible(View.GONE);
        }
    }

    /*
     * 设置列表的适配器
     */
    private void setListView() {
        if (blueToothDevices != null && blueToothDevices.size() > 0) {
            listView.setVisibility(View.VISIBLE);
        }
        if (TelinkApiManager.getInstance().getFoundDevices().size() > 0) {
            telinkListView.setVisibility(View.VISIBLE);
        }
        adapter = new BlueLightAdapter(this, blueToothDevices, mHandler, type);
        listView.setAdapter(adapter);
        telinkListAdapter = TelinkApiManager.getInstance().getFoundDevicesAdapter(this, mHandler);
        telinkListView.setLayoutManager(new LinearLayoutManager(this));
        telinkListView.setAdapter(telinkListAdapter);
    }

    /*
     * 设置标题名称
     */
    private void setTitle() {
        backBtn.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        ivTitle.setVisibility(View.VISIBLE);
//		tvTitle.setText(R.string.adddevice_title);
//		if (type == 6) {
//			tvTitle.setText(R.string.addbubble_insole_tip);
//		} else if (type == 4) {
//			tvTitle.setText(R.string.add_remoter_title);
//		} else if (type == 3) {
//			tvTitle.setText(R.string.addbubble_colorlight_tip);
//		} else if (type == 2) {
//			tvTitle.setText(R.string.addbubble_bridge_tip);
//		} else if (type == 1) {
//			tvTitle.setText(R.string.add_bubble_title);
//		}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                overridePendingTransition(R.anim.anim_in_from_left,
                        R.anim.anim_out_to_right);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null && !isTelinkAutoConnect) {
            controller.discoverDevices(true, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (controller != null) {
            controller.discoverDevices(false, this);
            controller.setonFeedbackListener(null);
        }
    }
    @Subscribe
    public void listenTelinkDeviceAddToGroup(TelinkOperation operation){
        if(isTelinkAutoConnect){
            if(operation.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_FAIL){
                showTelinkAutoConnectErrorAlert();
                isTelinkOnBinding = false;
            }else if(operation.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_SUCCEED){
                TelinkApiManager.getInstance().startScanTelink(isTelinkAutoConnect, mHandler);
                isTelinkOnBinding = false;
            }
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        clearData();
        TelinkApiManager.getInstance().stopScan();

        checkTelinkAutoConnectGroup();
    }

    /**
     * 检测Group组是否有添加灯具成功
     */
    private void checkTelinkAutoConnectGroup(){
        if(autoConnectGroup != null){
            List<DeviceInfo> deviceInfos = TelinkGroupApiManager.getInstance().getDevicesInGroup(autoConnectGroup.address);
            if(deviceInfos.size() == 0){
                deleteGroup(autoConnectGroup);
            }
        }
    }

    private void deleteGroup(Group group){
        PISBase infor = PISManager.getInstance().getPISObject(group.PISKeyString);
        if(infor == null) return;
        PipaRequest req = mcm.removeGroup(infor.getGroupId());
        req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
            @Override
            public void onRequestStart(PipaRequest req) {
            }

            @Override
            public void onRequestResult(PipaRequest req) {
            }
        });
        mcm.request(req);
    }

    /**
     * 清理缓存数据
     */
    private void clearData() {
        MeshService.getInstance().idle(true);
        if (mAppearances != null) {
            mAppearances.clear();
            mAppearances = null;
        }
        if (blueToothDevices != null) {
            blueToothDevices.clear();
            blueToothDevices = null;
        }
        if (tempList != null) {
            tempList.clear();
            tempList = null;
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void newUuid(UUID uuid, int uuidHash, int rssi, int ttl) {
        try {
            BlueToothBubble infor = tempList.get(uuidHash);
            if (infor == null) {
                infor = new BlueToothBubble(uuid.toString().toUpperCase(),
                        rssi, uuidHash, ttl);
                tempList.append(uuidHash, infor);
            } else {
                infor.rssi = rssi;
                infor.ttl = ttl;
            }
            if (mAppearances.containsKey(uuidHash)) {
                infor.setAppearance(mAppearances.get(uuidHash));
            }
        } catch (NullPointerException e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        }
    }

    @Override
    public void newAppearance(final int uuidHash, byte[] appearance, String shortName) {
        if (mAppearances == null)
            mAppearances = new HashMap<>();
        Appearance tmpApperance = new Appearance(appearance, shortName);
        mAppearances.put(uuidHash, tmpApperance);
        LogUtils.i(TAG, "new device finded : " + tmpApperance.macAddr);
        final BlueToothBubble infor = tempList.get(uuidHash);
        if (infor == null)
            return;

        infor.setAppearance(tmpApperance);
        infor.updated();

        if (TextUtils.isEmpty(macs.get(infor.appearance.macAddr))) {
            HttpDeviceInfo httpDevice = new HttpDeviceInfo();
            HttpDeviceInfo.classidHttpRequest req = httpDevice.updateClassId(infor.appearance.macAddr);
            req.setOnPipaRequestStatusListener(new HttpRequest.OnPipaRequestStatusListener() {
                @Override
                public void onRequestStart(HttpRequest req) {

                }

                @Override
                public void onRequestResult(HttpRequest req) {
                    if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                        HttpDeviceInfo.classidHttpRequest clsReq = (HttpDeviceInfo.classidHttpRequest) req;
                        infor.setClassId(clsReq.classId);
                        infor.state = true;
                        if (TextUtils.isEmpty(macs.get(infor.appearance.macAddr)))
                            macs.put(infor.getAppearance().macAddr, infor.getClassId());
                        updateView(infor, uuidHash);
                    }
                }
            });
            PISHttpManager.getInstance(this).request(req);
        } else {
            infor.setClassId(macs.get(infor.appearance.macAddr));
            infor.state = true;
            updateView(infor, uuidHash);
        }
    }

    @Override
    public void deviceAssociated(int uuidHash, boolean success) {
        if (success) {
            mHandler.obtainMessage(MSG_CONFIG_SUCCESS, uuidHash, -1)
                    .sendToTarget();
        } else {
            mHandler.obtainMessage(MSG_CONFIG_FAILED, uuidHash, -1)
                    .sendToTarget();
        }
        LogUtils.i("MeshController", "success = " + success);
    }

    @Override
    public void associationProgress(int progress, String message) {

    }

    private void refreshLastTelinkStatusUpdateTime(){
        if(isTelinkAutoConnect){
            lastTelinkStatusUpdateTime = SystemClock.elapsedRealtime();
        }
    }

    /**
     * 自动绑定失败
     */
    private void showTelinkAutoConnectErrorAlert(){
        if(!isTelinkAutoConnect)return;
        try {
            mHandler.removeCallbacks(checkTelinkAutoConnectRunnable);
            Dialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.addbubble_bind_failed)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            backBtn.performClick();
                        }
                    })
                    .setCancelable(false)
                    .create();
            alertDialog.show();
        }catch (Exception e){
            PgyCrashManager.reportCaughtException(this, e);
        }
    }



    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case MeshEvent.EVENT_TYPE_PROVISION_SUCCESS:
                mHandler.sendEmptyMessage(MSG_TELINK_DEVBIND_SUCCESS);
                refreshLastTelinkStatusUpdateTime();
                break;
            case MeshEvent.EVENT_TYPE_PROVISION_FAIL:
                mHandler.sendEmptyMessage(MSG_TELINK_DEVBIND_FAILED);
                if(!isTelinkAutoConnect){
                    showTelinkAutoConnectErrorAlert();
                }
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_SUCCESS:
                refreshLastTelinkStatusUpdateTime();
                mHandler.sendEmptyMessage(MSG_TELINK_CONFIG_SUCCESS);
                TelinkApiManager.getInstance().autoConnectToDevices(this);
                break;
            case MeshEvent.EVENT_TYPE_KEY_BIND_FAIL:
                mHandler.sendEmptyMessage(MSG_TELINK_CONFIG_FAILED);
                if(!isTelinkAutoConnect){
                    showTelinkAutoConnectErrorAlert();
                }
                break;
            case ScanEvent.DEVICE_FOUND:
                refreshLastTelinkStatusUpdateTime();
                break;
        }
    }

    public class Appearance {
        private byte[] mAppearanceCode;
        private String mShortName;
        public String macAddr;

        public Appearance(String mac) {
            this.macAddr = mac;
        }

        public Appearance(byte[] appearanceCode, String shortName) {
            setAppearanceCode(appearanceCode);
            setMacAddress(shortName);
            setShortName(shortName);
        }

        private void setMacAddress(String shortName) {
            if (shortName != null && shortName.length() >= 12) {
                macAddr = shortName.substring(0, 12);
                macAddr = "0000" + macAddr;
            } else {
                macAddr = null;
            }
        }

        public String getShortName() {
            return mShortName;
        }

        public void setShortName(String mShortName) {
            setMacAddress(mShortName);
            this.mShortName = mShortName;
        }

        public byte[] getAppearanceCode() {
            return mAppearanceCode;
        }

        public void setAppearanceCode(byte[] mAppearanceCode) {
            this.mAppearanceCode = mAppearanceCode;
        }
    }

    /**
     * 刷新界面
     *
     * @param infor
     * @param uuidHash
     */
    @SuppressLint("DefaultLocale")
    private void updateView(BlueToothBubble infor, int uuidHash) {
        if (null == blueToothDevices) {
            blueToothDevices = new SparseArray<>();
        }
        if (infor == null)
            return;

        if (classFilter != null && !classFilter.contains(infor.getClassId()))
            return;

        BlueToothBubble bubble = blueToothDevices.get(uuidHash);
        if (bubble != null)
            blueToothDevices.remove(uuidHash);
        blueToothDevices.put(uuidHash, infor);
        mHandler.sendEmptyMessage(MSG_UPDATEVIEW);

    }

}