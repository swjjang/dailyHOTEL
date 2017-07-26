package com.daily.dailyhotel.repository.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.repository.local.model.RecentlyModel;

/**
 * Created by android_sam on 2017. 7. 26..
 */

public class DailyDb extends SQLiteOpenHelper implements BaseColumns
{
    private static final int DATABASE_VERSION = 1;

    private Context mContext;
    private boolean mIsClosing;

    public static final String DB_NAME = "daily.db";

    public static final String T_RECENTLY = "recently";

    private static final String CREATE_T_RECENTLY = "CREATE TABLE " + T_RECENTLY + " (" //
        + RecentlyModel._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL, " + RecentlyModel.PLACE_INDEX + " INTEGER NOT NULL UNIQUE DEFAULT 0, " + RecentlyModel.NAME + " TEXT NULL, " + RecentlyModel.ENGLISH_NAME + " TEXT NULL, " + RecentlyModel.SERVICE_TYPE + " TEXT NOT NULL, " + RecentlyModel.IMAGE_URL + " TEXT NULL " + ");";

    public DailyDb(Context context)
    {
        super(context, DB_NAME, null, DATABASE_VERSION);
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

    public void migrateAllRecentlyPlace(Context context)
    {
//        Recently
    }
}
