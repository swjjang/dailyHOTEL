package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.BoardListAdapter;
import com.twoheart.dailyhotel.obj.Board;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;

public class FAQActivity extends BaseActivity implements
		DailyHotelJsonResponseListener, ErrorListener {

	private static final String TAG = "HelpActivity";

	private RequestQueue mQueue;
	private ArrayList<Board> mList;
	private ExpandableListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("자주 묻는 질문");
		setContentView(R.layout.activity_board);
		mQueue = VolleyHttpClient.getRequestQueue();

		mListView = (ExpandableListView) findViewById(R.id.expandable_list_board);
		LoadingDialog.showLoading(this);

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_BOARD_FAQ).toString(),
				null, this, this));

	}
	
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	};

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				if (DEBUG)
					e.printStackTrace();
				
				Toast.makeText(this,
						"네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
						Toast.LENGTH_SHORT).show();
			} finally {
				LoadingDialog.hideLoading();
			}
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();

		Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
				Toast.LENGTH_SHORT).show();
		LoadingDialog.hideLoading();

	};

}
