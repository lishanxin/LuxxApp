package net.senink.seninkapp.ui.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;
import com.telink.sig.mesh.model.SigMeshModel;

import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
import net.senink.seninkapp.adapter.LightDetailAdapter;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISLightRGB;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PipaRequest;
//import com.senink.seninkapp.core.PISManager.onPISChangedListener;
//import com.senink.seninkapp.core.PipaRequest.OnPipaRequestStatusListener;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import com.senink.seninkapp.crmesh.MeshController;
//import com.senink.seninkapp.crmesh.MeshController.onFeedbackListener;
import net.senink.seninkapp.telink.api.TelinkApiManager;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.telink.model.TelinkOperation;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

// TODO LEE 灯组设置。灯的设置是否也进入这个界面，待验证
public class LightSettingActivity extends BaseActivity implements
        View.OnClickListener {
    // 请求码
    private static final int REQUEST_CODE = 10;
    private static final int MSG_DELETE_FAILED = 11;

    /**
     * 跳转至 LightEditActivity
     */
    public static final int REQUEST_GROUP_ADD = 1;
    public static final int REQUEST_NAME_MOD = 2;
    // 删除组失败
    private static final int MSG_DELETE_GROUP_FAILED = 14;

    // 标题名称
    private TextView tvTitle;
    private ImageView ivTitle;
    // 标题的中的返回按钮
    private Button backBtn;
    // 删除按钮
    private TextView deleteBtn;
    // 灯的名称
    private TextView nameBtn;
    // 名字的布局
    private RelativeLayout nameLayout;
    // 位置
//	private TextView locationBtn;
    // 未知的布局
//	private RelativeLayout locationLayout;
//	private RelativeLayout sceneLayout;
    //仅仅更新状态
    private boolean needRequest = true;
    // 开关状态
    private CheckBox switcher;
    // 组的布局
    private RelativeLayout groupLayout;
    private ImageButton editBtn;
    // 组列表
    private SwipeMenuListView listView;
    // 场景的添加按钮
//	private ImageButton addSceneBtn;
    //滚动组件
//	private ScrollView scoller;
    // 场景列表
//	private SwipeMenuListView sceneListView;
    // 适配器
    private LightDetailAdapter adapter;
    // 传递过来的pisbase对象
    private PISBase infor;
    // 灯所在的位置
//	private String location = null;
    // 分组信息的集合
//	private LightInforReciever lightReciever;
    // 从数据库中获取场景信息的类
//	private SceneDao mSceneDao;
    // 场景的适配器
//	private ScenesAdapter mSceneAdapter;
    // 场景模式的缓存管理类
//	private CacheManager cacheManger;
    private PISManager manager = null;
    // 获取分组信息的类
    private PISMCSManager mcm;
    // 蓝牙管理器
    private MeshController controller;
    // 是否从修改名称界面跳转过来
    private boolean isChangedOnModifyName = false;
    // 是否是从其他界面返回
//	private boolean isBack = false;
    // 加载框布局
    private RelativeLayout loaddingLayout;
    // 布局显示内容
    private ImageView ivLoading;
    private AnimationDrawable anima;

    private boolean isTelink = false;
    private boolean isTelinkGroup = false;
    private int telinkAddress = 0;
    private Group telinkGroup;
    private DeviceInfo deviceInfo;
    private int hslEleAdr;

    private int selectDeletePosition = -1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_DELETE_FAILED:
                    ToastUtils.showToast(LightSettingActivity.this,
                            R.string.del_smart_fail);
                    break;
                case MessageModel.MSG_GET_LOCATIONS: {
//				if (isLED) {
//					if (inforOnLED != null) {
//						setLocation(inforOnLED.mLocation,
//								(int) inforOnLED.mLocation);
//					}
//				} else {
//					if (infor != null) {
//						setLocation(infor.mLocation, (int) (infor.mLocation));
//					}
//				}
                }
                break;
                case MSG_DELETE_GROUP_FAILED: {
                    removeMessages(MSG_DELETE_GROUP_FAILED);

                    ToastUtils.showToast(LightSettingActivity.this,
                            R.string.del_smart_fail);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightsetting);
        manager = PISManager.getInstance();
        controller = MeshController.getInstance(this);
        EventBus.getDefault().register(this);
//		cacheManger = CacheManager.getInstance();
//		mSceneDao = new SceneDao(this);
        setData();
        initView();
//		setReciever();
        setListener();
        if (adapter == null)
            adapter = new LightDetailAdapter(this, infor);
        listView.setAdapter(adapter);
    }


    /**
     * 注册广播
     */
//	private void setReciever() {
//		lightReciever = new LightInforReciever();
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(MessageModel.ACTION_CHANGE_POSITION);
//		registerReceiver(lightReciever, filter);
//	}

    /**
     * 获取传值
     */
    private void setData() {

        Intent intent = getIntent();
        if (intent != null) {
            String pisKey = intent.getStringExtra(MessageModel.PISBASE_KEYSTR);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isTelink = bundle.getBoolean(TelinkApiManager.IS_TELINK_KEY, false);
                isTelinkGroup = bundle.getBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, false);
                telinkAddress = bundle.getInt(TelinkApiManager.TELINK_ADDRESS, 0);
                telinkGroup = isTelinkGroup ? TelinkGroupApiManager.getInstance().getGroupByAddress(telinkAddress) : null;
                if (isTelinkGroup && telinkGroup != null) {
                    hslEleAdr = telinkGroup.address;
                    pisKey = telinkGroup.PISKeyString;
                } else if (isTelink) {
                    deviceInfo = MyApplication.getInstance().getMesh().getDeviceByMeshAddress(telinkAddress);
                    if(deviceInfo == null){
                        finish();
                        return;
                    }
                    hslEleAdr = deviceInfo.getTargetEleAdr(SigMeshModel.SIG_MD_LIGHT_HSL_S.modelId);
                }
            }

            setPisData(pisKey);
        }
    }

    private void setPisData(String pisKey) {
        if (pisKey != null) {
            infor = manager.getPISObject(pisKey);

            try {
                if (infor != null) {
                    //更新组信息
                    PipaRequest req = infor.updateGroupInfo();
                    req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {

                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                                updateGroupInfo();
                        }
                    });
                    infor.request(req);
                } else {
                    if(!isTelinkGroup){
                        finish();
                    }
                }
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
        }
    }

    @Subscribe
    public void reSetTelinkGroupData(TelinkOperation opr) {
        if (opr.getOpr() == TelinkOperation.REFRESH_GROUP_DATA) {
            refreshViews();
        } else if (opr.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_SUCCEED) {
            adapter.removeView(selectDeletePosition);
            selectDeletePosition = -1;
            updateGroupInfo();
            hideLoadingDialog();
        } else if (opr.getOpr() == TelinkOperation.DEVICE_BIND_OR_UNBIND_GROUP_FAIL) {
            ToastUtils.showToast(LightSettingActivity.this, R.string.lightgroup_delete_group_failed);
            hideLoadingDialog();
            selectDeletePosition = -1;
        }
    }

    private void updateLightInfo() {
        tvTitle.setText(R.string.setting);
        if (infor != null) {
            nameBtn.setText(infor.getName());
//		setLocation(infor.getLocation());
            try {
                if (infor instanceof PISXinLight) {
                    needRequest = false;
                    switcher.setChecked(((PISXinLight) infor).getLightStatus() == PISXinLight.XINLIGHT_STATUS_ON);
                    needRequest = true;
                }
                if (infor instanceof PISxinColor) {
                    needRequest = false;
                    switcher.setChecked(((PISxinColor) infor).getLightStatus() == PISxinColor.XINCOLOR_STATUS_ON);
                    needRequest = true;
                }
            } catch (Exception e) {
                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
            }
        } else {
            if (telinkGroup != null) {
                nameBtn.setText(telinkGroup.name);
                switcher.setChecked(TelinkGroupApiManager.getInstance().isGroupOn(telinkGroup));
            } else {
                if (deviceInfo != null) {
                    nameBtn.setText(deviceInfo.getDeviceName());
                    switcher.setChecked(deviceInfo.getOnOff() == 1);
                }
            }
        }

    }

    private void updateGroupInfo() {
        try {
            SparseArray<GeneralDeviceModel> generalDeviceModels = new SparseArray<>();
            if (isTelink) {
                if (isTelinkGroup) {
                    List<DeviceInfo> telinkDevice = TelinkGroupApiManager.getInstance().getDevicesInGroup(telinkAddress);
                    for (int i = 0; i < telinkDevice.size(); i++) {
                        generalDeviceModels.put(i, new GeneralDeviceModel(new TelinkBase(telinkDevice.get(i))));
                    }
                } else {
                    List<Group> telinkGroups = TelinkGroupApiManager.getInstance().getGroupsWithDevice(telinkAddress);
                    for (int i = 0; i < telinkGroups.size(); i++) {
                        generalDeviceModels.put(i, new GeneralDeviceModel(new TelinkBase(telinkGroups.get(i))));
                    }
                }
            }
            addPISDevice(generalDeviceModels);
            adapter.setList(generalDeviceModels);
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
        }
        adapter.notifyDataSetChanged();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.invalidate();
            }
        }, 1000);
    }

    private void addPISDevice(SparseArray<GeneralDeviceModel> generalDeviceModels) {
        if (infor == null) return;
        List<PISBase> gObjs = infor.getGroupObjects();
        if (gObjs == null || gObjs.size() == 0) {
            return;
        }
        int telinkSize = generalDeviceModels.size();
        for (int i = 0; i < gObjs.size(); i++) {
            generalDeviceModels.put(telinkSize + i, new GeneralDeviceModel(gObjs.get(i)));
        }
    }

    /**
     * 显示加载动画
     */
    private void showLoadingDialog() {
        if (null == anima) {
            anima = (AnimationDrawable) ivLoading.getBackground();
        }
        anima.start();
        loaddingLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示加载动画
     */
    private void hideLoadingDialog() {
        if (anima != null) {
            anima.stop();
        }
        loaddingLayout.setVisibility(View.GONE);
    }

    /**
     * 设置监听器
     */
//	private boolean isChangedOnModifyName
    private void setListener() {
        setListViewListener();
//		locationLayout.setOnClickListener(this);
        nameLayout.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        nameBtn.setOnClickListener(this);
//		locationBtn.setOnClickListener(this);
//		addSceneBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        groupLayout.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        switcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!switcher.isPressed())
                    return;
//				if (!needRequest)
//					return;
                if (isTelink) {
                    TelinkApiManager.getInstance().setSwitchLightOnOff(hslEleAdr, isChecked);
                }
                if(infor == null) return;
                PipaRequest req = null;
                if (infor instanceof PISXinLight) {
                    req = ((PISXinLight) infor).commitLightStatus(isChecked);
                }
                if (infor instanceof PISxinColor) {
                    req = ((PISxinColor) infor).commitLightOnOff(isChecked);
                }
                if (req != null) {
                    req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                        @Override
                        public void onRequestStart(PipaRequest req) {
                            //开始操作动画
                        }

                        @Override
                        public void onRequestResult(PipaRequest req) {
                            if (req.errorCode != PipaRequest.REQUEST_RESULT_SUCCESSED) {
                                //提示错误信息
                                ToastUtils.showToast(LightSettingActivity.this,
                                        R.string.switch_set_failed);
                            }
                            if (infor instanceof PISXinLight) {
                                needRequest = false;
                                switcher.setChecked(((PISXinLight) infor).getLightStatus() == PISXinLight.XINLIGHT_STATUS_ON);
                                needRequest = true;
                            }
                            if (infor instanceof PISxinColor) {
                                needRequest = false;
                                switcher.setChecked(((PISxinColor) infor).getLightStatus() == PISxinColor.XINCOLOR_STATUS_ON);
                                needRequest = true;
                            }

                        }
                    });
                    req.NeedAck = true;
                    infor.request(req);
                }
            }
        });
//		setSceneListViewListener();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tvTitle = (TextView) findViewById(R.id.title_name);
        ivTitle = (ImageView) findViewById(R.id.title_logo_center);
        backBtn = (Button) findViewById(R.id.title_back);
        nameBtn = (TextView) findViewById(R.id.lightsetting_lightname);
        nameLayout = (RelativeLayout) findViewById(R.id.lightsetting_name_layout);
        switcher = (CheckBox) findViewById(R.id.lightsetting_switch);
        listView = (SwipeMenuListView) findViewById(R.id.lightsetting_grouplist);
        editBtn = (ImageButton) findViewById(R.id.lightsetting_edit);
        groupLayout = (RelativeLayout) findViewById(R.id.lightsetting_groupinfor_layout);

        loaddingLayout = (RelativeLayout) findViewById(R.id.lightsetting_loading_layout);
        ivLoading = (ImageView) findViewById(R.id.lightsetting_loading);
        // 设置标题内容
        setTitle();
        //删除按钮
        deleteBtn = (TextView) findViewById(R.id.title_delete);
        if (isTelink) {
            if (isTelinkGroup) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }
        } else {
            if (infor.ServiceType != PISBase.SERVICE_TYPE_GROUP) {
                deleteBtn.setVisibility(View.GONE);
            } else {
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置标题的组件
     */
    private void setTitle() {
        backBtn.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.GONE);
        ivTitle.setVisibility(View.VISIBLE);
    }

    private void backActivity(int resultCode) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        intent.putExtra("result", resultCode);

        //设置返回数据
        LightSettingActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        LightSettingActivity.this.finish();
        overridePendingTransition(R.anim.anim_in_from_left,
                R.anim.anim_out_to_right);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            // 返回按钮
            case R.id.title_back:
//			refreshScenes();
                backActivity(1);
                break;
            case R.id.lightsetting_edit:
            case R.id.lightsetting_groupinfor_layout: {
                //			isBack = true;
                intent = new Intent(LightSettingActivity.this,
                        LightEditActivity.class);
                    if(infor != null){
                        intent.putExtra(MessageModel.PISBASE_KEYSTR,
                                infor.getPISKeyString());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(TelinkApiManager.TELINK_ADDRESS, telinkAddress);
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_KEY, isTelink);
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, isTelinkGroup);
                    intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_GROUP_ADD);

//			LightSettingActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);
            }
            break;
            // 设置灯的名称
            case R.id.lightsetting_lightname:
            case R.id.lightsetting_name_layout: {
                isChangedOnModifyName = true;
                Bundle bundle = new Bundle();
                intent = new Intent(LightSettingActivity.this,
                        ModifyNameActivity.class);
                if (isTelink) {
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_KEY, isTelink);
                    bundle.putBoolean(TelinkApiManager.IS_TELINK_GROUP_KEY, isTelinkGroup);
                    bundle.putInt(TelinkApiManager.TELINK_ADDRESS, telinkAddress);
                    intent.putExtras(bundle);
                } else if (infor != null) {
                    intent.putExtra(MessageModel.PISBASE_KEYSTR,
                            infor.getPISKeyString());
                }

                startActivityForResult(intent, REQUEST_NAME_MOD);
                overridePendingTransition(R.anim.anim_in_from_right,
                        R.anim.anim_out_to_left);
            }
            break;
            case R.id.title_delete: {
                mcm = PISManager.getInstance().getMCSObject();
                if (telinkGroup != null) {
                    if (adapter != null && adapter.getCount() > 0) {
                        deleteGroupByAlert();
                    } else {
                        TelinkGroupApiManager.getInstance().deleteGroup(telinkGroup.address);
                        if (telinkGroup.PISKeyString != null) {
                            PISBase infor = PISManager.getInstance().getPISObject(telinkGroup.PISKeyString);
                            if (infor != null) {
                                deletePISGroup(infor);
                            } else {
                                backActivity(RESULT_CODE);
                            }
                        } else {
                            backActivity(RESULT_CODE);
                        }
                    }
                    return;
                }
                if (infor != null) {
                    if (infor.getGroupObjects().size() > 0) {
//					ToastUtils.showToast(LightSettingActivity.this, R.string.lightgroup_delete_tips);
                        deleteGroupByAlert();
                    } else {
                        deletePISGroup(infor);
                    }
                }

            }
            break;
            default:
                break;
        }
    }

    private static final int RESULT_CODE = 3;


    private void deleteGroupByAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LightSettingActivity.this);
        builder.setTitle(R.string.lightgroup_notice)
                .setIcon(R.drawable.luxx_login_icon)
                .setMessage(R.string.lightgroup_delete_tips)
                .setPositiveButton(R.string.lightgroup_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (telinkGroup != null) {
                            TelinkGroupApiManager.getInstance().deleteGroup(telinkGroup.address);
                        } else {
                            if (infor != null) {
                                TelinkGroupApiManager.getInstance().deleteTelinkGroupByPisKey(infor.getPISKeyString());
                            }
                        }
                        if (infor != null) {
                            deletePISGroup(infor);
                        } else {
                            backActivity(RESULT_CODE);
                        }
                    }
                })
                .setNegativeButton(R.string.lightgroup_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deletePISGroup(PISBase infor) {
        PipaRequest req = mcm.removeGroup(infor.getGroupId());
        req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
            @Override
            public void onRequestStart(PipaRequest req) {
                showLoadingDialog();
            }

            @Override
            public void onRequestResult(PipaRequest req) {
                mHandler.removeMessages(MSG_DELETE_GROUP_FAILED);
                hideLoadingDialog();
                if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED) {
                    backActivity(RESULT_CODE);
                } else {
                    mHandler.removeMessages(MSG_DELETE_GROUP_FAILED);
                    mHandler.sendEmptyMessage(MSG_DELETE_GROUP_FAILED);
                }
            }
        });
        mcm.request(req);
        mHandler.sendEmptyMessageDelayed(MSG_DELETE_GROUP_FAILED,
                Constant.TIMEOUT_TIME);
    }

    /**
     * 设置设备列表的监听器
     */
    private void setListViewListener() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(0xFFFD430A));
                deleteItem.setWidth(Utils.dpToPx(50,
                        LightSettingActivity.this.getResources()));
                deleteItem.setTitle(R.string.delete);
                deleteItem.setTitleSize(12);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0:
                        GeneralDeviceModel generalDeviceModel = adapter.getGroupObject(position);
                        if (generalDeviceModel.isTelink()) {
                            // todo lee 解绑设备操作
                            showLoadingDialog();
                            TelinkBase telinkBase = generalDeviceModel.getTelinkBase();

                            if (telinkBase.isDevice()) {
                                if (isTelinkGroup && telinkGroup != null) {
                                    TelinkGroupApiManager.getInstance().deleteDeviceFromGroup(telinkGroup.address, telinkBase.getDevice().meshAddress);
                                }
                            } else {
                                if (deviceInfo != null) {
                                    TelinkGroupApiManager.getInstance().deleteDeviceFromGroup(telinkBase.getGroup().address, deviceInfo.meshAddress);
                                }
                            }

                        } else {
                            PISBase obj = generalDeviceModel.getPisBase();
                            try {
                                PipaRequest req;
                                PISBase pis;
                                if (infor != null) {
                                    if (obj.ServiceType == PISBase.SERVICE_TYPE_GROUP) {
                                        pis = infor;
                                        req = pis.removeFromGroup(obj);
                                    } else {
                                        pis = obj;
                                        req = pis.removeFromGroup(infor);
                                    }
                                    req.setOnPipaRequestStatusListener(new PipaRequest.OnPipaRequestStatusListener() {
                                        @Override
                                        public void onRequestStart(PipaRequest req) {
                                            showLoadingDialog();
                                        }

                                        @Override
                                        public void onRequestResult(PipaRequest req) {
                                            hideLoadingDialog();
                                            if (req.errorCode == PipaRequest.REQUEST_RESULT_SUCCESSED)
                                                adapter.removeView(position);
                                            else
                                                ToastUtils.showToast(LightSettingActivity.this, R.string.lightgroup_delete_group_failed);
                                            updateGroupInfo();

                                        }
                                    });
                                    req.NeedAck = true;
                                    pis.request(req);
                                }
                            } catch (Exception e) {
                                PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
                                ToastUtils.showToast(LightSettingActivity.this, R.string.lightgroup_delete_group_failed);
                            }
                        }

                        break;
                }
                return false;
            }
        });

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (loaddingLayout.getVisibility() == View.VISIBLE) {
            if (ev.getAction() == MotionEvent.BUTTON_BACK) {
                hideLoadingDialog();
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshViews();
    }

    /**
     * 刷新数据和组件
     */
    private void refreshViews() {
//		updateScene();
        updateLightInfo();
        updateGroupInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
//				if (infor instanceof PISXinLight) {
//					mSceneAdapter.setList(cacheManger.getLEDScenes());
//				} else if (infor instanceof  PISxinColor){
//					mSceneAdapter.setList(cacheManger.getRGBScenes());
//				}
//				mSceneAdapter.notifyDataSetChanged();
                break;
            case REQUEST_GROUP_ADD:
                if (infor == null) return;
                // TODO LEE 添加完成后的回调
                if (infor.ServiceType == PISBase.SERVICE_TYPE_GROUP) {
                    List<PISBase> srvs = infor.getGroupObjects();
                    if (srvs.size() > 0)    // NextApp.tw
                    {
                        if (srvs.get(0).getT2() == 0x05 && infor.getName().length() >= 9) {
                            String tmp1 = infor.getName().substring(0, 9);
                            String tmp2 = "new group";
                            if (tmp1.equals(tmp2)) {
                                PISMCSManager manager = PISManager.getInstance().getMCSObject();
                                PipaRequest req = manager.modifyGroup(
                                        infor.getGroupId(), "Candle", (byte) (infor.getT1() & 0xFF), (byte) (infor.getT2() & 0xFF));
                                manager.request(req);
                            }
                        }
                    }
                }
                updateGroupInfo();
                updateLightInfo();
                break;
            case REQUEST_NAME_MOD:
                updateLightInfo();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshViews();
    }

    @Override
    protected void onDestroy() {
//		unregisterReceiver(lightReciever);
        mHandler.removeMessages(MessageModel.MSG_GET_DEVICES);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        backBtn.performClick();
    }

//	/**
//	 * 设置位置
//	 *
//	 * @param pos
//	 */
//	private void setLocation(int pos) {
//		try{
//			String strLocation = PISManager.getInstance().getLocations().get(pos);
//			if (location != null && strLocation != null) {
//				locationBtn.setText(strLocation);
//				infor.setLocation(pos);
//			} else {
//				locationBtn.setText(R.string.no_know);
//			}
//		}catch (Exception e){
//			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
//			locationBtn.setText(R.string.no_know);
//		}
//	}
//
//	private class LightInforReciever extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (MessageModel.ACTION_CHANGE_POSITION.equals(action)) {
//				byte position = intent.getByteExtra("position", (byte) 0);
//				int pos = (int) (position & 0xFF);
//				setLocation(pos);
//
//				new Thread() {
//					@Override
//					public void run() {
////						PISManager.locations
//						List<LocationName> locations = HttpUtils
//								.getLocationAndSave(LightSettingActivity.this);
//						if (PISManager.getInstance().getLocations() != null
//								&& PISManager.getInstance().getLocations().size() > 0) {
//							mHandler.sendEmptyMessage(MessageModel.MSG_GET_LOCATIONS);
//						}
//					}
//				}.start();
//			}
//		}
//	}
}