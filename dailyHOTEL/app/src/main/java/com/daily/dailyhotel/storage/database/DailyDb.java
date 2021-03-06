package com.daily.dailyhotel.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyColumns;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.repository.local.model.GourmetRecentlySuggestList;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.repository.local.model.SearchResultHistoryList;
import com.daily.dailyhotel.repository.local.model.StayIbRecentlySuggestList;
import com.daily.dailyhotel.repository.local.model.StayObRecentlySuggestList;
import com.daily.dailyhotel.repository.local.model.TempReviewList;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.util.Calendar;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class DailyDb extends SQLiteOpenHelper implements BaseColumns
{
    private static final int DATABASE_VERSION = 5;

    public static final int MAX_RECENT_PLACE_COUNT = 30;

    private static boolean mIsTest = true;

    private Context mContext;
    private boolean mIsClosing;

    public static final String DB_NAME = "daily.db";

    public static final String T_RECENTLY = "recently";
    public static final String T_STAY_OB_RECENTLY_SUGGEST = "stay_ob_recently_suggest";
    public static final String T_TEMP_REVIEW = "temp_review";
    public static final String T_STAY_IB_RECENTLY_SUGGEST = "stay_ib_recently_suggest";
    public static final String T_GOURMET_IB_RECENTLY_SUGGEST = "gourmet_ib_recently_suggest";
    public static final String T_SEARCH_RESULT_HISTORY = "search_result_history";

    // added database version 1 and change version 5 (added field REGION_NAME)
    private static final String CREATE_T_RECENTLY = "CREATE TABLE IF NOT EXISTS " + T_RECENTLY + " (" //
        + RecentlyList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + RecentlyList.PLACE_INDEX + " INTEGER NOT NULL UNIQUE DEFAULT 0, " //
        + RecentlyList.NAME + " TEXT, " //
        + RecentlyList.ENGLISH_NAME + " TEXT, " //
        + RecentlyList.SERVICE_TYPE + " TEXT NOT NULL, " // ServiceType.name() 으로 저장 예정 HOTEL, OB_STAY, GOURMET
        + RecentlyList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + RecentlyList.REGION_NAME + " TEXT, " //
        + RecentlyList.IMAGE_URL + " TEXT " + ");";

    // added database version 3 and change version 5 (added field DISPLAY_TEXT) drop and create
    private static final String CREATE_T_STAY_OB_RECENTLY_SUGGEST = "CREATE TABLE IF NOT EXISTS " + T_STAY_OB_RECENTLY_SUGGEST + " (" //
        + StayObRecentlySuggestList._ID + " INTEGER  PRIMARY KEY NOT NULL, " //
        + StayObRecentlySuggestList.SUGGEST + " TEXT NOT NULL, " //
        + StayObRecentlySuggestList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + StayObRecentlySuggestList.KEYWORD + " TEXT " + ");";

    // added database version 4
    private static final String CREATE_T_TEMP_REVIEW = "CREATE TABLE IF NOT EXISTS " + T_TEMP_REVIEW + " (" //
        + TempReviewList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + TempReviewList.RESERVATION_INDEX + " INTEGER NOT NULL UNIQUE DEFAULT 0, " //
        + TempReviewList.SERVICE_TYPE + " TEXT NOT NULL, " //
        + TempReviewList.START_DATE + " TEXT, " //
        + TempReviewList.END_DATE + " TEXT, " //
        + TempReviewList.SCORE_QUESTION + " TEXT, " //
        + TempReviewList.PICK_QUESTION + " TEXT, " //
        + TempReviewList.COMMENT + " TEXT " + ");";

    // added database version 5
    private static final String CREATE_T_GOURMET_RECENTLY_SUGGEST = "CREATE TABLE IF NOT EXISTS " + T_GOURMET_IB_RECENTLY_SUGGEST + " (" //
        + GourmetRecentlySuggestList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + GourmetRecentlySuggestList.TYPE + " TEXT NOT NULL, " //
        + GourmetRecentlySuggestList.DISPLAY + " TEXT NOT NULL, " //
        + GourmetRecentlySuggestList.SUGGEST + " TEXT NOT NULL, " //
        + GourmetRecentlySuggestList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + GourmetRecentlySuggestList.KEYWORD + " TEXT " + ");";

    // added database version 5
    private static final String CREATE_T_STAY_IB_RECENTLY_SUGGEST = "CREATE TABLE IF NOT EXISTS " + T_STAY_IB_RECENTLY_SUGGEST + " (" //
        + StayIbRecentlySuggestList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + StayIbRecentlySuggestList.TYPE + " TEXT NOT NULL, " //
        + StayIbRecentlySuggestList.DISPLAY + " TEXT NOT NULL, " //
        + StayIbRecentlySuggestList.SUGGEST + " TEXT NOT NULL, " //
        + StayIbRecentlySuggestList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + StayIbRecentlySuggestList.KEYWORD + " TEXT " + ");";

    // added database version 5
    private static final String CREATE_T_SEARCH_RESULT_HISTORY = "CREATE TABLE IF NOT EXISTS " + T_SEARCH_RESULT_HISTORY + " (" //
        + SearchResultHistoryList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + SearchResultHistoryList.SERVICE_TYPE + " TEXT NOT NULL, " // ServiceType.name() 으로 저장 예정 HOTEL, OB_STAY, GOURMET
        + SearchResultHistoryList.DISPLAY_NAME + " TEXT NOT NULL, " //
        + SearchResultHistoryList.START_DATE + " TEXT NOT NULL, " //
        + SearchResultHistoryList.END_DATE + " TEXT, " //
        + SearchResultHistoryList.SUGGEST + " TEXT, " // StaySuggest, GourmetSuggest, StayOutboundSuggest - json String 예정
        + SearchResultHistoryList.ADULT_COUNT + " INTEGER NOT NULL DEFAULT 0, " //
        + SearchResultHistoryList.START_DATE_TIME + " INTEGER NOT NULL DEFAULT 0, " //
        + SearchResultHistoryList.SAVING_TIME + " INTEGER NOT NULL DEFAULT 0, " //
        + SearchResultHistoryList.CHILD_AGE_LIST + " TEXT " + ");";

    public DailyDb(Context context)
    {
        super(context, DB_NAME, null, DATABASE_VERSION);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        ExLog.v("version : " + db.getVersion());

        upGradeAllDb(db);
    }

    @Override
    public synchronized void close()
    {
        mIsClosing = true;
        super.close();
        mIsClosing = false;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        ExLog.v("Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion >= DATABASE_VERSION)
        {
            return;
        }

        if (oldVersion <= 4)
        {
            upGradeGourmetRecentlySuggestDb(db);
            upGradeStayIbRecentlySuggestDb(db);
            upGradeSearchResultHistoryDb(db);
            upGradeStayObRecentlySuggestDb(db);
            upGradeRecentlyPlaceDb(db);
        }

        if (oldVersion <= 3)
        {
            upGradeTempReviewDb(db);
        }
    }

    private void upGradeAllDb(SQLiteDatabase db)
    {
        upGradeRecentlyPlaceDb(db);
        upGradeStayObRecentlySuggestDb(db);
        upGradeTempReviewDb(db);
        upGradeGourmetRecentlySuggestDb(db);
        upGradeStayIbRecentlySuggestDb(db);
        upGradeSearchResultHistoryDb(db);
    }

    public void upGradeRecentlyPlaceDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_RECENTLY);
        db.execSQL(CREATE_T_RECENTLY);
    }

    public void upGradeStayObRecentlySuggestDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_STAY_OB_RECENTLY_SUGGEST);
        db.execSQL(CREATE_T_STAY_OB_RECENTLY_SUGGEST);
    }

    private void upGradeTempReviewDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_TEMP_REVIEW);
        db.execSQL(CREATE_T_TEMP_REVIEW);
    }

    public void upGradeGourmetRecentlySuggestDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_GOURMET_IB_RECENTLY_SUGGEST);
        db.execSQL(CREATE_T_GOURMET_RECENTLY_SUGGEST);
    }

    public void upGradeStayIbRecentlySuggestDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_STAY_IB_RECENTLY_SUGGEST);
        db.execSQL(CREATE_T_STAY_IB_RECENTLY_SUGGEST);
    }

    public void upGradeSearchResultHistoryDb(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_SEARCH_RESULT_HISTORY);
        db.execSQL(CREATE_T_SEARCH_RESULT_HISTORY);
    }

    private SQLiteDatabase getDb()
    {
        SQLiteDatabase db = null;

        try
        {
            db = getWritableDatabase();
        } catch (SQLiteException e)
        {
            ExLog.e(e.toString());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        return db;
    }

    public long insert(String table, ContentValues contentValues)
    {
        long rowId = -1;
        SQLiteDatabase db = getDb();

        if (db != null)
        {
            try
            {
                rowId = db.insert(table, null, contentValues);
            } catch (SQLiteConstraintException e)
            {
                // 무결성 예약 조건 위합 에러 시 -1 리턴
                ExLog.e(e.toString());
            }
        }

        return rowId;
    }

    public synchronized void insertOrUpdate(String table, String keyId, ContentValues contentValues)
    {
        SQLiteDatabase db = getDb();
        if (db != null)
        {
            if (contentValues.containsKey(keyId) == false)
            {
                insert(table, contentValues);
                ExLog.d("INSERT : ");
                return;
            }

            int rowId = db.update(table, contentValues, keyId + "=?", new String[]{String.valueOf(contentValues.get(keyId))});
            if (rowId > 0)
            {
                ExLog.d("UPDATE : ");
            } else
            {
                insert(table, contentValues);
                ExLog.d("INSERT : ");
            }
        }
    }

    public Cursor rawQuery(String sql)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        ExLog.d("rawQuery::sql = " + sql);

        return db.rawQuery(sql, null);
    }

    public void execQuery(String sql)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return;
        }

        db.execSQL(sql);
    }

    public Cursor getRecentlyPlaces(int limit, Constants.ServiceType... serviceTypes)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_RECENTLY);

        if (serviceTypes != null && serviceTypes.length > 0)
        {
            sqlBuilder.append(" WHERE ");

            for (int i = 0; i < serviceTypes.length; i++)
            {
                if (i > 0)
                {
                    sqlBuilder.append(" OR ");
                }

                sqlBuilder.append(RecentlyColumns.SERVICE_TYPE).append("=\"").append(serviceTypes[i].name()).append("\"");
            }
        }

        sqlBuilder.append(" ORDER BY ").append(RecentlyColumns.SAVING_TIME).append(" DESC");

        if (limit > 0)
        {
            sqlBuilder.append(" limit ").append(limit);
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public Cursor getRecentlyPlace(Constants.ServiceType serviceType, int index)
    {
        if (index <= 0)
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_RECENTLY);

        if (serviceType != null)
        {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(RecentlyColumns.SERVICE_TYPE).append("=\"").append(serviceType.name()).append("\"");
            sqlBuilder.append(" AND ").append(RecentlyColumns.PLACE_INDEX).append("=").append(index).append("");
        } else
        {
            sqlBuilder.append(" WHERE ").append(RecentlyColumns.PLACE_INDEX).append("=").append(index).append("");
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public long checkExistRecentPlace(Constants.ServiceType serviceType, int index)
    {
        if (index <= 0)
        {
            return -1;
        }

        Cursor cursor = null;

        try
        {
            cursor = getRecentlyPlace(serviceType, index);

            if (cursor != null)
            {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(RecentlyColumns.SAVING_TIME);
                return cursor.getLong(columnIndex);
            }

        } catch (Exception e)
        {
            return -1;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return -1;
    }

    public long getOldestSavingTime(Constants.ServiceType... serviceTypes)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return -1;
        }

        Cursor cursor = null;
        long savingTime = -1;

        try
        {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
            sqlBuilder.append(T_RECENTLY);

            if (serviceTypes != null && serviceTypes.length > 0)
            {
                sqlBuilder.append(" WHERE ");

                for (int i = 0; i < serviceTypes.length; i++)
                {
                    if (i > 0)
                    {
                        sqlBuilder.append(" OR ");
                    }

                    sqlBuilder.append(RecentlyColumns.SERVICE_TYPE).append("=\"").append(serviceTypes[i].name()).append("\"");
                }
            }

            sqlBuilder.append(" ORDER BY ").append(RecentlyColumns.SAVING_TIME).append(" ASC");
            sqlBuilder.append(" LIMIT ").append(1);

            cursor = rawQuery(sqlBuilder.toString());
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(RecentlyColumns.SAVING_TIME);
                savingTime = cursor.getLong(columnIndex);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            savingTime = -1;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return savingTime;
    }

    public void addRecentlyPlace(final Constants.ServiceType serviceType, int index, String name //
        , String englishName, String imageUrl, String regionName, boolean isUpdateDate)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long savingTime;

            if (isUpdateDate == true)
            {
                Calendar calendar = DailyCalendar.getInstance();
                savingTime = calendar.getTimeInMillis();
            } else
            {
                savingTime = checkExistRecentPlace(serviceType, index);

                if (savingTime == -1)
                {
                    Calendar calendar = DailyCalendar.getInstance();
                    savingTime = calendar.getTimeInMillis();
                }
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(RecentlyColumns.PLACE_INDEX, index);
            contentValues.put(RecentlyColumns.NAME, name);
            contentValues.put(RecentlyColumns.ENGLISH_NAME, englishName);
            contentValues.put(RecentlyColumns.SERVICE_TYPE, serviceType == null ? "" : serviceType.name());
            contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
            contentValues.put(RecentlyColumns.IMAGE_URL, imageUrl);
            contentValues.put(RecentlyColumns.REGION_NAME, regionName);

            db.beginTransaction();

            insertOrUpdate(T_RECENTLY, RecentlyColumns.PLACE_INDEX, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        maintainMaxRecentlyItem(serviceType);

        mContext.getContentResolver().notifyChange(RecentlyList.NOTIFICATION_URI, null);
    }

    public void maintainMaxRecentlyItem(Constants.ServiceType serviceType)
    {
        Cursor cursor = null;

        long savingTime = -1;

        try
        {
            cursor = getRecentlyPlaces(-1, serviceType);
            if (cursor != null && cursor.getCount() > MAX_RECENT_PLACE_COUNT)
            {
                cursor.moveToPosition(MAX_RECENT_PLACE_COUNT);
                int columnIndex = cursor.getColumnIndex(RecentlyList.SAVING_TIME);
                savingTime = cursor.getLong(columnIndex);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing
            }
        }

        if (savingTime <= 0)
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_RECENTLY, RecentlyList.SAVING_TIME + " <= " + savingTime, null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }
    }

    public void deleteRecentlyItem(Constants.ServiceType serviceType, int index)
    {
        if (serviceType == null || index <= 0)
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_RECENTLY, RecentlyList.PLACE_INDEX + " = " + index //
                + " AND " + RecentlyList.SERVICE_TYPE + " = '" + serviceType.name() + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(RecentlyList.NOTIFICATION_URI, null);
    }

    public void deleteAllRecentlyItem(Constants.ServiceType serviceType)
    {
        if (serviceType == null)
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_RECENTLY, RecentlyList.SERVICE_TYPE + " = '" + serviceType.name() + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(RecentlyList.NOTIFICATION_URI, null);
    }

    private ContentValues convertContentValues(RecentlyPlace recentlyPlace, long savingTime)
    {
        if (recentlyPlace == null)
        {
            return null;
        }

        if (savingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentlyColumns.PLACE_INDEX, recentlyPlace.index);
        contentValues.put(RecentlyColumns.NAME, recentlyPlace.title);
        contentValues.put(RecentlyColumns.ENGLISH_NAME, "");
        contentValues.put(RecentlyColumns.SERVICE_TYPE, recentlyPlace.serviceType);
        contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
        contentValues.put(RecentlyColumns.IMAGE_URL, recentlyPlace.imageUrl);

        return contentValues;
    }

    private ContentValues convertContentValues(Stay stay, long savingTime)
    {
        if (stay == null)
        {
            return null;
        }

        if (savingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentlyColumns.PLACE_INDEX, stay.index);
        contentValues.put(RecentlyColumns.NAME, stay.name);
        contentValues.put(RecentlyColumns.ENGLISH_NAME, "");
        contentValues.put(RecentlyColumns.SERVICE_TYPE, Constants.ServiceType.HOTEL.name());
        contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
        contentValues.put(RecentlyColumns.IMAGE_URL, stay.imageUrl);

        return contentValues;
    }

    private ContentValues convertContentValues(Gourmet gourmet, long savingTime)
    {
        if (gourmet == null)
        {
            return null;
        }

        if (savingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentlyColumns.PLACE_INDEX, gourmet.index);
        contentValues.put(RecentlyColumns.NAME, gourmet.name);
        contentValues.put(RecentlyColumns.ENGLISH_NAME, "");
        contentValues.put(RecentlyColumns.SERVICE_TYPE, Constants.ServiceType.GOURMET.name());
        contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
        contentValues.put(RecentlyColumns.IMAGE_URL, gourmet.imageUrl);

        return contentValues;
    }

    private ContentValues convertContentValues(StayOutbound stayOutbound, long savingTime)
    {
        if (stayOutbound == null)
        {
            return null;
        }

        if (savingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentlyColumns.PLACE_INDEX, stayOutbound.index);
        contentValues.put(RecentlyColumns.NAME, stayOutbound.name);
        contentValues.put(RecentlyColumns.ENGLISH_NAME, stayOutbound.nameEng);
        contentValues.put(RecentlyColumns.SERVICE_TYPE, Constants.ServiceType.OB_STAY.name());
        contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);

        ImageMap imageMap = stayOutbound.getImageMap();
        String imageUrl = imageMap == null ? "" : imageMap.smallUrl;
        contentValues.put(RecentlyColumns.IMAGE_URL, imageUrl);

        return contentValues;
    }

    public Cursor getStayObRecentlySuggestList(int limit)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_STAY_OB_RECENTLY_SUGGEST);

        sqlBuilder.append(" ORDER BY ").append(StayObRecentlySuggestColumns.SAVING_TIME).append(" DESC");

        if (limit > 0)
        {
            sqlBuilder.append(" limit ").append(limit);
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public Cursor getStayObRecentlySuggest(long _id)
    {
        if (_id <= 0)
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_STAY_OB_RECENTLY_SUGGEST);
        sqlBuilder.append(" WHERE ").append(StayObRecentlySuggestColumns._ID).append("=").append(_id).append("");

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public String getStayObRecentlySuggestKeyword(long _id)
    {
        if (_id <= 0)
        {
            return null;
        }

        Cursor cursor = null;

        try
        {
            cursor = getStayObRecentlySuggest(_id);

            if (cursor != null)
            {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(StayObRecentlySuggestColumns.KEYWORD);
                return cursor.getString(columnIndex);
            }

        } catch (Exception e)
        {
            return null;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return null;
    }

    public long checkExistStayObRecentlySuggest(long _id)
    {
        if (_id <= 0)
        {
            return -1;
        }

        Cursor cursor = null;

        try
        {
            cursor = getStayObRecentlySuggest(_id);

            if (cursor != null)
            {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(StayObRecentlySuggestColumns.SAVING_TIME);
                return cursor.getLong(columnIndex);
            }

        } catch (Exception e)
        {
            return -1;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return -1;
    }

    public long getOldestStayObRecentlySuggestSavingTime()
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return -1;
        }

        Cursor cursor = null;
        long savingTime = -1;

        try
        {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
            sqlBuilder.append(T_STAY_OB_RECENTLY_SUGGEST);
            sqlBuilder.append(" ORDER BY ").append(StayObRecentlySuggestColumns.SAVING_TIME).append(" ASC");
            sqlBuilder.append(" LIMIT ").append(1);

            cursor = rawQuery(sqlBuilder.toString());
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(StayObRecentlySuggestColumns.SAVING_TIME);
                savingTime = cursor.getLong(columnIndex);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            savingTime = -1;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return savingTime;
    }

    public void addStayObRecentlySuggest(long _id, String suggestString, String keyword, boolean isUpdateDate)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long savingTime;

            if (isUpdateDate == true)
            {
                Calendar calendar = DailyCalendar.getInstance();
                savingTime = calendar.getTimeInMillis();
            } else
            {
                savingTime = checkExistStayObRecentlySuggest(_id);

                if (savingTime == -1)
                {
                    Calendar calendar = DailyCalendar.getInstance();
                    savingTime = calendar.getTimeInMillis();
                }
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(StayObRecentlySuggestColumns._ID, _id);
            contentValues.put(StayObRecentlySuggestColumns.SUGGEST, suggestString);
            contentValues.put(StayObRecentlySuggestColumns.SAVING_TIME, savingTime);
            contentValues.put(StayObRecentlySuggestColumns.KEYWORD, keyword);

            db.beginTransaction();

            insertOrUpdate(T_STAY_OB_RECENTLY_SUGGEST, StayObRecentlySuggestColumns._ID, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        maintainMaxStayObRecentlySuggest();

        mContext.getContentResolver().notifyChange(StayObRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public void maintainMaxStayObRecentlySuggest()
    {
        Cursor cursor = null;

        long savingTime = -1;

        try
        {
            cursor = getStayObRecentlySuggestList(-1);
            if (cursor != null && cursor.getCount() > MAX_RECENT_PLACE_COUNT)
            {
                cursor.moveToPosition(MAX_RECENT_PLACE_COUNT);
                int columnIndex = cursor.getColumnIndex(StayObRecentlySuggestColumns.SAVING_TIME);
                savingTime = cursor.getLong(columnIndex);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing
            }
        }

        if (savingTime <= 0)
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_STAY_OB_RECENTLY_SUGGEST, StayObRecentlySuggestColumns.SAVING_TIME + " <= " + savingTime, null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }
    }

    public void deleteAllStayObRecentlySuggest()
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_STAY_OB_RECENTLY_SUGGEST, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(StayObRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public void deleteStayObRecentlySuggest(long id)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_STAY_OB_RECENTLY_SUGGEST, StayObRecentlySuggestColumns._ID + " = " + id, null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(StayObRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public long getTempReviewId(int reservationIndex, String serviceType, String startDate, String endDate)
    {
        if (reservationIndex <= 0 || DailyTextUtils.isTextEmpty(startDate))
        {
            return -1;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return -1;
        }

        long id = -1;
        Cursor cursor = null;

        try
        {
            cursor = getTempReview(reservationIndex, serviceType, startDate, endDate);
            if (cursor != null)
            {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(TempReviewList._ID);
                id = cursor.getLong(columnIndex);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            id = -1;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return id;
    }

    public Cursor getTempReview(int reservationIndex, String serviceType, String startDate, String endDate)
    {
        if (reservationIndex <= 0 || DailyTextUtils.isTextEmpty(startDate))
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_TEMP_REVIEW);
        sqlBuilder.append(" WHERE ");

        sqlBuilder.append(TempReviewList.RESERVATION_INDEX).append("=").append(reservationIndex).append("");
        sqlBuilder.append(" AND ").append(TempReviewList.START_DATE).append("=\"").append(startDate).append("\"");

        if (DailyTextUtils.isTextEmpty(endDate) == false)
        {
            sqlBuilder.append(" AND ").append(TempReviewList.END_DATE).append("=\"").append(endDate).append("\"");
        }

        if (DailyTextUtils.isTextEmpty(serviceType) == false)
        {
            sqlBuilder.append(" AND ").append(TempReviewList.SERVICE_TYPE).append("=\"").append(serviceType).append("\"");
        }

        sqlBuilder.append(" ORDER BY ").append(TempReviewList.START_DATE).append(" DESC");
        sqlBuilder.append(" limit ").append(1);

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public void addTempReview(int reservationIndex, String serviceType //
        , String startDate, String endDate, String scoreQuestion, String pickQuestion, String comment)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long oldId = getTempReviewId(reservationIndex, serviceType, startDate, endDate);

            ContentValues contentValues = new ContentValues();
            if (oldId > 0)
            {
                contentValues.put(TempReviewList._ID, oldId);
            }

            contentValues.put(TempReviewList.RESERVATION_INDEX, reservationIndex);
            contentValues.put(TempReviewList.SERVICE_TYPE, serviceType);
            contentValues.put(TempReviewList.START_DATE, startDate);
            contentValues.put(TempReviewList.END_DATE, endDate);
            contentValues.put(TempReviewList.SCORE_QUESTION, scoreQuestion);
            contentValues.put(TempReviewList.PICK_QUESTION, pickQuestion);
            contentValues.put(TempReviewList.COMMENT, comment);

            db.beginTransaction();

            insertOrUpdate(T_TEMP_REVIEW, TempReviewList._ID, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        mContext.getContentResolver().notifyChange(TempReviewList.NOTIFICATION_URI, null);
    }

    public void deleteTempReview(int reservationIndex, String serviceType, String startDate)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_TEMP_REVIEW, TempReviewList.RESERVATION_INDEX + " = " + reservationIndex //
                + " AND " + TempReviewList.SERVICE_TYPE + " = '" + serviceType + "'" //
                + " AND " + TempReviewList.START_DATE + " = '" + startDate + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(TempReviewList.NOTIFICATION_URI, null);
    }

    public void deleteAllTempReview()
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_TEMP_REVIEW, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(TempReviewList.NOTIFICATION_URI, null);
    }

    public Cursor getGourmetRecentlySuggest(String type, String display)
    {
        if (DailyTextUtils.isTextEmpty(type, display))
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_GOURMET_IB_RECENTLY_SUGGEST);
        sqlBuilder.append(" WHERE ").append(GourmetRecentlySuggestList.TYPE).append("=\"").append(type).append("\"");
        sqlBuilder.append(" AND ").append(GourmetRecentlySuggestList.DISPLAY).append("=\"").append(display).append("\"");

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public Cursor getGourmetRecentlySuggestList(int maxCount)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_GOURMET_IB_RECENTLY_SUGGEST);
        sqlBuilder.append(" ORDER BY ").append(GourmetRecentlySuggestList.SAVING_TIME).append(" DESC");

        if (maxCount > 0)
        {
            sqlBuilder.append(" limit ").append(maxCount);
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public void addGourmetRecentlySuggest(String type, String display, String suggestString, String keyword)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long savingTime = -1;
            long oldId = -1;
            Cursor cursor = null;

            try
            {
                cursor = getGourmetRecentlySuggest(type, display);

                if (cursor != null)
                {
                    cursor.moveToFirst();

                    int idColumnIndex = cursor.getColumnIndex(GourmetRecentlySuggestList._ID);
                    oldId = cursor.getLong(idColumnIndex);

                    //                    int savingTimeColumnIndex = cursor.getColumnIndex(GourmetRecentlySuggestList.SAVING_TIME);
                    //                    savingTime = cursor.getLong(savingTimeColumnIndex);
                }

            } catch (Exception e)
            {
                oldId = -1;
                //                savingTime = -1;
            } finally
            {
                try
                {
                    if (cursor != null)
                    {
                        cursor.close();
                    }
                } catch (Exception e)
                {
                    // do nothing!
                }
            }

            //            if (savingTime == -1)
            //            {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
            //            }

            ContentValues contentValues = new ContentValues();

            if (oldId > 0)
            {
                contentValues.put(GourmetRecentlySuggestList._ID, oldId);
            }

            contentValues.put(GourmetRecentlySuggestList.TYPE, type);
            contentValues.put(GourmetRecentlySuggestList.DISPLAY, display);
            contentValues.put(GourmetRecentlySuggestList.SUGGEST, suggestString);
            contentValues.put(GourmetRecentlySuggestList.SAVING_TIME, savingTime);
            contentValues.put(GourmetRecentlySuggestList.KEYWORD, keyword);

            db.beginTransaction();

            insertOrUpdate(T_GOURMET_IB_RECENTLY_SUGGEST, GourmetRecentlySuggestList._ID, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        mContext.getContentResolver().notifyChange(GourmetRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public void deleteGourmetRecentlySuggest(String type, String display)
    {
        if (DailyTextUtils.isTextEmpty(type, display))
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_GOURMET_IB_RECENTLY_SUGGEST, GourmetRecentlySuggestList.TYPE + " = '" + type + "'" //
                + " AND " + GourmetRecentlySuggestList.DISPLAY + " = '" + display + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(GourmetRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public Cursor getStayIbRecentlySuggest(String type, String display)
    {
        if (DailyTextUtils.isTextEmpty(type, display))
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_STAY_IB_RECENTLY_SUGGEST);
        sqlBuilder.append(" WHERE ").append(StayIbRecentlySuggestList.TYPE).append("=\"").append(type).append("\"");
        sqlBuilder.append(" AND ").append(StayIbRecentlySuggestList.DISPLAY).append("=\"").append(display).append("\"");

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public Cursor getStayIbRecentlySuggestList(int maxCount)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_STAY_IB_RECENTLY_SUGGEST);
        sqlBuilder.append(" ORDER BY ").append(StayIbRecentlySuggestList.SAVING_TIME).append(" DESC");

        if (maxCount > 0)
        {
            sqlBuilder.append(" limit ").append(maxCount);
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public void addStayIbRecentlySuggest(String type, String display, String suggestString, String keyword)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long savingTime = -1;
            long oldId = -1;
            Cursor cursor = null;

            try
            {
                cursor = getStayIbRecentlySuggest(type, display);

                if (cursor != null)
                {
                    cursor.moveToFirst();

                    int idColumnIndex = cursor.getColumnIndex(StayIbRecentlySuggestList._ID);
                    oldId = cursor.getLong(idColumnIndex);
                }

            } catch (Exception e)
            {
                oldId = -1;
            } finally
            {
                try
                {
                    if (cursor != null)
                    {
                        cursor.close();
                    }
                } catch (Exception e)
                {
                    // do nothing!
                }
            }

            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();

            ContentValues contentValues = new ContentValues();

            if (oldId > 0)
            {
                contentValues.put(StayIbRecentlySuggestList._ID, oldId);
            }

            contentValues.put(StayIbRecentlySuggestList.TYPE, type);
            contentValues.put(StayIbRecentlySuggestList.DISPLAY, display);
            contentValues.put(StayIbRecentlySuggestList.SUGGEST, suggestString);
            contentValues.put(StayIbRecentlySuggestList.SAVING_TIME, savingTime);
            contentValues.put(StayIbRecentlySuggestList.KEYWORD, keyword);

            db.beginTransaction();

            insertOrUpdate(T_STAY_IB_RECENTLY_SUGGEST, StayIbRecentlySuggestList._ID, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        mContext.getContentResolver().notifyChange(StayIbRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public void deleteStayIbRecentlySuggest(String type, String display)
    {
        if (DailyTextUtils.isTextEmpty(type, display))
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_STAY_IB_RECENTLY_SUGGEST, StayIbRecentlySuggestList.TYPE + " = '" + type + "'" //
                + " AND " + StayIbRecentlySuggestList.DISPLAY + " = '" + display + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(StayIbRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public Cursor getSearchResultHistory(String serviceType, String displayName)
    {
        if (DailyTextUtils.isTextEmpty(serviceType, displayName))
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_SEARCH_RESULT_HISTORY);
        sqlBuilder.append(" WHERE ").append(SearchResultHistoryList.SERVICE_TYPE).append("=\"").append(serviceType).append("\"");
        sqlBuilder.append(" AND ").append(SearchResultHistoryList.DISPLAY_NAME).append("=\"").append(displayName).append("\"");
        sqlBuilder.append(" ORDER BY ").append(SearchResultHistoryList.SAVING_TIME).append(" DESC");

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    private void maintainSearchResultHistory(String serviceType, String checkDate)
    {
        if (DailyTextUtils.isTextEmpty(serviceType, checkDate))
        {
            return;
        }

        long checkTime = -1;
        try
        {
            checkTime = DailyCalendar.getInstance(checkDate, true).getTimeInMillis();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (checkTime <= 0)
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_SEARCH_RESULT_HISTORY, SearchResultHistoryList.SERVICE_TYPE + " = '" + serviceType + "'" //
                + " AND " + SearchResultHistoryList.START_DATE_TIME + " < '" + checkTime + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }
    }

    public void addSearchResultHistory(String serviceType, String displayName, String currentDate, String startDate //
        , String endDate, String suggest, int adultCount, String childList)
    {
        if (DailyTextUtils.isTextEmpty(serviceType, displayName, currentDate, startDate, suggest))
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            long oldId = -1;
            Cursor cursor = null;

            try
            {
                cursor = getSearchResultHistory(serviceType, displayName);

                if (cursor != null && cursor.getCount() > 0)
                {
                    cursor.moveToFirst();

                    int idColumnIndex = cursor.getColumnIndex(SearchResultHistoryList._ID);
                    oldId = cursor.getLong(idColumnIndex);
                }

            } catch (Exception e)
            {
                oldId = -1;
            } finally
            {
                try
                {
                    if (cursor != null)
                    {
                        cursor.close();
                    }
                } catch (Exception e)
                {
                    // do nothing!
                }
            }

            long startDateTime = DailyCalendar.getInstance(startDate, DailyCalendar.ISO_8601_FORMAT).getTimeInMillis();

            ContentValues contentValues = new ContentValues();

            if (oldId > 0)
            {
                contentValues.put(SearchResultHistoryList._ID, oldId);
            }

            contentValues.put(SearchResultHistoryList.SERVICE_TYPE, serviceType);
            contentValues.put(SearchResultHistoryList.DISPLAY_NAME, displayName);
            contentValues.put(SearchResultHistoryList.START_DATE, startDate);
            contentValues.put(SearchResultHistoryList.END_DATE, endDate);
            contentValues.put(SearchResultHistoryList.SUGGEST, suggest);
            contentValues.put(SearchResultHistoryList.ADULT_COUNT, adultCount);
            contentValues.put(SearchResultHistoryList.CHILD_AGE_LIST, childList);
            contentValues.put(SearchResultHistoryList.START_DATE_TIME, startDateTime);

            long savingTime = DailyCalendar.getInstance().getTimeInMillis();
            contentValues.put(SearchResultHistoryList.SAVING_TIME, savingTime);

            db.beginTransaction();

            insertOrUpdate(T_SEARCH_RESULT_HISTORY, SearchResultHistoryList._ID, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e)
        {
            ExLog.w("add fail : " + e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (IllegalStateException e)
            {
                // ignore
            }
        }

        maintainSearchResultHistory(serviceType, currentDate);

        mContext.getContentResolver().notifyChange(StayIbRecentlySuggestList.NOTIFICATION_URI, null);
    }

    public Cursor getSearchResultHistoryList(String serviceType, String checkDate, int maxCount)
    {
        if (DailyTextUtils.isTextEmpty(serviceType, checkDate))
        {
            return null;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return null;
        }

        long checkTime = -1;

        try
        {
            checkTime = DailyCalendar.getInstance(checkDate, true).getTimeInMillis();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_SEARCH_RESULT_HISTORY);
        sqlBuilder.append(" WHERE ").append(SearchResultHistoryList.SERVICE_TYPE).append("=\"").append(serviceType).append("\"");
        sqlBuilder.append(" AND ").append(SearchResultHistoryList.START_DATE_TIME).append(">=").append(checkTime).append("");
        sqlBuilder.append(" ORDER BY ").append(SearchResultHistoryList.SAVING_TIME).append(" DESC");

        if (maxCount > 0)
        {
            sqlBuilder.append(" limit ").append(maxCount);
        }

        Cursor cursor = rawQuery(sqlBuilder.toString());

        return cursor;
    }

    public void deleteSearchResultHistory(String serviceType, String displayName)
    {
        if (DailyTextUtils.isTextEmpty(serviceType, displayName))
        {
            return;
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return;
        }

        try
        {
            db.beginTransaction();
            db.delete(T_SEARCH_RESULT_HISTORY, SearchResultHistoryList.SERVICE_TYPE + " = '" + serviceType + "'" //
                + " AND " + SearchResultHistoryList.DISPLAY_NAME + " = '" + displayName + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            try
            {
                db.endTransaction();
            } catch (Exception e)
            {
            }
        }

        mContext.getContentResolver().notifyChange(SearchResultHistoryList.NOTIFICATION_URI, null);
    }

    //    public void exportDatabase(String databaseName)
    //    {
    //        try
    //        {
    //            //            File sd = Environment.getExternalStorageDirectory();
    //            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    //            File data = Environment.getDataDirectory();
    //
    //            if (sd.canWrite())
    //            {
    //                String currentDBPath = "//data//" + "com.twoheart.dailyhotel.debug" + "//databases//" + databaseName + "";
    //                String backupDBPath = "backup.db";
    //                File currentDB = new File(data, currentDBPath);
    //                File backupDB = new File(sd, backupDBPath);
    //
    //                if (currentDB.exists())
    //                {
    //                    FileChannel src = new FileInputStream(currentDB).getChannel();
    //                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
    //                    dst.transferFrom(src, 0, src.size());
    //                    src.close();
    //                    dst.close();
    //                }
    //            }
    //        } catch (Exception e)
    //        {
    //
    //        }
    //    }
}
