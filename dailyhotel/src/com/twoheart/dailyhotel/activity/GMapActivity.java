package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class GMapActivity extends BaseActivity {
	
	private static final String TAG = "GMapActivity";
	private GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("지도 확대");
		setContentView(R.layout.activity_gmap);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				try {
//					googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();
//					prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
//					addMarker(Double.parseDouble(prefs.getString(PREFERENCE_HOTEL_LAT, "0")), Double.parseDouble(prefs.getString(PREFERENCE_HOTEL_LNG, "0")), prefs.getString(PREFERENCE_HOTEL_NAME, "No data"));
//				} catch (Exception e) {
//					Log.d(TAG, e.toString());
//				}
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
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
	}
	
	
}
