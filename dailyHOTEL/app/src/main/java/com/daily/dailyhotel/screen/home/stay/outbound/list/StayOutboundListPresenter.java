package com.daily.dailyhotel.screen.home.stay.outbound.list;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyImageSpan;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.repository.local.SearchLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.search.stay.outbound.research.ResearchStayOutboundActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.filter.StayOutboundFilterActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.preview.StayOutboundPreviewActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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
    private static final int DAYS_OF_MAXCOUNT = 365;
    private static final int NIGHTS_OF_MAXCOUNT = 28;
    static final float DEFAULT_RADIUS = 10.0f;
    static final int NUMBER_OF_RESULTS = 20;

    private StayOutboundListAnalyticsInterface mAnalytics;
    StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    CommonRemoteImpl mCommonRemoteImpl;
    SuggestRemoteImpl mSuggestRemoteImpl;
    SearchLocalImpl mSearchLocalImpl;

    private CommonDateTime mCommonDateTime;
    StayBookDateTime mStayBookDateTime;

    StayOutboundSuggest mStayOutboundSuggest;
    People mPeople;
    StayOutboundFilters mStayOutboundFilters;
    float mRadius = DEFAULT_RADIUS;
    List<StayOutbound> mStayOutboundList;
    DailyLocationExFactory mDailyLocationExFactory;

    // 리스트 요청시에 다음이 있는지에 대한 인자들
    String mCacheKey, mCacheLocation, mCustomerSessionId;
    boolean mMoreResultsAvailable, mMoreEnabled;

    private ViewState mViewState = ViewState.LIST;
    private int mWishPosition;
    private int mWishStayIndex;

    StayOutbound mStayOutboundByLongPress;
    android.support.v4.util.Pair[] mPairsByLongPress;

    private Disposable mChangedLocationDisposable;

    DailyDeepLink mDailyDeepLink;

    boolean mIsFirstLoad;

    public enum ViewState
    {
        MAP,
        LIST
    }

    enum ScreenType
    {
        DEFAULT,
        // 마지막 스크린 상태 유지
        NONE,
        EMPTY,
        ERROR,
        SEARCH_LOCATION,
        LIST,
        SHIMMER
    }

    public interface StayOutboundListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayOutboundListAnalyticsParam analyticsParam);

        StayOutboundListAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity, boolean empty);

        void onEventStayClick(Activity activity, int index, boolean provideRewardSticker, boolean dailyChoice, boolean hasCoupon);

        void onEventDestroy(Activity activity);

        void onEventList(Activity activity, StayBookDateTime stayBookDateTime, StayOutboundSuggest suggest, int size);

        void onEventWishClick(Activity activity, int stayIndex, boolean isWish, boolean isListViewState);

        void onEventMapClick(Activity activity);

        void onEventFilterClick(Activity activity);

        void onEventCalendarClick(Activity activity);

        void onEventPeopleClick(Activity activity);

        void onEventStayClick(Activity activity);

        void onEventGourmetClick(Activity activity);

        void onEventPopularAreaClick(Activity activity, String areaName);

        void onEventChangedRadius(Activity activity, String areaName);

        void onEventResearchClick(Activity activity, StayOutboundSuggest suggest);

        void onEventApplyFilter(Activity activity, StayOutboundSuggest suggest, StayOutboundFilters stayOutboundFilters);

        StayOutboundDetailAnalyticsParam getDetailAnalyticsParam(StayOutbound stayOutbound, String grade, int rankingPosition, int listSize);
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
    public void constructorInitialize(StayOutboundListActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_search_result_data);

        mAnalytics = new StayOutboundListAnalyticsImpl();

        mViewState = ViewState.LIST;

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl();
        mCommonRemoteImpl = new CommonRemoteImpl();
        mSuggestRemoteImpl = new SuggestRemoteImpl();
        mSearchLocalImpl = new SearchLocalImpl();

        setFilter(StayOutboundFilters.SortType.RECOMMENDATION, -1);

        mIsFirstLoad = true;

        setRefresh(true);
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
            mAnalytics.setAnalyticsParam(new StayOutboundListAnalyticsParam());

            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;

                return false;
            }

            if (mDailyDeepLink != null && mDailyDeepLink.isExternalDeepLink() == true)
            {
                setRefresh(false);

                lock();

                addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().subscribe(new Consumer<CommonDateTime>()
                {
                    @Override
                    public void accept(CommonDateTime commonDateTime) throws Exception
                    {
                        DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                        StayOutboundSuggest stayOutboundSuggest = new StayOutboundSuggest();
                        stayOutboundSuggest.id = Long.parseLong(externalDeepLink.getIndex());
                        stayOutboundSuggest.categoryKey = externalDeepLink.getCategoryKey();
                        stayOutboundSuggest.display = externalDeepLink.getTitle();
                        stayOutboundSuggest.displayText = externalDeepLink.getTitle();
                        setSuggest(stayOutboundSuggest);

                        StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime);
                        setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

                        setPeople(People.DEFAULT_ADULTS, null);

                        Constants.SortType sortType = externalDeepLink.getSorting();

                        switch (sortType)
                        {
                            case HIGH_PRICE:
                                setFilter(StayOutboundFilters.SortType.HIGH_PRICE, -1);
                                break;

                            case LOW_PRICE:
                                setFilter(StayOutboundFilters.SortType.LOW_PRICE, -1);
                                break;

                            case SATISFACTION:
                                setFilter(StayOutboundFilters.SortType.SATISFACTION, -1);
                                break;

                            default:
                                setFilter(StayOutboundFilters.SortType.RECOMMENDATION, -1);
                                break;
                        }

                        notifyFilterChanged();
                        notifyToolbarChanged();

                        onRefreshAll(true);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onHandleErrorAndFinish(throwable);
                    }
                }));
            }
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
        {
            StayOutboundSuggestParcel stayOutboundSuggestParcel = intent.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

            if (stayOutboundSuggestParcel != null)
            {
                setSuggest(stayOutboundSuggestParcel.getSuggest());
            }

            String checkInDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            setStayBookDateTime(checkInDateTime, checkOutDateTime);

            int numberOfAdults = intent.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);

            setPeople(numberOfAdults, childAgeList);

            if (mStayOutboundSuggest != null && StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == true)
            {
                mStayOutboundFilters.defaultSortType = StayOutboundFilters.SortType.DISTANCE;

                setFilter(StayOutboundFilters.SortType.DISTANCE, mStayOutboundSuggest.latitude, mStayOutboundSuggest.longitude);
            }

            mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));
        } else if (intent.hasExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_KEYWORD) == true)
        {

        } else
        {
            return false;
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
        notifyToolbarChanged();

        getViewInterface().setViewTypeOptionImage(ViewState.MAP);

        if (mDailyDeepLink == null)
        {
            initView(mStayOutboundSuggest);
        }
    }

    private void initView(StayOutboundSuggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(suggest.categoryKey) == false)
        {
            getViewInterface().setRadiusVisible(false);
        } else
        {
            getViewInterface().setRadiusVisible(true);
            getViewInterface().setRadius(mRadius);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefreshAll(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRetryClick();
        }

        if (Util.supportPreview(getActivity()) == true)
        {
            if (getViewInterface().isBlurVisible() == true)
            {
                getViewInterface().setBlurVisible(getActivity(), false);
            } else
            {
                // View 타입이 리스트일때만
                if (mViewState == ViewState.LIST)
                {
                    int count = DailyPreference.getInstance(getActivity()).getCountPreviewGuide() + 1;

                    if (count == 2)
                    {
                        getViewInterface().showPreviewGuide();
                    } else if (count > 2)
                    {
                        return;
                    }

                    DailyPreference.getInstance(getActivity()).setCountPreviewGuide(count);
                }
            }
        }
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }

        mAnalytics.onEventDestroy(getActivity());
    }

    @Override
    public boolean onBackPressed()
    {
        setResultCode(Activity.RESULT_CANCELED);

        // 빈 리스트인 경우 종료한다.
        if (mStayOutboundList == null || mStayOutboundList.size() == 0)
        {
            return super.onBackPressed();
        }

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

    private void setResultCode(int resultCode)
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST, new StayOutboundSuggestParcel(mStayOutboundSuggest));
        intent.putExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, mPeople.numberOfAdults);
        intent.putExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST, mPeople.getChildAgeList());

        setResult(resultCode, intent);
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
            case StayOutboundListActivity.REQUEST_CODE_DETAIL:
                switch (resultCode)
                {
                    case BaseActivity.RESULT_CODE_DATA_CHANGED:
                        setRefresh(true);
                        break;
                }
                break;

            case StayOutboundListActivity.REQUEST_CODE_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;

            case StayOutboundListActivity.REQUEST_CODE_PEOPLE:
                onPeopleActivityResult(resultCode, data);
                break;

            case StayOutboundListActivity.REQUEST_CODE_FILTER:
                onFilterActivityResult(resultCode, data);
                break;

            case StayOutboundListActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onMyLocationClick();
                        break;

                    default:
                        break;
                }
                break;
            }

            case StayOutboundListActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
                break;

            case StayOutboundListActivity.REQUEST_CODE_PREVIEW:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        addCompositeDisposable(Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                onStayClick(mPairsByLongPress, mStayOutboundByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe());
                        break;

                    case BaseActivity.RESULT_CODE_DATA_CHANGED:
                        if (data != null && data.hasExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION) == true//
                            && data.hasExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_MY_WISH) == true)
                        {
                            int position = data.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION, -1);
                            boolean wish = data.getBooleanExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_MY_WISH, false);

                            onChangedWish(position, wish, mWishStayIndex);
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        onRefreshAll(true);
                        break;
                }
                break;

            case StayOutboundListActivity.REQUEST_CODE_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            boolean isWish = data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false);

                            onChangedWish(mWishPosition, isWish, mWishStayIndex);

                            mAnalytics.onEventWishClick(getActivity(), mWishStayIndex, isWish, mViewState == ViewState.LIST);
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        onRefreshAll(true);
                        break;
                }
                break;

            case StayOutboundListActivity.REQUEST_CODE_RESEARCH:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        StayOutboundSuggestParcel suggestParcel = data.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel != null)
                        {
                            mStayOutboundSuggest = suggestParcel.getSuggest();
                        }

                        String checkInDateTime = data.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
                        String checkOutDateTime = data.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

                        mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                        mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);

                        int numberOfAdults = data.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> arrayList = data.getIntegerArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        setPeople(numberOfAdults, arrayList);

                        if (mStayOutboundSuggest != null && StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == true)
                        {
                            mStayOutboundFilters.defaultSortType = StayOutboundFilters.SortType.DISTANCE;
                            setFilter(StayOutboundFilters.SortType.DISTANCE, mStayOutboundSuggest.latitude, mStayOutboundSuggest.longitude);
                        } else
                        {
                            mStayOutboundFilters.defaultSortType = StayOutboundFilters.SortType.RECOMMENDATION;
                            resetFilter();
                        }

                        StayOutboundListAnalyticsParam analyticsParam = mAnalytics.getAnalyticsParam();

                        if (analyticsParam != null)
                        {
                            analyticsParam.keyword = data.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_KEYWORD);
                        }
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        initView(mStayOutboundSuggest);

                        if (mViewState == ViewState.MAP)
                        {
                            onViewTypeClick();
                        }

                        mIsFirstLoad = true;
                        setScreenVisible(ScreenType.NONE, mStayOutboundFilters);

                        notifyToolbarChanged();
                        notifyFilterChanged();

                        onRefreshAll(true);
                        break;
                }
                break;
        }
    }

    private void onCalendarActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    String checkInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME);
                    String checkOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);

                    if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                    {
                        return;
                    }

                    setStayBookDateTime(checkInDateTime, checkOutDateTime);
                    notifyToolbarChanged();

                    if (mStayOutboundFilters != null && mStayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE)
                    {
                        refreshAllSortTypeDistance();
                    } else
                    {
                        onRefreshAll(true);
                    }
                }
                break;
        }
    }

    private void onPeopleActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    int numberOfAdults = intent.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                    ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                    setPeople(numberOfAdults, childAgeList);
                    notifyToolbarChanged();

                    if (mStayOutboundFilters != null && mStayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE)
                    {
                        refreshAllSortTypeDistance();
                    } else
                    {
                        onRefreshAll(true);
                    }
                }
                break;
        }
    }

    private void onFilterActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    StayOutboundFilters.SortType sortType = StayOutboundFilters.SortType.valueOf(intent.getStringExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT));
                    int rating = intent.getIntExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, -1);

                    setFilter(sortType, rating);
                    notifyFilterChanged();

                    if (sortType != null && sortType == StayOutboundFilters.SortType.DISTANCE)
                    {
                        refreshAllSortTypeDistance();
                    } else
                    {
                        onRefreshAll(true);
                    }

                    mAnalytics.onEventApplyFilter(getActivity(), mStayOutboundSuggest, mStayOutboundFilters);
                }
                break;
        }
    }

    private void refreshAllSortTypeDistance()
    {
        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == true)
        {
            notifyFilterChanged();
            onRefreshAll(true);
        } else
        {
            Observable observable = searchMyLocation(null);

            if (observable != null)
            {
                screenLock(false);

                setScreenVisible(ScreenType.SEARCH_LOCATION, mStayOutboundFilters);

                addCompositeDisposable(observable.subscribe(new Consumer<Location>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                    {
                        setFilter(StayOutboundFilters.SortType.DISTANCE, location.getLatitude(), location.getLongitude());
                        notifyFilterChanged();

                        onRefreshAll(false);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                    {
                        unLockAll();

                        setScreenVisible(ScreenType.EMPTY, mStayOutboundFilters);
                    }
                }));
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

        clearCompositeDisposable();

        setRefresh(false);

        if (mIsFirstLoad == true)
        {
            mIsFirstLoad = false;
            screenLock(false);

            setScreenVisible(ScreenType.SHIMMER, mStayOutboundFilters);
        } else
        {
            screenLock(showProgress);
            setScreenVisible(ScreenType.DEFAULT, mStayOutboundFilters);
        }

        Observable<StayOutbounds> observable;
        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == false)
        {
            observable = mStayOutboundRemoteImpl.getList(getActivity(), mStayBookDateTime, mStayOutboundSuggest.id, mStayOutboundSuggest.categoryKey//
                , mPeople, mStayOutboundFilters, NUMBER_OF_RESULTS, mCacheKey, mCacheLocation, mCustomerSessionId).observeOn(AndroidSchedulers.mainThread());
        } else
        {
            observable = mStayOutboundRemoteImpl.getList(getActivity(), mStayBookDateTime, mStayOutboundFilters.latitude, mStayOutboundFilters.longitude, mRadius//
                , mPeople, mStayOutboundFilters, NUMBER_OF_RESULTS, false, mCacheKey, mCacheLocation, mCustomerSessionId).observeOn(AndroidSchedulers.mainThread());
        }

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime()//
            , observable, (commonDateTime, stayOutbounds) -> {
                setCommonDateTime(commonDateTime);

                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stayOutbounds.activeReward);

                return stayOutbounds;
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(stayOutbounds -> {
            int size = stayOutbounds.getStayOutbound().size();

            mAnalytics.onScreen(getActivity(), size == 0);

            mAnalytics.onEventList(getActivity(), mStayBookDateTime, mStayOutboundSuggest, size);

            onStayOutbounds(stayOutbounds);

            getViewInterface().setRefreshing(false);
            unLockAll();

            if (size > 0)
            {
                addCompositeDisposable(mSearchLocalImpl.addStayObSearchResultHistory(getActivity(), mCommonDateTime//
                    , mStayBookDateTime, mStayOutboundSuggest, mPeople).observeOn(AndroidSchedulers.mainThread()).subscribe());
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();

                // 리스트를 호출하다가 에러가 난 경우 처리 방안
                // 검색 결과 없는 것으로
                getViewInterface().setRefreshing(false);
                setScreenVisible(ScreenType.ERROR, mStayOutboundFilters);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        setResultCode(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRefreshAll(boolean showProgress)
    {
        clearCache();
        setRefresh(true);
        onRefresh(showProgress);
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
            Calendar startCalendar = DailyCalendar.getInstance(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);
            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);
            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.STAY, true, 0, true);

            startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_CALENDAR);

            if (mViewState == ViewState.LIST)
            {
                mAnalytics.onEventCalendarClick(getActivity());
            }
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

        startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_PEOPLE);

        switch (mViewState)
        {
            case LIST:
                mAnalytics.onEventPeopleClick(getActivity());

            case MAP:
                break;
        }
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }

        switch (mViewState)
        {
            case LIST:
            {
                Intent intent = StayOutboundFilterActivity.newInstance(getActivity(), mStayOutboundFilters, ViewState.LIST.name());
                startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_FILTER);

                mAnalytics.onEventFilterClick(getActivity());
                break;
            }

            case MAP:
            {
                Intent intent = StayOutboundFilterActivity.newInstance(getActivity(), mStayOutboundFilters, ViewState.MAP.name());
                startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_FILTER);
                break;
            }
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
                screenLock(true);

                mViewState = ViewState.MAP;

                getViewInterface().showMapLayout(getActivity().getSupportFragmentManager());

                getViewInterface().setViewTypeOptionImage(ViewState.LIST);

                mAnalytics.onEventMapClick(getActivity());
                break;
            }

            // 현재 맵화면인 경우
            case MAP:
            {
                mViewState = ViewState.LIST;

                clearCompositeDisposable();

                getViewInterface().hideMapLayout(getActivity().getSupportFragmentManager());

                getViewInterface().setViewTypeOptionImage(ViewState.MAP);

                unLockAll();
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStayClick(android.support.v4.util.Pair[] pair, StayOutbound stayOutbound)
    {
        if (stayOutbound == null || mStayOutboundList == null || lock() == true)
        {
            return;
        }

        String imageUrl;
        if (ScreenUtils.getScreenWidth(getActivity()) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(stayOutbound.getImageMap().bigUrl) == false)
            {
                imageUrl = stayOutbound.getImageMap().bigUrl;
            } else
            {
                imageUrl = stayOutbound.getImageMap().smallUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(stayOutbound.getImageMap().mediumUrl) == false)
            {
                imageUrl = stayOutbound.getImageMap().mediumUrl;
            } else
            {
                imageUrl = stayOutbound.getImageMap().smallUrl;
            }
        }

        int rankingPosition = 0;
        for (StayOutbound searchStayOutbound : mStayOutboundList)
        {
            rankingPosition++;

            if (searchStayOutbound.index == stayOutbound.index)
            {
                break;
            }
        }

        StayOutboundDetailAnalyticsParam analyticsParam = mAnalytics.getDetailAnalyticsParam(stayOutbound//
            , getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating)//
            , rankingPosition, mStayOutboundList.size());

        analyticsParam.country = mStayOutboundSuggest.country;
        analyticsParam.city = mStayOutboundSuggest.city;

        if (Util.isUsedMultiTransition() == true && pair != null)
        {
            getActivity().setExitSharedElementCallback(new SharedElementCallback()
            {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                    for (View view : sharedElements)
                    {
                        if (view instanceof SimpleDraweeView)
                        {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair);

            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, stayOutbound.total//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPeople.numberOfAdults, mPeople.getChildAgeList(), true//
                , mViewState == ViewState.MAP ? StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP : StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                , analyticsParam)//
                , StayOutboundListActivity.REQUEST_CODE_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, stayOutbound.total//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPeople.numberOfAdults, mPeople.getChildAgeList(), false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
                , StayOutboundListActivity.REQUEST_CODE_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        mAnalytics.onEventStayClick(getActivity(), stayOutbound.index, stayOutbound.provideRewardSticker//
            , stayOutbound.dailyChoice, DailyTextUtils.isTextEmpty(stayOutbound.couponDiscountPriceText) == false);
    }

    @Override
    public void onStayLongClick(int position, android.support.v4.util.Pair[] pair, StayOutbound stayOutbound)
    {
        mPairsByLongPress = pair;
        mStayOutboundByLongPress = stayOutbound;

        getViewInterface().setBlurVisible(getActivity(), true);

        startActivityForResult(StayOutboundPreviewActivity.newInstance(getActivity(), stayOutbound.index//
            , position, stayOutbound.name//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mPeople.numberOfAdults, mPeople.getChildAgeList())//
            , StayOutboundListActivity.REQUEST_CODE_PREVIEW);
    }

    @Override
    public synchronized void onScrollList(int listSize, int lastVisibleItemPosition)
    {
        if (mMoreEnabled == true && mMoreResultsAvailable == true && lastVisibleItemPosition > listSize * 2 / 3)
        {
            mMoreEnabled = false;

            Observable<StayOutbounds> observable;
            if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == false)
            {
                observable = mStayOutboundRemoteImpl.getList(getActivity(), mStayBookDateTime, mStayOutboundSuggest.id, mStayOutboundSuggest.categoryKey//
                    , mPeople, mStayOutboundFilters, NUMBER_OF_RESULTS, mCacheKey, mCacheLocation, mCustomerSessionId).observeOn(AndroidSchedulers.mainThread());
            } else
            {
                observable = mStayOutboundRemoteImpl.getList(getActivity(), mStayBookDateTime, mStayOutboundFilters.latitude, mStayOutboundFilters.longitude, mRadius//
                    , mPeople, mStayOutboundFilters, NUMBER_OF_RESULTS, false, mCacheKey, mCacheLocation, mCustomerSessionId).observeOn(AndroidSchedulers.mainThread());
            }

            addCompositeDisposable(observable.subscribe(stayOutbounds -> {
                onStayOutbounds(stayOutbounds);

                getViewInterface().setRefreshing(false);
                unLockAll();
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();

                    mMoreEnabled = true;
                }
            }));
        }
    }

    @Override
    public void onMapReady()
    {
        getViewInterface().setStayOutboundMakeMarker(mStayOutboundList, true, true);

        unLockAll();
    }

    @Override
    public void onMarkerClick(StayOutbound stayOutbound, List<StayOutbound> stayOutboundList)
    {
        if (stayOutbound == null || stayOutboundList == null)
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

                Collections.sort(stayOutboundList, comparator);

                return stayOutboundList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutbound>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<StayOutbound> stayOutboundList) throws Exception
            {
                getViewInterface().setStayOutboundMapViewPagerList(getActivity(), stayOutboundList, mStayBookDateTime.getNights() > 1//
                    , DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerEnabled());
                getViewInterface().setMapViewPagerVisibility(true);
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

        unLockAll();
    }

    @Override
    public void onMapClick()
    {
        getViewInterface().setMapViewPagerVisibility(false);
    }

    @Override
    public void onMyLocationClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);
        Observable<Long> locationAnimationObservable = null;

        if (mViewState == ViewState.MAP)
        {
            locationAnimationObservable = getViewInterface().getLocationAnimation();
        }

        Observable observable = searchMyLocation(locationAnimationObservable);

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe(new Consumer<Location>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                {
                    getViewInterface().setMyLocation(location);
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        }
    }

    @Override
    public void onRetryClick()
    {
        if (lock() == true)
        {
            return;
        }

        setScreenVisible(ScreenType.NONE, mStayOutboundFilters);
        onRefreshAll(true);
    }

    @Override
    public void onResearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(ResearchStayOutboundActivity.newInstance(getActivity(), mCommonDateTime.openDateTime, mCommonDateTime.closeDateTime//
            , mCommonDateTime.currentDateTime, mCommonDateTime.dailyDateTime//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayOutboundSuggest, mPeople.numberOfAdults, mPeople.getChildAgeList()), StayOutboundListActivity.REQUEST_CODE_RESEARCH);

        mAnalytics.onEventResearchClick(getActivity(), mStayOutboundSuggest);
    }

    @Override
    public void onCallClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayOutboundListActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onWishClick(int position, StayOutbound stayOutbound)
    {
        if (stayOutbound == null || lock() == true)
        {
            return;
        }

        mWishPosition = position;
        mWishStayIndex = stayOutbound.index;

        boolean currentWish = stayOutbound.myWish;

        if (DailyHotel.isLogin() == true)
        {
            onChangedWish(position, !currentWish, mWishStayIndex);
        }

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.OB_STAY//
            , stayOutbound.index, !currentWish, AnalyticsManager.Screen.DAILYHOTEL_LIST), StayOutboundListActivity.REQUEST_CODE_WISH_DIALOG);
    }

    @Override
    public void onChangedLocation(LatLng latLng, float radius, float zoom)
    {
        if (latLng == null || radius <= 0.0 || zoom == 0)
        {
            return;
        }

        int numberOfResults = zoom >= 13.0f ? 100 : 20;

        if (mChangedLocationDisposable != null)
        {
            mChangedLocationDisposable.dispose();
            mChangedLocationDisposable = null;
        }

        mChangedLocationDisposable = Observable.just(numberOfResults).delaySubscription(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Integer, ObservableSource<StayOutbounds>>()
        {
            @Override
            public ObservableSource<StayOutbounds> apply(Integer numberOfResults) throws Exception
            {
                getViewInterface().setMapProgressBarVisible(true);

                return mStayOutboundRemoteImpl.getList(getActivity(), mStayBookDateTime, latLng.latitude, latLng.longitude, radius//
                    , mPeople, mStayOutboundFilters, numberOfResults, true, null, null, null);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayOutbounds>()
        {
            @Override
            public void accept(StayOutbounds stayOutbounds) throws Exception
            {
                //                ExLog.d("pinkred - result : " + stayOutbounds.getStayOutbound().size());

                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stayOutbounds.activeReward);

                getViewInterface().setStayOutboundMakeMarker(stayOutbounds.getStayOutbound(), false, false);

                unLockAll();

                getViewInterface().setMapProgressBarVisible(false);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                getViewInterface().setMapProgressBarVisible(false);
            }
        });

        addCompositeDisposable(mChangedLocationDisposable);
    }

    @Override
    public void onClearChangedLocation()
    {
        if (mChangedLocationDisposable != null)
        {
            mChangedLocationDisposable.dispose();
            mChangedLocationDisposable = null;
        }

        unLockAll();
    }

    @Override
    public void onRadiusClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showRadiusPopup();

        unLockAll();
    }

    @Override
    public void onChangedRadius(float radius)
    {
        if (lock() == true)
        {
            return;
        }

        mRadius = radius;

        onRefreshAll(true);

        mAnalytics.onEventChangedRadius(getActivity(), mStayOutboundSuggest.display);
    }

    @Override
    public void onSearchStayClick()
    {
        if (lock() == true)
        {
            return;
        }

        setResultCode(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY);
        finish();

        mAnalytics.onEventStayClick(getActivity());
    }

    @Override
    public void onSearchGourmetClick()
    {
        if (lock() == true)
        {
            return;
        }

        setResultCode(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET);
        finish();

        mAnalytics.onEventGourmetClick(getActivity());
    }

    @Override
    public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (lock() == true)
        {
            return;
        }

        mStayOutboundSuggest = stayOutboundSuggest;

        notifyToolbarChanged();

        onRefreshAll(true);

        mAnalytics.onEventPopularAreaClick(getActivity(), stayOutboundSuggest.name);
    }

    void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);
    }


    private void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
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

    void setSuggest(StayOutboundSuggest stayOutboundSuggest)
    {
        mStayOutboundSuggest = stayOutboundSuggest;
    }

    void setFilter(StayOutboundFilters.SortType sortType, int rating)
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        if (sortType == null)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        } else
        {
            mStayOutboundFilters.sortType = sortType;
        }

        mStayOutboundFilters.rating = rating;
    }

    void resetFilter()
    {
        if (mStayOutboundFilters == null)
        {
            return;
        }

        mStayOutboundFilters.reset();
        mStayOutboundFilters.latitude = 0d;
        mStayOutboundFilters.longitude = 0d;
    }

    void setFilter(StayOutboundFilters.SortType sortType, double latitude, double longitude)
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        if (sortType == null)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        } else
        {
            mStayOutboundFilters.sortType = sortType;
        }

        mStayOutboundFilters.latitude = latitude;
        mStayOutboundFilters.longitude = longitude;
    }

    protected void onChangedWish(int position, boolean wish, int stayIndex)
    {
        if (position < 0)
        {
            return;
        }

        switch (mViewState)
        {
            case LIST:
            {
                ObjectItem objectItem = getViewInterface().getObjectItem(position);

                if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                {
                    ((StayOutbound) objectItem.getItem()).myWish = wish;
                }

                getViewInterface().setWish(position, wish);
                break;
            }

            case MAP:
                getViewInterface().setMapWish(position, wish);

                List<ObjectItem> objectItemList = getViewInterface().getObjectItemList();
                int size = objectItemList.size();
                ObjectItem objectItem;

                for (int i = 0; i < size; i++)
                {
                    objectItem = objectItemList.get(i);

                    if (objectItem.mType == ObjectItem.TYPE_ENTRY)
                    {
                        StayOutbound stayOutbound = objectItem.getItem();

                        if (stayOutbound.index == stayIndex)
                        {
                            stayOutbound.myWish = wish;
                            getViewInterface().setWish(i, wish);
                        }
                    }
                }
                break;
        }
    }

    private void onStayOutbounds(StayOutbounds stayOutbounds)
    {
        if (stayOutbounds == null)
        {
            return;
        }

        final boolean isAdded;

        if (DailyTextUtils.isTextEmpty(mCacheKey, mCacheLocation, mCustomerSessionId) == true)
        {
            isAdded = false;

            if (mStayOutboundList != null)
            {
                mStayOutboundList.clear();
            }

            mStayOutboundList = stayOutbounds.getStayOutbound();
        } else
        {
            isAdded = true;

            mStayOutboundList.addAll(stayOutbounds.getStayOutbound());
        }

        if (mViewState == ViewState.MAP)
        {
            getViewInterface().setStayOutboundMakeMarker(mStayOutboundList, true, true);

            try
            {
                getViewInterface().setStayOutboundMapViewPagerList(getActivity(), mStayOutboundList, mStayBookDateTime.getNights() > 1//
                    , stayOutbounds.activeReward);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        addCompositeDisposable(Observable.just(stayOutbounds).subscribeOn(Schedulers.io()).map(new Function<StayOutbounds, List<ObjectItem>>()
        {
            @Override
            public List<ObjectItem> apply(StayOutbounds stayOutbounds) throws Exception
            {
                List<ObjectItem> objectItemList = new ArrayList<>();

                // Android 에서는 forEach를 사용하면 런타임시에 문제가 발생할수 있음.
                //                stayOutbounds.getStayOutbound().forEach((stayOutbound) -> listItemList.add(new ListItem(ListItem.TYPE_ENTRY, stayOutbound)));

                List<StayOutbound> stayOutboundList = stayOutbounds.getStayOutbound();
                if (stayOutboundList == null || stayOutboundList.size() == 0)
                {
                    return objectItemList;
                }

                boolean isDefaultSortType = mStayOutboundFilters == null || mStayOutboundFilters.sortType == StayOutboundFilters.SortType.RECOMMENDATION;
                boolean hasDailyChoice = stayOutboundList.get(0).dailyChoice == true;
                if (isDefaultSortType == true && hasDailyChoice == true)
                {
                    boolean addAllSection = false;
                    boolean addDailyChoiceSection = false;

                    for (StayOutbound stayOutbound : stayOutboundList)
                    {
                        if (stayOutbound.dailyChoice == true)
                        {
                            if (addDailyChoiceSection == false)
                            {
                                addDailyChoiceSection = true;
                                objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, getString(R.string.label_dailychoice)));
                            }
                        } else
                        {
                            if (addAllSection == false)
                            {
                                addAllSection = true;
                                objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, getString(R.string.label_all)));
                            }
                        }

                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stayOutbound));
                    }
                } else
                {
                    for (StayOutbound stayOutbound : stayOutboundList)
                    {
                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stayOutbound));
                    }
                }

                if (DailyTextUtils.isTextEmpty(stayOutbounds.cacheKey, stayOutbounds.cacheLocation, stayOutbounds.customerSessionId) == true)
                {
                    stayOutbounds.moreResultsAvailable = false;
                }

                if (isAdded == false)
                {
                    if (objectItemList.size() > 0)
                    {
                        if (stayOutbounds.moreResultsAvailable == true)
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                        } else
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                        }
                    }
                } else
                {
                    if (objectItemList.size() > 0)
                    {
                        if (stayOutbounds.moreResultsAvailable == true)
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                        } else
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                        }
                    } else
                    {
                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                    }
                }

                mCacheKey = stayOutbounds.cacheKey;
                mCacheLocation = stayOutbounds.cacheLocation;
                mCustomerSessionId = stayOutbounds.customerSessionId;
                mMoreEnabled = mMoreResultsAvailable = stayOutbounds.moreResultsAvailable;

                return objectItemList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ObjectItem>>()
        {
            @Override
            public void accept(List<ObjectItem> objectItemList) throws Exception
            {
                if (isAdded == false)
                {
                    boolean isSortByDistance = mStayOutboundFilters != null && (mStayOutboundFilters.sortType == StayOutboundFilters.SortType.DISTANCE//
                        || StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == true);

                    if (objectItemList == null || objectItemList.size() == 0)
                    {
                        setScreenVisible(ScreenType.EMPTY, mStayOutboundFilters);

                        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == false)
                        {
                            getViewInterface().setPopularAreaVisible(false);

                            addCompositeDisposable(mSuggestRemoteImpl.getPopularRegionSuggestsByStayOutbound(getActivity()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StayOutboundSuggest>>()
                            {
                                @Override
                                public void accept(List<StayOutboundSuggest> stayOutboundSuggests) throws Exception
                                {
                                    if (stayOutboundSuggests != null && stayOutboundSuggests.size() > 0)
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

                                }
                            }));
                        }
                    } else
                    {
                        setScreenVisible(ScreenType.LIST, mStayOutboundFilters);
                    }

                    getViewInterface().setStayOutboundList(objectItemList, isSortByDistance, mStayBookDateTime.getNights() > 1//
                        , stayOutbounds.activeReward);
                } else
                {
                    setScreenVisible(ScreenType.LIST, mStayOutboundFilters);

                    getViewInterface().addStayOutboundList(objectItemList);
                }
            }
        }));
    }

    void notifyFilterChanged()
    {
        if (mStayOutboundFilters == null)
        {
            return;
        }

        if (isDefaultFilter(mStayOutboundFilters) == true)
        {
            getViewInterface().setFilterOptionImage(false);
        } else
        {
            getViewInterface().setFilterOptionImage(true);
        }
    }

    private boolean isDefaultFilter(StayOutboundFilters stayOutboundFilters)
    {
        if (stayOutboundFilters == null)
        {
            return true;
        }

        return stayOutboundFilters.isDefaultFilter();
    }

    void notifyToolbarChanged()
    {
        if (mStayBookDateTime == null)
        {
            return;
        }

        String title = mStayOutboundSuggest.id == 0 ? (DailyTextUtils.isTextEmpty(mStayOutboundSuggest.city) == true ? mStayOutboundSuggest.displayText : mStayOutboundSuggest.city) : mStayOutboundSuggest.displayText;

        if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == true && DailyTextUtils.isTextEmpty(title) == true)
        {
            title = getString(R.string.label_search_nearby_empty_address);
        }

        try
        {
            String subTitleText = String.format(Locale.KOREA, "%s - %s, %d박 돋 %s"//
                , mStayBookDateTime.getCheckInDateTime("M.d(EEE)")//
                , mStayBookDateTime.getCheckOutDateTime("M.d(EEE)")//
                , mStayBookDateTime.getNights(), mPeople.toTooShortString(getActivity()));

            int startIndex = subTitleText.indexOf('돋');

            SpannableString spannableString = new SpannableString(subTitleText);
            spannableString.setSpan(new DailyImageSpan(getActivity(), R.drawable.shape_filloval_cababab, DailyImageSpan.ALIGN_VERTICAL_CENTER), startIndex, startIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setToolbarTitle(title, spannableString);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    /**
     * 리스트를 처음부터 호출한다.
     */
    private void clearCache()
    {
        mCacheKey = null;
        mCacheLocation = null;
        mCustomerSessionId = null;
        mMoreResultsAvailable = false;
        mMoreEnabled = false;

        if (mStayOutboundList != null)
        {
            mStayOutboundList.clear();
            mStayOutboundList = null;
        }
    }

    void setScreenVisible(ScreenType screenType, StayOutboundFilters filters)
    {
        if (screenType == null || getViewInterface() == null)
        {
            return;
        }

        switch (screenType)
        {
            case DEFAULT:
                break;

            case NONE:
                getViewInterface().hideEmptyScreen();
                getViewInterface().setErrorScreenVisible(false);
                getViewInterface().setSearchLocationScreenVisible(false);
                getViewInterface().setListScreenVisible(false);
                getViewInterface().setShimmerScreenVisible(false);
                break;

            case EMPTY:
                getViewInterface().setErrorScreenVisible(false);
                getViewInterface().setSearchLocationScreenVisible(false);
                getViewInterface().setListScreenVisible(false);
                getViewInterface().setShimmerScreenVisible(false);

                if (StayOutboundSuggest.CATEGORY_LOCATION.equalsIgnoreCase(mStayOutboundSuggest.categoryKey) == false)
                {
                    if (isDefaultFilter(filters) == true)
                    {
                        getViewInterface().showEmptyScreen(StayOutboundListViewInterface.EmptyScreenType.SEARCH_SUGGEST_DEFAULT);
                        getViewInterface().setBottomLayoutVisible(false);
                    } else
                    {
                        getViewInterface().showEmptyScreen(StayOutboundListViewInterface.EmptyScreenType.SEARCH_SUGGEST_FILTER_ON);
                        getViewInterface().setBottomLayoutVisible(true);
                        getViewInterface().setBottomLayoutType(StayOutboundListViewInterface.EmptyScreenType.SEARCH_SUGGEST_FILTER_ON);
                    }
                } else
                {
                    if (isDefaultFilter(filters) == true && mRadius == DEFAULT_RADIUS)
                    {
                        getViewInterface().showEmptyScreen(StayOutboundListViewInterface.EmptyScreenType.LOCATION_DEFAULT);
                        getViewInterface().setBottomLayoutVisible(false);
                    } else
                    {
                        getViewInterface().showEmptyScreen(StayOutboundListViewInterface.EmptyScreenType.LOCATOIN_FILTER_ON);
                        getViewInterface().setBottomLayoutVisible(true);
                        getViewInterface().setBottomLayoutType(StayOutboundListViewInterface.EmptyScreenType.LOCATOIN_FILTER_ON);
                    }
                }
                break;

            case ERROR:
                getViewInterface().hideEmptyScreen();
                getViewInterface().setErrorScreenVisible(true);
                getViewInterface().setSearchLocationScreenVisible(false);
                getViewInterface().setListScreenVisible(false);
                getViewInterface().setBottomLayoutVisible(false);
                getViewInterface().setShimmerScreenVisible(false);
                break;

            case SEARCH_LOCATION:
                getViewInterface().hideEmptyScreen();
                getViewInterface().setErrorScreenVisible(false);
                getViewInterface().setSearchLocationScreenVisible(true);
                getViewInterface().setListScreenVisible(false);
                getViewInterface().setBottomLayoutType(StayOutboundListViewInterface.EmptyScreenType.NONE);
                getViewInterface().setShimmerScreenVisible(false);
                break;

            case LIST:
                getViewInterface().hideEmptyScreen();
                getViewInterface().setErrorScreenVisible(false);
                getViewInterface().setSearchLocationScreenVisible(false);
                getViewInterface().setListScreenVisible(true);
                getViewInterface().setBottomLayoutType(StayOutboundListViewInterface.EmptyScreenType.NONE);
                getViewInterface().setShimmerScreenVisible(false);
                break;

            case SHIMMER:
                getViewInterface().hideEmptyScreen();
                getViewInterface().setErrorScreenVisible(false);
                getViewInterface().setSearchLocationScreenVisible(false);
                getViewInterface().setListScreenVisible(false);
                getViewInterface().setBottomLayoutType(StayOutboundListViewInterface.EmptyScreenType.NONE);
                getViewInterface().setShimmerScreenVisible(true);
                break;
        }
    }

    private Observable<Location> searchMyLocation(Observable locationAnimationObservable)
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            return null;
        }

        Disposable locationAnimationDisposable;

        if (locationAnimationObservable != null)
        {
            locationAnimationDisposable = locationAnimationObservable.subscribe();
        } else
        {
            locationAnimationDisposable = null;
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
                {
                    @Override
                    public void onRequirePermission()
                    {
                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        observer.onError(new Exception());
                    }

                    @Override
                    public void onProviderDisabled()
                    {
                        observer.onError(new ProviderException());
                    }

                    @Override
                    public void onProviderEnabled()
                    {
                        mDailyLocationExFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
                        {
                            @Override
                            public void onFailed()
                            {
                                observer.onError(new Exception());
                            }

                            @Override
                            public void onAlreadyRun()
                            {
                                observer.onError(new DuplicateRunException());
                            }

                            @Override
                            public void onLocationChanged(Location location)
                            {
                                unLockAll();

                                mDailyLocationExFactory.stopLocationMeasure();

                                if (location == null)
                                {
                                    observer.onError(new NullPointerException());
                                } else
                                {
                                    observer.onNext(location);
                                    observer.onComplete();
                                }
                            }

                            @Override
                            public void onCheckSetting(ResolvableApiException exception)
                            {
                                observer.onError(exception);
                            }
                        });
                    }
                });
            }
        }.doOnComplete(new Action()
        {
            @Override
            public void run() throws Exception
            {
                if (locationAnimationDisposable != null)
                {
                    locationAnimationDisposable.dispose();
                }
            }
        }).doOnDispose(new Action()
        {
            @Override
            public void run() throws Exception
            {
                if (locationAnimationDisposable != null)
                {
                    locationAnimationDisposable.dispose();
                }
            }
        }).doOnError(new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                if (locationAnimationDisposable != null)
                {
                    locationAnimationDisposable.dispose();
                }

                if (throwable instanceof PermissionException)
                {
                    Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    getViewInterface().showSimpleDialog(//
                        getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                        getString(R.string.dialog_btn_text_dosetting), //
                        getString(R.string.dialog_btn_text_cancel), //
                        new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, StayOutboundListActivity.REQUEST_CODE_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                            }
                        }, null, true);
                } else if (throwable instanceof DuplicateRunException)
                {

                } else if (throwable instanceof ResolvableApiException)
                {
                    try
                    {
                        ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), StayOutboundListActivity.REQUEST_CODE_SETTING_LOCATION);
                    } catch (Exception e)
                    {
                        DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }
        });
    }
}
