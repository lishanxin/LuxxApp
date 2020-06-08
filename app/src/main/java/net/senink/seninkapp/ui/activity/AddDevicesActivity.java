package net.senink.seninkapp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pinm.PINMoBLE.MeshController;
import net.senink.piservice.pinm.PinmInterface;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISMCSManager;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.LuxxMusicColor;
import net.senink.piservice.services.PISxinColor;
import net.senink.piservice.struct.PIServiceInfo;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.BuildConfig;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISMCSManager;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.crmesh.MeshController;
import net.senink.seninkapp.adapter.LightListAdapter;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.home.HomeActivity;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshBase;
import net.senink.seninkapp.ui.view.pulltorefreshlistview.PullToRefreshListView;

import org.spongycastle.LICENSE;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于添加不同类型的设备
 * 
 * @author zhaojunfeng
 * @date 2015-09-29
 */
public class AddDevicesActivity extends BaseActivity implements View.OnClickListener {
	 private final static String TAG = "AddDevicesActivity";
	// 按钮
	private TextView tvDevice;
	private TextView tvGroup;

	private ImageButton ibDevice;
	private ImageButton ibGroup;
	private RelativeLayout rlPage;

//	private ListView newDeviceListView;
//	private NewDeviceListAdapter deviceAdapter;

//	private ListView newGroupListView;
//	private NewGroupListAdapter groupAdapter;

	private PISMCSManager mcm;
	private PISManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_new_device);
			manager = PISManager.getInstance();
			mcm = manager.getMCSObject();


			tvDevice = (TextView) findViewById(R.id.new_light_title);
			tvGroup = (TextView) findViewById(R.id.new_group_title);

			ibDevice = (ImageButton) findViewById(R.id.new_light);
			ibGroup = (ImageButton) findViewById(R.id.new_group);

			rlPage = (RelativeLayout) findViewById(R.id.activity_new_device);

			tvDevice.setText(R.string.new_light_name);
			tvGroup.setText(R.string.new_group_name);
			ibDevice.setOnClickListener(this);
			ibGroup.setOnClickListener(this);
			rlPage.setOnClickListener(this);
//			ibDevice.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(AddDevicesActivity.this,
//							AddBlueToothDeviceActivity.class);
//					String[] classidFilter;
//					classidFilter = new String[1];
//					classidFilter[0] = ProductClassifyInfo.CLASSID_DEFAULT;;
//					intent.putExtra("classid", classidFilter);
//					if (intent != null) {
//						startActivityForResult(intent, 1008);
//						overridePendingTransition(R.anim.anim_in_from_right,
//								R.anim.anim_out_to_left);
//					}
//				}
//			});
//
//			ibGroup.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					addGroups(0x13, 0x03);
//				}
//			});

		}catch (Exception e){
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_in_from_left,
				R.anim.anim_out_to_right);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.new_light: {  // TODO LEE 添加灯
				Intent intent = new Intent(AddDevicesActivity.this,
						AddBlueToothDeviceActivity.class);
				String[] classidFilter;
				classidFilter = new String[1];
				classidFilter[0] = ProductClassifyInfo.CLASSID_DEFAULT;;
				intent.putExtra("classid", classidFilter);
				if (intent != null) {
					startActivityForResult(intent, 1008);
					overridePendingTransition(R.anim.anim_in_from_right,
							R.anim.anim_out_to_left);
				}
			}
				break;
			case R.id.new_group:// TODO 添加灯组
				addGroups(0x10, 0x03);
				break;
			default:
				super.onBackPressed();
				overridePendingTransition(R.anim.anim_in_from_left,
						R.anim.anim_out_to_right);
				break;
		}
	}

	private void addGroups(int t1, int t2) {
		try{
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
						finish();
						overridePendingTransition(R.anim.anim_in_from_left,
								R.anim.anim_out_to_right);
						// TODO LEE 需要把两个灯组合并成一个。
						TelinkGroupApiManager.getInstance().addGroup(getString(R.string.default_group_name), req.object);
					}else
						ToastUtils.showToast(AddDevicesActivity.this, R.string.add_group_error_tip);

				}
			});
			mcm.request(req);
		}catch (Exception e){
			PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
		}
	}

	public class NewDeviceListAdapter extends BaseAdapter {
		private final int ITEM_COUNT_OF_LINE = 4;
		// 存放设备信息的集合
		private List<List<String>> bases;
		private HashMap<String, ImageButton> btnClassifies;
		private HashMap<String, TextView> tvClassifies;
		// 布局引用器
		private LayoutInflater inflater = null;

		public NewDeviceListAdapter(Activity context, List<String> newDevices) {
			inflater = LayoutInflater.from(context);
			setList(newDevices);
		}

		/**
		 * 更新信息
		 *
		 * @param newDevices
		 *  是否需要发送请求获取开关状态
		 */
		public void setList(List<String> newDevices) {
			//需要格式化成每行三个设备的ListItem
			try {
				if (bases == null)
					bases = new ArrayList<>();
				if (newDevices == null)
					return;
				bases.clear();
				List<String> clsItem = null;
				for (String clsStr : newDevices){
					int resid;
					if (this instanceof NewGroupListAdapter){
						resid = ProductClassifyInfo.getGroupResourceId(clsStr);
						if (resid == R.drawable.grp_default_selector)
							continue;
					}
					else{
						resid = ProductClassifyInfo.getProductResourceId(clsStr);
						if (R.drawable.pro_default_selector == resid)
							continue;
					}
					if (clsStr.compareTo(ProductClassifyInfo.CLASSID_DEFAULT)==0)
						continue;
					if (clsItem == null)
						clsItem = new ArrayList<>();
					clsItem.add(clsStr);
					if (clsItem.size() == ITEM_COUNT_OF_LINE){
						bases.add(clsItem);
						clsItem = null;
					}
				}
				if (clsItem != null && clsItem.size() > 0)
					bases.add(clsItem);
			}catch (Exception e){
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		}

		public List<String> getList() {
			List<String> result = new ArrayList<>();

			for (List<String> item : bases){
				for (String strCls : item)
					result.add(strCls);
			}
			return result;
		}


		@Override
		public int getCount() {
			return bases.size();
		}

		@Override
		public Object getItem(int position) {
			return bases.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			List<String> clsItem = bases.get(position);
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.new_device_listitem, null);
				holder = new ViewHolder();
				holder.rootLayout = (LinearLayout)convertView.findViewById(R.id.adddevices_content_layout);

				holder.itemLayout[0] = (RelativeLayout)convertView.findViewById(R.id.newdevice_item1);
				holder.btn[0] = (ImageButton)convertView.findViewById(R.id.newdevice_btn1);
				holder.tv[0] = (TextView)convertView.findViewById(R.id.newdevice_tv1);

				holder.itemLayout[1] = (RelativeLayout)convertView.findViewById(R.id.newdevice_item2);
				holder.btn[1] = (ImageButton)convertView.findViewById(R.id.newdevice_btn2);
				holder.tv[1] = (TextView)convertView.findViewById(R.id.newdevice_tv2);

				holder.itemLayout[2] = (RelativeLayout)convertView.findViewById(R.id.newdevice_item3);
				holder.btn[2] = (ImageButton)convertView.findViewById(R.id.newdevice_btn3);
				holder.tv[2] = (TextView)convertView.findViewById(R.id.newdevice_tv3);

				holder.itemLayout[3] = (RelativeLayout)convertView.findViewById(R.id.newdevice_item4);
				holder.btn[3] = (ImageButton)convertView.findViewById(R.id.newdevice_btn4);
				holder.tv[3] = (TextView)convertView.findViewById(R.id.newdevice_tv4);

				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}

			if (clsItem != null && clsItem.size() > 0) {
				int len = clsItem.size();

				setLightView(holder, len > 0? clsItem.get(0) : null, position, 0);
				setLightView(holder, len > 1? clsItem.get(1) : null, position, 1);
				setLightView(holder, len > 2? clsItem.get(2) : null, position, 2);
				setLightView(holder, len > 3? clsItem.get(3) : null, position, 3);

			} else {
				convertView.setVisibility(View.GONE);
			}

			return convertView;
		}

		/**
		 * 列表中 设置一行中的item
		 * @param holder
		 * @param clsStr
		 * @param pos
		 * @param index
		 */
		public void setLightView(ViewHolder holder, final String clsStr, int pos, int index) {
			if (clsStr == null || clsStr.compareTo(ProductClassifyInfo.CLASSID_DEFAULT) == 0) {
				holder.itemLayout[index].setVisibility(View.INVISIBLE);
				return;
			}else
				holder.itemLayout[index].setVisibility(View.VISIBLE);

			try{
				int resid = ProductClassifyInfo.getProductResourceId(clsStr);
				if (resid == R.drawable.pro_default_selector){
					holder.itemLayout[index].setVisibility(View.INVISIBLE);
				}
				else {
					holder.btn[index].setBackgroundResource(resid);
					holder.tv[index].setText(ProductClassifyInfo.getNameResourceId(clsStr));

					holder.btn[index].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(AddDevicesActivity.this,
									AddBlueToothDeviceActivity.class);
							String[] classidFilter;
							if (PISManager.getInstance().hasCloudConnect()){
								classidFilter = new String[1];
								classidFilter[0] = clsStr;
							}
							else{
								classidFilter = new String[2];
								classidFilter[0] = clsStr;
								classidFilter[1] = ProductClassifyInfo.CLASSID_DEFAULT;
							}
							intent.putExtra("classid", classidFilter);
							if (intent != null) {
								startActivityForResult(intent, 1008);
								overridePendingTransition(R.anim.anim_in_from_right,
										R.anim.anim_out_to_left);
							}
						}
					});
				}
			}catch (Exception e){
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}

		}

		public class ViewHolder {
			public LinearLayout rootLayout;
			public RelativeLayout[] itemLayout = new RelativeLayout[ITEM_COUNT_OF_LINE];
			public ImageButton[] btn = new ImageButton[ITEM_COUNT_OF_LINE];
			public TextView[] tv = new TextView[ITEM_COUNT_OF_LINE];

		}
	}


	public class NewGroupListAdapter extends NewDeviceListAdapter{
		public NewGroupListAdapter(Activity context, List<String> newDevices) {
			super(context, newDevices);
		}

		@Override
		public void setLightView(ViewHolder holder, final String clsStr, int pos, int index) {
			super.setLightView(holder, clsStr, pos, index);

			if (clsStr == null || clsStr.compareTo(ProductClassifyInfo.CLASSID_DEFAULT) == 0) {
				holder.itemLayout[index].setVisibility(View.INVISIBLE);
				return;
			}else
				holder.itemLayout[index].setVisibility(View.VISIBLE);

			try{
				int resid = ProductClassifyInfo.getGroupResourceId(clsStr);
				if (resid == R.drawable.grp_default_selector){
					holder.itemLayout[index].setVisibility(View.INVISIBLE);
				}
				else {
					holder.btn[index].setBackgroundResource(resid);
					holder.tv[index].setText(ProductClassifyInfo.getNameResourceId(clsStr));

					holder.btn[index].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							List<PIServiceInfo> srvInfos = ProductClassifyInfo.getServiceInfoList(clsStr);
							for (PIServiceInfo srvInfo : srvInfos) {
								if (srvInfo.T1 != 0 && srvInfo.T2 != 0) {
									addGroups(srvInfo.T1 & 0xFF, srvInfo.T2 & 0xFF);
									break;
								}
							}
						}
					});
				}
			}catch (Exception e){
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}

		}
	}
}