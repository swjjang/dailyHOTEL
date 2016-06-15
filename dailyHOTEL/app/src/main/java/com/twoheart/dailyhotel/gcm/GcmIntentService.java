package com.twoheart.dailyhotel.gcm;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.push.PushLockDialogActivity;
import com.twoheart.dailyhotel.screen.common.push.ScreenOnPushDialogActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.WakeLock;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * GCM 메시지가 올 경우 실제로 처리하는 클래스, 스마트폰이 꺼져있는 경우 잠금을 뚫고 다이얼로그를 띄움. 스마트폰이 켜져있으며 우리 앱을
 * 킨 상태에서 결제 완료 메시지를 받았다면, 결제완료 다이얼로그를 띄움. 노티피케이션은 GCM이 들어오는 어떠한 경우에도 모두 띄움.
 * <p>
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
    private ImageLoaderNotification mImageLoaderNotification;

    public GcmIntentService()
    {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);

        String messageType = googleCloudMessaging.getMessageType(intent);

        mIsBadge = false;
        mIsSound = true;

        if (extras != null && !extras.isEmpty())
        {
            try
            {
                // 중복 체크를 위한 값
                String collapseKey = intent.getStringExtra("collapse_key");
                String title = extras.getString("title");
                JSONObject jsonMsg = new JSONObject(extras.getString("message"));
                String msg = jsonMsg.getString("msg");
                String imageUrl = null;

                if (Util.isTextEmpty(title) == true)
                {
                    title = getString(R.string.app_name);
                }

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

                switch (type)
                {
                    case PUSH_TYPE_ACCOUNT_COMPLETE:
                    {
                        String tid = jsonMsg.getString("TID");
                        String hotelName = jsonMsg.getString("hotelName");
                        String paidPrice = jsonMsg.getString("paidPrice");

                        sendPush(messageType, type, title, msg, imageUrl, null);
                        break;
                    }

                    case PUSH_TYPE_NOTICE:
                    {
                        // 푸쉬 알림을 해지하면 푸쉬를 받지 않는다
                        if (DailyPreference.getInstance(this).isUserBenefitAlarm() == false)
                        {
                            return;
                        }

                        if (collapseKey.equalsIgnoreCase(DailyPreference.getInstance(this).getCollapsekey()) == false)
                        {
                            DailyPreference.getInstance(this).setCollapsekey(collapseKey);

                            String link = null;

                            // dailyhotel://dailyhotel.co.kr?view=hotel&idx=131&date=20151109&night=1
                            if (jsonMsg.has("targetLink") == true)
                            {
                                link = jsonMsg.getString("targetLink");
                            }

                            sendPush(messageType, type, title, msg, imageUrl, link);
                        }

                        break;
                    }
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    public void sendPush(String messageType, int type, String title, String msg, String imageUrl, String link)
    {
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
        {
            if (isScreenOn(this) && type != -1)
            {
                // 데일리호텔 앱이 켜져있는경우.
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
                String className = topActivity.getClassName();

                if (className.contains("dailyhotel") && !className.contains("PushLockDialogActivity") && !mIsBadge)
                {
                    Intent i = new Intent(this, ScreenOnPushDialogActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, type);
                    i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, msg);
                    i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TITLE, title);

                    switch (type)
                    {
                        case PUSH_TYPE_ACCOUNT_COMPLETE:
                            i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK, "dailyhotel://dailyhotel.co.kr?view=bookings");
                            break;

                        case PUSH_TYPE_NOTICE:
                            i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK, link);
                            break;
                    }

                    startActivity(i);
                }
            } else if (!isScreenOn(this) && !mIsBadge)
            {
                // 스크린이 꺼져있으면 팝업을 띄우지 않는다
                if (type != PUSH_TYPE_ACCOUNT_COMPLETE)
                {
                    return;
                }

                // 스크린 꺼져있는경우
                WakeLock.acquireWakeLock(this, PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP); // PushDialogActivity에서 release 해줌.
                KeyguardManager manager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
                KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);
                lock.disableKeyguard(); // 기존의 잠금화면을 disable

                Intent i = new Intent(this, PushLockDialogActivity.class);
                i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, msg);
                i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TITLE, title);
                i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, type);

                switch (type)
                {
                    case PUSH_TYPE_ACCOUNT_COMPLETE:
                        i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK, "dailyhotel://dailyhotel.co.kr?view=bookings");
                        break;

                    case PUSH_TYPE_NOTICE:
                        i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_LINK, link);
                        break;
                }

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            // 노티피케이션은 케이스에 상관없이 항상 뜨도록함.
            sendNotification(type, title, msg, imageUrl, link);
        }
    }

    public boolean isScreenOn(Context context)
    {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    private void sendNotification(int type, String title, String msg, String imageUrl, String link)
    {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, LauncherActivity.class);
        if (type == PUSH_TYPE_ACCOUNT_COMPLETE)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_ACCOUNT_COMPLETE);
            intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?view=bookings"));
        } else if (type == PUSH_TYPE_NOTICE)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_NOTICE);

            if (Util.isTextEmpty(link) == false)
            {
                intent.setData(Uri.parse(link));
            }
        }

        // type은 notice 타입과 account_complete 타입이 존재함. reservation일 경우 예약확인 창으로 이동.
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = null;

        if (mIsSound)
        {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (Util.isTextEmpty(imageUrl) == true)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.icon_noti_small) //
                .setContentTitle(title).setAutoCancel(true).setSound(uri) //
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_noti_big)) //
                .setPriority(NotificationCompat.PRIORITY_MAX)//
                .setColor(getResources().getColor(R.color.dh_theme_color))//
                .setContentIntent(contentIntent);

            if (msg.contains("\\n") == false)
            {
                builder.setContentText(msg);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
            } else
            {
                String[] message = msg.split("\\\\n");

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                for (String text : message)
                {
                    inboxStyle.addLine(text);
                }

                builder.setContentText(message[0]);
                builder.setStyle(inboxStyle);
            }

            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        } else
        {
            if (mImageLoaderNotification == null)
            {
                mImageLoaderNotification = new ImageLoaderNotification(contentIntent, uri, title, msg);
            }

            switch (mImageLoaderNotification.getStatus())
            {
                case PENDING:
                    mImageLoaderNotification.execute(imageUrl);
                    break;

                case RUNNING:
                    break;

                case FINISHED:
                    mImageLoaderNotification.cancel(true);
                    mImageLoaderNotification = new ImageLoaderNotification(contentIntent, uri, title, msg);
                    mImageLoaderNotification.execute(imageUrl);
                    break;
            }
        }
    }

    private class ImageLoaderNotification extends AsyncTask<String, Void, Bitmap>
    {
        private String mTitle;
        private String mMessage;
        private PendingIntent mPendingIntent;
        private Uri mUri;

        public ImageLoaderNotification(PendingIntent contentIntent, Uri uri, String title, String message)
        {
            super();

            mPendingIntent = contentIntent;
            mUri = uri;
            mTitle = title;
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

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                return BitmapFactory.decodeStream(inputStream, null, options);

            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (Util.isOverAPI16() == true)
            {
                try
                {
                    notificationBuilder(bitmap);
                } catch (Exception e)
                {
                    notifyCompatBuilder(bitmap);
                }
            } else
            {
                notifyCompatBuilder(bitmap);
            }
        }

        private void notificationBuilder(Bitmap bitmap)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Notification.Builder builder = new Notification.Builder(GcmIntentService.this)//
                .setContentTitle(mTitle).setContentText(mMessage).setSound(mUri) //
                .setTicker(mTitle) //
                .setAutoCancel(true) //
                .setSmallIcon(R.drawable.icon_noti_small)//
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_noti_big, options));

            if (Util.isOverAPI21() == true)
            {
                builder.setColor(getResources().getColor(R.color.dh_theme_color));
            }

            if (bitmap != null)
            {
                builder.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap).setSummaryText(mMessage));
            }

            builder.setContentIntent(mPendingIntent);
            builder.setPriority(Notification.PRIORITY_MAX);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        private void notifyCompatBuilder(Bitmap bitmap)
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(GcmIntentService.this);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            builder.setContentTitle(mTitle) //
                .setContentText(mMessage) //
                .setTicker(mTitle) //
                .setSound(mUri) //
                .setAutoCancel(true) //
                .setSmallIcon(R.drawable.icon_noti_small) //
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_noti_big, options)) //
                .setColor(getResources().getColor(R.color.dh_theme_color));

            if (bitmap != null)
            {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(mMessage));
            }

            builder.setContentIntent(mPendingIntent);
            builder.setPriority(NotificationCompat.PRIORITY_MAX);

            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}