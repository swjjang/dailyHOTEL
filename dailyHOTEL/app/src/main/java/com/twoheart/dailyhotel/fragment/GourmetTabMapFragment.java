/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * TabMapFragment (지도 탭)
 * <p>
 * 호텔 탭 중 지도 탭 프래그먼트
 */
package com.twoheart.dailyhotel.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.Util;

public class GourmetTabMapFragment extends PlaceTabMapFragment implements OnMapClickListener
{
    public static GourmetTabMapFragment newInstance(PlaceBookingDetail placeBookingDetail, String title)
    {
        GourmetTabMapFragment newFragment = new GourmetTabMapFragment();
        Bundle arguments = new Bundle();

        //관련 정보들은 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL, placeBookingDetail);
        newFragment.setArguments(arguments);
        newFragment.setTitle(title);

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
}
