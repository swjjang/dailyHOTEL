package com.twoheart.dailyhotel;

import static com.twoheart.dailyhotel.util.AppConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.twoheart.dailyhotel.activity.SplashActivity;

public class GCMIntentService extends GCMBaseIntentService{
	private final static String TAG = "GCMIntentService";
	public final static String SENDER_ID = "288636757896";
	
	private SharedPreferences prefs;
	
	public GCMIntentService(){
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			Log.i("TAG", "pushmessage");
			showMessage(context, intent);
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Log.i(TAG, "onRegistered " +  regId);
		
		try {
			// DB 전송
			HttpClient client = new DefaultHttpClient();
			String postUrl = REST_URL + DEVICE + "/append";
			HttpPost post = new HttpPost(postUrl);
		
			List params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("device", regId));
			
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			post.setEntity(ent);
			client.execute(post);
			
			prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putBoolean(PREFERENCE_GCM, true);
			ed.commit();
			
		} catch (Exception e) {
			Log.e(TAG, "error");
			
			prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putBoolean(PREFERENCE_GCM, false);
			ed.commit();
		}
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.i(TAG, "onUnregistered " +  regId);
	}
	
	private void showMessage(Context context, Intent intent) {
		String title = "dailyHOTEL";
		String msg = intent.getStringExtra("msg");
		String ticker = "dailyHOTEL New Message";
		
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);

		// 해당 어플을 실행하는 이벤트를 하고싶을 때 아래 주석을 풀어주세요
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
                | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		Notification notification = new Notification();
		notification.icon = R.drawable.dh_ic_home_72;
		notification.tickerText = ticker;
		notification.when = System.currentTimeMillis();
		notification.vibrate = new long[] { 500, 100, 500, 100 };
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, title, msg, pendingIntent);

		notificationManager.notify(0, notification);
	}
	
	
	
}
