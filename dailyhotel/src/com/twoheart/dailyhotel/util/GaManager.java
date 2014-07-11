package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.app.Application;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.Logger.LogLevel;

public class GaManager extends Application{

	// Placeholder property ID.
	private static final String GA_PROPERTY_ID = "UA-43721645-1";
	private static GaManager instance = null;
	
	public static GaManager getInstance() {
		if (instance == null) { 
			instance = new GaManager();
		}
		return instance;
	}

	/**
	 * 구매 완료 하였으면 구글 애널래틱스 Ecommerce Tracking 을 위하여 필히 호출한다.
	 * @param trasId 우리 거래 고유값
	 * @param pName 호텔명
	 * @param pCategory 호텔 카테고리
	 * @param pPrice 호텔 판매가
	 */
	public void purchaseComplete(String trasId, 
			String pName, String pCategory, Double pPrice) {
		
		// affiliation = 'DailyHOTEL',Tax = 0, Shipping = 0 ,SKU = 1, Quantitiy = 1 ,Currency = 'KRW'
		// price = revenue
		
		
		GoogleAnalytics ga= GoogleAnalytics.getInstance(getApplicationContext());
		Tracker track = ga.getTracker(GA_PROPERTY_ID);
		
		
//		track.send(
//				MapBuilder.createTransaction(
//						trasId,
//		
//						)
//				);
	}
}
