package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.services.CallSmsSafeService;
import com.gyw.function.mobilesafe.services.ShowAddressService;
import com.gyw.function.mobilesafe.services.WatchDogService;
import com.gyw.function.mobilesafe.ui.ServiceStautsUtils;
import com.gyw.function.mobilesafe.ui.SettingChangeView;
import com.gyw.function.mobilesafe.ui.SettingCheckView;


public class SettingActivity extends Activity {
	private SharedPreferences sp;
	private String s;
	private SettingCheckView scv_setting_update;
	/**
	 * 黑名单拦截的组合控件
	 */
	private SettingCheckView scv_setting_blacknumber;

	/**
	 * 归属地显示
	 */
	private SettingCheckView scv_setting_showaddress;

	/**
	 * 看门狗设置
	 */
	private SettingCheckView scv_setting_applock;

	/**
	 * 更改背景的组合控件
	 */
	private SettingChangeView scv_change_bg;

	private static final String items[] = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		scv_change_bg = (SettingChangeView) findViewById(R.id.scv_change_bg);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		scv_change_bg.setDesc(items[sp.getInt("which", 0)]);
		//自动更新的逻辑
		scv_setting_update = (SettingCheckView) findViewById(R.id.scv_setting_update);
		scv_setting_update.setChecked(sp.getBoolean("update", false));
		scv_setting_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(scv_setting_update.isChecked()){
					scv_setting_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					scv_setting_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		//黑名单拦截的逻辑
		scv_setting_blacknumber = (SettingCheckView) findViewById(R.id.scv_setting_blacknumber);
		scv_setting_blacknumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(scv_setting_blacknumber.isChecked()){
					scv_setting_blacknumber.setChecked(false);
					//停止服务,取消注册广播接受者
					Intent intent = new Intent(SettingActivity.this,CallSmsSafeService.class);
					stopService(intent);
				}else{
					scv_setting_blacknumber.setChecked(true);
					//开启服务 注册广播接受者
					Intent intent = new Intent(SettingActivity.this,CallSmsSafeService.class);
					startService(intent);
				}
			}
		});
		//号码归属地显示
		scv_setting_showaddress = (SettingCheckView) findViewById(R.id.scv_setting_showaddress);
		scv_setting_showaddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(scv_setting_showaddress.isChecked()){
					scv_setting_showaddress.setChecked(false);
					Intent intent = new Intent(SettingActivity.this,ShowAddressService.class);
					stopService(intent);
				}else{
					scv_setting_showaddress.setChecked(true);
					Intent intent = new Intent(SettingActivity.this,ShowAddressService.class);
					startService(intent);
				}
			}
		});
		//看门狗设置
		scv_setting_applock = (SettingCheckView) findViewById(R.id.scv_setting_applock);
		scv_setting_applock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(scv_setting_applock.isChecked()){
					scv_setting_applock.setChecked(false);
					Intent intent = new Intent(SettingActivity.this,WatchDogService.class);
					stopService(intent);
				}else{
					scv_setting_applock.setChecked(true);
					Intent intent = new Intent(SettingActivity.this,WatchDogService.class);
					startService(intent);
				}
			}
		});


	}

	@Override
	protected void onStart() {
		//读取系统的运行信息,看看我的服务是否还活着,如果服务活着,就设置勾选为true,如果服务停止了就设置勾选false
		if(ServiceStautsUtils.isServiceRunning(this, "com.gyw.function.mobilesafe.services.CallSmsSafeService")){
			scv_setting_blacknumber.setChecked(true);
		}else{
			scv_setting_blacknumber.setChecked(false);
		}
		if(ServiceStautsUtils.isServiceRunning(this, "com.gyw.function.mobilesafe.services.ShowAddressService")){
			scv_setting_showaddress.setChecked(true);
		}else{
			scv_setting_showaddress.setChecked(false);
		}
		if(ServiceStautsUtils.isServiceRunning(this, "com.gyw.function.mobilesafe.services.WatchDogService")){
			scv_setting_applock.setChecked(true);
		}else{
			scv_setting_applock.setChecked(false);
		}
		super.onStart();
	}

	/**
	 * 更改归属地提示框的背景
	 * @param view
	 */
	public void changeAddressBg(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("归属地提示框风格");

		builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = sp.edit();
				editor.putInt("which", which);
				editor.commit();
				dialog.dismiss();
				scv_change_bg.setDesc(items[which]);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				s.equals("gaga");
			}
		});
		builder.show();
	}
}
