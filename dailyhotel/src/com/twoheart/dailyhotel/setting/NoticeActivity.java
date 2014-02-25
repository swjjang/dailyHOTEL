package com.twoheart.dailyhotel.setting;

import static com.twoheart.dailyhotel.AppConstants.NOTICE;
import static com.twoheart.dailyhotel.AppConstants.REST_URL;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.asynctask.GeneralHttpTask;
import com.twoheart.dailyhotel.asynctask.onCompleteListener;
import com.twoheart.dailyhotel.utils.LoadingDialog;

public class NoticeActivity extends ActionBarActivity {
	
	private static final String TAG = "NoticeActivity";
	
	private ArrayList<BoardElement> list;
	private ExpandableListView listView;
	private ImageView iv_arrow;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Notification View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_board);
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");

		// set Title
		setTitle(Html.fromHtml("<font color='#050505'>공지사항</font>"));
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_actionbar_background));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e("Error", "Exception loading drawable"); 
		}
		
		LoadingDialog.showLoading(this);
		new GeneralHttpTask(noticeListener, getApplicationContext()).execute(REST_URL + NOTICE);
		
	}
	
	public void parseJson(String str) {
		list = new ArrayList<BoardElement>();
		
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray json = jsonObj.getJSONArray("articles");
			
			for( int i=0; i< json.length(); i++) {
				
				JSONObject obj = json.getJSONObject(i);
				String subject = obj.getString("subject");
				String content = obj.getString("content");
				String regdate = obj.getString("regdate");
				
				list.add(new BoardElement(subject, content, regdate));
			}
			LoadingDialog.hideLoading();
			
		} catch(Exception e) {
			Log.d(TAG, "TagDataParser" + "->" + e.getMessage());
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		setListView();
	}
	
	public void setListView() {
		listView = (ExpandableListView) findViewById(R.id.expandable_list_board);
		listView.setAdapter(new BoardAdapter(this, list));
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected onCompleteListener noticeListener = new onCompleteListener() {
		
		@Override
		public void onTaskFailed() {
			Log.d(TAG, "onTaskFailed");
			LoadingDialog.hideLoading();
			Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void onTaskComplete(String result) {
			parseJson(result);
		}
	};
}
