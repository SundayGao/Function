package com.gyw.function.mobilesafe.services;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.gyw.function.mobilesafe.db.dao.BlackNumberDao;

/**
 * 需求: 广播接受者的存活周期和服务保持一致.
 */
public class CallSmsSafeService extends Service {
	private BlackNumberDao dao;
	//电话管理的服务.
	private TelephonyManager tm;
	/**
	 * 内部类 短信广播接收者的实例
	 */
	private InnerSmsReceiver receiver;
	/**
	 * 电话呼叫状态变化的监听器
	 */
	private MyPhoneListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		//得到电话管理的服务
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//注册电话呼叫状态的监听器.
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		//设置关心短信到来的动作
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		//代码注册广播接受者
		registerReceiver(receiver, filter);
		//System.out.println("黑名单拦截的服务已经开启了.");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//System.out.println("黑名单拦截的服务已经关闭了.");
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}

	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj:objs){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String address = smsMessage.getOriginatingAddress();
				String result = dao.find(address);
				if("2".equals(result)||"3".equals(result)){
					//System.out.println("黑名单短信,拦截...");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){//luncence 分词算法.
					//System.out.println("发票短信,拦截...");
					abortBroadcast();
				}
			}
		}
	}
	private class MyPhoneListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE://空闲状态

					break;
				case TelephonyManager.CALL_STATE_RINGING://响铃状态, 有人给你打电话了.
					String result = dao.find(incomingNumber);
					if("1".equals(result)||"3".equals(result)){
						//System.out.println("挂断电话");
						//记录被挂断的电话号码 ,还有时间.
						endCall();//挂断了电话. 呼叫记录可能还没有生成出来.
						//监视呼叫记录的数据库,看什么时候生成了记录,就把他删除掉.
						Uri uri = Uri.parse("content://call_log/calls");
						getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
							@Override
							public void onChange(boolean selfChange) {
								//当观察的数据库 数据发生变化的时候 调用的方法.
								deleteCallLog(incomingNumber);
								super.onChange(selfChange);
							}
						});

					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK://接通电话

					break;
			}


			super.onCallStateChanged(state, incomingNumber);
		}

	}
	public void endCall() {
		//IBinder iBinder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony  iTelephony = ITelephony.Stub.asInterface(iBinder);
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除呼叫记录
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
	}

}
