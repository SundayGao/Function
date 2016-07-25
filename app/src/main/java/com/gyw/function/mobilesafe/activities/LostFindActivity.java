package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.utils.IntentUtils;


public class LostFindActivity extends Activity {
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_find);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
		iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
		tv_lostfind_number.setText(sp.getString("safenumber", ""));
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			iv_lostfind_status.setImageResource(R.drawable.lock);
		}else{
			iv_lostfind_status.setImageResource(R.drawable.unlock);
		}
	}
	
	public void reEntrySetup(View view){
		IntentUtils.startActivityAndFinish(this, Setup1Activity.class);
	}
}
