package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;
import android.provider.BaseColumns;

import com.daily.dailyhotel.domain.RecentlyColumns;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class RecentlyList implements RecentlyColumns, BaseColumns
{
    private static final String AUTHORITY = RecentlyList.class.getPackage().getName();

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/recently");


}
