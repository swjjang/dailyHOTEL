package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

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
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class ZoomMapActivity extends BaseActivity
{
	private GoogleMap googleMap;
	private HotelDetail mHotelDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zoom_map);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			mHotelDetail = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL);
		}

		if (mHotelDetail == null)
		{
			finish();
			return;
		}

		setActionBar(mHotelDetail.getHotel().getName());

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();

		if (googleMap != null)
		{
			googleMap.setMyLocationEnabled(false);
			addMarker(mHotelDetail.getLatitude(), mHotelDetail.getLongitude(), mHotelDetail.getHotel().getName());
		}
	}

	public void addMarker(Double lat, Double lng, String hotel_name)
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
