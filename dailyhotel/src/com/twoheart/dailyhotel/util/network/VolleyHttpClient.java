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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

public class VolleyHttpClient implements Constants {

	private static final String KEY_DAILYHOTEL_COOKIE = "JSESSIONID";

	public static final int TIME_OUT = 5000;
	public static final int MAX_RETRY = 2;

	public static Cookie cookie;
	public static CookieManager cookieManager;
	private static RequestQueue sRequestQueue;
	private static Context sContext;
	private static HttpClient sHttpClient;

	public static void init(Context context) {

		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				registry);
		sHttpClient = new DefaultHttpClient(cm, params);

		sContext = context;
		sRequestQueue = Volley.newRequestQueue(sContext, new HttpClientStack(
				sHttpClient));
		// sRequestQueue = Volley.newRequestQueue(sContext);

		CookieSyncManager.createInstance(sContext);
		cookieManager = CookieManager.getInstance();

	}

	public static RequestQueue getRequestQueue() {
		if (sRequestQueue != null) {
			return sRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue is not initialized.");

		}
	}

	public static Boolean isAvailableNetwork() {
		boolean result = false;

		AvailableNetwork availableNetwork = AvailableNetwork.getInstance();

		switch (availableNetwork.getNetType(sContext)) {
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

	// 서버 response로부터 cookie를 가져와 기억함.
	public static void setSessionCookie() {

		List<Cookie> cookies = ((DefaultHttpClient) sHttpClient)
				.getCookieStore().getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.size(); i++) {
				Cookie newCookie = cookies.get(i);
				if (newCookie.getName().equals(KEY_DAILYHOTEL_COOKIE)) {
					if (cookie == null) {
						cookie = newCookie;

						StringBuilder cookieString = new StringBuilder();
						cookieString.append(cookie.getName()).append("=")
								.append(cookie.getValue());

						cookieManager.setCookie(URL_DAILYHOTEL_SERVER,
								cookieString.toString());

						CookieSyncManager.getInstance().sync();
					}
				}
			}
		}
	}
	
	public static void destroyCookie() {
		VolleyHttpClient.cookieManager
				.removeAllCookie();
		VolleyHttpClient.cookie = null;
		CookieSyncManager.getInstance().sync();
	}

}
