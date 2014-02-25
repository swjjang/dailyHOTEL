package com.twoheart.dailyhotel.asynctask;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.twoheart.dailyhotel.utils.RestClient;

public class GeneralHttpTask extends AsyncTask<String, Void, String>{

	private final static String TAG = "GeneralHttpTask";
	
	private onCompleteListener listener;
	private ArrayList<ParameterElement> list;	// parameter
	private Cookie cookie;
	private CookieManager cookieManager;
	private Context context;
	
	public GeneralHttpTask(onCompleteListener listener, Context context) {
		this.listener = listener;
		this.context = context;
	}
	
	public GeneralHttpTask(onCompleteListener listener, ArrayList<ParameterElement> list, Context context) {
		this.listener = listener;
		this.list = list;  
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		CookieSyncManager.createInstance(context);
		cookieManager = CookieManager.getInstance();
		CookieSyncManager.getInstance().startSync();
	}
	
	/**
	 *  param : Http Post request url
	 */
	@Override
	protected String doInBackground(String... params) {
		Log.d(TAG, "Task Url : " + params[0]);
		
		RestClient client = RestClient.getInstance();
		client.setUrl(params[0]);
		
		if(list != null) {	// parmeter가 있을때
			for(int i=0; i<list.size(); i++) {
				Log.d(TAG, "name : " + list.get(i).getName() + "value : " + list.get(i).getValue());
				client.addParam(list.get(i).getName(), list.get(i).getValue());
				
			}
		}
		
		client.execute("POST");
		cookie = client.getCookie();
		
		return client.getResponse();
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(result.equals("NETWORK_ERROR")) {
			listener.onTaskFailed();
		} else {
			if(cookie != null) {
				String cookieString = cookie.getName() + "=" + cookie.getValue();
		        Log.e("cookieString", cookieString);
		        cookieManager.setCookie("http://dailyhotel.kr/goodnight", cookieString);
		        CookieSyncManager.getInstance().sync();
			}
			listener.onTaskComplete(result);
		}
	}
}
