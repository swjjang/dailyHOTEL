/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabInfomationFragment (정보 탭)
 * <p>
 * 호텔 탭 중 정보 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

public class GourmetBookingDetailTabInfomationFragment extends BaseFragment
{
    private static final String KEY_BUNDLE_ARGUMENTS_PLACEBOOKINGDETAIL = "placeBookingDetail";

    private PlaceBookingDetail mPlaceBookingDetail;

    public static GourmetBookingDetailTabInfomationFragment newInstance(PlaceBookingDetail placeBookingDetail)
    {
        GourmetBookingDetailTabInfomationFragment newFragment = new GourmetBookingDetailTabInfomationFragment();
        Bundle arguments = new Bundle();

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
        View view = inflater.inflate(R.layout.fragment_hotel_tab_info, container, false);

        ScrollView scrollLayout = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollLayout, getResources().getColor(R.color.default_over_scroll_edge));

        ViewGroup viewGroup = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);

        if (mPlaceBookingDetail != null)
        {
            for (String key : mPlaceBookingDetail.getSpecification().keySet())
            {
                addView(inflater, viewGroup, key, mPlaceBookingDetail.getSpecification().get(key));
            }
        }

        return view;
    }

    private void addView(LayoutInflater inflater, ViewGroup viewGroup, String subject, List<String> contentList)
    {
        View sectionView = inflater.inflate(R.layout.list_row_booking_information_section, viewGroup, false);
        TextView sectionTitleView = (TextView) sectionView.findViewById(R.id.sectionTextView);
        sectionTitleView.setText(subject);

        viewGroup.addView(sectionView);

        addTextView(inflater, viewGroup, contentList);
    }

    private void addTextView(LayoutInflater inflater, ViewGroup viewGroups, List<String> contentList)
    {
        if (viewGroups == null || contentList == null || contentList.size() == 0)
        {
            return;
        }

        int size = contentList.size();

        for (int i = 0; i < size; i++)
        {
            View textLayout = inflater.inflate(R.layout.list_row_detail_text, viewGroups, false);
            TextView textView = (TextView) textLayout.findViewById(R.id.textView);
            textView.setText(contentList.get(i));

            if (i == size - 1)
            {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = Util.dpToPx(getContext(), 12);
                textLayout.setLayoutParams(layoutParams);
            }

            viewGroups.addView(textLayout);
        }
    }
}
