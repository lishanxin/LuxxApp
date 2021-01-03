package com.datepicker.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

public interface WheelViewAdapter {
  int getItemsCount();
  
  View getItem(int paramInt, View paramView, ViewGroup paramViewGroup);
  
  View getEmptyItem(View paramView, ViewGroup paramViewGroup);
  
  void registerDataSetObserver(DataSetObserver paramDataSetObserver);
  
  void unregisterDataSetObserver(DataSetObserver paramDataSetObserver);
}


/* Location:              /Users/mac/HehuoProject/YoujijiaProject/LuxxApp/app/libs/DataPicker.jar!/com/datepicker/adapter/WheelViewAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */