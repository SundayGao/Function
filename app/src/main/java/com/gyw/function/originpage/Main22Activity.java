package com.gyw.function.originpage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.gyw.function.R;
import com.gyw.function.MainActivity;

public class Main22Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
    }

    public void btnOnClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.bt_main_1:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.bt_main_2:
                startActivity(new Intent(this, FullscreenActivity.class));
                break;
            case R.id.bt_main_3:
                startActivity(new Intent(this, Main222Activity.class));
                break;
            case R.id.bt_main_4:
                startActivity(new Intent(this, Main23Activity.class));
                break;
            case R.id.bt_main_5:
                startActivity(new Intent(this, ScrollingActivity.class));
                break;
            case R.id.bt_main_6:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.bt_main_7:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.bt_main_8:
                startActivity(new Intent(this, ItemListActivity.class));
                break;
            case R.id.bt_main_9:
                startActivity(new Intent(this, Main24Activity.class));
                break;
        }
    }
}
