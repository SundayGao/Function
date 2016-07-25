package com.gyw.function.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.domain.ProcessInfo;

/**
 * 进程管理器的业务类
 */
public class TaskInfoProvider {

	/**
	 * 获取正在运行的所有的进程信息
	 * @return 进程信息集合
	 */
	public static List<ProcessInfo> getRunningProcessInfos(Context context){

		//声明总的集合
		List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for(RunningAppProcessInfo info: infos){
			ProcessInfo processInfo = new ProcessInfo();
			String packname = info.processName;
			processInfo.setPackname(packname);
			long memsize = am.getProcessMemoryInfo(new int[]{info.pid})[0].getTotalPrivateDirty()*1024;
			processInfo.setMemsize(memsize);
			try {
				PackageInfo packinfo = pm.getPackageInfo(packname, 0);
				String appname = packinfo.applicationInfo.loadLabel(pm).toString();
				processInfo.setAppname(appname);
				Drawable icon = packinfo.applicationInfo.loadIcon(pm);
				processInfo.setIcon(icon);
				if((packinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
					//系统进程
					processInfo.setUsertask(false);
				}else{
					//用户进程
					processInfo.setUsertask(true);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				processInfo.setAppname(packname);
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.default_icon));
			}
			processInfos.add(processInfo);
		}

		return processInfos;
	}
}
