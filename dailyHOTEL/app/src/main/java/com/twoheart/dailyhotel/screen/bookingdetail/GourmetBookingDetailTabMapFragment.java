package com.twoheart.dailyhotel.screen.bookingdetail;

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
import com.twoheart.dailyhotel.adapter.NameInfoWindowAdapter;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

public class GourmetBookingDetailTabMapFragment extends BaseFragment implements OnMapClickListener
{
    protected static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";

    protected PlaceBookingDetail mPlaceBookingDetail;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private FrameLayout mMapLayout;
    private View mGoogleMapLayout;
    private Marker mMarker;
    private Handler mHandler = new Handler();

    public static GourmetBookingDetailTabMapFragment newInstance(PlaceBookingDetail placeBookingDetail)
    {
        GourmetBookingDetailTabMapFragment newFragment = new GourmetBookingDetailTabMapFragment();
        Bundle arguments = new Bundle();

        //관련 정보들은 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL, placeBookingDetail);
        newFragment.setArguments(arguments);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPlaceBookingDetail = getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_tab_map, container, false);

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);

        GourmetBookingDetail gourmetBookingDetail = (GourmetBookingDetail) mPlaceBookingDetail;

        TextView hotelNameTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
        TextView hotelAddressTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);

        hotelNameTextView.setText(gourmetBookingDetail.placeName);
        hotelNameTextView.setSelected(true);
        hotelAddressTextView.setText(gourmetBookingDetail.address);
        hotelAddressTextView.setSelected(true);

        TextView hotelGradeTextView = (TextView) view.findViewById(R.id.hv_hotel_grade);

        if (Util.isTextEmpty(gourmetBookingDetail.category) == true)
        {
            hotelGradeTextView.setVisibility(View.GONE);
        } else
        {
            hotelGradeTextView.setVisibility(View.VISIBLE);
            hotelGradeTextView.setText(gourmetBookingDetail.category);
            hotelGradeTextView.setTextColor(getResources().getColor(R.color.black));
            hotelGradeTextView.setBackgroundResource(R.drawable.shape_rect_blackcolor);
        }

        return view;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        Intent intent = ZoomMapActivity.newInstance(baseActivity//
            , ZoomMapActivity.SourceType.BOOKING, mPlaceBookingDetail.placeName//
            , mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude);

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
            mGoogleMap.setInfoWindowAdapter(new NameInfoWindowAdapter(baseActivity));
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
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

                try
                {
                    if (mGoogleMapLayout == null)
                    {
                        LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        mGoogleMapLayout = (ViewGroup) inflater.inflate(R.layout.view_map, null, false);
                    }

                    googleMapLayout.addView(mGoogleMapLayout);

                    mMapFragment = (SupportMapFragment) baseActivity.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());

                    googleMapLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onMapClick(new LatLng(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude));
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
                            onMapClick(new LatLng(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude));
                        }
                    });
                    return;
                }

                googleMapLayout.setOnClickListener(null);

                mMapFragment.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        mGoogleMap = googleMap;
                        mGoogleMap.setOnMapClickListener(GourmetBookingDetailTabMapFragment.this);
                        mGoogleMap.setMyLocationEnabled(false);
                        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

                        addMarker(mPlaceBookingDetail.latitude, mPlaceBookingDetail.longitude, mPlaceBookingDetail.placeName);
                    }
                });
            }
        }, 500);
    }
}
