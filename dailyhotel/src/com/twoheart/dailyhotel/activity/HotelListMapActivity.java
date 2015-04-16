package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelListMapActivity extends BaseActivity
{
	private GoogleMap googleMap;
	private ArrayList<Hotel> mHotelArrayList;
	private String mRegion;
	private SaleTime mSaleTime;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zoom_map);

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			mRegion = bundle.getString(NAME_INTENT_EXTRA_DATA_REGION);
			mSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
			mHotelArrayList = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELLIST);
		}

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();

		if (googleMap != null)
		{
			googleMap.setMyLocationEnabled(false);

			if (mHotelArrayList != null)
			{
				for (Hotel hotel : mHotelArrayList)
				{
					addMarker(hotel);
				}
			}
		}
	}

	public void addMarker(Hotel hotel)
	{
		//		if (googleMap != null)
		//		{
		//			googleMap.addMarker(new MarkerOptions().position(new LatLng(hotel.mLatitude, lng)).title(price)).showInfoWindow();
		//
		//			LatLng address = new LatLng(lat, lng);
		//			CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();
		//
		//			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		//			googleMap.setInfoWindowAdapter(new GoogleMapPopupAdapter(this));
		//			googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
		//			{
		//				@Override
		//				public boolean onMarkerClick(Marker marker)
		//				{
		//					marker.showInfoWindow();
		//					return true;
		//				}
		//			});
		//		}
	}
}
