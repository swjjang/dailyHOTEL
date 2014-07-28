package com.twoheart.dailyhotel.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.OnLoadListener;

public class RatingHotelFragment extends DialogFragment implements 
		Constants, OnClickListener, DailyHotelJsonResponseListener, OnLoadListener {

	private static final String KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL = "hotel_detail";
	private static final String RECOMMEND_THIS_HOTEL = "1";
	private static final String NOT_RECOMMEND_THIS_HOTEL = "2";
	
	private MainActivity mHostActivity;
	private RequestQueue mQueue;
	
	private HotelDetail mHotelDetail;
	
	private ImageView ivBtnClose;
	private Button btnRecommend, btnCancel;
	private TextView tvHotelName;

	public static RatingHotelFragment newInstance(HotelDetail hotelDetail) {

		RatingHotelFragment newFragment = new RatingHotelFragment();
		Bundle arguments = new Bundle();

		if (hotelDetail != null)
			arguments.putParcelable(KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL, hotelDetail);
		newFragment.setArguments(arguments);

		return newFragment;

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHostActivity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mQueue = VolleyHttpClient.getRequestQueue();
		mHotelDetail = getArguments().getParcelable(
				KEY_BUNDLE_ARGUMENTS_HOTEL_DETAIL);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		if (getDialog() != null) {
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			getDialog().setCanceledOnTouchOutside(false);
		}

		View view = inflater.inflate(R.layout.fragment_dialog_rating_hotel,parent, false);
		
		tvHotelName = (TextView) view.findViewById(R.id.tv_rating_hotel_name);
		ivBtnClose = (ImageView) view.findViewById(R.id.btn_dialog_rating_hotel_close);
		btnRecommend = (Button) view.findViewById(R.id.btn_rating_hotel_recommend);
		btnCancel = (Button) view.findViewById(R.id.btn_rating_hotel_cancel);
		
		StringBuilder hotelNameWithColon = new StringBuilder("'");
		hotelNameWithColon.append(mHotelDetail.getHotel().getName()).append("'");
		
		tvHotelName.setText(hotelNameWithColon);
		
		tvHotelName.setTypeface(DailyHotel.getBoldTypeface());
		btnRecommend.setTypeface(DailyHotel.getBoldTypeface());
		btnCancel.setTypeface(DailyHotel.getBoldTypeface());
		
		ivBtnClose.setOnClickListener(this);
		btnRecommend.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		GlobalFont.apply((ViewGroup) view);

		return view;

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public void onStart() {
		super.onStart();
		// change dialog width
		if (getDialog() != null) {

			int fullWidth = getDialog().getWindow().getAttributes().width;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				Display display = getActivity().getWindowManager()
						.getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				fullWidth = size.x;
			} else {
				Display display = getActivity().getWindowManager()
						.getDefaultDisplay();
				fullWidth = display.getWidth();
			}

			final int padding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
							.getDisplayMetrics());

			int w = fullWidth - padding;
			int h = getDialog().getWindow().getAttributes().height;

			getDialog().getWindow().setLayout(w, h);
		}

	}
	
	@Override
	public void onDestroyView() {
		destroyRatingHotelFlag();
		super.onDestroyView();
	}
	
	public void destroyRatingHotelFlag() {
		SharedPreferences sharedPreference = mHostActivity.getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		Editor editor = sharedPreference.edit();
		
		editor.putString(KEY_PREFERENCE_HOTEL_NAME, VALUE_PREFERENCE_HOTEL_NAME_DEFAULT);
		editor.putInt(KEY_PREFERENCE_HOTEL_SALE_IDX, VALUE_PREFERENCE_HOTEL_SALE_IDX_DEFAULT);
		editor.putString(KEY_PREFERENCE_HOTEL_CHECKOUT, VALUE_PREFERENCE_HOTEL_CHECKOUT_DEFAULT);
		editor.remove(KEY_PREFERENCE_USER_IDX);
		
		editor.commit();
		
	}

	@Override
	public void onClick(View v) {
		String reviewResult = null;
		
		if (v.getId() == btnRecommend.getId()) reviewResult = RECOMMEND_THIS_HOTEL;
		else if (v.getId() == btnCancel.getId()) reviewResult = NOT_RECOMMEND_THIS_HOTEL;
		else if (v.getId() == ivBtnClose.getId()) dismiss();
		
		if (reviewResult != null) {
			Map<String, String> reviewResultParams = new HashMap<String, String>();
			reviewResultParams.put("rating", reviewResult);
			
			lockUI();
			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER)
							.append(URL_WEBAPI_RESERVE_REVIEW)
							.append(mHotelDetail.getSaleIdx())
							.toString(), reviewResultParams, this, mHostActivity));
			
		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_RESERVE_REVIEW)) {
			try {
				JSONObject obj = response;
				String result = obj.getString("success");

				if (result.equals("true")) unLockUI();
				else unLockUI();


			} catch (Exception e) {
				onError(e);
			} finally {
				showToast(getString(R.string.toast_msg_thanks_to_your_opinion), Toast.LENGTH_LONG, true);
				dismiss();
			}
		}
	}

	@Override
	public void lockUI() {
		mHostActivity.lockUI();
	}

	@Override
	public void unLockUI() {
		mHostActivity.unLockUI();
	}
	
	private void showToast(String message, int length, boolean isAttachToActivity) {
		mHostActivity.showToast(message, length, isAttachToActivity);
	}
	
	private void onError(Exception e) {
		mHostActivity.onError(e);
	}

}
