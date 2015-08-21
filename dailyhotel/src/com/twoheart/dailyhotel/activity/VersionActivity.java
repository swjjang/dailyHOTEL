package com.twoheart.dailyhotel.activity;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class VersionActivity extends BaseActivity implements OnClickListener
{
	private TextView tvCurrentVersion, tvNewVersion;
	private TextView btnUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_version);
		setActionBar(R.string.actionbar_title_version_activity);

		tvCurrentVersion = (TextView) findViewById(R.id.tv_version_cur);
		tvNewVersion = (TextView) findViewById(R.id.tv_version_new);
		btnUpdate = (TextView) findViewById(R.id.btn_version_update);
		//		btnUpdate.setOnClickListener(this);

		getVersionInfo();

	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(VersionActivity.this).recordScreen(Screen.VERSION);
		super.onStart();
	}

	public void getVersionInfo()
	{
		try
		{
			tvCurrentVersion.setText("v" + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
			tvNewVersion.setText("v" + sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0"));
		} catch (Exception e)
		{
			onError(e);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnUpdate.getId())
		{
			try
			{
				int maxVersion = Integer.parseInt(sharedPreference.getString(KEY_PREFERENCE_MAX_VERSION_NAME, "1.0.0").replace(".", ""));
				int currentVersion = Integer.parseInt(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName.replace(".", ""));

				if (maxVersion > currentVersion)
				{
					Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
					marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));
					startActivity(marketLaunch);
				} else
				{
					DailyToast.showToast(VersionActivity.this, R.string.toast_msg_already_latest_version, Toast.LENGTH_SHORT);
				}
			} catch (Exception e)
			{
				onError(e);
			}

		}
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}
}
