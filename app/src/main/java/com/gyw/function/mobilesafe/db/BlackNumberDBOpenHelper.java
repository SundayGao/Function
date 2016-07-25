package com.gyw.function.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 创建一个应用程序的数据库,数据库文件的名称叫itheima.db
	 * @param context
	 */
	public BlackNumberDBOpenHelper(Context context) {
		super(context, "itheima.db", null, 1);
	}
	//当数据库第一次被创建的时候调用下面的方法,适合做数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		//_id数据库的主键,自增长
		//phone 黑名单电话号码
		//mode 拦截模式 1 电话拦截 2短信拦截 3全部拦截
		db.execSQL("create table blacknumberinfo (_id integer primary key autoincrement, phone varchar(20),mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
