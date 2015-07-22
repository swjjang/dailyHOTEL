/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * VolleyHttpClient
 * 
 * 네트워크 이미지 처리 및 네트워크 처리 작업을 담당하는 외부 라이브러리 Vol
 * ley를 네트워크 처리 작업을 목적으로 사용하기 위해 설정하는 유틸 클래스이다. 
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.network;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

public class VolleyHttpClient implements Constants
{

	private static final String KEY_DAILYHOTEL_COOKIE = "JSESSIONID";

	public static final int TIME_OUT = 5000;
	public static final int MAX_RETRY = 2;

	private static RequestQueue sRequestQueue;
	private static Context sContext;
	private static HttpClient sHttpClient;

	public static void init(Context context)
	{

		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
		sHttpClient = new DefaultHttpClient(cm, params);

		sContext = context;
		sRequestQueue = Volley.newRequestQueue(sContext, new HttpClientStack(sHttpClient));
		// sRequestQueue = Volley.newRequestQueue(sContext);
	}

	public static RequestQueue getRequestQueue()
	{
		if (sRequestQueue == null)
			init(sContext);
		return sRequestQueue;

	}

	public static Boolean isAvailableNetwork()
	{
		boolean result = false;

		AvailableNetwork availableNetwork = AvailableNetwork.getInstance();

		switch (availableNetwork.getNetType(sContext))
		{
			case AvailableNetwork.NET_TYPE_WIFI:
				// WIFI 연결상태
				result = true;
				break;
			case AvailableNetwork.NET_TYPE_3G:
				// 3G 혹은 LTE연결 상태
				result = true;
				break;
			case AvailableNetwork.NET_TYPE_NONE:
				result = false;
				break;
		}
		return result;
	}

	public static boolean hasActiveNetwork()
	{
		return AvailableNetwork.getInstance().hasActiveNetwork(sContext);
	}

	// 서버 response로부터 cookie를 가져와 기억함.
	// 로그인 요청 후 성공적으로 응답을 받았을 경우 반드시 이 메서드를 사용해야 함.
	public static void createCookie()
	{

		//		if (CookieManager.getInstance().getCookie(URL_DAILYHOTEL_SERVER) != null)
		//			Log.e("Common: " + CookieManager.getInstance().getCookie(URL_DAILYHOTEL_SERVER));

		List<Cookie> cookies = ((DefaultHttpClient) sHttpClient).getCookieStore().getCookies();

		if (cookies != null)
		{
			for (int i = 0; i < cookies.size(); i++)
			{
				Cookie newCookie = cookies.get(i);

				if (newCookie.getName().equals(KEY_DAILYHOTEL_COOKIE))
				{
					StringBuilder cookieString = new StringBuilder();
					cookieString.append(newCookie.getName()).append("=").append(newCookie.getValue());
					CookieManager.getInstance().setAcceptCookie(true);

					CookieManager.getInstance().setCookie(newCookie.getDomain(), cookieString.toString());

					CookieSyncManager.getInstance().sync();
				}
			}
		}
	}

	// 로그아웃 시 반드시 이 메서드를 사용해야 함.
	public static void destroyCookie()
	{
		CookieManager.getInstance().removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

}
