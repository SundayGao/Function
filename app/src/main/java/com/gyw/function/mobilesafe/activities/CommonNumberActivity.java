package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.CommonNumberDao;


public class CommonNumberActivity extends Activity {
    private ExpandableListView elv;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = SQLiteDatabase.openDatabase("/data/data/com.itheima.mobilesafe/files/commonnum.db",
                null, SQLiteDatabase.OPEN_READONLY);
        setContentView(R.layout.activity_common_num);
        elv = (ExpandableListView) findViewById(R.id.elv);
        elv.setAdapter(new CommonNumAdapter());
        elv.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), groupPosition + "--" + childPosition + "�������", 0).show();
                return false;
            }
        });
    }

    private class CommonNumAdapter extends BaseExpandableListAdapter {

        /**
         * ��ȡ�ж��ٸ�����
         */
        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(db);
        }

        /**
         * ����ĳ��λ�ӵķ����ж��ٸ�����
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao
                    .getChildrenCountByGroupPosition(db, groupPosition);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(20);
            tv.setTextColor(Color.RED);
            tv.setText("       "
                    + CommonNumberDao.getNameByGroupPosition(db, groupPosition));
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) convertView;
            }
            tv.setTextSize(16);
            tv.setTextColor(Color.BLACK);
            tv.setText(CommonNumberDao.getChildNameByPosition(db, groupPosition,
                    childPosition));
            return tv;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
