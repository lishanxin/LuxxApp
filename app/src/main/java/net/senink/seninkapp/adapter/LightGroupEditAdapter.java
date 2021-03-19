package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;
import com.telink.sig.mesh.light.MeshService;
import com.telink.sig.mesh.model.DeviceInfo;
import com.telink.sig.mesh.model.Group;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.GeneralDeviceModel;
import net.senink.seninkapp.MyApplication;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISLightLED;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PisDeviceGroup;
import net.senink.seninkapp.telink.api.TelinkGroupApiManager;
import net.senink.seninkapp.telink.model.TelinkBase;
import net.senink.seninkapp.telink.view.IconGenerator;
import net.senink.seninkapp.ui.activity.LightEditActivity;
import net.senink.seninkapp.ui.activity.LightGroupSettingActivity;
import net.senink.seninkapp.ui.activity.LightRGBDetailActivity;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.entity.LocationName;
import net.senink.seninkapp.ui.util.ToastUtils;
import net.senink.seninkapp.ui.util.Utils;



public class LightGroupEditAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<GeneralDeviceModel> bases = new ArrayList<>();
	private Handler mHandler = null;
	private int LightMode = 0;

	public LightGroupEditAdapter(Context context, List<GeneralDeviceModel> infors, int T2,
			Handler mHandler) {

		LightMode = T2;				// NextApp.tw
		this.inflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
		setList(infors);
	}

//	public LightGroupEditAdapter(Context context,
//			SparseArray<PISBase> infors, Handler mHandler) {
//		this.isGroup = false;
//		this.inflater = LayoutInflater.from(context);
//		this.mHandler = mHandler;
//		setList(infors);
//	}

	/**
	 * 更新数据
	 * 
	 * @param infors
	 */
	public void setList(List<GeneralDeviceModel> infors) {
		if (null == infors) {
			this.bases = new ArrayList<>();
		} else {
			this.bases = infors;
		}
	}

//	public void setList(SparseArray<PISBase> infors) {
//		if (null == infors) {
//			this.groupInfors = new SparseArray<PISBase>();
//		} else {
//			this.groupInfors = infors;
//		}
//	}

	@Override
	public int getCount() {
		return bases.size();
//		if (isGroup) {
//			return bases.size();
//		} else {
//			return groupInfors.size();
//		}
	}

	@Override
	public Object getItem(int position) {
		return bases.get(position);
//		if (isGroup) {
//			return bases.get(position);
//		} else {
//			return groupInfors.valueAt(position);
//		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lightedit_listview_item,
					null);
			holder.tvName = (Button) convertView
					.findViewById(R.id.lightedit_item_groupname);
			holder.tvLocation = (TextView) convertView
					.findViewById(R.id.lightedit_item_location);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.lightedit_item_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GeneralDeviceModel generalBase = bases.get(position);
		if (generalBase != null) {
			if (generalBase.isTelink()) {
				TelinkBase telinkBase = generalBase.getTelinkBase();
				String name = "";
				if (telinkBase.isDevice()) {
					DeviceInfo deviceInfo = telinkBase.getDevice();
					name = deviceInfo.getDeviceName();
					final int deviceType = deviceInfo.nodeInfo != null && deviceInfo.nodeInfo.cpsData.lowPowerSupport() ? 1 : 0;
					holder.ivIcon.setBackgroundResource(IconGenerator.getIcon(deviceType, deviceInfo.getOnOff()));
					List<Integer> sublist = deviceInfo.subList;
					if(sublist != null){
						StringBuilder groupName = new StringBuilder();
						for (Integer integer : sublist) {
							Group group = TelinkGroupApiManager.getInstance().getGroupByAddress(integer);
							if(group != null && group.name != null){
								groupName.append(group.name).append(",");
							}
						}
						if(groupName.length() > 0){
							groupName.deleteCharAt(groupName.length() - 1);
//							holder.tvLocation.setText(groupName.toString());
						}
					}
				} else {
					Group group = telinkBase.getGroup();
					name = group.name;
					boolean isOn = TelinkGroupApiManager.getInstance().isGroupOn(group);
					holder.ivIcon.setBackgroundResource(IconGenerator.getGroupIconRes(isOn));
				}
				holder.tvName.setText(name);
			} else {
				PISBase base = generalBase.getPisBase();
				if (base != null) {
					holder.tvName.setText(base.getName() == null ? "" : base.getName());
					holder.tvLocation.setVisibility(View.VISIBLE);

					PISDevice dev;
					StateListDrawable sld = null;
					PISxinColor light = (PISxinColor)base;
					if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP){
						dev = base.getDeviceObject();
						if (dev != null) {
							if ( base.getT2() == 0x05 )
							{
								sld = ProductClassifyInfo.getProductStateListDrawable(convertView.getContext(),
										ProductClassifyInfo.CLASSID_EUREKA_CANDLE,
										dev.getStatus(),
										light.getLightStatus());
							}
							else
							{
								sld = ProductClassifyInfo.getProductStateListDrawable(convertView.getContext(),
										dev.getClassString(),
										dev.getStatus(),
										light.getLightStatus());
							}
						} else {
							sld = ProductClassifyInfo.getGroupStateListDrawable(convertView.getContext(),
									ProductClassifyInfo.CLASSID_DEFAULT,
									0,0);
						}
					}else {
						//找到对应的Service，并利用其DEVICE找到classid
						List<PISBase> srvs = PISManager.getInstance().PIServicesWithQuery(
								base.getIntegerType(), PISManager.EnumServicesQueryBaseonType);
						if(srvs == null || srvs.size() == 0)
							dev = null;
						else
							dev = srvs.get(0).getDeviceObject();
						if (dev != null) {
							sld = ProductClassifyInfo.getGroupStateListDrawable(convertView.getContext(),
									dev.getClassString(),
									dev.getStatus(),
									light.getLightStatus());
						} else {
							sld = ProductClassifyInfo.getGroupStateListDrawable(convertView.getContext(),
									ProductClassifyInfo.CLASSID_DEFAULT,
									0,0);
						}
					}

					if (sld != null) {
						try {
							holder.ivIcon.setBackground(sld);
//					holder.ivIcon
//							.setBackgroundResource(resourceId);
//					if (base.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
//						setGrayOnBackgroud(holder.ivIcon.getBackground(), 1);
//					else
//						setGrayOnBackgroud(holder.ivIcon.getBackground(), 0);

						}catch (Exception e){
							PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
						}
					}
					setLocation(holder.tvLocation, (base.getLocation() & 0xFF));
				}
			}
			holder.tvName.setTag(generalBase);
			setListener(holder.tvName);
		}


		return convertView;
	}
	private void setGrayOnBackgroud(Drawable iv, int value) {
		ColorMatrix matrix = new ColorMatrix();
		if (value == 0) {
			matrix.setSaturation(0);
		}
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		iv.setColorFilter(filter);
	}
	private void setListener(Button btn) {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Object obj = v.getTag();
//				((PISBase) obj).getGroupId()
				if (obj == null)
					return;
				try {
					// NextApp.tw
					GeneralDeviceModel generalDeviceModel = (GeneralDeviceModel) obj;
					if(generalDeviceModel.isTelink()){
						mHandler.obtainMessage(
								LightEditActivity.MSG_TELINK_ITEM_CLICK, generalDeviceModel.getTelinkBase())
								.sendToTarget();
					}else{
						PISBase group = generalDeviceModel.getPisBase();

						if ( LightMode == 0 | group.getT2() == LightMode )
						{
							mHandler.obtainMessage(
									LightEditActivity.MSG_ITEM_CLICK, group)
									.sendToTarget();
						}
						else
						{
							if ( LightMode == 0x03 )
							{
								mHandler.obtainMessage(
										16, group)
										.sendToTarget();
							}
							if ( LightMode == 0x05 )
							{
								mHandler.obtainMessage(
										15, group)
										.sendToTarget();
							}
						}
					}

				} catch (Exception e) {
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}


			}
		});
	}

	/**
	 * 设置
	 * 
	 * @param tv
	 * @param pos
	 */
	private void setLocation(TextView tv, int pos) {
		LocationName infor = null;
		if (PISManager.getInstance().getLocations().size() > pos && pos >= 0) {
//			infor = PISManager.getInstance().locations.get(pos);
		}
		String location = null;
		if (infor != null) {
			location = infor.name;
		}
		if (location != null) {
			tv.setText(location);
		} else {
			tv.setText(R.string.unknown);
		}
	}

	private class ViewHolder {
		public Button tvName;
		public TextView tvLocation;
		public ImageView ivIcon;
	}
}
