package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.Util;

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

        Util.initializeMemory();

        DailyDeepLink.getInstance().clear();

        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);

        Uri uri = getIntent().getData();

        if (uri != null)
        {
            DailyDeepLink.getInstance().setDeepLink(uri);

            if (DailyDeepLink.getInstance().isValidateLink() == true)
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
