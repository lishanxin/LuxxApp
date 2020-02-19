package net.senink.seninkapp.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.SparseArray;

import net.senink.piservice.PISConstantDefine;
import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;
import net.senink.piservice.pis.PISManager;
import net.senink.piservice.services.PISXinLight;
import net.senink.piservice.services.PISXinRemoter;
import net.senink.piservice.services.PISXinSwitch;
import net.senink.piservice.services.PISxinColor;


/**
 * PIPA数据排序的工具类
 * 
 * @author zhaojunfeng
 * @date 2015-06-26
 * 
 */
public class SortUtils {

	/**
	 * 把插排的数据重新排序
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<PISBase[]> sortSwitches(List<PISDevice> list) {
		ArrayList<PISBase[]> mList = null;
		if (list != null && list.size() > 0) {
			mList = new ArrayList<PISBase[]>();
			for (PISDevice dev : list) {
				if (dev != null && dev.getPIServices() != null
						&& dev.getPIServices().size() > 0) {
					ArrayList<PISBase> services = dev.getPIServices();
					if (services.get(0) instanceof PISXinSwitch) {
						PISBase[] array = new PISBase[4];
						int size = services.size();
						for (int i = 0; i < size; i++) {
							if (services.get(i) != null) {
								int index = services.get(i).getServiceId() - 2;
								if (index >= 4 || index < 0) {
									index = 0;
								}
//								array[index] = services.get(i);
//								array[index].macAddr = dev.macAddr;
//								array[index].getServiceInfo();
							}
						}
						mList.add(array);
					}
				}
			}
		}
		return mList;
	}

	/**
	 * 把灯的数据重新排序
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<PISBase[]> sortServiceFor4(List<PISBase> list) {
		ArrayList<PISBase[]> mList = new ArrayList<PISBase[]>();
		if (list == null || list.size() == 0)
			return mList;

		int size = list.size() / 4;
		if (list.size() % 4 > 0) {
			size++;
		}
		for (int i = 0; i < size; i++) {
			PISBase[] array = new PISBase[4];
			for (int k = 0; k < 4; k++) {
				if (i * 4 + k >= list.size()) {
					break;
				}
				array[k] = list.get(i * 4 + k);
			}
			mList.add(array);
		}

		return mList;
	}

	/**
	 * 在连接器界面排序网关下的设备
	 * 
	 * @param list
	 * @return
	 */
	public static SparseArray<PISDevice> sortLightsOnBlueLinker(
			List<PISDevice> list) {
		SparseArray<PISDevice> mList = new SparseArray<PISDevice>();
		if (list != null && list.size() > 0) {
			for (PISDevice dev : list) {
				if (dev != null && dev.getPIServices() != null
						&& dev.getPIServices().size() > 0) {
					PISBase base = dev.getPIServices().get(0);
					if (base instanceof PISxinColor
							|| base instanceof PISXinLight) {
						mList.put(dev.getServiceId(), dev);
					}
				}
			}
		}
		return mList;
	}

	/**
	 * 把灯分组的数据重新排序
	 * 
	 * @param list
	 * @return
	 */
//	public static ArrayList<PISBase[]> sortLightGroups(
//			SparseArray<PISBase> list) {
//		ArrayList<PISBase[]> mList = null;
//		if (list == null || list.size() == 0)
//			return mList;
//		List<PISBase> bases = new ArrayList<PISBase>();
//		for (int i = 0, size = list.size(); i < size; i++) {
//			PISBase infor = list.valueAt(i);
//			if (infor != null
//					&& (infor.getT1() == PISConstantDefine.PIS_XINCOLOR_T1
//					&& infor.getT2() == PISConstantDefine.PIS_XINCOLOR_T2)) {
//				bases.add(infor);
//			}
//		}
//		for (int i = 0, size = list.size(); i < size; i++) {
//			PISBase infor = list.valueAt(i);
//			if (infor != null
//					&& (infor.getT1() == PISConstantDefine.PIS_XINLIGHT_T1
//					&& infor.getT2() == PISConstantDefine.PIS_XINLIGHT_T2)) {
//				bases.add(infor);
//			}
//		}
//		mList = new ArrayList<PISBase[]>();
//		int size = bases.size() / 4;
//		if (bases.size() % 4 > 0) {
//			size++;
//		}
//		for (int i = 0; i < size; i++) {
//			PISBase[] array = new PISBase[4];
//			for (int j = 0; j < 4; j++) {
//				if (i * 4 + j >= bases.size()) {
//					break;
//				}
//				array[j] = bases.get(i * 4 + j);
//			}
//			mList.add(array);
//		}
//		bases.clear();
//		return mList;
//	}

	/**
	 * 把控制器的数据重新排序
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<PISBase[]> sortRemoters(List<PISDevice> list) {
		ArrayList<PISBase[]> mList = null;
		if (list != null && list.size() > 0) {
			List<PISBase> bases = new ArrayList<PISBase>();
			for (PISDevice dev : list) {
				if (dev != null && dev.getPIServices() != null
						&& dev.getPIServices().size() > 0) {
					PISBase base = dev.getPIServices().get(0);
					if (base instanceof PISXinRemoter) {
						bases.add(base);
					}
				}
			}
			if (bases.size() > 0) {
				mList = new ArrayList<PISBase[]>();
				int size = bases.size() / 4;
				if (bases.size() % 4 > 0) {
					size++;
				}
				for (int i = 0; i < size; i++) {
					PISBase[] array = new PISBase[4];
					for (int k = 0; k < 4; k++) {
						if (i * 4 + k >= bases.size()) {
							break;
						}
						array[k] = bases.get(i * 4 + k);
					}
					mList.add(array);
				}
			}
		}
		return mList;
	}

	/**
	 * 把智能穿戴的数据重新排序
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<PISBase[]> sortInsoles(List<PISDevice> list) {
		ArrayList<PISBase[]> mList = null;
		if (list != null && list.size() > 0) {
			List<PISBase> bases = new ArrayList<PISBase>();
			for (PISDevice dev : list) {
				if (dev != null && dev.getPIServices() != null
						&& dev.getPIServices().size() > 0) {
//					PISBase base = dev.getPIServices().get(0);
//					if (base instanceof PISXinInsole) {
//						bases.add(base);
//					}
				}
			}
			if (bases.size() > 0) {
				mList = new ArrayList<PISBase[]>();
				int size = bases.size() / 4;
				if (bases.size() % 4 > 0) {
					size++;
				}
				for (int i = 0; i < size; i++) {
					PISBase[] array = new PISBase[4];
					for (int k = 0; k < 4; k++) {
						if (i * 4 + k >= bases.size()) {
							break;
						}
						array[k] = bases.get(i * 4 + k);
					}
					mList.add(array);
				}
			}
		}
		return mList;
	}
	
	/**
	 * 获取所有在线的灯，开关，智能元的服务
	 * 
	 * @param key
	 * @param devices
	 * @return
	 */
//	public static List<PISBase> getBases(PISSmartCell cell,
//			List<PISDevice> devices) {
//		List<PISBase> mList = new ArrayList<PISBase>();
//		if (cell != null && devices != null && devices.size() > 0) {
//			String key = cell.getPISKeyString();
//			if (TextUtils.isEmpty(cell.getSupportMacAddr())) {
//				cell.getSmartCellInfo();
//			} else {
//				if (!cell.getSupportMacAddr().equals(
//						PISSmartCell.DEFAULT_MACADDR)) {
//					for (PISDevice device : devices) {
//						List<PISBase> list = device.getPIServices();
//						if (list != null && list.size() > 0) {
//							for (PISBase base : list) {
//								if (!base.getPISKeyString().equals(key)
//										&& cell.getPisTypes() != null) {
//									List<byte[]> types = cell.getPisTypes();
//									int size = types.size();
//									for (int i = 0; i < size; i++) {
//										byte[] data = types.get(i);
//										if (base.mT1 == data[0]
//												&& base.mT2 == data[1]) {
//											base.macAddr = device.macAddr;
//											mList.add(base);
//											break;
//										}
//									}
//								}
//							}
//						}
//					}
//				} else {
//					for (PISDevice device : devices) {
//						List<PISBase> list = device.getPIServices();
//						if (list != null && list.size() > 0) {
//							for (PISBase base : list) {
//								if (!TextUtils.isEmpty(key)
//										&& !base.getPISKeyString().equals(key)
//										&& ((base.getT1() == PISConstantDefine.PIS_XINLIGHT_T1
//										&& base.getT2() == PISConstantDefine.PIS_XINLIGHT_T2)
//										|| (base.getT1() == PISConstantDefine.PIS_XINCOLOR_T1
//										&& base.getT2() == PISConstantDefine.PIS_XINCOLOR_T2)
//										|| (base.getT1() == PISConstantDefine.PIS_SMART_CELL_T1
//										&& base.getT2() == PISConstantDefine.PIS_SMART_CELL_T2)
//										|| (base.getT1() == PISConstantDefine.PIS_SWITCH_T1
//										&& (base.getT2() == PISConstantDefine.PIS_SWITCH_T2_1
//										|| base.getT2() == PISConstantDefine.PIS_SWITCH_T2_2)))) {
//									base.macAddr = device.macAddr;
//									mList.add(base);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return mList;
//	}

	/**
	 * 获取某个服务对象
	 * 
	 * @param mac
	 * @param servId
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static PISBase getPISBase(String mac, short servId) {
//		PISBase base = null;
		List<PISDevice> devs = PISManager.getInstance().PIDevicesWithQuery(mac, PISManager.EnumDevicesQueryBaseonMac);
		if (devs == null || devs.size() == 0)
			return null;
		PISDevice dev = devs.get(0);
		List<PISBase> srvs = dev.getPIServices();
		for (PISBase srv : srvs){
			if (servId == srv.getServiceId())
				return srv;
		}
		return null;
//		ArrayList<PISDevice> devices = PISManager.getInstance()
//				.getPisDeviceList();
//		if (!TextUtils.isEmpty(mac) && devices != null && devices.size() > 0) {
//			mac = mac.replace("-", "");
//			mac = mac.replace(":", "");
//			mac = mac.toLowerCase();
//			for (PISDevice device : devices) {
//				if (!TextUtils.isEmpty(device.macAddr)) {
//					String macAddr = device.macAddr;
//					macAddr = macAddr.replace("-", "");
//					macAddr = macAddr.replace(":", "");
//					macAddr = macAddr.toLowerCase();
//					if (mac.equals(macAddr)) {
//						List<PISBase> list = device.getPIServices();
//						if (list != null && list.size() > 0) {
//							for (PISBase bas : list) {
//								if (servId == bas.mServiceID) {
//									bas.macAddr = macAddr;
//									base = bas;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		return base;
	}
	
	public static ArrayList<PISBase> sortPISBase(Map<String,PISBase> bases){
		ArrayList<PISBase> infors = null;
		if (bases != null && bases.size() > 0) {
			infors = new ArrayList<PISBase>();
			Iterator<Entry<String,PISBase>> iter = bases.entrySet().iterator();
			while(iter.hasNext()){
				PISBase base = iter.next().getValue();
				if (base != null) {
					infors.add(base);
				}
			}
		}
		return infors;
	}
}
