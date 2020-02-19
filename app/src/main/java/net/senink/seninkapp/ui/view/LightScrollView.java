package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

public class LightScrollView extends ScrollView {
	
	private ListView listView;
	public LightScrollView(Context context) {
		super(context);
	}

	public LightScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (listView != null) {
			listView.onTouchEvent(ev);
		}
//		else{
		  return super.onTouchEvent(ev);
//		}
	}
	
	public void setListView(ListView listview){
		this.listView = listview;
	}
}
