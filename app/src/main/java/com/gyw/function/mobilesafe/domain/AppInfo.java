package com.gyw.function.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	/**
	 * 应用程序的图标
	 */
	private Drawable icon;
	/**
	 * 应用程序名称
	 */
	private String appname;
	/**
	 * 应用程序包名
	 */
	private String packname;
	/**
	 * 是否安装在手机内存
	 */
	private boolean inRom;
	/**
	 * 应用程序apk的大小
	 */
	private long apksize;
	/**
	 * 是否是用户应用程序
	 */
	private boolean userApp;
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public long getApksize() {
		return apksize;
	}
	public void setApksize(long apksize) {
		this.apksize = apksize;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}


}
