package net.senink.seninkapp.ui.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import net.senink.piservice.pis.PISBase;
import net.senink.piservice.pis.PISDevice;

/**
 * 用于把数据序列化和反序列化
 * @author zhaojunfeng
 * @date 2015-11-10
 */
public class CacheManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3612993488616032451L;
  
	public HashMap<String, PISBase> mPISMap = new HashMap<String, PISBase>();
	public HashMap<Short,PISDevice> deviceGroupInfors = new HashMap<Short,PISDevice>();
	
	private void writeObject(java.io.ObjectOutputStream ous) throws IOException {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("device", mPISMap);
		map.put("group", deviceGroupInfors);
		ous.writeObject(map);
//		ous.writeObject(mPISMap);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
//		mPISMap = (HashMap<String, PISBase>)ois.readObject();
		Map<String,Object> map = (Map<String,Object>) ois.readObject();
		mPISMap = (HashMap<String, PISBase>)map.get("device");
		deviceGroupInfors = (HashMap<Short,PISDevice>)map.get("group");
	}
}
