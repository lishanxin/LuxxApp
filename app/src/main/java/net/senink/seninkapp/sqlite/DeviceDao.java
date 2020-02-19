package net.senink.seninkapp.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
//import com.senink.seninkapp.core.PISBase;
//import com.senink.seninkapp.core.PisDeviceGroup;
import net.senink.piservice.pis.PISBase;
import net.senink.seninkapp.ui.util.CommonUtils;

/**
 * 数据库的增删改的设备信息类
 * 
 * @author zhaojunfeng
 * @date 2015-10-30
 * 
 */
public class DeviceDao {

	private DBHelper helper;

	public DeviceDao(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 获取所有分组信息
	 * 
	 * @return
	 */
	public SparseArray<PISBase> getAllGroups() {
		SQLiteDatabase db = helper.getReadableDatabase();
		SparseArray<PISBase> groups = new SparseArray<PISBase>();
		String sql = "select * from device where isgroup=1";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				byte[] data = cursor.getBlob(cursor
						.getColumnIndex(DBHelper.INFOR));
//				PISBase group = CommonUtils
//						.byteArrayToPisDeviceGroup(data);
//				if (group != null) {
//					groups.append(group.groupId, group);
//				}
			}
			cursor.close();
		}
		db.close();
		return groups;
	}

	/**
	 * 获取所有设备信息
	 * 
	 * @return
	 */
	public Map<String, PISBase> getAllDevices() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Map<String, PISBase> bases = new HashMap<String, PISBase>();
		String sql = "select * from device where isgroup=0";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				byte[] data = cursor.getBlob(cursor
						.getColumnIndex(DBHelper.INFOR));
//				PISBase base = CommonUtils.byteArrayToPISBase(data);
//				if (base != null) {
//					base.mStatus = 0;
//					base.mServiceInfoInited = true;
//					bases.put(base.getPISKeyString(), base);
//				}
			}
			cursor.close();
		}
		db.close();
		return bases;
	}

	/**
	 * 把设备信息存入数据库
	 * 
	 * @param bases
	 * @return
	 */
//	public int insertDevices(ArrayList<PISBase[]> bases) {
//		int num = 0;
//		if (bases != null && bases.size() > 0) {
//			SQLiteDatabase db = helper.getWritableDatabase();
//			for (PISBase[] array : bases) {
//				if (array != null && array.length > 0) {
//					for (PISBase base : array) {
//						if (base != null) {
//							ContentValues value = new ContentValues();
//							value.put(DBHelper.ISGROUP, 0);
//							value.put(DBHelper.INFOR,
//									CommonUtils.PISBaseToByteArray(base));
//							long id = db.insert(DBHelper.TABLE_DEVICE,
//									null, value);
//							if (id >= 0) {
//								num++;
//							}
//						}
//					}
//				}
//			}
//			db.close();
//		}
//		return num;
//	}

	/**
	 * 插入组信息
	 */
//	public int insertGroups(SparseArray<PisDeviceGroup> groups) {
//		int num = 0;
//		if (groups != null && groups.size() > 0) {
//			SQLiteDatabase db = helper.getWritableDatabase();
//			int size = groups.size();
//			for (int i = 0; i < size; i++) {
//				PisDeviceGroup group = groups.valueAt(i);
//				if (group != null) {
//					ContentValues value = new ContentValues();
//					value.put(DBHelper.ISGROUP, 0);
//					value.put(DBHelper.INFOR,
//							CommonUtils.PisDeviceGroupToByteArray(group));
//					long id = db.insert(DBHelper.TABLE_DEVICE, null, value);
//					if (id >= 0) {
//						num++;
//					}
//				}
//			}
//		}
//		return num;
//	}

	/**
	 * 删除所有数据
	 */
//	public int deleteAll() {
//		int num = 0;
//		SQLiteDatabase db = helper.getWritableDatabase();
//		num = db.delete(DBHelper.TABLE_DEVICE, "isgroup=1", null);
//		num += db.delete(DBHelper.TABLE_DEVICE, "isgroup=0", null);
//		db.close();
//		return num;
//	}
}
