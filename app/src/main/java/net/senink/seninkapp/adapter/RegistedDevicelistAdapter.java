package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISXinSwitch;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISDevice;
//import com.senink.seninkapp.core.PISGWConfig;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSwitch;
import net.senink.seninkapp.ui.activity.SwitchDetailActivity;
import net.senink.seninkapp.ui.util.HttpUtils;
import net.senink.seninkapp.ui.util.SharePreferenceUtils;

public class RegistedDevicelistAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayList<PISDevice> devices;
	private ArrayList<Byte> bytes;
	private ArrayList<Integer> types;

	public RegistedDevicelistAdapter(Context context,
			ArrayList<PISDevice> devices, ArrayList<Byte> bytes,
			ArrayList<Integer> types) {
		this.context = context;
		this.devices = devices;
		this.bytes = bytes;
		this.types = types;
	}

	public void addData(ArrayList<PISDevice> devices, ArrayList<Byte> bytes,
			ArrayList<Integer> types) {
		this.devices = devices;
		this.bytes = bytes;
		this.types = types;
	}

	@Override
	public int getGroupCount() {
		if (devices != null) {
			return devices.size();
		} else if (bytes != null) {
			return bytes.size();
		} else if (types != null) {
			return types.size();
		}
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int count = 0;
		if (devices != null) {
			try {
				PISDevice pis = (PISDevice) getGroup(groupPosition);
				count = pis.getPIServices().size();
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		} else if (bytes != null) {
//			count = PISManager
//					.getInstance()
//					.queryPisMap(PISConstantDefine.SERVICES_QUERY_ON_LOCATION,
//							bytes.get(groupPosition).byteValue(), (byte) 0,
//							null).size();
		} else if (types != null) {
//			count = PISManager.getInstance()
//					.getPisListByType(types.get(groupPosition)).size();
		}

		return count;
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (devices != null) {
			return devices.get(groupPosition);
		} else if (bytes != null) {
			return bytes.get(groupPosition);
		} else if (types != null) {
			return types.get(groupPosition);
		}
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		PISBase pisBase = null;
		if (devices != null) {
			try {
				PISDevice pis = (PISDevice) getGroup(groupPosition);
				pisBase = pis.getPIServices().get(childPosition);
			} catch (Exception e) {
				PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
			}
		} else if (bytes != null) {
//			pisBase = PISManager
//					.getInstance()
//					.queryPisMap(PISConstantDefine.SERVICES_QUERY_ON_LOCATION,
//							bytes.get(groupPosition).byteValue(), (byte) 0,
//							null).get(childPosition);
		} else if (types != null) {
//			pisBase = PISManager.getInstance()
//					.getPisListByType(types.get(groupPosition))
//					.get(childPosition);

		}
		// if(pisBase!=null){
		// pisBase =PISManager
		// .getInstance().getPisByKey(pisBase.getPISKeyString());
		// }
		return pisBase;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("static-access")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		// PISDevice pisDevice = devices.get(groupPosition);
		if (convertView == null) {
			convertView = (View) LayoutInflater.from(context).inflate(
					R.layout.expandablelist_title_item, null);
			groupHolder = new GroupHolder();
			groupHolder.titleTv = (TextView) convertView
					.findViewById(R.id.title);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		String name = null;
		if (devices != null) {
			name = devices.get(groupPosition).getName();
		} else if (bytes != null) {
			int index = SwitchDetailActivity.toInt(new byte[] { bytes
					.get(groupPosition) });
			name = SharePreferenceUtils.getInstance(context).getLocationValue(
					context, String.valueOf(index));
			if (null == name || "".equals(name)) {
				name = context.getResources().getString(R.string.no_know);
			}
		} else if (types != null) {
//			int type = types.get(groupPosition);
//			Bundle bundle = PISManager.getInstance().getT1T2ByType(type);
//			int t1 = bundle.getInt("t1");
//			int t2 = bundle.getInt("t2");
//			String key = String.valueOf(bundle.getInt("t1"))
//					+ String.valueOf(bundle.getInt("t2"));
//			Log.i("test", "key==>" + key + ", t1 = " + t1 + ", t2 = " + t2);
//			name = SharePreferenceUtils.getInstance(context).getLocationValue(
//					context, key);
		}
		if (name == null || "".equals(name)) {
			name = context.getResources().getString(R.string.no_know);
		}
		groupHolder.titleTv.setText(name);
		return convertView;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ItemHolder itemHolder = null;
		PISBase pisBase = (PISBase) getChild(groupPosition, childPosition);
		if (convertView == null) {
			itemHolder = new ItemHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.registed_device_list_item, null);
			itemHolder.deviceInfo = (TextView) convertView
					.findViewById(R.id.registerd_device_name);
			itemHolder.gong_lv = (TextView) convertView
					.findViewById(R.id.gong_lv);
			itemHolder.icon = (ImageView) convertView.findViewById(R.id.icon);

			itemHolder.deviceSwitch = (CheckBox) convertView
					.findViewById(R.id.registerd_device_switch);
			itemHolder.item_area = (LinearLayout) convertView
					.findViewById(R.id.item_area);
			convertView.setTag(itemHolder);
		} else {
			itemHolder = (ItemHolder) convertView.getTag();
		}
		itemHolder.deviceInfo.setText(pisBase.getName());
		itemHolder.deviceInfo.setTag(pisBase);
		if (devices != null) {
			itemHolder.deviceInfo.setTag(R.id.registerd_device_name,
					devices.get(groupPosition));
		}
//		if (pisBase instanceof PISGWConfig) {
//			itemHolder.deviceSwitch.setVisibility(View.INVISIBLE);
//			// itemHolder.deviceInfo.setCompoundDrawablesWithIntrinsicBounds(
//			// R.drawable.icon_list_zhineng03, 0, 0, 0);
//			itemHolder.icon.setImageResource(R.drawable.icon_list_zhineng03);
//			PISGWConfig pg = (PISGWConfig) pisBase;
//			itemHolder.deviceInfo.setText(pg.get_ssid());
//		} else
		if (pisBase instanceof PISXinSwitch) {
			itemHolder.deviceSwitch.setVisibility(View.VISIBLE);
			itemHolder.gong_lv.setVisibility(View.VISIBLE);

			PISXinSwitch swicth = (PISXinSwitch) pisBase;

			String tmp = context.getResources().getString(R.string.cur_gong_lv)
					+ String.format("%.2f", swicth.getPower())
					+ context.getResources().getString(R.string.wa);
			itemHolder.gong_lv.setText(tmp);
			itemHolder.deviceSwitch.setChecked(swicth.getSwitchStatus());
			if (swicth.getSwitchStatus()) {
				itemHolder.icon.setImageResource(R.drawable.on_switch);
			} else {
				itemHolder.icon.setImageResource(R.drawable.off_switch);
			}
			itemHolder.deviceSwitch.setTag(swicth);
			itemHolder.deviceSwitch.setTag(R.id.registerd_device_name,
					itemHolder);
			itemHolder.deviceSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					HttpUtils.networkStateusOK(context);
					CheckBox checkBox = (CheckBox) view;
					Log.i("test", "state==>" + checkBox.isChecked());
					PISXinSwitch swicth = (PISXinSwitch) view.getTag();
					swicth.setSwitchStatus(checkBox.isChecked());
					Log.i("test", "start pkey" + swicth.getPISKeyString());
					// checkBox.setChecked(!swicth.isOn());
					PipaRequest req = swicth.commitSwitchStatus(swicth.getSwitchStatus());
//					swicth.setSwitchLcs(swicth.clickStateSwitch);
					// ItemHolder holder = (ItemHolder) view
					// .getTag(R.id.registerd_device_name);
					// if (checkBox.isChecked()) {
					// holder.icon.setImageResource(R.drawable.on_switch);
					// } else {
					// holder.icon.setImageResource(R.drawable.off_switch);
					// }
					PISManager.getInstance().DiscoverAll(); // refresh();
				}
			});
		}
		if (pisBase.getStatus() == PISBase.SERVICE_STATUS_OFFLINE) {
			itemHolder.item_area.setAlpha(0.3f);
			itemHolder.deviceSwitch.setEnabled(false);
		} else {
			itemHolder.item_area.setAlpha(1f);
			itemHolder.deviceSwitch.setEnabled(true);
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}

class GroupHolder {
	public TextView titleTv;
}

class ItemHolder {
	public CheckBox deviceSwitch;
	public TextView deviceInfo;
	public TextView gong_lv;
	public ImageView icon;
	public LinearLayout item_area;
}
