package com.gyw.function.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	/**
	 * 代表当前的条目是否被选中
	 */
	private boolean checked;


	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	private Drawable icon;
	private String appname;
	private long memsize;
	private boolean usertask;
	private String packname;
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
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUsertask() {
		return usertask;
	}
	public void setUsertask(boolean usertask) {
		this.usertask = usertask;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}

}
