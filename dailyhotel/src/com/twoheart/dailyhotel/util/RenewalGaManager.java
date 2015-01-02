package com.twoheart.dailyhotel.util;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.app.Application;
import android.content.Context;

public class RenewalGaManager extends Application {
	private static RenewalGaManager instance = null;

	private Tracker tracker;

	private RenewalGaManager(Context con) {
		GoogleAnalytics ga= GoogleAnalytics.getInstance(con);
		tracker = ga.getTracker("UA-43721645-6");
	}

	public static RenewalGaManager getInstance(Context con) {
		if (instance == null) { 
			instance = new RenewalGaManager(con);
		}
		return instance;
	}
	
	public Tracker getTracker() {
		return this.tracker;
	}
	
	public void recordScreen(String screenName, String page) {
		tracker.send(MapBuilder
			    .createAppView()
			    .set(Fields.SCREEN_NAME, screenName)
			    .set(Fields.PAGE, page)
			    .build()
			);
		
		tracker.set(Fields.SCREEN_NAME, null);
		tracker.set(Fields.PAGE, null);
	}
	
	public void recordEvent(String category, String action, String label, Long value) {
		tracker.send(MapBuilder.
                createEvent(
                		category, 
                		action, 
                		label, 
                		value).build());
	}
}
