package com.twoheart.dailyhotel.util;

import android.content.Context;
import android.os.PowerManager;

public class WakeLock {

	private static PowerManager.WakeLock wakeLock;

	public static void acquireWakeLock(Context context, int level) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(level, context.getClass()
				.getName());
		
		if (wakeLock != null) wakeLock.acquire();

	}

	public static void releaseWakeLock() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
	}

}
