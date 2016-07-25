package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.utils.ToastUtils;


public class EnterPasswordActivity extends Activity {
	private ImageView iv_locked_icon;
	private TextView tv_locked_name;
	private EditText et_password;
	private String packname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		tv_locked_name = (TextView) findViewById(R.id.tv_locked_name);
		iv_locked_icon = (ImageView) findViewById(R.id.iv_locked_icon);
		et_password  = (EditText) findViewById(R.id.et_password);
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");
		PackageManager pm = getPackageManager();
		try {
			String appname = pm.getPackageInfo(packname, 0).applicationInfo.loadLabel(pm)
					.toString();
			Drawable appicon = pm.getPackageInfo(packname, 0).applicationInfo.loadIcon(pm);
			tv_locked_name.setText(appname);
			iv_locked_icon.setImageDrawable(appicon);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}

	/**
	 * 只要Activity界面不可见,就立刻关闭掉当前页面.
	 */
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}

	public void click(View view){
		String password = et_password.getText().toString().trim();
		if("123".equals(password)){
			//通知看门狗,这个应用程序临时的取消保护
			//在Activity给服务一个消息.
			//发送一个自定义的广播,这个广播只有看门狗能够识别.
			Intent intent = new Intent();
			intent.setAction("com.gyw.function.mobilesafe.wangwang");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			//密码输入正确
			finish();
		}else{
			ToastUtils.show(this, "密码输入错误");
		}
	}

}
