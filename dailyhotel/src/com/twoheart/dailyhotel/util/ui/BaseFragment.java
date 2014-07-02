package com.twoheart.dailyhotel.util.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public abstract class BaseFragment extends Fragment implements Constants, OnLoadListener, ErrorListener {

	protected BaseActivity mHostActivity;
	protected RequestQueue mQueue;
	protected Toast mToast;

	private String title;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHostActivity = (BaseActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mQueue = VolleyHttpClient.getRequestQueue();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		GlobalFont.apply((ViewGroup) getView().getRootView());
	}

	@Override
	public void onPause() {
		if (mToast != null)
			mToast.cancel();

		super.onPause();
	}

	public void showToast(String message, int length, boolean isAttachToFragment) {
		if (isAttachToFragment) {
			mToast = Toast.makeText(mHostActivity.getApplicationContext(), message, length);
			mToast.show();

		} else {
			Toast.makeText(mHostActivity.getApplicationContext(), message, length).show();

		}
	}

	public void onError(Exception error) {
		mHostActivity.onError(error);
	}

	public void onError() {
		mHostActivity.onError();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		mHostActivity.onErrorResponse(error);
	}

	@Override
	public void lockUI() {
		mHostActivity.lockUI();
	}

	@Override
	public void unLockUI() {
		mHostActivity.unLockUI();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	protected void chgClickable(View v) {
		v.setClickable(!v.isClickable());
	}
}
