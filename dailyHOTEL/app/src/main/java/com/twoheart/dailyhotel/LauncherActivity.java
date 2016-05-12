package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }

        startActivity(newIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).onStart(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        AnalyticsManager.getInstance(this).onStop(this);
    }
}
