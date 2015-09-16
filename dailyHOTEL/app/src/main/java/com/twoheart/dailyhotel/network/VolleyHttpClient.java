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
package com.twoheart.dailyhotel.network;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.twoheart.dailyhotel.util.AvailableNetwork;
import com.twoheart.dailyhotel.util.Constants;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class VolleyHttpClient implements Constants
{
	private static final String KEY_DAILYHOTEL_COOKIE = "JSESSIONID";

	public static final int TIME_OUT = 5000;
	public static final int MAX_RETRY = 2;

	private static RequestQueue sRequestQueue;
	private static Context sContext;
	private static HttpClient sHttpClient;

	private static CookieSyncManager mCookieSyncManager;

	public static void init(Context context)
	{
		sHttpClient = getHttpClient();

		sContext = context;
		sRequestQueue = Volley.newRequestQueue(sContext, new HttpClientStack(sHttpClient));
	}

	public static RequestQueue getRequestQueue()
	{
		synchronized (VolleyHttpClient.class)
		{
			if (sRequestQueue == null)
			{
				init(sContext);
			}
		}

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

	/**
	 * 서버 response로부터 cookie를 가져와 기억함. 로그인 요청 후 성공적으로 응답을 받았을 경우 반드시 이 메서드를 사용해야
	 * 함.
	 */
	public static void createCookie()
	{
		cookieManagerCreate();

		List<Cookie> cookies = ((DefaultHttpClient)sHttpClient).getCookieStore().getCookies();

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

					cookieManagerSync();
				}
			}
		}
	}

	public static void cookieManagerCreate()
	{
		if (mCookieSyncManager == null)
		{
			synchronized (VolleyHttpClient.class)
			{
				if (mCookieSyncManager == null)
				{
					mCookieSyncManager = CookieSyncManager.createInstance(sContext);
				}
			}
		}
	}

	public static void cookieManagerSync()
	{
		cookieManagerCreate();

		mCookieSyncManager.sync();
	}

	public static void cookieManagerStopSync()
	{
		cookieManagerCreate();

		mCookieSyncManager.stopSync();
	}

	public static void cookieManagerStartSync()
	{
		cookieManagerCreate();

		mCookieSyncManager.startSync();
	}

	// 로그아웃 시 반드시 이 메서드를 사용해야 함.
	public static void destroyCookie()
	{
		cookieManagerCreate();

		CookieManager.getInstance().removeAllCookie();
		cookieManagerSync();
	}

	private static HttpClient getHttpClient()
	{
		try
		{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sslSocketFactory = new SFSSLSocketFactory(trustStore);
			sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sslSocketFactory, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e)
		{
			return new DefaultHttpClient();
		}
	}

	static class SFSSLSocketFactory extends SSLSocketFactory
	{
		private SSLContext sslContext = SSLContext.getInstance("TLS");

		public SFSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
		{
			super(truststore);

			TrustManager trustManager = new X509TrustManager()
			{
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
				}

				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}

			};

			sslContext.init(null, new TrustManager[] { trustManager }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException
		{
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException
		{
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
