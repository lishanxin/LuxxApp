/*     */ package com.datepicker.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.text.TextUtils;
/*     */ import android.view.View;
/*     */ import android.view.ViewGroup;
/*     */ import android.widget.TextView;
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
/*     */ public class NumericWheelAdapter
/*     */   extends AbstractWheelTextAdapter
/*     */ {
/*     */   public static final int DEFAULT_MAX_VALUE = 9;
/*     */   private static final int DEFAULT_MIN_VALUE = 0;
/*     */   private int minValue;
/*     */   private int maxValue;
/*     */   private String format;
/*     */   private String label;
/*     */   
/*     */   public NumericWheelAdapter(Context context, boolean isLeft) {
/*  36 */     this(context, 0, 9, isLeft);
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
/*     */   public NumericWheelAdapter(Context context, int minValue, int maxValue, boolean isLeft) {
/*  50 */     this(context, minValue, maxValue, (String)null, isLeft);
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
/*     */   public NumericWheelAdapter(Context context, int minValue, int maxValue, String format, boolean isLeft) {
/*  67 */     super(context, isLeft);
/*     */     
/*  69 */     this.minValue = minValue;
/*  70 */     this.maxValue = maxValue;
/*  71 */     this.format = format;
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence getItemText(int index) {
/*  76 */     if (index >= 0 && index < getItemsCount()) {
/*  77 */       int value = this.minValue + index;
/*  78 */       return (this.format != null) ? String.format(this.format, new Object[] { Integer.valueOf(value)
/*  79 */           }) : Integer.toString(value);
/*     */     } 
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getItemsCount() {
/*  86 */     return this.maxValue - this.minValue + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public View getItem(int index, View convertView, ViewGroup parent) {
/*  91 */     if (index >= 0 && index < getItemsCount()) {
/*  92 */       if (convertView == null) {
/*  93 */         convertView = getView(this.itemResourceId, parent);
/*     */       }
/*  95 */       TextView textView = getTextView(convertView, this.itemTextResourceId);
/*  96 */       if (textView != null) {
/*  97 */         CharSequence text = getItemText(index);
/*  98 */         if (text == null) {
/*  99 */           text = "";
/*     */         }
/* 101 */         if (TextUtils.isEmpty(this.label)) {
/* 102 */           this.label = "";
/*     */         }
/* 104 */         textView.setText(text + this.label);
/*     */         
/* 106 */         if (this.itemResourceId == -1) {
/* 107 */           configureTextView(textView);
/*     */         }
/*     */       } 
/* 110 */       return convertView;
/*     */     } 
/* 112 */     return null;
/*     */   }
/*     */   
/*     */   public void setLabel(String label) {
/* 116 */     this.label = label;
/*     */   }
/*     */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/NumericWheelAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */