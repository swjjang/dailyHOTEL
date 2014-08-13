package com.twoheart.dailyhotel;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.activity.AccountCompleteDialogActivity;
import com.twoheart.dailyhotel.activity.GcmLockDialogActivity;
import com.twoheart.dailyhotel.activity.PushDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.WakeLock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

/**
 * GCM 메시지가 올 경우 실제로 처리하는 클래스,
 * 스마트폰이 꺼져있는 경우 잠금을 뚫고 다이얼로그를 띄움.
 * 스마트폰이 켜져있으며 우리 앱을 킨 상태에서 결제 완료 메시지를 받았다면, 결제완료 다이얼로그를 띄움.
 * 노티피케이션은 GCM이 들어오는 어떠한 경우에도 모두 띄움.
 * @author jangjunho
 *
 */
public class GcmIntentService extends IntentService implements Constants{

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { 

			try {
				JSONObject jsonMsg = new JSONObject(extras.getString("message"));
				String type = jsonMsg.getString("type");
				String msg = jsonMsg.getString("msg");

				android.util.Log.e("GCM_MESSAGE",jsonMsg.toString());
				
				if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
					
					if (isScreenOn(this) && type.equals("account_complete")) { // 데일리호텔 앱이 켜져있는경우.
						
						ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
						ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
						String className = topActivity.getClassName();

						android.util.Log.e("CURRENT_ACTIVITY_PACKAGE", className+" / "+className);
						
						if (className.contains("dailyhotel") && !className.contains("GcmLockDialogActivity")) {
							
							Intent i = new Intent(this, AccountCompleteDialogActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("msg", msg);
							startActivity(i);
						}
						
					} else if (!isScreenOn(this)) { // 스크린 꺼져있는경우
						
						WakeLock.acquireWakeLock(this, PowerManager.FULL_WAKE_LOCK
								| PowerManager.ACQUIRE_CAUSES_WAKEUP);	// PushDialogActivity에서 release 해줌.
						KeyguardManager manager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);  
						KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);  
						lock.disableKeyguard();  // 기존의 잠금화면을 disable

						Intent i = new Intent(this, GcmLockDialogActivity.class);
						i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, msg);

						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | 
								Intent.FLAG_ACTIVITY_CLEAR_TOP);
						this.startActivity(i);
					}
					// 노티피케이션은 케이스에 상관없이 항상 뜨도록함.
					sendNotification(type, msg);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	public boolean isScreenOn(Context context) {
		return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
	}

	private void sendNotification(String type, String msg) {
		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, MainActivity.class);
		if (type.equals("account_complete")) intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_INTENT_FROM_PUSH, true);
		// type은 notice 타입과 account_complete 타입이 존재함. reservation일 경우 예약확인 창으로 이동.

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.img_ic_appicon)
		.setContentTitle(getString(R.string.app_name))
		.setAutoCancel(true)
		.setSound(uri)
		.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	

}