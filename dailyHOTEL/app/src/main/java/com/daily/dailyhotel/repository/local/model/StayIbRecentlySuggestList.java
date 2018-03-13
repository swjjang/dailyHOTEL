package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.StayIbRecentlySuggestColumns;
import com.twoheart.dailyhotel.BuildConfig;

/**
 * Created by android_sam on 2018. 3. 12..
 */

public class StayIbRecentlySuggestList implements StayIbRecentlySuggestColumns
{
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".dailycontentprovider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/suggest/stay_ib");
}
