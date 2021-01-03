/*     */ package com.datepicker;
/*     */ 
/*     */ import android.view.View;
/*     */ import android.widget.LinearLayout;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class WheelRecycle
/*     */ {
/*     */   private List<View> items;
/*     */   private List<View> emptyItems;
/*     */   private WheelView wheel;
/*     */   
/*     */   public WheelRecycle(WheelView wheel) {
/*  28 */     this.wheel = wheel;
/*     */   }
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
/*     */   public int recycleItems(LinearLayout layout, int firstItem, ItemsRange range) {
/*  45 */     int index = firstItem;
/*  46 */     for (int i = 0; i < layout.getChildCount(); ) {
/*  47 */       if (!range.contains(index)) {
/*  48 */         recycleView(layout.getChildAt(i), index);
/*  49 */         layout.removeViewAt(i);
/*  50 */         if (i == 0) {
/*  51 */           firstItem++;
/*     */         }
/*     */       } else {
/*  54 */         i++;
/*     */       } 
/*  56 */       index++;
/*     */     } 
/*  58 */     return firstItem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public View getItem() {
/*  67 */     return getCachedView(this.items);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public View getEmptyItem() {
/*  76 */     return getCachedView(this.emptyItems);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearAll() {
/*  83 */     if (this.items != null) {
/*  84 */       this.items.clear();
/*     */     }
/*  86 */     if (this.emptyItems != null) {
/*  87 */       this.emptyItems.clear();
/*     */     }
/*     */   }
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
/*     */   private List<View> addView(View view, List<View> cache) {
/* 101 */     if (cache == null) {
/* 102 */       cache = new LinkedList<View>();
/*     */     }
/*     */     
/* 105 */     cache.add(view);
/* 106 */     return cache;
/*     */   }
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
/*     */   private void recycleView(View view, int index) {
/* 119 */     int count = this.wheel.getViewAdapter().getItemsCount();
/*     */     
/* 121 */     if ((index < 0 || index >= count) && !this.wheel.isCyclic()) {
/*     */       
/* 123 */       this.emptyItems = addView(view, this.emptyItems);
/*     */     } else {
/* 125 */       while (index < 0) {
/* 126 */         index = count + index;
/*     */       }
/* 128 */       index %= count;
/* 129 */       this.items = addView(view, this.items);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private View getCachedView(List<View> cache) {
/* 141 */     if (cache != null && cache.size() > 0) {
/* 142 */       View view = cache.get(0);
/* 143 */       cache.remove(0);
/* 144 */       return view;
/*     */     } 
/* 146 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/WheelRecycle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */