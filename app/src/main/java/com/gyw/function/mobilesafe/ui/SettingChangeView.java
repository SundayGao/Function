package com.gyw.function.mobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyw.function.R;


public class SettingChangeView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_desc;


    public SettingChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingChangeView);
        String title = typedArray.getString(R.styleable.SettingChangeView_mytitle);
        String desc = typedArray.getString(R.styleable.SettingChangeView_desc);
        typedArray.recycle();

        tv_desc.setText(desc);
        tv_title.setText(title);
    }

    /**
     * 设置里面显示的文本
     *
     * @param text
     */
    public void setDesc(String text) {
        tv_desc.setText(text);
    }

    public SettingChangeView(Context context) {
        super(context, null);
        initView(context);
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        View.inflate(context, R.layout.ui_change_view, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
    }

}
