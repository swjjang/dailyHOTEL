package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelNameInfoWindowAdapter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ZoomMapActivity extends BaseActivity
{
	private GoogleMap mGoogleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zoom_map);

		Intent intent = getIntent();

		String hotelName = null;
		double latitude = 0;
		double longitude = 0;

		if (intent != null)
		{
			hotelName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
			latitude = intent.getDoubleExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, 0);
			longitude = intent.getDoubleExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, 0);
		}

		if (hotelName == null || latitude == 0 || longitude == 0)
		{
			finish();
			return;
		}

		setActionBar(hotelName);

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();

		if (mGoogleMap != null)
		{
			mGoogleMap.getUiSettings().setCompassEnabled(false);
			mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
			mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
			mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
			mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
			mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

			mGoogleMap.setMyLocationEnabled(false);

			relocationZoomControl();
			addMarker(mGoogleMap, latitude, longitude, hotelName);
		}
	}

	private void relocationZoomControl()
	{
		View zoomControl = findViewById(0x1);

		if (zoomControl != null && zoomControl.getLayoutParams() instanceof RelativeLayout.LayoutParams)
		{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControl.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

			zoomControl.setPadding(zoomControl.getPaddingLeft(), Util.dpToPx(this, 10), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
			zoomControl.setLayoutParams(params);
		}
	}

	private void addMarker(GoogleMap googleMap, Double lat, Double lng, String hotel_name)
	{
		if (googleMap != null)
		{
			Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));
			marker.showInfoWindow();

			LatLng address = new LatLng(lat, lng);
			CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();

			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			googleMap.setInfoWindowAdapter(new HotelNameInfoWindowAdapter(this));
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
			{
				@Override
				public boolean onMarkerClick(Marker marker)
				{
					marker.showInfoWindow();
					return true;
				}
			});
		}
	}
}
