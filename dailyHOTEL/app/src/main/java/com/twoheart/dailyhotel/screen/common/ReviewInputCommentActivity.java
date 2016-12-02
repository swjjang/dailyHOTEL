package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;

import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by android_sam on 2016. 12. 2..
 */

public class ReviewInputCommentActivity extends BaseActivity
{
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

        Intent intent = new Intent(context, ReviewInputCommentActivity.class);

        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_REVIEW_COMMENT, text);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }


}
