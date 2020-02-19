package net.senink.seninkapp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 存储设备信息的数据库管理类
 * 
 * @author zhaojunfeng
 * @date 2015-10-26
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	// 数据库名称
	private static final String NAME = "senink_device.db";
	// 设备表的名称
	public static final String TABLE_DEVICE = "device";
	// 场景表的名称
	public static final String TABLE_SCENE = "scene";
	// 用户账号表
	public static final String TABLE_USER = "user";
	// id
	public static final String ID = "_id";
	// 用户名称
	public static final String USER_NAME = "name";
	/*
	 * 是否是分组 1:组 0：设备
	 */
	public static final String ISGROUP = "isgroup";
	// 设备信息 或 PISManager存储的路径
	public static final String INFOR = "infor";
	// 场景的名称
	public static final String SCENE_NAME = "scenename";
	// 亮度
	public static final String SCENE_BRIGHT = "bright";
	// 冷暖色值
	public static final String SCENE_COLDWARM = "coldwarm";
	// 场景的图片
	public static final String SCENE_PICTURE = "picture";
	// 是否是常用的
	public static final String SCENE_USED = "isused";
	/*
	 * t1与服务的t1保持一致 t1包含两种：RGB和LED的t1
	 */
	public static final String SCENE_T1 = "t1";
	/*
	 * t2与服务的t2保持一致 t2包含两种：RGB和LED的t2
	 */
	public static final String SCENE_T2 = "t2";
	// 场景模式下RGB中RGB的值
	public static final String SCENE_RGB = "rgb";

	public DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS user(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, infor TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS scene(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, t1 INTEGER, t2 INTEGER,scenename TEXT, isused INTEGER, bright INTEGER, coldwarm INTEGER, rgb INTEGER,picture TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
