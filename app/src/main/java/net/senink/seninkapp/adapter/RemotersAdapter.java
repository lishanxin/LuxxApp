package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.activity.RemoterSettingActivity;
import net.senink.seninkapp.ui.constant.MessageModel;
import net.senink.seninkapp.ui.constant.ProductClassifyInfo;
import net.senink.seninkapp.ui.home.HomeActivity;

/**
 * 主页中的遥控器的适配器
 * 
 * @author zhaojunfeng
 * @date 2015-08-27
 */
public class RemotersAdapter extends BaseAdapter {
	// 存放设备信息的集合
	private ArrayList<PISBase[]> bases;
	// 布局引用器
	private LayoutInflater inflater = null;
	// 上下文
	private HomeActivity context;

	public RemotersAdapter(HomeActivity context, ArrayList<PISBase[]> devices) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		setList(devices);
	}

	/**
	 * 更新信息
	 * 
	 * @param devices
	 */
	public void setList(ArrayList<PISBase[]> devices) {
		if (bases != null) {
			bases.clear();
		}
		if (null == devices) {
			this.bases = new ArrayList<PISBase[]>();
		} else {
			this.bases = devices;
		}
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
			convertView = inflater.inflate(R.layout.home_remoter_item, null);
			holder.rootLayout = (RelativeLayout) convertView
					.findViewById(R.id.remoter_root);
			holder.iconBtn1 = (Button) convertView
					.findViewById(R.id.remoter_icon1);
			holder.iconBtn2 = (Button) convertView
					.findViewById(R.id.remoter_icon2);
			holder.iconBtn3 = (Button) convertView
					.findViewById(R.id.remoter_icon3);
			holder.iconBtn4 = (Button) convertView
					.findViewById(R.id.remoter_icon4);
			holder.tvName1 = (TextView) convertView
					.findViewById(R.id.remoter_name1);
			holder.tvName2 = (TextView) convertView
					.findViewById(R.id.remoter_name2);
			holder.tvName3 = (TextView) convertView
					.findViewById(R.id.remoter_name3);
			holder.tvName4 = (TextView) convertView
					.findViewById(R.id.remoter_name4);
			holder.itemLayout1 = (LinearLayout) convertView
					.findViewById(R.id.remoter_item1);
			holder.itemLayout2 = (LinearLayout) convertView
					.findViewById(R.id.remoter_item2);
			holder.itemLayout3 = (LinearLayout) convertView
					.findViewById(R.id.remoter_item3);
			holder.itemLayout4 = (LinearLayout) convertView
					.findViewById(R.id.remoter_item4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (bases.get(position) != null && bases.get(position).length > 0) {
			holder.rootLayout.setVisibility(View.VISIBLE);
			PISBase[] array = bases.get(position);
			int len = array.length;
			if (len > 0 && null != array[0]) {
				setLightView(holder.itemLayout1, holder.iconBtn1,
						holder.tvName1, array[0], position, 0);
			} else {
				setLightView(holder.itemLayout1, holder.iconBtn1,
						holder.tvName1, null, position, 0);
			}
			if (len > 1 && null != array[1]) {
				setLightView(holder.itemLayout2, holder.iconBtn2,
						holder.tvName2, array[1], position, 1);
			} else {
				setLightView(holder.itemLayout2, holder.iconBtn2,
						holder.tvName2, null, position, 1);
			}
			if (len > 2 && null != array[2]) {
				setLightView(holder.itemLayout3, holder.iconBtn3,
						holder.tvName3, array[2], position, 2);
			} else {
				setLightView(holder.itemLayout3, holder.iconBtn3,
						holder.tvName3, null, position, 2);
			}
			if (len > 3 && null != array[3]) {
				setLightView(holder.itemLayout4, holder.iconBtn4,
						holder.tvName4, array[3], position, 3);
			} else {
				setLightView(holder.itemLayout4, holder.iconBtn4,
						holder.tvName4, null, position, 3);
			}
		} else {
			holder.rootLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	/**
	 * 遥控器列表中 设置一行中的item
	 * @param layout
	 * @param btn
	 * @param tv
	 * @param infor
	 * @param pos
     * @param index
     */
	private void setLightView(LinearLayout layout, Button btn, TextView tv,
			PISBase infor, int pos, int index) {
		if (infor != null) {
			// 获取该组中所有服务的信息
			layout.setVisibility(View.VISIBLE);
			tv.setText(infor.getName() == null ? "" : infor.getName());
			if (infor.getStatus() == PISBase.SERVICE_STATUS_ONLINE) {
				btn.setEnabled(true);
			} else {
				btn.setEnabled(false);
			}

			int resourceId = 0;
			PISDevice dev;
			if (infor.ServiceType != PISBase.SERVICE_TYPE_GROUP){
				dev = infor.getDeviceObject();
				if (dev != null) {
					resourceId = ProductClassifyInfo.getProductResourceId(dev.getClassString());
				} else {
					resourceId = R.drawable.pro_default_selector;
				}
			}else {
				//找到对应的Service，并利用其DEVICE找到classid
				List<PISBase> srvs = PISManager.getInstance().PIServicesWithQuery(
						infor.getIntegerType(), PISManager.EnumServicesQueryBaseonType);
				if(srvs == null || srvs.size() == 0)
					dev = null;
				else
					dev = srvs.get(0).getDeviceObject();
				if (dev != null) {
					resourceId = ProductClassifyInfo.getGroupResourceId(dev.getClassString());
				} else {
					resourceId = R.drawable.grp_default_selector;
				}
			}

			if (resourceId != 0) {
				try {
					btn.setBackgroundResource(resourceId);
					if (infor.getStatus() == PISBase.SERVICE_STATUS_ONLINE)
						setGrayOnBackgroud(btn.getBackground(), 1);
					else
						setGrayOnBackgroud(btn.getBackground(), 0);

				}catch (Exception e){
					PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
				}
			}


			btn.setTag(infor);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					if (view.getTag() != null) {
						try {
							PISBase base = (PISBase) view.getTag();
							if (base != null) {
								Intent intent = null;
								intent = new Intent(context,RemoterSettingActivity.class);

								if (intent != null) {
									intent.putExtra(
											MessageModel.PISBASE_KEYSTR,
											base.getPISKeyString());
									context.startActivity(intent);
									context.overridePendingTransition(
											R.anim.anim_in_from_right,
											R.anim.anim_out_to_left);
								}
							}
						} catch (Exception e) {
							PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
						}

					}
				}
			});
		} else {
			layout.setVisibility(View.INVISIBLE);
			tv.setText("");
		}
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
		public RelativeLayout rootLayout;
		public LinearLayout itemLayout1;
		public TextView tvName1;
		public Button iconBtn1;
		public LinearLayout itemLayout2;
		public TextView tvName2;
		public Button iconBtn2;
		public LinearLayout itemLayout3;
		public TextView tvName3;
		public Button iconBtn3;
		public LinearLayout itemLayout4;
		public TextView tvName4;
		public Button iconBtn4;
	}
}
