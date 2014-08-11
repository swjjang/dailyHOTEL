package com.twoheart.dailyhotel;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.activity.GcmDialogActivity;
import com.twoheart.dailyhotel.activity.PushDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.WakeLock;

import android.app.Activity;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;


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

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
            	if (!isScreenOn(this)) { // 스크린 꺼져있음
        			WakeLock.acquireWakeLock(this, PowerManager.FULL_WAKE_LOCK
        					| PowerManager.ACQUIRE_CAUSES_WAKEUP);	// PushDialogActivity에서 release 해줌.
        			KeyguardManager manager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);  
        			KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);  
        			lock.disableKeyguard();  
        			
        			Intent i = new Intent(this, GcmDialogActivity.class);
        			i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, extras.getString("message"));
        			
        			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | 
        					Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			this.startActivity(i);
        		}
            	
                sendNotification(extras.getString("message"));

            }

        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
    public boolean isScreenOn(Context context) {
    	return ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }
    
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_INTENT_FROM_PUSH, true);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.img_ic_appicon)
                        .setContentTitle("데일리호텔")
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}