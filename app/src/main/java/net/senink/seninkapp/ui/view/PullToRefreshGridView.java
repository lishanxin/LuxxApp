package net.senink.seninkapp.ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.senink.seninkapp.R;

@SuppressLint("SimpleDateFormat")
public class PullToRefreshGridView extends GridView implements OnScrollListener {

	private static final String TAG = "listview";
	private final static int RELEASE_To_REFRESH = 0; // 释放
	private final static int PULL_To_REFRESH = 1;// 下拉刷新
	private final static int REFRESHING = 2; // 正在刷新
	private final static int DONE = 3; // 按下
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	private LayoutInflater inflater;
	private LinearLayout headView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	private int headContentWidth;
	private int headContentHeight;
	private int startY;
	private int firstItemIndex;
	private int state;
	private boolean isBack;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	public Context context;

	public PullToRefreshGridView(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context);
	}

	public View getView() {
		return headView;
	}

	@SuppressLint("InflateParams")
	private void init(Context context) {

		// setCacheColorHint(context.getResources().getColor(R.color.transparent));

		inflater = LayoutInflater.from(context);
		// head 布局文件
		headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		// 下拉箭头
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		// 进度条
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		// 下拉提示 刷新
		tipsTextview = (TextView) headView
				.findViewById(R.id.head_tipsTextView);
		// 最新一次刷新时间
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		// 计算head的高宽
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		// 初始状态是 隐藏掉head 布局
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();
		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		setOnScrollListener(this);
		// 下拉以及恢复动画
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);
		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);
		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		try {
			if (arg1 == OnScrollListener.SCROLL_STATE_IDLE) {
				state = DONE;
				changeHeaderViewByState();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			// 在down时候记录当前Y的位置
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置‘");
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					// 由下拉刷新状态，到done状态
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.v(TAG, "由下拉刷新状态，到done状态");
					}

					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}
				isRecored = false;
				isBack = false;
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight/2)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight/2) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}
					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// 更新headView的size

					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void changeHeaderViewByState() {
		switch (state) {
		// 松开刷新状态
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText(getContext().getResources().getString(
					R.string.refresh_alsert));
			Log.v(TAG, "当前状态，松开刷新");
			break;
		// 下拉刷新
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			// 箭头反转向上
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText(getContext().getResources().getString(
						R.string.xia_la_refresh));
			} else {
				tipsTextview.setText(getContext().getResources().getString(
						R.string.xia_la_refresh));
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;
		// 刷新中 状态
		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(getContext().getResources().getString(
					R.string.refresh_alert));
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v(TAG, "当前状态,正在刷新...");
			break;
		// 刷新完毕
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.arrow);
			tipsTextview.setText(getContext().getResources().getString(
					R.string.xia_la_refresh));
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {

		this.refreshListener = refreshListener;

		isRefreshable = true;

	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		String date = format.format(new Date());
		lastUpdatedTextView.setText(getContext().getResources().getString(
				R.string.last_refresh_alsert) + date);
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		String date = format.format(new Date());
		lastUpdatedTextView.setText(getContext().getResources().getString(
				R.string.last_refresh_alsert) + date);
		super.setAdapter(adapter);
	}
}