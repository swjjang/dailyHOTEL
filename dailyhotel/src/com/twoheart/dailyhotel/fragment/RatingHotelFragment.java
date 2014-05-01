package com.twoheart.dailyhotel.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelDetail;

public class RatingHotelFragment extends DialogFragment {
	
	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";
	private HotelDetail mHotelDetail;
	
	public static RatingHotelFragment newInstance(HotelDetail hotelDetail) {
		
		RatingHotelFragment newFragment = new RatingHotelFragment();
		Bundle arguments = new Bundle();
		
		arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		
		newFragment.setArguments(arguments);
		
		return newFragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mHotelDetail = getArguments().getParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		if (getDialog() != null)
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		View view = inflater.inflate(R.layout.fragment_dialog_rating_hotel, parent, false);
		
		return view;
		
	}
	
	

}
