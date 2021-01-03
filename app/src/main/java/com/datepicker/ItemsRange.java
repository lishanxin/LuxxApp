/*    */ package com.datepicker;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ItemsRange
/*    */ {
/*    */   private int first;
/*    */   private int count;
/*    */   
/*    */   public ItemsRange() {
/* 17 */     this(0, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ItemsRange(int first, int count) {
/* 29 */     this.first = first;
/* 30 */     this.count = count;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getFirst() {
/* 39 */     return this.first;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLast() {
/* 48 */     return getFirst() + getCount() - 1;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCount() {
/* 57 */     return this.count;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean contains(int index) {
/* 68 */     return (index >= getFirst() && index <= getLast());
/*    */   }
/*    */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/ItemsRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */