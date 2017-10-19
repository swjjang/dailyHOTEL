package com.daily.dailyhotel.storage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyColumns;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.repository.local.model.StayObRecentlySuggestList;
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
    private static final int DATABASE_VERSION = 2;

    public static final int MAX_RECENT_PLACE_COUNT = 30;

    private static boolean mIsTest = true;

    private Context mContext;
    private boolean mIsClosing;

    public static final String DB_NAME = "daily.db";

    public static final String T_RECENTLY = "recently";
    public static final String T_STAY_OB_RECENTLY_SUGGEST = "stay_ob_recently_suggest";

    private static final String CREATE_T_RECENTLY = "CREATE TABLE IF NOT EXISTS " + T_RECENTLY + " (" //
        + RecentlyList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + RecentlyList.PLACE_INDEX + " INTEGER NOT NULL UNIQUE DEFAULT 0, " //
        + RecentlyList.NAME + " TEXT NULL, " //
        + RecentlyList.ENGLISH_NAME + " TEXT NULL, " //
        + RecentlyList.SERVICE_TYPE + " TEXT NOT NULL, " // ServiceType.name() 으로 저장 예정 HOTEL, OB_STAY, GOURMET
        + RecentlyList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + RecentlyList.IMAGE_URL + " TEXT NULL " + ");";

    // insert ver 2
    private static final String CREATE_T_STAY_OB_RECENTLY_SUGGEST = "CREATE TABLE IF NOT EXISTS " + T_STAY_OB_RECENTLY_SUGGEST + " (" //
        + StayObRecentlySuggestList._ID + " INTEGER  PRIMARY KEY NOT NULL, " //
        + StayObRecentlySuggestList.NAME + " TEXT NULL, " //
        + StayObRecentlySuggestList.CITY + " TEXT NULL, " //
        + StayObRecentlySuggestList.COUNTRY + " TEXT NULL, " //
        + StayObRecentlySuggestList.COUNTRY_CODE + " TEXT NULL, " //
        + StayObRecentlySuggestList.CATEGORY_KEY + " TEXT NULL, " //
        + StayObRecentlySuggestList.DISPLAY + " TEXT NULL, " //
        + StayObRecentlySuggestList.LATITUDE + " DOUBLE NOT NULL DEFAULT 0, " //
        + StayObRecentlySuggestList.LONGITUDE + " DOUBLE NOT NULL DEFAULT 0, " //
        + StayObRecentlySuggestList.SAVING_TIME + " LONG NOT NULL DEFAULT 0 " + ");";

    public DailyDb(Context context)
    {
        super(context, DB_NAME, null, DATABASE_VERSION);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        ExLog.v("version : " + db.getVersion());

        createDbObjects(db);
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

        if (oldVersion < DATABASE_VERSION)
        {
            createDbObjects(db);
        }
    }

    private void createDbObjects(SQLiteDatabase db)
    {
        db.execSQL(CREATE_T_RECENTLY);
        db.execSQL(CREATE_T_STAY_OB_RECENTLY_SUGGEST);
    }

    private void dropAllDbObjects(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + T_RECENTLY);
        db.execSQL("drop table if exists " + T_STAY_OB_RECENTLY_SUGGEST);
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
        , String englishName, String imageUrl, boolean isUpdateDate)
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
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(RecentlyColumns.PLACE_INDEX, index);
            contentValues.put(RecentlyColumns.NAME, name);
            contentValues.put(RecentlyColumns.ENGLISH_NAME, englishName);
            contentValues.put(RecentlyColumns.SERVICE_TYPE, serviceType == null ? "" : serviceType.name());
            contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
            contentValues.put(RecentlyColumns.IMAGE_URL, imageUrl);

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

    public void addStayObRecentlySuggest(long _id, String name, String city, String country //
        , String countryCode, String categoryKey, String display //
        , double latitude, double longitude, boolean isUpdateDate)
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
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(StayObRecentlySuggestColumns._ID, _id);
            contentValues.put(StayObRecentlySuggestColumns.NAME, name);
            contentValues.put(StayObRecentlySuggestColumns.CITY, city);
            contentValues.put(StayObRecentlySuggestColumns.COUNTRY, country);
            contentValues.put(StayObRecentlySuggestColumns.COUNTRY_CODE, countryCode);
            contentValues.put(StayObRecentlySuggestColumns.CATEGORY_KEY, categoryKey);
            contentValues.put(StayObRecentlySuggestColumns.DISPLAY, display);
            contentValues.put(StayObRecentlySuggestColumns.LATITUDE, latitude);
            contentValues.put(StayObRecentlySuggestColumns.LONGITUDE, longitude);
            contentValues.put(StayObRecentlySuggestColumns.SAVING_TIME, savingTime);

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
