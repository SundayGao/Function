package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.utils.IntentUtils;
import com.gyw.function.utils.Md5Utils;
import com.gyw.function.utils.ToastUtils;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private SharedPreferences sp;
	private static final String[] names={"手机防盗","通讯卫士","软件管理","进程管理","流量统计",
			"手机杀毒","缓存清理","高级工具","设置中心"};
	private static int[] icons={
			R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app_selector,
			R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
			R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdManager.getInstance(this).init("e6baa120370bc464", "f38575fc19afc907", true);
		setContentView(R.layout.activity_home);
		LinearLayout ll_adcontainer = (LinearLayout) findViewById(R.id.ll_adcontainer);
		ll_adcontainer.addView(new AdView(this, AdSize.FIT_SCREEN));
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				switch (position) {
					case 0: //手机防盗
						//判断用户是否设置过手机防盗的密码.
						String password = sp.getString("password", null);
						if(TextUtils.isEmpty(password)){
							//弹出设置密码的对话框
							showSetupPasswordDialog();
						}else{
							//弹出输入密码的对话框
							showEnterPasswordDialog();
						}
						break;
					case 1://设置中心
						IntentUtils.startActivity(HomeActivity.this, CallSmsSafeActivity.class);
						break;
					case 2://软件管理器
						IntentUtils.startActivity(HomeActivity.this, AppManagerActivity.class);
						break;
					case 3://进程管理器
						IntentUtils.startActivity(HomeActivity.this, TaskManagerActivity.class);
						break;
					case 4://流量统计
						IntentUtils.startActivity(HomeActivity.this, TrafficManagerActivity.class);
						break;
					case 5://手机杀毒
						IntentUtils.startActivity(HomeActivity.this, AntiVirusActivity.class);
						break;
					case 6://缓存清理
						IntentUtils.startActivity(HomeActivity.this, CleanCacheActivity.class);
						break;
					case 7://高级工具
						IntentUtils.startActivity(HomeActivity.this, AtoolsActivity.class);
						break;
					case 8://设置中心
						IntentUtils.startActivity(HomeActivity.this, SettingActivity.class);
						break;
				}

			}
		});
	}
	//享元模式
	private AlertDialog.Builder builder;
	private View view;
	private EditText et_password;
	private Button bt_ok;
	private Button bt_cancel;

	/**
	 * 输入密码对话框
	 */
	protected void showEnterPasswordDialog() {
		builder = new Builder(this);
		view = View.inflate(this, R.layout.dialog_enter_pwd, null);
		builder.setView(view);
		et_password = (EditText) view.findViewById(R.id.et_password);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String inputPassword = et_password.getText().toString().trim();
				String savedPassword = sp.getString("password", null);
				if(TextUtils.isEmpty(inputPassword)){
					ToastUtils.show(HomeActivity.this, "密码输入不能为空");
					return;
				}
				if(Md5Utils.encode(inputPassword).equals(savedPassword)){
					ToastUtils.show(HomeActivity.this, "密码输入正确,进入手机防盗的界面");
					dialog.dismiss();
					//判断用户是否完成过设置向导,
					boolean finishsetup = sp.getBoolean("finishsetup", false);
					if(finishsetup){
						//如果完成过,进入手机防盗的界面,
						IntentUtils.startActivity(HomeActivity.this, LostFindActivity.class);
					}else{
						//否,则进入设置向导界面.
						IntentUtils.startActivity(HomeActivity.this, Setup1Activity.class);
					}

				}else{
					ToastUtils.show(HomeActivity.this, "密码输入错误");
				}
			}
		});
		dialog = builder.show();
	}
	private AlertDialog dialog;
	/**
	 * 设置密码对话框
	 */
	protected void showSetupPasswordDialog() {
		builder = new Builder(this);
		//自定义对话框显示的内容.
		view = View.inflate(this, R.layout.dialog_setup_pwd, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		final EditText et_password_confirm = (EditText) view.findViewById(R.id.et_password_confirm);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				String password_confirm = et_password_confirm.getText().toString().trim();
				if(TextUtils.isEmpty(password)||TextUtils.isEmpty(password_confirm)){
					ToastUtils.show(HomeActivity.this, "密码不能为空");
					return;
				}
				if(!password.equals(password_confirm)){
					ToastUtils.show(HomeActivity.this, "两次密码输入不一致");
					return;
				}
				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.encode(password));
				editor.commit();
				dialog.dismiss();
				//显示输入密码的对话框
				showEnterPasswordDialog();
			}
		});
		builder.setView(view);
		dialog = builder.show();
	}

	private class HomeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return names.length;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_homeitem_name);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
			tv_name.setText(names[position]);
			iv_icon.setImageResource(icons[position]);
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
}
