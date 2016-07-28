package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.os.AsyncTask;

import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class ImageDownloadAsyncTask extends AsyncTask<String, Void, Boolean>
{

    private Context mContext;

    public ImageDownloadAsyncTask(Context context)
    {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String url = params[0];
        String version = params[1];

        if (Util.isTextEmpty(url) == true || Util.isTextEmpty(version) == true)
        {
            return false;
        }

        String fileName = Util.makeIntroImageFileName(version);
        File downloadedFile = null;
        BufferedSink bufferedSink = null;

        DailyPreference.getInstance(mContext).setIntroImageNewUrl(url);
        DailyPreference.getInstance(mContext).setIntroImageNewVersion(version);

        try
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();

            downloadedFile = new File(mContext.getCacheDir(), fileName);

            bufferedSink = Okio.buffer(Okio.sink(downloadedFile));
            bufferedSink.writeAll(response.body().source());
            bufferedSink.close();

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
            String currentVersion = DailyPreference.getInstance(mContext).getIntroImageVersion();
            String newVersion = DailyPreference.getInstance(mContext).getIntroImageNewVersion();
            DailyPreference.getInstance(mContext).setIntroImageVersion(newVersion);

            DailyPreference.getInstance(mContext).setIntroImageNewUrl(null);
            DailyPreference.getInstance(mContext).setIntroImageNewVersion(null);

            // 파일 삭제
            if (Util.isTextEmpty(currentVersion) == false)
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