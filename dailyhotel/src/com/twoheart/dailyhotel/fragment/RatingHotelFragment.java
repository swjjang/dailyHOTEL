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
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class RatingHotelFragment
		extends DialogFragment implements Constants, OnLoadListener
{
	private static final String NOT_RATE_THIS_HOTEL = "0";
	private static final String RECOMMEND_THIS_HOTEL = "1";
	private static final String NOT_RECOMMEND_THIS_HOTEL = "2";

	private MainActivity mHostActivity;
	private RequestQueue mQueue;

	private String mTicketName;
	private int mReservationIndex;
	private Long mCheckInDate;
	private Long mCheckOutDate;
	private PlaceMainFragment.TYPE mPlaceType;

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
			arguments.putString(NAME_INTENT_EXTRA_DATA_TYPE, PlaceMainFragment.TYPE.HOTEL.name());
		}

		newFragment.setArguments(arguments);

		return newFragment;
	}

	public static RatingHotelFragment newInstance(String placeName, int reservationIndex, long checkInDate)
	{
		RatingHotelFragment newFragment = new RatingHotelFragment();
		Bundle arguments = new Bundle();

		if (placeName != null)
		{
			arguments.putString(NAME_INTENT_EXTRA_DATA_PLACENAME, placeName);
			arguments.putInt(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, reservationIndex);
			arguments.putLong(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInDate);
			arguments.putString(NAME_INTENT_EXTRA_DATA_TYPE, PlaceMainFragment.TYPE.FNB.name());
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

		mPlaceType = PlaceMainFragment.TYPE.valueOf(getArguments().getString(NAME_INTENT_EXTRA_DATA_TYPE));

		switch (mPlaceType)
		{
			case HOTEL:
			{
				mTicketName = getArguments().getString(NAME_INTENT_EXTRA_DATA_HOTELNAME);
				mReservationIndex = getArguments().getInt(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX);
				mCheckInDate = getArguments().getLong(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
				mCheckOutDate = getArguments().getLong(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE);
				break;
			}

			case FNB:
			{
				mTicketName = getArguments().getString(NAME_INTENT_EXTRA_DATA_PLACENAME);
				mReservationIndex = getArguments().getInt(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX);
				mCheckInDate = getArguments().getLong(NAME_INTENT_EXTRA_DATA_CHECKINDATE);
				break;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		Dialog dialog = getDialog();

		if (dialog != null)
		{
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			dialog.setCanceledOnTouchOutside(false);
		}

		View view = inflater.inflate(R.layout.fragment_dialog_rating_hotel, parent, false);

		TextView ratingPeriod = (TextView) view.findViewById(R.id.periodTextView);
		TextView ratingHotelName = (TextView) view.findViewById(R.id.hotelNameTextView);
		TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);

		ratingPeriod.setTypeface(FontManager.getInstance(mHostActivity).getMediumTypeface());
		messageTextView.setTypeface(FontManager.getInstance(mHostActivity).getMediumTypeface());

		TextView positiveTextView = (TextView) view.findViewById(R.id.positiveTextView);
		TextView negativeTextView = (TextView) view.findViewById(R.id.negativeTextView);

		switch (mPlaceType)
		{
			case HOTEL:
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.KOREA);
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

				String periodDate = String.format("%s - %s", simpleDateFormat.format(new Date(mCheckInDate)), simpleDateFormat.format(new Date(mCheckOutDate)));
				ratingPeriod.setText(getString(R.string.frag_rating_hotel_text1, periodDate));
				break;
			}

			case FNB:
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE)", Locale.KOREA);
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

				String periodDate = simpleDateFormat.format(new Date(mCheckInDate));
				ratingPeriod.setText(getString(R.string.frag_rating_hotel_text1, periodDate));
				break;
			}
		}

		ratingHotelName.setText(getString(R.string.frag_rating_hotel_text2, mTicketName));

		positiveTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				updateSatifactionRating(mPlaceType, mReservationIndex, RECOMMEND_THIS_HOTEL);
			}
		});

		negativeTextView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RECOMMEND_THIS_HOTEL);
			}
		});

		return view;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
	public void onStart()
	{
		super.onStart();

		Dialog dialog = getDialog();

		if (dialog != null)
		{
			int fullWidth = dialog.getWindow().getAttributes().width;

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
			int h = dialog.getWindow().getAttributes().height;

			dialog.getWindow().setLayout(w, h);
		}
	}

	private void updateSatifactionRating(PlaceMainFragment.TYPE type, int index, String result)
	{
		if (result == null)
		{
			return;
		}

		lockUI();

		Map<String, String> params = new HashMap<String, String>();
		params.put("rating", result);

		switch (type)
		{
			case HOTEL:
				params.put("reserv_idx", String.valueOf(index));
				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SATISFACTION_RATING_UPDATE).toString(), params, mReserveReviewJsonResponseListener, mHostActivity));
				break;

			case FNB:
				params.put("reservation_rec_idx", String.valueOf(index));
				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_SESSION_RATING_UPDATE).toString(), params, mReserveReviewJsonResponseListener, mHostActivity));
				break;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog)
	{
		lockUI();

		updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RATE_THIS_HOTEL);

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

	public void setOnDismissListener(DialogInterface.OnDismissListener listener)
	{
		if (getDialog() != null)
		{
			getDialog().setOnDismissListener(listener);
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

					DailyToast.showToast(mHostActivity, msg, Toast.LENGTH_LONG);
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
