package com.twoheart.dailyhotel.util;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 */
public class ABTestPreference
{
	private SharedPreferences mPreferences;
	private Editor mEditor;

	private static ABTestPreference mInstance;

	// A/B Test 기능
	private static final String KAKAOTALK_CONSULT = "1";

	public interface OnABTestListener
	{
		public void onPostExecute();
	};

	public static ABTestPreference getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (ABTestPreference.class)
			{
				if (mInstance == null)
				{
					mInstance = new ABTestPreference(context);
				}
			}
		}

		return mInstance;
	}

	private ABTestPreference(Context context)
	{
		mPreferences = context.getSharedPreferences("abTest", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	public void requestConfiguration(Context context, RequestQueue requestQueue, final OnABTestListener listener)
	{
		if (context == null || requestQueue == null)
		{
			if (listener != null)
			{
				listener.onPostExecute();
			}
			return;
		}

		StringBuffer params = new StringBuffer();

		params.append("?code=1");

		params.append("&device_id=");
		params.append(Util.getDeviceUUID(context));

		params.append("&device_type=0");

		params.append("&device_version=");
		params.append(Build.VERSION.RELEASE);

		params.append("&app_version=");
		try
		{
			params.append(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e)
		{
		}

		requestQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(Constants.URL_DAILYHOTEL_SERVER).append(Constants.URL_WEBAPI_ABTEST_TESTCASE).append(params).toString(), null, new DailyHotelJsonResponseListener()
		{
			@Override
			public void onResponse(String url, JSONObject response)
			{
				setConfiguration(response);

				if (listener != null)
				{
					listener.onPostExecute();
				}
			}
		}, new ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError arg0)
			{
				if (listener != null)
				{
					listener.onPostExecute();
				}
			}
		}));
	}

	private void setConfiguration(JSONObject jsonObject)
	{
		try
		{
			if (jsonObject == null)
			{
				throw new Exception("response == null");
			}

			int msg_code = jsonObject.getInt("msg_code");

			if (msg_code != 0)
			{
				throw new Exception("response == null");
			}

			JSONObject data = jsonObject.getJSONObject("data");
			boolean isTester = data.getBoolean("is_tester");

			setKakaotalkConsult(isTester ? 1 : 0);
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	public void feedbackKakaotalkConsult(Context context, RequestQueue requestQueue, final OnABTestListener listener)
	{
		if (context == null || requestQueue == null)
		{
			return;
		}

		StringBuffer params = new StringBuffer();
		params.append("?device_id=");
		params.append(Util.getDeviceUUID(context));

		requestQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(Constants.URL_DAILYHOTEL_SERVER).append(Constants.URL_WEBAPI_ABTEST_KAKAO_CONSULT_FEEDBACK).append(params).toString(), null, new DailyHotelJsonResponseListener()
		{
			@Override
			public void onResponse(String url, JSONObject response)
			{
				if (listener != null)
				{
					listener.onPostExecute();
				}
			}
		}, new ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError arg0)
			{
				if (listener != null)
				{
					listener.onPostExecute();
				}
			}
		}));

	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// Preference
	/////////////////////////////////////////////////////////////////////////////////////////

	public int getKakaotalkConsult()
	{
		int state = 0;

		if (mPreferences != null)
		{
			state = mPreferences.getInt(KAKAOTALK_CONSULT, 0);
		}

		return state;
	}

	public void setKakaotalkConsult(int state)
	{
		if (mEditor != null)
		{
			mEditor.putInt(KAKAOTALK_CONSULT, state);
			mEditor.commit();
		}
	}

	public void clear()
	{
		if (mEditor != null)
		{
			mEditor.clear();
			mEditor.commit();
		}
	}
}
