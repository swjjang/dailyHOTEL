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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.ReviewItem;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends BaseActivity implements Constants, View.OnClickListener
{
    private static final String INTENT_EXTRA_DATA_REVIEW = "review";

    private static final String NOT_RATE = "0";
    private static final String RECOMMEND = "1";
    private static final String NOT_RECOMMEND = "2";

    private Review mReview;
    private Dialog mDialog;

    public static Intent newInstance(Context context, Review review) throws IllegalArgumentException
    {
        if (context == null || review == null)
        {
            throw new IllegalArgumentException();
        }

        Intent intent = new Intent(context, ReviewActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_REVIEW, review);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

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

        mReview = intent.getParcelableExtra(INTENT_EXTRA_DATA_REVIEW);

        showReviewDialog();
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
        }
    }

    private void showReviewDialog()
    {
        mDialog = new Dialog(this);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_review, null, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView periodTextView = (TextView) view.findViewById(R.id.periodTextView);
        TextView positiveTextView = (TextView) view.findViewById(R.id.positiveTextView);
        TextView negativeTextView = (TextView) view.findViewById(R.id.negativeTextView);

        final ReviewItem reviewItem = mReview.getReviewItem();

        if (reviewItem == null)
        {
            throw new NullPointerException("reviewItem == null");
        }

        // 타이틀
        titleTextView.setText(getString(R.string.message_review_title, reviewItem.itemName));

        try
        {
            // 시간
            switch (reviewItem.placeType)
            {
                case HOTEL:
                {
                    String periodDate = String.format("%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }

                case FNB:
                {
                    String periodDate = DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        // 이미지
        DailyEmoticonImageView badEmoticonImageView = (DailyEmoticonImageView)view.findViewById(R.id.badEmoticonImageView);
        DailyEmoticonImageView goodEmoticonImageView = (DailyEmoticonImageView)view.findViewById(R.id.goodEmoticonImageView);

        //        final String placeName = String.format("\'%s\'", mTicketName);
        //        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.frag_rating_hotel_text2, placeName));
        //        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dh_theme_color)), 0, placeName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //
        //        ratingHotelName.setText(spannableStringBuilder);

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                updateSatifactionRating(reviewItem.placeType, reviewItem.itemIdx, RECOMMEND);

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, reviewItem.itemName);
                params.put(AnalyticsManager.KeyType.SATISFACTION_SURVEY, AnalyticsManager.ValueType.SATISFIED);

                switch (reviewItem.placeType)
                {
                    case HOTEL:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_SATISFACTION, params);
                        break;

                    case FNB:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_SATISFACTION, params);
                        break;
                }
            }
        });

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                updateSatifactionRating(reviewItem.placeType, reviewItem.itemIdx, NOT_RECOMMEND);

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, reviewItem.itemName);
                params.put(AnalyticsManager.KeyType.SATISFACTION_SURVEY, AnalyticsManager.ValueType.DISSATISFIED);

                switch (reviewItem.placeType)
                {
                    case HOTEL:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_DISSATISFACTION, params);
                        break;

                    case FNB:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_DISSATISFACTION, params);
                        break;
                }
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                //                updateSatifactionRating(reviewItem.placeType, reviewItem.itemIdx, NOT_RATE);

                switch (reviewItem.placeType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
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

    private void recordAnalytics(PlaceType placeType, String ratingName, boolean isSatisfaction)
    {
        //        if (isSatisfaction == true)
        //        {
        //            switch (placeType)
        //            {
        //                case HOTEL:
        //                {
        //                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
        //                        , AnalyticsManager.Action.HOTEL_SATISFACTION_DETAILED_POPPEDUP, ratingName, null);
        //                    break;
        //                }
        //
        //                case FNB:
        //                {
        //                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
        //                        , AnalyticsManager.Action.GOURMET_SATISFACTION_DETAILED_POPPEDUP, ratingName, null);
        //                    break;
        //                }
        //            }
        //        } else
        //        {
        //            switch (placeType)
        //            {
        //                case HOTEL:
        //                {
        //                    Map<String, String> eventParams = Collections.singletonMap(AnalyticsManager.KeyType.TICKET_NAME, mTicketName);
        //                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
        //                        , AnalyticsManager.Action.HOTEL_DISSATISFACTION_DETAILED_POPPEDUP, ratingName, eventParams);
        //                    break;
        //                }
        //
        //                case FNB:
        //                {
        //                    Map<String, String> eventParams = Collections.singletonMap(AnalyticsManager.KeyType.TICKET_NAME, mTicketName);
        //                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
        //                        , AnalyticsManager.Action.GOURMET_DISSATISFACTION_DETAILED_POPPEDUP, ratingName, eventParams);
        //                    break;
        //                }
        //            }
        //        }
    }

    private DailyHotelJsonResponseListener mRecentReviewJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            unLockUI();

            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {

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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            finish();
        }
    };
}
