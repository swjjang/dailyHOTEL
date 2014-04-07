package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.ZoomMapActivity;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.TabActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class TabMapFragment extends BaseFragment implements OnMapClickListener {

	private static final String TAG = "HotelTabMapFragment";

	private TabActivity mHostActivity;
	private HotelDetail mHotelDetail;

	private SupportMapFragment mMapFragment;
	private GoogleMap googleMap;
	private TextView tvName, tvAddress;
	private FrameLayout gradeBackground;
	private TextView gradeText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mHostActivity = (TabActivity) getActivity();
		mHotelDetail = mHostActivity.hotelDetail;

		View view = inflater.inflate(R.layout.fragment_hotel_tab_map,
				container, false);

		tvName = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
		tvAddress = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);

		tvName.setText(mHostActivity.hotelDetail.getHotel().getName());
		tvName.setSelected(true);
		tvAddress.setText(mHostActivity.hotelDetail.getHotel().getAddress());
		tvAddress.setSelected(true);

		gradeBackground = (FrameLayout) view
				.findViewById(R.id.fl_hotel_row_grade);
		gradeText = (TextView) view.findViewById(R.id.tv_hotel_row_grade);

		String category = mHotelDetail.getHotel().getCategory();

		if (category.equals("biz") | category.equals("hostel") | category.equals("grade1") | 
				category.equals("grade2") | category.equals("grade3")) {
			gradeBackground.setBackgroundColor(Color.parseColor("#055870"));
			gradeText.setText("비지니스");

		} else if (category.equals("boutique")) {
			gradeBackground.setBackgroundColor(Color.parseColor("#9f2d58"));
			gradeText.setText("부띠끄");

		} else if (category.equals("residence")) {
			gradeBackground.setBackgroundColor(Color.parseColor("#407f67"));
			gradeText.setText("레지던스");

		} else if (category.equals("resort") | category.equals("pension") | category.equals("condo")) {
			gradeBackground.setBackgroundColor(Color.parseColor("#cf8d14"));
			gradeText.setText("리조트");

		} else if (category.equals("special")) {
			gradeBackground.setBackgroundColor(Color.parseColor("#ab380a"));
			gradeText.setText("특급");

		} else {
			gradeBackground.setBackgroundColor(Color.parseColor("#808080"));
			gradeText.setText("미정");
		}
		
		return view;
	}

	@Override
	public void onMapClick(LatLng latLng) {
		Intent i = new Intent(mHostActivity, ZoomMapActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, mHotelDetail);
		startActivity(i);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMapFragment = (SupportMapFragment) mHostActivity
				.getSupportFragmentManager().findFragmentById(R.id.frag_map);
		googleMap = mMapFragment.getMap();
		
		if (googleMap != null) {
			googleMap.setOnMapClickListener(this);
			googleMap.setMyLocationEnabled(false);	
		}

		addMarker(mHotelDetail.getLatitude(), mHotelDetail.getLongitude(),
				mHotelDetail.getHotel().getName());

	}

	@Override
	public void onDestroyView() {
		if (!mHostActivity.isFinishing())
			mHostActivity.getSupportFragmentManager().beginTransaction()
					.remove(mMapFragment).commitAllowingStateLoss();
		super.onDestroyView();
	}

	// 마커 추가
	public void addMarker(Double lat, Double lng, String hotel_name) {
		if (googleMap != null) {
			googleMap.addMarker(new MarkerOptions().position(
					new LatLng(lat, lng)).title(hotel_name));
			LatLng address = new LatLng(lat, lng);
			CameraPosition cp = new CameraPosition.Builder().target((address))
					.zoom(15).build();
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		}
	}
}
