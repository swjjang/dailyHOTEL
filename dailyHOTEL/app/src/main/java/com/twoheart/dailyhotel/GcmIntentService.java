package com.twoheart.dailyhotel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.activity.PushLockDialogActivity;
import com.twoheart.dailyhotel.activity.ScreenOnPushDialogActivity;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.WakeLock;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * GCM 메시지가 올 경우 실제로 처리하는 클래스, 스마트폰이 꺼져있는 경우 잠금을 뚫고 다이얼로그를 띄움. 스마트폰이 켜져있으며 우리 앱을
 * 킨 상태에서 결제 완료 메시지를 받았다면, 결제완료 다이얼로그를 띄움. 노티피케이션은 GCM이 들어오는 어떠한 경우에도 모두 띄움.
 * <p/>
 * case 1 : 휴대폰이 켜져있지만 현재 데일리호텔이 켜져있지 않은 상황, => 푸시만 뜸 case 2 : 휴대폰이 켜져있고 데일리호텔이
 * 켜져있는 상황 => 푸시, 다이얼로그형 푸시 뜸 case 3 : 휴대폰이 꺼져있는 경우 => 다이얼로그형 푸시만 뜸
 *
 * @author jangjunho
 */
public class GcmIntentService extends IntentService implements Constants
{
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private boolean mIsBadge;
    private boolean mIsSound;

    public GcmIntentService()
    {
        super("GcmIntentService");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        mIsBadge = false;
        mIsSound = true;

        if (extras != null && !extras.isEmpty())
        {
            try
            {
                // 중복 체크를 위한 값
                String collapseKey = intent.getStringExtra("collapse_key");
                JSONObject jsonMsg = new JSONObject(extras.getString("message"));
                String msg = jsonMsg.getString("msg");
                String imageUrl = null;

                if (jsonMsg.has("image_url") == true)
                {
                    imageUrl = jsonMsg.getString("image_url");
                }

                int type = -1;

                if (jsonMsg.getString("type").equals("notice"))
                {
                    type = PUSH_TYPE_NOTICE;
                } else if (jsonMsg.getString("type").equals("account_complete"))
                {
                    type = PUSH_TYPE_ACCOUNT_COMPLETE;
                }

                if (!jsonMsg.isNull("badge"))
                {
                    mIsBadge = jsonMsg.getBoolean("badge");
                }

                if (!jsonMsg.isNull("sound"))
                {
                    mIsSound = jsonMsg.getBoolean("sound");
                }

                SharedPreferences pref = this.getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);

                switch (type)
                {
                    case PUSH_TYPE_ACCOUNT_COMPLETE:
                        String tid = jsonMsg.getString("TID");
                        String hotelName = jsonMsg.getString("hotelName");
                        String paidPrice = jsonMsg.getString("paidPrice");

                        if (collapseKey.equals(pref.getString("collapseKey", "")))
                        {
                        } else
                        {
                            Editor editor = pref.edit();
                            editor.putString("collapseKey", collapseKey);
                            editor.apply();

                            sendPush(messageType, type, msg, imageUrl);

                            // 로그 남기기 이슈
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
                            Date date = new Date();
                            String strDate = dateFormat.format(date);
                            String userIdxStr = pref.getString(KEY_PREFERENCE_USER_IDX, "0");
                            String roomIdx = pref.getString(KEY_PREFERENCE_HOTEL_ROOM_IDX, "0");
                            String checkInTime = pref.getString(KEY_PREFERENCE_HOTEL_CHECKIN, "0");
                            String checkOutTime = pref.getString(KEY_PREFERENCE_HOTEL_CHECKOUT, "0");
                            String transId = strDate + userIdxStr; //기타 결제수단은 이걸 transaction ID로 사용하고 계좌이체의 경우 넘겨받는 tid값을 사용함.

                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                            strDate = dateFormat2.format(date);

                            AnalyticsManager.getInstance(getApplicationContext()).purchaseComplete(tid, transId, roomIdx, hotelName, Screen.GCMSERVICE, checkInTime, checkOutTime, strDate, Pay.Type.VBANK.name(), Double.parseDouble(paidPrice));

                            // 가상계좌 내용 정리
                            editor.remove(KEY_PREFERENCE_USER_IDX);
                            editor.remove(KEY_PREFERENCE_HOTEL_NAME);
                            editor.remove(KEY_PREFERENCE_HOTEL_ROOM_IDX);
                            editor.remove(KEY_PREFERENCE_HOTEL_CHECKOUT);
                            editor.remove(KEY_PREFERENCE_HOTEL_CHECKIN);
                            editor.commit();
                        }
                        break;

                    case PUSH_TYPE_NOTICE:
                        if (collapseKey.equals(pref.getString("collapseKey", "")))
                        {
                        } else
                        {
                            Editor editor = pref.edit();
                            editor.putString("collapseKey", collapseKey);
                            editor.apply();
                            sendPush(messageType, type, msg, imageUrl);
                            break;
                        }
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public void sendPush(String messageType, int type, String msg, String imageUrl)
    {
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
        {
            if (isScreenOn(this) && type != -1)
            {
                // 데일리호텔 앱이 켜져있는경우.
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
                String className = topActivity.getClassName();

                if (className.contains("dailyhotel") && !className.contains("GcmLockDialogActivity") && !mIsBadge)
                {
                    Intent i = new Intent(this, ScreenOnPushDialogActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, type);
                    i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, msg);
                    startActivity(i);
                }
            } else if (!isScreenOn(this) && !mIsBadge)
            {
                // 스크린 꺼져있는경우
                WakeLock.acquireWakeLock(this, PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP); // PushDialogActivity에서 release 해줌.
                KeyguardManager manager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);
                lock.disableKeyguard(); // 기존의 잠금화면을 disable

                Intent i = new Intent(this, PushLockDialogActivity.class);
                i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, msg);
                i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, type);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
            }

            // 노티피케이션은 케이스에 상관없이 항상 뜨도록함.
            sendNotification(type, msg, imageUrl);
        }
    }

    public boolean isScreenOn(Context context)
    {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private void sendNotification(int type, String msg, String imageUrl)
    {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        if (type == PUSH_TYPE_ACCOUNT_COMPLETE)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_ACCOUNT_COMPLETE);
        } else if (type == PUSH_TYPE_NOTICE)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_NOTICE);
        }

        // type은 notice 타입과 account_complete 타입이 존재함. reservation일 경우 예약확인 창으로 이동.
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = null;

        if (mIsSound)
        {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (TextUtils.isEmpty(imageUrl) == true)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.img_ic_appicon_feature).setContentTitle(getString(R.string.app_name)).setAutoCancel(true).setSound(uri).setContentText(msg);
            builder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        } else
        {
            new ImageLoaderNotification(contentIntent, uri, msg).execute(imageUrl);
        }
    }

    private class ImageLoaderNotification extends AsyncTask<String, Void, Bitmap>
    {
        private String mMessage;
        private PendingIntent mPendingIntent;
        private Uri mUri;

        public ImageLoaderNotification(PendingIntent contentIntent, Uri uri, String message)
        {
            super();

            mPendingIntent = contentIntent;
            mUri = uri;
            mMessage = message;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            InputStream inputStream;

            try
            {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(GcmIntentService.this);

            builder.setContentTitle(getString(R.string.app_name)) //
                    .setContentText(mMessage) //
                    .setTicker(getResources().getString(R.string.app_name)) //
                    .setSound(mUri) //
                    .setAutoCancel(true) //
                    .setSmallIcon(R.drawable.img_ic_appicon_feature);

            if (bitmap != null)
            {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(mMessage));
            }

            builder.setContentIntent(mPendingIntent);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}