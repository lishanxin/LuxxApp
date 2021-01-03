/*    */ package com.datepicker.adapter;
/*    */ 
/*    */ import android.database.DataSetObserver;
/*    */ import android.view.View;
/*    */ import android.view.ViewGroup;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractWheelAdapter
/*    */   implements WheelViewAdapter
/*    */ {
/*    */   private List<DataSetObserver> datasetObservers;
/*    */   
/*    */   public View getEmptyItem(View convertView, ViewGroup parent) {
/* 18 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void registerDataSetObserver(DataSetObserver observer) {
/* 23 */     if (this.datasetObservers == null) {
/* 24 */       this.datasetObservers = new LinkedList<DataSetObserver>();
/*    */     }
/* 26 */     this.datasetObservers.add(observer);
/*    */   }
/*    */ 
/*    */   
/*    */   public void unregisterDataSetObserver(DataSetObserver observer) {
/* 31 */     if (this.datasetObservers != null) {
/* 32 */       this.datasetObservers.remove(observer);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void notifyDataChangedEvent() {
/* 40 */     if (this.datasetObservers != null) {
/* 41 */       for (DataSetObserver observer : this.datasetObservers) {
/* 42 */         observer.onChanged();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void notifyDataInvalidatedEvent() {
/* 51 */     if (this.datasetObservers != null)
/* 52 */       for (DataSetObserver observer : this.datasetObservers)
/* 53 */         observer.onInvalidated();  
/*    */   }
/*    */ }


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/AbstractWheelAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */