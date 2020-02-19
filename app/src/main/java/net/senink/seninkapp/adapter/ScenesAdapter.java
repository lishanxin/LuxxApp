package net.senink.seninkapp.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.util.PictureUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;

/**
 * 用于显示场景列表的适配器
 * @author zhaojunfeng
 * @date 2015-11-24
 * 
 */

public class ScenesAdapter extends BaseAdapter {

    private List<SceneBean> list;
    private LayoutInflater inflater;
    private Context context;
	public ScenesAdapter(Context context,List<SceneBean> beans){
		setList(beans);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}
	
	public void setList(List<SceneBean> beans) {
		if (null == beans) {
			this.list = new ArrayList<SceneBean>();
		} else {
			this.list = beans;
		}
	}
	
	public void removeItem(SceneBean bean) {
		list.remove(bean);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public SceneBean getItem(int position) {
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
		SceneBean bean = list.get(position);
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.lightsetting_listview_item, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.item_groupname);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_icon);
			convertView.setTag(holder);
		} else {
            holder = (ViewHolder)convertView.getTag();
		}
		if (bean != null) {
			holder.tvName.setText(bean.sceneName == null?"":bean.sceneName);
			setPicture(holder.ivIcon,bean.picture);
		}
		return convertView;
	}

	private void setPicture(ImageView ivIcon, String picture) {
		Bitmap bm = null;
		if (!TextUtils.isEmpty(picture) && picture.length() > 1) {
			Bitmap dp = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.icon_default_scene1)).getBitmap();
			bm = PictureUtils.scalePicture(picture, dp.getWidth(), dp.getWidth());
			bm = SDCardUtils.getRoundRectBitmap(bm, 10);
		}
		if (null == bm) {
			bm = PictureUtils.getSmallDefaultBitmap(context, picture);
		}
		ivIcon.setImageBitmap(bm);
	}

	public class ViewHolder{
		public ImageView ivIcon;
		public TextView tvName;
	}
}
