package com.twoheart.dailyhotel.screen.event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EventListActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    View mEmptyView;
    ListView mListView;
    EventListAdapter mEventListAdapter;
    private EventListNetworkController mEventListNetworkController;
    private boolean mDontReload;
    private DailyDeepLink mDailyDeepLink;

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, EventListActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        initDeepLink(intent);

        setContentView(R.layout.activity_eventlist);

        mEventListNetworkController = new EventListNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        DailyPreference.getInstance(this).setNewEvent(false);
        DailyPreference.getInstance(this).setViewedEventTime(DailyPreference.getInstance(this).getLatestEventTime());

        initToolbar();
        initLayout();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);
    }

    private void initDeepLink(Intent intent)
    {
        if (intent == null || intent.hasExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == false)
        {
            return;
        }

        try
        {
            mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
        } catch (Exception e)
        {
            mDailyDeepLink = null;
        }
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_event_list_frag);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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

        mListView = findViewById(R.id.listView);
        EdgeEffectColor.setEdgeGlowColor(mListView, getResources().getColor(R.color.default_over_scroll_edge));
        mListView.setOnItemClickListener(this);

        View homeButtonView = findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
            }
        });
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(this, Screen.EVENT_LIST, null);

        super.onStart();

        if (mDailyDeepLink != null)
        {
            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                if (externalDeepLink.isEventDetailView() == true)
                {
                    startEventWeb(externalDeepLink.getUrl(), externalDeepLink.getTitle());
                }
            } else
            {

            }

            mDailyDeepLink.clear();
            mDailyDeepLink = null;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mDontReload == true)
        {
            mDontReload = false;
        } else
        {
            lockUI();
            mEventListNetworkController.requestEventList();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        Event event = mEventListAdapter.getItem(position);

        startEventWeb(event.linkUrl, event.title);

        AnalyticsManager.getInstance(EventListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.EVENT_CLICKED, event.title, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_EVENTWEB:
                mDontReload = true;

                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                    finish();
                }
                break;
        }
    }

    void startEventWeb(String url, String eventName)
    {
        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            return;
        }

        Intent intent = EventWebActivity.newInstance(EventListActivity.this, EventWebActivity.SourceType.EVENT, url, eventName);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Action Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private EventListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new EventListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onEventListResponse(List<Event> eventList)
        {
            if (mEventListAdapter == null)
            {
                mEventListAdapter = new EventListAdapter(EventListActivity.this, 0, new ArrayList<Event>());
            } else
            {
                mEventListAdapter.clear();
            }

            if (eventList == null || eventList.size() == 0)
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
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            EventListActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            EventListActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            EventListActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            EventListActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            EventListActivity.this.onErrorResponse(call, response);
        }
    };
}