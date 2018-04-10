package com.daily.dailyhotel.screen.home.search.stay.inbound.result.search;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.GoogleAddress;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultViewModel;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.preview.StayPreviewActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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
public class SearchStayResultListFragmentPresenter extends BasePagerFragmentPresenter<SearchStayResultListFragment, SearchStayResultListFragmentInterface.ViewInterface>//
    implements SearchStayResultListFragmentInterface.OnEventListener
{
    static final int MAXIMUM_NUMBER_PER_PAGE = 200;

    static final int PAGE_NONE = -1;
    static final int PAGE_FINISH = Integer.MAX_VALUE;

    SearchStayResultListFragmentInterface.AnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;
    CampaignTagRemoteImpl mCampaignTagRemoteImpl;
    GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;

    SearchStayResultViewModel mViewModel;

    Observer mViewTypeObserver;
    Observer mFilterObserver;
    Observer mBookDateTimeObserver;
    Observer mRadiusObserver;

    int mPage = PAGE_NONE; // 리스트에서 페이지
    boolean mMoreRefreshing; // 특정 스크를 이상 내려가면 더보기로 목록을 요청하는데 lock()걸리면 안되지만 계속 요청되면 안되어서 해당 키로 락을 건다.
    boolean mNeedToRefresh; // 화면 리플래쉬가 필요한 경우
    SearchStayResultTabPresenter.ViewType mViewType;
    String mCallByScreen;

    //
    Stay mStayByLongPress;
    int mListCountByLongPress;
    android.support.v4.util.Pair[] mPairsByLongPress;

    //
    Category mCategory;
    int mWishPosition;
    boolean mEmptyList; // 목록, 맵이 비어있는지 확인
    DailyLocationExFactory mDailyLocationFactory;

    public SearchStayResultListFragmentPresenter(@NonNull SearchStayResultListFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        mCallByScreen = bundle.getString("callByScreen");
        mCategory = new Category(bundle.getString("categoryCode"), bundle.getString("categoryName"));

        return getViewInterface().getContentView(inflater, R.layout.fragment_search_gourmet_result_data, container);
    }

    @NonNull
    @Override
    protected SearchStayResultListFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchStayResultListFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new SearchStayResultListFragmentAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl();
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl();
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl();

        initViewModel(activity);

        setRefresh(false);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mViewModel = ViewModelProviders.of(activity).get(SearchStayResultViewModel.class);

        mViewTypeObserver = new Observer<SearchStayResultTabPresenter.ViewType>()
        {
            @Override
            public void onChanged(@Nullable SearchStayResultTabPresenter.ViewType viewType)
            {
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {
                    getViewInterface().scrollStop();

                    // 이전 타입이 맵이고 리스트로 이동하는 경우 리스트를 재 호출 한다.
                    if (mViewType == SearchStayResultTabPresenter.ViewType.MAP && viewType == SearchStayResultTabPresenter.ViewType.LIST)
                    {
                        mNeedToRefresh = true;
                    }

                    setViewType(viewType);
                }
            }
        };

        mViewModel.setViewTypeObserver(activity, mViewTypeObserver);

        mBookDateTimeObserver = new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                // 날짜가 변경되면 해당 화면 진입시 재 로딩할 준비를 한다.
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {

                }

                if (mViewType == SearchStayResultTabPresenter.ViewType.MAP)
                {
                    getViewInterface().setMapViewPagerVisible(false);
                }
            }
        };

        mViewModel.setBookDateTimeObserver(activity, mBookDateTimeObserver);

        mFilterObserver = new Observer<StayFilter>()
        {
            @Override
            public void onChanged(@Nullable StayFilter filter)
            {
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {

                }

                if (mViewType == SearchStayResultTabPresenter.ViewType.MAP)
                {
                    getViewInterface().setMapViewPagerVisible(false);
                }
            }
        };

        mViewModel.setFilterObserver(activity, mFilterObserver);

        mRadiusObserver = new Observer<Float>()
        {
            @Override
            public void onChanged(@Nullable Float radius)
            {
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {

                }

                if (mViewType == SearchStayResultTabPresenter.ViewType.MAP)
                {
                    getViewInterface().setMapViewPagerVisible(false);
                }
            }
        };

        mViewModel.setRadiusObserver(activity, mRadiusObserver);
    }

    boolean isCurrentFragment()
    {
        return (mViewModel.getCategory() != null && mCategory != null//
            && mViewModel.getCategory().code.equalsIgnoreCase(mCategory.code) == true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayResultListFragmentInterface.AnalyticsInterface) analytics;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case SearchStayResultTabActivity.REQUEST_CODE_PREVIEW:
                onPreviewActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_WISH_DIALOG:
                onWishDialogActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_DETAIL:
                onDetailActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_PERMISSION_MANAGER:
                onPermissionActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
                break;
        }
    }

    private void onPreviewActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                Observable.create(new ObservableOnSubscribe<Object>()
                {
                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception
                    {
                        onStayClick(mWishPosition, mStayByLongPress, mListCountByLongPress, mPairsByLongPress, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                break;

            case BaseActivity.RESULT_CODE_REFRESH:
                if (intent != null && intent.hasExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                {
                    onChangedWish(mWishPosition, intent.getBooleanExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                } else
                {
                    onRefresh();
                }
                break;
        }
    }

    private void onWishDialogActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
            case BaseActivity.RESULT_CODE_ERROR:
                if (intent != null)
                {
                    onChangedWish(mWishPosition, intent.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                }
                break;

            case BaseActivity.RESULT_CODE_REFRESH:
                onRefresh();
                break;
        }
    }

    private void onDetailActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case BaseActivity.RESULT_CODE_REFRESH:
                if (intent != null && intent.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                {
                    onChangedWish(mWishPosition, intent.getBooleanExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                } else
                {
                    onRefresh();
                }
                break;

            default:
                break;
        }
    }

    private void onPermissionActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                onMyLocationClick();
                break;

            default:
                break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        if (Util.supportPreview(getActivity()) == true && getViewInterface().isBlurVisible() == true)
        {
            getViewInterface().setBlurVisible(getActivity(), false);
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mViewModel != null)
        {
            mViewModel.removeViewTypeObserver(mViewTypeObserver);
            mViewModel.removeFilterObserver(mFilterObserver);
            mViewModel.removeBookDateTimeObserver(mBookDateTimeObserver);
            mViewModel.removeRadiusObserver(mRadiusObserver);

            mViewModel = null;
        }
    }

    @Override
    public void onBackClick()
    {
        // 사용하지 않음.
    }

    @Override
    public void onSelected()
    {
        if (getFragment().isRemoving() == true || getFragment().isAdded() == false || getActivity() == null || mViewModel == null)
        {
            return;
        }

        if (mViewModel.getViewType() != mViewType)
        {
            setViewType(mViewModel.getViewType());
        } else
        {
            if (mNeedToRefresh == true)
            {
                onRefresh();
            }
        }

        if (mEmptyList == false)
        {
            getViewInterface().setFloatingActionViewVisible(true);
            getViewInterface().setFloatingActionViewTypeMapEnabled(true);
        } else
        {
            if (mViewModel.getFilter().isDefault() == true)
            {
                getViewInterface().setFloatingActionViewVisible(false);
            } else
            {
                getViewInterface().setFloatingActionViewVisible(true);
            }

            getViewInterface().setFloatingActionViewTypeMapEnabled(false);
        }
    }

    @Override
    public void onUnselected()
    {
        if (getFragment().isRemoving() == true || getFragment().isAdded() == false || getActivity() == null//
            || mViewType == null || mViewModel == null)
        {
            return;
        }

        switch (mViewType)
        {
            case LIST:
                break;

            case MAP:
                if (getViewInterface().isMapViewPagerVisible() == true)
                {
                    getViewInterface().setMapViewPagerVisible(false);
                }
                break;
        }
    }

    @Override
    public void onRefresh()
    {
        mNeedToRefresh = false;

        switch (mViewType)
        {
            case LIST:
                setRefresh(true);
                onRefresh(true);
                break;

            case MAP:
                if (getViewInterface().isMapViewPagerVisible() == true)
                {
                    onMapClick();
                }

                onMapReady();
                break;
        }
    }

    @Override
    public void scrollTop()
    {
        getViewInterface().scrollTop();
    }

    @Override
    public boolean onBackPressed()
    {
        switch (mViewType)
        {
            case LIST:
                return false;

            case MAP:
                if (getViewInterface().isMapViewPagerVisible() == true)
                {
                    onMapClick();
                } else
                {
                    mViewModel.setViewType(SearchStayResultTabPresenter.ViewType.LIST);
                }
                return true;
        }

        return false;
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity() == null || getFragment().isRemoving() == true || getActivity().isFinishing() == true || isRefresh() == false || mViewModel == null)
        {
            setRefresh(false);
            return;
        }

        if (mViewModel.getFilter() == null//
            || mViewModel.searchViewModel == null//
            || mViewModel.getViewType() == null//
            || mViewModel.getBookDateTime() == null//
            || mViewModel.getCommonDateTime() == null)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        mPage = 1;

        boolean applyFilter = mViewModel.getFilter().isDefault() == false;

        getViewInterface().hideEmptyViewVisible();

        StaySuggest suggest = mViewModel.getSuggest();

        Observable<Boolean> observable;

        if (suggest.isLocationSuggestType() == true && hasLocationDataInSuggest(suggest) == false)
        {
            getViewInterface().setLocationProgressBarVisible(true);

            StaySuggest.Location locationSuggestItem = (StaySuggest.Location) suggest.getSuggestItem();

            observable = searchMyLocation(null).onErrorResumeNext(new Function<Throwable, ObservableSource<Location>>()
            {
                @Override
                public ObservableSource<Location> apply(Throwable throwable) throws Exception
                {
                    return Observable.just(new Location("provider"));
                }
            }).flatMap(new Function<Location, ObservableSource<GoogleAddress>>()
            {
                @Override
                public ObservableSource<GoogleAddress> apply(Location location) throws Exception
                {
                    if (location.getLongitude() == 0.0d && location.getLatitude() == 0.0d)
                    {
                        return Observable.just(new GoogleAddress());
                    }

                    locationSuggestItem.latitude = location.getLatitude();
                    locationSuggestItem.longitude = location.getLongitude();

                    return mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()).onErrorResumeNext(new Function<Throwable, ObservableSource<GoogleAddress>>()
                    {
                        @Override
                        public ObservableSource<GoogleAddress> apply(Throwable throwable) throws Exception
                        {
                            return Observable.just(new GoogleAddress());
                        }
                    });
                }
            }).flatMap(new Function<GoogleAddress, ObservableSource<Boolean>>()
            {
                @Override
                public ObservableSource<Boolean> apply(GoogleAddress googleAddress) throws Exception
                {
                    locationSuggestItem.address = DailyTextUtils.isTextEmpty(googleAddress.address) ? getString(R.string.label_search_nearby_empty_address) : googleAddress.address;
                    locationSuggestItem.name = DailyTextUtils.isTextEmpty(googleAddress.shortAddress) ? getString(R.string.label_search_nearby_empty_address) : googleAddress.shortAddress;

                    getActivity().runOnUiThread(() -> mViewModel.setSuggest(mViewModel.getSuggest()));

                    return Observable.just(true);
                }
            });
        } else
        {
            observable = Observable.just(true);
        }

        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Stays>>()
        {
            @Override
            public ObservableSource<Stays> apply(Boolean result) throws Exception
            {
                return mStayRemoteImpl.getList(getActivity(), mViewModel.categoryType, getQueryMap(mPage), null);
            }
        }).map(new Function<Stays, Pair<Stays, List<ObjectItem>>>()
        {
            @Override
            public Pair<Stays, List<ObjectItem>> apply(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                List<ObjectItem> objectItemList = toObjectItemList(stays.getStayList());

                return new Pair(stays, objectItemList);
            }
        }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Pair<Stays, List<ObjectItem>>, ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> apply(Pair<Stays, List<ObjectItem>> pair) throws Exception
            {
                getViewInterface().setLocationProgressBarVisible(false);

                Stays stays = pair.first;
                List<ObjectItem> objectItemList = pair.second;
                Observable<Boolean> fragmentObservable;

                int size = objectItemList.size();

                if (size == 0)
                {
                    setEmptyResultList(applyFilter);

                    fragmentObservable = Observable.just(true);
                } else
                {
                    setResultList(stays, objectItemList, applyFilter);

                    if (mViewModel.resetCategory == true && Category.ALL.code.equalsIgnoreCase(mCategory.code) == true)
                    {
                        mViewModel.resetCategory = false;
                        fragmentObservable = getFragment().getFragmentEventListener().addCategoryList(stays.getStayCategoryList());
                    } else
                    {
                        fragmentObservable = Observable.just(true);
                    }
                }

                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);

                mAnalytics.onScreen(getActivity(), mViewModel.getViewType(), mViewModel.getBookDateTime(), mViewModel.getSuggest(), mViewModel.getFilter(), size == 0, mCallByScreen);

                mAnalytics.onEventSearchResult(getActivity(), mViewModel.getBookDateTime(), mViewModel.getSuggest()//
                    , mViewModel.getInputKeyword(), size, MAXIMUM_NUMBER_PER_PAGE);

                return fragmentObservable;
            }
        }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean result) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);

                onHandleErrorAndFinish(throwable);
            }
        }));
    }

    List<ObjectItem> toObjectItemList(List<Stay> stayList)
    {
        List<ObjectItem> objectItemList = new ArrayList<>();

        if (stayList != null && stayList.size() > 0)
        {
            for (Stay stay : stayList)
            {
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
            }
        }

        return objectItemList;
    }

    void setEmptyResultList(boolean applyFilter)
    {
        mEmptyList = true;
        mPage = PAGE_NONE;

        getViewInterface().setFloatingActionViewVisible(applyFilter);
        getViewInterface().setFloatingActionViewTypeMapEnabled(false);
        getViewInterface().setListLayoutVisible(false);

        if (mViewModel.getSuggest().isLocationSuggestType() == true)
        {
            boolean notDefaultRadius = mViewModel.searchViewModel.getRadius() != SearchStayResultTabPresenter.DEFAULT_RADIUS;

            getViewInterface().showLocationEmptyViewVisible(applyFilter || notDefaultRadius);
        } else
        {
            if (applyFilter == true)
            {
                getViewInterface().showDefaultEmptyViewVisible();
            } else
            {
                getFragment().getFragmentEventListener().setEmptyViewVisible(true);
            }
        }
    }

    void setResultList(Stays stays, List<ObjectItem> objectItemList, boolean applyFilter)
    {
        if (stays == null || objectItemList == null)
        {
            return;
        }

        int size = objectItemList.size();

        mEmptyList = false;

        getViewInterface().setSearchResultCount(stays.totalCount, stays.searchMaxCount);

        getViewInterface().setFloatingActionViewVisible(true);

        boolean allSoldOut = isAllSoldOut(stays.getStayList());

        getViewInterface().setFloatingActionViewTypeMapEnabled(allSoldOut == false);

        if (size < MAXIMUM_NUMBER_PER_PAGE)
        {
            mPage = PAGE_FINISH;

            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
        } else
        {
            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
        }

        getFragment().getFragmentEventListener().setEmptyViewVisible(false);
        getViewInterface().hideEmptyViewVisible();
        getViewInterface().setListLayoutVisible(true);

        getViewInterface().setSearchResultCount(size, MAXIMUM_NUMBER_PER_PAGE);
        getViewInterface().setList(objectItemList, mViewModel.getSuggest().isLocationSuggestType() || mViewModel.isDistanceSort()//
            , mViewModel.getBookDateTime().getNights() > 1, stays.activeReward, DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);

    }

    private boolean isAllSoldOut(List<Stay> stayList)
    {
        if (stayList == null)
        {
            return true;
        }

        for (Stay stay : stayList)
        {
            if (stay.soldOut == false)
            {
                return false;
            }
        }

        return true;
    }

    private boolean hasLocationDataInSuggest(@NonNull StaySuggest suggest)
    {
        if (suggest == null)
        {
            return false;
        }

        if (suggest.isLocationSuggestType() == true)
        {
            StaySuggest.Location locationSuggestItem = (StaySuggest.Location) suggest.getSuggestItem();

            return locationSuggestItem.latitude != 0.0d && locationSuggestItem.longitude != 0.0d;
        }

        return false;
    }

    @Override
    public void onSwipeRefreshing()
    {
        if (lock() == true)
        {
            return;
        }

        clearCompositeDisposable();

        mNeedToRefresh = false;

        setRefresh(true);
        onRefresh(false);
    }

    @Override
    public void onMoreRefreshing()
    {
        if (getActivity() == null || getActivity().isFinishing() == true || mViewModel == null || mPage == PAGE_FINISH || mMoreRefreshing == true)
        {
            return;
        }

        if (mViewModel.getFilter() == null//
            || mViewModel.searchViewModel == null//
            || mViewModel.getViewType() == null//
            || mViewModel.getBookDateTime() == null//
            || mViewModel.getCommonDateTime() == null)
        {
            return;
        }

        mMoreRefreshing = true;

        mPage++;

        addCompositeDisposable(mStayRemoteImpl.getList(getActivity(), mViewModel.categoryType, getQueryMap(mPage), null).map(new Function<Stays, List<ObjectItem>>()
        {
            @Override
            public List<ObjectItem> apply(Stays stays) throws Exception
            {
                return toObjectItemList(stays.getStayList());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ObjectItem>>()
        {
            @Override
            public void accept(List<ObjectItem> objectItemList) throws Exception
            {
                int listSize = objectItemList.size();

                if (listSize < MAXIMUM_NUMBER_PER_PAGE)
                {
                    mPage = PAGE_FINISH;

                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                } else
                {
                    objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                }

                getViewInterface().addList(objectItemList);

                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);

                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onStayClick(int position, Stay stay, int listCount, android.support.v4.util.Pair[] pairs, int gradientType)
    {
        if (mViewModel == null || stay == null || lock() == true)
        {
            return;
        }

        mWishPosition = position;

        StayDetailAnalyticsParam analyticsParam = getAnalyticsParam(stay, listCount);
        StayBookDateTime bookDateTime = mViewModel.getBookDateTime();

        if (Util.isUsedMultiTransition() == true && pairs != null)
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

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                , analyticsParam);

            startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                , analyticsParam);

            startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        mAnalytics.onEventStayClick(getActivity(), stay, mViewModel.getSuggest());
    }

    private StayDetailAnalyticsParam getAnalyticsParam(Stay stay, int listCount)
    {
        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        analyticsParam.price = stay.price;
        analyticsParam.discountPrice = stay.discountPrice;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.entryPosition = stay.entryPosition + 1;
        analyticsParam.totalListCount = listCount;
        analyticsParam.isDailyChoice = stay.dailyChoice;
        analyticsParam.setAddressAreaName(stay.addressSummary);

        return analyticsParam;
    }

    @Override
    public void onStayLongClick(int position, Stay stay, int listCount, android.support.v4.util.Pair[] pairs)
    {
        if (mViewModel == null || stay == null || lock() == true)
        {
            return;
        }

        mWishPosition = position;

        mListCountByLongPress = listCount;
        mPairsByLongPress = pairs;
        mStayByLongPress = stay;

        getViewInterface().setBlurVisible(getActivity(), true);

        StayBookDateTime bookDateTime = mViewModel.getBookDateTime();

        Intent intent = StayPreviewActivity.newInstance(getActivity()//
            , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stay.index, stay.name, stay.grade.getName(getActivity()), stay.discountPrice);

        startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_PREVIEW);
    }

    @Override
    public void onMapReady()
    {
        if (getActivity().isFinishing() == true || mViewModel == null)
        {
            return;
        }

        if (mViewModel.getFilter() == null//
            || mViewModel.searchViewModel == null//
            || mViewModel.getViewType() == null//
            || mViewModel.getBookDateTime() == null//
            || mViewModel.getCommonDateTime() == null)
        {
            return;
        }

        lock();
        screenLock(true);

        boolean applyFilter = mViewModel.getFilter().isDefault() == false;

        getViewInterface().hideEmptyViewVisible();

        // 맵은 모든 마커를 받아와야 하기 때문에 페이지 개수를 -1으로 한다.
        // 맵의 마커와 리스트의 목록은 상관관계가 없다.
        addCompositeDisposable(mStayRemoteImpl.getList(getActivity(), mViewModel.categoryType, getQueryMap(-1), null).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Stays>()
        {
            @Override
            public void accept(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                List<Stay> stayList = stays.getStayList();

                if (stayList == null || stayList.size() == 0)
                {
                    mEmptyList = true;

                    getViewInterface().setFloatingActionViewVisible(applyFilter);
                    getViewInterface().setFloatingActionViewTypeMapEnabled(false);

                    if (mViewModel.getSuggest().isLocationSuggestType() == true)
                    {
                        boolean notDefaultRadius = mViewModel.searchViewModel.getRadius() != SearchStayResultTabPresenter.DEFAULT_RADIUS;
                        getViewInterface().showLocationEmptyViewVisible(applyFilter || notDefaultRadius);
                    } else
                    {
                        getViewInterface().showDefaultEmptyViewVisible();
                    }

                    unLockAll();
                } else
                {
                    mEmptyList = false;
                    getViewInterface().setFloatingActionViewVisible(true);
                    getViewInterface().setFloatingActionViewTypeMapEnabled(true);

                    getViewInterface().hideEmptyViewVisible();
                    getViewInterface().setMapLayoutVisible(true);

                    getViewInterface().setMapList(stayList, true, true);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);

                mViewModel.setViewType(SearchStayResultTabPresenter.ViewType.LIST);
            }
        }));
    }

    @Override
    public void onMarkerClick(Stay stay, List<Stay> stayList)
    {
        if (stay == null || stayList == null || lock() == true)
        {
            return;
        }

        addCompositeDisposable(Observable.just(stay).subscribeOn(Schedulers.io()).map(new Function<Stay, List<Stay>>()
        {
            @Override
            public List<Stay> apply(@io.reactivex.annotations.NonNull Stay stay) throws Exception
            {
                Comparator<Stay> comparator = new Comparator<Stay>()
                {
                    public int compare(Stay stay1, Stay stay2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(stay.latitude, stay.longitude, stay1.latitude, stay1.longitude, results1);

                        float[] results2 = new float[3];
                        Location.distanceBetween(stay.latitude, stay.longitude, stay2.latitude, stay2.longitude, results2);

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(stayList, comparator);

                return stayList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Stay>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<Stay> stayList) throws Exception
            {
                getViewInterface().setMapViewPagerList(getActivity(), stayList, mViewModel.getBookDateTime().getNights() > 1//
                    , DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigRewardStickerEnabled());
                getViewInterface().setMapViewPagerVisible(true);

                unLockAll();
            }
        }));
    }

    @Override
    public void onMarkersCompleted()
    {
        unLockAll();
    }

    @Override
    public void onMapClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setMapViewPagerVisible(false);

        unLockAll();
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

        if (mViewType == SearchStayResultTabPresenter.ViewType.MAP)
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
    public void onCallClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(CallDialogActivity.newInstance(getActivity()), SearchStayResultTabActivity.REQUEST_CODE_CALL);

        mAnalytics.onEventCallClick(getActivity());
    }

    @Override
    public void onFilterClick()
    {
        if (getFragment() == null || getFragment().getFragmentEventListener() == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onFilterClick();
    }

    @Override
    public void onRadiusClick()
    {
        if (getFragment() == null || getFragment().getFragmentEventListener() == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onRadiusClick();
    }

    @Override
    public void onEmptyStayResearchClick()
    {
        if (getFragment() == null || getFragment().getFragmentEventListener() == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onEmptyStayResearchClick();
    }

    @Override
    public void onCalendarClick()
    {
        if (getFragment() == null || getFragment().getFragmentEventListener() == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onCalendarClick();
    }

    @Override
    public void onWishClick(int position, Stay stay)
    {
        if (position < 0 || stay == null)
        {
            return;
        }

        mWishPosition = position;

        boolean currentWish = stay.myWish;

        if (DailyHotel.isLogin() == true)
        {
            onChangedWish(position, !currentWish);
        }

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
            , stay.index, !currentWish, AnalyticsManager.Screen.DAILYHOTEL_LIST), SearchStayResultTabActivity.REQUEST_CODE_WISH_DIALOG);

        mAnalytics.onEventWishClick(getActivity(), !currentWish);
    }

    void setViewType(SearchStayResultTabPresenter.ViewType viewType)
    {
        if (viewType == null || mViewType == viewType)
        {
            return;
        }

        mViewType = viewType;

        switch (viewType)
        {
            case LIST:
                getViewInterface().setListLayoutVisible(true);
                getViewInterface().hideMapLayout(getFragment().getChildFragmentManager());

                if (mPage == PAGE_NONE || mNeedToRefresh == true)
                {
                    onRefresh();
                }
                break;

            case MAP:
                getViewInterface().setListLayoutVisible(false);

                // mPage == PAGE_NONE은 현재 호출된 목록이 없다. 진입을 바로 맵으로 했다.
                // show가 되면 onMapReady() 가 호출 되어서 자동 갱신된다.
                getViewInterface().setMapLayoutVisible(mPage != PAGE_NONE);
                getViewInterface().showMapLayout(getFragment().getChildFragmentManager());
                break;
        }
    }

    void onChangedWish(int position, boolean wish)
    {
        if (position < 0)
        {
            return;
        }

        getViewInterface().setWish(position, wish);
    }

    Map<String, Object> getQueryMap(int page)
    {
        Map<String, Object> queryMap = new HashMap<>();

        if (mViewModel.listType != null)
        {
            switch (mViewModel.listType)
            {
                case SEARCH:
                    queryMap.put("saleSearchType", "SHOW_SOLD_OUT");
                    break;

                default:
                    break;
            }
        }

        Map<String, Object> bookDateTimeQueryMap = getBookDateTimeQueryMap(mViewModel.getBookDateTime());

        if (bookDateTimeQueryMap != null)
        {
            queryMap.putAll(bookDateTimeQueryMap);
        }

        if (mCategory != null && Category.ALL.code.equalsIgnoreCase(mCategory.code) == false)
        {
            queryMap.put("category", mCategory.code);
        }

        Map<String, Object> suggestQueryMap = getSuggestQueryMap(mViewModel.getSuggest(), mViewModel.searchViewModel.getRadius());

        if (suggestQueryMap != null)
        {
            queryMap.putAll(suggestQueryMap);
        }

        Map<String, Object> filterQueryMap = getFilterQueryMap(mViewModel.getFilter());

        if (filterQueryMap != null)
        {
            queryMap.putAll(filterQueryMap);
        }

        // page
        // limit
        // 페이지 번호. 0 보다 큰 정수. 값을 입력하지 않으면 페이지네이션을 적용하지 않고 전체 데이터를 반환함
        if (page > 0)
        {
            queryMap.put("page", page);
            queryMap.put("limit", MAXIMUM_NUMBER_PER_PAGE);
        }

        // details
        queryMap.put("details", true);

        return queryMap;
    }

    private Map<String, Object> getBookDateTimeQueryMap(StayBookDateTime bookDateTime)
    {
        if (bookDateTime == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        queryMap.put("dateCheckIn", bookDateTime.getCheckInDateTime("yyyy-MM-dd"));
        queryMap.put("stays", bookDateTime.getNights());

        return queryMap;
    }

    private Map<String, Object> getSuggestQueryMap(StaySuggest suggest, float radius)
    {
        if (suggest == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        switch (suggest.getSuggestType())
        {
            case STAY:
            {
                StaySuggest.Stay suggestItem = (StaySuggest.Stay) suggest.getSuggestItem();
                queryMap.put("targetIndices", suggestItem.index);
                break;
            }

            case DIRECT:
                queryMap.put("term", suggest.getSuggestItem().name);
                break;

            case LOCATION:
            {
                StaySuggest.Location suggestItem = (StaySuggest.Location) suggest.getSuggestItem();

                queryMap.put("latitude", suggestItem.latitude);
                queryMap.put("longitude", suggestItem.longitude);
                queryMap.put("radius", radius);
                break;
            }

            case STATION:
            {
                StaySuggest.Station suggestItem = (StaySuggest.Station) suggest.getSuggestItem();

                queryMap.put("subwayIdx", suggestItem.index);
                break;
            }

            case AREA_GROUP:
            {
                StaySuggest.AreaGroup areaGroupSuggestItem = (StaySuggest.AreaGroup) suggest.getSuggestItem();

                queryMap.put("provinceIdx", areaGroupSuggestItem.index);

                StaySuggest.Area areaSuggestItem = areaGroupSuggestItem.area;

                if (areaSuggestItem != null && areaSuggestItem.index > 0)
                {
                    queryMap.put("areaIdx", areaSuggestItem.index);
                }
                break;
            }
        }

        return queryMap;
    }

    private Map<String, Object> getFilterQueryMap(StayFilter filter)
    {
        if (filter == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();
        List<String> bedTypeList = filter.getBedTypeList();

        List<String> luxuryFilterList = new ArrayList<>();
        List<String> amenitiesFilterList = filter.getAmenitiesFilter();
        List<String> roomAmenitiesFilterList = filter.getRoomAmenitiesFilterList();

        queryMap.put("persons", filter.person);

        if (bedTypeList != null && bedTypeList.size() > 0)
        {
            queryMap.put("bedType", bedTypeList);
        }

        if (amenitiesFilterList != null && amenitiesFilterList.size() > 0)
        {
            luxuryFilterList.addAll(amenitiesFilterList);
        }

        if (roomAmenitiesFilterList != null && roomAmenitiesFilterList.size() > 0)
        {
            luxuryFilterList.addAll(roomAmenitiesFilterList);
        }

        if (luxuryFilterList.size() > 0)
        {
            queryMap.put("luxury", luxuryFilterList);
        }

        Map<String, Object> sortQueryMap = getSortQueryMap(filter.sortType, mViewModel.filterLocation);

        if (sortQueryMap != null)
        {
            queryMap.putAll(sortQueryMap);
        }

        return queryMap;
    }

    private Map<String, Object> getSortQueryMap(StayFilter.SortType sortType, Location location)
    {
        if (sortType == null)
        {
            return null;
        }

        Map<String, Object> queryMap = new HashMap<>();

        switch (sortType)
        {
            case DEFAULT:
                break;

            case DISTANCE:
                queryMap.put("sortProperty", "Distance");
                queryMap.put("sortDirection", "Asc");

                if (location != null)
                {
                    queryMap.put("latitude", location.getLatitude());
                    queryMap.put("longitude", location.getLongitude());
                }
                break;

            case LOW_PRICE:
                queryMap.put("sortProperty", "Price");
                queryMap.put("sortDirection", "Asc");
                break;

            case HIGH_PRICE:
                queryMap.put("sortProperty", "Price");
                queryMap.put("sortDirection", "Desc");
                break;

            case SATISFACTION:
                queryMap.put("sortProperty", "Rating");
                queryMap.put("sortDirection", "Desc");
                break;
        }

        return queryMap;
    }

    private Observable<Location> searchMyLocation(Observable locationAnimationObservable)
    {
        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationFactory.measuringLocation() == true)
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
            protected void subscribeActual(io.reactivex.Observer<? super Location> observer)
            {
                mDailyLocationFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
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
                        mDailyLocationFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
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

                                mDailyLocationFactory.stopLocationMeasure();

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
                if (locationAnimationDisposable != null && locationAnimationDisposable.isDisposed() == false)
                {
                    locationAnimationDisposable.dispose();
                }
            }
        }).doOnDispose(new Action()
        {
            @Override
            public void run() throws Exception
            {
                if (locationAnimationDisposable != null && locationAnimationDisposable.isDisposed() == false)
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

                if (locationAnimationDisposable != null && locationAnimationDisposable.isDisposed() == false)
                {
                    locationAnimationDisposable.dispose();
                }

                if (throwable instanceof PermissionException)
                {
                    Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_PERMISSION_MANAGER);
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
                                startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_SETTING_LOCATION);
                            }
                        }, new View.OnClickListener()//
                        {
                            @Override
                            public void onClick(View v)
                            {
                                getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                            }
                        }, null, true);
                } else if (throwable instanceof DuplicateRunException)
                {

                } else if (throwable instanceof ResolvableApiException)
                {
                    try
                    {
                        ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), SearchStayResultTabActivity.REQUEST_CODE_SETTING_LOCATION);
                    } catch (Exception e)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                } else
                {
                    getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }
        });
    }
}
