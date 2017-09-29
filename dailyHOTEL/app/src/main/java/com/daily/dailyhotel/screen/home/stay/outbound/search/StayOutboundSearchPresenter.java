package com.daily.dailyhotel.screen.home.stay.outbound.search;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.domain.StayObRecentlySuggestColumns;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.database.DailyDbHelper;
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
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundSearchPresenter extends BaseExceptionPresenter<StayOutboundSearchActivity, StayOutboundSearchViewInterface> implements StayOutboundSearchView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    private StayOutboundSearchAnalyticsInterface mAnalytics;
    private CommonRemoteImpl mCommonRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private String mKeyword;
    private People mPeople;
    private boolean mIsSuggestChanged;
    private boolean mIsShowCalendar;

    private DailyDeepLink mDailyDeepLink;

    public interface StayOutboundSearchAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventDestroy(Activity activity);

        void onEventSuggestClick(Activity activity);

        void onEventPeopleClick(Activity activity);
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

        setLastSuggestByDb();
        // 기본 성인 2명, 아동 0명
        setLastPeopleByPreference();

        notifyPeopleChanged();
        notifySuggestsChanged();

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
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_stay_outbound_search));

        if (mDailyDeepLink != null)
        {
            if (processDeepLinkBeforeCommonDateTime(mDailyDeepLink) == true)
            {
                mDailyDeepLink.clear();
                mDailyDeepLink = null;
            }
        } else
        {
            if (isSuggestChanged() == false)
            {
                onSuggestClick(false);
            }
        }
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
                    if (data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                        && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

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
                    if (data.hasExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
                    {
                        SuggestParcel suggestParcel = data.getParcelableExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mKeyword = data.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                        if (suggestParcel != null)
                        {
                            setSuggest(suggestParcel.getSuggest());
                            notifySuggestsChanged();

                            if (mIsShowCalendar == true)
                            {
                                mIsShowCalendar = false;
                                onCalendarClick();
                            }
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
                        setSuggest(null);
                        notifySuggestsChanged();
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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime()//
            .subscribe(commonDateTime ->
            {
                setCommonDateTime(commonDateTime);

                String checkInDate = DailyPreference.getInstance(getActivity()).getStayOutboundSearchCheckInDate();
                String checkOutDate = DailyPreference.getInstance(getActivity()).getStayOutboundSearchCheckOutDate();

                if (DailyTextUtils.isTextEmpty(checkInDate, checkOutDate) == true)
                {
                    setStayBookDefaultDateTime(commonDateTime);
                } else
                {
                    long currentTime;
                    long checkInTime;

                    try
                    {
                        currentTime = DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT).getTime();
                        checkInTime = DailyCalendar.convertDate(checkInDate, DailyCalendar.ISO_8601_FORMAT).getTime();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());

                        currentTime = 0;
                        checkInTime = -1;
                    }

                    if (currentTime > checkInTime)
                    {
                        setStayBookDefaultDateTime(commonDateTime);
                    } else
                    {
                        setStayBookDateTime(checkInDate, 0, checkOutDate, 0);
                    }
                }

                if (processDeepLinkAfterCommonDateTime(mDailyDeepLink, commonDateTime) == true)
                {
                    mDailyDeepLink.clear();
                    mDailyDeepLink = null;
                }

                notifyStayBookDateTimeChanged();


                screenUnLock();
            }, throwable -> onHandleErrorAndFinish(throwable)));
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

        Intent intent = StayOutboundSearchSuggestActivity.newInstance(getActivity(), "");
        startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_SUGGEST);

        if (isUserAction == true)
        {
            mAnalytics.onEventSuggestClick(getActivity());
        }
    }

    @Override
    public void onSearchKeyword()
    {
        if (mSuggest == null || mPeople == null)
        {
            return;
        }

        Intent intent;

        //        if (mSuggest.id == 0)
        //        {
        // 키워드 검색인 경우
        //            intent = StayOutboundListActivity.newInstance(getActivity(), mSuggest.city//
        //                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                , mPeople.numberOfAdults, mPeople.getChildAgeList());
        //        } else
        //        {
        StayOutboundListAnalyticsParam analyticsParam = new StayOutboundListAnalyticsParam();
        analyticsParam.keyword = mKeyword;

        // Suggest검색인 경우
        intent = StayOutboundListActivity.newInstance(getActivity(), mSuggest//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mPeople.numberOfAdults, mPeople.getChildAgeList(), analyticsParam);

        //        }

        startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_LIST);
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true || mStayBookDateTime == null)
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

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 77), true);

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

    private void setCommonDateTime(CommonDateTime commonDateTime)
    {
        mCommonDateTime = commonDateTime;
    }

    /**
     * 주의 할점은 해외 호텔은 데일리 시간이 아닌 현재 시간으로 한다.
     *
     * @param commonDateTime
     */
    private void setStayBookDefaultDateTime(CommonDateTime commonDateTime)
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
    private void setStayBookDateTime(String checkInDateTime, int checkInAfterDay, String checkOutDateTime, int checkOutAfterDay)
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

    private void setSuggest(Suggest suggest)
    {
        if (lock() == true)
        {
            return;
        }

        mSuggest = suggest;

        addCompositeDisposable(mSuggestLocalImpl.addSuggestDb(suggest).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    private void setLastSuggestByDb()
    {
        DailyDb dailyDb = DailyDbHelper.getInstance().open(getActivity());

        Suggest suggest = null;
        Cursor cursor = null;

        try
        {
            cursor = dailyDb.getStayObRecentlySuggestList(1);

            if (cursor != null && cursor.getCount() > 0)
            {
                cursor.moveToFirst();

                long id = cursor.getLong(cursor.getColumnIndex(StayObRecentlySuggestColumns._ID));
                String name = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.NAME));
                String city = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CITY));
                String country = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY));
                String countryCode = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.COUNTRY_CODE));
                String categoryKey = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.CATEGORY_KEY));
                String display = cursor.getString(cursor.getColumnIndex(StayObRecentlySuggestColumns.DISPLAY));
                double latitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(StayObRecentlySuggestColumns.LONGITUDE));

                suggest = new Suggest(id, name, city, country, countryCode, categoryKey, display, latitude, longitude);

                mIsShowCalendar = false;
            } else
            {
                mIsShowCalendar = true;
            }

        } catch (Exception e)
        {
            ExLog.e(e.toString());

            suggest = null;
            mIsShowCalendar = true;
        } finally
        {
            try
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            } catch (Exception e)
            {
            }
        }

        DailyDbHelper.getInstance().close();

        mSuggest = suggest;
    }

    private void setKeyword(String keyword)
    {
        mKeyword = keyword;
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

    private void notifyStayBookDateTimeChanged()
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

    private void notifySuggestsChanged()
    {
        if (mSuggest != null)
        {
            setSuggestChanged(true);

            getViewInterface().setSuggest(mSuggest.display);

            if (mSuggest.id != 0)
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

    private boolean processDeepLinkAfterCommonDateTime(DailyDeepLink dailyDeepLink, CommonDateTime commonDateTime)
    {
        if (dailyDeepLink == null || commonDateTime == null)
        {
            return false;
        }

        if (dailyDeepLink.isValidateLink() == false)
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
                    Suggest suggest = new Suggest();
                    suggest.id = Long.parseLong(externalDeepLink.getIndex());
                    suggest.categoryKey = externalDeepLink.getCategoryKey();
                    suggest.display = externalDeepLink.getTitle();

                    setSuggest(suggest);

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

        if (dailyDeepLink.isValidateLink() == false)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                if (externalDeepLink.isPlaceDetailView() == true)
                {
                    startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
                        , StayOutboundSearchActivity.REQUEST_CODE_DETAIL);

                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                    return true;
                }
            } else if (dailyDeepLink.isInternalDeepLink() == true)
            {
                DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) dailyDeepLink;
                if (internalDeepLink.isStayOutboundSearchSuggestView() == true)
                {
                    String keyword = internalDeepLink.getKeyword();
                    Intent intent = StayOutboundSearchSuggestActivity.newInstance(getActivity(), keyword);
                    startActivityForResult(intent, StayOutboundSearchActivity.REQUEST_CODE_SUGGEST);
                    return true;
                }
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
}
