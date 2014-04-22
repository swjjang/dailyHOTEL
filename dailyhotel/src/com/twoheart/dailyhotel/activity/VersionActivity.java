package com.twoheart.dailyhotel.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class VersionActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "VersionActivity";

	private TextView tvCurrentVersion, tvNewVersion;
	private Button btnUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("버전정보");
		setContentView(R.layout.activity_version);

		tvCurrentVersion = (TextView) findViewById(R.id.tv_version_cur);
		tvNewVersion = (TextView) findViewById(R.id.tv_version_new);
		btnUpdate = (Button) findViewById(R.id.btn_version_update);
		btnUpdate.setOnClickListener(this);
		
		getVersionInfo();

	}

	public void getVersionInfo() {
		try {
			tvCurrentVersion.setText("v"
					+ getPackageManager().getPackageInfo(this.getPackageName(),
							0).versionName);
			tvNewVersion.setText("v"
					+ sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0"));
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

	}

	// Jason Park | Case of max version is equal to current version...
	@Override
	public void onClick(View v) {
 		if (v.getId() == btnUpdate.getId()) {
			try {
				int maxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, null).replace(".", ""));
				int currentVersion = Integer.parseInt(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName.replace(".", ""));

				if (maxVersion == currentVersion) {
					Toast.makeText(this, "이미 최신버전입니다.", Toast.LENGTH_LONG).show();
				} else {
					Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
					marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));
					// Play Store
//					marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_DAILYHOTEL));
					// T Store
//					 marketLaunch.setData(Uri.parse(URL_STORE_T_DAILYHOTEL));
					startActivity(marketLaunch);
				}

			} catch (Exception e) {
				e.toString();
			}
		}
	}

	// Jason Park
	public void showAlert(String str) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage(str);
		alert.show();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
	}
}
