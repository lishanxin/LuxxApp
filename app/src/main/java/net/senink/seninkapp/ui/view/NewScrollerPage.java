package net.senink.seninkapp.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

@SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
public class NewScrollerPage extends ViewGroup {

	// private static final String TAG = "hxj";

	private Scroller scroller;

	private int currentScreenIndex = 0;

	boolean isOnTounchScoller = false;
	private GestureDetector gestureDetector;

	private boolean fling;

	int defaultPage = 0;
	int scrollDuringTime = 0;
	int scrollPage = -1;

	public NewScrollerPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public NewScrollerPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public NewScrollerPage(Context context) {
		super(context);
		initView(context);
	}

	public int getCurrentScreenIndex() {
		return currentScreenIndex;
	}

	@SuppressWarnings("deprecation")
	private void initView(final Context context) {
		this.scroller = new Scroller(context);
		this.gestureDetector = new GestureDetector(new OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}

			@Override
			public boolean onScroll(final MotionEvent e1, final MotionEvent e2,
					final float distanceX, final float distanceY) {

				if ((currentScreenIndex == 0 && distanceX < 0)
						|| (distanceX > 0 && currentScreenIndex == getChildCount() - 1)) {
					return false;
				}
				scrollBy((int) distanceX, 0);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (Math.abs(velocityX) > ViewConfiguration.get(context)
						.getScaledMinimumFlingVelocity()) {
					if (velocityX > 100 && currentScreenIndex > 0) {
						// Log.d(TAG, ">>>>fling to left");
						fling = true;
						scrollDuringTime = 100;
						scrollToScreen(currentScreenIndex - 1);
					} else if (velocityX < -100
							&& currentScreenIndex < getChildCount() - 1) {
						// Log.d(TAG, ">>>>fling to right800");
						fling = true;
						scrollDuringTime = 100;
						scrollToScreen(currentScreenIndex + 1);
					}
				}

				return true;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});

	}

	/**
	 * @param defaultPage
	 *            from 0
	 * @throws Exception
	 */
	public void setDefaultPage(int defaultPage) {
		if (defaultPage > (getChildCount() - 1) || defaultPage < 0) {
			throw new IllegalStateException(
					"defaultPageScreenIndex must be >=0 && <=(getChildCount() - 1)");
		}

		this.defaultPage = defaultPage;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.setVisibility(View.VISIBLE);
			child.measure(MeasureSpec.makeMeasureSpec(getWidth(),
					MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
					getHeight(), MeasureSpec.EXACTLY));
			child.layout(i * getWidth(), 0, getWidth() + i * getWidth(),
					getHeight());

		}
		int delta = currentScreenIndex * getWidth() - getScrollX();
		if (!isOnTounchScoller) {
			scroller.startScroll(getScrollX(), 0, delta, 0, 0);
			if (onScreenChangeListener != null) {
				onScreenChangeListener.onScreenChange(getChildCount(),
						currentScreenIndex);
			}
		} else {
			scroller.startScroll(getScrollX(), 0, delta, 0, scrollDuringTime);
		}
		invalidate();

	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), 0);
			postInvalidate();
			if (scroller.isFinished() && scrollPage != currentScreenIndex) {
				scrollPage = currentScreenIndex;
				if (onScreenChangeListener != null)
					onScreenChangeListener.operateSoft(currentScreenIndex);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isOnTounchScoller = true;
		gestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (!fling) {
				scrollDuringTime = 350;
				snapToDestination();
			}
			fling = false;
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		// Log.i("hj", "onTouchEvent end>>" + event.getAction());
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		if (ev.getPointerCount() > 1) {
			return false;
		}
		return gestureDetector.onTouchEvent(ev);
	}

	public void moveToAppointScreen(int whichScreen) {
		scrollToScreen(whichScreen);
	}

	private void scrollToScreen(int whichScreen) {
		if (getFocusedChild() != null && whichScreen != currentScreenIndex
				&& getFocusedChild() == getChildAt(currentScreenIndex)) {
			getFocusedChild().clearFocus();
		}

		currentScreenIndex = whichScreen;
		final int delta = whichScreen * getWidth() - getScrollX();
		scroller.startScroll(getScrollX(), 0, delta, 0, 300);
		invalidate();

		if (onScreenChangeListener != null) {
			onScreenChangeListener.onScreenChange(getChildCount(),
					currentScreenIndex);
		}
	}

	private void snapToDestination() {
		int page = (getScrollX() + (getWidth() / 2)) / getWidth();
		if (page > getChildCount() - 1) {
			page = currentScreenIndex;
		} else if (page <= 0) {
			page = 0;
		}
		scrollToScreen(page);
	}

	public interface OnScreenChangeListener {
		void onScreenChange(int count, int currentIndex);

		void operateSoft(int currentIndex);
	}

	private OnScreenChangeListener onScreenChangeListener;

	public void setOnScreenChangeListener(
			OnScreenChangeListener onScreenChangeListener) {
		this.onScreenChangeListener = onScreenChangeListener;
	}

}
