package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.gyw.function.R;
import com.gyw.function.utils.IntentUtils;


public class AtoolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_atools);
		super.onCreate(savedInstanceState);
	}
	/**
	 * 号码归属地查询
	 * @param view
	 */
	public void addressQuery(View view) {
		IntentUtils.startActivity(this, NumberAddressQueryActivity.class);
	}
	/**
	 * 常用号码查询
	 */
	public void commonNumQuery(View view){
		IntentUtils.startActivity(this, CommonNumberActivity.class);
	}

	public void openAppLock(View view){
		IntentUtils.startActivity(this, AppLockActivity.class);
	}

}
