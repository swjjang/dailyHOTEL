package com.twoheart.dailyhotel.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.BookingAdapter;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.view.HotelViewPager;
import com.viewpagerindicator.TabPageIndicator;

public class BookingTabActivity extends BaseActivity {

	private final static String TAG ="BookingTabActivity";
	
	private HotelViewPager pager;
	private TabPageIndicator indicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("예약확인");
		setContentView(R.layout.activity_booking_tab);
		loadResource();
		
		FragmentPagerAdapter adapter = new BookingAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		
	}
	
	public void loadResource() {
		pager = (HotelViewPager) findViewById(R.id.booking_pager);
		indicator = (TabPageIndicator) findViewById(R.id.booking_indicator);
	}
	
	@Override
	public void onBackPressed() {
		finish();
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
