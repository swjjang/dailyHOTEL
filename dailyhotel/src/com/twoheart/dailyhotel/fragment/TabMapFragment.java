/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * TabMapFragment (지도 탭)
 * 
 * 호텔 탭 중 지도 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.ZoomMapActivity;
import com.twoheart.dailyhotel.adapter.HotelNameInfoWindowAdapter;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class TabMapFragment extends BaseFragment implements OnMapClickListener
{
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

	private BookingHotelDetail mHotelDetail;
	private SupportMapFragment mMapFragment;
	private GoogleMap mGoogleMap;
	private Marker mMarker;

	public static TabMapFragment newInstance(BookingHotelDetail hotelDetail, String title)
	{
		TabMapFragment newFragment = new TabMapFragment();
		Bundle arguments = new Bundle();

		//관련 정보들은 BookingTabActivity에서 넘겨받음. 
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);
		newFragment.setTitle(title);

		return newFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mHotelDetail = (BookingHotelDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_hotel_tab_map, container, false);

		TextView hotelNameTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
		TextView hotelAddressTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);

		hotelNameTextView.setText(mHotelDetail.getHotel().getName());
		hotelNameTextView.setSelected(true);
		hotelAddressTextView.setText(mHotelDetail.getHotel().getAddress());
		hotelAddressTextView.setSelected(true);

		TextView hotelGradeTextView = (TextView) view.findViewById(R.id.hv_hotel_grade);

		hotelGradeTextView.setText(mHotelDetail.getHotel().getCategory().getName(getActivity()));
		hotelGradeTextView.setBackgroundResource(mHotelDetail.getHotel().getCategory().getColorResId());

		return view;
	}

	@Override
	public void onMapClick(LatLng latLng)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null || mGoogleMap == null)
		{
			return;
		}

		Intent intent = new Intent(baseActivity, ZoomMapActivity.class);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, mHotelDetail.getHotel().getName());
		intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mHotelDetail.getLatitude());
		intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mHotelDetail.getLongitude());

		startActivity(intent);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);

		View viewGroup = mMapFragment.getView();

		if (viewGroup instanceof ViewGroup)
		{
			View viewLayout = ((ViewGroup) viewGroup).getChildAt(0);

			if (viewLayout instanceof ViewGroup)
			{
				View viewButton = ((ViewGroup) viewLayout).getChildAt(1);

				if (viewButton instanceof Button)
				{
					viewButton.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							BaseActivity baseActivity = (BaseActivity) getActivity();

							if (baseActivity == null || baseActivity.isFinishing() == true)
							{
								return;
							}

							Util.installGooglePlayService(baseActivity);
						}
					});
				}
			}
		}

		mMapFragment.getMapAsync(new OnMapReadyCallback()
		{
			@Override
			public void onMapReady(GoogleMap googleMap)
			{
				mGoogleMap = googleMap;
				mGoogleMap.setOnMapClickListener(TabMapFragment.this);
				mGoogleMap.setMyLocationEnabled(false);
				mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

				addMarker(mHotelDetail.getLatitude(), mHotelDetail.getLongitude(), mHotelDetail.getHotel().getName());
			}
		});
	}

	@Override
	public void onResume()
	{
		if (mMarker != null)
		{
			mMarker.showInfoWindow();
		}

		super.onResume();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onDestroyView()
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		try
		{
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			{
				if (!baseActivity.isFinishing())
					baseActivity.getSupportFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
			} else
			{
				if (!baseActivity.isDestroyed())
					baseActivity.getSupportFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
			}

		} catch (IllegalStateException e)
		{
			onError(e);
		}

		super.onDestroyView();
	}

	// 마커 추가
	public void addMarker(Double lat, Double lng, String hotel_name)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (mGoogleMap != null)
		{
			mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			mMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));
			mMarker.showInfoWindow();

			LatLng address = new LatLng(lat, lng);
			CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();
			mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			mGoogleMap.setInfoWindowAdapter(new HotelNameInfoWindowAdapter(baseActivity));
			mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
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
