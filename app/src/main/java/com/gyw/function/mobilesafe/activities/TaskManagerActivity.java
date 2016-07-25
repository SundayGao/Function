package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.domain.ProcessInfo;
import com.gyw.function.mobilesafe.engine.TaskInfoProvider;
import com.gyw.function.mobilesafe.ui.SystemInfoUtils;
import com.gyw.function.utils.IntentUtils;
import com.gyw.function.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


public class TaskManagerActivity extends Activity {
	// PackageManager 相当于windows系统的软件管理器 获取的是静态的信息.
	// ActivityManager 相当于windows系统的任务管理器 获取的是动态的信息.
	private TextView tv_process_count;
	private TextView tv_memory_info;
	/**
	 * 正在运行的进程数量
	 */
	private int runningProcessCount;
	/**
	 * 手机可用ram空间
	 */
	private long availRam;
	/**
	 * 手机总的ram大小
	 */
	private long totalRam;

	private ListView lv_taskmanger;
	private LinearLayout ll_loading;
	private TextView tv_status;
	/**
	 * 用户进程集合
	 */
	private List<ProcessInfo> userProcessInfos;
	/**
	 * 系统进程集合
	 */
	private List<ProcessInfo> systemProcessInfos;

	private TaskManagerAdapter adapter;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			adapter = new TaskManagerAdapter();
			lv_taskmanger.setAdapter(adapter);
			ll_loading.setVisibility(View.GONE);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);
		availRam = SystemInfoUtils.getAvailRam(this);
		totalRam = SystemInfoUtils.getTotalRam(this);
		tv_process_count.setText("运行进程:" + runningProcessCount + "个");
		tv_memory_info.setText("剩余/总内存:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));

		lv_taskmanger = (ListView) findViewById(R.id.lv_taskmanger);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		fillData();

		// 给listview注册滚动的监听器
		lv_taskmanger.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				if (userProcessInfos != null && systemProcessInfos != null) {
					if (firstVisibleItem > userProcessInfos.size()) {
						tv_status.setText("系统进程:" + systemProcessInfos.size()
								+ "个");
					} else {
						tv_status.setText("用户进程:" + userProcessInfos.size()
								+ "个");
					}
				}
			}
		});
		// 给listview的item注册点击事件
		lv_taskmanger.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				ProcessInfo processInfo;
				if (position == 0) {
					return;
				} else if (position == (userProcessInfos.size() + 1)) {
					return;
				} else if (position <= userProcessInfos.size()) {// 用户程序
					processInfo = userProcessInfos.get(position - 1);// 减去第一个textview占据的位置.
				} else {// 系统程序
					processInfo = systemProcessInfos.get(position - 1
							- userProcessInfos.size() - 1);
				}
				if (processInfo.getPackname().equals(getPackageName())) {
					return;
				}
				if (processInfo.isChecked()) {
					processInfo.setChecked(false);
				} else {
					processInfo.setChecked(true);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 填充数据对应的逻辑
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				List<ProcessInfo> infos = TaskInfoProvider
						.getRunningProcessInfos(getApplicationContext());
				userProcessInfos = new ArrayList<ProcessInfo>();
				systemProcessInfos = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : infos) {
					if (info.isUsertask()) {
						// 用户进程
						userProcessInfos.add(info);
					} else {
						// 系统进程
						systemProcessInfos.add(info);
					}
				}
				// 通知界面更新
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class TaskManagerAdapter extends BaseAdapter {
		// 控制listview里面显示多少个数据
		@Override
		public int getCount() {
			if (getSharedPreferences("config", MODE_PRIVATE).getBoolean(
					"showsystem", true)) {
				return userProcessInfos.size() + 1 + systemProcessInfos.size()
						+ 1;
			}else{
				return userProcessInfos.size() + 1 ;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ProcessInfo processInfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户进程:" + userProcessInfos.size() + "个");
				return tv;
			} else if (position == (userProcessInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统进程:" + systemProcessInfos.size() + "个");
				return tv;
			} else if (position <= userProcessInfos.size()) {
				processInfo = userProcessInfos.get(position - 1);// 减去第一个textview占据的位置.
			} else {
				processInfo = systemProcessInfos.get(position - 1
						- userProcessInfos.size() - 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_taskinfo, null);
				holder = new ViewHolder();
				holder.iv_appicon = (ImageView) view
						.findViewById(R.id.iv_appicon);
				holder.tv_appname = (TextView) view
						.findViewById(R.id.tv_appname);
				holder.tv_memsize = (TextView) view
						.findViewById(R.id.tv_memsize);
				holder.cb = (CheckBox) view.findViewById(R.id.cb);
				view.setTag(holder);
			}
			holder.iv_appicon.setImageDrawable(processInfo.getIcon());
			holder.tv_appname.setText(processInfo.getAppname());
			holder.tv_memsize.setText("内存占用:"
					+ Formatter.formatFileSize(getApplicationContext(),
					processInfo.getMemsize()));
			holder.cb.setChecked(processInfo.isChecked());
			if (processInfo.getPackname().equals(getPackageName())) {
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setVisibility(View.VISIBLE);
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
		ImageView iv_appicon;
		TextView tv_appname;
		TextView tv_memsize;
		CheckBox cb;
	}

	/**
	 * 全选
	 *
	 * @param view
	 */
	public void selectAll(View view) {
		for (ProcessInfo info : userProcessInfos) {
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		for (ProcessInfo info : systemProcessInfos) {
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选
	 *
	 * @param view
	 */
	public void selectOpposite(View view) {
		for (ProcessInfo info : userProcessInfos) {
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		for (ProcessInfo info : systemProcessInfos) {
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 杀死选中的进程
	 *
	 * @param view
	 */
	public void killSelect(View view) {
		int count = 0;
		long savedmem = 0;
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 记录被杀掉的进程信息
		List<ProcessInfo> killedProcess = new ArrayList<ProcessInfo>();
		for (ProcessInfo info : userProcessInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				count++;
				savedmem += info.getMemsize();
				killedProcess.add(info);
			}
		}
		for (ProcessInfo info : systemProcessInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackname());
				count++;
				savedmem += info.getMemsize();
				killedProcess.add(info);
			}
		}
		for (ProcessInfo info : killedProcess) {
			if (info.isUsertask()) {
				userProcessInfos.remove(info);
			} else {
				systemProcessInfos.remove(info);
			}
		}
		ToastUtils.show(
				this,
				"清理了" + count + "个进程,释放了"
						+ Formatter.formatFileSize(this, savedmem) + "的内存");
		adapter.notifyDataSetChanged();
		runningProcessCount -= count;
		availRam += savedmem;
		tv_process_count.setText("运行进程:" + runningProcessCount + "个");
		tv_memory_info.setText("剩余/总内存:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
	}

	/**
	 * 打开进程管理器的设置界面.
	 *
	 * @param view
	 */
	public void openSetting(View view) {
		IntentUtils.startActivity(this, TaskManagerSettingActivity.class);
	}

	/**
	 * 界面用户可见调用的方法
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		}
	}
}
