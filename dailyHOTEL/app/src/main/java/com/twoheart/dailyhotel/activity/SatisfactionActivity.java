/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p/>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.OnLoadListener;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SatisfactionActivity extends BaseActivity implements Constants, OnLoadListener
{
    private static final String NOT_RATE = "0";
    private static final String RECOMMEND = "1";
    private static final String NOT_RECOMMEND = "2";

    private RequestQueue mQueue;

    private String mSatisfaction;
    private String mTicketName;
    private int mReservationIndex;
    private Long mCheckInDate;
    private Long mCheckOutDate;
    private PlaceMainFragment.TYPE mPlaceType;
    private Dialog mDialog;


    public static Intent newInstance(Context context, String hotelName, int reservationIndex, long checkInDate, long checkOutDate) throws IllegalArgumentException
    {
        if (Util.isTextEmpty(hotelName) == true || reservationIndex == 0 || checkInDate == 0 || checkOutDate == 0)
        {
            throw new IllegalArgumentException();
        }

        Intent intent = new Intent(context, SatisfactionActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, hotelName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInDate);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, checkOutDate);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, PlaceMainFragment.TYPE.HOTEL.name());

        return intent;
    }

    public static Intent newInstance(Context context, String placeName, int reservationIndex, long checkInDate) throws IllegalArgumentException
    {
        if (Util.isTextEmpty(placeName) == true || reservationIndex == 0 || checkInDate == 0)
        {
            throw new IllegalArgumentException();
        }

        Intent intent = new Intent(context, SatisfactionActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, placeName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInDate);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, PlaceMainFragment.TYPE.FNB.name());

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mQueue = VolleyHttpClient.getRequestQueue();

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mPlaceType = PlaceMainFragment.TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));

        switch (mPlaceType)
        {
            case HOTEL:
            {
                mTicketName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
                mReservationIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, 0);
                mCheckInDate = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, 0);
                mCheckOutDate = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, 0);
                break;
            }

            case FNB:
            {
                mTicketName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
                mReservationIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, 0);
                mCheckInDate = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, 0);
                break;
            }
        }

        showSatisfactionDialog();
    }

    @Override
    protected void onStop()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }

        mDialog = null;

        super.onStop();
    }

    private void showSatisfactionDialog()
    {
        mDialog = new Dialog(this);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_rating_hotel, null, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView ratingPeriod = (TextView) view.findViewById(R.id.periodTextView);
        TextView ratingHotelName = (TextView) view.findViewById(R.id.hotelNameTextView);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);

        ratingPeriod.setTypeface(FontManager.getInstance(this).getMediumTypeface());
        messageTextView.setTypeface(FontManager.getInstance(this).getMediumTypeface());

        TextView positiveTextView = (TextView) view.findViewById(R.id.positiveTextView);
        TextView negativeTextView = (TextView) view.findViewById(R.id.negativeTextView);

        switch (mPlaceType)
        {
            case HOTEL:
            {
                // 제목
                titleTextView.setText(R.string.frag_rating_hotel_title);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                String periodDate = String.format("%s - %s", simpleDateFormat.format(new Date(mCheckInDate)), simpleDateFormat.format(new Date(mCheckOutDate)));
                ratingPeriod.setText(getString(R.string.frag_rating_hotel_text1, periodDate));
                break;
            }

            case FNB:
            {
                // 제목
                titleTextView.setText(R.string.frag_rating_gourmet_title);

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
                updateSatifactionRating(mPlaceType, mReservationIndex, RECOMMEND);
            }
        });

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RECOMMEND);
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RATE);
            }
        });

        mDialog.show();
    }


    private void showSatisfactionDetailDialog(String[] service)
    {
        if (mDialog != null && mDialog.isShowing() == true)
        {
            mDialog.dismiss();
        }

        mDialog = null;
        mDialog = new Dialog(this);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_satisfaction_detail, null, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);

        TextView selectServiceTextView = (TextView) view.findViewById(R.id.selectServiceTextView);
        View selectServiceLayout = view.findViewById(R.id.selectServiceLayout);

        TextView service01TextView = (TextView) view.findViewById(R.id.service01TextView);
        TextView service02TextView = (TextView) view.findViewById(R.id.service02TextView);
        TextView service03TextView = (TextView) view.findViewById(R.id.service03TextView);
        TextView service04TextView = (TextView) view.findViewById(R.id.service04TextView);

        final TextView commentsHintView = (TextView) view.findViewById(R.id.commentsHintView);
        final EditText commentsView = (EditText) view.findViewById(R.id.commentsView);

        commentsView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                commentsHintView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (commentsView.length() == 0)
                {
                    commentsHintView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        switch (mPlaceType)
        {
            case HOTEL:
            {
                if (RECOMMEND.equalsIgnoreCase(mSatisfaction) == true)
                {
                    // 만족함
                    titleTextView.setText(R.string.satisfaction_recommend_title);
                    contentTextView.setText(R.string.satisfaction_recommend_detailtext);

                    selectServiceTextView.setVisibility(View.GONE);
                    selectServiceLayout.setVisibility(View.GONE);

                    commentsHintView.setText(R.string.satisfaction_hotel_comments_hint01);

                } else if (NOT_RECOMMEND.equalsIgnoreCase(mSatisfaction) == true)
                {
                    // 만족안함
                    titleTextView.setText(R.string.satisfaction_not_recommend_title);
                    contentTextView.setText(R.string.satisfaction_not_recommend_detailtext);

                    if (service != null && service.length == 4)
                    {
                        selectServiceTextView.setVisibility(View.VISIBLE);
                        selectServiceLayout.setVisibility(View.VISIBLE);

                        service01TextView.setText(service[0]);
                        service02TextView.setText(service[1]);
                        service03TextView.setText(service[2]);
                        service04TextView.setText(service[3]);
                    } else
                    {
                        selectServiceTextView.setVisibility(View.GONE);
                        selectServiceLayout.setVisibility(View.GONE);
                    }

                    commentsHintView.setText(R.string.satisfaction_hotel_comments_hint02);
                }
                break;
            }

            case FNB:
            {
                if (RECOMMEND.equalsIgnoreCase(mSatisfaction) == true)
                {
                    // 만족함
                    titleTextView.setText(R.string.satisfaction_recommend_title);
                    contentTextView.setText(R.string.satisfaction_recommend_detailtext);

                    selectServiceTextView.setVisibility(View.GONE);
                    selectServiceLayout.setVisibility(View.GONE);

                    commentsHintView.setText(R.string.satisfaction_gourmet_comments_hint01);

                } else if (NOT_RECOMMEND.equalsIgnoreCase(mSatisfaction) == true)
                {
                    // 만족안함
                    titleTextView.setText(R.string.satisfaction_not_recommend_title);
                    contentTextView.setText(R.string.satisfaction_not_recommend_detailtext);

                    if (service != null && service.length == 4)
                    {
                        selectServiceTextView.setVisibility(View.VISIBLE);
                        selectServiceLayout.setVisibility(View.VISIBLE);

                        service01TextView.setText(service[0]);
                        service02TextView.setText(service[1]);
                        service03TextView.setText(service[2]);
                        service04TextView.setText(service[3]);
                    } else
                    {
                        selectServiceTextView.setVisibility(View.GONE);
                        selectServiceLayout.setVisibility(View.GONE);
                    }

                    commentsHintView.setText(R.string.satisfaction_gourmet_comments_hint02);
                }
                break;
            }
        }

        TextView positiveTextView = (TextView) view.findViewById(R.id.positiveTextView);

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
                return;
            }
        });
    }


    private void updateSatifactionRating(PlaceMainFragment.TYPE type, int index, String result)
    {
        if (result == null)
        {
            return;
        }

        lockUI();

        mSatisfaction = result;

        Map<String, String> params = new HashMap<String, String>();
        params.put("rating", result);

        DailyHotelJsonResponseListener listener = mReserveReviewJsonResponseListener;

        switch (type)
        {
            case HOTEL:
                params.put("reserv_idx", String.valueOf(index));
                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SATISFACTION_RATING_UPDATE).toString(), params, listener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        // TODO Auto-generated method stub

                    }
                }));
                break;

            case FNB:
                params.put("reservation_rec_idx", String.valueOf(index));
                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_SESSION_RATING_UPDATE).toString(), params, listener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        // TODO Auto-generated method stub

                    }
                }));
                break;
        }
    }

    private DailyHotelJsonResponseListener mReserveReviewJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            if (mDialog != null && mDialog.isShowing() == true)
            {
                mDialog.dismiss();
            }

            mDialog = null;

            if (NOT_RATE.equalsIgnoreCase(mSatisfaction) == true)
            {
                finish();
                return;
            }

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

                    // 상세 만족도 정보 요청

                    //                    DailyToast.showToast(mHostActivity, msg, Toast.LENGTH_LONG);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    };
}
