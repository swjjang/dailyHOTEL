package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.twoheart.dailyhotel.screen.main.MainActivity;

public class LauncherActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        // Google Android first open conversion tracking snippet
        // Add this code to the onCreate() method of your application activity
        AdWordsConversionReporter.reportWithConversionId(getApplicationContext(),//
            "972698918", "FtZiCNvClGIQpurozwM", "0.00", false);

        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);

        Uri uri = getIntent().getData();

        if (uri != null)
        {
            final String KAKAOLINK = "kakaolink";
            final String DAILYHOTEL = "dailyhotel";

            String link = uri.toString();

            if (link.indexOf(KAKAOLINK) >= 0 || link.indexOf(DAILYHOTEL) >= 0)
            {
                newIntent.setData(uri);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }

        startActivity(newIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        finish();
    }
}
