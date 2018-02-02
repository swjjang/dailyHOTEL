package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.network.model.StayKeyword;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyRecentSearches;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStaySuggestPresenter //
    extends BaseExceptionPresenter<SearchStaySuggestActivity, SearchStaySuggestInterface> //
    implements SearchStaySuggestView.OnEventListener
{
    private SearchStaySuggestAnalyticsInterface mAnalytics;

    private SuggestRemoteImpl mSuggestRemoteImpl;
    private RecentlyRemoteImpl mRecentlyRemoteImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    private GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;

    private DailyRecentSearches mDailyRecentSearches;
    private StayBookDateTime mStayBookDateTime;
    private String mKeyword;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface SearchStaySuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public SearchStaySuggestPresenter(@NonNull SearchStaySuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchStaySuggestInterface createInstanceViewInterface()
    {
        return new SearchStaySuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchStaySuggestActivity activity)
    {
        setContentView(R.layout.activity_search_stay_suggest_data);

        setAnalytics(new SearchStaySuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStaySuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

        String checkInDate = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
        String checkOutDate = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE);

        if (DailyTextUtils.isTextEmpty(checkInDate, checkOutDate) == true)
        {
            return false;
        }

        try
        {
            mStayBookDateTime = new StayBookDateTime();

            mStayBookDateTime.setCheckInDateTime(checkInDate);
            mStayBookDateTime.setCheckOutDateTime(checkOutDate);
        } catch (Exception e)
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
        if (getViewInterface() == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mKeyword) == false)
        {
            getViewInterface().setKeywordEditText(mKeyword);
            //            return;
        }

        String searchKeywordEditHint = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigSearchStaySuggestHint();
        if (DailyTextUtils.isTextEmpty(searchKeywordEditHint) == false)
        {
            getViewInterface().setKeywordEditHint(searchKeywordEditHint);
        }

        setCheckVoiceSearchEnabled();

        getViewInterface().showKeyboard();
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
            case SearchStaySuggestActivity.REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mKeyword = result.get(0);
                    getViewInterface().setKeywordEditText(mKeyword);
                }
                break;
            }

            case SearchStaySuggestActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        startSearchMyLocation(true);
                        break;

                    default:
                        // 권한 설정을 하지 않았을때 아무것도 하지 않음
                        break;
                }
                break;
            }

            case SearchStaySuggestActivity.REQUEST_CODE_SETTING_LOCATION:
                startSearchMyLocation(true);
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject(DailyDb.MAX_RECENT_PLACE_COUNT, Constants.ServiceType.HOTEL) //
            .observeOn(Schedulers.io()).flatMap(new Function<JSONObject, ObservableSource<ArrayList<RecentlyPlace>>>()
            {
                @Override
                public ObservableSource<ArrayList<RecentlyPlace>> apply(JSONObject jsonObject) throws Exception
                {
                    if (jsonObject == null || jsonObject.has("keys") == false)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    return mRecentlyRemoteImpl.getInboundRecentlyList(jsonObject);
                }
            });

        addCompositeDisposable(Observable.zip(ibObservable //
            , mRecentlyLocalImpl.getRecentlyIndexList(Constants.ServiceType.HOTEL) //
            , new BiFunction<ArrayList<RecentlyPlace>, ArrayList<Integer>, List<StaySuggest>>()
            {
                @Override
                public List<StaySuggest> apply(ArrayList<RecentlyPlace> stayList, ArrayList<Integer> expectedList) throws Exception
                {
                    if (expectedList != null && expectedList.size() > 0)
                    {
                        Collections.sort(stayList, new Comparator<RecentlyPlace>()
                        {
                            @Override
                            public int compare(RecentlyPlace o1, RecentlyPlace o2)
                            {
                                Integer position1 = expectedList.indexOf(o1.index);
                                Integer position2 = expectedList.indexOf(o2.index);

                                return position1.compareTo(position2);
                            }
                        });
                    }

                    mDailyRecentSearches = new DailyRecentSearches(DailyPreference.getInstance(getActivity()).getHotelRecentSearches());
                    List<Keyword> keywordList = mDailyRecentSearches.getList();

                    ArrayList<StaySuggest> staySuggestList = new ArrayList<>();

                    if (keywordList != null && keywordList.size() > 0)
                    {
                        staySuggestList.add(new StaySuggest(StaySuggest.MENU_TYPE_RECENTLY_SEARCH //
                            , null, getString(R.string.label_search_suggest_recently_search)));

                        for (Keyword keyword : keywordList)
                        {
                            staySuggestList.add(new StaySuggest(keyword));
                        }
                    }

                    if (stayList != null && stayList.size() > 0)
                    {
                        staySuggestList.add(new StaySuggest(StaySuggest.MENU_TYPE_RECENTLY_STAY //
                            , null, getString(R.string.label_recently_stay)));

                        for (RecentlyPlace recentlyPlace : stayList)
                        {
                            staySuggestList.add(new StaySuggest(recentlyPlace));
                        }
                    }

                    return staySuggestList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<StaySuggest>>()
        {
            @Override
            public void accept(List<StaySuggest> staySuggests) throws Exception
            {
                boolean visible = staySuggests != null && staySuggests.size() > 0;
                getViewInterface().setRecentlySuggests(staySuggests);
                getViewInterface().setRecentlySuggestVisible(visible);

                startSearchMyLocation(false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().setRecentlySuggests(null);
                getViewInterface().setRecentlySuggestVisible(false);

                startSearchMyLocation(false);

                unLockAll();
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        addCompositeDisposable(Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                if (getViewInterface() != null)
                {
                    getViewInterface().hideKeyboard();
                }

                return Observable.just(true);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                getActivity().onBackPressed();
            }
        }));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onSearchSuggest(String keyword)
    {
        clearCompositeDisposable();

        if (mStayBookDateTime == null)
        {
            Util.restartApp(getActivity());
            return;
        }

        String checkInDate;
        int nights;

        try
        {
            checkInDate = mStayBookDateTime.getCheckInDateTime("yyyy-MM-dd");
            nights = mStayBookDateTime.getNights();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        mKeyword = keyword;

        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            onSuggestList(null);
        } else
        {
            addCompositeDisposable(mSuggestRemoteImpl.getSuggestsByStayInbound(checkInDate, nights, keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).map(new Function<Pair<String, ArrayList<StayKeyword>>, List<StaySuggest>>()
                {
                    @Override
                    public List<StaySuggest> apply(Pair<String, ArrayList<StayKeyword>> stringArrayListPair) throws Exception
                    {
                        ArrayList<StayKeyword> keywordList = stringArrayListPair.second;
                        ArrayList<StaySuggest> staySuggestList = new ArrayList<>();

                        if (keywordList == null || keywordList.size() == 0)
                        {
                            return staySuggestList;
                        }

                        String oldCategoryKey = null;

                        for (StayKeyword stayKeyword : keywordList)
                        {
                            StaySuggest staySuggest = new StaySuggest(stayKeyword);

                            if (DailyTextUtils.isTextEmpty(oldCategoryKey) || oldCategoryKey.equalsIgnoreCase(staySuggest.categoryKey) == false)
                            {
                                int resId;
                                if (StaySuggest.CATEGORY_STAY.equalsIgnoreCase(staySuggest.categoryKey))
                                {
                                    resId = R.string.label_search_suggest_type_stay;
                                    oldCategoryKey = staySuggest.categoryKey;
                                } else
                                {
                                    resId = R.string.label_search_suggest_type_region;
                                    oldCategoryKey = StaySuggest.CATEGORY_REGION;
                                }

                                staySuggestList.add(new StaySuggest(StaySuggest.MENU_TYPE_SUGGEST, null, getString(resId)));

                            }

                            staySuggestList.add(staySuggest);
                        }

                        return staySuggestList;
                    }
                }).subscribe(new Consumer<List<StaySuggest>>()
                {
                    @Override
                    public void accept(List<StaySuggest> staySuggestList) throws Exception
                    {
                        SearchStaySuggestPresenter.this.onSuggestList(staySuggestList);

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        SearchStaySuggestPresenter.this.onSuggestList(null);

                        unLockAll();
                    }
                }));
        }
    }

    @Override
    public void onSuggestClick(StaySuggest staySuggest)
    {
        if (staySuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addRecentSearches(staySuggest);

        getViewInterface().setKeywordEditText(staySuggest.displayName);
        startFinishAction(staySuggest, mKeyword, null);
    }

    @Override
    public void onRecentlySuggestClick(StaySuggest staySuggest)
    {
        if (staySuggest == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addRecentSearches(staySuggest);

        getViewInterface().setKeywordEditText(staySuggest.displayName);
        startFinishAction(staySuggest, mKeyword, null);
    }

    private Keyword getKeyword(StaySuggest staySuggest)
    {
        if (getActivity() == null || staySuggest == null)
        {
            return null;
        }

        int icon = Keyword.DEFAULT_ICON;
        if (StaySuggest.CATEGORY_STAY.equalsIgnoreCase(staySuggest.categoryKey))
        {
            icon = Keyword.HOTEL_ICON;
        }

        return new Keyword(icon, staySuggest.displayName);
    }

    private void addRecentSearches(StaySuggest staySuggest)
    {
        if (getActivity() == null || staySuggest == null)
        {
            return;
        }

        Keyword keyword = getKeyword(staySuggest);

        if (keyword == null)
        {
            return;
        }

        mDailyRecentSearches.addString(keyword);
        DailyPreference.getInstance(getActivity()).setHotelRecentSearches(mDailyRecentSearches.toString());
    }

    private void startFinishAction(StaySuggest staySuggest, String keyword, String analyticsClickType)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcel(staySuggest));
        intent.putExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteAllRecentlySuggest(boolean skipLocked)
    {
        if (skipLocked == false && lock() == true)
        {
            return;
        }

        getViewInterface().setRecentlySuggests(null);
        getViewInterface().setRecentlySuggestVisible(false);

        Observable<Boolean> recentlySearchObservable = Observable.defer(new Callable<ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> call() throws Exception
            {
                mDailyRecentSearches.clear();
                DailyPreference.getInstance(getActivity()).setHotelRecentSearches("");

                return Observable.just(true);
            }
        }).subscribeOn(Schedulers.io());

        addCompositeDisposable(Observable.zip(mRecentlyLocalImpl.clearRecentlyItems(Constants.ServiceType.HOTEL) //
            , recentlySearchObservable, new BiFunction<Boolean, Boolean, Boolean>()
            {
                @Override
                public Boolean apply(Boolean t1, Boolean t2) throws Exception
                {
                    return true;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
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

    @Override
    public void onDeleteRecentlySuggest(int position, StaySuggest staySuggest)
    {
        if (getViewInterface() == null || staySuggest == null || position < 0)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        getViewInterface().removeRecentlyItem(position);

        if (getViewInterface().getRecentlySuggestEntryCount() == 0)
        {
            onDeleteAllRecentlySuggest(true);
            return;
        }

        if (StaySuggest.MENU_TYPE_RECENTLY_STAY == staySuggest.menuType)
        {
            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.HOTEL, staySuggest.stayIndex) //
                .flatMap(new Function<Boolean, ObservableSource<ArrayList<RecentlyDbPlace>>>()
                {
                    @Override
                    public ObservableSource<ArrayList<RecentlyDbPlace>> apply(Boolean aBoolean) throws Exception
                    {
                        return mRecentlyLocalImpl.getRecentlyTypeList(Constants.ServiceType.HOTEL);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<RecentlyDbPlace>>()
                {
                    @Override
                    public void accept(ArrayList<RecentlyDbPlace> recentlyDbPlaces) throws Exception
                    {
                        if (recentlyDbPlaces.size() == 0)
                        {
                            getViewInterface().removeRecentlySection(StaySuggest.MENU_TYPE_RECENTLY_STAY);
                        }

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
        } else
        {
            // 최근 검색어
            Keyword keyword = getKeyword(staySuggest);
            if (keyword == null)
            {
                unLockAll();
                return;
            }

            mDailyRecentSearches.remove(keyword);
            if (mDailyRecentSearches.size() == 0)
            {
                getViewInterface().removeRecentlySection(StaySuggest.MENU_TYPE_RECENTLY_SEARCH);
            }

            DailyPreference.getInstance(getActivity()).setHotelRecentSearches(mDailyRecentSearches.toString());

            unLockAll();
        }
    }

    @Override
    public void onVoiceSearchClick()
    {
        if (getViewInterface() == null || isVoiceSearchEnabled() == false)
        {
            return;
        }

        getViewInterface().hideKeyboard();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.label_search_suggest_voice_search_title));

        try
        {
            startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a)
        {
            DailyToast.showToast(getActivity(), R.string.message_search_suggest_voice_search_error, DailyToast.LENGTH_SHORT);
            getViewInterface().setVoiceSearchEnabled(false);
        }
    }

    @Override
    public void setCheckVoiceSearchEnabled()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setVoiceSearchEnabled(isVoiceSearchEnabled());
    }

    @Override
    public void onNearbyClick(StaySuggest staySuggest)
    {
        startSearchMyLocation(true);
    }

    private boolean isVoiceSearchEnabled()
    {
        if (getActivity() == null)
        {
            return false;
        }

        List<ResolveInfo> activities = getActivity().getPackageManager() //
            .queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        return activities.size() > 0;
    }

    private void onSuggestList(List<StaySuggest> staySuggestList)
    {
        getViewInterface().setProgressBarVisible(false);

        boolean hasKeyword = DailyTextUtils.isTextEmpty(mKeyword) == false;
        getViewInterface().setSuggestsVisible(hasKeyword);

        getViewInterface().setSuggests(staySuggestList);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void startSearchMyLocation(boolean isUserClick)
    {
        Observable<Location> observable = searchMyLocation(isUserClick);

        if (observable == null)
        {
            ExLog.e("sam - observable is null");
            return;
        }

        if (isUserClick == true)
        {
            screenLock(true);
        }

        addCompositeDisposable(observable.subscribe(new Consumer<Location>()
        {
            @Override
            public void accept(Location location) throws Exception
            {
                StaySuggest locationStaySuggest = new StaySuggest(StaySuggest.MENU_TYPE_LOCATION, StaySuggest.CATEGORY_LOCATION, null);
                locationStaySuggest.latitude = location.getLatitude();
                locationStaySuggest.longitude = location.getLongitude();

                addCompositeDisposable(mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String address) throws Exception
                    {
                        locationStaySuggest.displayName = address;

                        getViewInterface().setNearbyStaySuggest(true, locationStaySuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        getViewInterface().setKeywordEditText(locationStaySuggest.displayName);
                        startFinishAction(locationStaySuggest, mKeyword, null);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        getViewInterface().setNearbyStaySuggest(true, locationStaySuggest);

                        if (isUserClick == false)
                        {
                            return;
                        }

                        unLockAll();

                        locationStaySuggest.displayName = getString(R.string.label_search_nearby_empty_address);

                        getViewInterface().setKeywordEditText(locationStaySuggest.displayName);
                        startFinishAction(locationStaySuggest, mKeyword, null);
                    }
                }));

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                boolean isAgreePermission = true;
                String displayName = null;

                if (throwable instanceof PermissionException)
                {
                    displayName = getString(R.string.label_search_nearby_description);
                    isAgreePermission = false;
                }

                StaySuggest locationStaySuggest = new StaySuggest(StaySuggest.MENU_TYPE_LOCATION, StaySuggest.CATEGORY_LOCATION, displayName);

                getViewInterface().setNearbyStaySuggest(isAgreePermission, locationStaySuggest);

                if (isUserClick == false)
                {
                    return;
                }

                unLockAll();
            }
        }));
    }

    private Observable<Location> searchMyLocation(boolean isUserClick)
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            // 이미 동작 하고 있음
            return null;
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
                                //                                unLockAll();

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

                    @Override
                    public void onProviderDisabled()
                    {
                        observer.onError(new ProviderException());
                    }
                });
            }
        }.doOnError(throwable ->
        {
            //            unLockAll();
            if (isUserClick == false)
            {
                // 화면 진입 시 처리 임
                return;
            }

            unLockAll();


            if (throwable instanceof PermissionException)
            {
                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, SearchStaySuggestActivity.REQUEST_CODE_SETTING_LOCATION);
                    }
                };

                View.OnClickListener negativeListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                getViewInterface().showSimpleDialog(//
                    getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                    getString(R.string.dialog_btn_text_dosetting), //
                    getString(R.string.dialog_btn_text_cancel), //
                    positiveListener, negativeListener, cancelListener, null, true);
            } else if (throwable instanceof DuplicateRunException)
            {

            } else if (throwable instanceof ResolvableApiException)
            {
                try
                {
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), SearchStaySuggestActivity.REQUEST_CODE_SETTING_LOCATION);
                } catch (Exception e)
                {
                    getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            } else
            {
                getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
            }
        });
    }
}
