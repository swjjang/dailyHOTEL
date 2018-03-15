package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.SearchResultHistoryColumns;
import com.twoheart.dailyhotel.BuildConfig;

/**
 * Created by android_sam on 2018. 3. 15..
 */

public class SearchResultHistoryList implements SearchResultHistoryColumns
{
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".dailycontentprovider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/search/history");
}
