package net.senink.seninkapp.sqlite;

import net.senink.seninkapp.ui.util.LogUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 数据库的增删改的用户信息类
 * 
 * @author zhaojunfeng
 * @date 2015-10-30
 * 
 */
public class UserService {

	private DBHelper helper;

	public UserService(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 获取某个用户下PISManager的存储路径
	 * 
	 * @param user
	 * @return
	 */
	public String getPath(String user) {
		String path = null;
		if (!TextUtils.isEmpty(user)) {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cr = db.rawQuery("select infor from " + DBHelper.TABLE_USER
					+ " where name=?", new String[] { user });
			if (cr != null) {
				if (cr.moveToNext()) {
					path = cr.getString(cr.getColumnIndex("infor"));
				}
				cr.close();
			}
			db.close();
		}
		LogUtils.i("SaveManager", "getPath(): path = " + path);
		return path;
	}

	/**
	 * 插入用户信息
	 * 
	 * @param user
	 * @param path
	 * @return
	 */
	public long insertUser(String user, String path) {
		long id = 0;
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(path)) {
			if (!exist(user)) {
				SQLiteDatabase db = helper.getWritableDatabase();
				ContentValues value = new ContentValues();
				value.put(DBHelper.USER_NAME, user);
				value.put(DBHelper.INFOR, path);
				id = db.insert(DBHelper.TABLE_USER, null, value);
				db.close();
			} else {
				id = upgradeUser(user, path);
			}
		}
		LogUtils.i("SaveManager", "insertUser(): id = " + id);
		return id;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 * @param path
	 * @return
	 */
	public long upgradeUser(String user, String path) {
		long id = 0;
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(path)) {
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues value = new ContentValues();
			value.put(DBHelper.USER_NAME, user);
			value.put(DBHelper.INFOR, path);
			id = db.update(DBHelper.TABLE_USER, value, " name=? ",
					new String[] { user });
			db.close();
		}
		return id;
	}

	/**
	 * 删除某个用户
	 * 
	 * @param user
	 * @return
	 */
	public int delecteUser(String user) {
		int num = 0;
		if (!TextUtils.isEmpty(user)) {
			SQLiteDatabase db = helper.getReadableDatabase();
			num = db.delete(DBHelper.TABLE_USER, " name=? ",
					new String[] { user });
			db.close();
		}
		return num;
	}

	/**
	 * 某个用户信息是否已经存在
	 * 
	 * @param user
	 * @return
	 */
	public boolean exist(String user) {
		boolean hasUser = false;
		if (!TextUtils.isEmpty(user)) {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cr = db.rawQuery("select infor from " + DBHelper.TABLE_USER
					+ " where name=?", new String[] { user });
			if (cr != null) {
				if (cr.getCount() > 0) {
					hasUser = true;
				}
				cr.close();
			}
			db.close();
		}
		return hasUser;
	}
}
