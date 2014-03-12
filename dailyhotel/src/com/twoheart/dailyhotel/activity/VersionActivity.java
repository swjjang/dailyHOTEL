package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_MAX_VERSION_CODE;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_MAX_VERSION_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_MIN_VERSION_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class VersionActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "VersionActivity";

	private TextView tv_cur, tv_new;
	private Button btn_update;

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("버전정보");
		setContentView(R.layout.activity_version);

		loadResource();
		getVersionInfo();

	}

	public void loadResource() {
		tv_cur = (TextView) findViewById(R.id.tv_version_cur);
		tv_new = (TextView) findViewById(R.id.tv_version_new);
		btn_update = (Button) findViewById(R.id.btn_version_update);
		btn_update.setOnClickListener(this);
	}

	public void getVersionInfo() {
		try {
			prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			tv_cur.setText("v"
					+ getPackageManager().getPackageInfo(this.getPackageName(),
							0).versionName);
			tv_new.setText("v"
					+ prefs.getString(PREFERENCE_MAX_VERSION_NAME, "1.0.0"));
		} catch (Exception e) {
			e.toString();
		}

	}

	// Jason Park | Case of max version is equal to current version...
	@Override
	public void onClick(View v) {
 		if (v.getId() == btn_update.getId()) {
			try {
				int max_version = Integer.parseInt(prefs.getString(PREFERENCE_MAX_VERSION_NAME, null).replace(".", ""));
				int current_version = Integer.parseInt(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName.replace(".", ""));

				if (max_version == current_version) {
					Toast.makeText(getApplicationContext(), "이미 최신버전입니다.", Toast.LENGTH_LONG).show();
				} else {
					Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
					// Play Store
					marketLaunch
							.setData(Uri
									.parse("market://details?id=com.twoheart.dailyhotel"));
					// T Store
					// marketLaunch.setData(Uri.parse("http://tsto.re/0000412421"));
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
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
