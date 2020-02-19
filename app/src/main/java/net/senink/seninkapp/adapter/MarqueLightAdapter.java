package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
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

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PisDeviceGroup;
import net.senink.seninkapp.ui.activity.MarqueeLightActivity;
import net.senink.seninkapp.ui.entity.LocationName;

/**
 * 跑马灯添加灯设备的适配器
 * 
 * @author zhaojunfeng
 * @date 2016-01-20
 */

public class MarqueLightAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<PISBase> bases = new ArrayList<PISBase>();
	private SparseArray<PISBase> infors;
	private Handler mHandler = null;
    private boolean isGroup;
	public MarqueLightAdapter(Context context, List<PISBase> infors,
			Handler mHandler) {
		this.inflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
		setList(infors);
	}

	public MarqueLightAdapter(Context context, SparseArray<PISBase> infors,
			Handler mHandler) {
		this.inflater = LayoutInflater.from(context);
		this.mHandler = mHandler;
		this.isGroup = true;
		setList(infors);
	}
	
	public void setList(SparseArray<PISBase> infors) {
		if (null == infors) {
			this.infors = new SparseArray<PISBase>();
		} else {
			this.infors = infors;
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param infors
	 */
	public void setList(List<PISBase> infors) {
		if (null == infors) {
			this.bases = new ArrayList<PISBase>();
		} else {
			this.bases = infors;
		}
	}

	public List<PISBase> getLights(){
		return bases;
	}
	
	@Override
	public int getCount() {
		if (isGroup) {
			return infors.size();
		} else {
			return bases.size();
		}
	}

	@Override
	public PISBase getItem(int position) {
		if (isGroup) {
			return infors.valueAt(position);
		} else {
			return bases.get(position);
		}
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
		if (!isGroup) {
			PISBase base = bases.get(position);
			if (base != null) {
				holder.tvName.setText(base.getName() == null ? "" : base.getName());
				holder.tvLocation.setVisibility(View.VISIBLE);
				setLocation(holder.tvLocation, (base.getLocation() & 0xFF));
				holder.ivIcon.setBackgroundResource(R.drawable.icon_equipment_light_group);
				holder.tvName.setTag(base);
				setListener(holder.tvName);
			}
		} else {
			PISBase group = infors.valueAt(position);
			if (group != null) {
				holder.tvName.setText(group.getName() == null ? "" : group.getName());
				holder.tvLocation.setVisibility(View.GONE);
				holder.tvName.setTag(group);
				holder.ivIcon.setBackgroundResource(R.drawable.icon_light_group);
				setListener(holder.tvName);
			}
		}
		return convertView;
	}

	private void setListener(Button btn) {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Object obj = v.getTag();
				if (obj != null) {
					if (isGroup) {
						PISBase group = (PISBase) obj;
						mHandler.obtainMessage(MarqueeLightActivity.MSG_ITEM_CLICK,
								group).sendToTarget();
					} else {
						PISBase base = (PISBase) obj;
						mHandler.obtainMessage(MarqueeLightActivity.MSG_ITEM_CLICK,
								base).sendToTarget();
					}
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
//			infor = PISManager.getInstance()getInstance.locations.get(pos);
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
