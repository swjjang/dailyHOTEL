package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelInfoWindowAdapter;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class HotelListMapActivity extends BaseActivity
{
	private GoogleMap googleMap;
	private ArrayList<Hotel> mHotelArrayList;
	private String mRegion;
	private SaleTime mSaleTime;
	private HotelInfoWindowAdapter mHotelInfoWindowAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zoom_map);

		Intent intent = getIntent();

		if (intent != null)
		{
			mRegion = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_REGION);
			mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
			mHotelArrayList = intent.getParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_HOTELLIST);
		}

		String title = String.format("%s(%s)", mRegion, mSaleTime.getCurrentDate());
		setActionBar(title);

		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();

		if (googleMap != null)
		{
			googleMap.setMyLocationEnabled(false);

			if (mHotelArrayList != null)
			{
				int size = mHotelArrayList.size();
				double latitude = 0.0;
				double longitude = 0.0;
				double i = 0.0;

				for (Hotel hotel : mHotelArrayList)
				{
					if (hotel.mLatitude == 0.0)
					{
						hotel.mLatitude = 36.240562 + i / 1000;
					}

					if (hotel.mLongitude == 0.0)
					{
						hotel.mLongitude = 127.867222 + i / 1000;
					}

					i++;

					addMarker(hotel);

					latitude += hotel.mLatitude;
					longitude += hotel.mLongitude;
				}

				latitude /= size;
				longitude /= size;

				LatLng address = new LatLng(latitude, longitude);
				CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();

				googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

				mHotelInfoWindowAdapter = new HotelInfoWindowAdapter(this);

				googleMap.setInfoWindowAdapter(mHotelInfoWindowAdapter);
				googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
				{
					@Override
					public boolean onMarkerClick(Marker marker)
					{
						LatLng latlng = marker.getPosition();

						int size = mHotelArrayList.size();

						for (int i = 0; i < size; i++)
						{
							Hotel hotel = mHotelArrayList.get(i);

							if (latlng.latitude == hotel.mLatitude && latlng.longitude == hotel.mLongitude)
							{
								//								mHotelInfoWindowAdapter.setHotel(hotel, i);
								marker.showInfoWindow();
								break;
							}
						}

						return false;
					}
				});

				googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
				{
					@Override
					public void onInfoWindowClick(Marker arg0)
					{
						if (isLockUiComponent() == true)
						{
							return;
						}

						lockUiComponent();
						//
						//						Hotel hotel = mHotelInfoWindowAdapter.getHotel();
						//						int index = mHotelInfoWindowAdapter.getHotelIndex();
						//
						//						Intent intent = new Intent(HotelListMapActivity.this, HotelTabActivity.class);
						//
						//						String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "");
						//
						//						SharedPreferences.Editor editor = sharedPreference.edit();
						//						editor.putString(KEY_PREFERENCE_REGION_SELECT_GA, region);
						//						editor.putString(KEY_PREFERENCE_HOTEL_NAME_GA, hotel.getName());
						//						editor.commit();
						//
						//						intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTEL, hotel);
						//
						//						intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);
						//
						//						intent.putExtra(NAME_INTENT_EXTRA_DATA_REGION, region);
						//						intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, index);
						//
						//						startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTELTAB);

						releaseUiComponent();
					}
				});
			}
		}
	}

	public void addMarker(Hotel hotel)
	{
		if (googleMap != null)
		{
			HotelPriceRenderer hotelPriceRenderer = new HotelPriceRenderer(hotel);

			googleMap.addMarker(new MarkerOptions().position(new LatLng(hotel.mLatitude, hotel.mLongitude)).title(hotel.getDiscount()).icon(hotelPriceRenderer.getBitmap()));
		}
	}

	private class HotelPriceRenderer
	{
		private String mPrice;
		private IconGenerator mIconGenerator;

		public HotelPriceRenderer(Hotel hotel)
		{
			int originalPrice = Integer.parseInt(hotel.getDiscount().replaceAll(",", ""));
			DecimalFormat comma = new DecimalFormat("###,##0");

			mPrice = "₩" + comma.format(originalPrice);

			mIconGenerator = new IconGenerator(HotelListMapActivity.this);

			mIconGenerator.setTextColor(getResources().getColor(R.color.white));
			mIconGenerator.setColor(getResources().getColor(hotel.getCategory().getColorResId()));
		}

		public BitmapDescriptor getBitmap()
		{
			Bitmap icon = mIconGenerator.makeIcon(mPrice);

			return BitmapDescriptorFactory.fromBitmap(icon);
		}
	}
}
