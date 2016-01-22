package com.twoheart.dailyhotel.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.twoheart.dailyhotel.util.Constants;

/**
 * GCM 메시지가 올 경우 이를 받아 실제로 처리하는 GcmItentService 로 전달함.
 *
 * @author jangjunho
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver implements Constants
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));

        setResultCode(Activity.RESULT_OK);
    }
}
