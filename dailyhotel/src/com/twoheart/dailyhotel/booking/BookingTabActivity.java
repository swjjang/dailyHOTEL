package com.twoheart.dailyhotel.booking;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.common.view.ViewPagerCustom;
import com.viewpagerindicator.TabPageIndicator;

public class BookingTabActivity extends ActionBarActivity {

	private final static String TAG ="BookingTabActivity";
	
	private ViewPagerCustom pager;
	private TabPageIndicator indicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_tab);
		loadResource();
		
		FragmentPagerAdapter adapter = new BookingAdapter(getSupportFragmentManager());
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		
		// setTitle
		setTitle(Html.fromHtml("<font color='#050505'>예약확인</font>"));
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e(TAG, "Exception loading drawable"); 
		}
	}
	
	public void loadResource() {
		pager = (ViewPagerCustom) findViewById(R.id.booking_pager);
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
