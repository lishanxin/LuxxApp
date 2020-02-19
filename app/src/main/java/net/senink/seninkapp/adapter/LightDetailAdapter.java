package net.senink.seninkapp.adapter;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.pis.PipaRequest;
import net.senink.piservice.services.PISxinColor;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PisDeviceGroup;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;

import java.util.List;

/**
 * 用于在灯设置界面中的组列表
 * 
 * @author zhaojunfeng
 * @date 2015-07-14
 */
public class LightDetailAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private SparseArray<PISBase> list = new SparseArray<PISBase>();
	// 灯的信息
	private PISBase mPISBase = null;

	private PISManager manager = null;
	private int selectedIndex = 0;

	public LightDetailAdapter(Context context, PISBase base) {
		this.inflater = LayoutInflater.from(context);
		list.clear();
		manager = PISManager.getInstance();
		this.mPISBase = base;
	}

	public LightDetailAdapter(Context context,
			SparseArray<PISBase> infors, PISBase base) {
		this.inflater = LayoutInflater.from(context);
		manager = PISManager.getInstance();
		this.mPISBase = base;
		setList(infors);
	}

	/**
	 * 增加信息
	 */
	public void addInfor(PISBase group) {
		this.list.append(group.getShortAddr(), group);
	}

	/**
	 * 是否已经有数据
	 * 
	 * @return
	 */
	public boolean containKey(short groupId) {
		boolean isOn = false;
		if (list != null && null != list.get(groupId, null)) {
			isOn = true;
		}
		return isOn;
	}

	/**
	 * 更新信息
	 * 
	 * @param infors
	 */
	public void setList(SparseArray<PISBase> infors) {
		if (null == infors) {
			this.list = new SparseArray<PISBase>();
		} else {
			this.list = infors;
		}
	}

	/**
	 * 移除数据
	 * 
	 * @param pos
	 */
	public void removeView(int pos) {
		if (pos >= 0 && pos < list.size()) {
			selectedIndex = 0;
			list.removeAt(pos);
			notifyDataSetChanged();
		}
	}

	/**
	 * 获取对应的PISBase对象
	 */
	public PISBase getGroupObject(int position){
		if (position >= 0 & position < list.size())
			return list.valueAt(position);
		else
			return null;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.valueAt(position);
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
		PISBase base = list.valueAt(position);
		if (base != null) {
			holder.tvName.setText(base.getName() == null ? "" : base.getName());

			int resourceId = 0;
			PISDevice dev;
			StateListDrawable sld = null;
			PISxinColor light = (PISxinColor)base;
			if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP){
				dev = base.getDeviceObject();
				if (dev != null) {
					if ( base.getT1() == 0x10 && base.getT2() == 0x05 )
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

//					resourceId = ProductClassifyInfo.getThumbResourceId(dev.getClassString());
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
//			if (base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
//					&& base.getT2() == PISConstantDefine.PIS_LIGHT_DOUBLE) {
//				holder.ivIcon
//						.setImageResource(R.drawable.icon_lightgroup_item_led);
//			} else {
//				holder.ivIcon.setImageResource(R.drawable.icon_light_group);
//			}
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
	private class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
	}
}
