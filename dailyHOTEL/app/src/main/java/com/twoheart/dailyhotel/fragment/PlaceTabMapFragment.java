/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabMapFragment (지도 탭)
 * <p>
 * 호텔 탭 중 지도 탭 프래그먼트
 */
package com.twoheart.dailyhotel.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.Util;

public abstract class PlaceTabMapFragment extends BaseFragment implements OnMapClickListener
{
    protected static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";

    protected PlaceBookingDetail mPlaceBookingDetail;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    protected View mPlaceholderMapView;
    private Marker mMarker;

    @Override
    public void onMapClick(LatLng latLng)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null || mGoogleMap == null)
        {
            return;
        }

        Intent intent = new Intent(baseActivity, ZoomMapActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, mPlaceBookingDetail.placeName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mPlaceBookingDetail.latitude);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mPlaceBookingDetail.longitude);

        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);

        if (Util.isGooglePlayServicesAvailable(getActivity()) == true)
        {
            mPlaceholderMapView.setVisibility(View.GONE);

            mMapFragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap googleMap)
                {
                    mGoogleMap = googleMap;
                    mGoogleMap.setOnMapClickListener(PlaceTabMapFragment.this);
                    mGoogleMap.setMyLocationEnabled(false);
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

                    addMarker(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.placeName);
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

            getChildFragmentManager().beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
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
