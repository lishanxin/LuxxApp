package net.senink.seninkapp.ui.view.listview;

import java.util.ArrayList;
import java.util.List;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener{

	private SwipeMenuLayout mLayout;
	private SwipeMenu mMenu;
	private OnSwipeItemClickListener onItemClickListener;
	private int position;
    private static final int IMAGEVIEW_ID = 1000;
    private static final int TEXTVIEW_ID = 1001;
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
		super(menu.getContext());
		mMenu = menu;
		List<SwipeMenuItem> items = menu.getMenuItems();
		int id = 0;
		for (SwipeMenuItem item : items) {
			addItem(item, id++);
		}
	}

	@SuppressWarnings("deprecation")
	private void addItem(SwipeMenuItem item, int id) {
		LayoutParams params = new LayoutParams(item.getWidth(),
				LayoutParams.MATCH_PARENT);
		LinearLayout parent = new LinearLayout(getContext());
		parent.setId(id);
		parent.setGravity(Gravity.CENTER);
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setLayoutParams(params);
		parent.setBackgroundDrawable(item.getBackground());
		parent.setOnClickListener(this);
		addView(parent);

		if (item.getIcon() != null) {
			parent.addView(createIcon(item));
		}
		if (!TextUtils.isEmpty(item.getTitle())) {
			parent.addView(createTitle(item));
		}

	}

	private ImageView createIcon(SwipeMenuItem item) {
		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(item.getIcon());
		iv.setId(IMAGEVIEW_ID);
		return iv;
	}

	private TextView createTitle(SwipeMenuItem item) {
		TextView tv = new TextView(getContext());
		tv.setText(item.getTitle());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(item.getTitleSize());
		tv.setTextColor(item.getTitleColor());
		tv.setId(TEXTVIEW_ID);
		return tv;
	}

	@Override
	public void onClick(View v) {
//		if (onItemClickListener != null && mLayout.isOpen()) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(this, mMenu, v.getId());
		}
	}

	public OnSwipeItemClickListener getOnSwipeItemClickListener() {
		return onItemClickListener;
	}

	public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setLayout(SwipeMenuLayout mLayout) {
		this.mLayout = mLayout;
	}
	
	public static interface OnSwipeItemClickListener {
		void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
	}
	
	public List<ImageView> getImageViews(){
		List<ImageView> ivList = null;
		int count = getChildCount();
		if (count > 0) {
			ivList = new ArrayList<ImageView>();
			for (int i = 0; i < count; i++) {
				try{
					View view = getChildAt(i);
					ImageView iv = (ImageView) view.findViewById(IMAGEVIEW_ID);
					if (iv != null) {
						ivList.add(iv);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	    return ivList;
	}
	
	public List<TextView> getTextViews(){
		int count = getChildCount();
		List<TextView> tvList = null;
		if (count > 0) {
			tvList = new ArrayList<TextView>();
			for (int i = 0; i < count; i++) {
				try{
					View view = getChildAt(i);
					TextView tv = (TextView)view.findViewById(TEXTVIEW_ID);
					if (tv != null) {
						tvList.add(tv);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	    return tvList;
	}
}
