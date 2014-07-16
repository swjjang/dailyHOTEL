package com.twoheart.dailyhotel.util;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.text.format.Time;

public class SaleCloseAlarmManager{
	
	private static SaleCloseAlarmManager instance = null;
	private Context context;
	
	SaleCloseAlarmManager(Context con) {
		super();
		this.context = con;
	}
	
	public static SaleCloseAlarmManager getInstance(Context con) {
		if(instance == null) {
			instance = new SaleCloseAlarmManager(con);
		}
		return instance;
	}
	
	private AlarmManager getManager(){
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setAlarm(Long curMilTime) {
		AlarmManager man = getManager();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(curMilTime);
		
		if(cal.get(Calendar.HOUR_OF_DAY) >= 2) {
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
		}
		
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		android.util.Log.e("TIMEE!!",cal.getTime().toString());
		
//		PendingIntent event = PendingIntent.get
//		event.
//		man.set(AlarmManager.RTC, cal.getTimeInMillis(), event);
//		man.setRepeating(type, triggerAtMillis, intervalMillis, operation);
//		man.setInexactRepeating(type, triggerAtMillis, intervalMillis, operation);t
	}

}
