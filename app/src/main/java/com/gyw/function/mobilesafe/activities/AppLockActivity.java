package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.AppLockDao;
import com.gyw.function.mobilesafe.domain.AppInfo;
import com.gyw.function.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;


public class AppLockActivity extends Activity implements OnClickListener {
	private TextView tv_unlock;
	private TextView tv_locked;
	// 两个显示程序个数的标签
	private TextView tv_unlock_count;
	private TextView tv_locked_count;

	// 两个线性布局
	private LinearLayout ll_unlock;
	private LinearLayout ll_locked;

	// 未加锁listview
	private ListView lv_unlock;
	//已加锁的listview
	private ListView lv_locked;
	/**
	 * 手机里面所有的应用程序信息
	 */
	private List<AppInfo> allAppInfos;
	/**
	 * 未加锁的程序集合
	 */
	private List<AppInfo> unlockAppInfos;
	/**
	 * 已加锁的程序集合
	 */
	private List<AppInfo> lockedAppInfo;

	private AppLockDao dao;

	private ApplockAdapter unlockadapter;
	private ApplockAdapter lockedadapter;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		dao = new AppLockDao(this);
		tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
		tv_locked_count = (TextView) findViewById(R.id.tv_locked_count);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_locked = (TextView) findViewById(R.id.tv_locked);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		tv_locked.setOnClickListener(this);
		tv_unlock.setOnClickListener(this);

		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_locked = (ListView) findViewById(R.id.lv_locked);

		allAppInfos = AppInfoProvider.getAppInfos(this);
		unlockAppInfos = new ArrayList<AppInfo>();
		lockedAppInfo = new ArrayList<AppInfo>();
		for (AppInfo info : allAppInfos) {
			if (dao.find(info.getPackname())) {
				// 已经加锁
				lockedAppInfo.add(info);
			} else {
				// 没有加锁
				unlockAppInfos.add(info);
			}
		}
		unlockadapter = new ApplockAdapter(true);
		lockedadapter = new ApplockAdapter(false);
		lv_unlock.setAdapter(unlockadapter);
		lv_locked.setAdapter(lockedadapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_unlock:
				ll_locked.setVisibility(View.INVISIBLE);
				ll_unlock.setVisibility(View.VISIBLE);
				tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				tv_locked.setBackgroundResource(R.drawable.tab_right_default);
				break;
			case R.id.tv_locked:
				ll_locked.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.INVISIBLE);
				tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
				tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
				break;
		}
	}

	private class ApplockAdapter extends BaseAdapter {
		/**
		 * 代表是否是未加锁的界面 true 未加锁 false 已加锁
		 */
		private boolean isUnlock;

		public ApplockAdapter(boolean isUnlock) {
			this.isUnlock = isUnlock;
		}

		@Override
		public int getCount() {
			int count;
			if (isUnlock) {
				count = unlockAppInfos.size();
				tv_unlock_count.setText("未加锁软件:" + count + "个");
			}else{
				count = lockedAppInfo.size();
				tv_locked_count.setText("已加锁软件:" + count + "个");
			}
			return count;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_appunlock, null);
				holder = new ViewHolder();
				holder.iv_applock_icon = (ImageView) view
						.findViewById(R.id.iv_applock_icon);
				holder.tv_applock_name = (TextView) view
						.findViewById(R.id.tv_applock_name);
				holder.iv_applock_addordelete = (ImageView) view
						.findViewById(R.id.iv_applock_add);
				view.setTag(holder);
			}
			final AppInfo appinfo ;
			if (isUnlock) {
				//未加锁
				appinfo = unlockAppInfos.get(position);
				holder.iv_applock_addordelete.setImageResource(R.drawable.list_button_lock_pressed);
			}else{
				//已加锁
				appinfo = lockedAppInfo.get(position);
				holder.iv_applock_addordelete.setImageResource(R.drawable.list_button_unlock_pressed);
			}

			holder.iv_applock_icon.setImageDrawable(appinfo.getIcon());
			holder.tv_applock_name.setText(appinfo.getAppname());
			holder.iv_applock_addordelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 把对象从集合里面移除
					if (isUnlock) {
						TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						ta.setDuration(200);
						view.startAnimation(ta);//开子线程 发消息更新界面.
						ta.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {

							}

							@Override
							public void onAnimationRepeat(Animation animation) {

							}
							@Override
							public void onAnimationEnd(Animation animation) {
								//未加锁
								unlockAppInfos.remove(appinfo);
								lockedAppInfo.add(appinfo);
								dao.add(appinfo.getPackname());
								unlockadapter.notifyDataSetChanged();
								lockedadapter.notifyDataSetChanged();
							}
						});

					}else{
						//已加锁
						TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, -1.0f,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0);
						ta.setDuration(200);
						view.startAnimation(ta);
						ta.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								lockedAppInfo.remove(appinfo);
								unlockAppInfos.add(appinfo);
								dao.delete(appinfo.getPackname());
								unlockadapter.notifyDataSetChanged();
								lockedadapter.notifyDataSetChanged();
							}
						});
					}
					//notifyDataSetChanged();

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

	static class ViewHolder {
		ImageView iv_applock_icon;
		TextView tv_applock_name;
		ImageView iv_applock_addordelete;
	}
}
