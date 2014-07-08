package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;

public class NetworkErrorFragment extends DialogFragment implements OnClickListener{
	
	private static NetworkErrorFragment instance = null;
	private Button btnSetting;
	private Button btnRetry;
	public Callback setting;
	public Callback retry;
	
	public static NetworkErrorFragment getInstance(Callback setting,Callback retry) {
		if (instance == null) {
			instance = new NetworkErrorFragment();
		}
		instance.setting = setting;
		instance.retry = retry;
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getDialog() != null) {
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			getDialog().setCanceledOnTouchOutside(false);
		}
		
		View v = inflater.inflate(R.layout.fragment_dialog_network_error, container,false);
		btnSetting = (Button)(v.findViewById(R.id.btn_network_error_setting));
		btnRetry= (Button)(v.findViewById(R.id.btn_network_error_retry));
		
		btnSetting.setOnClickListener(this);
		btnRetry.setOnClickListener(this);
		return v; 
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnSetting.getId()) {
			setting.handleMessage(null);
		} else if (v.getId() == btnRetry.getId()) {
			retry.handleMessage(null);
		}
	}

}
