package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.os.AsyncTask;

import com.daily.base.util.ExLog;
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
    private OnCompletedListener mOnCompletedListener;
    private String mVersion;

    public interface OnCompletedListener
    {
        void onCompleted(boolean result, String version);
    }

    private Context mContext;

    public ImageDownloadAsyncTask(Context context, String version, OnCompletedListener onCompletedListener)
    {
        mContext = context;
        mVersion = version;
        mOnCompletedListener = onCompletedListener;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        String url = params[0];

        if (com.daily.base.util.TextUtils.isTextEmpty(url, mVersion) == true)
        {
            return false;
        }

        String fileName = Util.makeImageFileName(mVersion);
        File downloadedFile = null;
        BufferedSink bufferedSink = null;

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
        if (mOnCompletedListener != null)
        {
            mOnCompletedListener.onCompleted(result, mVersion);
        }
    }
}