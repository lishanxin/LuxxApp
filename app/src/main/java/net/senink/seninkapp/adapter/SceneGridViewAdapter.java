package net.senink.seninkapp.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.senink.seninkapp.R;
import net.senink.seninkapp.ui.entity.SceneBean;
import net.senink.seninkapp.ui.gridview.BaseDynamicGridAdapter;
import net.senink.seninkapp.ui.util.PictureUtils;
import net.senink.seninkapp.ui.util.SDCardUtils;

/**
 * 用于显示场景列表的适配器(用于gridview)
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 * 
 */

public class SceneGridViewAdapter extends BaseDynamicGridAdapter {

	private LayoutInflater inflater;
	private Context context;
	private View tvTip;
	private int columnNum = 0;

	public SceneGridViewAdapter(Context context, List<SceneBean> beans,
			View view, int count) {
		super(context, beans, count);
		this.columnNum = count;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.tvTip = view;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		SceneBean bean = (SceneBean) getItem(position);
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.gridview_scene_item, null);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.scene_item_title);
			holder.spaceView = convertView.findViewById(R.id.scene_item_space);
			holder.ivIcon = (RelativeLayout) convertView
					.findViewById(R.id.scene_item_icon);
			holder.ivLoading =  (ImageView) convertView.findViewById(R.id.scene_item_loadding);
			holder.ivSelected =  (ImageView) convertView.findViewById(R.id.scene_item_selected);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (bean != null) {
			holder.tvName.setText(bean.sceneName == null ? "" : bean.sceneName);
			setPicture(holder.ivIcon, bean.picture);
			if (bean.selected) {
				holder.ivSelected.setVisibility(View.VISIBLE);
			}else{
				holder.ivSelected.setVisibility(View.GONE);
			}
		}
		if (columnNum == 3) {
			if (position == 3 || position == 4 || position == 5) {
				holder.spaceView.setVisibility(View.VISIBLE);
				tvTip.setVisibility(View.VISIBLE);
			} else {
				if (getCount() < 4) {
					tvTip.setVisibility(View.GONE);
				}
			}
		} else {
			if (position == 4 || position == 5 || position == 6
					|| position == 7) {
				holder.spaceView.setVisibility(View.VISIBLE);
				tvTip.setVisibility(View.VISIBLE);
			} else {
				if (getCount() < 4) {
					tvTip.setVisibility(View.GONE);
				}
			}
		}
		return convertView;
	}

	@SuppressWarnings("deprecation")
	private void setPicture(RelativeLayout ivIcon, String picture) {
		Bitmap bm = null;
		if (!TextUtils.isEmpty(picture) && picture.length() > 1) {
			Bitmap dp = ((BitmapDrawable) context.getResources().getDrawable(
					R.drawable.icon_scene_default)).getBitmap();
			bm = PictureUtils.scalePicture(picture, dp.getWidth(),
					dp.getWidth());
			bm = SDCardUtils.getRoundRectBitmap(bm, 10);
		}
		if (null == bm) {
			bm = PictureUtils.getBigDefaultBitmap(context, picture);
		}
		try {
			ivIcon.setBackground(new BitmapDrawable(context.getResources(),
					bm));
		} catch (Exception e) {
			ivIcon.setBackgroundDrawable(new BitmapDrawable(context
					.getResources(), bm));
		}
	}

	public class ViewHolder {
		public RelativeLayout ivIcon;
		public TextView tvName;
		public View spaceView;
		public ImageView ivLoading;
		public ImageView ivSelected;
	}
}
