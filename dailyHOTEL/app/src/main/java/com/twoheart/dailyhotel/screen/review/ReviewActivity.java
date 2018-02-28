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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.ReviewItem;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ReviewActivity extends BaseActivity
{
    private static final int REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT = 100;
    private static final String INTENT_EXTRA_DATA_REVIEW = "review";

    private static final int REQUEST_NEXT_FOCUS = 1;

    Review mReview;
    private Dialog mDialog;
    String mReviewGrade;

    DailyEmoticonImageView[] mDailyEmoticonImageView;
    ReviewLayout mReviewLayout;
    ReviewNetworkController mReviewNetworkController;

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case REQUEST_NEXT_FOCUS:
                    mReviewLayout.nextFocusReview(msg.arg1);
                    unLockUI();
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



        mReviewNetworkController = new ReviewNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

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

        if (mReviewLayout != null)
        {
            mReviewLayout.startAnimation();
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

        if (mReviewLayout != null)
        {
            mReviewLayout.stopAnimation();
        }
    }

    @Override
    protected void onDestroy()
    {
        stopEmoticonAnimation();

        if (mReviewLayout != null)
        {
            mReviewLayout.stopAnimation();
        }

        hideReviewDialog();

        super.onDestroy();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, mReview.getReviewItem().itemName);

            switch (mReview.getReviewItem().serviceType)
            {
                case HOTEL:
                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                        , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, params);
                    break;

                case GOURMET:
                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                        , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, params);
                    break;

                case OB_STAY:
                    AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                        , AnalyticsManager.Action.REVIEW_DETAIL_OB, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED, params);
                    break;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        showSimpleDialog(getString(R.string.message_review_dialog_cancel_review_title)//
            , getString(R.string.message_review_dialog_cancel_review_description), getString(R.string.dialog_btn_text_yes)//
            , getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    lockUI(false);

                    DailyToast.showToast(ReviewActivity.this, R.string.message_review_toast_canceled_review_detail, Toast.LENGTH_SHORT);

                    if (mReviewLayout != null)
                    {
                        mHandler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mReviewLayout.hideReviewDetailAnimation();
                            }
                        }, 1000);
                    }

                    try
                    {
                        Map<String, String> params = new HashMap<>();
                        params.put(AnalyticsManager.KeyType.NAME, mReview.getReviewItem().itemName);

                        switch (mReview.getReviewItem().serviceType)
                        {
                            case HOTEL:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP, AnalyticsManager.Label._YES, params);
                                break;

                            case GOURMET:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP, AnalyticsManager.Label._YES, params);
                                break;

                            case OB_STAY:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP_OB, AnalyticsManager.Label._YES, params);
                                break;
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    releaseUiComponent();

                    try
                    {
                        Map<String, String> params = new HashMap<>();
                        params.put(AnalyticsManager.KeyType.NAME, mReview.getReviewItem().itemName);

                        switch (mReview.getReviewItem().serviceType)
                        {
                            case HOTEL:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP, AnalyticsManager.Label._NO, params);
                                break;

                            case GOURMET:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP, AnalyticsManager.Label._NO, params);
                                break;

                            case OB_STAY:
                                AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                    , AnalyticsManager.Action.REVIEW_POPUP_OB, AnalyticsManager.Label._NO, params);
                                break;
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    releaseUiComponent();
                }
            }, null, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT:
                if (resultCode == RESULT_OK && data != null && data.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT) == true)
                {
                    String text = data.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT);

                    if (mReviewLayout == null)
                    {
                        restartExpiredSession();
                        return;
                    }

                    mReviewLayout.setReviewCommentView(text);
                    setConfirmTextView();
                }
                break;
        }
    }

    void showReviewDetail()
    {
        stopEmoticonAnimation();

        if (mReviewLayout == null)
        {
            mReviewLayout = new ReviewLayout(this, mOnEventListener);
        }

        setContentView(mReviewLayout.onCreateView(R.layout.activity_review));

        ReviewItem reviewItem = mReview.getReviewItem();

        if (reviewItem == null)
        {
            Util.restartApp(this);
            return;
        }

        switch (reviewItem.serviceType)
        {
            case HOTEL:
            {
                try
                {
                    String periodDate = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));

                    mReviewLayout.setPlaceInformation(reviewItem.itemName, getString(R.string.message_review_date, periodDate), mReviewGrade);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
                break;
            }

            case GOURMET:
            {
                try
                {
                    String periodDate = DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");

                    mReviewLayout.setPlaceInformation(reviewItem.itemName, getString(R.string.message_review_date, periodDate), mReviewGrade);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
                break;
            }

            case OB_STAY:
                try
                {
                    String periodDate = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));

                    mReviewLayout.setPlaceInformation(reviewItem.itemName, getString(R.string.message_review_date, periodDate), mReviewGrade);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
                break;
        }

        mReviewLayout.setPlaceImageUrl(this, mReview.getReviewItem().serviceType, mReview.getReviewItem().getImageMap());

        int position = 0;

        ArrayList<ReviewScoreQuestion> reviewScoreQuestionList = mReview.getReviewScoreQuestionList();

        if (reviewScoreQuestionList != null && reviewScoreQuestionList.size() > 0)
        {
            for (ReviewScoreQuestion reviewScoreQuestion : reviewScoreQuestionList)
            {
                View view = mReviewLayout.getReviewScoreView(this, position++, reviewScoreQuestion);

                mReviewLayout.addScrollLayout(view);
            }
        }

        ArrayList<ReviewPickQuestion> reviewPickQuestionList = mReview.getReviewPickQuestionList();

        if (reviewPickQuestionList != null && reviewPickQuestionList.size() > 0)
        {
            for (ReviewPickQuestion reviewPickQuestion : reviewPickQuestionList)
            {
                View view = mReviewLayout.getReviewPickView(this, position++, reviewPickQuestion);

                mReviewLayout.addScrollLayout(view);
            }
        }

        if (mReview.requiredCommentReview == true)
        {
            View view = mReviewLayout.getReviewCommentView(this, position++, reviewItem.serviceType);
            mReviewLayout.addScrollLayout(view);
        }

        setConfirmTextView();

        mReviewLayout.setVisibility(false);

        // Analytics
        switch (reviewItem.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_REVIEWDETAIL, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_REVIEWDETAIL, null);
                break;

            case OB_STAY:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_REVIEWDETAIL_OUTBOUND, null);
                break;
        }
    }

    void hideReviewDialog()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
        }

        mDialog = null;
    }

    void stopEmoticonAnimation()
    {
        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.stopAnimation();
            }
        }

        mDailyEmoticonImageView = null;
    }

    void setConfirmTextView()
    {
        int uncheckedReviewCount = mReviewLayout.getUncheckedReviewCount();
        String text;
        boolean enabled;

        if (uncheckedReviewCount > 0)
        {
            if (uncheckedReviewCount == mReview.reviewAllCount)
            {
                text = getString(R.string.message_review_answer_question, uncheckedReviewCount);
            } else
            {
                text = getString(R.string.message_review_remain_n_count, uncheckedReviewCount);
            }

            enabled = false;
        } else if (uncheckedReviewCount == 0)
        {
            text = getString(R.string.message_review_send_feedback);
            enabled = true;
        } else
        {
            return;
        }

        mReviewLayout.setConfirmTextView(text, enabled);
    }

    private void showReviewDialog()
    {
        mDialog = new Dialog(this);

        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_review, null, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView periodTextView = view.findViewById(R.id.periodTextView);
        View goodEmoticonView = view.findViewById(R.id.goodEmoticonView);
        View badEmoticonView = view.findViewById(R.id.badEmoticonView);

        final ReviewItem reviewItem = mReview.getReviewItem();

        if (reviewItem == null)
        {
            throw new NullPointerException("reviewItem == null");
        }

        // 타이틀
        String title = getString(R.string.message_review_title, reviewItem.itemName);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
        spannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(this).getRegularTypeface()),//
            title.lastIndexOf('\'') + 1, title.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        titleTextView.setText(spannableStringBuilder);

        try
        {
            // 시간
            switch (reviewItem.serviceType)
            {
                case HOTEL:
                {
                    String periodDate = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));
                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }

                case GOURMET:
                {
                    String periodDate = DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");

                    periodTextView.setText(getString(R.string.message_review_date, periodDate));
                    break;
                }

                case OB_STAY:
                {
                    String periodDate = String.format(Locale.KOREA, "%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));
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
        mDailyEmoticonImageView[0] = view.findViewById(R.id.badEmoticonImageView);
        mDailyEmoticonImageView[1] = view.findViewById(R.id.goodEmoticonImageView);

        mDailyEmoticonImageView[0].setJSONData("Review_Animation.aep.comp-737-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[1].setJSONData("Review_Animation.aep.comp-573-B_satfisfied.kf.json");

        final int VALUE_DP100 = ScreenUtils.dpToPx(ReviewActivity.this, 100);
        final int paddingValue = VALUE_DP100 * 17 / 200;

        mDailyEmoticonImageView[0].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
        mDailyEmoticonImageView[1].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);

        mDailyEmoticonImageView[0].startAnimation();
        mDailyEmoticonImageView[1].startAnimation();

        // 딤이미지
        final View badEmoticonDimView = view.findViewById(R.id.badEmoticonDimView);
        final View goodEmoticonDimView = view.findViewById(R.id.goodEmoticonDimView);

        // 텍스트
        final TextView badEmoticonTextView = view.findViewById(R.id.badEmoticonTextView);
        final TextView goodEmoticonTextView = view.findViewById(R.id.goodEmoticonTextView);

        goodEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI(false);

                JSONObject jsonObject = mReview.toReviewJSONObject(Review.GRADE_GOOD);

                if (jsonObject == null)
                {
                    // 에러 문구가 필요할까?
                    restartExpiredSession();
                    return;
                }

                if (mDailyEmoticonImageView[1].isAnimationStart() == false)
                {
                    mDailyEmoticonImageView[1].startAnimation();
                }

                switch (reviewItem.serviceType)
                {
                    case HOTEL:
                    case GOURMET:
                        mReviewNetworkController.requestAddReviewInformation(jsonObject);
                        break;

                    case OB_STAY:
                        mReviewNetworkController.requestStayOutboundAddReviewInformation(mReview.reserveIdx, jsonObject);
                        break;
                }

                ValueAnimator animation = ValueAnimator.ofFloat(0.83f, 1f);
                animation.setDuration(200);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        try
                        {
                            float value = (Float) animation.getAnimatedValue();
                            final int paddingValue = (int) (VALUE_DP100 * (1.0f - value) / 2);

                            mDailyEmoticonImageView[1].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }
                    }
                });

                animation.start();

                mDailyEmoticonImageView[0].stopAnimation();
                badEmoticonDimView.setVisibility(View.VISIBLE);
                goodEmoticonTextView.setSelected(true);
                goodEmoticonTextView.setTypeface(FontManager.getInstance(ReviewActivity.this).getMediumTypeface());

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, reviewItem.itemName);
                params.put(AnalyticsManager.KeyType.SATISFACTION_SURVEY, AnalyticsManager.ValueType.SATISFIED);

                switch (reviewItem.serviceType)
                {
                    case HOTEL:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_SATISFACTION, params);
                        break;

                    case GOURMET:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_SATISFACTION, params);
                        break;

                    case OB_STAY:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.OB_SATISFACTION, params);
                        break;
                }

                //                DailyToast.showToast(ReviewActivity.this, R.string.message_review_toast_satisfied, Toast.LENGTH_LONG);
            }
        });

        badEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI(false);

                JSONObject jsonObject = mReview.toReviewJSONObject(Review.GRADE_BAD);

                if (jsonObject == null)
                {
                    // 에러 문구가 필요할까?
                    restartExpiredSession();
                    return;
                }

                if (mDailyEmoticonImageView[0].isAnimationStart() == false)
                {
                    mDailyEmoticonImageView[0].startAnimation();
                }

                switch (reviewItem.serviceType)
                {
                    case HOTEL:
                    case GOURMET:
                        mReviewNetworkController.requestAddReviewInformation(jsonObject);
                        break;

                    case OB_STAY:
                        mReviewNetworkController.requestStayOutboundAddReviewInformation(mReview.reserveIdx, jsonObject);
                        break;
                }

                ValueAnimator animation = ValueAnimator.ofFloat(0.83f, 1f);
                animation.setDuration(200);
                animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        try
                        {
                            float value = (Float) animation.getAnimatedValue();
                            final int paddingValue = (int) (VALUE_DP100 * (1.0f - value) / 2);

                            mDailyEmoticonImageView[0].setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
                        } catch (Exception e)
                        {
                            ExLog.e(e.toString());
                        }
                    }
                });

                animation.start();

                mDailyEmoticonImageView[1].stopAnimation();
                goodEmoticonDimView.setVisibility(View.VISIBLE);
                badEmoticonTextView.setSelected(true);
                badEmoticonTextView.setTypeface(FontManager.getInstance(ReviewActivity.this).getMediumTypeface());

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, reviewItem.itemName);
                params.put(AnalyticsManager.KeyType.SATISFACTION_SURVEY, AnalyticsManager.ValueType.DISSATISFIED);

                switch (reviewItem.serviceType)
                {
                    case HOTEL:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_DISSATISFACTION, params);
                        break;

                    case GOURMET:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_DISSATISFACTION, params);
                        break;

                    case OB_STAY:
                        params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.OB_DISSATISFACTION, params);
                        break;
                }

                //                DailyToast.showToast(ReviewActivity.this, R.string.message_review_toast_dissatisfied, Toast.LENGTH_LONG);
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                JSONObject jsonObject = mReview.toReviewJSONObject(Review.GRADE_NONE);

                if (jsonObject == null)
                {
                    // 에러 문구가 필요할까?
                    restartExpiredSession();
                    return;
                }

                mReviewGrade = Review.GRADE_NONE;

                switch (reviewItem.serviceType)
                {
                    case HOTEL:
                        mReviewNetworkController.requestAddReviewInformation(jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED, null);
                        break;

                    case GOURMET:
                        mReviewNetworkController.requestAddReviewInformation(jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.GOURMET_CLOSE_BUTTON_CLICKED, null);
                        break;

                    case OB_STAY:
                        mReviewNetworkController.requestStayOutboundAddReviewInformation(mReview.reserveIdx, jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                            , AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP, AnalyticsManager.Label.OB_CLOSE_BUTTON_CLICKED, null);
                        break;
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                stopEmoticonAnimation();
            }
        });

        // Analytics
        switch (reviewItem.serviceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_SATISFACTIONEVALUATION, null);
                break;

            case GOURMET:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_SATISFACTIONEVALUATION, null);
                break;

            case OB_STAY:
                AnalyticsManager.getInstance(ReviewActivity.this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_SATISFACTIONEVALUATION_OUTBOUND, null);
                break;
        }

        try
        {
            mDialog.setContentView(view);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private ReviewLayout.OnEventListener mOnEventListener = new ReviewLayout.OnEventListener()
    {
        private void sendMessageDelayed(int position)
        {
            mHandler.removeMessages(REQUEST_NEXT_FOCUS);

            Message message = new Message();
            message.what = REQUEST_NEXT_FOCUS;
            message.arg1 = position;

            mHandler.sendMessageDelayed(message, 500);
        }

        @Override
        public void onReviewScoreTypeClick(int position, int reviewScore)
        {
            setConfirmTextView();

            if (mReviewLayout.hasUncheckedReview() == true)
            {

                sendMessageDelayed(position);
            }
        }

        @Override
        public void onReviewPickTypeClick(int position, int selectedType)
        {
            setConfirmTextView();

            if (mReviewLayout.hasUncheckedReview() == true)
            {
                sendMessageDelayed(position);
            }
        }

        @Override
        public void onReviewCommentClick(int position, String comment)
        {
            try
            {
                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, mReview.getReviewItem().itemName);

                switch (mReview.getReviewItem().serviceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.REVIEW_WRITE_CLICKED, params);
                        break;

                    case GOURMET:
                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.REVIEW_WRITE_CLICKED, params);
                        break;

                    case OB_STAY:
                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL_OB, AnalyticsManager.Label.REVIEW_WRITE_CLICKED, params);
                        break;
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            Intent intent = WriteReviewCommentActivity.newInstance(ReviewActivity.this, mReview.getReviewItem().serviceType, mReview.getReviewItem().itemName, comment);
            startActivityForResult(intent, REQUEST_ACTIVITY_WRITE_REVIEW_COMMENT);
        }

        @Override
        public void onConfirmClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            JSONArray scoreReviewJSONArray = null, pickReviewJSONArray = null;
            String comment = null;
            int position = 0;

            // Score
            ArrayList<ReviewScoreQuestion> reviewScoreQuestionList = mReview.getReviewScoreQuestionList();

            if (reviewScoreQuestionList != null)
            {
                scoreReviewJSONArray = new JSONArray();

                for (ReviewScoreQuestion reviewScoreQuestion : reviewScoreQuestionList)
                {
                    try
                    {
                        Object value = mReviewLayout.getReviewValue(position++);

                        if (value == null || value instanceof Integer == false)
                        {
                            continue;
                        }

                        JSONObject jsonObject = reviewScoreQuestion.toReviewAnswerJSONObject((int) value);

                        if (jsonObject != null)
                        {
                            scoreReviewJSONArray.put(jsonObject);
                        }
                    } catch (JSONException e)
                    {
                        ExLog.e(e.toString());
                    }
                }
            }

            // Pick
            ArrayList<ReviewPickQuestion> reviewPickQuestionList = mReview.getReviewPickQuestionList();

            if (reviewPickQuestionList != null)
            {
                pickReviewJSONArray = new JSONArray();

                for (ReviewPickQuestion reviewPickQuestion : reviewPickQuestionList)
                {
                    try
                    {
                        Object value = mReviewLayout.getReviewValue(position++);

                        if (value == null || value instanceof Integer == false)
                        {
                            continue;
                        }

                        JSONObject jsonObject = reviewPickQuestion.toReviewAnswerJSONObject((int) value - 1);

                        if (jsonObject != null)
                        {
                            pickReviewJSONArray.put(jsonObject);
                        }
                    } catch (JSONException e)
                    {
                        ExLog.e(e.toString());
                    }
                }
            }

            // Comment
            Object value = mReviewLayout.getReviewValue(position++);

            if (value != null && value instanceof String == true)
            {
                comment = (String) value;
            }

            JSONObject jsonObject = mReview.toReviewDetailJSONObject(scoreReviewJSONArray, pickReviewJSONArray, comment);

            if (jsonObject == null)
            {
                restartExpiredSession();
                return;
            }

            try
            {
                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, mReview.getReviewItem().itemName);
                params.put(AnalyticsManager.KeyType.GRADE, mReviewGrade);

                switch (mReview.getReviewItem().serviceType)
                {
                    case HOTEL:
                        mReviewNetworkController.requestAddReviewDetailInformation(jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.SUBMIT, params);
                        break;

                    case GOURMET:
                        mReviewNetworkController.requestAddReviewDetailInformation(jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL, AnalyticsManager.Label.SUBMIT, params);
                        break;

                    case OB_STAY:
                        mReviewNetworkController.requestStayOutboundAddReviewDetailInformation(mReview.reserveIdx, jsonObject);

                        AnalyticsManager.getInstance(ReviewActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_DETAIL_OB, AnalyticsManager.Label.SUBMIT, params);
                        break;
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onBackPressed()
        {
            ReviewActivity.this.onBackPressed();
        }

        @Override
        public void onReviewDetailAnimationEnd()
        {
            unLockUI();
            mReviewLayout.startAnimation();
        }

        @Override
        public void finish()
        {
            ReviewActivity.this.finish();
        }
    };

    private ReviewNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new ReviewNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onAddReviewInformation(String grade)
        {
            unLockUI();

            mReviewGrade = grade;

            if (Review.GRADE_NONE.equalsIgnoreCase(grade) == true)
            {
                DailyToast.showToast(ReviewActivity.this, R.string.message_review_toast_canceled_review, Toast.LENGTH_SHORT);
                finish();
            } else
            {
                setResult(RESULT_OK);

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showReviewDetail();

                        hideReviewDialog();

                        mReviewLayout.showReviewDetailAnimation();
                    }
                }, 1000);
            }
        }

        @Override
        public void onAddReviewDetailInformation()
        {
            DailyToast.showToast(ReviewActivity.this, R.string.toast_msg_thanks_to_your_opinion, Toast.LENGTH_SHORT);

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mReviewLayout.hideReviewDetailAnimation();
                }
            }, 1000);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            ReviewActivity.this.onError(call, e, onlyReport);

            if (Review.GRADE_NONE.equalsIgnoreCase(mReviewGrade) == true)
            {
                finish();
            }
        }

        @Override
        public void onError(Throwable e)
        {
            ReviewActivity.this.onError(e);

            if (Review.GRADE_NONE.equalsIgnoreCase(mReviewGrade) == true)
            {
                finish();
            }
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            if (Review.GRADE_NONE.equalsIgnoreCase(mReviewGrade) == true)
            {
                ReviewActivity.this.onErrorPopupMessage(msgCode, message);
            } else
            {
                ReviewActivity.this.onErrorPopupMessage(msgCode, message, null);
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            ReviewActivity.this.onErrorToastMessage(message);

            if (Review.GRADE_NONE.equalsIgnoreCase(mReviewGrade) == true)
            {
                finish();
            }
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            ReviewActivity.this.onErrorResponse(call, response);

            if (Review.GRADE_NONE.equalsIgnoreCase(mReviewGrade) == true)
            {
                finish();
            }
        }
    };
}
