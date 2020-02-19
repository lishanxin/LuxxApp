package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 用于不可滚动的listview
 * @author zhaojunfeng
 *
 *@date 2015-07-21
 */
public class MyListView extends ListView {
	private onChildNumberChangeListener listener;
	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (listener != null && getChildCount() > 0) {
			listener.onNumberChange();
		}
	}
	
	public void setOnChildNumberChangeListener(onChildNumberChangeListener listener){
		this.listener = listener;
	}
	
	public interface onChildNumberChangeListener{
		public void onNumberChange();
	}
}
