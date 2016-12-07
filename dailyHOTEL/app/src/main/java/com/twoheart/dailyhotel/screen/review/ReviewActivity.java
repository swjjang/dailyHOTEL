/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * RatingHotelFragment (호텔 만족도 조사 화면)
 * <p>
 * 호텔 만족도 조사를 위한 화면
 */
package com.twoheart.dailyhotel.screen.review;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.ReviewItem;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends BaseActivity
{
    private static final int REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT = 100;
    private static final String INTENT_EXTRA_DATA_REVIEW = "review";

    private static final int REQUEST_NEXT_FOCUSE = 1;

    private Review mReview;
    private Dialog mDialog;

    private DailyEmoticonImageView[] mDailyEmoticonImageView;
    private ReviewLayout mReviewLayout;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REQUEST_NEXT_FOCUSE:
                    mReviewLayout.nextFocusReview((ReviewCardLayout) msg.obj);
                    break;
            }
        }
    };

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
    protected void onResume()
    {
        super.onResume();

        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.startAnimation();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.stopAnimation();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        hideReviewDialog();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT:
                if (resultCode == RESULT_OK)
                {
                    if (data != null && data.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT) == true)
                    {
                        String text = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT);

                        mReviewLayout.setReviewCommentView(text);
                    }
                }
                break;
        }
    }

    private void showReviewDetail()
    {
        mReviewLayout = new ReviewLayout(this, mOnEventListener);

        setContentView(mReviewLayout.onCreateView(R.layout.activity_review));

        ReviewItem reviewItem = mReview.getReviewItem();

        if (reviewItem == null)
        {
            Util.restartApp(this);
            return;
        }

        try
        {
            String periodDate = String.format("%s - %s"//
                , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));

            mReviewLayout.setPlaceInformation(reviewItem.itemName, getString(R.string.message_review_date, periodDate));
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        mReviewLayout.setPlaceImageUrl(this, mReview.getReviewItem().imageUrl);

        ArrayList<ReviewScoreQuestion> reviewScoreQuestionList = mReview.getReviewScoreQuestionList();

        if (reviewScoreQuestionList != null && reviewScoreQuestionList.size() > 0)
        {
            for (ReviewScoreQuestion reviewScoreQuestion : reviewScoreQuestionList)
            {
                View view = mReviewLayout.getReviewScoreView(this, reviewScoreQuestion);

                mReviewLayout.addScrollLayout(view);
            }
        }

        ArrayList<ReviewPickQuestion> reviewPickQuestionList = mReview.getReviewPickQuestionList();

        if (reviewPickQuestionList != null && reviewPickQuestionList.size() > 0)
        {
            for (ReviewPickQuestion reviewPickQuestion : reviewPickQuestionList)
            {
                View view = mReviewLayout.getReviewPickView(this, reviewPickQuestion);

                mReviewLayout.addScrollLayout(view);
            }
        }

        if (mReview.requiredCommentReview == true)
        {
            View view = mReviewLayout.getReviewCommentView(this, reviewItem.placeType);
            mReviewLayout.addScrollLayout(view);
        }
    }

    private void hideReviewDialog()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }

        mDialog = null;
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
        View goodEmoticonView = view.findViewById(R.id.goodEmoticonView);
        View badEmoticonView = view.findViewById(R.id.badEmoticonView);

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
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));
                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }

                case FNB:
                {
                    String periodDate = DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");

                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        mDailyEmoticonImageView = null;
        mDailyEmoticonImageView = new DailyEmoticonImageView[2];

        // 이미지
        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) view.findViewById(R.id.badEmoticonImageView);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) view.findViewById(R.id.goodEmoticonImageView);

        mDailyEmoticonImageView[0].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[1].setJSONData("01_worst_1.aep.comp-424-A_not_satisfied.kf.json");

        final int VALUE_DP100 = Util.dpToPx(ReviewActivity.this, 100);
        final int paddingValue = VALUE_DP100 * 17 / 200;

        mDailyEmoticonImageView[0].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        mDailyEmoticonImageView[1].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);

        mDailyEmoticonImageView[0].startAnimation();
        mDailyEmoticonImageView[1].startAnimation();

        // 딤이미지
        final View badEmoticonDimView = view.findViewById(R.id.badEmoticonDimView);
        final View goodEmoticonDimView = view.findViewById(R.id.goodEmoticonDimView);

        goodEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }


                //                updateSatifactionRating(reviewItem.placeType, reviewItem.itemIdx, RECOMMEND);

                ValueAnimator animation = ValueAnimator.ofFloat(0.83f, 1f);
                animation.setDuration(200);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float value = (float) animation.getAnimatedValue();
                        final int paddingValue = (int) (VALUE_DP100 * (1.0f - value) / 2);

                        mDailyEmoticonImageView[1].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
                    }
                });

                animation.start();

                mDailyEmoticonImageView[0].stopAnimation();
                badEmoticonDimView.setVisibility(View.VISIBLE);

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

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        hideReviewDialog();

                        showReviewDetail();
                    }
                }, 3000);
            }
        });

        badEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                ValueAnimator animation = ValueAnimator.ofFloat(0.83f, 1f);
                animation.setDuration(200);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float value = (float) animation.getAnimatedValue();
                        final int paddingValue = (int) (VALUE_DP100 * (1.0f - value) / 2);

                        mDailyEmoticonImageView[0].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
                    }
                });

                animation.start();

                mDailyEmoticonImageView[1].stopAnimation();
                goodEmoticonDimView.setVisibility(View.VISIBLE);

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

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        hideReviewDialog();

                        showReviewDetail();
                    }
                }, 3000);
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

                finish();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mDailyEmoticonImageView = null;
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

    private ReviewLayout.OnEventListener mOnEventListener = new ReviewLayout.OnEventListener()
    {
        private void sendMessageDelayed(ReviewCardLayout reviewCardLayout)
        {

        }

        @Override
        public void onReviewScoreTypeClick(ReviewCardLayout reviewCardLayout, int reviewScore)
        {
            mHandler.removeMessages(REQUEST_NEXT_FOCUSE);
            Message message = mHandler.obtainMessage(REQUEST_NEXT_FOCUSE, reviewCardLayout);
            mHandler.sendMessageDelayed(message, 1000);
        }

        @Override
        public void onReviewPickTypeClick(ReviewCardLayout reviewCardLayout, int position)
        {
            mHandler.removeMessages(REQUEST_NEXT_FOCUSE);
            Message message = mHandler.obtainMessage(REQUEST_NEXT_FOCUSE, reviewCardLayout);
            mHandler.sendMessageDelayed(message, 1000);
        }

        @Override
        public void onReviewCommentClick(ReviewCardLayout reviewCardLayout, String comment)
        {
            Intent intent = WriteReviewCommentActivity.newInstance(ReviewActivity.this, comment);
            startActivityForResult(intent, REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT);
        }

        @Override
        public void onConfirmClick()
        {

        }

        @Override
        public void finish()
        {
            ReviewActivity.this.finish();
        }
    };
}
