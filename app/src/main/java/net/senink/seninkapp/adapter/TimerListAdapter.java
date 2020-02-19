package net.senink.seninkapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.util.ByteUtilLittleEndian;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.ByteUtilLittleEndian;
//import com.senink.seninkapp.core.CommonUtils;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISGWConfigTimer;
//import com.senink.seninkapp.core.PISManager;
//import com.senink.seninkapp.core.PISSmartCell;
//import com.senink.seninkapp.core.PISSwitchTimer;
//import com.senink.seninkapp.core.PisDeviceGroup;
//import net.senink.seninkapp.ui.activity.AddTimerActivity;
import net.senink.seninkapp.ui.constant.Constant;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.entity.TimerTaskInfor;
import net.senink.seninkapp.ui.util.ByteUtilBigEndian;
import net.senink.seninkapp.ui.util.CommonUtils;
import net.senink.seninkapp.ui.util.SortUtils;
import net.senink.seninkapp.ui.util.StringUtils;
import net.senink.seninkapp.ui.util.Utils;
import net.senink.seninkapp.ui.view.listview.SwipeMenu;
import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;
import net.senink.seninkapp.ui.view.listview.SwipeMenuListView.OnMenuItemClickListener;

/**
 * 用于在定时器列表界面中的组列表
 * 
 * @author zhaojunfeng
 * @date 2015-12-31
 */
public class TimerListAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private SparseArray<TimerTaskInfor> list = null;
	private Activity mContext;
//	private PISSmartCell cell;
	private boolean isTimer;
	private boolean status = false;

//	public TimerListAdapter(Activity context, PISSmartCell cell,
//			SparseArray<TimerTaskInfor> infors) {
//		this.inflater = LayoutInflater.from(context);
//		this.mContext = context;
////		this.cell = cell;
//		setList(infors);
//	}

	public void setStatus(boolean isOn) {
		this.status = isOn;
		notifyDataSetChanged();
	}

//	public TimerListAdapter(Activity context, PISSmartCell cell,
//			SparseArray<TimerTaskInfor> infors, boolean isTimer) {
//		this.inflater = LayoutInflater.from(context);
//		this.mContext = context;
//		this.cell = cell;
//		this.isTimer = isTimer;
//		setList(infors);
//	}

	/**
	 * 更新信息
	 * 
	 * @param infors
	 */
	public void setList(SparseArray<TimerTaskInfor> infors) {
		if (null == infors) {
			this.list = new SparseArray<TimerTaskInfor>();
		} else {
			this.list = infors;
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TimerTaskInfor getItem(int position) {
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
			convertView = inflater.inflate(R.layout.smartcelltimer_item, null);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.smartcelltimer_item_name);
			holder.lvOperate = (SwipeMenuListView) convertView
					.findViewById(R.id.smartcelltimer_item_listview);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.smartcelltimer_item_time);
			holder.rootLayout = (LinearLayout) convertView
					.findViewById(R.id.smartcelltimer_item_root);
			holder.lineView1 = convertView
					.findViewById(R.id.smartcelltimer_item_line1);
			holder.lineView2 = convertView
					.findViewById(R.id.smartcelltimer_item_line2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TimerTaskInfor infor = list.valueAt(position);
		setOperateView(holder.lvOperate, infor);
		String time = StringUtils.getDay(mContext, infor.time);
		if (TextUtils.isEmpty(time)) {
			time = mContext.getString(R.string.timer_time, "");
		} else {
			time = mContext.getString(R.string.timer_time, time);
		}
		setTextColor(holder.tvName);
		setBackGround(holder.rootLayout, holder.tvName, holder.tvTime,
				holder.lvOperate, holder.lineView1, holder.lineView2);
		holder.tvTime.setText(time);
		setName(holder.tvName, infor);
		return convertView;
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param layout
	 * @param tvName
	 * @param tvTime
	 * @param lineView1
	 * @param lineView1
	 */
	private void setBackGround(LinearLayout layout, TextView tvName,
			TextView tvTime, SwipeMenuListView listView, View lineView1,
			View lineView2) {
		int bgColor = 0;
		Resources res = mContext.getResources();
		if (status) {
			bgColor = res.getColor(R.color.timer_line_default_bgcolor);
			layout.setBackgroundColor(Color.WHITE);
			tvName.setBackgroundColor(Color.WHITE);
			tvTime.setBackgroundColor(Color.WHITE);
			listView.setBackgroundColor(Color.WHITE);
			lineView1.setBackgroundColor(bgColor);
			lineView2.setBackgroundColor(bgColor);
		} else {
			bgColor = res.getColor(R.color.timer_bgcolor);
			layout.setBackgroundColor(bgColor);
			tvName.setBackgroundColor(bgColor);
			tvTime.setBackgroundColor(bgColor);
			listView.setBackgroundColor(bgColor);
			bgColor = res.getColor(R.color.timer_line_bgcolor);
			lineView1.setBackgroundColor(bgColor);
			lineView2.setBackgroundColor(bgColor);
		}
	}

	/**
	 * 设置设备名称字体颜色
	 * 
	 * @param tvName
	 */
	private void setTextColor(TextView tvName) {
		int bgColor = 0;
		Resources res = mContext.getResources();
		if (status) {
			bgColor = res.getColor(R.color.timer_name_default_textcolor);
		} else {
			bgColor = res.getColor(R.color.timer_name_textcolor);
		}
		tvName.setTextColor(bgColor);
	}

	private void setName(TextView tvName, TimerTaskInfor infor) {
		String name = "";
		Resources res = mContext.getResources();
		Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
		if (infor != null) {
			PISBase base = null;
			String mac = infor.macAddr;
			if (!TextUtils.isEmpty(mac)) {
//				mac = CommonUtils.getStringOnMacAddr(mac);
				if (mac.startsWith("ffffffff")) {
					short groupId = infor.serviceId;
					byte[] ids = ByteUtilLittleEndian.getBytes(groupId);
					groupId = ByteUtilBigEndian.byteArrToShort(ids);
//					base = PISManager.getInstance().getSomeoneGroup(groupId);
				} else {
					base = SortUtils.getPISBase(infor.macAddr, infor.serviceId);
				}
			}
			if (base != null) {
				name = base.getName();
				int resId = R.drawable.ic_launcher;
				if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
					resId = R.drawable.icon_timer_rgblight;
				} else if (base.ServiceType != PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
					resId = R.drawable.icon_timer_ledlight;
				} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_ELECTRICIAN
						&& (base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_NORMAL
						|| base.getT2() == PISConstantDefine.PIS_ELECTRICIAN_SWITCH_POWER)) {
					resId = R.drawable.icon_timer_switch;
				} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_COLOR) {
					resId = R.drawable.icon_timer_rgbgroup;
				} else if (base.ServiceType == PISBase.SERVICE_TYPE_GROUP
						&& base.getT1() == PISConstantDefine.PIS_MAJOR_LIGHT
						&& base.getT2() == PISConstantDefine.PIS_LIGHT_LIGHT) {
					resId = R.drawable.icon_timer_ledgroup;
				} else if (base.getT1() == PISConstantDefine.PIS_MAJOR_SYSTEM
						&& base.getT2() == PISConstantDefine.PIS_SYSTEM_SMARTCELL) {
//					String guid = ((PISSmartCell) base).getGUID();
//					if (PISSmartCell.GUID_PARENT_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_FLOW_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_BANDWIDTH_CONTROL.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_VIDEO_SPEEDUP.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_GAME_SPEEDUP.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_AD_INTERCEPT.equals(guid)) {
//
//					} else if (PISSmartCell.GUID_TIMER_GWCONFIG.equals(guid)
//							|| PISSmartCell.GUID_TIMER_SWITCH.equals(guid)) {
//						resId = R.drawable.icon_timer_timer;
//					} else if (PISSmartCell.GUID_SYNCHRONOUS.equals(guid)) {
//						resId = R.drawable.icon_timer_synchronous;
//					} else if (PISSmartCell.GUID_LINKAGE.equals(guid)) {
//						resId = R.drawable.icon_timer_linkage;
//					} else if (PISSmartCell.GUID_MARQUEE_LIGHT.equals(guid)) {
//						resId = R.drawable.icon_timer_marquee;
//					} else if (PISSmartCell.GUID_COLOR_LIGHT.equals(guid)) {
//						resId = R.drawable.icon_timer_colorlight;
//					}
				}
				if (resId != 0) {
					drawable = res.getDrawable(resId);
				}
				tvName.setTag(infor);
			}
		}
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		tvName.setCompoundDrawables(null, drawable, null, null);
		tvName.setText(name);
	}

	private void setOperateView(final SwipeMenuListView listView,
			final TimerTaskInfor infor) {
		MyAdapter adapter = new MyAdapter(infor);
		listView.setAdapter(adapter);
		if (!isTimer && status) {
			SwipeMenuCreator creator = new SwipeMenuCreator() {

				@Override
				public void create(SwipeMenu menu) {
					SwipeMenuItem editItem = new SwipeMenuItem(mContext);
					editItem.setBackground(new ColorDrawable(0xFFC7C7C7));
					editItem.setWidth(Utils.dpToPx(55, mContext.getResources()));
					editItem.setTitle(R.string.Edit);
					editItem.setTitleSize(18);
					editItem.setTitleColor(Color.WHITE);
					menu.addMenuItem(editItem);
					editItem = new SwipeMenuItem(mContext);
					editItem.setBackground(new ColorDrawable(0xFFFF004B));
					editItem.setWidth(Utils.dpToPx(55, mContext.getResources()));
					editItem.setTitle(R.string.delete);
					editItem.setTitleSize(18);
					editItem.setTitleColor(Color.WHITE);
					menu.addMenuItem(editItem);
				}
			};
			listView.setMenuCreator(creator);
			listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(int position, SwipeMenu menu,
						int index) {
					if (infor != null) {
//						if (cell != null) {
//							if (index == 0) {
//								Intent intent = new Intent(mContext,
//										AddTimerActivity.class);
//								intent.putExtra(MessageModel.ACTIVITY_VALUE,
//										cell.getPISKeyString());
//								intent.putExtra("taskid", infor.taskId);
//								mContext.startActivityForResult(intent,
//										Constant.REQUEST_CODE_ADDTIMER);
//								mContext.overridePendingTransition(
//										R.anim.anim_in_from_right,
//										R.anim.anim_out_to_left);
//							} else if (index == 1) {
//								if (cell instanceof PISGWConfigTimer) {
//									((PISGWConfigTimer) cell).deleteTimerTask(
//											infor.taskId, true);
//								} else if (cell instanceof PISSwitchTimer) {
//									((PISSwitchTimer) cell).deleteTimerTask(
//											infor.taskId, true);
//								}
//							}
//						}
					}
					return false;
				}
			});
		}
	}

	private class ViewHolder {
		public SwipeMenuListView lvOperate;
		public TextView tvName;
		public TextView tvTime;
		public LinearLayout rootLayout;
		public View lineView1;
		public View lineView2;
	}

	private class MyAdapter extends BaseAdapter {

		private TimerTaskInfor infor;

		public MyAdapter(TimerTaskInfor infor) {
			this.infor = infor;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return infor;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.timer_item_time, null);
			TextView tv = (TextView) convertView
					.findViewById(R.id.timer_item_time);
			String time = null;
			if (infor != null && infor.time != null) {
				time = StringUtils.getTime(infor.time);
			}
			if (TextUtils.isEmpty(time)) {
				time = "";
			}
			setTextColor(tv);
			tv.setText(time);
			return convertView;
		}

		/**
		 * 设置时间字体颜色
		 * 
		 * @param tv
		 */
		private void setTextColor(TextView tv) {
			int bgColor = 0;
			Resources res = mContext.getResources();
			if (status) {
				bgColor = res.getColor(R.color.timer_time_default_textcolor);
			} else {
				bgColor = res.getColor(R.color.timer_time_textcolor);
			}
			tv.setTextColor(bgColor);
		}
	}
}
