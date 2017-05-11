package com.daily.dailyhotel.screen.stay.outbound.list;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ListItem;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.parcel.SuggestParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundListPresenter extends BaseExceptionPresenter<StayOutboundListActivity, StayOutboundListViewInterface> //
    implements StayOutboundListView.OnEventListener
{
    private static final int REQUEST_CODE_CALENDAR = 10000;
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    private StayOutboundListAnalyticsInterface mAnalytics;
    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private Persons mPersons;
    private List<StayOutbound> mStayOutboundList;

    // 리스트 요청시에 다음이 있는지에 대한 인자들
    private String mCacheKey, mCacheLocation;
    private boolean mMoreResultsAvailable;

    private ViewState mViewState;

    enum ViewState
    {
        MAP,
        LIST
    }

    public interface StayOutboundListAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundListPresenter(@NonNull StayOutboundListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundListViewInterface createInstanceViewInterface()
    {
        return new StayOutboundListView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundListActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_result_data);

        setAnalytics(new StayOutboundListAnalyticsImpl());

        mViewState = ViewState.LIST;

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
        {
            SuggestParcel suggestParcel = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

            if (suggestParcel != null)
            {
                mSuggest = suggestParcel.getSuggest();
            }

            String checkInDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mPersons.numberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mPersons.setChildList(intent.getStringArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST));

        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD) == true)
        {
            String keyword = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD);

            mSuggest = new Suggest();
            mSuggest.city = keyword;

            String checkInDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mPersons.numberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mPersons.setChildList(intent.getStringArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST));
        } else
        {
            return false;
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {
        if (DailyTextUtils.isTextEmpty(mSuggest.id) == true)
        {
            // 키워드 검색인 경우
            getViewInterface().setToolbarTitle(mSuggest.city);
        } else
        {
            // Suggest 검색인 경우
            getViewInterface().setToolbarTitle(mSuggest.display);
        }

        setCalendarText(mStayBookDateTime);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            clearCache();
            onRefresh();
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
    }

    @Override
    public boolean onBackPressed()
    {
        if (mViewState == ViewState.MAP)
        {
            try
            {
                if (getViewInterface().isMapViewPagerVisibility() == true)
                {
                    getViewInterface().setMapViewPagerVisibility(false);
                } else
                {
                    onViewTypeClick();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                onViewTypeClick();
            }

            return true;
        }

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
            case REQUEST_CODE_CALENDAR:
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

                        onCalendarDateTime(checkInDateTime, checkOutDateTime);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(true);

        Observable<StayOutbounds> observable;

        if (DailyTextUtils.isTextEmpty(mSuggest.id) == true)
        {
            // 키워드 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime, null//
                , mSuggest.city, mPersons, mCacheKey, mCacheLocation);
        } else
        {
            // Suggest 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime//
                , mSuggest.countryCode, mSuggest.city, mPersons, mCacheKey, mCacheLocation);
        }

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), observable//
            , (commonDateTime, stayOutbounds) ->
            {
                onCommonDateTime(commonDateTime);

                return stayOutbounds;
            }).subscribe(stayOutbounds ->
        {
            onStayOutbounds(stayOutbounds);
            screenUnLock();
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                // 리스트를 호출하다가 에러가 난 경우 처리 방안
                // 검색 결과 없는 것으로

                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
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
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, true);

            startActivityForResult(intent, REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }
    }

    @Override
    public void onViewTypeClick()
    {
        if (lock() == true || getActivity() == null)
        {
            return;
        }

        switch (mViewState)
        {
            // 현재 리스트 화면인 경우
            case LIST:
            {
                mViewState = ViewState.MAP;

                getViewInterface().showMapLayout(getActivity().getSupportFragmentManager());
                break;
            }

            // 현재 맵화면인 경우
            case MAP:
            {
                mViewState = ViewState.LIST;

                getViewInterface().hideMapLayout(getActivity().getSupportFragmentManager());
                break;
            }
        }
    }

    @Override
    public void onStayClick()
    {
        if (lock() == true)
        {
            return;
        }


    }

    @Override
    public void onStayLongClick()
    {

    }

    @Override
    public void onScrollList(int listSize, int lastVisibleItemPosition)
    {
        if (lastVisibleItemPosition > listSize / 3)
        {
            onAddList();
        }
    }

    @Override
    public void onMapReady()
    {
        getViewInterface().setStayOutboundMakeMarker(mStayOutboundList);
    }

    @Override
    public void onMarkerClick(StayOutbound stayOutbound)
    {
        if (stayOutbound == null || mStayOutboundList == null)
        {
            return;
        }

        addCompositeDisposable(Observable.just(stayOutbound).subscribeOn(Schedulers.io()).map(new Function<StayOutbound, List<StayOutbound>>()
        {
            @Override
            public List<StayOutbound> apply(@io.reactivex.annotations.NonNull StayOutbound stayOutbound) throws Exception
            {
                Comparator<StayOutbound> comparator = new Comparator<StayOutbound>()
                {
                    public int compare(StayOutbound stayOutbound1, StayOutbound stayOutbound2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(stayOutbound.latitude, stayOutbound.longitude, stayOutbound1.latitude, stayOutbound1.longitude, results1);

                        float[] results2 = new float[3];
                        Location.distanceBetween(stayOutbound.latitude, stayOutbound.longitude, stayOutbound2.latitude, stayOutbound2.longitude, results2);

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(mStayOutboundList, comparator);

                return mStayOutboundList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutbound>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<StayOutbound> stayOutboundList) throws Exception
            {
                getViewInterface().setStayOutboundMapViewPagerList(getActivity(), stayOutboundList);
            }
        }));
    }

    @Override
    public void onMarkersCompleted()
    {
        if (getViewInterface() == null)
        {
            return;
        }


    }

    @Override
    public void onMapClick()
    {

    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void onStayOutbounds(StayOutbounds stayOutbounds)
    {
        if (stayOutbounds == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mCacheKey, mCacheLocation) == true)
        {
            mStayOutboundList = stayOutbounds.getStayOutbound();
        } else
        {
            mStayOutboundList.addAll(stayOutbounds.getStayOutbound());
        }

        if (mViewState == ViewState.MAP)
        {
            getViewInterface().setStayOutboundMakeMarker(mStayOutboundList);
            getViewInterface().setStayOutboundMapViewPagerList(getActivity(), mStayOutboundList);
        }

        addCompositeDisposable(Observable.just(stayOutbounds).subscribeOn(Schedulers.io()).map(new Function<StayOutbounds, List<ListItem>>()
        {
            @Override
            public List<ListItem> apply(StayOutbounds stayOutbounds) throws Exception
            {
                List<ListItem> listItemList = new ArrayList<>();

                for (StayOutbound stayOutbound : stayOutbounds.getStayOutbound())
                {
                    listItemList.add(new ListItem(ListItem.TYPE_ENTRY, stayOutbound));
                }

                if (listItemList.size() > 0)
                {
                    if (stayOutbounds.moreResultsAvailable == true)
                    {
                        listItemList.add(new ListItem(ListItem.TYPE_LOADING_VIEW, null));
                    } else
                    {
                        listItemList.add(new ListItem(ListItem.TYPE_FOOTER_VIEW, null));
                    }
                }

                mCacheKey = stayOutbounds.cacheKey;
                mCacheLocation = stayOutbounds.cacheLocation;

                return listItemList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ListItem>>()
        {
            @Override
            public void accept(List<ListItem> listItems) throws Exception
            {
                getViewInterface().setStayOutboundList(listItems);
            }
        }));
    }

    private void setCalendarText(StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), stayBookDateTime.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onCalendarDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        setStayBookDateTime(checkInDateTime, checkOutDateTime);
        onStayBookDateTime(mStayBookDateTime);
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
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
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onStayBookDateTime(@NonNull StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), stayBookDateTime.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onAddList()
    {
        if (getActivity().isFinishing() == true || lock() == true)
        {
            return;
        }

        Observable<StayOutbounds> observable;

        if (DailyTextUtils.isTextEmpty(mSuggest.id) == true)
        {
            // 키워드 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime, null//
                , mSuggest.city, mPersons, mCacheKey, mCacheLocation);
        } else
        {
            // Suggest 검색인 경우
            observable = mStayOutboundRemoteImpl.getStayOutBoundList(mStayBookDateTime//
                , mSuggest.countryCode, mSuggest.city, mPersons, mCacheKey, mCacheLocation);
        }

        addCompositeDisposable(observable.subscribe(stayOutbounds ->
        {
            onStayOutbounds(stayOutbounds);
            unLockAll();
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                // 리스트를 호출하다가 에러가 난 경우 처리 방안
                // 검색 결과 없는 것으로

                onHandleError(throwable);
            }
        }));
    }

    /**
     * 리스트를 처음부터 호출한다.
     */
    private void clearCache()
    {
        mCacheKey = null;
        mCacheLocation = null;

        if (mStayOutboundList != null)
        {
            mStayOutboundList.clear();
            mStayOutboundList = null;
        }
    }
}
