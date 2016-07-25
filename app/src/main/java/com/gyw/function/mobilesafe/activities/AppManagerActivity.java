package com.gyw.function.mobilesafe.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.domain.AppInfo;
import com.gyw.function.mobilesafe.engine.AppInfoProvider;
import com.gyw.function.utils.ToastUtils;


public class AppManagerActivity extends Activity implements OnClickListener {
	private TextView tv_disk_avail;
	private TextView tv_sd_avail;
	// 正在加载布局
	private LinearLayout ll_loading;
	// 应用程序列表
	private ListView lv_appmanager;
	/**
	 * 所有应用程序信息的集合
	 */
	private List<AppInfo> appinfos;
	/**
	 * 用户应用程序信息集合
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * 系统应用程序信息集合
	 */
	private List<AppInfo> systemAppInfos;

	/**
	 * textview的文本,用来表示当前滚动的位置信息
	 */
	private TextView tv_status;

	/**
	 * 整个界面的悬浮窗体 需求是整个Activity只有一个悬浮窗体存在
	 */
	private PopupWindow popWindow;

	// 根据位置信息,确定出来哪个条目被点击了.
	private AppInfo appinfo;// 被点击的app信息

	private InnerUninstallReceiver receiver;

	private AppInfoAdapter adapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.GONE);
			// 设置listview的数据适配器
			adapter = new AppInfoAdapter();
			lv_appmanager.setAdapter(adapter);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		//注册广播接受者
		receiver = new InnerUninstallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);

		tv_disk_avail = (TextView) findViewById(R.id.tv_disk_avail);
		tv_sd_avail = (TextView) findViewById(R.id.tv_sd_avail);
		tv_status = (TextView) findViewById(R.id.tv_status);
		File datafile = Environment.getDataDirectory();
		long datasize = datafile.getFreeSpace();
		File sdfile = Environment.getExternalStorageDirectory();
		long sdsize = sdfile.getFreeSpace();
		tv_disk_avail.setText("内存可用:"
				+ Formatter.formatFileSize(this, datasize));
		tv_sd_avail.setText("SD卡可用:" + Formatter.formatFileSize(this, sdsize));

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_appmanager = (ListView) findViewById(R.id.lv_appmanger);
		// 给listview注册一个滚动的监听事件
		lv_appmanager.setOnScrollListener(new OnScrollListener() {
			/**
			 * 当滚动的状态发生变化的时候调用的方法 静止-->滚动 滚动-->静止 滚动--->滑翔 滑翔--->静止
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			/**
			 * 当listview列表只要滚动就会被调用.
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序:" + systemAppInfos.size() + "个");
					} else {
						tv_status.setText("用户程序:" + userAppInfos.size() + "个");
					}
				}
			}
		});
		// 给listview的条目注册item的点击事件
		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (position == 0) {// 第0个位置显示一个textview的标签
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {
					// 用户程序
					int location = position - 1;// 减去最前面的第一个textview占据的位置
					appinfo = userAppInfos.get(location);
				} else {
					// 系统程序
					int location = position - 1 - userAppInfos.size() - 1;
					appinfo = systemAppInfos.get(location);
				}

				dismissPopupWindow();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_app_item, null);
				popWindow = new PopupWindow(contentView, -2, -2);
				// 默认pupupwindow是没有背景的.
				popWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				popWindow.showAtLocation(parent, Gravity.TOP + Gravity.LEFT,
						60, location[1]);

				AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
				aa.setDuration(300);
				ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);
				LinearLayout ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);

				LinearLayout ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_share.setOnClickListener(AppManagerActivity.this);

				LinearLayout ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_start.setOnClickListener(AppManagerActivity.this);

				LinearLayout ll_info = (LinearLayout) contentView
						.findViewById(R.id.ll_info);
				ll_info.setOnClickListener(AppManagerActivity.this);
			}
		});
		fillData();
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appinfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appinfos) {
					if (appInfo.isUserApp()) {
						// 用户程序
						userAppInfos.add(appInfo);
					} else {
						// 系统程序
						systemAppInfos.add(appInfo);
					}
				}
				// 通知界面更新
				handler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * 关闭当前界面的弹出窗体
	 */
	private void dismissPopupWindow() {
		if (popWindow != null && popWindow.isShowing()) {
			popWindow.dismiss();
			popWindow = null;
		}
	}

	private class AppInfoAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// return appinfos.size();
			return userAppInfos.size() + systemAppInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appinfo;
			if (position == 0) {// 第0个位置显示一个textview的标签
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户程序:" + userAppInfos.size() + "个");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统程序:" + systemAppInfos.size() + "个");
				return tv;
			} else if (position <= userAppInfos.size()) {
				// 用户程序
				int location = position - 1;// 减去最前面的第一个textview占据的位置
				appinfo = userAppInfos.get(location);
			} else {
				// 系统程序
				int location = position - 1 - userAppInfos.size() - 1;
				appinfo = systemAppInfos.get(location);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_appicon = (ImageView) view
						.findViewById(R.id.iv_appicon);
				holder.tv_appname = (TextView) view
						.findViewById(R.id.tv_appname);
				holder.tv_applocation = (TextView) view
						.findViewById(R.id.tv_applocation);
				holder.tv_appsize = (TextView) view
						.findViewById(R.id.tv_appsize);
				view.setTag(holder);
			}
			// AppInfo appinfo = appinfos.get(position);

			holder.tv_appname.setText(appinfo.getAppname());
			holder.iv_appicon.setImageDrawable(appinfo.getIcon());
			holder.tv_appsize.setText(Formatter.formatFileSize(
					getApplicationContext(), appinfo.getApksize()));
			if (appinfo.isInRom()) {
				holder.tv_applocation.setText("手机内存");
			} else {
				holder.tv_applocation.setText("外存储卡");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	static class ViewHolder {
		TextView tv_appname;
		ImageView iv_appicon;
		TextView tv_appsize;
		TextView tv_applocation;
	}

	@Override
	protected void onDestroy() {
		dismissPopupWindow();
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_share:
				//System.out.println("分享:" + appinfo.getPackname());
				shareApplication();
				break;
			case R.id.ll_start:
				//System.out.println("启动:" + appinfo.getPackname());
				startApplication();
				break;
			case R.id.ll_uninstall:
				//System.out.println("卸载:" + appinfo.getPackname());
				uninstallApplication();
				break;
			case R.id.ll_info:
				//System.out.println("查看信息:" + appinfo.getPackname());
				showApplicationInfo();
				break;
		}
		dismissPopupWindow();
	}

	/**
	 * 打开系统详细信息界面
	 */
	private void showApplicationInfo() {
//		  <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
//          <category android:name="android.intent.category.DEFAULT" />
//          <data android:scheme="package" />
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appinfo.getPackname()));
		startActivity(intent);
	}

	/**
	 * 卸载应用程序
	 */
	private void uninstallApplication() {
		if (appinfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + appinfo.getPackname()));
			startActivity(intent);
		}else{
			ToastUtils.show(this, "系统软件需要有root权限后才可以被卸载");
		}
	}

	/**
	 * 开启应用程序
	 */
	private void startApplication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appinfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			ToastUtils.show(this, "当前应用程序无法启动");
		}
	}

	/**
	 * 分享应用程序
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"推荐你使用一款软件,软件的名称为:" + appinfo.getAppname() + ",我用的很爽.");
		startActivity(intent);
	}

	private class InnerUninstallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//System.out.println("哈哈哈,被卸载了.");
			if(appinfo.isUserApp()){
				userAppInfos.remove(appinfo);
			}else{
				systemAppInfos.remove(appinfo);
			}
			adapter.notifyDataSetChanged();
		}
	}

}
