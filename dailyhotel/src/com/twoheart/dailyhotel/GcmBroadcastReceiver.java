package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

/**
 * GCM 메시지가 올 경우 이를 받아 실제로 처리하는 GcmItentService 로 전달함.
 * 
 * @author jangjunho
 *
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver implements Constants
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		SharedPreferences pref = context.getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
		// gcm_id 가 empty라면 해당 기기는 로그아웃 된 상태이므로, GCM을 받지 않도록 한다.
		if (pref.getString(KEY_PREFERENCE_GCM_ID, "").isEmpty())
		{
			ExLog.e("Ignore Push is true");
		} else
		{
			ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
			startWakefulService(context, (intent.setComponent(comp)));

			setResultCode(Activity.RESULT_OK);

		}
	}
}
