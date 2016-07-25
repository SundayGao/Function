package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.services.AutoKillService;
import com.gyw.function.mobilesafe.ui.ServiceStautsUtils;
import com.gyw.function.mobilesafe.ui.SettingCheckView;


public class TaskManagerSettingActivity extends Activity {
	private SettingCheckView scv_showsystem;
	private SharedPreferences sp;

	/**
	 * 锁屏清理进程
	 */
	private SettingCheckView scv_autokill;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		scv_showsystem = (SettingCheckView) findViewById(R.id.scv_showsystem);
		scv_showsystem.setChecked(sp.getBoolean("showsystem", true));
		scv_showsystem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(scv_showsystem.isChecked()){
					scv_showsystem.setChecked(false);
					editor.putBoolean("showsystem", false);
				}else{
					scv_showsystem.setChecked(true);
					editor.putBoolean("showsystem", true);
				}
				editor.commit();
			}
		});
		scv_autokill = (SettingCheckView) findViewById(R.id.scv_autokill);
		scv_autokill.setChecked(sp.getBoolean("autokill", true));
		scv_autokill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(scv_autokill.isChecked()){
					scv_autokill.setChecked(false);
					editor.putBoolean("autokill", false);
					Intent intent = new Intent(getApplicationContext(),AutoKillService.class);
					stopService(intent);
				}else{
					scv_autokill.setChecked(true);
					editor.putBoolean("autokill", true);
					Intent intent = new Intent(getApplicationContext(),AutoKillService.class);
					startService(intent);
				}
				editor.commit();
			}
		});
	}
	@Override
	protected void onStart() {
		if(ServiceStautsUtils.isServiceRunning(this, "com.gyw.function.mobilesafe.services.AutoKillService")){
			scv_autokill.setChecked(true);
		}else{
			scv_autokill.setChecked(false);
		}
		super.onStart();
	}
}
