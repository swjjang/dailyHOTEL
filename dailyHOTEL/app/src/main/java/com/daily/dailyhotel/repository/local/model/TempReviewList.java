package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.TempReviewColumns;
import com.twoheart.dailyhotel.BuildConfig;

/**
 * Created by android_sam on 2017. 9. 11..
 */

public class TempReviewList implements TempReviewColumns
{
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".dailycontentprovider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/temp_review");
}
