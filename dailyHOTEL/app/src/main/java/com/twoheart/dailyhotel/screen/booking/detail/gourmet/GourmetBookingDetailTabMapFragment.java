package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.adapter.PlaceNameInfoWindowAdapter;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.text.ParseException;

public class GourmetBookingDetailTabMapFragment extends BaseFragment implements OnMapClickListener
{
    private static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";
    private static final String KEY_BUNDLE_ARGUMENTS_ISUSED = "isUsed";

    private boolean mIsUsed;
    private GourmetBookingDetail mGourmetBookingDetail;
    private GoogleMap mGoogleMap;
    private FrameLayout mMapLayout;
    private SupportMapFragment mSupportMapFragment;
    private Marker mMarker;
    private Handler mHandler = new Handler();

    public static GourmetBookingDetailTabMapFragment newInstance(PlaceBookingDetail placeBookingDetail, boolean isUsed)
    {
        GourmetBookingDetailTabMapFragment newFragment = new GourmetBookingDetailTabMapFragment();
        Bundle arguments = new Bundle();

        //관련 정보들은 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL, placeBookingDetail);
        arguments.putBoolean(KEY_BUNDLE_ARGUMENTS_ISUSED, isUsed);
        newFragment.setArguments(arguments);

        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGourmetBookingDetail = getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL);
        mIsUsed = getArguments().getBoolean(KEY_BUNDLE_ARGUMENTS_ISUSED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hotel_tab_map, container, false);

        mMapLayout = (FrameLayout) view.findViewById(R.id.mapLayout);

        TextView placeNameTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_name);
        TextView placeAddressTextView = (TextView) view.findViewById(R.id.tv_hotel_tab_map_address);

        placeNameTextView.setText(mGourmetBookingDetail.placeName);
        placeNameTextView.setSelected(true);
        placeAddressTextView.setText(mGourmetBookingDetail.address);
        placeAddressTextView.setSelected(true);

        TextView placeCateogryTextView = (TextView) view.findViewById(R.id.hv_hotel_grade);

        String displayCategory;
        if (Util.isTextEmpty(mGourmetBookingDetail.subCategory) == false)
        {
            displayCategory = mGourmetBookingDetail.subCategory;
        } else
        {
            displayCategory = mGourmetBookingDetail.category;
        }

        if (Util.isTextEmpty(displayCategory) == true)
        {
            placeCateogryTextView.setVisibility(View.GONE);
        } else
        {
            placeCateogryTextView.setVisibility(View.VISIBLE);
            placeCateogryTextView.setText(displayCategory);
            placeCateogryTextView.setTextColor(getResources().getColor(R.color.black));
            placeCateogryTextView.setBackgroundResource(R.drawable.shape_rect_blackcolor);
        }

        View buttonLayout = view.findViewById(R.id.buttonLayout);
        buttonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();
                if (baseActivity.lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                try
                {
                    //                    String reservationTime = Util.simpleDateFormatISO8601toFormat(gourmetBookingDetail.reservationTime, "yyMMdd");
                    String reservationTime = DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyMMdd");
                    String label = String.format("Gourmet-%s-%s", mGourmetBookingDetail.placeName, reservationTime);

                    Util.showShareMapDialog(baseActivity, mGourmetBookingDetail.placeName//
                        , mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude, false, AnalyticsManager.Category.BOOKING_STATUS//
                        , mIsUsed ? AnalyticsManager.Action.PAST_BOOKING_NAVIGATION_APP_CLICKED : AnalyticsManager.Action.UPCOMING_BOOKING_NAVIGATION_APP_CLICKED//
                        , label);
                } catch (ParseException e)
                {
                    ExLog.d(e.toString());
                }
            }
        });

        View copyAddressView = view.findViewById(R.id.copyAddressView);
        copyAddressView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BaseActivity baseActivity = (BaseActivity) getActivity();

                Util.clipText(baseActivity, mGourmetBookingDetail.address);

                DailyToast.showToast(baseActivity, R.string.message_detail_copy_address, Toast.LENGTH_SHORT);

                try
                {
                    //                    String reservationTime = Util.simpleDateFormatISO8601toFormat(gourmetBookingDetail.reservationTime, "yyMMdd");
                    String reservationTime = DailyCalendar.convertDateFormatString(mGourmetBookingDetail.reservationTime, DailyCalendar.ISO_8601_FORMAT, "yyMMdd");
                    String label = String.format("Gourmet-%s-%s", mGourmetBookingDetail.placeName, reservationTime);

                    AnalyticsManager.getInstance(baseActivity).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , mIsUsed ? AnalyticsManager.Action.PAST_BOOKING_ADDRESS_COPY_CLICKED : AnalyticsManager.Action.UPCOMING_BOOKING_ADDRESS_COPY_CLICKED//
                        , label, null);
                } catch (ParseException e)
                {
                    ExLog.d(e.toString());
                }
            }
        });

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
            , ZoomMapActivity.SourceType.GOURMET_BOOKING, mGourmetBookingDetail.placeName, mGourmetBookingDetail.address//
            , mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude, false);

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

        if (mSupportMapFragment == null)
        {
            mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        }

        View placeholderMapView = mMapLayout.findViewById(R.id.placeholderMapView);

        if (Util.isInstallGooglePlayService(baseActivity) == true)
        {
            placeholderMapView.setVisibility(View.GONE);
            googleMapSetting();
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

    private void googleMapSetting()
    {
        if (mSupportMapFragment == null || mGoogleMap != null)
        {
            return;
        }

        mSupportMapFragment.getMapAsync(new OnMapReadyCallback()
        {
            @Override
            public void onMapReady(GoogleMap googleMap)
            {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapClickListener(GourmetBookingDetailTabMapFragment.this);
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

                addMarker(mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude, mGourmetBookingDetail.placeName);
            }
        });
    }
}
