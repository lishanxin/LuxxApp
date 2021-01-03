/*     */ package com.datepicker;
/*     */ 
/*     */ import android.annotation.SuppressLint;
/*     */ import android.content.Context;
/*     */ import android.os.Handler;
/*     */ import android.os.Message;
/*     */ import android.view.GestureDetector;
/*     */ import android.view.MotionEvent;
/*     */ import android.view.animation.Interpolator;
/*     */ import android.widget.Scroller;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WheelScroller
/*     */ {
/*     */   private static final int SCROLLING_DURATION = 400;
/*     */   public static final int MIN_DELTA_FOR_SCROLLING = 1;
/*     */   private ScrollingListener listener;
/*     */   private Context context;
/*     */   private GestureDetector gestureDetector;
/*     */   private Scroller scroller;
/*     */   private int lastScrollY;
/*     */   private float lastTouchedY;
/*     */   private boolean isScrollingPerformed;
/*     */   private GestureDetector.SimpleOnGestureListener gestureListener;
/*     */   private final int MESSAGE_SCROLL = 0;
/*     */   private final int MESSAGE_JUSTIFY = 1;
/*     */   @SuppressLint({"HandlerLeak"})
/*     */   private Handler animationHandler;
/*     */   
/*     */   public WheelScroller(Context context, ScrollingListener listener) {
/* 155 */     this.gestureListener = new GestureDetector.SimpleOnGestureListener()
/*     */       {
/*     */ 
/*     */         
/*     */         public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
/*     */         {
/* 161 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
/* 166 */           WheelScroller.this.lastScrollY = 0;
/* 167 */           int maxY = Integer.MAX_VALUE;
/* 168 */           int minY = -2147483647;
/* 169 */           WheelScroller.this.scroller.fling(0, WheelScroller.this.lastScrollY, 0, (int)-velocityY, 0, 0, -2147483647, 
/* 170 */               2147483647);
/* 171 */           WheelScroller.this.setNextMessage(0);
/* 172 */           return true;
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 201 */     this.animationHandler = new Handler()
/*     */       {
/* 203 */         public void handleMessage(Message msg) { WheelScroller.this.scroller.computeScrollOffset();
/* 204 */           int currY = WheelScroller.this.scroller.getCurrY();
/* 205 */           int delta = WheelScroller.this.lastScrollY - currY;
/* 206 */           WheelScroller.this.lastScrollY = currY;
/* 207 */           if (delta != 0) {
/* 208 */             WheelScroller.this.listener.onScroll(delta);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 213 */           if (Math.abs(currY - WheelScroller.this.scroller.getFinalY()) < 1) {
/* 214 */             currY = WheelScroller.this.scroller.getFinalY();
/* 215 */             WheelScroller.this.scroller.forceFinished(true);
/*     */           } 
/* 217 */           if (!WheelScroller.this.scroller.isFinished()) {
/* 218 */             WheelScroller.this.animationHandler.sendEmptyMessage(msg.what);
/* 219 */           } else if (msg.what == 0) {
/* 220 */             WheelScroller.this.justify();
/*     */           } else {
/* 222 */             WheelScroller.this.finishScrolling();
/*     */           }  }
/*     */       };
/*     */     this.gestureDetector = new GestureDetector(context, (GestureDetector.OnGestureListener)this.gestureListener);
/*     */     this.gestureDetector.setIsLongpressEnabled(false);
/*     */     this.scroller = new Scroller(context);
/*     */     this.listener = listener;
/*     */     this.context = context;
/*     */   } public void setInterpolator(Interpolator interpolator) { this.scroller.forceFinished(true);
/* 231 */     this.scroller = new Scroller(this.context, interpolator); } private void justify() { this.listener.onJustify();
/* 232 */     setNextMessage(1); }
/*     */   public void scroll(int distance, int time) { this.scroller.forceFinished(true); this.lastScrollY = 0; this.scroller.startScroll(0, 0, 0, distance, (time != 0) ? time : 400); setNextMessage(0); startScrolling(); }
/*     */   public void stopScrolling() { this.scroller.forceFinished(true); }
/*     */   public boolean onTouchEvent(MotionEvent event) { int distanceY; switch (event.getAction()) { case 0:
/*     */         this.lastTouchedY = event.getY(); this.scroller.forceFinished(true); clearMessages(); break;
/*     */       case 2:
/*     */         distanceY = (int)(event.getY() - this.lastTouchedY); if (distanceY != 0) { startScrolling(); this.listener.onScroll(distanceY); this.lastTouchedY = event.getY(); }  break; }  if (!this.gestureDetector.onTouchEvent(event) && event.getAction() == 1)
/* 239 */       justify();  return true; } private void startScrolling() { if (!this.isScrollingPerformed) {
/* 240 */       this.isScrollingPerformed = true;
/* 241 */       this.listener.onStarted();
/*     */     }  } private void setNextMessage(int message) {
/*     */     clearMessages();
/*     */     this.animationHandler.sendEmptyMessage(message);
/*     */   } private void clearMessages() {
/*     */     this.animationHandler.removeMessages(0);
/*     */     this.animationHandler.removeMessages(1);
/*     */   } void finishScrolling() {
/* 249 */     if (this.isScrollingPerformed) {
/* 250 */       this.listener.onFinished();
/* 251 */       this.isScrollingPerformed = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface ScrollingListener {
/*     */     void onScroll(int param1Int);
/*     */     
/*     */     void onStarted();
/*     */     
/*     */     void onFinished();
/*     */     
/*     */     void onJustify();
/*     */   }
/*     */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/WheelScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */