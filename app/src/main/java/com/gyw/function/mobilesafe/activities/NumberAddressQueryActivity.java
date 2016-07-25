package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.NumberAddressDao;
import com.gyw.function.utils.ToastUtils;

public class NumberAddressQueryActivity extends Activity {
	private EditText et_number;
	private TextView tv_result;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_query);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_number = (EditText) findViewById(R.id.et_number);
		tv_result = (TextView) findViewById(R.id.tv_result);
	}

	public void query(View view){
		String number = et_number.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			ToastUtils.show(this, "号码不能为空");
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);
			vibrator.vibrate(100);
			return;
		}
		String address = NumberAddressDao.getAddress(number);
		tv_result.setText("归属地:"+address);
	}
}
