package com.gyw.function.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gyw.function.mobilesafe.db.AppLockDBOpenHelper;


public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	/**
	 * 添加一条锁定应用程序的包名
	 * @param packname 包名
	 * @return
	 */
	public boolean add(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		long result = db.insert("lockinfo", null, values);
		db.close();
		if(result!=-1){
			//大吼一声发个消息.
			Uri uri = Uri.parse("content://com.itheima.mobilesafe.applockdb");
			context.getContentResolver().notifyChange(uri, null);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 删除一条锁定应用程序的包名
	 * @param packname 包名
	 * @return
	 */
	public boolean delete(String packname){
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("lockinfo", "packname=?", new String[]{packname});
		db.close();
		if(result>0){
			//大吼一声发个消息.
			Uri uri = Uri.parse("content://com.itheima.mobilesafe.applockdb");
			context.getContentResolver().notifyChange(uri, null);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 查询应用程序的包名是否需要被锁定
	 * @param packname 包名
	 * @return
	 */
	public boolean find(String packname){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("lockinfo", null, "packname=?", new String[]{packname}, null, null, null);
		result = cursor.moveToNext();
		cursor.close();
		db.close();
		return result;
	}



	/**
	 * 查询全部的锁定的应用程序包名
	 * @param packname 包名
	 * @return
	 */
	public List<String> findAll(){
		List<String> packnames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("lockinfo", new String[]{"packname"}, null, null, null, null, null);
		while(cursor.moveToNext()){
			packnames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packnames;
	}
}
