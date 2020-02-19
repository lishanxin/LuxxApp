package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import net.senink.seninkapp.R;

/**
 * 选择框
 * 
 * @author zhaojunfeng
 * 
 * @date 2016-01-12
 */
public class CheckButton extends Button implements OnClickListener{
	private Drawable checkedDrawable = null;
	private Drawable noCheckedDrawable = null;
	private boolean isChecked;
    private OnCheckedChangeListener listener;
	public CheckButton(Context context) {
		super(context);
		init(context);
	}


	public CheckButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CheckButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}
   
	private void init(Context context) {
		Resources res = context.getResources();
		if (null == checkedDrawable) {
			checkedDrawable = res.getDrawable(R.drawable.icon_light_on);
		}
		if (null == noCheckedDrawable) {
			noCheckedDrawable = res.getDrawable(R.drawable.icon_light_off);
		}
	}
	
	private void init(Context context, AttributeSet attrs) {
         TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckButtonAttr);
         checkedDrawable = a.getDrawable(0);
//         noCheckedDrawable = a.getDrawable(1);
		 a.recycle();
		 init(context);
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean ischecked) {
		this.isChecked = ischecked;
		if (ischecked) {
			if (checkedDrawable != null) {
				setBackground(checkedDrawable);
			}
		}else{
			if (noCheckedDrawable != null) {
				setBackground(noCheckedDrawable);
			}
		}
	}
   
	public void setChecked() {
		this.isChecked = !this.isChecked;
		setChecked(isChecked);
	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener l){
		this.listener = l;
		setOnClickListener(this);
	}
	
    public interface OnCheckedChangeListener{
    	void onCheckedChanged(View view, boolean isChecked);
    }

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onCheckedChanged(v, !isChecked);
		}
	}
    
}
