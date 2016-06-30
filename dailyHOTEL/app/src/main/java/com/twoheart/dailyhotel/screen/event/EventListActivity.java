package com.twoheart.dailyhotel.screen.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    private View mEmptyView;
    private ListView mListView;
    private Event mSelectedEvent;
    private EventListAdapter mEventListAdapter;
    private EventListNetworkController mEventListNetworkController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eventlist);

        mEventListNetworkController = new EventListNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        DailyPreference.getInstance(this).setNewEvent(false);
        DailyPreference.getInstance(this).setViewedEventTime(DailyPreference.getInstance(this).getLastestEventTime());

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_event_list_frag), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        mEmptyView = findViewById(R.id.emptyLayout);

        mListView = (ListView) findViewById(R.id.listView);
        EdgeEffectColor.setEdgeGlowColor(mListView, getResources().getColor(R.color.default_over_scroll_edge));
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.EVENT_LIST);

        super.onStart();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isEventDetailView() == true)
            {
                startEventWeb(DailyDeepLink.getInstance().getUrl(), DailyDeepLink.getInstance().getEventName());
            }

            DailyDeepLink.getInstance().clear();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        mEventListNetworkController.requestEventList();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        mSelectedEvent = mEventListAdapter.getItem(position);
        mEventListNetworkController.requestEventPageUrl(mSelectedEvent);
    }

    private void startEventWeb(String url, String eventName)
    {
        if (Util.isTextEmpty(url) == true)
        {
            return;
        }

        Intent intent = EventWebActivity.newInstance(EventListActivity.this, EventWebActivity.SourceType.EVENT, url, eventName, null);
        startActivity(intent);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Action Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private EventListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new EventListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void processEventPage(String eventUrl)
        {
            startEventWeb(eventUrl, mSelectedEvent.name);

            AnalyticsManager.getInstance(EventListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.EVENT_CLICKED, mSelectedEvent.name, null);
        }

        @Override
        public void onEventListResponse(List<Event> eventList)
        {
            if (mEventListAdapter == null)
            {
                mEventListAdapter = new EventListAdapter(EventListActivity.this, 0, new ArrayList<Event>());
            }

            mEventListAdapter.clear();

            if (eventList == null)
            {
                mListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else
            {
                mListView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);

                mEventListAdapter.addAll(eventList);
                mListView.setAdapter(mEventListAdapter);
                mEventListAdapter.notifyDataSetChanged();
            }

            unLockUI();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            EventListActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            EventListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            EventListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            EventListActivity.this.onErrorToastMessage(message);
        }
    };
}