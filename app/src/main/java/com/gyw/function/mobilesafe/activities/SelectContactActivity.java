package com.gyw.function.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.utils.ContactInfoUtils;

import java.util.List;

public class SelectContactActivity extends Activity {
	private ListView lv_contacts;
	private List<ContactInfoUtils.ContactInfo> infos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lv_contacts = (ListView) findViewById(R.id.lv_contacts);
		//获取手机里面全部的联系人信息.
		infos = ContactInfoUtils.getContactInfos(this);
		lv_contacts.setAdapter(new ContactAdapter());
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String phone = infos.get(position).phone;
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				//关闭界面
				finish();
			}
		});

	}

	private class ContactAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return infos.size();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.item_contact, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			tv_name.setText(infos.get(position).name);
			tv_phone.setText(infos.get(position).phone);
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
}
