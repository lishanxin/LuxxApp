/*    */ package com.datepicker.adapter;
/*    */ 
/*    */ import android.content.Context;
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
/*    */ public class ArrayWheelAdapter<T>
/*    */   extends AbstractWheelTextAdapter
/*    */ {
/*    */   private T[] items;
/*    */   
/*    */   public ArrayWheelAdapter(Context context, Object[] items, boolean isLeft) {
/* 25 */     super(context, isLeft);
/*    */ 
/*    */     
/* 28 */     this.items = (T[])items;
/*    */   }
/*    */ 
/*    */   
/*    */   public CharSequence getItemText(int index) {
/* 33 */     if (index >= 0 && index < this.items.length) {
/* 34 */       T item = this.items[index];
/* 35 */       if (item instanceof CharSequence) {
/* 36 */         return (CharSequence)item;
/*    */       }
/* 38 */       return item.toString();
/*    */     } 
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getItemsCount() {
/* 45 */     return this.items.length;
/*    */   }
/*    */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/ArrayWheelAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */