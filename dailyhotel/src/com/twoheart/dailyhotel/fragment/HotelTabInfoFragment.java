package com.twoheart.dailyhotel.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.HotelTabActivity;
import com.twoheart.dailyhotel.obj.HotelDetail;

public class HotelTabInfoFragment extends Fragment {

	private static final String TAG = "HotelTabInfoFragment";

	private HotelTabActivity mHostActivity;
	private HotelDetail mHotelDetail;
	private LinearLayout layout;

	private int infoViewCount = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_hotel_tab_info, null);
		mHostActivity = (HotelTabActivity) getActivity();
		mHotelDetail = mHostActivity.hotelDetail;

		layout = (LinearLayout) view.findViewById(R.id.layout_hotel_tab_info);

		for (String key : mHotelDetail.getSpecification().keySet()) {
			addView(view, key, mHotelDetail.getSpecification()
					.get(key));
		}

		return view;
	}

	private void addView(View view, String subject, List<String> contentList) {

		LayoutInflater inflater = (LayoutInflater) view.getContext()
				.getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
		LinearLayout layout_view = (LinearLayout) inflater.inflate(
				R.layout.list_row_hotel_tab_info, layout, false);

		TextView tvInfoNumber = (TextView) layout_view
				.findViewById(R.id.tv_list_row_hotel_tab_info_number);
		TextView tv_subject = (TextView) layout_view
				.findViewById(R.id.tv_hotel_tab_info_subject);

		tvInfoNumber.setText(Integer.toString(infoViewCount++));
		tv_subject.setText(subject);

		LinearLayout content_view = (LinearLayout) layout_view
				.findViewById(R.id.layout_hotel_tab_info_content);
		LinearLayout content_listview = (LinearLayout) layout_view
				.findViewById(R.id.layout_hotel_tab_info_content_list);

		for (int i = 0; i < contentList.size(); i++) {
			LinearLayout rowLayout = (LinearLayout) inflater.inflate(
					R.layout.list_row_hotel_tab_info_content, layout_view,
					false);
			TextView tv_content = (TextView) rowLayout
					.findViewById(R.id.tv_hotel_tab_info_content);
			tv_content.setText(contentList.get(i));
			content_listview.addView(rowLayout);

			// layout_view.addView(rowLayout);
		}

		// content_view.addView(content_listview);
		// layout_view.addView(content_view);
		layout.addView(layout_view);

		View line = new View(view.getContext());
		// line.setLayoutParams(new
		// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		// DhManager.getPixels(1, view.getContext())));
		// line.setPadding(0, DhManager.getPixels(1, view.getContext()), 0, 0);
		line.setBackgroundResource(R.color.dh_gray50);
		layout.addView(line);

	}

}
