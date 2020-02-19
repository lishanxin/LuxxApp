package net.senink.seninkapp.interfaces;

import java.util.ArrayList;
import android.util.SparseArray;

import net.senink.piservice.pis.PISBase;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PisDeviceGroup;

/**
 * 用于监听开关、蓝牙灯和网关是否有数据更新
 * 
 * @author zhaojunfeng
 * @date 2015-07-10
 */
public interface OnDeviceRefreshListener {

	public void deviceRefresh(ArrayList<PISBase[]> deviceList);

	public void groupRefresh(SparseArray<PISBase[]> infors);
}
