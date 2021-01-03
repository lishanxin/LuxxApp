/*     */ package com.datepicker.adapter;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.text.TextUtils;
/*     */ import android.util.Log;
/*     */ import android.view.LayoutInflater;
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
/*     */ public abstract class AbstractWheelTextAdapter
/*     */   extends AbstractWheelAdapter
/*     */ {
/*     */   public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
/*     */   protected static final int NO_RESOURCE = 0;
/*     */   public static final int DEFAULT_TEXT_COLOR = -13488081;
/*     */   public static final int LABEL_COLOR = -1118482;
/*     */   public static final int DEFAULT_TEXT_SIZE = 22;
/*  34 */   private int textColor = -13488081;
/*  35 */   private int textSize = 22;
/*     */ 
/*     */   
/*     */   protected Context context;
/*     */ 
/*     */   
/*     */   protected LayoutInflater inflater;
/*     */ 
/*     */   
/*     */   protected int itemResourceId;
/*     */ 
/*     */   
/*     */   protected int itemTextResourceId;
/*     */ 
/*     */   
/*     */   protected int emptyItemResourceId;
/*     */   
/*     */   private boolean isLeft;
/*     */ 
/*     */   
/*     */   protected AbstractWheelTextAdapter(Context context, boolean isLeft) {
/*  56 */     this(context, -1, isLeft);
/*  57 */     this.isLeft = isLeft;
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
/*     */   protected AbstractWheelTextAdapter(Context context, int itemResource, boolean isLeft) {
/*  70 */     this(context, itemResource, 0, isLeft);
/*  71 */     this.isLeft = isLeft;
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
/*     */   protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource, boolean isLeft) {
/*  87 */     this.context = context;
/*  88 */     this.itemResourceId = itemResource;
/*  89 */     this.itemTextResourceId = itemTextResource;
/*  90 */     this.inflater = (LayoutInflater)context
/*  91 */       .getSystemService("layout_inflater");
/*  92 */     this.isLeft = isLeft;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextColor() {
/* 101 */     return this.textColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTextColor(int textColor) {
/* 111 */     this.textColor = textColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextSize() {
/* 120 */     return this.textSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTextSize(int textSize) {
/* 130 */     this.textSize = textSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getItemResource() {
/* 139 */     return this.itemResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItemResource(int itemResourceId) {
/* 149 */     this.itemResourceId = itemResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getItemTextResource() {
/* 158 */     return this.itemTextResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItemTextResource(int itemTextResourceId) {
/* 168 */     this.itemTextResourceId = itemTextResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getEmptyItemResource() {
/* 177 */     return this.emptyItemResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEmptyItemResource(int emptyItemResourceId) {
/* 187 */     this.emptyItemResourceId = emptyItemResourceId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract CharSequence getItemText(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public View getItem(int index, View convertView, ViewGroup parent) {
/* 201 */     if (index >= 0 && index < getItemsCount()) {
/* 202 */       if (convertView == null) {
/* 203 */         convertView = getView(this.itemResourceId, parent);
/*     */       }
/* 205 */       TextView textView = getTextView(convertView, this.itemTextResourceId);
/* 206 */       if (textView != null) {
/* 207 */         CharSequence text = getItemText(index);
/* 208 */         if (text == null) {
/* 209 */           text = "";
/*     */         }
/* 211 */         textView.setText(text);
/*     */         
/* 213 */         if (this.itemResourceId == -1) {
/* 214 */           configureTextView(textView);
/*     */         }
/*     */       } 
/* 217 */       return convertView;
/*     */     } 
/* 219 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public View getEmptyItem(View convertView, ViewGroup parent) {
/* 224 */     if (convertView == null) {
/* 225 */       convertView = getView(this.emptyItemResourceId, parent);
/*     */     }
/* 227 */     if (this.emptyItemResourceId == -1 && 
/* 228 */       convertView instanceof TextView) {
/* 229 */       configureTextView((TextView)convertView);
/*     */     }
/*     */     
/* 232 */     return convertView;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void configureTextView(TextView view) {
/* 242 */     view.setTextColor(this.textColor);
/* 243 */     if (this.isLeft) {
/* 244 */       view.setGravity(21);
/* 245 */       view.setPadding(view.getPaddingLeft(), 4, 30, 4);
/*     */     } else {
/* 247 */       view.setGravity(19);
/* 248 */       view.setPadding(30, 4, view.getPaddingRight(), 4);
/*     */     } 
/* 250 */     view.setTextSize(this.textSize);
/* 251 */     view.setEllipsize(TextUtils.TruncateAt.END);
/* 252 */     view.setLines(1);
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
/*     */   public TextView getTextView(View view, int textResource) {
/* 267 */     TextView text = null;
/*     */     try {
/* 269 */       if (textResource == 0 && view instanceof TextView) {
/* 270 */         text = (TextView)view;
/* 271 */       } else if (textResource != 0) {
/* 272 */         text = (TextView)view.findViewById(textResource);
/*     */       } 
/* 274 */     } catch (ClassCastException e) {
/* 275 */       Log.e("AbstractWheelAdapter", 
/* 276 */           "You must supply a resource ID for a TextView");
/* 277 */       throw new IllegalStateException(
/* 278 */           "AbstractWheelAdapter requires the resource ID to be a TextView", 
/* 279 */           e);
/*     */     } 
/*     */     
/* 282 */     return text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public View getView(int resource, ViewGroup parent) {
/* 293 */     switch (resource) {
/*     */       case 0:
/* 295 */         return null;
/*     */       case -1:
/* 297 */         return (View)new TextView(this.context);
/*     */     } 
/* 299 */     return this.inflater.inflate(resource, parent, false);
/*     */   }
/*     */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/AbstractWheelTextAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */