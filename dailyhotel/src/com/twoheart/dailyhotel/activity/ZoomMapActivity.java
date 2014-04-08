package com.twoheart.dailyhotel.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ZoomMapActivity extends BaseActivity {
	
	private static final String TAG = "GMapActivity";
	private GoogleMap googleMap;
	private HotelDetail mHotelDetail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setActionBar("지도 확대");
//		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));
		setContentView(R.layout.activity_zoom_map);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
			mHotelDetail = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
		
		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();
		
		if (googleMap != null) {
			googleMap.setMyLocationEnabled(false);	
		}
		
		addMarker(mHotelDetail.getLatitude(), mHotelDetail.getLongitude(),
				mHotelDetail.getHotel().getName());
		
	}
	
	public void addMarker(Double lat, Double lng, String hotel_name) {
		if(googleMap != null) {
			googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			LatLng address = new LatLng(lat,lng);
			CameraPosition cp = new CameraPosition.Builder().target((address )).zoom(15).build();
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		}
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
	}
	
	
}
