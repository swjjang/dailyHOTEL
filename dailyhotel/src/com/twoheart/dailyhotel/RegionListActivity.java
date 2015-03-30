/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * RegionListActivity (지역리스트 화면)
 * 
 * 지역리스트를 보여주는 화면
 * 1. 호텔리스트 화면의 우측상단 돋보기 버튼을 클릭
 * 2. 앱을 처음 설치하고 가이드 화면이 종료된 후 
 * 이 화면이 보여진다.  
 * 
 */
package com.twoheart.dailyhotel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.adapter.SeparatedListAdpater;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class RegionListActivity extends BaseActivity implements OnItemClickListener
{
	private SeparatedListAdpater adapter;

	private ArrayList<String> mKoRegionList;
	private ArrayList<String> mJaRegionList;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setActionBar(getString(R.string.act_list_region_title));
		setContentView(R.layout.activity_region_list);

		// 어댑터 생성
		adapter = new SeparatedListAdpater(this);

		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);

		lockUI();
		mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(), null, mSiteCountryLocationListJsonArrayResponseListener, RegionListActivity.this));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		String content = adapter.getItem(position).toString();
		RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectRegion", content, (long) (position + 1));

		if (!content.equals(sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "")))
		{
			SharedPreferences.Editor editor = sharedPreference.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, content);
			editor.commit();
		}

		finish();

	}

	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		// Inflate the menu; this adds items to the action bar if it is present.
	//		getMenuInflater().inflate(R.menu.region_list, menu);
	//		return true;
	//	}
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		// Handle action bar item clicks here. The action bar will
	//		// automatically handle clicks on the Home/Up button, so long
	//		// as you specify a parent activity in AndroidManifest.xml.
	//		int id = item.getItemId();
	//		if (id == R.id.action_settings) {
	//			return true;
	//		}
	//		return super.onOptionsItemSelected(item);
	//	}

	@Override
	public void finish()
	{
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
	}

	@Override
	public void onBackPressed()
	{
		// 선택된 지역이 없는 경우(앱을 처음 깔고 들어온 경우)에 지역을 선택하지 않고 back을 누를경우
		// 지역을 선택하라는 토스트를 띄워줌.
		if (sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "").equals(""))
		{
			showToast(getString(R.string.act_list_region_select_region), Toast.LENGTH_SHORT, false);
			return;
		}
		super.onBackPressed();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonArrayResponseListener mSiteCountryLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
	{

		@Override
		public void onResponse(String url, JSONArray response)
		{

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				ExLog.d("site/get ? " + response.toString());

				int length = response.length();
				mJaRegionList = new ArrayList<String>(length);

				for (int i = 0; i < length; i++)
				{
					JSONObject obj = response.getJSONObject(i);
					String name = obj.getString("name");

					mJaRegionList.add(name);
				}

				mQueue.add(new DailyHotelJsonArrayRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SITE_LOCATION_LIST).toString(), null, mSiteLocationListJsonArrayResponseListener, RegionListActivity.this));
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonArrayResponseListener mSiteLocationListJsonArrayResponseListener = new DailyHotelJsonArrayResponseListener()
	{

		@Override
		public void onResponse(String url, JSONArray response)
		{

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				ExLog.d("site/get ? " + response.toString());

				int length = response.length();
				mKoRegionList = new ArrayList<String>(length);

				for (int i = 0; i < length; i++)
				{
					JSONObject obj = response.getJSONObject(i);
					String name = obj.getString("name");

					mKoRegionList.add(name);
				}

				// 배열 어댑터를 section으로 추가
				// site/get API => 대한민국의 지역리스트를 반환함.
				// site/get/country API => 해외의 지역리스트를 반환함. 현재는 일본의 지역리스트를 반환함.
				// 지역리스트를 받아올 때 대한민국, 일본 과 같은 국가이름을 받아오지 못함.
				// 따라서 현재는 대한민국, 일본의 지역리스트를 각각의 list에 담아 추가함.

				adapter.addSection(getString(R.string.act_list_region_korea), new ArrayAdapter<String>(RegionListActivity.this, R.layout.list_row_region, mKoRegionList));
				adapter.addSection(getString(R.string.act_list_region_japan), new ArrayAdapter<String>(RegionListActivity.this, R.layout.list_row_region, mJaRegionList));
				list.setAdapter(adapter);
				unLockUI();

			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	//	@Override
	//	public void onResponse(String url, JSONArray response) {
	//		if (url.contains(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST)) {
	//			try {
	//				ExLog.d("site/get ? " + response.toString());
	//				mJaRegionList = new ArrayList<String>();
	//
	//				JSONArray arr = response;
	//				for (int i = 0; i < arr.length(); i++) {
	//					JSONObject obj = arr.getJSONObject(i);
	//					String name = new String();
	//					name = obj.getString("name");
	//					mJaRegionList.add(name);
	//				}
	//		        
	//		        mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
	//						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
	//								URL_WEBAPI_SITE_LOCATION_LIST).toString(),
	//								null, RegionListActivity.this,
	//								RegionListActivity.this));
	//				
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//		else if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
	//			try {
	//				ExLog.d("site/get ? " + response.toString());
	//				mKoRegionList = new ArrayList<String>();
	//
	//				JSONArray arr = response;
	//				for (int i = 0; i < arr.length(); i++) {
	//					JSONObject obj = arr.getJSONObject(i);
	//					String name = new String();
	//					name = obj.getString("name");
	//					mKoRegionList.add(name);
	//				}
	//				
	//				// 배열 어댑터를 section으로 추가
	//				// site/get API => 대한민국의 지역리스트를 반환함.
	//				// site/get/country API => 해외의 지역리스트를 반환함. 현재는 일본의 지역리스트를 반환함.
	//				// 지역리스트를 받아올 때 대한민국, 일본 과 같은 국가이름을 받아오지 못함.
	//				// 따라서 현재는 대한민국, 일본의 지역리스트를 각각의 list에 담아 추가함.
	//				
	//		        adapter.addSection(getString(R.string.act_list_region_korea), new ArrayAdapter<String>(this, 
	//		                R.layout.list_row_region, mKoRegionList));
	//		        adapter.addSection(getString(R.string.act_list_region_japan), new ArrayAdapter<String>(this, 
	//		                R.layout.list_row_region, mJaRegionList));
	//		        list.setAdapter(adapter);
	//		        unLockUI();
	//				
	//			} catch (Exception e) {
	//				onError(e);
	//			}
	//		}
	//		
	//		
	//	}
}
