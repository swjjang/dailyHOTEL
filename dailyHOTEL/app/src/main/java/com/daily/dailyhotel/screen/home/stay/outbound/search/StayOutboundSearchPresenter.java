package com.daily.dailyhotel.screen.home.stay.outbound.search;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.home.search.stay.outbound.suggest.SearchStayOutboundSuggestActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.calendar.StayOutboundCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */

@Deprecated
public class StayOutboundSearchPresenter extends BaseExceptionPresenter<StayOutboundSearchActivity, StayOutboundSearchViewInterface> implements StayOutboundSearchView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 365;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    private StayOutboundSearchAnalyticsInterface mAnalytics;
    private CommonRemoteImpl mCommonRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;
    private SuggestRemoteImpl mSuggestRemoteImpl;

    CommonDateTime mCommonDateTime;
    StayBookDateTime mStayBookDateTime;

    StayOutboundSuggest mStayOutboundSuggest;
    private String mKeyword;
    private String mAnalyticsClickType;
    private People mPeople;
    private boolean mIsSuggestChanged;
    boolean mIsShowCalendar;

    DailyDeepLink mDailyDeepLink;

    public interface StayOutboundSearchAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventDestroy(Activity activity);

        void onEventSuggestClick(Activity activity);

        void onEventPeopleClick(Activity activity);

        void onEventPopularSuggestClick(Activity activity, String suggestDisplayName);
    }

    public StayOutboundSearchPresenter(@NonNull StayOutboundSearchActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundSearchViewInterface createInstanceViewInterface()
    {
        return new StayOutboundSearchView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundSearchActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_data);

        setAnalytics(new StayOutboundSearchAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);
        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);

        addCompositeDisposable(mSuggestLocalImpl.getRecentlyStayOutboundSuggest().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayOutboundSuggest>()
        {
            @Override
            public void accept(StayOutboundSuggest stayOutboundSuggest) throws Exception
            {
                if (stayOutboundSuggest == null)
                {
                    mIsShowCalendar = true;
                } else
                {
                    mIsShowCalendar = false;
                }

                mStayOutboundSuggest = stayOutboundSuggest;
                notifySuggestsChanged();

                onAfterGetRecentlySuggest();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mIsShowCalendar = true;
                mStayOutboundSuggest = null;
                notifySuggestsChanged();

                onAfterGetRecentlySuggest();
            }
        }));

        // 기본 성인 2명, 아동 0명
        setLastPeopleByPreference();
        notifyPeopleChanged();

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundSearchAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;
            }
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_stay_outbound_search));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();

        mAnalytics.onEventDestroy(getActivity());
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case StayOutboundSearchActivity.REQUEST_CODE_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                        && data.hasExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                    {
                        String checkInDateTime = data.getStringExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                        {
                            return;
                        }

                        // 캘린더를 먼저보는 경우 캘린더를 띄우지 않는다.
                        mIsShowCalendar = false;

                        setStayBookDateTime(checkInDateTime, 0, checkOutDateTime, 0);
                        notifyStayBookDateTimeChanged();
                    }
                }
                break;
            }

            case StayOutboundSearchActivity.REQUEST_CODE_SUGGEST:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
                    {
                        StayOutboundSuggestParcel stayOutboundSuggestParcel = data.getParcelableExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        String keyword = data.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                        String clickType = data.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE);

                        if (stayOutboundSuggestParcel != null)
                        {
                            addCompositeDisposable(setSuggestAndKeyword(stayOutboundSuggestParcel.getSuggest(), keyword, clickType).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
                            {
                                @Override
                                public void accept(Object o) throws Exception
                                {
                                    notifySuggestsChanged();

                                    if (mIsShowCalendar == true)
                                    {
                                        mIsShowCalendar = false;
                                        onCalendarClick();
                                    }
                                }
                            }));
                        } else
                        {
                            if (isSuggestChanged() == false)
                            {
                                onBackClick();
                            }
                        }
                    }
                } else
                {
                    if (isSuggestChanged() == false)
                    {
                        onBackClick();
                    }
                }
                break;
            }

            case StayOutboundSearchActivity.REQUEST_CODE_PEOPLE:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS) == true && data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST) == true)
                    {
                        int numberOfAdults = data.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> arrayList = data.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        setPeople(numberOfAdults, arrayList);
                        notifyPeopleChanged();
                    }
                }
                break;
            }

            case StayOutboundSearchActivity.REQUEST_CODE_LIST:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_RESEARCH) == true//
                        && data.getBooleanExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_RESEARCH, false) == true)
                    {
                        addCompositeDisposable(setSuggestAndKeyword(null, null, null).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
                        {
                            @Override
                            public void accept(Object o) throws Exception
                            {
                                notifySuggestsChanged();
                            }
                        }));
                    }
                }
                break;
            }
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CommonDateTime>()
        {
            @Override
            public void accept(CommonDateTime commonDateTime) throws Exception
            {
                setCommonDateTime(commonDateTime);

                String checkInDate = DailyPreference.getInstance(getActivity()).getStayOutboundSearchCheckInDate();
                String checkOutDate = DailyPreference.getInstance(getActivity()).getStayOutboundSearchCheckOutDate();

                if (DailyTextUtils.isTextEmpty(checkInDate, checkOutDate) == true)
                {
                    setStayBookDefaultDateTime(mCommonDateTime);
                } else
                {
                    long currentTime;
                    long checkInTime;

                    try
                    {
                        currentTime = DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
                        checkInTime = DailyCalendar.convertDate(checkInDate, DailyCalendar.ISO_8601_FORMAT).getTime();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        currentTime = 0;
                        checkInTime = -1;
                    }

                    if (currentTime > checkInTime)
                    {
                        setStayBookDefaultDateTime(mCommonDateTime);
                    } else
                    {
                        setStayBookDateTime(checkInDate, 0, checkOutDate, 0);
                    }
                }

                if (processDeepLinkAfterCommonDateTime(mDailyDeepLink, mCommonDateTime) == true)
                {
                    mDailyDeepLink.clear();
                    mDailyDeepLink = null;
                }

                notifyStayBookDateTimeChanged();

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));

        addCompositeDisposable(mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
        {
            @Override
            public void accept(List<StayOutboundSuggest> stayOutboundSuggests) throws Exception
            {
                if (stayOutboundSuggests == null || stayOutboundSuggests.size() == 0)
                {
                    getViewInterface().setPopularAreaVisible(false);
                } else
                {
                    getViewInterface().setPopularAreaVisible(true);
                    getViewInterface().setPopularAreaList(stayOutboundSuggests);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setPopularAreaVisible(false);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSuggestClick(boolean isUserAction)
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = SearchStayOutboundSuggestActivity.newInstance(getActivity(), "");
        startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_SUGGEST);

        if (isUserAction == true)
        {
            mAnalytics.onEventSuggestClick(getActivity());
        }
    }

    @Override
    public void onSearchKeyword()
    {
        if (mStayOutboundSuggest == null || mPeople == null || mStayBookDateTime == null)
        {
            return;
        }

        Intent intent;

        //        if (mStayOutboundSuggest.id == 0)
        //        {
        // 키워드 검색인 경우
        //            intent = StayOutboundListActivity.newInstance(getActivity(), mStayOutboundSuggest.city//
        //                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                , mPeople.numberOfAdults, mPeople.getChildAgeList());
        //        } else
        //        {
        StayOutboundListAnalyticsParam analyticsParam = new StayOutboundListAnalyticsParam();
        analyticsParam.keyword = mKeyword;
        analyticsParam.analyticsClickType = mAnalyticsClickType;

        // Suggest검색인 경우
        intent = StayOutboundListActivity.newInstance(getActivity(), mStayOutboundSuggest//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mPeople.numberOfAdults, mPeople.getChildAgeList(), analyticsParam);

        //        }

        startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_LIST);
    }

    @Override
    public void onCalendarClick()
    {
        if (mStayBookDateTime == null || lock() == true)
        {
            return;
        }

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance();
            startCalendar.setTime(DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT));
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);

            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);

            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayOutboundCalendarActivity.newInstance(getActivity()//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 41), true);

            startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onPeopleClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent;

        if (mPeople == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), mPeople.numberOfAdults, mPeople.getChildAgeList());
        }

        startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_PEOPLE);

        mAnalytics.onEventPeopleClick(getActivity());
    }

    @Override
    public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null || lock() == true)
        {
            return;
        }

        addCompositeDisposable(setSuggestAndKeyword(stayOutboundSuggest, stayOutboundSuggest.display, AnalyticsManager.Category.OB_SEARCH_ORIGIN_RECOMMEND).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                notifySuggestsChanged();

                unLockAll();
            }
        }));

        mAnalytics.onEventPopularSuggestClick(getActivity(), stayOutboundSuggest.display);
    }

    void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    /**
     * 주의 할점은 해외 호텔은 데일리 시간이 아닌 현재 시간으로 한다.
     *
     * @param commonDateTime
     */
    void setStayBookDefaultDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        setStayBookDateTime(commonDateTime.currentDateTime, 0, commonDateTime.currentDateTime, 1);
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    void setStayBookDateTime(String checkInDateTime, int checkInAfterDay, String checkOutDateTime, int checkOutAfterDay)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            if (checkInAfterDay == 0)
            {
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            } else
            {
                mStayBookDateTime.setCheckInDateTime(checkInDateTime, checkInAfterDay);
            }

            if (checkOutAfterDay == 0)
            {
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } else
            {
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime, checkOutAfterDay);
            }

            DailyPreference.getInstance(getActivity()).setStayOutboundSearchCheckInDate(mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            DailyPreference.getInstance(getActivity()).setStayOutboundSearchCheckOutDate(mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            onHandleError(e);
        }
    }

    private Observable setSuggestAndKeyword(StayOutboundSuggest stayOutboundSuggest, String keyword, String analyticsClickType)
    {
        mStayOutboundSuggest = stayOutboundSuggest;
        mKeyword = keyword;
        mAnalyticsClickType = analyticsClickType;

        return mSuggestLocalImpl.addStayOutboundSuggestDb(stayOutboundSuggest, keyword);
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);

        DailyPreference.getInstance(getActivity()).setStayOutboundSearchPeople(mPeople.toJsonString());
    }

    private void setLastPeopleByPreference()
    {
        JSONObject jsonObject = DailyPreference.getInstance(getActivity()).getStayOutboundSearchPeople();
        mPeople = new People(jsonObject);
    }

    void notifyStayBookDateTimeChanged()
    {
        if (mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), mStayBookDateTime.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifySuggestsChanged()
    {
        if (mStayOutboundSuggest != null)
        {
            setSuggestChanged(true);

            getViewInterface().setSuggest(mStayOutboundSuggest.display);

            if (mStayOutboundSuggest.id != 0)
            {
                getViewInterface().setSearchEnable(true);
            }
        } else
        {
            getViewInterface().setSuggest(null);
            getViewInterface().setSearchEnable(false);
        }
    }

    private void notifyPeopleChanged()
    {
        if (mPeople == null)
        {
            return;
        }

        getViewInterface().setPeopleText(mPeople.toString(getActivity()));
    }

    private boolean checkDeepLinkAfterCommonDateTime(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                if (externalDeepLink.isStayOutboundSearchResultView() == true)
                {
                    return true;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    boolean processDeepLinkAfterCommonDateTime(DailyDeepLink dailyDeepLink, CommonDateTime commonDateTime)
    {
        if (dailyDeepLink == null || commonDateTime == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                if (externalDeepLink.isStayOutboundSearchResultView() == true)
                {
                    StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest();
                    stayOutboundSuggest.id = Long.parseLong(externalDeepLink.getIndex());
                    stayOutboundSuggest.categoryKey = externalDeepLink.getCategoryKey();
                    stayOutboundSuggest.display = externalDeepLink.getTitle();

                    addCompositeDisposable(setSuggestAndKeyword(stayOutboundSuggest, null, AnalyticsManager.Category.OB_SEARCH_ORIGIN_ETC).observeOn(AndroidSchedulers.mainThread()).subscribe());

                    String date = externalDeepLink.getDate();
                    int datePlus = externalDeepLink.getDatePlus();
                    int nights = 1;

                    try
                    {
                        nights = Integer.parseInt(externalDeepLink.getNights());
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    } finally
                    {
                        if (nights <= 0)
                        {
                            nights = 1;
                        }
                    }

                    Date currentDate = DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT);

                    if (DailyTextUtils.isTextEmpty(date) == false)
                    {
                        Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));

                        int checkInDateInt = Integer.parseInt(DailyCalendar.format(checkInDate, "yyyyMMdd"));
                        int currentDateInt = Integer.parseInt(DailyCalendar.format(currentDate, "yyyyMMdd"));

                        if (checkInDateInt < currentDateInt)
                        {
                            checkInDate = currentDate;
                        }

                        String checkInDateString = DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT);
                        setStayBookDateTime(checkInDateString, 0, checkInDateString, nights);
                    } else //if (datePlus >= 0)
                    {
                        String checkInDateString = DailyCalendar.format(currentDate, DailyCalendar.ISO_8601_FORMAT);
                        setStayBookDateTime(checkInDateString, datePlus, checkInDateString, datePlus + nights);
                    }

                    notifySuggestsChanged();
                    onSearchKeyword();

                    return true;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    private boolean processDeepLinkBeforeCommonDateTime(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;


                if (externalDeepLink.isStayOutboundSearchResultView() == true)
                {

                } else if (externalDeepLink.isPlaceDetailView() == true)
                {
                    startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
                        , StayOutboundSearchActivity.REQUEST_CODE_DETAIL);

                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                    return true;
                }
            } else if (dailyDeepLink.isInternalDeepLink() == true)
            {
                DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) dailyDeepLink;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return false;
    }

    private boolean isSuggestChanged()
    {
        return mIsSuggestChanged;
    }

    private void setSuggestChanged(boolean isSuggestChanged)
    {
        mIsSuggestChanged = isSuggestChanged;
    }

    void onAfterGetRecentlySuggest()
    {
        if (processDeepLinkBeforeCommonDateTime(mDailyDeepLink) == true)
        {
            mDailyDeepLink.clear();
            mDailyDeepLink = null;
        } else if (checkDeepLinkAfterCommonDateTime(mDailyDeepLink) == true)
        {
            // do nothing! - skip deepLink
        } else if (isSuggestChanged() == false)
        {
            onSuggestClick(false);
        }
    }
}
