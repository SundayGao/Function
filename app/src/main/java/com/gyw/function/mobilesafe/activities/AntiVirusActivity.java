package com.gyw.function.mobilesafe.activities;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gyw.function.R;
import com.gyw.function.mobilesafe.db.dao.AntiVirusDao;


public class AntiVirusActivity extends Activity {
	private ImageView iv_scan;
	private ProgressBar pb;
	private LinearLayout ll_container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		pb = (ProgressBar) findViewById(R.id.pb);
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(2000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);
		scanVirus();
	}

	/**
	 * 扫描病毒
	 */
	private void scanVirus() {
		new Thread() {
			public void run() {
				// 遍历手机里面的每一个应用程序信息. 查询他的特征码在病毒数据库是否存在.
				PackageManager pm = getPackageManager();
				List<PackageInfo> packinfos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES+PackageManager.GET_SIGNATURES);
				pb.setMax(packinfos.size());
				int process = 0;
				for (PackageInfo packinfo : packinfos) {
					try {
						String apkpath = packinfo.applicationInfo.sourceDir;
						//System.out.println("程序名:"+packinfo.applicationInfo.loadLabel(pm));
						//System.out.println("签名:"+Md5Utils.encode(packinfo.signatures[0].toCharsString()));
						File file = new File(apkpath);
						MessageDigest digest = MessageDigest.getInstance("md5");
						FileInputStream fis = new FileInputStream(file);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = fis.read(buffer)) != -1) {
							digest.update(buffer, 0, len);
						}
						byte[] result = digest.digest();
						StringBuffer sb = new StringBuffer();
						for (byte b : result) {
							String str = Integer.toHexString(b & 0xff);
							if (str.length() == 1) {
								sb.append("0");
							}
							sb.append(str);
						}
						String md5 = sb.toString();
						// 检查md5的特征码在病毒数据库里面是否存在.
						final String info = AntiVirusDao.isVirus(md5);
						final String appname = packinfo.applicationInfo
								.loadLabel(pm).toString();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								TextView tv = new TextView(
										getApplicationContext());
								if (info != null) {
									// 发现病毒
									tv.setText(appname + "发现病毒");
									tv.setTextColor(Color.RED);
								} else {
									// 扫描安全
									tv.setText(appname + "扫描安全");
									tv.setTextColor(Color.GREEN);
								}
								ll_container.addView(tv, 0);
							}
						});
						Thread.sleep(50);
						process++;
						pb.setProgress(process);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			};
		}.start();
	}
}
