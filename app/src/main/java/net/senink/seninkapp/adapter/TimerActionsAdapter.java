package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PisDeviceGroup;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 智能控制之设备与动作列表的适配器
 * 
 * @author zhaojunfeng
 * @date 2016-01-06
 */

public class TimerActionsAdapter extends BaseAdapter {

	private List<PISBase> mList;
	private LayoutInflater inflater;

	public TimerActionsAdapter(Activity activity, List<PISBase> bases) {
		this.inflater = LayoutInflater.from(activity);
		setList(bases);
	}

	public void setList(List<PISBase> bases) {
		if (null == bases) {
			this.mList = new ArrayList<PISBase>();
		} else {
			this.mList = bases;
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public PISBase getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
		ViewHolder holder;
		if (null == v) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.timeractions_item, null);
			holder.tvName = (TextView) v
					.findViewById(R.id.timeraction_item_name);
			holder.ivIcon = (ImageView) v
					.findViewById(R.id.timeraction_item_icon);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		PISBase base = mList.get(arg0);
		setViews(holder.tvName, holder.ivIcon, base);
		return v;
	}

	private void setViews(TextView tvName, ImageView iv, PISBase base) {
		int resId = R.drawable.ic_launcher;
		String name = base.getName();
		if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP
				&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
				&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
			resId = R.drawable.btn_timer_rgblight_selector;
		} else if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP
				&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
				&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
			resId = R.drawable.btn_timer_ledlight_selector;
		} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_ELECTRICIAN
				&& (base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_NORMAL
				|| base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER)) {
			resId = R.drawable.btn_timer_switch_selector;
		} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
				&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
				&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
			resId = R.drawable.btn_timer_rgbgroup_selector;
		} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
				&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
				&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
			resId = R.drawable.btn_timer_ledgroup_selector;
		} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_SYSTEM
				&& base.getT2() == PISConstantDefine.PIS_SYSTEM_SMARTCELL) {
//			String guid = ((PISSmartCell) base).getGUID();
//			if (PISSmartCell.GUID_PARENT_CONTROL.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_FLOW_CONTROL.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_VIDEO_SPEEDUP.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_GAME_SPEEDUP.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_AD_INTERCEPT.equals(guid)) {
//
//			} else if (PISSmartCell.GUID_TIMER_GWCONFIG.equals(guid)
//					|| PISSmartCell.GUID_TIMER_SWITCH.equals(guid)) {
//				resId = R.drawable.btn_timer_timer_selector;
//			} else if (PISSmartCell.GUID_SYNCHRONOUS.equals(guid)) {
//				resId = R.drawable.btn_timer_synchronous_selector;
//			} else if (PISSmartCell.GUID_LINKAGE.equals(guid)) {
//				resId = R.drawable.btn_timer_linkage_selector;
//			} else if (PISSmartCell.GUID_MARQUEE_LIGHT.equals(guid)) {
//				resId = R.drawable.btn_timer_marquee_selector;
//			} else if (PISSmartCell.GUID_COLOR_LIGHT.equals(guid)) {
//				resId = R.drawable.btn_timer_colorlight_selector;
//			}
		}
		iv.setBackgroundResource(resId);
		tvName.setText(name == null ? "" : name);
	}

	private class ViewHolder {
		public TextView tvName;
		public ImageView ivIcon;
	}
}
