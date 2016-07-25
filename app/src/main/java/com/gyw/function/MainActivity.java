package com.gyw.function;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.gyw.function.function3.clickdeflect.ClickDeflectActivity;
import com.gyw.function.mobilesafe.activities.HomeActivity;
import com.gyw.function.originpage.Main222Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void btnOnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_function_1:
                startActivity(new Intent(this, Main222Activity.class));
                break;
            case R.id.bt_function_2:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.bt_function_3:
                startActivity(new Intent(this, ClickDeflectActivity.class));
                break;
            case R.id.bt_function_4:
                Toast.makeText(this, "function 4", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
