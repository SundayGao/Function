package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.BlackNumberDao;
import com.gyw.function.mobilesafe.domain.BlackNumberInfo;
import com.gyw.function.utils.ToastUtils;

import java.util.List;


public class CallSmsSafeActivity extends Activity {
	private ListView lv_blacknumber;
	private BlackNumberDao dao;
	private CallSmsSafeAdapter adapter;
	private LinearLayout ll_loading;
	private int startIndex = 0;
	private int maxCount = 20;
	/**
	 * 数据库总条目的个数
	 */
	private int totalCount = 0;
	/**
	 * 全部的黑名单号码信息
	 */
	private List<BlackNumberInfo> infos;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter == null) {
				adapter = new CallSmsSafeAdapter();
				lv_blacknumber.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载ui界面
		setContentView(R.layout.activity_callsmssafe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		dao = new BlackNumberDao(this);
		totalCount = dao.getTotalCount();
		fillData();
		// 给listview注册一个滚动监听器
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			/**
			 * 当listview的滚动状态发生变化的时候调用的方法
			 *
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态, listview停止在界面上
						// 判断listview界面上的最后一个用户可见的条目的信息
						// 得到listview界面最后一个可见条目在集合里面的位置,位置id是从0开始的.
						int position = lv_blacknumber.getLastVisiblePosition();
						int size = infos.size();// 集合的总大小 从1开始的.
						if (position == (size - 1)) {
							//System.out.println("用户把listview移动到了最下面,加载更多数据");
							startIndex += maxCount;
							if(startIndex>=totalCount){
								//说明数据已经加载到最后了.
								ToastUtils.show(CallSmsSafeActivity.this, "没有更多数据了");
								return;
							}
							// 查询数据库把新的数据获取出来.
							fillData();
						}
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 触摸滚动

						break;
					case OnScrollListener.SCROLL_STATE_FLING: // 滑 翔 ,惯性滚动

						break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {

			}
		});
	}

	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				if (infos == null) {
					infos = dao.findPart(startIndex, maxCount);// 有可能是一个耗时的操作
				} else {
					infos.addAll(dao.findPart(startIndex, maxCount));
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 添加黑名单号码的点击事件
	 *
	 * @param view
	 *            当前的按钮
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		// 初始化对话框的布局
		View dialogView = View.inflate(getApplicationContext(),
				R.layout.dialog_add_blacknumber, null);
		final AlertDialog dialog = builder.create();
		final EditText et_black_number = (EditText) dialogView
				.findViewById(R.id.et_black_number);
		final RadioGroup rg_mode = (RadioGroup) dialogView
				.findViewById(R.id.rg_mode);
		Button bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
		Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_black_number.getText().toString().trim();
				if (TextUtils.isEmpty(phone)) {
					ToastUtils.show(CallSmsSafeActivity.this, "黑名单号码不能为空");
					return;
				}
				int id = rg_mode.getCheckedRadioButtonId();
				String mode = "3";
				switch (id) {
					case R.id.rb_phone:
						mode = "1";
						break;
					case R.id.rb_sms:
						mode = "2";
						break;
					case R.id.rb_all:
						mode = "3";
						break;
				}
				// 只是把数据加入到了数据库,没有刷新界面.
				boolean result = dao.add(phone, mode);
				// 刷新界面.
				if (result) {
					// 添加成功
					ToastUtils.show(CallSmsSafeActivity.this, "添加成功");
					BlackNumberInfo object = new BlackNumberInfo();
					object.setMode(mode);
					object.setPhone(phone);
					infos.add(0, object);
					// 通知listview界面刷新
					// lv_blacknumber.setAdapter(new
					// CallSmsSafeAdapter());导致界面回到第一个条目
					adapter.notifyDataSetChanged();// 通知 listview界面更新
				} else {
					ToastUtils.show(CallSmsSafeActivity.this, "添加失败");
				}
				dialog.dismiss();
			}
		});
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return infos.size();
		}

		/**
		 * @param position
		 *            位置
		 * @param convertview
		 *            历史回收的view对象. 当某个view对象被完全移除屏幕的时候 1.
		 *            尽量的复用converview(历史缓存的view),减少view对象创建的个数 2. 尽量的减少
		 *            子孩子id的查询次数 . 定义一个viewholder
		 */
		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				// 复用历史view对象
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 创建新的view对象
				view = View.inflate(getApplicationContext(),
						R.layout.item_callsmssafe, null);
				// 每次寻找子孩子消耗很多的资源cpu 内存.
				// 只有当子view对象第一次被创建的时候 查询id
				holder = new ViewHolder();
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_black_phone);
				holder.iv_delete_blacknumber = (ImageView) view
						.findViewById(R.id.iv_delete_blacknumber);
				view.setTag(holder);
			}
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("全部拦截");
			}
			final String phone = infos.get(position).getPhone();
			holder.tv_phone.setText(phone);
			holder.iv_delete_blacknumber
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							AlertDialog.Builder builder = new Builder(
									CallSmsSafeActivity.this);
							builder.setTitle("警告");
							builder.setMessage("确定删除这个黑名单号码么?");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// 删除数据库的记录
											boolean result = dao.delete(phone);
											if (result) {
												ToastUtils
														.show(CallSmsSafeActivity.this,
																"删除成功");
												// 删除ui界面的数据
												infos.remove(position);
												// 刷新界面
												adapter.notifyDataSetChanged();
											} else {
												ToastUtils
														.show(CallSmsSafeActivity.this,
																"删除失败");
											}
										}
									});
							builder.setNegativeButton("取消", null);
							builder.show();
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

	/**
	 * 子孩子id的容器
	 */
	static class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete_blacknumber;
	}
}
