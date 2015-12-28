package com.twoheart.dailyhotel.screen.bookingdetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.twoheart.dailyhotel.util.Util;

public class GourmetBookingDetailTabMapFragment extends BaseFragment implements OnMapClickListener
{
    protected static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";

    protected PlaceBookingDetail mPlaceBookingDetail;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    protected View mPlaceholderMapView;
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

        mPlaceholderMapView = view.findViewById(R.id.placeholderMapView);

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
                    mGoogleMap.setOnMapClickListener(GourmetBookingDetailTabMapFragment.this);
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
}
