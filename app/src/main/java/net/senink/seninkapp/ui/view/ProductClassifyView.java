package net.senink.seninkapp.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.senink.piservice.pis.PISManager;
import net.senink.seninkapp.R;

import java.util.List;

/**
 * 自定义三个按钮的组件
 * 
 * @author zhaojunfeng
 * 
 */
public class ProductClassifyView extends RelativeLayout implements
		OnClickListener {

	/**上下文*/
	private Context mContext;
	/**被选中的字体颜色*/
	private int tColor = 0;
	/**监听器*/
	private OnItemClickListner listener;
	/**可显示的类型列表*/
	private List<Integer> vaildClassies = null;

	/**
	 * 构造方法
	 */
	public ProductClassifyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	/**
	 * 构造方法
	 */
	public ProductClassifyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	/**
	 * 构造方法
	 */
	public ProductClassifyView(Context context) {
		super(context);
		this.mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(
				R.layout.product_classify_view, this);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private class ClassifyItem{
		LinearLayout linear;
		public ClassifyItem(LinearLayout l){
			linear = l;
		}
		public Button getButton(){
			if (linear == null)
				return null;
			for (int j = 0; j<linear.getChildCount(); j++){
				View v = linear.getChildAt(j);
				if (v instanceof Button){
					return (Button)v;
				}
			}
			return null;
		};
		public View getSplite(){
			if (linear == null)
				return null;
			for (int j = 0; j<linear.getChildCount(); j++){
				View v = linear.getChildAt(j);
				if (!(v instanceof Button)){
					return v;
				}
			}
			return null;
		}
	}
	private SparseArray<ClassifyItem> btnList = null;
	private void initView() {
		if (btnList == null)
			btnList = new SparseArray<>();
		btnList.clear();

		tColor = mContext.getResources().getColor(R.color.threebtn_bg);

		LinearLayout btn;
		btn =  (LinearLayout) findViewById(R.id.home_classify_switch);
		btnList.put(PISManager.PISERVICE_CATEGORY_SOCKET, new ClassifyItem(btn));
		btn = (LinearLayout) findViewById(R.id.home_classify_light);
		btnList.put(PISManager.PISERVICE_CATEGORY_LIGHT, new ClassifyItem(btn));
		btn = (LinearLayout) findViewById(R.id.home_classify_network);
		btnList.put(PISManager.PISERVICE_CATEGORY_BRIDGE, new ClassifyItem(btn));
		btn = (LinearLayout) findViewById(R.id.home_classify_remoter);
		btnList.put(PISManager.PISERVICE_CATEGORY_REMOTER, new ClassifyItem(btn));
		btn = (LinearLayout) findViewById(R.id.home_classify_wearable);
		btnList.put(PISManager.PISERVICE_CATEGORY_WEAR, new ClassifyItem(btn));

		for(int i = 0; i < btnList.size(); i++){
			ClassifyItem item = btnList.valueAt(i);
			item.getButton().setOnClickListener(this);
		}
		updateSelectState(0);
	}

	public void setProductClassify(List<Integer> classes){

	}

	public void updateView(List<Integer> categories){
		try {
			ClassifyItem lastItem = null;
			for (int i = 0; i < btnList.size(); i++) {
				ClassifyItem item = btnList.valueAt(i);
				Integer cls = btnList.keyAt(i);
				if (categories == null || !categories.contains(cls))
					item.linear.setVisibility(View.GONE);
				else {
					item.linear.setVisibility(View.VISIBLE);
					lastItem = item;
				}
			}
			if (lastItem != null)
				lastItem.getSplite().setVisibility(View.GONE);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 更新按钮的背景和字体颜色
	 * 
	 * @param categoriy
	 */
	public void updateSelectState(int categoriy) {
		try {
			for (int i = 0; i < btnList.size(); i++) {
				int key = btnList.keyAt(i);
				ClassifyItem item = btnList.valueAt(i);
				Button btn = item.getButton();

				if (key == categoriy) {
					if (btn != null)
						btn.setTextColor(Color.WHITE);
					item.getButton().setEnabled(false);
				} else {
					if (btn != null)
						btn.setTextColor(tColor);
					item.getButton().setEnabled(true);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		int categoriy = 0;
		switch (view.getId()) {
		case R.id.home_classify_switch_btn:
			categoriy = PISManager.PISERVICE_CATEGORY_SOCKET;
			break;
		case R.id.home_classify_light_btn:
			categoriy = PISManager.PISERVICE_CATEGORY_LIGHT;
			break;
		case R.id.home_classify_network_btn:
			categoriy = PISManager.PISERVICE_CATEGORY_BRIDGE;
			break;
		case R.id.home_classify_remoter_btn:
			categoriy = PISManager.PISERVICE_CATEGORY_REMOTER;
		    break;
		case R.id.home_classify_wearable_btn:
			categoriy = PISManager.PISERVICE_CATEGORY_WEAR;
			break;
		}

		if (categoriy == 0)
			return;
		updateSelectState(categoriy);
		if (listener != null) {
			listener.onClick(view, categoriy);
		}

	}

	/**
	 * 模拟点击某个按钮
	 */
	public void click(int which) {

		try {
			Button btn = btnList.get(which).getButton();

			if (btn != null)
				btn.performClick();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 设置监听器
	 * 
	 * @param listener
	 */
	public void setOnItemClickListener(OnItemClickListner listener) {
		this.listener = listener;
	}

	public interface OnItemClickListner {
		public void onClick(View view, int which);
	}
}
