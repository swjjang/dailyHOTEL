package com.twoheart.dailyhotel.screen.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.repository.local.CartLocalImpl;

/**
 * 앱 종료 후에 흔적을 남기지 히스토리 및 최근 실행 앱을 남기지 않기 위해서
 */
public class ExitActivity extends Activity
{
    public static void exitApplication(Context context)
    {
        Intent intent = new Intent(context, ExitActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new CartLocalImpl().clearGourmetCart(this).subscribe();

        if (VersionUtils.isOverAPI21() == true)
        {
            finishAndRemoveTask();
        } else
        {
            finish();
        }
    }
}
