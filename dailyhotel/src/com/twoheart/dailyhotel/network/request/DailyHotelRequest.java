package com.twoheart.dailyhotel.network.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;

import android.util.Base64;

public abstract class DailyHotelRequest<T>
		extends Request<T>implements Constants
{
	private Map<String, String> mParameters;

	public DailyHotelRequest(int method, String url, Map<String, String> parameters, ErrorListener errorListener)
	{
		this(method, url, errorListener);

		mParameters = parameters;
	}

	private DailyHotelRequest(int method, String url, ErrorListener listener)
	{
		super(method, getUrlDecoderEx(url), listener);
		setTag(listener);

		setRetryPolicy(new DefaultRetryPolicy(REQUEST_EXPIRE_JUDGE, REQUEST_MAX_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected abstract Response<T> parseNetworkResponse(NetworkResponse response);

	@Override
	protected abstract void deliverResponse(T response);

	@Override
	protected Map<String, String> getParams()
	{
		return mParameters;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("os-type", "android");
		map.put("app-version", DailyHotel.VERSION);

		return map;
	}

	public static String getUrlEncoder(final String url)
	{
		final int SEED_LENGTH = 5;
		StringBuilder encodeUrl = new StringBuilder();
		StringBuilder seedLocationNumber = new StringBuilder();

		try
		{
			String alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

			Random random = new Random(System.currentTimeMillis());
			StringBuilder seed = new StringBuilder();

			for (int i = 0; i < SEED_LENGTH; i++)
			{
				int number = random.nextInt(alphas.length());
				seed.append(alphas.charAt(number));
			}

			String firstUrl = Crypto.encrypt(seed.toString(), url);
			encodeUrl.append(firstUrl);

			for (int i = 0; i < SEED_LENGTH; i++)
			{
				int number = random.nextInt(encodeUrl.length());

				encodeUrl.insert(number, seed.charAt(i));
				seedLocationNumber.append(number).append('$');
			}

			String base64LocationNumber = new String(Base64.encodeToString(seedLocationNumber.toString().getBytes(), Base64.NO_WRAP));
			encodeUrl.insert(0, base64LocationNumber + "$");
			encodeUrl.append('$');

			ExLog.d("encoderUrl : " + encodeUrl.toString());
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}

		return encodeUrl.toString();
	}

	public static String getUrlDecoderEx(String url)
	{
		if (Constants.UNENCRYPTED_URL == true)
		{
			return url;
		}

		String param = null;
		String encoderUrl = null;

		if (url.contains("/") == true)
		{
			int index = url.indexOf('/');
			param = url.substring(index);
			encoderUrl = url.substring(0, index);
		} else if (url.contains("?") == true)
		{
			int index = url.indexOf('?');
			param = url.substring(index);
			encoderUrl = url.substring(0, index);
		} else
		{
			encoderUrl = url;
		}

		StringBuilder decodeUrl = new StringBuilder();
		String[] seperateUrl = encoderUrl.split("\\$");

		int count = seperateUrl.length / 2;

		// 앞의것 2개는 Url, 뒤의것 2개는 API
		for (int i = 0; i < count; i++)
		{
			String locatinoNumber = new String(Base64.decode(seperateUrl[i * 2], Base64.NO_WRAP));
			StringBuilder encodeUrl = new StringBuilder(locatinoNumber);
			encodeUrl.append(seperateUrl[i * 2 + 1]);

			decodeUrl.append(getUrlDecoder(encodeUrl.toString()));
		}

		if (param != null)
		{
			decodeUrl.append(param);
		}

		return decodeUrl.toString();
	}

	private static String getUrlDecoder(String url)
	{
		final int SEED_LENGTH = 5;

		String decodeUrl = null;
		String[] text = url.split("\\$");

		// 앞의 5개가 위치키이다.
		StringBuilder seed = new StringBuilder();
		StringBuilder base64Url = new StringBuilder(text[SEED_LENGTH]);
		char[] alpha = new char[1];

		for (int i = SEED_LENGTH - 1; i >= 0; i--)
		{
			try
			{
				int location = Integer.parseInt(text[i]);

				base64Url.getChars(location, location + 1, alpha, 0);
				base64Url.delete(location, location + 1);

				seed.insert(0, alpha);
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}

		try
		{
			decodeUrl = Crypto.decrypt(seed.toString(), base64Url.toString());
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}

		return decodeUrl;
	}

	//	public static void makeUrlEncoder()
	//	{
	//		DailyHotelRequest.getUrlEncoder("api/fnb/payment/session/easy");
	//	}
}
