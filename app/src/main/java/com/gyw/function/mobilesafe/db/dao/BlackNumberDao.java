package com.gyw.function.mobilesafe.db.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gyw.function.mobilesafe.db.BlackNumberDBOpenHelper;
import com.gyw.function.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 操作黑名单数据库,提供增删改查的方法.
 * @author Administrator
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;
	/**
	 * 黑名单dao的构造方法 
	 * @param context 上下文
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	/**
	 * 查找黑名单号码的拦截模式
	 * @param phone 要查找的电话号码
	 * @return 拦截模式 1 电话拦截 2短信拦截 3全部拦截 如果返回null代表 不是黑名单号码
	 */
	public String find(String phone){
		String mode = null;
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumberinfo", null, "phone=?", new String[]{phone}, null, null, null);
		if(cursor.moveToNext()){
			mode = cursor.getString(cursor.getColumnIndex("mode"));
		}
		cursor.close();
		db.close();
		return mode;
	}
	/**
	 * 添加黑名单号码
	 * @param phone 黑名单电话号码
	 * @param mode 拦截模式
	 * @return
	 */
	public boolean add(String phone,String mode){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);
		long id = db.insert("blacknumberinfo", null, values);
		db.close();
		if(id!=-1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 删除黑名单号码
	 * @param phone 黑名单号码
	 * @return 是否删除成功
	 */
	public boolean delete(String phone){
		SQLiteDatabase  db = helper.getWritableDatabase();
		int rowcount = db.delete("blacknumberinfo", "phone=?", new String[]{phone});
		db.close();
		if(rowcount==0){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 修改黑名单号码的拦截模式
	 * @param phone 要修改的黑名单号码
	 * @param newmode 新的拦截模式
	 * @return 是否修改成功
	 */
	public boolean update(String phone,String newmode){
		SQLiteDatabase  db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		int rowcount =db.update("blacknumberinfo", values, "phone=?", new String[]{phone});
		db.close();
		if(rowcount==0){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * 返回全部的黑名单号码信息
	 * @return
	 */
	public List<BlackNumberInfo> findAll(){
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor = db.query("blacknumberinfo", null, null, null, null, null, "_id desc");
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			info.setPhone(phone);
			info.setMode(mode);
			infos.add(info);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		db.close();
		return infos;
	}

	/**
	 * 分批的黑名单号码信息
	 * @param startIndex 开始位置
	 * @param maxCount 最多获取多少条数据
	 * @return
	 */
	public List<BlackNumberInfo> findPart(int startIndex, int maxCount){
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id, phone,mode from blacknumberinfo order by _id desc limit ? offset ?", new String[]{
				String.valueOf(maxCount),String.valueOf(startIndex)
		});
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			info.setPhone(phone);
			info.setMode(mode);
			infos.add(info);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		db.close();
		return infos;
	}

	/**
	 * 分页显示黑名单号码信息
	 * @param pagenumber 页码号
	 * @return
	 */
	public List<BlackNumberInfo> findPage(int pagenumber){
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select _id, phone,mode from blacknumberinfo order by _id desc limit ? offset ?", new String[]{
				String.valueOf(20),String.valueOf(20*pagenumber)
		});
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			info.setPhone(phone);
			info.setMode(mode);
			infos.add(info);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		db.close();
		return infos;
	}


	/**
	 * 获取黑名单号码的条目信息
	 * @return 总个数
	 */
	public int getTotalCount(){
		SQLiteDatabase  db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blacknumberinfo",null);
		cursor.moveToNext();
		int totalcount = cursor.getInt(0);
		cursor.close();
		db.close();
		return totalcount;
	}
}
