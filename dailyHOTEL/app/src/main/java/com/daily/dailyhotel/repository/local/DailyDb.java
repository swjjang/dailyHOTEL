package com.daily.dailyhotel.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.RecentlyColumns;
import com.daily.dailyhotel.repository.local.model.RecentlyList;
import com.daily.dailyhotel.repository.local.model.RecentlyRealmObject;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmResults;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class DailyDb extends SQLiteOpenHelper implements BaseColumns
{
    private static final int DATABASE_VERSION = 1;

    public static final int MAX_RECENT_PLACE_COUNT = 30;

    private static boolean mIsTest = true;

    private Context mContext;
    private boolean mIsClosing;

    public static final String DB_NAME = "daily.db";

    public static final String T_RECENTLY = "recently";

    private static final String CREATE_T_RECENTLY = "CREATE TABLE " + T_RECENTLY + " (" //
        + RecentlyList._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " //
        + RecentlyList.PLACE_INDEX + " INTEGER NOT NULL UNIQUE DEFAULT 0, " //
        + RecentlyList.NAME + " TEXT NULL, " //
        + RecentlyList.ENGLISH_NAME + " TEXT NULL, " //
        + RecentlyList.SERVICE_TYPE + " TEXT NOT NULL, " // ServiceType.name() 으로 저장 예정 HOTEL, OB_STAY, GOURMET
        + RecentlyList.SAVING_TIME + " LONG NOT NULL DEFAULT 0, " //
        + RecentlyList.IMAGE_URL + " TEXT NULL " + ");";

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
    }

    private void createDbObjects(SQLiteDatabase db)
    {
        db.execSQL(CREATE_T_RECENTLY);
    }

    private void dropAllDbObjects(SQLiteDatabase db)
    {
        db.execSQL("drop table if exists " + CREATE_T_RECENTLY);
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

    public Cursor getRecentlyPlaces(RecentlyPlaceUtil.ServiceType[] serviceTypes, int limit)
    {
        SQLiteDatabase db = getDb();
        if (db == null)
        {
            return null;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(T_RECENTLY);

        if (serviceTypes != null)
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

    public Cursor getRecentlyPlace(RecentlyPlaceUtil.ServiceType serviceType, int index)
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

    public long checkExistRecentPlace(RecentlyPlaceUtil.ServiceType serviceType, int index)
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
                cursor.close();
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return -1;
    }

    public long getOldestSavingTime(RecentlyPlaceUtil.ServiceType... serviceTypes)
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

            if (serviceTypes != null)
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
                cursor.close();
            } catch (Exception e)
            {
                // do nothing!
            }
        }

        return savingTime;
    }

    // main Activity 에서 진행
    public boolean migrateAllRecentlyPlace(ArrayList<HomePlace> homePlaceList)
    {
        if (homePlaceList == null || homePlaceList.size() == 0)
        {
            // realm db 에도 결과가 없으면  migration 되었다고 판단함
            RealmResults<RecentlyRealmObject> realmResultList = RecentlyPlaceUtil.getRecentlyTypeList((RecentlyPlaceUtil.ServiceType[]) null);
            return realmResultList == null || realmResultList.size() == 0;
        }

        long oldestSavingTime = -1;

        long oldestRealmSavingTime = RecentlyPlaceUtil.getOldestSavingTime((RecentlyPlaceUtil.ServiceType[]) null);
        long oldestDbSavingTime = getOldestSavingTime((RecentlyPlaceUtil.ServiceType[]) null);

        if (oldestRealmSavingTime >= 0)
        {
            oldestSavingTime = oldestRealmSavingTime;
        }

        if (oldestDbSavingTime >= 0 && oldestSavingTime > oldestDbSavingTime)
        {
            oldestSavingTime = oldestDbSavingTime;
        }

        if (oldestSavingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            oldestSavingTime = calendar.getTimeInMillis();
        }

        SQLiteDatabase db = getDb();
        if (db == null)
        {
            // db를 사용할 수 없는 상태이므로 migration 실패로 판단
            return false;
        }

        boolean isSuccess = false;

        try
        {
            db.beginTransaction();

            for (HomePlace homePlace : homePlaceList)
            {
                RecentlyPlaceUtil.ServiceType serviceType = RecentlyPlaceUtil.getServiceType(homePlace.serviceType);
                long oldDbSavingTime = checkExistRecentPlace(serviceType, homePlace.index);

                if (oldDbSavingTime > 0)
                {
                    continue;
                }

                RecentlyRealmObject oldRecentlyPlace = RecentlyPlaceUtil.getRecentlyPlace(serviceType, homePlace.index);

                long oldSavingTime = -1;
                if (oldRecentlyPlace != null)
                {
                    oldSavingTime = oldRecentlyPlace.savingTime;
                }

                if (oldSavingTime <= 0)
                {
                    oldSavingTime = --oldestSavingTime;
                }

                ContentValues contentValues = convertContentValues(homePlace, oldSavingTime);
                insertOrUpdate(T_RECENTLY, RecentlyColumns.PLACE_INDEX, contentValues);
            }

            db.setTransactionSuccessful();
            isSuccess = true;

            exportDatabse(DB_NAME);
        } catch (Exception e)
        {
            isSuccess = false;
            ExLog.w("migration fail : " + e.toString());
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

        return isSuccess;
    }

    private ContentValues convertContentValues(HomePlace homePlace, long savingTime)
    {
        if (homePlace == null)
        {
            return null;
        }


        if (savingTime <= 0)
        {
            Calendar calendar = DailyCalendar.getInstance();
            savingTime = calendar.getTimeInMillis();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(RecentlyColumns.PLACE_INDEX, homePlace.index);
        contentValues.put(RecentlyColumns.NAME, homePlace.title);
        contentValues.put(RecentlyColumns.ENGLISH_NAME, "");
        contentValues.put(RecentlyColumns.SERVICE_TYPE, homePlace.serviceType);
        contentValues.put(RecentlyColumns.SAVING_TIME, savingTime);
        contentValues.put(RecentlyColumns.IMAGE_URL, homePlace.imageUrl);

        return contentValues;
    }

    public void exportDatabse(String databaseName)
    {
        try
        {
            //            File sd = Environment.getExternalStorageDirectory();
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            if (sd.canWrite())
            {
                String currentDBPath = "//data//" + "com.twoheart.dailyhotel.debug" + "//databases//" + databaseName + "";
                String backupDBPath = "backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists())
                {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e)
        {

        }
    }
}
