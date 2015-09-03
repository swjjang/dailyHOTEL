/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * 
 * 호텔 만족도 조사를 위한 화면
 * 
 */
package com.twoheart.dailyhotel.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.view.OnLoadListener;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

public class RatingHotelFragment extends
		DialogFragment implements Constants, OnClickListener, OnLoadListener
{
	private static final String NOT_RATE_THIS_HOTEL = "0";
	private static final String RECOMMEND_THIS_HOTEL = "1";
	private static final String NOT_RECOMMEND_THIS_HOTEL = "2";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private String mHotelName;
	private int mReservationIndex;
	private Long mCheckInDate;
	private Long mCheckOutDate;

	private Button btnRecommend, btnCancel;

	public static RatingHotelFragment newInstance(String hotelName, int reservationIndex, long checkInDate, long checkOutDate)
	{
		RatingHotelFragment newFragment = new RatingHotelFragment();
		Bundle arguments = new Bundle();

		if (hotelName != null)
		{
			arguments.putString(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotelName);
			arguments.putInt(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, reservationIndex);
			arguments.putLong(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInDate);
			arguments.putLong(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, checkOutDate);
		}

		newFragment.setArguments(arguments);

		return newFragment;

	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mHostActivity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mQueue = VolleyHttpClient.getRequestQueue();

		mHotelName = getArguments().getString(NAME_INTENT_EXTRA_DATA_HOTELNAME);
		mReservationIndex = getArguments().getInt(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX);
		mCheckInDate = getArguments().getLong(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
		mCheckOutDate = getArguments().getLong(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		if (getDialog() != null)
		{
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			getDialog().setCanceledOnTouchOutside(false);
		}

		View view = inflater.inflate(R.layout.fragment_dialog_rating_hotel, parent, false);

		TextView ratingPeriod = (TextView) view.findViewById(R.id.periodTextView);
		TextView ratingHotelName = (TextView) view.findViewById(R.id.hotelNameTextView);
		TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);

		ratingPeriod.setTypeface(FontManager.getInstance(mHostActivity).getMediumTypeface());
		messageTextView.setTypeface(FontManager.getInstance(mHostActivity).getMediumTypeface());

		btnRecommend = (Button) view.findViewById(R.id.positiveTextView);
		btnCancel = (Button) view.findViewById(R.id.negativeTextView);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.KOREA);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		String periodDate = String.format("%s - %s", simpleDateFormat.format(new Date(mCheckInDate)), simpleDateFormat.format(new Date(mCheckOutDate)));

		ratingPeriod.setText(getString(R.string.frag_rating_hotel_text1, periodDate));
		ratingHotelName.setText(getString(R.string.frag_rating_hotel_text2, mHotelName));

		btnRecommend.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle)
	{
		//		super.onSaveInstanceState(bundle);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public void onStart()
	{
		super.onStart();
		// change dialog width
		if (getDialog() != null)
		{

			int fullWidth = getDialog().getWindow().getAttributes().width;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
			{
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				fullWidth = size.x;
			} else
			{
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				fullWidth = display.getWidth();
			}

			final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());

			int w = fullWidth - padding;
			int h = getDialog().getWindow().getAttributes().height;

			getDialog().getWindow().setLayout(w, h);
		}
	}

	@Override
	public void onClick(View v)
	{
		String reviewResult = null;

		if (v.getId() == btnRecommend.getId())
		{
			reviewResult = RECOMMEND_THIS_HOTEL;
		} else if (v.getId() == btnCancel.getId())
		{
			reviewResult = NOT_RECOMMEND_THIS_HOTEL;
		}

		if (reviewResult != null)
		{
			lockUI();

			Map<String, String> params = new HashMap<String, String>();
			params.put("rating", reviewResult);
			params.put("reserv_idx", String.valueOf(mReservationIndex));

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SATISFACTION_RATING_UPDATE).toString(), params, mReserveReviewJsonResponseListener, mHostActivity));
		}
	}

	@Override
	public void onCancel(DialogInterface dialog)
	{
		lockUI();

		Map<String, String> params = new HashMap<String, String>();
		params.put("rating", NOT_RATE_THIS_HOTEL);
		params.put("reserv_idx", String.valueOf(mReservationIndex));

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SATISFACTION_RATING_UPDATE).toString(), params, mReserveReviewJsonResponseListener, mHostActivity));

		super.onCancel(dialog);
	}

	@Override
	public void lockUI()
	{
		if (mHostActivity != null)
		{
			mHostActivity.lockUI();
		}
	}

	@Override
	public void unLockUI()
	{
		if (mHostActivity != null)
		{
			mHostActivity.unLockUI();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mReserveReviewJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			unLockUI();
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				JSONObject jsonObject = response.getJSONObject("data");

				boolean result = jsonObject.getBoolean("is_success");
				int msgCode = response.getInt("msg_code");

				if (response.has("msg") == true)
				{
					String msg = response.getString("msg");

					switch (msgCode)
					{
						case 100:
							DailyToast.showToast(mHostActivity, msg, Toast.LENGTH_LONG);
							break;

						case 200:
							if (mHostActivity.isFinishing() == true)
							{
								return;
							}

							mHostActivity.showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
							break;
					}
				}
			} catch (Exception e)
			{
				ExLog.e(e.toString());
			} finally
			{
				dismissAllowingStateLoss();
			}
		}
	};
}
