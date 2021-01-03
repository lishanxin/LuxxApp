/*      */ package com.datepicker;
/*      */ 
/*      */ import android.annotation.SuppressLint;
/*      */ import android.content.Context;
/*      */ import android.database.DataSetObserver;
/*      */ import android.graphics.Canvas;
/*      */ import android.graphics.Paint;
/*      */ import android.graphics.drawable.Drawable;
/*      */ import android.graphics.drawable.GradientDrawable;
/*      */ import android.util.AttributeSet;
/*      */ import android.view.MotionEvent;
/*      */ import android.view.View;
/*      */ import android.view.ViewGroup;
/*      */ import android.view.animation.Interpolator;
/*      */ import android.widget.LinearLayout;
/*      */ import com.datepicker.adapter.WheelViewAdapter;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class WheelView
/*      */   extends View
/*      */ {
/*   35 */   private int[] SHADOWS_COLORS = new int[] { -1, -805306369, 
/*   36 */       -1879048193 };
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int ITEM_OFFSET_PERCENT = 0;
/*      */ 
/*      */   
/*      */   private static final int PADDING = 10;
/*      */ 
/*      */   
/*      */   private static final int DEF_VISIBLE_ITEMS = 5;
/*      */ 
/*      */   
/*   49 */   private int currentItem = 0;
/*      */ 
/*      */   
/*   52 */   private int visibleItems = 5;
/*      */ 
/*      */   
/*   55 */   private int itemHeight = 0;
/*      */ 
/*      */   
/*      */   private Drawable centerDrawable;
/*      */ 
/*      */   
/*   61 */   private int wheelBackground = 2130837504;
/*   62 */   private int wheelForeground = 2130837505;
/*      */ 
/*      */   
/*      */   private GradientDrawable topShadow;
/*      */ 
/*      */   
/*      */   private GradientDrawable bottomShadow;
/*      */ 
/*      */   
/*      */   private boolean drawShadows = true;
/*      */   
/*      */   private WheelScroller scroller;
/*      */   
/*      */   private boolean isScrollingPerformed;
/*      */   
/*      */   private int scrollingOffset;
/*      */   
/*      */   boolean isCyclic = false;
/*      */   
/*      */   private LinearLayout itemsLayout;
/*      */   
/*      */   private int firstItem;
/*      */   
/*      */   private WheelViewAdapter viewAdapter;
/*      */   
/*      */   private boolean isLeft;
/*      */   
/*   89 */   private WheelRecycle recycle = new WheelRecycle(this);
/*      */ 
/*      */   
/*   92 */   private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
/*   93 */   private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();
/*   94 */   private List<OnWheelClickedListener> clickingListeners = new LinkedList<OnWheelClickedListener>();
/*      */   
/*   96 */   String label = "";
/*      */   WheelScroller.ScrollingListener scrollingListener;
/*      */   private DataSetObserver dataObserver;
/*      */   
/*      */   public WheelView(Context context, AttributeSet attrs, int defStyle)
/*      */   {
/*  102 */     super(context, attrs, defStyle);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  133 */     this.scrollingListener = new WheelScroller.ScrollingListener()
/*      */       {
/*      */         public void onStarted() {
/*  136 */           WheelView.this.isScrollingPerformed = true;
/*  137 */           WheelView.this.notifyScrollingListenersAboutStart();
/*      */         }
/*      */ 
/*      */         
/*      */         public void onScroll(int distance) {
/*  142 */           WheelView.this.doScroll(distance);
/*      */           
/*  144 */           int height = WheelView.this.getHeight();
/*  145 */           if (WheelView.this.scrollingOffset > height) {
/*  146 */             WheelView.this.scrollingOffset = height;
/*  147 */             WheelView.this.scroller.stopScrolling();
/*  148 */           } else if (WheelView.this.scrollingOffset < -height) {
/*  149 */             WheelView.this.scrollingOffset = -height;
/*  150 */             WheelView.this.scroller.stopScrolling();
/*      */           } 
/*      */         }
/*      */ 
/*      */         
/*      */         public void onFinished() {
/*  156 */           if (WheelView.this.isScrollingPerformed) {
/*  157 */             WheelView.this.notifyScrollingListenersAboutEnd();
/*  158 */             WheelView.this.isScrollingPerformed = false;
/*      */           } 
/*      */           
/*  161 */           WheelView.this.scrollingOffset = 0;
/*  162 */           WheelView.this.invalidate();
/*      */         }
/*      */ 
/*      */         
/*      */         public void onJustify() {
/*  167 */           if (Math.abs(WheelView.this.scrollingOffset) > 1) {
/*  168 */             WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);
/*      */           }
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  214 */     this.dataObserver = new DataSetObserver()
/*      */       {
/*      */         public void onChanged() {
/*  217 */           WheelView.this.invalidateWheel(false);
/*      */         }
/*      */         
/*      */         public void onInvalidated()
/*      */         {
/*  222 */           WheelView.this.invalidateWheel(true); } }; initData(context); } public WheelView(Context context, AttributeSet attrs) { super(context, attrs); this.scrollingListener = new WheelScroller.ScrollingListener() { public void onStarted() { WheelView.this.isScrollingPerformed = true; WheelView.this.notifyScrollingListenersAboutStart(); } public void onScroll(int distance) { WheelView.this.doScroll(distance); int height = WheelView.this.getHeight(); if (WheelView.this.scrollingOffset > height) { WheelView.this.scrollingOffset = height; WheelView.this.scroller.stopScrolling(); } else if (WheelView.this.scrollingOffset < -height) { WheelView.this.scrollingOffset = -height; WheelView.this.scroller.stopScrolling(); }  } public void onInvalidated() { WheelView.this.invalidateWheel(true); } public void onFinished() { if (WheelView.this.isScrollingPerformed) { WheelView.this.notifyScrollingListenersAboutEnd(); WheelView.this.isScrollingPerformed = false; }  WheelView.this.scrollingOffset = 0; WheelView.this.invalidate(); } public void onJustify() { if (Math.abs(WheelView.this.scrollingOffset) > 1) WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);  } }; this.dataObserver = new DataSetObserver() { public void onChanged() { WheelView.this.invalidateWheel(false); } }; initData(context); } public WheelView(Context context) { super(context); this.scrollingListener = new WheelScroller.ScrollingListener() { public void onStarted() { WheelView.this.isScrollingPerformed = true; WheelView.this.notifyScrollingListenersAboutStart(); } public void onScroll(int distance) { WheelView.this.doScroll(distance); int height = WheelView.this.getHeight(); if (WheelView.this.scrollingOffset > height) { WheelView.this.scrollingOffset = height; WheelView.this.scroller.stopScrolling(); } else if (WheelView.this.scrollingOffset < -height) { WheelView.this.scrollingOffset = -height; WheelView.this.scroller.stopScrolling(); }  } public void onFinished() { if (WheelView.this.isScrollingPerformed) { WheelView.this.notifyScrollingListenersAboutEnd(); WheelView.this.isScrollingPerformed = false; }  WheelView.this.scrollingOffset = 0; WheelView.this.invalidate(); } public void onJustify() { if (Math.abs(WheelView.this.scrollingOffset) > 1) WheelView.this.scroller.scroll(WheelView.this.scrollingOffset, 0);  } }; this.dataObserver = new DataSetObserver() { public void onChanged() { WheelView.this.invalidateWheel(false); } public void onInvalidated() { WheelView.this.invalidateWheel(true); } }
/*      */       ;
/*      */     initData(context); } private void initData(Context context) { this.scroller = new WheelScroller(getContext(), this.scrollingListener); } public void setInterpolator(Interpolator interpolator) {
/*      */     this.scroller.setInterpolator(interpolator);
/*      */   } public int getVisibleItems() {
/*      */     return this.visibleItems;
/*      */   } public void setVisibleItems(int count) {
/*      */     this.visibleItems = count;
/*      */   } public WheelViewAdapter getViewAdapter() {
/*      */     return this.viewAdapter;
/*      */   }
/*      */   public void setViewAdapter(WheelViewAdapter viewAdapter, boolean isLeft) {
/*  234 */     this.isLeft = isLeft;
/*  235 */     if (this.viewAdapter != null) {
/*  236 */       this.viewAdapter.unregisterDataSetObserver(this.dataObserver);
/*      */     }
/*      */     
/*  240 */     this.viewAdapter = viewAdapter;
/*  241 */     if (this.viewAdapter != null) {
/*  242 */       this.viewAdapter.registerDataSetObserver(this.dataObserver);
/*      */     }
/*  244 */     invalidateWheel(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addChangingListener(OnWheelChangedListener listener) {
/*  254 */     this.changingListeners.add(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeChangingListener(OnWheelChangedListener listener) {
/*  264 */     this.changingListeners.remove(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void notifyChangingListeners(int oldValue, int newValue) {
/*  276 */     for (OnWheelChangedListener listener : this.changingListeners) {
/*  277 */       listener.onChanged(this, oldValue, newValue);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addScrollingListener(OnWheelScrollListener listener) {
/*  288 */     this.scrollingListeners.add(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeScrollingListener(OnWheelScrollListener listener) {
/*  298 */     this.scrollingListeners.remove(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void notifyScrollingListenersAboutStart() {
/*  305 */     for (OnWheelScrollListener listener : this.scrollingListeners) {
/*  306 */       listener.onScrollingStarted(this);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void notifyScrollingListenersAboutEnd() {
/*  314 */     for (OnWheelScrollListener listener : this.scrollingListeners) {
/*  315 */       listener.onScrollingFinished(this);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addClickingListener(OnWheelClickedListener listener) {
/*  326 */     this.clickingListeners.add(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeClickingListener(OnWheelClickedListener listener) {
/*  336 */     this.clickingListeners.remove(listener);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void notifyClickListenersAboutClick(int item) {
/*  343 */     for (OnWheelClickedListener listener : this.clickingListeners) {
/*  344 */       listener.onItemClicked(this, item);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCurrentItem() {
/*  354 */     return this.currentItem;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCurrentItem(int index, boolean animated) {
/*  366 */     if (this.viewAdapter == null || this.viewAdapter.getItemsCount() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*  370 */     int itemCount = this.viewAdapter.getItemsCount();
/*  371 */     if (index < 0 || index >= itemCount) {
/*  372 */       if (this.isCyclic) {
/*  373 */         while (index < 0) {
/*  374 */           index += itemCount;
/*      */         }
/*  376 */         index %= itemCount;
/*      */       } else {
/*      */         return;
/*      */       } 
/*      */     }
/*  381 */     if (index != this.currentItem) {
/*  382 */       if (animated) {
/*  383 */         int itemsToScroll = index - this.currentItem;
/*  384 */         if (this.isCyclic) {
/*  385 */           int scroll = itemCount + Math.min(index, this.currentItem) - 
/*  386 */             Math.max(index, this.currentItem);
/*  387 */           if (scroll < Math.abs(itemsToScroll)) {
/*  388 */             itemsToScroll = (itemsToScroll < 0) ? scroll : -scroll;
/*      */           }
/*      */         } 
/*  391 */         scroll(itemsToScroll, 0);
/*      */       } else {
/*  393 */         this.scrollingOffset = 0;
/*      */         
/*  395 */         int old = this.currentItem;
/*  396 */         this.currentItem = index;
/*      */         
/*  398 */         notifyChangingListeners(old, this.currentItem);
/*      */         
/*  400 */         invalidate();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCurrentItem(int index) {
/*  412 */     setCurrentItem(index, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCyclic() {
/*  422 */     return this.isCyclic;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCyclic(boolean isCyclic) {
/*  432 */     this.isCyclic = isCyclic;
/*  433 */     invalidateWheel(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean drawShadows() {
/*  442 */     return this.drawShadows;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDrawShadows(boolean drawShadows) {
/*  452 */     this.drawShadows = drawShadows;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setShadowColor(int start, int middle, int end) {
/*  463 */     this.SHADOWS_COLORS = new int[] { start, middle, end };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWheelBackground(int resource) {
/*  472 */     this.wheelBackground = resource;
/*  473 */     setBackgroundResource(this.wheelBackground);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWheelForeground(int resource) {
/*  482 */     this.wheelForeground = resource;
/*  483 */     this.centerDrawable = getContext().getResources().getDrawable(
/*  484 */         this.wheelForeground);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void invalidateWheel(boolean clearCaches) {
/*  494 */     if (clearCaches) {
/*  495 */       this.recycle.clearAll();
/*  496 */       if (this.itemsLayout != null) {
/*  497 */         this.itemsLayout.removeAllViews();
/*      */       }
/*  499 */       this.scrollingOffset = 0;
/*  500 */     } else if (this.itemsLayout != null) {
/*      */       
/*  502 */       this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
/*      */     } 
/*      */     
/*  505 */     invalidate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initResourcesIfNecessary() {
/*  512 */     if (this.centerDrawable == null) {
/*  513 */       this.centerDrawable = getContext().getResources().getDrawable(
/*  514 */           this.wheelForeground);
/*      */     }
/*      */     
/*  517 */     if (this.topShadow == null) {
/*  518 */       this.topShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, 
/*  519 */           this.SHADOWS_COLORS);
/*      */     }
/*      */     
/*  522 */     if (this.bottomShadow == null) {
/*  523 */       this.bottomShadow = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, 
/*  524 */           this.SHADOWS_COLORS);
/*      */     }
/*      */     
/*  527 */     setBackgroundResource(this.wheelBackground);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getDesiredHeight(LinearLayout layout) {
/*  538 */     if (layout != null && layout.getChildAt(0) != null) {
/*  539 */       this.itemHeight = layout.getChildAt(0).getMeasuredHeight();
/*      */     }
/*      */     
/*  542 */     int desired = this.itemHeight * this.visibleItems - this.itemHeight * 
/*  543 */       0 / 50;
/*      */     
/*  545 */     return Math.max(desired, getSuggestedMinimumHeight());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getItemHeight() {
/*  554 */     if (this.itemHeight != 0) {
/*  555 */       return this.itemHeight;
/*      */     }
/*      */     
/*  558 */     if (this.itemsLayout != null && this.itemsLayout.getChildAt(0) != null) {
/*  559 */       this.itemHeight = this.itemsLayout.getChildAt(0).getHeight();
/*  560 */       return this.itemHeight;
/*      */     } 
/*      */     
/*  563 */     return getHeight() / this.visibleItems;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @SuppressLint("WrongConstant")
private int calculateLayoutWidth(int widthSize, int mode) {
/*  576 */     initResourcesIfNecessary();
/*      */ 
/*      */     
/*  579 */     this.itemsLayout.setLayoutParams(new ViewGroup.LayoutParams(-2, 
/*  580 */           -2));
/*  581 */     this.itemsLayout
/*  582 */       .measure(MeasureSpec.makeMeasureSpec(widthSize,
/*  583 */           0), MeasureSpec.makeMeasureSpec(
/*  584 */           0, 0));
/*  585 */     int width = this.itemsLayout.getMeasuredWidth();
/*      */     
/*  587 */     if (mode == 1073741824) {
/*  588 */       width = widthSize;
/*      */     } else {
/*  590 */       width += 20;
/*      */ 
/*      */       
/*  593 */       width = Math.max(width, getSuggestedMinimumWidth());
/*      */       
/*  595 */       if (mode == Integer.MIN_VALUE && widthSize < width) {
/*  596 */         width = widthSize;
/*      */       }
/*      */     } 
/*      */     
/*  600 */     this.itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 20,
/*  601 */           1073741824), MeasureSpec.makeMeasureSpec(0,
/*  602 */           0));
/*      */     
/*  604 */     return width;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
/*  609 */     int height, widthMode = MeasureSpec.getMode(widthMeasureSpec);
/*  610 */     int heightMode = MeasureSpec.getMode(heightMeasureSpec);
/*  611 */     int widthSize = MeasureSpec.getSize(widthMeasureSpec);
/*  612 */     int heightSize = MeasureSpec.getSize(heightMeasureSpec);
/*      */     
/*  614 */     buildViewForMeasuring();
/*      */     
/*  616 */     int width = calculateLayoutWidth(widthSize, widthMode);
/*      */ 
/*      */     
/*  619 */     if (heightMode == 1073741824) {
/*  620 */       height = heightSize;
/*      */     } else {
/*  622 */       height = getDesiredHeight(this.itemsLayout);
/*      */       
/*  624 */       if (heightMode == Integer.MIN_VALUE) {
/*  625 */         height = Math.min(height, heightSize);
/*      */       }
/*      */     } 
/*      */     
/*  629 */     setMeasuredDimension(width, height);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void onLayout(boolean changed, int l, int t, int r, int b) {
/*  634 */     layout(r - l, b - t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void layout(int width, int height) {
/*  646 */     int itemsWidth = width - 20;
/*      */     
/*  648 */     this.itemsLayout.layout(0, 0, itemsWidth, height);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void onDraw(Canvas canvas) {
/*  653 */     super.onDraw(canvas);
/*  654 */     if (this.viewAdapter != null && this.viewAdapter.getItemsCount() > 0) {
/*  655 */       updateView();
/*      */       
/*  657 */       drawItems(canvas);
/*  658 */       drawCenterRect(canvas);
/*      */     } 
/*  660 */     if (this.drawShadows) {
/*  661 */       drawShadows(canvas);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawShadows(Canvas canvas) {
/*  675 */     int height = (int)(3.2D * getItemHeight());
/*      */     
/*  677 */     this.topShadow.setBounds(0, 0, getWidth(), height);
/*  678 */     this.topShadow.draw(canvas);
/*      */     
/*  680 */     this.bottomShadow
/*  681 */       .setBounds(0, getHeight() - height, getWidth(), getHeight());
/*  682 */     this.bottomShadow.draw(canvas);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawItems(Canvas canvas) {
/*  692 */     canvas.save();
/*      */     
/*  694 */     int top = (this.currentItem - this.firstItem) * getItemHeight() + (
/*  695 */       getItemHeight() - getHeight()) / 2;
/*  696 */     canvas.translate(10.0F, (-top + this.scrollingOffset));
/*      */     
/*  698 */     this.itemsLayout.draw(canvas);
/*      */     
/*  700 */     canvas.restore();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void drawCenterRect(Canvas canvas) {
/*  710 */     int center = getHeight() / 2;
/*  711 */     int offset = (int)((getItemHeight() / 2) * 1.2D);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  718 */     Paint paint = new Paint();
/*  719 */     paint.setColor(-855310);
/*      */     
/*  721 */     paint.setStrokeWidth(3.0F);
/*  722 */     if (this.isLeft) {
/*      */       
/*  724 */       canvas.drawLine((getWidth() / 2), (center - offset), getWidth(), (center - offset), paint);
/*      */       
/*  726 */       canvas.drawLine((getWidth() / 2), (center + offset), getWidth(), (center + offset), paint);
/*      */     } else {
/*      */       
/*  729 */       canvas.drawLine(0.0F, (center - offset), (getWidth() / 2), (center - offset), paint);
/*      */       
/*  731 */       canvas.drawLine(0.0F, (center + offset), (getWidth() / 2), (center + offset), paint);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @SuppressLint({"ClickableViewAccessibility"})
/*      */   public boolean onTouchEvent(MotionEvent event) {
/*  739 */     if (!isEnabled() || getViewAdapter() == null) {
/*  740 */       return true;
/*      */     }
/*      */     
/*  743 */     switch (event.getAction()) {
/*      */       case 2:
/*  745 */         if (getParent() != null) {
/*  746 */           getParent().requestDisallowInterceptTouchEvent(true);
/*      */         }
/*      */         break;
/*      */       
/*      */       case 1:
/*  751 */         if (!this.isScrollingPerformed) {
/*  752 */           int distance = (int)event.getY() - getHeight() / 2;
/*  753 */           if (distance > 0) {
/*  754 */             distance += getItemHeight() / 2;
/*      */           } else {
/*  756 */             distance -= getItemHeight() / 2;
/*      */           } 
/*  758 */           int items = distance / getItemHeight();
/*  759 */           if (items != 0 && isValidItemIndex(this.currentItem + items)) {
/*  760 */             notifyClickListenersAboutClick(this.currentItem + items);
/*      */           }
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/*  766 */     return this.scroller.onTouchEvent(event);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void doScroll(int delta) {
/*  776 */     this.scrollingOffset += delta;
/*      */     
/*  778 */     int itemHeight = getItemHeight();
/*  779 */     int count = this.scrollingOffset / itemHeight;
/*      */     
/*  781 */     int pos = this.currentItem - count;
/*  782 */     int itemCount = this.viewAdapter.getItemsCount();
/*      */     
/*  784 */     int fixPos = this.scrollingOffset % itemHeight;
/*  785 */     if (Math.abs(fixPos) <= itemHeight / 2) {
/*  786 */       fixPos = 0;
/*      */     }
/*  788 */     if (this.isCyclic && itemCount > 0) {
/*  789 */       if (fixPos > 0) {
/*  790 */         pos--;
/*  791 */         count++;
/*  792 */       } else if (fixPos < 0) {
/*  793 */         pos++;
/*  794 */         count--;
/*      */       } 
/*      */       
/*  797 */       while (pos < 0) {
/*  798 */         pos += itemCount;
/*      */       }
/*  800 */       pos %= itemCount;
/*      */     
/*      */     }
/*  803 */     else if (pos < 0) {
/*  804 */       count = this.currentItem;
/*  805 */       pos = 0;
/*  806 */     } else if (pos >= itemCount) {
/*  807 */       count = this.currentItem - itemCount + 1;
/*  808 */       pos = itemCount - 1;
/*  809 */     } else if (pos > 0 && fixPos > 0) {
/*  810 */       pos--;
/*  811 */       count++;
/*  812 */     } else if (pos < itemCount - 1 && fixPos < 0) {
/*  813 */       pos++;
/*  814 */       count--;
/*      */     } 
/*      */ 
/*      */     
/*  818 */     int offset = this.scrollingOffset;
/*  819 */     if (pos != this.currentItem) {
/*  820 */       setCurrentItem(pos, false);
/*      */     } else {
/*  822 */       invalidate();
/*      */     } 
/*      */ 
/*      */     
/*  826 */     this.scrollingOffset = offset - count * itemHeight;
/*  827 */     if (this.scrollingOffset > getHeight()) {
/*  828 */       this.scrollingOffset = this.scrollingOffset % getHeight() + getHeight();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void scroll(int itemsToScroll, int time) {
/*  841 */     int distance = itemsToScroll * getItemHeight() - this.scrollingOffset;
/*  842 */     this.scroller.scroll(distance, time);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ItemsRange getItemsRange() {
/*  851 */     if (getItemHeight() == 0) {
/*  852 */       return null;
/*      */     }
/*      */     
/*  855 */     int first = this.currentItem;
/*  856 */     int count = 1;
/*      */     
/*  858 */     while (count * getItemHeight() < getHeight()) {
/*  859 */       first--;
/*  860 */       count += 2;
/*      */     } 
/*      */     
/*  863 */     if (this.scrollingOffset != 0) {
/*  864 */       if (this.scrollingOffset > 0) {
/*  865 */         first--;
/*      */       }
/*  867 */       count++;
/*      */ 
/*      */       
/*  870 */       int emptyItems = this.scrollingOffset / getItemHeight();
/*  871 */       first -= emptyItems;
/*  872 */       count = (int)(count + Math.asin(emptyItems));
/*      */     } 
/*  874 */     return new ItemsRange(first, count);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean rebuildItems() {
/*  883 */     boolean updated = false;
/*  884 */     ItemsRange range = getItemsRange();
/*  885 */     if (this.itemsLayout != null) {
/*  886 */       int j = this.recycle.recycleItems(this.itemsLayout, this.firstItem, range);
/*  887 */       updated = (this.firstItem != j);
/*  888 */       this.firstItem = j;
/*      */     } else {
/*  890 */       createItemsLayout();
/*  891 */       updated = true;
/*      */     } 
/*      */     
/*  894 */     if (!updated) {
/*  895 */       updated = !(this.firstItem == range.getFirst() && 
/*  896 */         this.itemsLayout.getChildCount() == range.getCount());
/*      */     }
/*      */     
/*  899 */     if (this.firstItem > range.getFirst() && this.firstItem <= range.getLast()) {
/*  900 */       for (int j = this.firstItem - 1; j >= range.getFirst() && 
/*  901 */         addViewItem(j, true); j--)
/*      */       {
/*      */         
/*  904 */         this.firstItem = j;
/*      */       }
/*      */     } else {
/*  907 */       this.firstItem = range.getFirst();
/*      */     } 
/*      */     
/*  910 */     int first = this.firstItem;
/*  911 */     for (int i = this.itemsLayout.getChildCount(); i < range.getCount(); i++) {
/*  912 */       if (!addViewItem(this.firstItem + i, false) && 
/*  913 */         this.itemsLayout.getChildCount() == 0) {
/*  914 */         first++;
/*      */       }
/*      */     } 
/*  917 */     this.firstItem = first;
/*      */     
/*  919 */     return updated;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateView() {
/*  927 */     if (rebuildItems()) {
/*  928 */       calculateLayoutWidth(getWidth(), 1073741824);
/*  929 */       layout(getWidth(), getHeight());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @SuppressLint("WrongConstant")
private void createItemsLayout() {
/*  937 */     if (this.itemsLayout == null) {
/*  938 */       this.itemsLayout = new LinearLayout(getContext());
/*  939 */       this.itemsLayout.setOrientation(1);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void buildViewForMeasuring() {
/*  948 */     if (this.itemsLayout != null) {
/*  949 */       this.recycle.recycleItems(this.itemsLayout, this.firstItem, new ItemsRange());
/*      */     } else {
/*  951 */       createItemsLayout();
/*      */     } 
/*      */ 
/*      */     
/*  955 */     int addItems = this.visibleItems / 2;
/*  956 */     for (int i = this.currentItem + addItems; i >= this.currentItem - addItems; i--) {
/*  957 */       if (addViewItem(i, true)) {
/*  958 */         this.firstItem = i;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean addViewItem(int index, boolean first) {
/*  973 */     View view = getItemView(index);
/*  974 */     if (view != null) {
/*  975 */       if (first) {
/*  976 */         this.itemsLayout.addView(view, 0);
/*      */       } else {
/*  978 */         this.itemsLayout.addView(view);
/*      */       } 
/*      */       
/*  981 */       return true;
/*      */     } 
/*      */     
/*  984 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isValidItemIndex(int index) {
/*  995 */     return (this.viewAdapter != null && 
/*  996 */       this.viewAdapter.getItemsCount() > 0 && (
/*  997 */       this.isCyclic || (index >= 0 && 
/*  998 */       index < this.viewAdapter.getItemsCount())));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private View getItemView(int index) {
/* 1009 */     if (this.viewAdapter == null || this.viewAdapter.getItemsCount() == 0) {
/* 1010 */       return null;
/*      */     }
/* 1012 */     int count = this.viewAdapter.getItemsCount();
/* 1013 */     if (!isValidItemIndex(index)) {
/* 1014 */       return this.viewAdapter
/* 1015 */         .getEmptyItem(this.recycle.getEmptyItem(), (ViewGroup)this.itemsLayout);
/*      */     }
/* 1017 */     while (index < 0) {
/* 1018 */       index = count + index;
/*      */     }
/*      */ 
/*      */     
/* 1022 */     index %= count;
/* 1023 */     return this.viewAdapter.getItem(index, this.recycle.getItem(), (ViewGroup)this.itemsLayout);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void stopScrolling() {
/* 1030 */     this.scroller.stopScrolling();
/*      */   }
/*      */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/WheelView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */