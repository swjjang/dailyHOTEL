package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.DETAIL;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_DAY;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_IDX;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_MONTH;
import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_HOTEL_YEAR;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.ImageDetailAdapter;
import com.twoheart.dailyhotel.util.network.GeneralHttpTask;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ImageDetailActivity extends FragmentActivity implements OnClickListener{

	private static final String TAG = "ImageDetailActivity";
	
	private ImageDetailAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;
	private ArrayList<String> urlList;
	
	private Button btn_error;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_detail);
		
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
		String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
		String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
		String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
		String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);
		
		new GeneralHttpTask(imageListener, getApplicationContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
	}
	
	public void loadResource() {
		btn_error = (Button) findViewById(R.id.btn_error);
		btn_error.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == btn_error.getId()) {
			prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
			String hotel_idx = prefs.getString(PREFERENCE_HOTEL_IDX, null);
			String year = prefs.getString(PREFERENCE_HOTEL_YEAR, null);
			String month = prefs.getString(PREFERENCE_HOTEL_MONTH, null);
			String day = prefs.getString(PREFERENCE_HOTEL_DAY, null);
			
			LoadingDialog.showLoading(this);
			new GeneralHttpTask(imageListener, getApplicationContext()).execute(REST_URL + DETAIL + hotel_idx + "/" + year + "/" + month + "/" + day);
		}
	}
	
	public void parseJson(String str) {
		try {
			JSONObject obj = new JSONObject(str);
			JSONArray detailArr = obj.getJSONArray("detail");
			
			JSONObject addressObj =  detailArr.getJSONObject(0);
			JSONArray imgArr = addressObj.getJSONArray("img");
			
			urlList = new ArrayList<String>();
			
			for(int i=0; i<imgArr.length(); i++) {
				if(i==0)
					continue;
				JSONObject imgObj = imgArr.getJSONObject(i);
				urlList.add(imgObj.getString("path"));
			}
			
			adapter = new ImageDetailAdapter(getSupportFragmentManager(), urlList);
			
			pager = (ViewPager) findViewById(R.id.imagedetail_pager);
			pager.setAdapter(adapter);
			CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.imagedetail_indicator);
			this.indicator = indicator;
			indicator.setViewPager(pager);
			indicator.setSnap(true);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
//			setContentView(R.layout.fragment_error);
		}
		
//		LoadingDialog.hideLoading();
	}
	
	
	protected OnCompleteListener imageListener = new OnCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "imageListener onTaskFailed");
//			setContentView(R.layout.fragment_error);
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};

}
