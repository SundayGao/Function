package com.gyw.function.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 常用号码的信息dao
 *
 */
public class CommonNumberDao {

	/**
	 * 返回数据库一共有多少个大的分组信息
	 * @return
	 */
	public static int getGroupCount(SQLiteDatabase db){
		Cursor cursor = db.rawQuery("select count(*) from classlist ", null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}


	/**
	 * 根据分组的位子查询孩子的个数
	 * @param groupPosition 分组的位置
	 * @return
	 */
	public static int getChildrenCountByGroupPosition(SQLiteDatabase db,int groupPosition){
		int newposition = groupPosition+1;
		String tablename = "table"+newposition;
		Cursor cursor = db.rawQuery("select count(*) from "+tablename, null);
		cursor.moveToNext();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	/**
	 * 根据分组的位置查询分组的名称
	 * @param groupPosition 分组的位置
	 * @return
	 */
	public static String getNameByGroupPosition(SQLiteDatabase db,int groupPosition){
		int newposition = groupPosition+1;
		Cursor cursor = db.rawQuery("select name from classlist  where idx =?", new String[]{String.valueOf(newposition)});
		cursor.moveToNext();
		String name = cursor.getString(0);
		cursor.close();
		return name;
	}

	/**
	 * 根据分组的位置和孩子的位置查询孩子的名称
	 * @param groupPosition 分组的位置
	 * @param childPosition 孩子的位置
	 * @return
	 */
	public static String getChildNameByPosition(SQLiteDatabase db,int groupPosition,int childPosition){
		int newGroupPosition = groupPosition+1;
		int newChildPosition = childPosition+1;
		String tablename = "table"+newGroupPosition;
		Cursor cursor = db.rawQuery("select name,number from "+tablename+" where _id=?", new String[]{String.valueOf(newChildPosition)});
		cursor.moveToNext();
		String name = cursor.getString(0);
		String number = cursor.getString(1);
		cursor.close();
		return name+"\n"+number;
	}
}
