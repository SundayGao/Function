package com.gyw.function.mobilesafe.activities;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.ui.SettingCheckView;
import com.gyw.function.utils.IntentUtils;
import com.gyw.function.utils.ToastUtils;

public class Setup2Activity extends SetupBaseActivity {
	private SettingCheckView scv_setup2_bind;
	/**
	 * 电话管理的服务,可以获取手机电话相关的信息
	 */
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		scv_setup2_bind = (SettingCheckView) findViewById(R.id.scv_setup2_bind);
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			scv_setup2_bind.setChecked(false);
		} else {
			scv_setup2_bind.setChecked(true);
		}
		scv_setup2_bind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scv_setup2_bind.isChecked()) {
					// 取消绑定,把保存的串号设置为空
					scv_setup2_bind.setChecked(false);
					Editor editor = sp.edit();
					editor.putString("sim", null);
					editor.commit();
				} else {
					// 绑定,把串号记录下来
					scv_setup2_bind.setChecked(true);
					String sim = tm.getSimSerialNumber();
					//tm.getLine1Number();//得到手机卡的号码. sim卡的芯片上写的有电话号码
					Editor editor = sp.edit();
					editor.putString("sim", sim);
					editor.commit();
				}
			}
		});
	}

	@Override
	public void showNext() {
		if (scv_setup2_bind.isChecked()) {
			IntentUtils.startActivityAndFinish(this, Setup3Activity.class);
		}else{
			ToastUtils.show(this, "要开启手机防盗,必须绑定sim卡串号");
		}
	}

	@Override
	public void showPre() {
		IntentUtils.startActivityAndFinish(this, Setup1Activity.class);
	}
}
