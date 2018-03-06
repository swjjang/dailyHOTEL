package com.daily.dailyhotel.repository.local.model;

import android.net.Uri;

import com.daily.dailyhotel.domain.GourmetRecentlySuggestColumns;
import com.twoheart.dailyhotel.BuildConfig;

/**
 * Created by android_sam on 2018. 3. 6..
 */

public class GourmetRecentlySuggestList implements GourmetRecentlySuggestColumns
{
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".dailycontentprovider";

    public static final Uri NOTIFICATION_URI = Uri.parse("content://" + AUTHORITY + "/suggest/gourmet_ib");
}
