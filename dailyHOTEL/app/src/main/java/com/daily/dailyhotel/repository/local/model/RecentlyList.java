package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.RecentlyColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class RecentlyList implements RecentlyColumns
{
    private static final String AUTHORITY = "com.twoheart.dailyhotel.DailyContentProvider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/recently");


}
