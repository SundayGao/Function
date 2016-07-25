package com.gyw.function.mobilesafe.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoKillService extends Service {
	private InnerScreenLockReceiver receiver;
	private Timer timer;
	private TimerTask task;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		receiver = new InnerScreenLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				//System.out.println("哈哈,每各5秒执行一次.");
			}
		};
		//timer.schedule(task, 0, 5000);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		timer.cancel();
		task.cancel();
		super.onDestroy();
	}

	private class InnerScreenLockReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//System.out.println("哈哈哈,屏幕锁屏了.");
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for(RunningAppProcessInfo info:infos){
				am.killBackgroundProcesses(info.processName);
			}
		}
	}
}
