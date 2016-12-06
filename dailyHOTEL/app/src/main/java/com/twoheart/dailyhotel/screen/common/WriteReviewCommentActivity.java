package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 12. 2..
 */

public class WriteReviewCommentActivity extends BaseActivity
{
    private WriteReviewCommentLayout mLayout;

    public static Intent newInstance(Context context, String text) throws IllegalArgumentException
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        mLayout = new WriteReviewCommentLayout(this, mEventListener);

        setContentView(mLayout.onCreateView(R.layout.activity_write_review_comment));

        String text;
        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT) == true)
            {
                text = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT);
            } else
            {
                text = "";
            }
        } else
        {
            text = "";
        }
        mLayout.setData(text);
    }

    @Override
    public void finish()
    {
        setResult(RESULT_OK);

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
            finish();
        }

        @Override
        public void onBackClick(String text)
        {
            if (Util.isTextEmpty(text) == true)
            {
                setResult(RESULT_CANCELED);
                WriteReviewCommentActivity.this.finish();
            } else
            {

                WriteReviewCommentActivity.this.showSimpleDialog( //
                    getResources().getString(R.string.label_write_review_comment_dialog_title), //
                    getResources().getString(R.string.label_write_review_comment_dialog_message), //
                    getResources().getString(R.string.dialog_btn_text_confirm), //
                    getResources().getString(R.string.dialog_btn_text_cancel), //
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

        @Override
        public void finish()
        {
            setResult(RESULT_CANCELED);
            WriteReviewCommentActivity.this.finish();
        }
    };
}
