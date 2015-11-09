package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.BoardListAdapter;
import com.twoheart.dailyhotel.model.Board;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FAQActivity extends BaseActivity
{
    private ArrayList<Board> mList;
    private ExpandableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_board);
        setActionBar(R.string.actionbar_title_faq_activity);

        mListView = (ExpandableListView) findViewById(R.id.expandable_list_board);
        mListView.setOnGroupExpandListener(new OnGroupExpandListener()
        {
            // expand only one
            private int mPrevExpandedChildPos = -1;

            @Override
            public void onGroupExpand(int groupPosition)
            {
                if (mPrevExpandedChildPos != -1 && groupPosition != mPrevExpandedChildPos)
                {
                    mListView.collapseGroup(mPrevExpandedChildPos);
                }

                mPrevExpandedChildPos = groupPosition;

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.FAQ, Action.CLICK, mList.get(groupPosition).getSubject(), (long) (groupPosition + 1));
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(FAQActivity.this).recordScreen(Screen.FAQ);
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();
//        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(VolleyHttpClient.URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_BOARD_FAQ).toString(), null, mBoardFAQJsonResponseListener, this));
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mBoardFAQJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {

            mList = new ArrayList<Board>();

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                JSONArray json = response.getJSONArray("articles");

                int length = json.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject obj = json.getJSONObject(i);
                    String subject = obj.getString("subject");
                    String content = obj.getString("content");
                    //					String regdate = obj.getString("regdate");

                    mList.add(new Board(subject, content, null));
                }

                mListView.setAdapter(new BoardListAdapter(FAQActivity.this, mList));
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
}
