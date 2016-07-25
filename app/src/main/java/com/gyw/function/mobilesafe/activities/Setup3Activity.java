package com.gyw.function.mobilesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.gyw.function.R;
import com.gyw.function.utils.IntentUtils;
import com.gyw.function.utils.ToastUtils;


public class Setup3Activity extends SetupBaseActivity {
	private EditText et_setup3_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup3_phone);
		et_setup3_phone.setText(sp.getString("safenumber", ""));
	}

	public void selectContact(View view){
		//选择联系人,开启一个新的界面,
		//开启界面获取返回值
		Intent intent = new Intent(this,SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			String phone = data.getStringExtra("phone").replace("-", "").trim();
			et_setup3_phone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void showNext() {
		String phone = et_setup3_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastUtils.show(this, "安全号码不能为空");
			return;
		}
		Editor editor = sp.edit();
		editor.putString("safenumber", phone);
		editor.commit();
		IntentUtils.startActivityAndFinish(this, Setup4Activity.class);
	}

	@Override
	public void showPre() {
		IntentUtils.startActivityAndFinish(this, Setup2Activity.class);
	}

}
