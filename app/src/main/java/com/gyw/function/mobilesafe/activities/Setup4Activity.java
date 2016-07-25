package com.gyw.function.mobilesafe.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.receiver.MyAdmin;
import com.gyw.function.mobilesafe.ui.SettingCheckView;
import com.gyw.function.utils.IntentUtils;

public class Setup4Activity extends SetupBaseActivity {
	private SettingCheckView tv_setup4_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		tv_setup4_status = (SettingCheckView) findViewById(R.id.tv_setup4_status);
		tv_setup4_status.setChecked(sp.getBoolean("protecting", false));
		tv_setup4_status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(tv_setup4_status.isChecked()){
					tv_setup4_status.setChecked(false);
					editor.putBoolean("protecting", false);
				}else{
					tv_setup4_status.setChecked(true);
					editor.putBoolean("protecting", true);
				}
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		//System.out.println("设置完成了. 修改配置文件..");
		Editor editor = sp.edit();
		editor.putBoolean("finishsetup", true);
		editor.commit();
		//进入手机防盗的ui界面.
		IntentUtils.startActivityAndFinish(this, LostFindActivity.class);
	}
	@Override
	public void showPre() {
		IntentUtils.startActivityAndFinish(this, Setup3Activity.class);
	}
	/**
	 * 点击激活设备的超级管理员
	 * @param view
	 */
	public void activeAdmin(View view){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName who = new ComponentName(this, MyAdmin.class);
		// 把要激活的组件名告诉系统
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"开启后可以实现远程锁屏和销毁数据");
		startActivity(intent);
	}

}
