package com.twoheart.dailyhotel.activity;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ProfileActivity extends BaseActivity implements DailyHotelStringResponseListener, DailyHotelJsonResponseListener, OnClickListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("내 프로필");
		setContentView(R.layout.activity_profile);
		
		
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponse(String url, String response) {
		// TODO Auto-generated method stub
		
	}

}
