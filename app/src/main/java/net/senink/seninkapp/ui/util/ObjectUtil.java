package net.senink.seninkapp.ui.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对象与字节数组之间的转换工具类,要求传入的对象必须实现序列化接口
 * @author jindegege
 */
public class ObjectUtil {
	
	/**
	 * 对象转换成字节数组,要求传入的对象必须实现序列号接口.
	 * @param obj
	 * @return byte[]
	 */
	public static Object ByteToObject(byte[] buffer)throws Exception{  
        Object ob = null;
        try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer));
			ob = ois.readObject();
			ois.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ob;
    }
    
    public static byte[] ObjectToByte(Object obj)throws Exception{  
        byte [] bytes= new byte[4] ;
        try {
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			bytes=baos.toByteArray();
			baos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return bytes;
    }
    
	/**
	 * 判断字符串是否为空
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value){
		boolean isNull = false;
		if (null == value || "".equals(value)) {
			isNull = true;
		}
		return isNull;
	}
	/**
	 * 判断字符串是否为空
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String[] value){
		boolean isNull = false;
		if (null == value || value.length == 0) {
			isNull = true;
		}
		return isNull;
	}
}
