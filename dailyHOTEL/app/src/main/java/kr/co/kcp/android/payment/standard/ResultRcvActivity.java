package kr.co.kcp.android.payment.standard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ResultRcvActivity extends Activity
{

    public static Uri m_uriResult;
    public static boolean b_type = false;

    public static final String m_strLogTag = "PaySample";
    public static final String s_strLogTag = "PayACNTSample";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null || intent.getData() == null)
        {
            finish();
        }

        if ("dailyHOTEL".equals(intent.getData().getScheme()) == true)
        {
            m_uriResult = intent.getData();
        } else
        {
            m_uriResult = null;
        }

        finish();
    }
}