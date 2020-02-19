package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISConstantDefine;
//import com.senink.seninkapp.core.PISInsoleGroup;
import net.senink.seninkapp.ui.activity.InsoleActivity;
import net.senink.seninkapp.ui.home.HomeActivity;

/**
 * 主页中的智能鞋垫
 * 
 * @author zhaojunfeng
 * @date 2016-01-31
 */
public class InsoleListAdapter extends BaseAdapter {
	// 存放设备信息的集合
//	private ArrayList<PISInsoleGroup[]> bases;
	private ArrayList<PISBase> bases;
	// 布局引用器
	private LayoutInflater inflater = null;
	// 上下文
	private HomeActivity context;

//	public InsoleListAdapter(HomeActivity context, ArrayList<PISInsoleGroup[]> devices) {
	public InsoleListAdapter(HomeActivity context, ArrayList<PISBase> devices) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		setList(devices);
	}

	/**
	 * 更新信息
	 * 
	 * @param devices
	 */
//	public void setList(ArrayList<PISInsoleGroup[]> devices) {
	public void setList(ArrayList<PISBase> devices) {
		if (bases != null) {
			bases.clear();
		}
		if (null == devices) {
//			this.bases = new ArrayList<PISInsoleGroup[]>();
			this.bases = new ArrayList<PISBase>();
		} else {

			this.bases = devices;
		}
	}

	public ArrayList<PISBase> getList() {
		return bases;
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
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.home_lightlist_item, null);
			holder.rootLayout = (RelativeLayout) convertView
					.findViewById(R.id.lightlist_root);
			holder.nameBtn1 = (ImageButton) convertView
					.findViewById(R.id.lightlist_name1);
			holder.nameBtn2 = (ImageButton) convertView
					.findViewById(R.id.lightlist_name2);
			holder.nameBtn3 = (ImageButton) convertView
					.findViewById(R.id.lightlist_name3);
			holder.nameBtn4 = (ImageButton) convertView
					.findViewById(R.id.lightlist_name4);
			holder.itemLayout1 = (RelativeLayout) convertView
					.findViewById(R.id.lightlist_item1);
			holder.itemLayout2 = (RelativeLayout) convertView
					.findViewById(R.id.lightlist_item2);
			holder.itemLayout3 = (RelativeLayout) convertView
					.findViewById(R.id.lightlist_item3);
			holder.itemLayout4 = (RelativeLayout) convertView
					.findViewById(R.id.lightlist_item4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if (bases.get(position) != null && bases.get(position).length > 0) {
//			holder.rootLayout.setVisibility(View.VISIBLE);
//			PISBase[] array = bases.get(position);
//			int len = array.length;
//			if (len > 0 && null != array[0]) {
//				setLightView(holder.itemLayout1, holder.nameBtn1, array[0],
//						position, 0);
//			} else {
//				setLightView(holder.itemLayout1, holder.nameBtn1, null,
//						position, 0);
//			}
//			if (len > 1 && null != array[1]) {
//				setLightView(holder.itemLayout2, holder.nameBtn2, array[1],
//						position, 1);
//			} else {
//				setLightView(holder.itemLayout2, holder.nameBtn2, null,
//						position, 1);
//			}
//			if (len > 2 && null != array[2]) {
//				setLightView(holder.itemLayout3, holder.nameBtn3, array[2],
//						position, 2);
//			} else {
//				setLightView(holder.itemLayout3, holder.nameBtn3, null,
//						position, 2);
//			}
//			if (len > 3 && null != array[3]) {
//				setLightView(holder.itemLayout4, holder.nameBtn4, array[3],
//						position, 3);
//			} else {
//				setLightView(holder.itemLayout4, holder.nameBtn4, null,
//						position, 3);
//			}
//		} else {
//			holder.rootLayout.setVisibility(View.GONE);
//		}

		return convertView;
	}

	/**
	 * 设置每个item的tag
	 * 
	 * @param view
	 * @param infor
	 * @param pos
	 * @param index
	 */
	private void setTag(View view, PISBase infor, int pos, int index) {
		Object[] objs = new Object[3];
		objs[0] = pos;
		objs[1] = index;
		objs[2] = infor;
		view.setTag(objs);
	}

	/**
	 * 灯泡列表中 设置一行中的item
	 * @param layout
	 * @param nameBtn
	 * @param infor
	 * @param pos
     * @param index
     */
	private void setLightView(LinearLayout layout, Button nameBtn,
			PISBase infor, int pos, int index) {
		if (infor != null) {
			// 获取该组中所有服务的信息
			layout.setVisibility(View.VISIBLE);
//			nameBtn.setText(infor.mName == null ? "" : infor.mName);
//			setTag(layout, infor, pos, index);
//			if (infor.getT1() == (byte) PISConstantDefine.PIS_INSOLE_T1
//					&& infor.getT2() == PISConstantDefine.PIS_INSOLE_T2) {
//				nameBtn.setBackgroundResource(R.drawable.btn_insole_selector);
//			}
			nameBtn.setEnabled(true);
//			if (infor.mStatus == 1) {
//				nameBtn.setEnabled(true);
//			} else {
//				nameBtn.setEnabled(false);
//			}
			nameBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					View v = (View) view.getParent();
					if (v.getTag() != null) {
						try {
							Object[] objs = (Object[]) v.getTag();
//							if (objs != null && objs.length == 3) {
//								int pos = (Integer) objs[0];
//								int index = (Integer) objs[1];
//								PISInsoleGroup group = null;
//								try {
//									group = bases.get(pos)[index];
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								if (group != null) {
////									if (pisBase.mStatus != PISConstantDefine.SERVICE_STATUS_ONLINE) {
////										return;
////									}
//									if (group.mT1 == (byte)PISConstantDefine.PIS_INSOLE_T1
//						                    && group.mT2 == PISConstantDefine.PIS_INSOLE_T2) {
//										Intent intent = new Intent(context,InsoleActivity.class);
//										intent.putExtra("groupid",group.groupId);
//										context.startActivity(intent);
//										context.overridePendingTransition(
//												R.anim.anim_in_from_right,
//												R.anim.anim_out_to_left);
//									}
//								}
//							}
						} catch (Exception e) {
							PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
						}
					}
				}
			});
		} else {
			layout.setVisibility(View.INVISIBLE);
			nameBtn.setText("");
		}

	}

	private class ViewHolder {
		public RelativeLayout rootLayout;
		public RelativeLayout itemLayout1;
		public ImageButton nameBtn1;
		public RelativeLayout itemLayout2;
		public ImageButton nameBtn2;
		public RelativeLayout itemLayout3;
		public ImageButton nameBtn3;
		public RelativeLayout itemLayout4;
		public ImageButton nameBtn4;
	}
}
