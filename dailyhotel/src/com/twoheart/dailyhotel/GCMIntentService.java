package com.twoheart.dailyhotel;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.twoheart.dailyhotel.activity.SplashActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.vo.DailyHotelJsonRequest;

public class GCMIntentService extends GCMBaseIntentService implements Constants {

	private final static String TAG = "GCMIntentService";
	public final static String SENDER_ID = "288636757896";

	private SharedPreferences mSharedPreference;

	public GCMIntentService() {
		super(SENDER_ID);
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
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
		
		boolean result = false;
		
		if (DEBUG)
			Log.i(TAG, "onRegistered " + regId);
		
		try {
			
			RequestQueue queue = VolleyHttpClient.getRequestQueue();
			
			// DB 전송
			Map<String, String> gcmParams = new HashMap<String, String>();
			gcmParams.put("device", regId);
			
			queue.add(new DailyHotelJsonRequest(Method.POST, 
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_DEVICE).append("/append").toString(),
					gcmParams, null, null));
			
			result = true;

		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			
		} finally {
			SharedPreferences.Editor ed = mSharedPreference.edit();
			ed.putBoolean(KEY_PREFERENCE_GCM, result);
			ed.commit();
			
		}
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		if (DEBUG)
			Log.i(TAG, "onUnregistered " + regId);
	}

	private void showMessage(Context context, Intent intent) {
		String title = "dailyHOTEL";
		String msg = intent.getStringExtra("msg");
		String ticker = "dailyHOTEL New Message";

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);

		// 해당 어플을 실행하는 이벤트를 하고싶을 때 아래 주석을 풀어주세요
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, SplashActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
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

	public static void registerToken(Context context) {

		// 디바이스 GCM 사용 가능한지 확인
		GCMRegistrar.checkDevice(context);
		// 매니페스트 설정이 올바른지 확인
		GCMRegistrar.checkManifest(context);

		// registration ID（디바이스 토큰) 취득하고 등록되지 않은 경우 GCM에 등록
		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			GCMRegistrar.register(context, SENDER_ID);
			GCMRegistrar.setRegisteredOnServer(context, true);
			Log.v("TAG", "registered GCM");
		} else {
			if (GCMRegistrar.isRegisteredOnServer(context)) {
				Log.v("TAG", "Already registered");
				Log.v("TAG", regId);
			} else {
				GCMRegistrar.register(context, SENDER_ID);
				GCMRegistrar.setRegisteredOnServer(context, true);
				Log.v("TAG", "registered GCM");
			}
		}
	}

	public static void unregisterToken(Context context) {
		if (GCMRegistrar.isRegistered(context)) {
			GCMRegistrar.unregister(context);
		}
	}

}
