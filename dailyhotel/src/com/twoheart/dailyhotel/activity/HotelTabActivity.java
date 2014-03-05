package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_NAME;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelTabAdapter;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.view.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class HotelTabActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "HotelTabActivity";
	
	private static final int HOTEL_TAB_ACTIVITY = 1;
	
	private HotelViewPager pager;
	private TabPageIndicator indicator;
	
	String hotel_name;
	int hotel_idx;
	int avail_cnt;
	
	private Button tv_soldout;
	private Button btn_booking;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_tab);
		setActionBar(false);
		
		pager = (HotelViewPager) findViewById(R.id.pager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		
		tv_soldout = (Button) findViewById(R.id.tv_hotel_tab_soldout);
		btn_booking = (Button) findViewById(R.id.btn_hotel_tab_booking);
		btn_booking.setOnClickListener(this);
		
		// 선택된 호텔의 name, idx, avail_cnt 받음
		Intent intent = getIntent();
		avail_cnt = intent.getIntExtra("available_cnt", 0);
		
		// 호텔 sold out시
		if (avail_cnt == 0) {
			btn_booking.setVisibility(View.GONE);
			tv_soldout.setVisibility(View.VISIBLE);
		}

		SharedPreferences.Editor ed = prefs.edit();
		ed.putString(PREFERENCE_HOTEL_IDX, Integer.toString(intent.getIntExtra("hotel_idx", 0)));
		ed.putString(PREFERENCE_HOTEL_NAME, intent.getStringExtra("hotel_name"));
		ed.putString(PREFERENCE_HOTEL_YEAR, intent.getStringExtra("year"));
		ed.putString(PREFERENCE_HOTEL_MONTH, intent.getStringExtra("month"));
		ed.putString(PREFERENCE_HOTEL_DAY, intent.getStringExtra("day"));
		ed.commit();
		
		// create viewpager
		FragmentPagerAdapter adapter = new HotelTabAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		
		indicator.setViewPager(pager);
	}
	
	public void parseJson(String str) {
		FragmentPagerAdapter adapter = new HotelTabAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
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
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btn_booking.getId()) {
			
			if(checkLogin()) {
				Intent i = new Intent(this, HotelPaymentActivity.class);
				startActivityForResult(i, HOTEL_TAB_ACTIVITY);
				overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			} else {
				
				Intent i = new Intent(this, LoginActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_right,R.anim.hold);
			}
		}
	}
	
	public boolean checkLogin() {
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == HOTEL_TAB_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
