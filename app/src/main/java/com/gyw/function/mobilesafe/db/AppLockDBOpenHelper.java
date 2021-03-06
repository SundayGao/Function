package com.gyw.function.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 创建一个应用程序的数据库,数据库文件的名称叫applock.db
	 * @param context
	 */
	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	//当数据库第一次被创建的时候调用下面的方法,适合做数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		//_id数据库的主键,自增长
		//packname 要锁定的应用程序的包名.
		db.execSQL("create table lockinfo (_id integer primary key autoincrement, packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
