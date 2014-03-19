package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.BoardAdapter;
import com.twoheart.dailyhotel.obj.Board;
import com.twoheart.dailyhotel.util.network.OnCompleteListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class NoticeActivity extends BaseActivity {
	
	private static final String TAG = "NoticeActivity";
	
	private ArrayList<Board> list;
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
		setActionBar("공지사항");
		setContentView(R.layout.activity_board);
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		LoadingDialog.showLoading(this);
//		new GeneralHttpTask(noticeListener, getApplicationContext()).execute(REST_URL + NOTICE);
		
	}
	
	public void parseJson(String str) {
		list = new ArrayList<Board>();
		
		try {
			JSONObject jsonObj = new JSONObject(str);
			JSONArray json = jsonObj.getJSONArray("articles");
			
			for( int i=0; i< json.length(); i++) {
				
				JSONObject obj = json.getJSONObject(i);
				String subject = obj.getString("subject");
				String content = obj.getString("content");
				String regdate = obj.getString("regdate");
				
				list.add(new Board(subject, content, regdate));
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
	
	protected OnCompleteListener noticeListener = new OnCompleteListener() {
		
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
