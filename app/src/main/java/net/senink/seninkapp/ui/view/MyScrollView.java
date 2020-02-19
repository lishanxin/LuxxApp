package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	private OnSizeChangedListener listener;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		try{
		super.onLayout(changed, l, t, r, b);
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
		if (listener != null) {
			listener.onSizeChanged();
		}
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		this.listener = listener;
	}

	public interface OnSizeChangedListener {
		public void onSizeChanged();
	}
}
