package com.twoheart.dailyhotel.screen.eventlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupActivity;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends BaseActivity implements AdapterView.OnItemClickListener
{
    private View mEmptyView;
    private ListView mListView;
    private Event mSelectedEvent;
    private EventListAdapter mEventListAdapter;
    private EventListPresenter mEventListPresenter;

    public interface OnResponsePresenterListener
    {
        void onRequestEvent(String userIndex);

        void onUpdateUserInformation(Customer user, int recommender, boolean isDailyUser);

        void processEventPage(String eventUrl);

        void onSignin();

        void onEventListResponse(List<Event> eventList);

        void onInternalError();

        void onInternalError(String message);

        void onError();

        void onErrorResponse(VolleyError volleyError);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_eventlist);

        mEventListPresenter = new EventListPresenter(this, mOnResponsePresenterListener);

        DailyPreference.getInstance(this).setNewEvent(false);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_event_list_frag));
    }

    private void initLayout()
    {
        mEmptyView = findViewById(R.id.emptyLayout);

        mListView = (ListView) findViewById(R.id.listView);
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
        mEventListPresenter.requestEventList();
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
                    mEventListPresenter.requestUserAlive();
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

        mSelectedEvent = mEventListAdapter.getItem(position);
        mEventListPresenter.requestUserAlive();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // User Action Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnResponsePresenterListener mOnResponsePresenterListener = new OnResponsePresenterListener()
    {
        @Override
        public void onRequestEvent(String userIndex)
        {
            lockUI();

            mEventListPresenter.requestEventPageUrl(mSelectedEvent, userIndex);
        }

        @Override
        public void onUpdateUserInformation(Customer user, int recommender, boolean isDailyUser)
        {
            Intent intent = SignupActivity.newInstance(EventListActivity.this, user, recommender, isDailyUser);
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
        public void onInternalError()
        {
            EventListActivity.this.onInternalError();
        }

        @Override
        public void onInternalError(String message)
        {
            EventListActivity.this.onInternalError(message);
        }

        @Override
        public void onError()
        {
            EventListActivity.this.onError();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            EventListActivity.this.onErrorResponse(volleyError);
        }
    };
}