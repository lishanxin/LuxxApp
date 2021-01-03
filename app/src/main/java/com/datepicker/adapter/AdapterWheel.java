/*    */ package com.datepicker.adapter;
/*    */ 
/*    */ import android.content.Context;
/*    */ import com.datepicker.WheelAdapter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AdapterWheel
/*    */   extends AbstractWheelTextAdapter
/*    */ {
/*    */   private WheelAdapter adapter;
/*    */   
/*    */   public AdapterWheel(Context context, WheelAdapter adapter, boolean isLeft) {
/* 26 */     super(context, isLeft);
/*    */     
/* 28 */     this.adapter = adapter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WheelAdapter getAdapter() {
/* 37 */     return this.adapter;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getItemsCount() {
/* 42 */     return this.adapter.getItemsCount();
/*    */   }
/*    */ 
/*    */   
/*    */   protected CharSequence getItemText(int index) {
/* 47 */     return this.adapter.getItem(index);
/*    */   }
/*    */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/AdapterWheel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */