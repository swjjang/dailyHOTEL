/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.FontManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SatisfactionActivity extends BaseActivity implements Constants, View.OnClickListener
{
    private static final String NOT_RATE = "0";
    private static final String RECOMMEND = "1";
    private static final String NOT_RECOMMEND = "2";

    private String mSatisfaction;
    private String mTicketName;
    private int mReservationIndex;
    private long mCheckInDate;
    private long mCheckOutDate;
    private Constants.PlaceType mPlaceType;
    private Dialog mDialog;
    private ArrayList<ReviewCode> mReviewCodeList;
    private DailyEditText mCommentsView;

    private static class ReviewCode
    {
        String name;
        int index;
        boolean isChecked;
    }

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
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, PlaceType.HOTEL.name());

        return intent;
    }

    public static Intent newInstance(Context context, String placeName, int reservationIndex, long checkInDate) throws IllegalArgumentException
    {
        if (Util.isTextEmpty(placeName) == true || reservationIndex == 0 || checkInDate == 0)
        {
            throw new IllegalArgumentException();
        }

        Intent intent = new Intent(context, SatisfactionActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, placeName);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RESERVATIONINDEX, reservationIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInDate);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, PlaceType.FNB.name());

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mPlaceType = PlaceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));

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
    protected void onDestroy()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }

        mDialog = null;

        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.service01TextView:
            case R.id.service02TextView:
            case R.id.service03TextView:
            case R.id.service04TextView:
                ReviewCode reviewCode = (ReviewCode) v.getTag();

                if (reviewCode != null)
                {
                    reviewCode.isChecked = !reviewCode.isChecked;
                    v.setSelected(reviewCode.isChecked);
                }
                break;
        }
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

                switch (mPlaceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_SATISFACTION, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_SATISFACTION, null);
                        break;
                }
            }
        });

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RECOMMEND);

                switch (mPlaceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_DISSATISFACTION, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_DISSATISFACTION, null);
                        break;
                }
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                updateSatifactionRating(mPlaceType, mReservationIndex, NOT_RATE);

                switch (mPlaceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(SatisfactionActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_CLOSE_BUTTON_CLICKED, null);
                        break;
                }
            }
        });

        try
        {
            mDialog.setContentView(view);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }


    private void showSatisfactionDetailDialog(final boolean isSatisfaction, ArrayList<ReviewCode> reviewCodeList)
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
        mCommentsView = (DailyEditText) view.findViewById(R.id.commentsView);

        mCommentsView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                commentsHintView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (mCommentsView.length() == 0)
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
                if (isSatisfaction == true)
                {
                    // 만족함
                    titleTextView.setText(R.string.satisfaction_recommend_title);
                    contentTextView.setText(R.string.satisfaction_recommend_detailtext);

                    selectServiceTextView.setVisibility(View.GONE);
                    selectServiceLayout.setVisibility(View.GONE);

                    commentsHintView.setText(R.string.satisfaction_hotel_comments_hint01);

                } else
                {
                    // 만족안함
                    titleTextView.setText(R.string.satisfaction_not_recommend_title);
                    contentTextView.setText(R.string.satisfaction_not_recommend_detailtext);

                    if (reviewCodeList != null && reviewCodeList.size() == 5)
                    {
                        selectServiceTextView.setVisibility(View.VISIBLE);
                        selectServiceLayout.setVisibility(View.VISIBLE);

                        service01TextView.setText(reviewCodeList.get(0).name);
                        service01TextView.setTag(reviewCodeList.get(0));

                        service02TextView.setText(reviewCodeList.get(1).name);
                        service02TextView.setTag(reviewCodeList.get(1));

                        service03TextView.setText(reviewCodeList.get(2).name);
                        service03TextView.setTag(reviewCodeList.get(2));

                        service04TextView.setText(reviewCodeList.get(3).name);
                        service04TextView.setTag(reviewCodeList.get(3));

                        service01TextView.setOnClickListener(this);
                        service02TextView.setOnClickListener(this);
                        service03TextView.setOnClickListener(this);
                        service04TextView.setOnClickListener(this);
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
                if (isSatisfaction == true)
                {
                    // 만족함
                    titleTextView.setText(R.string.satisfaction_recommend_title);
                    contentTextView.setText(R.string.satisfaction_recommend_detailtext);

                    selectServiceTextView.setVisibility(View.GONE);
                    selectServiceLayout.setVisibility(View.GONE);

                    commentsHintView.setText(R.string.satisfaction_gourmet_comments_hint01);

                } else
                {
                    // 만족안함
                    titleTextView.setText(R.string.satisfaction_not_recommend_title);
                    contentTextView.setText(R.string.satisfaction_not_recommend_detailtext);

                    if (reviewCodeList != null && reviewCodeList.size() == 5)
                    {
                        selectServiceTextView.setVisibility(View.VISIBLE);
                        selectServiceLayout.setVisibility(View.VISIBLE);

                        service01TextView.setText(reviewCodeList.get(0).name);
                        service01TextView.setTag(reviewCodeList.get(0));

                        service02TextView.setText(reviewCodeList.get(1).name);
                        service02TextView.setTag(reviewCodeList.get(1));

                        service03TextView.setText(reviewCodeList.get(2).name);
                        service03TextView.setTag(reviewCodeList.get(2));

                        service04TextView.setText(reviewCodeList.get(3).name);
                        service04TextView.setTag(reviewCodeList.get(3));

                        service01TextView.setOnClickListener(this);
                        service02TextView.setOnClickListener(this);
                        service03TextView.setOnClickListener(this);
                        service04TextView.setOnClickListener(this);
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

        final TextView positiveTextView = (TextView) view.findViewById(R.id.positiveTextView);

        mCommentsView.setUsedImeActionSend(true);
        mCommentsView.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mCommentsView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {
                    positiveTextView.performClick();
                }
                return false;
            }
        });

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                Map<String, String> params = new HashMap<>();

                if (isSatisfaction == true)
                {
                    // 아무내용이 없으면 보내지 않는다.
                    if (Util.isTextEmpty(mCommentsView.getText().toString().trim()) == true)
                    {
                        finish();
                        return;
                    }

                } else
                {
                    String rating_types = null;

                    for (ReviewCode reviewCode : mReviewCodeList)
                    {
                        if (reviewCode.isChecked == true)
                        {
                            if (Util.isTextEmpty(rating_types) == true)
                            {
                                rating_types = String.valueOf(reviewCode.index);
                            } else
                            {
                                rating_types += ("," + reviewCode.index);
                            }
                        }
                    }

                    // 아무내용이 없으면 보내지 않는다.
                    if (Util.isTextEmpty(mCommentsView.getText().toString().trim()) == true && Util.isTextEmpty(rating_types) == true)
                    {
                        finish();
                        return;
                    }

                    if (Util.isTextEmpty(rating_types) == true)
                    {
                        rating_types = String.valueOf(mReviewCodeList.get(mReviewCodeList.size() - 1).index);
                    }

                    params.put("rating_types", rating_types);
                }

                params.put("msg", mCommentsView.getText().toString().trim());

                switch (mPlaceType)
                {
                    case HOTEL:
                    {
                        params.put("reserv_idx", String.valueOf(mReservationIndex));
                        DailyNetworkAPI.getInstance().requestHotelDetailRating(mNetworkTag, params, mReservSatisfactionUpdateJsonResponseListener, new ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError arg0)
                            {
                                finish();
                            }
                        });
                        break;
                    }

                    case FNB:
                    {
                        params.put("fnb_reservation_rec_idx", String.valueOf(mReservationIndex));

                        DailyNetworkAPI.getInstance().requestGourmetDetailRating(mNetworkTag, params, mReservSatisfactionUpdateJsonResponseListener, new ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError arg0)
                            {
                                finish();
                            }
                        });
                        break;
                    }
                }
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        });

        try
        {
            mDialog.setContentView(view);
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void updateSatifactionRating(Constants.PlaceType placeType, int index, String result)
    {
        if (result == null)
        {
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        mSatisfaction = result;

        DailyHotelJsonResponseListener listener = mReserveReviewJsonResponseListener;

        switch (placeType)
        {
            case HOTEL:
                DailyNetworkAPI.getInstance().requestHotelRating(mNetworkTag, result, Integer.toString(index), listener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        finish();
                    }
                });
                break;

            case FNB:
                DailyNetworkAPI.getInstance().requestGourmetRating(mNetworkTag, result, Integer.toString(index), listener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        finish();
                    }
                });
                break;
        }
    }

    private DailyHotelJsonResponseListener mReserveReviewJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (mDialog != null && mDialog.isShowing() == true)
            {
                mDialog.dismiss();
            }

            mDialog = null;

            if (NOT_RATE.equalsIgnoreCase(mSatisfaction) == true)
            {
                unLockUI();

                finish();
                return;
            }

            try
            {
                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = jsonObject.getBoolean("is_success");
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
                    String msg = response.getString("msg");

                    if (RECOMMEND.equalsIgnoreCase(mSatisfaction) == true)
                    {
                        unLockUI();

                        // 만족함
                        showSatisfactionDetailDialog(true, null);
                    } else
                    {
                        lockUI();

                        // 만족안함
                        String review = null;

                        switch (mPlaceType)
                        {
                            case HOTEL:
                                review = "HOTEL_DISSATISFACTION";
                                break;

                            case FNB:
                                review = "FNB_DISSATISFACTION";
                                break;
                        }

                        DailyNetworkAPI.getInstance().requestCommonReview(mNetworkTag, review, mRequestServicesJsonResponseListener, new ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError arg0)
                            {
                                finish();
                            }
                        });
                    }
                } else
                {
                    finish();
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                finish();
            }
        }
    };

    private DailyHotelJsonResponseListener mRequestServicesJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONArray jsonArray = response.getJSONArray("data");

                    if (mReviewCodeList == null)
                    {
                        mReviewCodeList = new ArrayList<>(5);
                    }

                    mReviewCodeList.clear();

                    int length = jsonArray.length();

                    for (int i = 0; i < length; i++)
                    {
                        JSONObject serviceJSONObject = jsonArray.getJSONObject(i);

                        ReviewCode reviewCode = new ReviewCode();
                        reviewCode.name = serviceJSONObject.getString("name");
                        reviewCode.index = serviceJSONObject.getInt("idx");

                        mReviewCodeList.add(reviewCode);
                    }

                    showSatisfactionDetailDialog(false, mReviewCodeList);
                } else
                {
                    finish();
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                finish();
            }
        }
    };


    private DailyHotelJsonResponseListener mReservSatisfactionUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    String msg = response.getString("msg");

                    DailyToast.showToast(SatisfactionActivity.this, msg, Toast.LENGTH_SHORT);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            } finally
            {
                finish();
            }
        }
    };
}
