package com.twoheart.dailyhotel.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.twoheart.dailyhotel.R;

public class Util implements Constants
{

	public static int dpToPx(Context context, double dp)
	{
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static String storeReleaseAddress()
	{
		if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
		{
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else
		{
			return URL_STORE_T_DAILYHOTEL;
		}
	}

	public static String storeReleaseAddress(String newUrl)
	{

		if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
		{
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else
		{
			return newUrl;
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		if (drawable instanceof BitmapDrawable)
			return ((BitmapDrawable) drawable).getBitmap();

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static View getActionBarView(Activity activity)
	{
		Window window = activity.getWindow();
		View v = window.getDecorView();
		int resId = activity.getResources().getIdentifier("action_bar_container", "id", "android");
		return v.findViewById(resId);
	}

	public static String dailyHotelTimeConvert(String dailyHotelTime)
	{
		final int positionOfDashPreviousHour = 8; // yy-MM-dd-hh이므로
		String correctTime = null;

		char checkOut[] = dailyHotelTime.toCharArray();
		StringBuilder parsedCheckOutTime = new StringBuilder();
		for (int i = 0; i < checkOut.length; i++)
		{
			if (i == positionOfDashPreviousHour)
				parsedCheckOutTime.append(" ");
			else
				parsedCheckOutTime.append(checkOut[i]);
		}
		parsedCheckOutTime.append(":00:00");
		correctTime = parsedCheckOutTime.toString();

		return correctTime;
	}

	public static void setLocale(Context context, String lang)
	{

		Locale locale = new Locale(lang);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = locale;
		res.updateConfiguration(conf, dm);
	}

	public static void restartApp(Context context)
	{
		if (context == null)
		{
			return;
		}

		// 메모리 해지 및 기타 바탕화면으로 빠진후에 메모리가 해지 되는 경우가 있어 강제 종료후에 다시 재실행한다.
		// 에러 후에 알람으로 다시 실행시키기.
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
		System.exit(0);
	}

	public static void finishOutOfMemory(Activity activity)
	{
		if (activity != null && activity.isFinishing() == true)
		{
			return;
		}

		// 세션이 만료되어 재시작 요청.
		SimpleAlertDialog.build(activity, activity.getString(R.string.dialog_notice2), activity.getString(R.string.dialog_msg_outofmemory), activity.getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				System.exit(0);
			}
		}, null).setCancelable(false).show();
	}

	public static String getDeviceUUID(final Context context)
	{
		UUID uuid = null;

		final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		try
		{
			if (!"9774d56d682e549c".equals(androidId))
			{
				uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
			} else
			{
				final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
			}
		} catch (UnsupportedEncodingException e)
		{
			ExLog.d(e.toString());
		}

		if (uuid != null)
		{
			return uuid.toString();
		} else
		{
			return null;
		}
	}

	public static int getLCDWidth(Context context)
	{
		if (context == null)
		{
			return 0;
		}

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

		return displayMetrics.widthPixels;
	}

	public static int getLCDHeight(Context context)
	{
		if (context == null)
		{
			return 0;
		}

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

		return displayMetrics.heightPixels;
	}

	public static boolean isNameCharacter(String text)
	{
		boolean result = false;

		if (TextUtils.isEmpty(text) == false)
		{
			result = Pattern.matches("^[a-zA-Z\\s.'-]+$", text);
		}

		return result;
	}

	public static boolean isOverAPI11()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean isOverAPI21()
	{
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public static boolean isTelephonyEnabled(Context context)
	{
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
	}

	public static boolean isTextEmpty(String text)
	{
		return (TextUtils.isEmpty(text) == true || "null".equalsIgnoreCase(text) == true);
	}

	public static String getAppVersion(Context context)
	{
		String version = null;
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e)
		{
			ExLog.d(e.toString());
		}

		return version;
	}
}
