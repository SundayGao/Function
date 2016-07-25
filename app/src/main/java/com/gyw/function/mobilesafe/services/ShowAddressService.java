package com.gyw.function.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.NumberAddressDao;

public class ShowAddressService extends Service {
	private TelephonyManager tm;
	private MyListener listener;
	private InnerOutCallRecevicer receiver;
	/**
	 * 窗体管理器
	 */
	private WindowManager wm;
	/**
	 * 自定义的土司
	 */
	private View view;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		receiver = new InnerOutCallRecevicer();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// 取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	private class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:// 响铃状态(别人给你打电话)
					// 响铃状态
					String address = NumberAddressDao.getAddress(incomingNumber);
					// Toast.makeText(getApplicationContext(), address, 1).show();
					showMyToast(address);
					break;
				case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
					if (view != null) {
						wm.removeView(view);
						view = null;
					}
					break;

				case TelephonyManager.CALL_STATE_OFFHOOK:// 通话状态

					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private class InnerOutCallRecevicer extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = NumberAddressDao.getAddress(phone);
			// Toast.makeText(getApplicationContext(), address, 1).show();
			showMyToast(address);
		}
	}

	/**
	 * 显示自定义土司
	 *
	 * @param address
	 */
	public void showMyToast(String address) {
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int which = getSharedPreferences("config", MODE_PRIVATE).getInt(
				"which", 0);
		int[] bgs = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		view = View.inflate(this, R.layout.toast_address, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//System.out.println("摸到");
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						//System.out.println("移动");
						int newX = (int) event.getRawX();
						int newY = (int) event.getRawY();
						int dx = newX - startX;
						int dy = newY - startY;
						params.x +=dx;
						params.y +=dy;
						if(params.x<0) params.x = 0;
						if(params.y<0) params.y = 0;
						if(params.y>wm.getDefaultDisplay().getHeight()-view.getHeight()){
							params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();
						}
						if(params.x>wm.getDefaultDisplay().getWidth()-view.getWidth()){
							params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();
						}
						wm.updateViewLayout(view, params);
						//重新初始化手指的位置.
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						//System.out.println("放手");
						SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt("lastx", params.x);
						editor.putInt("lasty", params.y);
						editor.commit();
						break;
				}
				return true;
			}
		});

		view.setBackgroundResource(bgs[which]);
		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		tv_address.setText(address);
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.gravity = Gravity.LEFT+Gravity.TOP;
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//(如果要想让土司相应点击事件,需要修改窗体类型)
		wm.addView(view, params);
	}
}
