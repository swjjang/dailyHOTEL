package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

/**
 * Created by android_sam on 2016. 12. 2..
 */

public class WriteReviewCommentActivity extends BaseActivity
{
    WriteReviewCommentLayout mLayout;
    private String mOriginText; // 리뷰 페이지로 부터 전달 받은 메세지 - 처음 진입 인지 수정 상태인지 판단 용도!
    PlaceType mPlaceType;

    public static Intent newInstance(Context context, PlaceType placeType, String text) throws IllegalArgumentException
    {
        if (context == null)
        {
            throw new IllegalArgumentException();
        }

        if (Util.isTextEmpty(text) == true)
        {
            text = "";
        }

        Intent intent = new Intent(context, WriteReviewCommentActivity.class);

        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT, text);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        mLayout = new WriteReviewCommentLayout(WriteReviewCommentActivity.this, mEventListener);

        setContentView(mLayout.onCreateView(R.layout.activity_write_review_comment));

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT) == true)
            {
                mOriginText = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT);
            } else
            {
                mOriginText = "";
            }

            String placeTypeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE);
            if (Util.isTextEmpty(placeTypeName) == false)
            {
                try
                {
                    mPlaceType = PlaceType.valueOf(placeTypeName);
                } catch (Exception e)
                {
                    ExLog.d(e.getMessage());
                }
            }
        } else
        {
            mOriginText = "";
        }

        mLayout.setData(mPlaceType, mOriginText);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Analytics
        switch (mPlaceType)
        {
            case HOTEL:
                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_REVIEWWRITE, null);
                break;

            case FNB:
                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_REVIEWWRITE, null);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        String reviewText = mLayout.getReviewText();

        if (Util.isTextEmpty(mOriginText) == true)
        {
            // 최초 입력 일때
            if (Util.isTextEmpty(reviewText) == true)
            {
                // 입력 데이터가 아무 것도 없을때 그냥 종료!
                setResult(RESULT_CANCELED);
                WriteReviewCommentActivity.this.finish();
            } else
            {
                // 입력 데이터 수정되어 팝업 생성!
                WriteReviewCommentActivity.this.showSimpleDialog( //
                    getResources().getString(R.string.label_write_review_comment_delete_dialog_title), //
                    getResources().getString(R.string.label_write_review_comment_delete_dialog_message), //
                    getResources().getString(R.string.dialog_btn_text_yes), //
                    getResources().getString(R.string.dialog_btn_text_no), //
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setResult(RESULT_CANCELED);
                            WriteReviewCommentActivity.this.finish();

                            try
                            {
                                switch (mPlaceType)
                                {
                                    case HOTEL:
                                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CANCEL, null);
                                        break;

                                    case FNB:
                                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CANCEL, null);
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
                            mLayout.showKeyboard();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mLayout.showKeyboard();
                        }
                    }, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            releaseUiComponent();
                        }
                    }, true);
            }
        } else
        {
            // 기존 입력 텍스트 있을때
            if (mOriginText.equalsIgnoreCase(reviewText) == true)
            {
                // 기존 입력 값과 같을 경우 바로 종료
                setResult(RESULT_CANCELED);
                WriteReviewCommentActivity.this.finish();
            } else
            {
                // 입력 데이터가 수정되어 팝업 생성!
                WriteReviewCommentActivity.this.showSimpleDialog( //
                    getResources().getString(R.string.label_write_review_comment_modify_dialog_title), //
                    getResources().getString(R.string.label_write_review_comment_modify_dialog_message), //
                    getResources().getString(R.string.dialog_btn_text_yes), //
                    getResources().getString(R.string.dialog_btn_text_no), //
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            setResult(RESULT_CANCELED);
                            WriteReviewCommentActivity.this.finish();

                            try
                            {
                                switch (mPlaceType)
                                {
                                    case HOTEL:
                                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CANCEL, null);
                                        break;

                                    case FNB:
                                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CANCEL, null);
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
                            mLayout.showKeyboard();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mLayout.showKeyboard();
                        }
                    }, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            releaseUiComponent();
                        }
                    }, true);
            }
        }

        try
        {
            switch (mPlaceType)
            {
                case HOTEL:
                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                        , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.BACK, null);
                    break;

                case FNB:
                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                        , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.BACK, null);
                    break;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    private WriteReviewCommentLayout.OnEventListener mEventListener = new WriteReviewCommentLayout.OnEventListener()
    {
        @Override
        public void onCompleteClick(String text)
        {
            try
            {
                switch (mPlaceType)
                {
                    case HOTEL:
                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.HOTEL_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CONFIRM, null);
                        break;

                    case FNB:
                        AnalyticsManager.getInstance(WriteReviewCommentActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_SATISFACTIONEVALUATION//
                            , AnalyticsManager.Action.REVIEW_WRITE, AnalyticsManager.Label.CONFIRM, null);
                        break;
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            Intent intent = new Intent();
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT, Util.trim(text));
            setResult(RESULT_OK, intent);
            WriteReviewCommentActivity.this.finish();
        }

        @Override
        public void onBackPressed()
        {
            WriteReviewCommentActivity.this.onBackPressed();
        }

        @Override
        public void finish()
        {
            setResult(RESULT_CANCELED);
            WriteReviewCommentActivity.this.finish();
        }
    };
}
