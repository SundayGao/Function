package com.gyw.function.mobilesafe.activities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyw.function.R;
import com.gyw.function.utils.ToastUtils;


public class CleanCacheActivity extends Activity {
	/**
	 * 扫描完毕
	 */
	protected static final int SCAN_FINISH = 1;
	public static final int SCANING_APP = 2;
	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager pm;
	private List<CacheInfo> cacheInfos;
	private ListView lv_cacheinfos;

	private FrameLayout fl_scan_status;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case SCAN_FINISH://扫描完毕.
					Toast.makeText(getApplicationContext(), "扫描完毕", Toast.LENGTH_SHORT).show();
					fl_scan_status.setVisibility(View.GONE);
					if(cacheInfos.size()>0){
						lv_cacheinfos.setAdapter(new CleanCacheAdapter());
					}else{
						ToastUtils.show(CleanCacheActivity.this, "恭喜您,手机100分,没有任何缓存");
					}
					break;

				case SCANING_APP://扫描应用程序中.
					String appname = (String) msg.obj;
					tv_scan_status.setText("正在扫描:"+appname);
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		lv_cacheinfos = (ListView) findViewById(R.id.lv_cacheinfos);
		pm = getPackageManager();
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		fl_scan_status = (FrameLayout) findViewById(R.id.fl_scan_status);
		pb = (ProgressBar) findViewById(R.id.pb);
		scanCache();
	}

	/**
	 * 扫描缓存
	 */
	private void scanCache() {
		fl_scan_status.setVisibility(View.VISIBLE);
		cacheInfos = new ArrayList<CleanCacheActivity.CacheInfo>();
		new Thread() {
			public void run() {
				// 1.扫描全部应用程序的包名
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb.setMax(infos.size());
				int progress = 0;
				for (PackageInfo info : infos) {
					String packname = info.packageName;
					try {
						Method method = PackageManager.class.getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(pm, packname, new MyObserver());
						Thread.sleep(50);
						progress++;
						pb.setProgress(progress);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//集合的数据就准备好了. 通知界面更新
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private class MyObserver extends IPackageStatsObserver.Stub {
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			try {
				Message msg = Message.obtain();
				msg.what = SCANING_APP;
				String appname = pm
						.getPackageInfo(pStats.packageName, 0).applicationInfo
						.loadLabel(pm).toString();
				msg.obj = appname;
				handler.sendMessage(msg);
				if (pStats.cacheSize > 0) {
					CacheInfo cacheinfo = new CacheInfo();
					cacheinfo.cache = pStats.cacheSize;
					cacheinfo.packname = pStats.packageName;
					cacheinfo.icon = pm.getPackageInfo(cacheinfo.packname, 0).applicationInfo
							.loadIcon(pm);
					cacheinfo.appname = appname;
					cacheInfos.add(cacheinfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	class CacheInfo {
		long cache;
		String packname;
		Drawable icon;
		String appname;
	}

	private class CleanCacheAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return cacheInfos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				view = View.inflate(getApplicationContext(), R.layout.item_cacheinfo, null);
				holder = new ViewHolder();
				holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
				holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
				holder.tv_appcachesize = (TextView) view.findViewById(R.id.tv_appcachesize);
				holder.iv_delete_cache = (ImageView) view.findViewById(R.id.iv_delete_cache);
				view.setTag(holder);
			}
			final CacheInfo info = cacheInfos.get(position);
			holder.iv_appicon.setImageDrawable(info.icon);
			holder.tv_appname.setText(info.appname);
			holder.tv_appcachesize.setText(Formatter.formatFileSize(getApplicationContext(), info.cache));
			//清除缓存的点击事件
			holder.iv_delete_cache.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//  mPm.deleteApplicationCacheFiles(packageName, mClearCacheObserver);
					Method[] methods = PackageManager.class.getMethods();
					for(Method method :methods){
						if("deleteApplicationCacheFiles".equals(method.getName())){
							try {
								method.invoke(pm, info.packname,new ClearCacheObserver());
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}
					}
				}
			});
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

	static class ViewHolder{
		ImageView iv_appicon;
		TextView tv_appname;
		TextView tv_appcachesize;
		ImageView iv_delete_cache;
	}

	class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
			ToastUtils.show(CleanCacheActivity.this, "清除状态:"+succeeded);
		}
	}
	/**
	 * 清理全部应用程序缓存的点击事件
	 * @param view
	 */
	public void cleanAll(View view){
		//freeStorageAndNotify
		Method[] methods =  PackageManager.class.getMethods();
		for(Method method:methods){
			if("freeStorageAndNotify".equals(method.getName())){
				try {
					method.invoke(pm, Integer.MAX_VALUE,new ClearCacheObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				scanCache();
				return;
			}
		}

	}
}
