package com.daily.dailyhotel.storage.database;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class DailyDbHelper
{
    private DailyDb mDb;
    private int mRefCount;

    private static final class DailyDbHelperHolder
    {
        static final DailyDbHelper sInstance = new DailyDbHelper();
    }

    DailyDbHelper()
    {

    }

    public static DailyDbHelper getInstance()
    {
        return DailyDbHelperHolder.sInstance;
    }

    public synchronized DailyDb open(Context context)
    {
        if (mDb == null)
        {
            try
            {
                mDb = new DailyDb(context);
            } finally
            {
                ExLog.v("database created");
            }
        }

        ++mRefCount;

        ExLog.v("refCount : " + mRefCount);

        return mDb;
    }

    public synchronized void close()
    {
        if (mRefCount <= 0)
        {
            ExLog.v("already closed");
            if (Constants.DEBUG == true)
            {
                throw new IllegalStateException("database already closed");
            }

            return;
        }

        --mRefCount;

        ExLog.v("refCount : " + mRefCount);

        if (mRefCount == 0 && mDb != null)
        {
            try
            {
                mDb.close();
            } finally
            {
                mDb = null;

                ExLog.v("database closed");
            }
        }
    }
}
