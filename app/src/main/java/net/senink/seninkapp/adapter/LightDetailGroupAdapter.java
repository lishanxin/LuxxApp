package net.senink.seninkapp.adapter;

import java.util.ArrayList;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用于在灯设置界面中的组列表
 * 
 * @author zhaojunfeng
 * @date 2015-07-14
 */
public class LightDetailGroupAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<PISBase> list = new ArrayList<PISBase>();

	public LightDetailGroupAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	public LightDetailGroupAdapter(Context context, ArrayList<PISBase> infors,
			short groupId) {
		this.inflater = LayoutInflater.from(context);
		setList(infors);
	}

	/**
	 * 更新信息
	 * 
	 * @param infors
	 */
	public void setList(ArrayList<PISBase> infors) {
		if (null == infors) {
			this.list = new ArrayList<PISBase>();
		} else {
			this.list = infors;
		}
	}

	/**
	 * 移除数据
	 * 
	 * @param base
	 */
	public void removeView(PISBase base) {
		if (base != null) {
			list.remove(base);
			notifyDataSetChanged();
		}
	}

	/**
	 * 获取信息
	 * 
	 * @param pos
	 */
	public PISBase getInfor(int pos) {
		PISBase base = null;
		if (pos >= 0 && pos < list.size()) {
			base = list.get(pos);
		}
		return base;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lightsetting_listview_item,
					null);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.item_groupname);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.item_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PISBase base = list.get(position);
		if (base != null) {
			holder.tvName.setText(base.getName() == null ? "" : base.getName());
			if (base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT && base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
				holder.ivIcon
						.setImageResource(R.drawable.icon_light_item_led);
			} else {
				holder.ivIcon
						.setImageResource(R.drawable.icon_equipment_light_group);
			}
		}
		return convertView;
	}

	private class ViewHolder {
		public TextView tvName;
		public ImageView ivIcon;
	}
}
