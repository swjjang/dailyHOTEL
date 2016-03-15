/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabMapFragment (지도 탭)
 * <p>
 * 호텔 탭 중 지도 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.common.BaseFragment;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class HotelBookingDetailTabMapFragment extends BaseFragment implements OnMapClickListener
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";

    private HotelBookingDetail mBookingDetail;
    private GoogleMap mGoogleMap;
    private FrameLayout mMapLayout;
    private View mGoogleMapLayout;
    private Marker mMarker;
    private Handler mHandler = new Handler();

    public static HotelBookingDetailTabMapFragment newInstance(PlaceBookingDetail bookingDetail)
    {
        HotelBookingDetailTabMapFragment newFragment = new HotelBookingDetailTabMapFragment();
        Bundle arguments = new Bundle();

        //관련 정보들은 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL, bookingDetail);
        newFragment.setArguments(arguments);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mBookingDetail = (HotelBookingDetail) getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_tab_map, container, false);

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);

        TextView hotelNameTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
        TextView hotelAddressTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);

        hotelNameTextView.setText(mBookingDetail.placeName);
        hotelNameTextView.setSelected(true);
        hotelAddressTextView.setText(mBookingDetail.address);
        hotelAddressTextView.setSelected(true);

        TextView hotelGradeTextView = (TextView) view.findViewById(R.id.hv_hotel_grade);

        hotelGradeTextView.setText(mBookingDetail.grade.getName(getActivity()));
        hotelGradeTextView.setBackgroundResource(mBookingDetail.grade.getColorResId());

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

        Intent intent = ZoomMapActivity.newInstance(baseActivity//
            , ZoomMapActivity.SourceType.BOOKING, mBookingDetail.placeName//
            , mBookingDetail.latitude, mBookingDetail.longitude);

        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        mMapLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Util.installGooglePlayService(baseActivity);
            }
        });

        if (Util.isInstallGooglePlayService(baseActivity) == true)
        {
            try
            {
                googleMapSetting(mMapLayout);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            } catch (Error error)
            {
                ExLog.d(error.toString());
            }
        }
    }

    @Override
    public void onResume()
    {
        if (mMarker != null)
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mMarker.showInfoWindow();
                }
            });
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

            LatLng address = new LatLng(lat, lng);
            CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            mGoogleMap.setInfoWindowAdapter(new PlaceNameInfoWindowAdapter(baseActivity));
            mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker marker)
                {
                    marker.showInfoWindow();
                    return true;
                }
            });

            mMarker.hideInfoWindow();
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mMarker.showInfoWindow();
                }
            });
        }
    }

    private void googleMapSetting(final FrameLayout googleMapLayout)
    {
        if (googleMapLayout == null)
        {
            return;
        }

        googleMapLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                if (baseActivity == null)
                {
                    return;
                }

                SupportMapFragment mapFragment = null;

                try
                {
                    if (mGoogleMapLayout == null)
                    {
                        LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
                    }

                    googleMapLayout.addView(mGoogleMapLayout);

                    mapFragment = (SupportMapFragment) baseActivity.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    googleMapLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onMapClick(new LatLng(mBookingDetail.latitude, mBookingDetail.longitude));
                        }
                    });
                    return;
                } catch (Error e)
                {
                    ExLog.e(e.toString());

                    googleMapLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onMapClick(new LatLng(mBookingDetail.latitude, mBookingDetail.longitude));
                        }
                    });
                    return;
                }

                googleMapLayout.setOnClickListener(null);

                if (mapFragment != null)
                {
                    mapFragment.getMapAsync(new OnMapReadyCallback()
                    {
                        @Override
                        public void onMapReady(GoogleMap googleMap)
                        {
                            mGoogleMap = googleMap;
                            mGoogleMap.setOnMapClickListener(HotelBookingDetailTabMapFragment.this);
                            mGoogleMap.setMyLocationEnabled(false);
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

                            addMarker(mBookingDetail.latitude, mBookingDetail.longitude, mBookingDetail.placeName);
                        }
                    });
                }
            }
        }, 500);
    }
}
