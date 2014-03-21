package kr.co.kcp.android.payment.standard;

import kr.co.kcp.android.payment.standard.KcpApplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ResultRcvActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(KcpApplication.m_strLogTag,
				"[ResultRcvActivity] called__onCreate");

		super.onCreate(savedInstanceState);

		// TODO Auto-generated method stub
		KcpApplication myApp = (KcpApplication) getApplication();
		Intent myIntent = getIntent();

		Log.d(KcpApplication.m_strLogTag, "[ResultRcvActivity] launch_uri=["
				+ myIntent.getData().toString() + "]");

		if (myIntent.getData().getScheme().equals("dailyHOTEL") == true) {
			myApp.m_uriResult = myIntent.getData();
		} else {
			myApp.m_uriResult = null;
		}

		finish();
	}
}