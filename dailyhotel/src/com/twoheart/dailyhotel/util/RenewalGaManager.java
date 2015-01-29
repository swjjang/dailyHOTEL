package com.twoheart.dailyhotel.util;

import com.google.analytics.tracking.android.Fields;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.content.Context;
import android.widget.Toast;

public class RenewalGaManager   {
	private static RenewalGaManager instance = null;
	private static int count = 0;
	private Tracker tracker;

	private RenewalGaManager(Context con) {
		GoogleAnalytics ga= GoogleAnalytics.getInstance(con);
		tracker = ga.getTracker(Constants.GA_PROPERTY_ID);
		Toast.makeText(con, Constants.GA_PROPERTY_ID+", count is " + ++count, Toast.LENGTH_LONG).show();
	}

	public static RenewalGaManager getInstance(Context con) {
		if (instance == null) { 
			instance = new RenewalGaManager(con);
		}
		return instance;
	}
	
	public static RenewalGaManager getInstance(Context con, String screen_name) {
        instance = new RenewalGaManager(con);
        instance.getTracker().set(Fields.SCREEN_NAME, screen_name);

        return instance;
	}
	
	private Tracker getTracker() {
		return this.tracker;
	}
	
	public void recordScreen(String screenName, String page) {
		tracker.send(MapBuilder
			    .createAppView()
			    .set(Fields.SCREEN_NAME, screenName)
			    .set(Fields.PAGE, page)
			    .build()
			);
		
	}
	
	public void recordEvent(String category, String action, String label, Long value) {
		tracker.send(MapBuilder.
                createEvent(
                		category, 
                		action, 
                		label, 
                		value).build());
	}
	
	
	/**
	 * 구매 완료 하였으면 구글 애널래틱스 Ecommerce Tracking 을 위하여 필히 호출한다.
	 * 실제 우리 앱의 매출을 자동으로 집계하여 알기위함.
	 * @param trasId userId+YYMMDDhhmmss
	 * @param pName 호텔명
	 * @param pCategory 호텔 카테고리
	 * @param pPrice 호텔 판매가(적립금을 사용 하는 경우 적립금을 까고 결제하는 금액)
	 */

	public void purchaseComplete(String trasId, 
			String pName, String pCategory, Double pPrice) {

		tracker.send(
				MapBuilder.createTransaction(
						trasId,
						"DailyHOTEL",
						pPrice,
						0d,
						0d,
						"KRW"
						).build()
				);

		tracker.send(
				MapBuilder.createItem(
						trasId,
						pName,
						"1",
						pCategory,
						pPrice,
						1L,
						"KRW"
						).build()
				);
		
		tracker.send(MapBuilder.
				createEvent(
						"Purchase", 
						"PurchaseComplete", 
						"PurchaseComplete", 
						1L).build());
	}
}
