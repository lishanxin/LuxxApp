package net.senink.seninkapp.sqlite;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import net.senink.seninkapp.ui.entity.SceneBean;

/**
 * 数据库的增删改的场景信息类
 * 
 * @author zhaojunfeng
 * @date 2015-11-24
 * 
 */
public class SceneDao {

	private DBHelper helper;

	public SceneDao(Context context) {
		helper = new DBHelper(context);
	}

	/**
	 * 获取某个用户下所有led或RGB灯下的场景信息
	 * asc 升幂，desc降幂
	 * @param user
	 * @return
	 */
	public List<SceneBean> getAllScenes(String user) {
		List<SceneBean> scenes = null;
		if (!TextUtils.isEmpty(user)) {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cr = db.rawQuery("select * from " + DBHelper.TABLE_SCENE
					+ " where name=? ORDER BY isused ASC ", new String[] { user });
			if (cr != null && cr.getCount() > 0) {
				scenes = new ArrayList<SceneBean>();
				while (cr.moveToNext()) {
					SceneBean bean = new SceneBean();
					bean.id = cr.getInt(cr.getColumnIndex(DBHelper.ID));
					bean.user = cr.getString(cr
							.getColumnIndex(DBHelper.USER_NAME));
					bean.sceneName = cr.getString(cr
							.getColumnIndex(DBHelper.SCENE_NAME));
					bean.t1 = cr.getInt(cr.getColumnIndex(DBHelper.SCENE_T1));
					bean.t2 = cr.getInt(cr.getColumnIndex(DBHelper.SCENE_T2));
					bean.rgb = cr.getInt(cr.getColumnIndex(DBHelper.SCENE_RGB));
					bean.used = cr.getInt(cr
							.getColumnIndex(DBHelper.SCENE_USED));
					bean.bright = cr.getInt(cr
							.getColumnIndex(DBHelper.SCENE_BRIGHT));
					bean.coldwarm = cr.getInt(cr
							.getColumnIndex(DBHelper.SCENE_COLDWARM));
					bean.picture = cr.getString(cr
							.getColumnIndex(DBHelper.SCENE_PICTURE));
					scenes.add(bean);
				}
				cr.close();
			}
			db.close();
		}
		return scenes;
	}

	/**
	 * 插入某个场景
	 * 
	 * @param bean
	 * @return
	 */
	public long insertScene(SceneBean bean) {
		long id = 0;
		if (bean != null && !TextUtils.isEmpty(bean.user)) {
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues value = new ContentValues();
			value.put(DBHelper.USER_NAME, bean.user);
			value.put(DBHelper.SCENE_NAME, bean.sceneName);
			value.put(DBHelper.SCENE_T1, bean.t1);
			value.put(DBHelper.SCENE_T2, bean.t2);
			value.put(DBHelper.SCENE_RGB, bean.rgb);
			value.put(DBHelper.SCENE_USED, bean.used);
			value.put(DBHelper.SCENE_BRIGHT, bean.bright);
			value.put(DBHelper.SCENE_COLDWARM, bean.coldwarm);
			value.put(DBHelper.SCENE_PICTURE, bean.picture);
			id = db.insert(DBHelper.TABLE_SCENE, null, value);
			db.close();
		}
		return id;
	}

	/**
	 * 更新场景信息
	 * 
	 * @param bean
	 * @return
	 */
	public long upgradeScene(SceneBean bean) {
		long id = 0;
		if (bean != null) {
			SQLiteDatabase db = helper.getWritableDatabase();
			ContentValues value = new ContentValues();
			value.put(DBHelper.USER_NAME, bean.user);
			value.put(DBHelper.SCENE_NAME, bean.sceneName);
			value.put(DBHelper.SCENE_T1, bean.t1);
			value.put(DBHelper.SCENE_T2, bean.t2);
			value.put(DBHelper.SCENE_RGB, bean.rgb);
			value.put(DBHelper.SCENE_USED, bean.used);
			value.put(DBHelper.SCENE_BRIGHT, bean.bright);
			value.put(DBHelper.SCENE_COLDWARM, bean.coldwarm);
			value.put(DBHelper.SCENE_PICTURE, bean.picture);
			id = db.update(DBHelper.TABLE_SCENE, value, " _id=" + bean.id + "",
					null);
			db.close();
		}
		return id;
	}

	/**
	 * 删除某个场景
	 * 
	 * @param user
	 * @return
	 */
	public int delecteScene(int id) {
		int num = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		num = db.delete(DBHelper.TABLE_SCENE, " _id=" + id + "", null);
		db.close();
		return num;
	}

	/**
	 * 某个用户下某个场景是否存在
	 * 
	 * @param bean
	 * @return
	 */
	public boolean exist(int id) {
		boolean hasUser = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cr = db.rawQuery("select * from " + DBHelper.TABLE_SCENE
				+ " where _id=" + id + " ", null);
		if (cr != null) {
			if (cr.getCount() > 0) {
				hasUser = true;
			}
			cr.close();
		}
		db.close();
		return hasUser;
	}
}
