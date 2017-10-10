package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.os.AsyncTask;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.Util;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class SplashImageDownloadAsyncTask extends AsyncTask<String, Void, Boolean>
{

    private Context mContext;

    public SplashImageDownloadAsyncTask(Context context)
    {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String url = params[0];
        String version = params[1];

        if (DailyTextUtils.isTextEmpty(url, version) == true)
        {
            return false;
        }

        String fileName = Util.makeIntroImageFileName(version);
        File downloadedFile = null;
        BufferedSink bufferedSink = null;

        DailyRemoteConfigPreference.getInstance(mContext).setRemoteConfigIntroImageNewUrl(url);
        DailyRemoteConfigPreference.getInstance(mContext).setRemoteConfigIntroImageNewVersion(version);

        try
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful() == true)
            {
                downloadedFile = new File(mContext.getCacheDir(), fileName);

                bufferedSink = Okio.buffer(Okio.sink(downloadedFile));
                bufferedSink.writeAll(response.body().source());
                bufferedSink.close();
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            if (bufferedSink != null)
            {
                try
                {
                    bufferedSink.close();
                } catch (IOException ioe)
                {
                    ExLog.d(ioe.toString());
                }
            }

            if (downloadedFile != null && downloadedFile.delete() == false)
            {
                downloadedFile.deleteOnExit();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (result == true)
        {
            String currentVersion = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigIntroImageVersion();
            String newVersion = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigIntroImageNewVersion();

            DailyRemoteConfigPreference.getInstance(mContext).setRemoteConfigIntroImageVersion(newVersion);
            DailyRemoteConfigPreference.getInstance(mContext).setRemoteConfigIntroImageNewUrl(null);
            DailyRemoteConfigPreference.getInstance(mContext).setRemoteConfigIntroImageNewVersion(null);

            // 파일 삭제
            if (DailyTextUtils.isTextEmpty(currentVersion) == false)
            {
                String fileName = Util.makeIntroImageFileName(currentVersion);
                File currentFile = new File(mContext.getCacheDir(), fileName);
                if (currentFile.exists() == true && currentFile.delete() == false)
                {
                    currentFile.deleteOnExit();
                }
            }
        }
    }
}