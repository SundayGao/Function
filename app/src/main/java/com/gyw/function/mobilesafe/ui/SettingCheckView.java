package com.gyw.function.mobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyw.function.R;


public class SettingCheckView extends LinearLayout {
    private CheckBox cb_status;

    public SettingCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingCheckView);
        String bigtitle = typedArray.getString(R.styleable.SettingCheckView_bigtitle);
        typedArray.recycle();

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(bigtitle);
    }

    public SettingCheckView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(View.inflate(context, R.layout.ui_setting_view, null));
        cb_status = (CheckBox) findViewById(R.id.cb_status);
        //System.out.println(cb_status.toString());
    }

    /**
     * 判断组合控件是否被选中
     *
     * @return
     */
    public boolean isChecked() {
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的选中状态
     *
     * @return
     */
    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);
    }
}
