package com.twoheart.dailyhotel.hotel;

import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_LAT;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_LNG;
import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_HOTEL_NAME;
import static com.twoheart.dailyhotel.AppConstants.SHARED_PREFERENCES_NAME;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;

public class GMapActivity extends ActionBarActivity {
	
	private static final String TAG = "GMapActivity";
	
	private GoogleMap googleMap;
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_gmap);
		
		setTitle("");
		
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
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				try {
					googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();
					prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
					addMarker(Double.parseDouble(prefs.getString(PREFERENCE_HOTEL_LAT, "0")), Double.parseDouble(prefs.getString(PREFERENCE_HOTEL_LNG, "0")), prefs.getString(PREFERENCE_HOTEL_NAME, "No data"));
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
			}
		}, 3000);
		
	}
	
	public void addMarker(Double lat, Double lng, String hotel_name) {
		if(googleMap != null) {
			googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			LatLng address = new LatLng(lat,lng);
			CameraPosition cp = new CameraPosition.Builder().target((address )).zoom(15).build();
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			googleMap.setMyLocationEnabled(true);
		}
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
