package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.RecentlyColumns;
import com.twoheart.dailyhotel.BuildConfig;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class RecentlyList implements RecentlyColumns
{
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".dailycontentprovider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/recently");


}
