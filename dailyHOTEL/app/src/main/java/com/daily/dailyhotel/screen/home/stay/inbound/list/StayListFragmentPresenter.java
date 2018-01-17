package com.daily.dailyhotel.screen.home.stay.inbound.list;


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
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
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
public class StayListFragmentPresenter extends BasePagerFragmentPresenter<StayListFragment, StayListFragmentInterface>//
    implements StayListFragmentView.OnEventListener
{
    static final int MAXIMUM_NUMBER_PER_PAGE = Constants.PAGENATION_LIST_SIZE;
    static final int PAGE_NONE = -1;
    static final int PAGE_FINISH = Integer.MAX_VALUE;

    private StayListFragmentAnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;

    StayTabPresenter.StayViewModel mStayViewModel;
    Observer mViewTypeObserver;
    Observer mStayBookDateTimeObserver;
    Observer mStayRegionObserver;
    Observer mStayFilterObserver;

    Category mCategory;
    int mPage = PAGE_NONE; // 리스트에서 페이지
    boolean mMoreRefreshing; // 특정 스크를 이상 내려가면 더보기로 목록을 요청하는데 lock()걸리면 안되지만 계속 요청되면 안되어서 해당 키로 락을 건다.
    boolean mNeedToRefresh; // 화면 리플래쉬가 필요한 경우
    StayTabPresenter.ViewType mViewType;

    //
    Stay mStayByLongPress;
    int mListCountByLongPress;
    android.support.v4.util.Pair[] mPairsByLongPress;

    //
    int mWishPosition;
    boolean mEmptyList; // 목록, 맵이 비어있는지 확인
    DailyLocationExFactory mDailyLocationExFactory;

    public interface StayListFragmentAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayListFragmentPresenter(@NonNull StayListFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        if (bundle != null)
        {
            String name = bundle.getString("name");
            String code = bundle.getString("code");

            mCategory = new Category(code, name);
        }

        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_list_data, container);
    }

    @NonNull
    @Override
    protected StayListFragmentInterface createInstanceViewInterface()
    {
        return new StayListFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StayListFragmentAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayListFragmentAnalyticsInterface) analytics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case StayTabActivity.REQUEST_CODE_PREVIEW:
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

                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                        if (data != null && data.hasExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            onRefresh();
                        }
                        break;
                }
                break;

            case StayTabActivity.REQUEST_CODE_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;

                    case BaseActivity.RESULT_CODE_REFRESH:
                        onRefresh();
                        break;
                }
                break;

            case StayTabActivity.REQUEST_CODE_DETAIL:
                switch (resultCode)
                {
                    case BaseActivity.RESULT_CODE_REFRESH:
                        if (data.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            onRefresh();
                        }
                        break;

                    default:
                        break;
                }
                break;

            case StayTabActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case StayTabActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
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

        if (mStayViewModel != null)
        {
            mStayViewModel.viewType.removeObserver(mViewTypeObserver);
            mStayViewModel.stayBookDateTime.removeObserver(mStayBookDateTimeObserver);
            mStayViewModel.stayRegion.removeObserver(mStayRegionObserver);
            mStayViewModel.stayFilter.removeObserver(mStayFilterObserver);

            mStayViewModel = null;
        }
    }

    @Override
    public void onBackClick()
    {

    }

    @Override
    public void onSelected()
    {
        if (mStayViewModel.viewType.getValue() != mViewType)
        {
            setViewType(mStayViewModel.viewType.getValue());
        } else
        {
            if (mNeedToRefresh == true)
            {
                onRefresh();
            }
        }

        if (mEmptyList == false)
        {
            getFragment().getFragmentEventListener().setFloatingActionViewVisible(true);
            getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(true);
        } else
        {
            if (mStayViewModel.stayFilter.getValue().isDefaultFilter() == true)
            {
                getFragment().getFragmentEventListener().setFloatingActionViewVisible(false);
            } else
            {
                getFragment().getFragmentEventListener().setFloatingActionViewVisible(true);
            }

            getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(false);
        }
    }

    @Override
    public void onUnselected()
    {

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
                onMapReady();
                break;
        }
    }

    @Override
    public void scrollTop()
    {

    }

    @Override
    public boolean onBackPressed()
    {
        if (isCurrentFragment() == false)
        {
            return false;
        }

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
                    mStayViewModel.viewType.setValue(StayTabPresenter.ViewType.LIST);
                }
                return true;
        }

        return false;
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false || mStayViewModel == null)
        {
            setRefresh(false);
            return;
        }

        if (mStayViewModel.selectedCategory.getValue() == null//
            || mStayViewModel.stayFilter.getValue() == null//
            || mStayViewModel.viewType.getValue() == null//
            || mStayViewModel.stayRegion.getValue() == null//
            || mStayViewModel.stayBookDateTime.getValue() == null//
            || mStayViewModel.commonDateTime.getValue() == null)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        mPage = 1;

        getViewInterface().setEmptyViewVisible(false, mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

        addCompositeDisposable(mStayRemoteImpl.getList(getQueryMap(mPage), DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()).map(new Function<Stays, Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public Pair<Boolean, List<ObjectItem>> apply(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                return new Pair<>(stays.activeReward, makeObjectItemList(stays.getStayList(), mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DEFAULT));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public void accept(Pair<Boolean, List<ObjectItem>> pair) throws Exception
            {
                int listSize = pair.second.size();

                boolean notDefaultFilter = mStayViewModel.stayFilter.getValue().isDefaultFilter() == false;

                if (listSize == 0)
                {
                    mEmptyList = true;
                    mPage = PAGE_NONE;

                    getFragment().getFragmentEventListener().setFloatingActionViewVisible(notDefaultFilter);
                    getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(false);

                    getViewInterface().setEmptyViewVisible(true, notDefaultFilter);
                } else
                {
                    mEmptyList = false;

                    getFragment().getFragmentEventListener().setFloatingActionViewVisible(true);
                    getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(true);

                    if (listSize < MAXIMUM_NUMBER_PER_PAGE)
                    {
                        mPage = PAGE_FINISH;

                        pair.second.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                    } else
                    {
                        pair.second.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                    }

                    getViewInterface().setEmptyViewVisible(false, mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

                    getViewInterface().setList(pair.second, mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE//
                        , mStayViewModel.stayBookDateTime.getValue().getNights() > 1, pair.first//
                        , DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);
                }

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
    public void onSwipeRefreshing()
    {
        if (lock() == true)
        {
            return;
        }

        clearCompositeDisposable();

        onRefresh();
    }

    @Override
    public void onMoreRefreshing()
    {
        if (getActivity().isFinishing() == true || mStayViewModel == null || mPage == PAGE_FINISH || mMoreRefreshing == true)
        {
            return;
        }

        if (mStayViewModel.selectedCategory.getValue() == null//
            || mStayViewModel.stayFilter.getValue() == null//
            || mStayViewModel.viewType.getValue() == null//
            || mStayViewModel.stayRegion.getValue() == null//
            || mStayViewModel.stayBookDateTime.getValue() == null//
            || mStayViewModel.commonDateTime.getValue() == null)
        {
            return;
        }

        mMoreRefreshing = true;

        mPage++;

        addCompositeDisposable(mStayRemoteImpl.getList(getQueryMap(mPage), DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()).map(new Function<Stays, Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public Pair<Boolean, List<ObjectItem>> apply(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                return new Pair<>(stays.activeReward, makeObjectItemList(stays.getStayList(), mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DEFAULT));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public void accept(Pair<Boolean, List<ObjectItem>> pair) throws Exception
            {
                int listSize = pair.second.size();

                if (listSize < MAXIMUM_NUMBER_PER_PAGE)
                {
                    mPage = PAGE_FINISH;

                    pair.second.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                } else
                {
                    pair.second.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                }

                getViewInterface().addList(pair.second, mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE//
                    , mStayViewModel.stayBookDateTime.getValue().getNights() > 1, pair.first,//
                    DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);

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
        if (mStayViewModel == null || stay == null || lock() == true)
        {
            return;
        }

        mWishPosition = position;

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        analyticsParam.setAddressAreaName(stay.addressSummary);
        analyticsParam.discountPrice = stay.discountPrice;
        analyticsParam.price = stay.price;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.setRegion(mStayViewModel.stayRegion.getValue());
        analyticsParam.entryPosition = stay.entryPosition;
        analyticsParam.totalListCount = listCount;
        analyticsParam.isDailyChoice = stay.dailyChoice;
        analyticsParam.gradeName = stay.grade.getName(getActivity());

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
                , mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , true, gradientType, analyticsParam);

            startActivityForResult(intent, StayTabActivity.REQUEST_CODE_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, stay.discountPrice//
                , mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            startActivityForResult(intent, StayTabActivity.REQUEST_CODE_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }
    }

    @Override
    public void onStayLongClick(int position, Stay stay, int listCount, android.support.v4.util.Pair[] pairs)
    {
        if (mStayViewModel == null || stay == null || lock() == true)
        {
            return;
        }

        mWishPosition = position;

        mListCountByLongPress = listCount;
        mPairsByLongPress = pairs;
        mStayByLongPress = stay;

        getViewInterface().setBlurVisible(getActivity(), true);

        Intent intent = StayPreviewActivity.newInstance(getActivity()//
            , mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stay.index, stay.name, stay.discountPrice, stay.grade.name());

        startActivityForResult(intent, StayTabActivity.REQUEST_CODE_PREVIEW);
    }

    @Override
    public void onViewPagerClose()
    {

    }

    @Override
    public void onMapReady()
    {
        if (getActivity().isFinishing() == true || mStayViewModel == null)
        {
            return;
        }

        if (mStayViewModel.selectedCategory.getValue() == null//
            || mStayViewModel.stayFilter.getValue() == null//
            || mStayViewModel.viewType.getValue() == null//
            || mStayViewModel.stayRegion.getValue() == null//
            || mStayViewModel.stayBookDateTime.getValue() == null//
            || mStayViewModel.commonDateTime.getValue() == null)
        {
            return;
        }

        lock();
        screenLock(true);

        getViewInterface().setEmptyViewVisible(false, mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

        // 맵은 모든 마커를 받아와야 하기 때문에 페이지 개수를 -1으로 한다.
        // 맵의 마커와 리스트의 목록은 상관관계가 없다.
        addCompositeDisposable(mStayRemoteImpl.getList(getQueryMap(-1), DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Stays>()
        {
            @Override
            public void accept(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                boolean notDefaultFilter = mStayViewModel.stayFilter.getValue().isDefaultFilter() == false;

                if (stays.getStayList() == null || stays.getStayList().size() == 0)
                {
                    mEmptyList = true;

                    getFragment().getFragmentEventListener().setFloatingActionViewVisible(notDefaultFilter);
                    getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(false);

                    getViewInterface().setEmptyViewVisible(true, notDefaultFilter);

                    unLockAll();
                } else
                {
                    mEmptyList = false;
                    getFragment().getFragmentEventListener().setFloatingActionViewVisible(true);
                    getFragment().getFragmentEventListener().setFloatingActionViewTypeMapEnabled(true);

                    getViewInterface().setEmptyViewVisible(false, notDefaultFilter);

                    getViewInterface().setMapList(stays.getStayList(), true, true, false);
                }
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
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
                getViewInterface().setStayMapViewPagerList(getActivity(), stayList, mStayViewModel.stayBookDateTime.getValue().getNights() > 1//
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

        if (mViewType == StayTabPresenter.ViewType.MAP)
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

    }

    @Override
    public void onResearchClick()
    {

    }

    @Override
    public void onCallClick()
    {

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
    public void onRegionClick()
    {
        if (getFragment() == null || getFragment().getFragmentEventListener() == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onRegionClick();
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
            , stay.index, !currentWish, position, AnalyticsManager.Screen.DAILYHOTEL_LIST), StayTabActivity.REQUEST_CODE_WISH_DIALOG);

        //        AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
        //            , AnalyticsManager.Action.WISH_STAY, !currentWish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        setViewType(StayTabPresenter.ViewType.NONE);

        mStayViewModel = ViewModelProviders.of(activity).get(StayTabPresenter.StayViewModel.class);

        mViewTypeObserver = new Observer<StayTabPresenter.ViewType>()
        {
            @Override
            public void onChanged(@Nullable StayTabPresenter.ViewType viewType)
            {
                if (isCurrentFragment() == false)
                {
                } else
                {
                    // 이전 타입이 맵이고 리스트로 이동하는 경우 리스트를 재 호출 한다.
                    if (mViewType == StayTabPresenter.ViewType.MAP && viewType == StayTabPresenter.ViewType.LIST)
                    {
                        mNeedToRefresh = true;
                    }

                    setViewType(viewType);
                }
            }
        };

        mStayViewModel.viewType.observe(activity, mViewTypeObserver);

        mStayBookDateTimeObserver = new Observer<StayBookDateTime>()
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
            }
        };

        mStayViewModel.stayBookDateTime.observe(activity, mStayBookDateTimeObserver);

        mStayRegionObserver = new Observer<StayRegion>()
        {
            @Override
            public void onChanged(@Nullable StayRegion stayRegion)
            {
                // 지역이 변경되면 해당 화면 진입시 재 로딩할 준비를 한다.
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {

                }
            }
        };

        mStayViewModel.stayRegion.observe(activity, mStayRegionObserver);

        mStayFilterObserver = new Observer<StayFilter>()
        {
            @Override
            public void onChanged(@Nullable StayFilter stayFilter)
            {
                // 필터가 변경되면 해당 화면 진입시 재 로딩할 준비를 한다.
                if (isCurrentFragment() == false)
                {
                    mNeedToRefresh = true;
                } else
                {

                }
            }
        };

        mStayViewModel.stayFilter.observe(activity, mStayFilterObserver);
    }

    boolean isCurrentFragment()
    {
        return (mStayViewModel.selectedCategory.getValue() != null && mCategory != null//
            && mStayViewModel.selectedCategory.getValue().code.equalsIgnoreCase(mCategory.code) == true);
    }

    void setViewType(StayTabPresenter.ViewType viewType)
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
                getViewInterface().showMapLayout(getFragment().getChildFragmentManager(), mPage == PAGE_NONE);
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

    /**
     * @param stayList
     * @param hasSection
     * @return
     */
    List<ObjectItem> makeObjectItemList(List<Stay> stayList, boolean hasSection)
    {
        List<ObjectItem> objectItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return objectItemList;
        }

        // 초이스가 없는 경우 섹션이 필요없다.
        if (hasSection == true && stayList.get(0).dailyChoice == true)
        {
            boolean addAllSection = false;
            boolean addDailyChoiceSection = false;
            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                if (stay.dailyChoice == true)
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

                stay.entryPosition = ++entryPosition;
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
            }
        } else
        {
            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                stay.entryPosition = ++entryPosition;
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
            }
        }

        return objectItemList;
    }

    Map<String, Object> getQueryMap(int page)
    {
        Map<String, Object> queryMap = new HashMap<>();

        if (mStayViewModel.stayBookDateTime.getValue() != null)
        {
            // dateCheckIn
            queryMap.put("dateCheckIn", mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime("yyyy-MM-dd"));

            // stays
            queryMap.put("stays", mStayViewModel.stayBookDateTime.getValue().getNights());
        }

        if (mStayViewModel.stayRegion.getValue() != null)
        {
            // provinceIdx
            Area areaGroup = mStayViewModel.stayRegion.getValue().getAreaGroup();
            if (areaGroup != null)
            {
                queryMap.put("provinceIdx", mStayViewModel.stayRegion.getValue().getAreaGroup().index);
            }

            Area area = mStayViewModel.stayRegion.getValue().getArea();
            if (area != null && area.index != StayArea.ALL)
            {
                // areaIdx
                queryMap.put("areaIdx", mStayViewModel.stayRegion.getValue().getArea().index);
            }
        }

        if (mCategory != null && Category.ALL.code.equalsIgnoreCase(mCategory.code) == false)
        {
            queryMap.put("category", mCategory.code);
        }

        if (mStayViewModel.stayFilter.getValue() != null)
        {
            // persons
            queryMap.put("persons", mStayViewModel.stayFilter.getValue().person);

            // bedType [Double, Twin, Ondol, Etc]
            List<String> flagBedTypeFilters = mStayViewModel.stayFilter.getValue().getBedTypeList();

            if (flagBedTypeFilters != null && flagBedTypeFilters.size() > 0)
            {
                queryMap.put("bedType", flagBedTypeFilters);
            }

            // luxury [Breakfast, Cooking, Bath, Parking, Pool, Finess, WiFi, NoParking, Pet, ShareBbq, KidsPlayRoom
            // , Sauna, BusinessCenter, Tv, Pc, SpaWallPool, Karaoke, PartyRoom, PrivateBbq
            List<String> luxuryFilterList = new ArrayList<>();
            List<String> amenitiesFilterList = mStayViewModel.stayFilter.getValue().getAmenitiesFilter();
            List<String> roomAmenitiesFilterList = mStayViewModel.stayFilter.getValue().getRoomAmenitiesFilterList();

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

            // sortProperty
            // sortDirection
            switch (mStayViewModel.stayFilter.getValue().sortType)
            {
                case DEFAULT:
                    break;

                case DISTANCE:
                    queryMap.put("sortProperty", "Distance");
                    queryMap.put("sortDirection", "Asc");
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

            // longitude
            // latitude
            // radius
            if (mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE && mStayViewModel.location.getValue() != null)
            {
                queryMap.put("latitude", mStayViewModel.location.getValue().getLatitude());
                queryMap.put("longitude", mStayViewModel.location.getValue().getLongitude());
            }
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
            protected void subscribeActual(io.reactivex.Observer<? super Location> observer)
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
                    startActivityForResult(intent, StayTabActivity.REQUEST_CODE_PERMISSION_MANAGER);
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
                                startActivityForResult(intent, StayTabActivity.REQUEST_CODE_SETTING_LOCATION);
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
                        ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), StayTabActivity.REQUEST_CODE_SETTING_LOCATION);
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
