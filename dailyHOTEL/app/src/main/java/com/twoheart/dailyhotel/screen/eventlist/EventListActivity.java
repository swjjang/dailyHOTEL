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
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.EventWebActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyPreference;
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
        AnalyticsManager.getInstance(this).recordScreen(Screen.EVENT_LIST);

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
            Intent intent = new Intent(EventListActivity.this, SignupActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, user);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_RECOMMENDER, recommender);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_ISDAILYUSER, isDailyUser);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void processEventPage(String eventUrl)
        {
            Intent intent = EventWebActivity.newInstance(EventListActivity.this, EventWebActivity.SourceType.EVENT, eventUrl);
            startActivity(intent);
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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //    private DailyHotelJsonResponseListener mDailyEventListJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            unLockUI();
    //
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code != 0)
    //                {
    //                    if (response.has("msg") == true)
    //                    {
    //                        String msg = response.getString("msg");
    //                        DailyToast.showToast(EventListActivity.this, msg, Toast.LENGTH_SHORT);
    //                    }
    //
    //                    setData(null);
    //                } else
    //                {
    //                    JSONArray eventJSONArray = response.getJSONArray("data");
    //
    //                    if (eventJSONArray == null)
    //                    {
    //                        setData(null);
    //                    } else
    //                    {
    //                        int length = eventJSONArray.length();
    //
    //                        if (length == 0)
    //                        {
    //                            setData(null);
    //                        } else
    //                        {
    //                            ArrayList<Event> eventList = new ArrayList<Event>(length);
    //
    //                            for (int i = 0; i < length; i++)
    //                            {
    //                                eventList.add(new Event(eventJSONArray.getJSONObject(i)));
    //                            }
    //
    //                            setData(eventList);
    //                        }
    //                    }
    //                }
    //            } catch (Exception e)
    //            {
    //                ExLog.d(e.toString());
    //                setData(null);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignin = jsonObject.getBoolean("is_signin");
    //
    //                    if (isSignin == true)
    //                    {
    //                        VolleyHttpClient.createCookie();
    //                        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, EventListActivity.this);
    //                        return;
    //                    }
    //                }
    //
    //                // 로그인 실패
    //                // data 초기화
    //                DailyPreference.getInstance(EventListActivity.this).removeUserInformation();
    //
    //                unLockUI();
    //
    //                // 로그인이 되어있지 않으면 회원 가입으로 이동
    //                Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
    //                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    //                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    //            } catch (JSONException e)
    //            {
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, String response)
    //        {
    //            String result = null;
    //
    //            if (false == Util.isTextEmpty(response))
    //            {
    //                result = response.trim();
    //            }
    //
    //            if (true == "alive".equalsIgnoreCase(result))
    //            {
    //                // session alive
    //                // 사용자 정보 요청.
    //                DailyNetworkAPI.getInstance().requestUserInformationEx(mNetworkTag, mUserInformationJsonResponseListener, EventListActivity.this);
    //            } else if (true == "dead".equalsIgnoreCase(result))
    //            {
    //                // session dead
    //                // 재로그인
    //                if (DailyPreference.getInstance(EventListActivity.this).isAutoLogin() == true)
    //                {
    //                    HashMap<String, String> params = Util.getLoginParams(EventListActivity.this);
    //                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, EventListActivity.this);
    //                } else
    //                {
    //                    unLockUI();
    //
    //                    // 로그인이 되어있지 않으면 회원 가입으로 이동
    //                    Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
    //                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    //                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    //                }
    //            } else
    //            {
    //                onError();
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mDailyEventPageJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            unLockUI();
    //
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code != 0)
    //                {
    //                    if (response.has("msg") == true)
    //                    {
    //                        String message = response.getString("msg");
    //                        showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null);
    //                    }
    //                } else
    //                {
    //                    String eventUrl = response.getJSONObject("data").getString("url");
    //
    //                    Intent intent = EventWebActivity.newInstance(EventListActivity.this, eventUrl);
    //                    startActivity(intent);
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //                unLockUI();
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                JSONObject jsonObject = response.getJSONObject("data");
    //
    //                Customer user = new Customer();
    //                user.setEmail(jsonObject.getString("email"));
    //                user.setName(jsonObject.getString("name"));
    //                user.setPhone(jsonObject.getString("phone"));
    //                user.setUserIdx(jsonObject.getString("idx"));
    //
    //                // 추천인
    //                int recommender = jsonObject.getInt("recommender_code");
    //                boolean isDailyUser = jsonObject.getBoolean("is_daily_user");
    //
    //                if (mSelectedEvent == null)
    //                {
    //                    DailyNetworkAPI.getInstance().requestEventList(mNetworkTag, mDailyEventListJsonResponseListener, EventListActivity.this);
    //                } else
    //                {
    //                    if (isEmptyTextField(new String[]{user.getEmail(), user.getPhone(), user.getName()}) == false && Util.isValidatePhoneNumber(user.getPhone()) == true)
    //                    {
    //                        requestEvent(mSelectedEvent, user.getUserIdx());
    //                        mSelectedEvent = null;
    //                    } else
    //                    {
    //                        // 정보 업데이트 화면으로 이동.
    //                        moveToUserInfoUpdate(user, recommender, isDailyUser);
    //                    }
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //                unLockUI();
    //            }
    //        }
    //    };
}