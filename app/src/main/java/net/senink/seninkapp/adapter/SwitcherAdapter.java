package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pgyersdk.crash.PgyCrashManager;

import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinSwitch;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PISSwitch;
import net.senink.seninkapp.ui.activity.SwitchDetailActivity;
import net.senink.seninkapp.ui.home.HomeActivity;

import net.senink.piservice.pis.PISBase;

/**
 * 插排列表的适配器
 * 
 * @author zhaojunfeng
 * @date 2015-11-09
 * 
 */

public class SwitcherAdapter extends BaseAdapter {

	private ArrayList<PISBase[]> list;
	private LayoutInflater inflater;
    private Activity context;
	public SwitcherAdapter(Activity context, ArrayList<PISBase[]> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		setList(list);
	}

	/**
	 * refresh the list
	 * 
	 * @param list
	 */
	public void setList(ArrayList<PISBase[]> list) {
		if (null == list) {
			this.list = new ArrayList<PISBase[]>();
		} else {
			this.list = list;
		}
//		int size = this.list.size();
//		if (size > 0) {
//			for (PISBase[] bases : this.list) {
//				for (PISBase base : bases) {
//					if (base != null) {
//						base.request(((PISXinSwitch) base).updateSwitchStatus());
//					}
//				}
//			}
//		}
	}

	/**
	 * 获取列表
	 */
	public ArrayList<PISBase[]> getList(){
		return list;
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
			convertView = inflater.inflate(R.layout.home_swiftlist_item, null);
			holder.ivNumber1 = (ImageView) convertView
					.findViewById(R.id.switcher_number1);
			holder.ivNumber2 = (ImageView) convertView
					.findViewById(R.id.switcher_number2);
			holder.ivNumber3 = (ImageView) convertView
					.findViewById(R.id.switcher_number3);
			holder.ivNumber4 = (ImageView) convertView
					.findViewById(R.id.switcher_number4);
			holder.name1Btn = (Button) convertView
					.findViewById(R.id.switcher_name1);
			holder.name2Btn = (Button) convertView
					.findViewById(R.id.switcher_name2);
			holder.name3Btn = (Button) convertView
					.findViewById(R.id.switcher_name3);
			holder.name4Btn = (Button) convertView
					.findViewById(R.id.switcher_name4);
			holder.item1Layout = (RelativeLayout) convertView
					.findViewById(R.id.switcher_item1);
			holder.item2Layout = (RelativeLayout) convertView
					.findViewById(R.id.switcher_item2);
			holder.item3Layout = (RelativeLayout) convertView
					.findViewById(R.id.switcher_item3);
			holder.item4Layout = (RelativeLayout) convertView
					.findViewById(R.id.switcher_item4);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position) != null) {
			PISBase[] bases = list.get(position);
			int len = bases.length;
			if (len >= 1) {
				setView(holder.ivNumber1, holder.name1Btn, holder.item1Layout,
						bases[0], 0);
			} else {
				setView(null, null, holder.item1Layout, null, 0);
			}
			if (len >= 2) {
				setView(holder.ivNumber2, holder.name2Btn, holder.item2Layout,
						bases[1], 1);
			} else {
				setView(null, null, holder.item2Layout, null, 1);
			}
			if (len >= 3) {
				setView(holder.ivNumber3, holder.name3Btn, holder.item3Layout,
						bases[2], 2);
			} else {
				setView(null, null, holder.item3Layout, null, 2);
			}
			if (len >= 4) {
				setView(holder.ivNumber4, holder.name4Btn, holder.item4Layout,
						bases[3], 3);
			} else {
				setView(null, null, holder.item4Layout, null, 3);
			}
		}
		return convertView;
	}

	/**
	 * 设置组件
	 * 
	 * @param iv
	 * @param btn
	 * @param layout
	 * @param base
	 * @param pos
	 */
	private void setView(ImageView iv, Button btn, RelativeLayout layout,
			PISBase base, int pos) {
		if (null == iv || null == btn) {
			layout.setVisibility(View.GONE);
		} else {
			layout.setVisibility(View.VISIBLE);
			if (base != null && base instanceof PISXinSwitch) {
				boolean isOn = ((PISXinSwitch) base).getSwitchStatus();
				if (isOn) {
					btn.setBackgroundResource(R.drawable.pro_000002000101_selector);
				} else {
					btn.setBackgroundResource(R.drawable.pro_000002000101_off_selector);
				}

				setListener(btn);
				btn.setTag(base);
				btn.setText(base.getName() == null ? "" : base.getName());

				if (base.getStatus() == PISBase.SERVICE_STATUS_ONLINE) {
					if (pos == 0) {
						iv.setBackgroundResource(R.drawable.grade_green_1);
					} else if (pos == 1) {
						iv.setBackgroundResource(R.drawable.grade_green_2);
					} else if (pos == 2) {
						iv.setBackgroundResource(R.drawable.grade_green_3);
					} else if (pos == 3) {
						iv.setBackgroundResource(R.drawable.grade_green_4);
					}
					setGrayOnBackgroud(btn.getBackground(), 1);
					btn.setEnabled(true);
				} else {
					if (pos == 0) {
						iv.setBackgroundResource(R.drawable.grade_red_1);
					} else if (pos == 1) {
						iv.setBackgroundResource(R.drawable.grade_red_2);
					} else if (pos == 2) {
						iv.setBackgroundResource(R.drawable.grade_red_3);
					} else if (pos == 3) {
						iv.setBackgroundResource(R.drawable.grade_red_4);
					}
					setGrayOnBackgroud(btn.getBackground(), 0);
					btn.setEnabled(false);
				}
			}
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
	private void setListener(Button btn){
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Object obj = v.getTag();
				if (obj != null) {
					try{
						PISBase base = (PISBase) obj;
						Intent intent = new Intent(context,SwitchDetailActivity.class);
						intent.putExtra(HomeActivity.VALUE_KEY, base.getPISKeyString());
						context.startActivity(intent);
						context.overridePendingTransition(R.anim.anim_in_from_right,
								R.anim.anim_out_to_left);
					}catch(Exception e){
						PgyCrashManager.reportCaughtException(PISManager.getDefaultContext(), e);
					}
				}
			}
		});
	}
	private class ViewHolder {
		public ImageView ivNumber1;
		public ImageView ivNumber2;
		public ImageView ivNumber3;
		public ImageView ivNumber4;
		public Button name1Btn;
		public Button name2Btn;
		public Button name3Btn;
		public Button name4Btn;
		public RelativeLayout item1Layout;
		public RelativeLayout item2Layout;
		public RelativeLayout item3Layout;
		public RelativeLayout item4Layout;
	}
}
