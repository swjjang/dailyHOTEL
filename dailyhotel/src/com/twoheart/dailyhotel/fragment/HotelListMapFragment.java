package com.twoheart.dailyhotel.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HotelListMapFragment extends com.google.android.gms.maps.SupportMapFragment
{
	private GoogleMap googleMap;
	private ArrayList<Hotel> mHotelArrayList;
	private HotelInfoWindowAdapter mHotelInfoWindowAdapter;
	protected HotelMainFragment.UserActionListener mUserActionListener;
	private SaleTime mSaleTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		googleMap = super.getMap();
		googleMap.setMyLocationEnabled(false);
		
		return view;
	}
	
	public void setUserActionListener(HotelMainFragment.UserActionListener userActionLister)
	{
		mUserActionListener = userActionLister;
	}
	
	public void setHotelList(ArrayList<Hotel> hotelArrayList, SaleTime saleTime)
	{
		mHotelArrayList = hotelArrayList;
		mSaleTime = saleTime;
	}
	
	
	public void makeMarker()
	{
		if (mHotelArrayList == null)
		{
			return;
		}
		
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

			mHotelInfoWindowAdapter = new HotelInfoWindowAdapter(getActivity());

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
							mHotelInfoWindowAdapter.setHotel(hotel, i);
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
					Hotel hotel = mHotelInfoWindowAdapter.getHotel();
					int index = mHotelInfoWindowAdapter.getHotelIndex();
					
					mUserActionListener.selectHotel(hotel, index, mSaleTime);
				}
			});
		}
	}

	private void addMarker(Hotel hotel)
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

//			mIconGenerator = new IconGenerator(HotelListFragment.this);

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
