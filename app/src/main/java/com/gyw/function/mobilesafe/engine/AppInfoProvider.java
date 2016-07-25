package com.gyw.function.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.gyw.function.mobilesafe.domain.AppInfo;

/**
 * 业务类,提供系统里面所有的应用程序信息
 */
public class AppInfoProvider {
	/**
	 * 获取手机里面所有的安装的应用程序信息/data/app/xxx   /system/app/xxx
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context){
		PackageManager pm = context.getPackageManager();
		List<AppInfo>  appinfos = new ArrayList<AppInfo>();
		List<PackageInfo> packinfos = pm.getInstalledPackages(0);
		for(PackageInfo packinfo:packinfos){
			AppInfo appInfo = new AppInfo();
			String packname = packinfo.packageName;
			//int uid = packinfo.applicationInfo.uid;
			appInfo.setPackname(packname);
			String appname = packinfo.applicationInfo.loadLabel(pm).toString();
			appInfo.setAppname(appname);
			Drawable icon = packinfo.applicationInfo.loadIcon(pm);
			appInfo.setIcon(icon);
			String path = packinfo.applicationInfo.sourceDir;
			File file = new File(path);
			long size = file.length();
			appInfo.setApksize(size);
			int flags = packinfo.applicationInfo.flags;//提交的答题卡.
			if((flags & ApplicationInfo.FLAG_SYSTEM)==0){
				//用户程序
				appInfo.setUserApp(true);
			}else{
				//系统程序
				appInfo.setUserApp(false);
			}
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				//手机系统内部
				appInfo.setInRom(true);
			}else{
				//SD卡
				appInfo.setInRom(false);
			}
			appinfos.add(appInfo);
		}
		return appinfos;
	}
}
