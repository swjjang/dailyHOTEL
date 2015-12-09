package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

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

    @Override
    protected void onStart()
    {
        super.onStart();

        if(Util.isOverAPI14() == true)
        {
            Branch branch = Branch.getInstance();
            branch.initSession(new Branch.BranchReferralInitListener()
            {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error)
                {
                }
            }, this.getIntent().getData(), this);
        }
    }
}
