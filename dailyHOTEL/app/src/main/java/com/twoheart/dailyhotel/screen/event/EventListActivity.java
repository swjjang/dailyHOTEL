package com.twoheart.dailyhotel.screen.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupStep1Activity;
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eventlist);

        mEventListNetworkController = new EventListNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        DailyPreference.getInstance(this).setNewEvent(false);

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
        EdgeEffectColor.setEdgeGlowColor(mListView, getResources().getColor(R.color.over_scroll_edge));
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.EVENT_LIST, null);

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        mEventListNetworkController.requestEventList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    lockUI();
                    processViewEvent(mSelectedEvent);
                } else
                {
                    mSelectedEvent = null;
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUI();
        processViewEvent(mEventListAdapter.getItem(position));
    }

    private void processViewEvent(Event selectedEvent)
    {
        if (selectedEvent == null)
        {
            return;
        }

        mSelectedEvent = selectedEvent;

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == true)
        {
            mOnNetworkControllerListener.onSignin();
        } else
        {
            mEventListNetworkController.requestUserInformationEx();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Action Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private EventListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new EventListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRequestEvent(String userIndex)
        {
            lockUI();

            mEventListNetworkController.requestEventPageUrl(mSelectedEvent, userIndex);
        }

        @Override
        public void onUpdateUserInformation(Customer user, int recommender, boolean isDailyUser)
        {
            Intent intent = AddProfileSocialActivity.newInstance(EventListActivity.this, user, recommender);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void processEventPage(String eventUrl)
        {
            Intent intent = EventWebActivity.newInstance(EventListActivity.this, EventWebActivity.SourceType.EVENT, eventUrl, null);
            startActivity(intent);

            AnalyticsManager.getInstance(EventListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.EVENT_CLICKED, mSelectedEvent.name, null);
        }

        @Override
        public void onSignin()
        {
            DailyPreference.getInstance(EventListActivity.this).removeUserInformation();

            // 로그인이 되어있지 않으면 회원 가입으로 이동
            Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
        public void onErrorMessage(int msgCode, String message)
        {
            unLockUI();
            EventListActivity.this.onErrorMessage(msgCode, message);
        }
    };
}