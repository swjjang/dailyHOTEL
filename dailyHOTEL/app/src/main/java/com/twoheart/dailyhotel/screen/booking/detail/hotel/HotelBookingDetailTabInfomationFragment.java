/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabInfomationFragment (정보 탭)
 * <p>
 * 호텔 탭 중 정보 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.BaseFragment;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class HotelBookingDetailTabInfomationFragment extends BaseFragment
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";

    private HotelBookingDetail mBookingDetail;
    private LinearLayout mLayout;

    private int infoViewCount;

    public static HotelBookingDetailTabInfomationFragment newInstance(PlaceBookingDetail bookingDetail)
    {

        HotelBookingDetailTabInfomationFragment newFragment = new HotelBookingDetailTabInfomationFragment();
        Bundle arguments = new Bundle();

        //호텔의 정보는 BookingTabActivity에서 넘겨받음.
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

        View view = inflater.inflate(R.layout.fragment_hotel_tab_info, container, false);
        mLayout = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);

        infoViewCount = 1;

        if (mBookingDetail != null)
        {
            for (String key : mBookingDetail.getSpecification().keySet())
            {
                addView(view, key, mBookingDetail.getSpecification().get(key));
            }
        }

        return view;
    }

    private void addView(View view, String subject, List<String> contentList)
    {
        View line = new View(view.getContext());
        line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Util.dpToPx(view.getContext(), 1)));
        line.setPadding(0, Util.dpToPx(view.getContext(), 1), 0, 0);
        line.setBackgroundResource(R.color.common_border);
        mLayout.addView(line);

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout_view = (LinearLayout) inflater.inflate(R.layout.list_row_hotel_tab_info, mLayout, false);

        TextView tvInfoNumber = (TextView) layout_view.findViewById(R.id.tv_list_row_hotel_tab_info_number);
        TextView tv_subject = (TextView) layout_view.findViewById(R.id.tv_hotel_tab_info_subject);

        tvInfoNumber.setText(Integer.toString(infoViewCount++));

        tv_subject.setText(subject);
        LinearLayout content_view = (LinearLayout) layout_view.findViewById(R.id.layout_hotel_tab_info_content);
        LinearLayout content_listview = (LinearLayout) layout_view.findViewById(R.id.layout_hotel_tab_info_content_list);

        for (int i = 0; i < contentList.size(); i++)
        {
            LinearLayout rowLayout = (LinearLayout) inflater.inflate(R.layout.list_row_hotel_tab_info_content, layout_view, false);
            TextView tv_content = (TextView) rowLayout.findViewById(R.id.tv_hotel_tab_info_content);
            tv_content.setText(contentList.get(i));
            content_listview.addView(rowLayout);
        }

        mLayout.addView(layout_view);
    }
}
