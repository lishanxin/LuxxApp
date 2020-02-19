package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISBase;

/**
 * 跑马灯适配器
 * @author zhaojunfeng
 * @date 2016-01-20
 */

public class MarqueeAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<PISBase> list;

	public MarqueeAdapter(Context context,List<PISBase> infor) {
		inflater = LayoutInflater.from(context);
		setList(infor);
	}

	public void setList(List<PISBase> infor) {
		if (null == infor) {
			this.list = new ArrayList<PISBase>();
		} else {
			this.list = infor;
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public PISBase getItem(int position) {
		if (position >= list.size()) {
			return null;
		}
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
 
	public List<PISBase> getLights(){
		return list;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.marquee_item, null);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.marquee_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PISBase infor = list.get(position);
		if (infor != null) {
			holder.tvName.setText(infor.getName() == null?"":infor.getName());
		}
		return convertView;
	}
	
	private class ViewHolder {
		public TextView tvName;
	}
}
