/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabMapFragment (지도 탭)
 * <p>
 * 호텔 탭 중 지도 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.bookingdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.ZoomMapActivity;
import com.twoheart.dailyhotel.adapter.HotelNameInfoWindowAdapter;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.util.Util;

public class HotelBookingDetailTabMapFragment extends BaseFragment implements OnMapClickListener
{
    private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

    private BookingHotelDetail mHotelDetail;
    private GoogleMap mGoogleMap;
    private View mPlaceholderMapView;
    private Marker mMarker;

    public static HotelBookingDetailTabMapFragment newInstance(BookingHotelDetail hotelDetail)
    {
        HotelBookingDetailTabMapFragment newFragment = new HotelBookingDetailTabMapFragment();
        Bundle arguments = new Bundle();

        //관련 정보들은 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
        newFragment.setArguments(arguments);
        //        newFragment.setTitle(title);

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

        mPlaceholderMapView = view.findViewById(R.id.placeholderMapView);

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);

        if (Util.isGooglePlayServicesAvailable(getActivity()) == true)
        {
            mPlaceholderMapView.setVisibility(View.GONE);

            mapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    mGoogleMap = googleMap;
                    mGoogleMap.setOnMapClickListener(HotelBookingDetailTabMapFragment.this);
                    mGoogleMap.setMyLocationEnabled(false);
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

                    addMarker(mHotelDetail.getLatitude(), mHotelDetail.getLongitude(), mHotelDetail.getHotel().getName());
                }
            });
        } else
        {
            mPlaceholderMapView.setVisibility(View.VISIBLE);
            mPlaceholderMapView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Util.installGooglePlayService((BaseActivity) getActivity());
                }
            });

            getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
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

    // 마커 추가
    public void addMarker(double lat, double lng, String hotel_name)
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
