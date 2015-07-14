/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * TabInfoFragment (정보 탭)
 * 
 * 호텔 탭 중 정보 탭 프래그먼트
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.util.List;

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

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.util.ui.BaseFragment;

public class TabInfoFragment extends BaseFragment
{
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";

	private BookingHotelDetail mHotelDetail;
	private LinearLayout layout;

	private int infoViewCount;

	public static TabInfoFragment newInstance(BookingHotelDetail hotelDetail, String title)
	{

		TabInfoFragment newFragment = new TabInfoFragment();
		Bundle arguments = new Bundle();

		//호텔의 정보는 HotelTabActivity, BookingTabActivity에서 넘겨받음. 
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);
		newFragment.setTitle(title);

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

		View view = inflater.inflate(R.layout.fragment_hotel_tab_info, container, false);
		layout = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);

		infoViewCount = 1;

		if (mHotelDetail != null)
		{
			for (String key : mHotelDetail.getSpecification().keySet())
			{
				addView(view, key, mHotelDetail.getSpecification().get(key));
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

		tv_subject.setTypeface(DailyHotel.getBoldTypeface());

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
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
		return px;
	}

}
