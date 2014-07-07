package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.BoardListAdapter;
import com.twoheart.dailyhotel.model.Board;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class FAQActivity extends BaseActivity implements
		DailyHotelJsonResponseListener {

	private static final String TAG = "HelpActivity";

	private ArrayList<Board> mList;
	private ExpandableListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("자주 묻는 질문");
		setContentView(R.layout.activity_board);

		mListView = (ExpandableListView) findViewById(R.id.expandable_list_board);

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		lockUI();
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_BOARD_FAQ).toString(),
				null, this, this));
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_BOARD_FAQ)) {
			mList = new ArrayList<Board>();

			try {
				JSONObject jsonObj = response;
				JSONArray json = jsonObj.getJSONArray("articles");

				for (int i = 0; i < json.length(); i++) {

					JSONObject obj = json.getJSONObject(i);
					String subject = obj.getString("subject");
					String content = obj.getString("content");
					String regdate = obj.getString("regdate");

					mList.add(new Board(subject, content, regdate));
				}
				
				mListView.setAdapter(new BoardListAdapter(this, mList));
			} catch (Exception e) {
				onError(e);
			} finally {
				unLockUI();
			}
		}
	}
}
