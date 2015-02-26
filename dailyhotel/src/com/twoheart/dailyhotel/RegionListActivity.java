package com.twoheart.dailyhotel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.adapter.SeparatedListAdpater;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonArrayRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonArrayResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class RegionListActivity extends BaseActivity implements OnItemClickListener, DailyHotelJsonArrayResponseListener  {
	// 제목, 설명
 
    private SeparatedListAdpater adapter;
    
    private ArrayList<String> mKoRegionList;
    private ArrayList<String> mJaRegionList;
    private ListView list;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setActionBar(getString(R.string.act_list_region_title));
		setContentView(R.layout.activity_region_list);
		
		 // 어댑터 생성
        adapter = new SeparatedListAdpater(this);
 
        list = (ListView) findViewById(R.id.list);       
        list.setOnItemClickListener(this);
        
        lockUI();
        mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST).toString(),
						null, RegionListActivity.this,
						RegionListActivity.this));
        
	}
	 
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String content = adapter.getItem(position).toString();
        RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectRegion", content, (long) (position+1));
        
        if (!content.equals(sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""))) {
			SharedPreferences.Editor editor = sharedPreference
					.edit();
			editor.putString(KEY_PREFERENCE_REGION_SELECT_BEFORE, sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));
			editor.putString(KEY_PREFERENCE_REGION_SELECT, content);
			editor.commit();
		}
        Log.d("RegionListActivity", "before region : " + sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_BEFORE, "") + " select region : " + sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, ""));

        finish();
        
    }

	@Override
	public void onResponse(String url, JSONArray response) {
		if (url.contains(URL_WEBAPI_SITE_COUNTRY_LOCATION_LIST)) {
			try {
				Log.d("RegionListActivity", "site/get ? " + response.toString());
				mJaRegionList = new ArrayList<String>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					name = obj.getString("name");
					mJaRegionList.add(name);
				}
		        
		        mQueue.add(new DailyHotelJsonArrayRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_SITE_LOCATION_LIST).toString(),
								null, RegionListActivity.this,
								RegionListActivity.this));
				
			} catch (Exception e) {
				onError(e);
			}
		}
		else if (url.contains(URL_WEBAPI_SITE_LOCATION_LIST)) {
			try {
				Log.d("RegionListActivity", "site/get ? " + response.toString());
				mKoRegionList = new ArrayList<String>();

				JSONArray arr = response;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					String name = new String();
					name = obj.getString("name");
					mKoRegionList.add(name);
				}
				
				// 배열 어댑터를 section으로 추가
		        adapter.addSection(getString(R.string.act_list_region_korea), new ArrayAdapter<String>(this, 
		                R.layout.list_row_region, mKoRegionList));
		        adapter.addSection(getString(R.string.act_list_region_japan), new ArrayAdapter<String>(this, 
		                R.layout.list_row_region, mJaRegionList));
		        list.setAdapter(adapter);
		        unLockUI();
				
			} catch (Exception e) {
				onError(e);
			}
		}
		
		
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
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
	}
	
	@Override
	public void onBackPressed() {
		
		if (sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT, "").equals("")) {
			Toast.makeText(getApplicationContext(), getString(R.string.act_list_region_select_region), Toast.LENGTH_SHORT).show();
			return;
		}
		super.onBackPressed();
	}
}
