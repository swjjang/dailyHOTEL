package com.twoheart.dailyhotel.screen.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 12. 2..
 */

public class WriteReviewCommentActivity extends BaseActivity
{
    private WriteReviewCommentLayout mLayout;
    private String mOriginText; // 리뷰 페이지로 부터 전달 받은 메세지 - 처음 진입 인지 수정 상태인지 판단 용도!
    private PlaceType mPlaceType;

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
            Intent intent = new Intent();
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT, text);
            setResult(RESULT_OK, intent);
            WriteReviewCommentActivity.this.finish();
        }

        @Override
        public void onBackClick(String text)
        {
            if (Util.isTextEmpty(mOriginText) == true)
            {
                // 최초 입력 일때
                if (Util.isTextEmpty(text) == true)
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
                            }
                        }, null);
                }
            } else
            {
                // 기존 입력 텍스트 있을때
                if (mOriginText.equalsIgnoreCase(text) == true)
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
                            }
                        }, null);
                }
            }
        }

        @Override
        public void finish()
        {
            setResult(RESULT_CANCELED);
            WriteReviewCommentActivity.this.finish();
        }
    };
}
