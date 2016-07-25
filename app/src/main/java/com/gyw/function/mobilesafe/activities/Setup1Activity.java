package com.gyw.function.mobilesafe.activities;

import android.os.Bundle;

import com.gyw.function.R;
import com.gyw.function.utils.IntentUtils;


public class Setup1Activity extends SetupBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	@Override
	public void showNext() {
		IntentUtils.startActivityAndFinish(this, Setup2Activity.class);
	}

	@Override
	public void showPre() {
		
	}
}
