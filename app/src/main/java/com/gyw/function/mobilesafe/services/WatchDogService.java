package com.gyw.function.mobilesafe.services;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.gyw.function.mobilesafe.activities.EnterPasswordActivity;
import com.gyw.function.mobilesafe.db.dao.AppLockDao;

public class WatchDogService extends Service {
	protected static final String TAG = "WatchDogService";
	private ActivityManager am;
	private boolean flag;
	private AppLockDao dao;
	private InnerWatchDogReceiver receiver;
	/**
	 * 临时要停止保护的应用程序包名
	 */
	private String tempStopProtectPackname;

	private Intent intent;
	private ApplockDBObserver observer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	List<RunningTaskInfo> infos;
	String packname ;
	private List<String> lockedPacknames;
	@Override
	public void onCreate() {
		receiver = new InnerWatchDogReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.gyw.function.mobilesafe.wangwang");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		registerReceiver(receiver, filter);
		Uri uri = Uri.parse("content://com.gyw.function.mobilesafe.applockdb");
		observer = new ApplockDBObserver(new Handler());
		getContentResolver().registerContentObserver(uri, true, observer);

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 获取任务栈的情况, 对任务栈里面的内容 进行排序 ,最近使用的任务栈在最前面
		dao = new AppLockDao(this);
		lockedPacknames = dao.findAll();
		intent = new Intent(WatchDogService.this,
				EnterPasswordActivity.class);
		// 服务里面没有任务栈信息,如果在服务开启Activity需要记得添加任务栈的flag
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startWatchDog();
		super.onCreate();
	}

	private void startWatchDog() {
		if(flag){
			return;
		}
		flag = true;
		new Thread() {
			public void run() {
				while (flag) {
//					long starttime = System.currentTimeMillis();
					infos = am.getRunningTasks(1);
					packname = infos.get(0).topActivity.getPackageName();
					//System.out.println(packname);
					//if (dao.find(packname)) {//查询内存效率比查询数据库效率高很多.
					if(lockedPacknames.contains(packname)){//查询内存效率更高
						// 应用程序需要被保护,弹出来一个输入密码的界面.
						// 再次判断这个应用程序是否需要临时停止保护
						if (packname.equals(tempStopProtectPackname)) {
							// 需要临时停止保护
						} else {
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					} else {
						// 应用程序不需要保护
					}
//					long endtime = System.currentTimeMillis();
//					Log.i(TAG,"一次while循环花费的时间为:"+String.valueOf(endtime-starttime));
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		unregisterReceiver(receiver);
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		receiver = null;
	}

	private class InnerWatchDogReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.gyw.function.mobilesafe.wangwang".equals(intent.getAction())) {

				tempStopProtectPackname = intent.getStringExtra("packname");
				//System.out.println("哈哈,看门狗识别到了主人的命令.停止保护:"
				//		+ tempStopProtectPackname);
			} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				//System.out.println("屏幕锁屏了....");
				flag = false;
				tempStopProtectPackname = null;
			}else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				//System.out.println("屏幕解锁了....");
				startWatchDog();
			}

		}
	}

	private class ApplockDBObserver extends ContentObserver{

		public ApplockDBObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG,"观察者观察到数据变化了....");
			lockedPacknames = dao.findAll();
		}

	}

}
