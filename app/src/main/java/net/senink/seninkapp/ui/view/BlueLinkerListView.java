package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 用于不可滚动的listview
 * 
 * @author zhaojunfeng
 * 
 * @date 2015-08-24
 */
public class BlueLinkerListView extends ListView{

	public BlueLinkerListView(Context context) {
		super(context);
	}

	public BlueLinkerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}


}
