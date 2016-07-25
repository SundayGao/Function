package com.gyw.function.function3.clickdeflect;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.gyw.function.R;


public class ClickDeflectActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    MyImageView joke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_click_deflect_main);
        joke = (MyImageView) findViewById(R.id.c_joke);
        joke.setOnClickIntent(new MyImageView.OnViewClick() {

            @Override
            public void onClick() {
                // TODO Auto-generated method stub
                Toast.makeText(ClickDeflectActivity.this, "事件触发", Toast.LENGTH_SHORT).show();
                System.out.println("1");
            }
        });
    }
}