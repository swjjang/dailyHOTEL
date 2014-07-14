package com.twoheart.dailyhotel.util;

import android.app.Application;
import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class GaManager extends Application implements Constants{

	// Placeholder property ID.


	private static GaManager instance = null;

	private Tracker tracker;

	private GaManager(Context con) {
		GoogleAnalytics ga= GoogleAnalytics.getInstance(con);
		tracker = ga.getTracker(GA_PROPERTY_ID);
	}

	public static GaManager getInstance(Context con) {
		if (instance == null) { 
			instance = new GaManager(con);
		}
		return instance;
	}

	/**
	 * 구매 완료 하였으면 구글 애널래틱스 Ecommerce Tracking 을 위하여 필히 호출한다.
	 * 실제 우리 앱의 매출을 자동으로 집계하여 알기위함.
	 * @param trasId saleIdx
	 * @param pName 호텔명
	 * @param pCategory 호텔 카테고리
	 * @param pPrice 호텔 판매가(적립금을 사용 하는 경우 적립금을 까고 결제하는 금액)
	 */

	public void purchaseComplete(String trasId, 
			String pName, String pCategory, Double pPrice) {

		tracker.send(
				MapBuilder.createTransaction(
						trasId,
						GA_COMMERCE_DEFAULT_AFFILIATION,
						pPrice,
						GA_COMMERCE_DEFAULT_TAX,
						GA_COMMERCE_DEFAULT_SHIPPING,
						GA_COMMERCE_DEFAULT_CURRENCY_CODE
						).build()
				);

		tracker.send(
				MapBuilder.createItem(
						trasId,
						pName,
						GA_COMMERCE_DEFAULT_SKU,
						pCategory,
						pPrice,
						GA_COMMERCE_DEFAULT_QUANTITY,
						GA_COMMERCE_DEFAULT_CURRENCY_CODE
						).build()
				);
		tracker.send(MapBuilder.
				createEvent(
						"Purchase", 
						"PurchaseComplete", 
						"Purchase", 
						1L).build());
	}

	/**
	 * 회원가입 숫자를 구글 애널래틱스에서 확인하기위하여, 회원 가입에 성공하였을때 필히 호출 한다.
	 */
	public void signupComplete() {
		tracker.send(MapBuilder.
				createEvent(
						"Signup", 
						"SignupComplete", 
						"SignupComplete", 
						1L).build());
	}
}
