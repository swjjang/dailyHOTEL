package com.daily.dailyhotel.screen.home.search.stay.inbound.result.campaign;


import android.app.Activity;
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
import com.daily.base.exception.BaseException;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabPresenter;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultViewModel;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.preview.StayPreviewActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
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
import java.util.List;

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
public class SearchStayCampaignTagListFragmentPresenter extends BasePagerFragmentPresenter<SearchStayCampaignTagListFragment, SearchStayCampaignTagListFragmentInterface.ViewInterface>//
    implements SearchStayCampaignTagListFragmentInterface.OnEventListener
{
    static final int MAXIMUM_NUMBER_PER_PAGE = Constants.PAGENATION_LIST_SIZE;
    static final int PAGE_NONE = -1;
    static final int PAGE_FINISH = Integer.MAX_VALUE;

    SearchStayCampaignTagListFragmentInterface.AnalyticsInterface mAnalytics;

    CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    SearchStayResultViewModel mViewModel;

    int mPage = PAGE_NONE; // 리스트에서 페이지
    boolean mMoreRefreshing; // 특정 스크를 이상 내려가면 더보기로 목록을 요청하는데 lock()걸리면 안되지만 계속 요청되면 안되어서 해당 키로 락을 건다.
    boolean mNeedToRefresh; // 화면 리플래쉬가 필요한 경우
    SearchStayResultTabPresenter.ViewType mViewType;

    //
    Stay mStayByLongPress;
    int mListCountByLongPress;
    android.support.v4.util.Pair[] mPairsByLongPress;

    //
    int mWishPosition;
    boolean mEmptyList; // 목록, 맵이 비어있는지 확인
    DailyLocationExFactory mDailyLocationFactory;

    public SearchStayCampaignTagListFragmentPresenter(@NonNull SearchStayCampaignTagListFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        return getViewInterface().getContentView(inflater, R.layout.fragment_search_gourmet_result_data, container);
    }

    @NonNull
    @Override
    protected SearchStayCampaignTagListFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchStayCampaignTagListFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new SearchStayCampaignTagListFragmentAnalyticsImpl());

        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

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
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayCampaignTagListFragmentInterface.AnalyticsInterface) analytics;
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

        if (mViewModel.getCommonDateTime() == null//
            || mViewModel.searchViewModel == null//
            || mViewModel.getBookDateTime() == null//
            || mViewModel.getSuggest() == null)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        mPage = 1;

        getViewInterface().setEmptyViewVisible(false, mViewModel.getFilter().isDefault() == false);

        StaySuggest suggest = mViewModel.getSuggest();
        StaySuggest.CampaignTag suggestItem = (StaySuggest.CampaignTag) suggest.getSuggestItem();
        final String DATE_FORMAT = "yyyy-MM-dd";
        StayBookDateTime bookDateTime = mViewModel.getBookDateTime();

        addCompositeDisposable(mCampaignTagRemoteImpl.getStayCampaignTags(suggestItem.index, bookDateTime.getCheckInDateTime(DATE_FORMAT), bookDateTime.getNights())//
            .map(new Function<StayCampaignTags, Pair<StayCampaignTags, List<ObjectItem>>>()
            {
                @Override
                public Pair<StayCampaignTags, List<ObjectItem>> apply(@io.reactivex.annotations.NonNull StayCampaignTags campaignTags) throws Exception
                {
                    List<ObjectItem> objectItemList = new ArrayList<>();
                    List<Stay> stayList = campaignTags.getStayList();

                    if (stayList != null && stayList.size() > 0)
                    {
                        for (Stay stay : stayList)
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
                        }
                    }

                    return new Pair(campaignTags, objectItemList);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<StayCampaignTags, List<ObjectItem>>>()
            {
                @Override
                public void accept(Pair<StayCampaignTags, List<ObjectItem>> pair) throws Exception
                {
                    StayCampaignTags campaignTags = pair.first;
                    CampaignTag campaignTag = campaignTags.getCampaignTag();
                    List<ObjectItem> objectItemList = pair.second;

                    if (DailyTextUtils.isTextEmpty(mViewModel.getSuggest().getSuggestItem().name) == true)
                    {
                        mViewModel.getSuggest().getSuggestItem().name = campaignTag.campaignTag;
                        mViewModel.setSuggest(mViewModel.getSuggest());
                    }

                    int size = objectItemList.size();

                    if (objectItemList.size() == 0)
                    {
                        mEmptyList = true;
                        mPage = PAGE_NONE;

                        getFragment().getFragmentEventListener().setEmptyViewVisible(true);
                    } else
                    {
                        mEmptyList = false;

                        if (size < MAXIMUM_NUMBER_PER_PAGE)
                        {
                            mPage = PAGE_FINISH;

                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                        } else
                        {
                            objectItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                        }

                        getFragment().getFragmentEventListener().setEmptyViewVisible(false);
                        getViewInterface().setListLayoutVisible(true);

                        getViewInterface().setSearchResultCount(size);

                        getViewInterface().setList(objectItemList, mViewModel.isDistanceSort()//
                            , mViewModel.getBookDateTime().getNights() > 1, campaignTags.activeReward//
                            , DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);
                    }

                    mMoreRefreshing = false;
                    getViewInterface().setSwipeRefreshing(false);
                    unLockAll();

                    mAnalytics.onEventSearchResult(getActivity(), mViewModel.getBookDateTime(), mViewModel.getSuggest()//
                        , campaignTag, size);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    if (throwable instanceof BaseException)
                    {
                        unLockAll();

                        BaseException baseException = (BaseException) throwable;

                        switch (baseException.getCode())
                        {
                            case -101: // 조회된 데이터가 없을때
                                mEmptyList = true;
                                mPage = PAGE_NONE;

                                getFragment().getFragmentEventListener().setEmptyViewVisible(true);
                                return;

                            case 200: // 종료된 캠페인 태그
                                showExpireTagDialog();
                                return;
                        }
                    }

                    onHandleErrorAndFinish(throwable);
                }
            }));
    }

    void showExpireTagDialog()
    {
        getViewInterface().showSimpleDialog(null //
            , getString(R.string.message_campaign_tag_finished) //
            , getString(R.string.dialog_btn_text_confirm) //
            , null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    getFragment().getFragmentEventListener().onFinishAndRefresh();
                }
            });
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
        analyticsParam.entryPosition = stay.entryPosition;
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


        //        Intent intent = StayPreviewActivity.newInstance(getActivity() //
        //            , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //            , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //            , stay.index, stay.name, stay.discountPrice, stay.grade.name());

        startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_PREVIEW);
    }

    @Override
    public void onMapReady()
    {
        if (getActivity().isFinishing() == true || mViewModel == null)
        {
            return;
        }

        if (mViewModel.getCommonDateTime() == null//
            || mViewModel.searchViewModel == null//
            || mViewModel.getBookDateTime() == null//
            || mViewModel.getSuggest() == null)
        {
            return;
        }

        lock();
        screenLock(true);

        getViewInterface().setEmptyViewVisible(false, false);

        // 맵은 모든 마커를 받아와야 하기 때문에 페이지 개수를 -1으로 한다.
        // 맵의 마커와 리스트의 목록은 상관관계가 없다.
        StaySuggest suggest = mViewModel.getSuggest();
        StaySuggest.CampaignTag suggestItem = (StaySuggest.CampaignTag) suggest.getSuggestItem();
        final String DATE_FORMAT = "yyyy-MM-dd";
        StayBookDateTime bookDateTime = mViewModel.getBookDateTime();

        addCompositeDisposable(mCampaignTagRemoteImpl.getStayCampaignTags(suggestItem.index, bookDateTime.getCheckInDateTime(DATE_FORMAT), bookDateTime.getNights())//
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayCampaignTags>()
            {
                @Override
                public void accept(StayCampaignTags campaignTags) throws Exception
                {
                    List<Stay> stayList = campaignTags.getStayList();

                    if (stayList == null || stayList.size() == 0)
                    {
                        mEmptyList = true;

                        getViewInterface().setEmptyViewVisible(true, false);

                        unLockAll();
                    } else
                    {
                        mEmptyList = false;

                        getViewInterface().setEmptyViewVisible(false, false);
                        getViewInterface().setMapLayoutVisible(true);

                        getViewInterface().setMapList(stayList, true, true, false);
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    ExLog.e(throwable.toString());

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
                getViewInterface().setMapViewPagerList(getActivity(), stayList);
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
