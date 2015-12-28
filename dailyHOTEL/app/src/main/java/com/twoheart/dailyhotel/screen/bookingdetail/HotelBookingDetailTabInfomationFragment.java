/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabInfomationFragment (정보 탭)
 * <p>
 * 호텔 탭 중 정보 탭 프래그먼트
 */
package com.twoheart.dailyhotel.screen.bookingdetail;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;

import java.util.List;

public class HotelBookingDetailTabInfomationFragment extends BaseFragment
{
    private static final String KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL = "bookingDetail";

    private HotelBookingDetail mBookingDetail;
    private LinearLayout layout;

    private int infoViewCount;

    public static HotelBookingDetailTabInfomationFragment newInstance(PlaceBookingDetail bookingDetail)
    {

        HotelBookingDetailTabInfomationFragment newFragment = new HotelBookingDetailTabInfomationFragment();
        Bundle arguments = new Bundle();

        //호텔의 정보는 BookingTabActivity에서 넘겨받음.
        arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_BOOKING_DETAIL, bookingDetail);
        newFragment.setArguments(arguments);
        //        newFragment.setTitle(title);

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
        layout = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);

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
        line.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getPixels(1, view.getContext())));
        line.setPadding(0, getPixels(1, view.getContext()), 0, 0);
        line.setBackgroundResource(R.color.common_border);
        layout.addView(line);

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout_view = (LinearLayout) inflater.inflate(R.layout.list_row_hotel_tab_info, layout, false);

        TextView tvInfoNumber = (TextView) layout_view.findViewById(R.id.tv_list_row_hotel_tab_info_number);
        TextView tv_subject = (TextView) layout_view.findViewById(R.id.tv_hotel_tab_info_subject);

        tvInfoNumber.setText(Integer.toString(infoViewCount++));

        //영어 버전
        //		String locale = mHostActivity.sharedPreference.getString(KEY_PREFERENCE_LOCALE, null);
        //		if (locale.equals("English")) {
        //			if (subject.equals("데일리의 추천 이유") || subject.equals("데일리의 추천이유")) tv_subject.setText("daily's Recommend Reason");
        //			else if (subject.equals("호텔 정보") || subject.equals("호텔정보")) tv_subject.setText("Hotel Info");
        //			else if (subject.equals("교통정보") || subject.equals("교통 정보")) tv_subject.setText("Traffic Info");
        //			else if (subject.equals("확인사항")) tv_subject.setText("Confirmation Items");
        //		} else tv_subject.setText(subject);
        tv_subject.setText(subject);
        LinearLayout content_view = (LinearLayout) layout_view.findViewById(R.id.layout_hotel_tab_info_content);
        LinearLayout content_listview = (LinearLayout) layout_view.findViewById(R.id.layout_hotel_tab_info_content_list);

        for (int i = 0; i < contentList.size(); i++)
        {
            LinearLayout rowLayout = (LinearLayout) inflater.inflate(R.layout.list_row_hotel_tab_info_content, layout_view, false);
            TextView tv_content = (TextView) rowLayout.findViewById(R.id.tv_hotel_tab_info_content);
            tv_content.setText(contentList.get(i));
            content_listview.addView(rowLayout);

            // layout_view.addView(rowLayout);
        }

        // content_view.addView(content_listview);
        // layout_view.addView(content_view);
        layout.addView(layout_view);

    }

    public int getPixels(int dipValue, Context context)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

}
