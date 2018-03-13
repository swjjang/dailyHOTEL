package com.daily.dailyhotel.screen.home.search.gourmet.suggest;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.GoogleAddress;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.local.SuggestLocalImpl;
import com.daily.dailyhotel.repository.remote.GoogleAddressRemoteImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetSuggestPresenter //
    extends BaseExceptionPresenter<SearchGourmetSuggestActivity, SearchGourmetSuggestInterface> //
    implements SearchGourmetSuggestView.OnEventListener
{
    SearchGourmetSuggestAnalyticsInterface mAnalytics;

    private SuggestRemoteImpl mSuggestRemoteImpl;
    private SuggestLocalImpl mSuggestLocalImpl;
    RecentlyRemoteImpl mRecentlyRemoteImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    GoogleAddressRemoteImpl mGoogleAddressRemoteImpl;
    private Disposable mSuggestDisposable;

    private GourmetBookDateTime mGourmetBookDateTime;
    private List<GourmetSuggestV2> mPopularAreaList; // 일단 형식만 맞추기 위해 - 기본 화면을 대신 적용
    private List<GourmetSuggestV2> mRecentlySuggestList;
    private List<GourmetSuggestV2> mSuggestList;
    GourmetSuggestV2 mLocationSuggest;
    String mKeyword;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface SearchGourmetSuggestAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onSearchSuggestList(Activity activity, String keyword, boolean hasGourmetSuggestList);

        void onDeleteRecentlySearch(Activity activity, String keyword);

        void onVoiceSearchClick(Activity activity);

        void onLocationSearchNoAddressClick(Activity activity);

        void onRecentlySearchList(Activity activity, boolean hasData);

        void onRecentlyGourmetList(Activity activity, boolean hasData);

        void onDeleteRecentlyGourmet(Activity activity);

        void onScreen(Activity activity);
    }

    public SearchGourmetSuggestPresenter(@NonNull SearchGourmetSuggestActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchGourmetSuggestInterface createInstanceViewInterface()
    {
        return new SearchGourmetSuggestView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchGourmetSuggestActivity activity)
    {
        setContentView(R.layout.activity_search_gourmet_suggest_data);

        setAnalytics(new SearchGourmetSuggestAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mSuggestLocalImpl = new SuggestLocalImpl(activity);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mGoogleAddressRemoteImpl = new GoogleAddressRemoteImpl(activity);

        boolean isAgreeLocation = DailyPreference.getInstance(activity).isAgreeTermsOfLocation();

        GourmetSuggestV2.Location location = new GourmetSuggestV2.Location();
        location.address = isAgreeLocation ? getString(R.string.label_search_nearby_empty_address) : getString(R.string.label_search_nearby_description);
        mLocationSuggest = new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_LOCATION, location);

        List<GourmetSuggestV2> popularList = new ArrayList<>();
        popularList.add(new GourmetSuggestV2(0 //
            , new GourmetSuggestV2.SuggestItem(getString(R.string.label_search_suggest_recently_empty_description_type_gourmet))));
        setPopularAreaList(popularList);
        notifyDataSetChanged();

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchGourmetSuggestAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mKeyword = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

        String visitDate = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_VISIT_DATE);

        if (DailyTextUtils.isTextEmpty(visitDate) == true)
        {
            return false;
        }

        try
        {
            mGourmetBookDateTime = new GourmetBookDateTime();

            mGourmetBookDateTime.setVisitDateTime(visitDate);
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

        String searchKeywordEditHint = DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigSearchGourmetSuggestHint();
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

        try
        {
            mAnalytics.onScreen(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
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
            case SearchGourmetSuggestActivity.REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mKeyword = result.get(0);
                    getViewInterface().setKeywordEditText(mKeyword);
                }
                break;
            }

            case SearchGourmetSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION:
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

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject( //
            SearchGourmetSuggestActivity.RECENTLY_PLACE_MAX_REQUEST_COUNT, Constants.ServiceType.GOURMET) //
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
            , mRecentlyLocalImpl.getRecentlyIndexList(Constants.ServiceType.GOURMET) //
            , mSuggestLocalImpl.getRecentlyGourmetSuggestList(10) //
            , new Function3<ArrayList<RecentlyPlace>, ArrayList<Integer>, List<GourmetSuggestV2>, List<GourmetSuggestV2>>()
            {
                @Override
                public List<GourmetSuggestV2> apply(ArrayList<RecentlyPlace> placeList, ArrayList<Integer> indexList, List<GourmetSuggestV2> searchList) throws Exception
                {
                    if (indexList != null && indexList.size() > 0)
                    {
                        Collections.sort(placeList, new Comparator<RecentlyPlace>()
                        {
                            @Override
                            public int compare(RecentlyPlace o1, RecentlyPlace o2)
                            {
                                Integer position1 = indexList.indexOf(o1.index);
                                Integer position2 = indexList.indexOf(o2.index);

                                return position1.compareTo(position2);
                            }
                        });
                    }

                    List<GourmetSuggestV2> recentlySuggestList = getRecentlySuggestList(searchList, placeList);
                    setRecentlySuggestList(recentlySuggestList);

                    try
                    {
                        mAnalytics.onRecentlySearchList(getActivity(), searchList != null && searchList.size() > 0);
                        mAnalytics.onRecentlyGourmetList(getActivity(), placeList != null && placeList.size() > 0);
                    } catch (Exception e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    return recentlySuggestList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<GourmetSuggestV2>>()
        {
            @Override
            public void accept(List<GourmetSuggestV2> gourmetSuggests) throws Exception
            {
                notifyDataSetChanged();

                startSearchMyLocation(false);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                notifyDataSetChanged();

                startSearchMyLocation(false);

                unLockAll();
            }
        }));
    }

    private void setPopularAreaList(List<GourmetSuggestV2> popularAreaList)
    {
        mPopularAreaList = popularAreaList;
    }

    List<GourmetSuggestV2> getRecentlySuggestList(List<GourmetSuggestV2> recentlySearchList, List<RecentlyPlace> recentlyPlaceList)
    {
        // 최근 검색어
        ArrayList<GourmetSuggestV2> recentlySuggestList = new ArrayList<>();

        if (recentlySearchList != null && recentlySearchList.size() > 0)
        {
            recentlySuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_RECENTLY_SEARCH //
                , new GourmetSuggestV2.Section(getString(R.string.label_search_suggest_recently_search))));

            recentlySuggestList.addAll(recentlySearchList);
        }

        // 최근 본 업장
        if (recentlyPlaceList != null && recentlyPlaceList.size() > 0)
        {
            recentlySuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_RECENTLY_GOURMET //
                , new GourmetSuggestV2.Section(getString(R.string.label_recently_gourmet))));

            for (RecentlyPlace recentlyPlace : recentlyPlaceList)
            {
                GourmetSuggestV2.Gourmet gourmet = new GourmetSuggestV2.Gourmet();
                GourmetSuggestV2.Province province = new GourmetSuggestV2.Province();
                province.name = recentlyPlace.regionName;

                gourmet.index = recentlyPlace.index;
                gourmet.name = recentlyPlace.title;
                gourmet.province = province;

                recentlySuggestList.add(new GourmetSuggestV2(GourmetSuggestV2.MENU_TYPE_RECENTLY_GOURMET, gourmet));
            }
        }

        return recentlySuggestList;
    }

    void setRecentlySuggestList(List<GourmetSuggestV2> recentlySuggestList)
    {
        mRecentlySuggestList = recentlySuggestList;
    }

    void setSuggestList(List<GourmetSuggestV2> suggestList)
    {
        mSuggestList = suggestList;
    }

    void notifyDataSetChanged()
    {
        if (DailyTextUtils.isTextEmpty(mKeyword) == false)
        {
            getViewInterface().setSuggests(mSuggestList);
            return;
        }

        // 추천 검색어의 경우 검색어가 있을때만 작동 해야 함
        setSuggestList(null);

        if (mRecentlySuggestList != null && mRecentlySuggestList.size() > 0)
        {
            getViewInterface().setRecentlySuggests(mLocationSuggest, mRecentlySuggestList);
            return;
        }

        getViewInterface().setPopularAreaSuggests(mLocationSuggest, mPopularAreaList);
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
        //        clearCompositeDisposable();
        removeCompositeDisposable(mSuggestDisposable);

        if (mGourmetBookDateTime == null)
        {
            Util.restartApp(getActivity());
            return;
        }

        String visitDate;

        try
        {
            visitDate = mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return;
        }

        mKeyword = keyword;

        getViewInterface().setProgressBarVisible(true);

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            setSuggestList(null);
            notifyDataSetChanged();

            getViewInterface().setProgressBarVisible(false);
            unLockAll();
        } else
        {
            mSuggestDisposable = mSuggestRemoteImpl.getSuggestsByGourmetV2(visitDate, keyword) //
                .delaySubscription(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()) //
                .subscribe(new Consumer<List<GourmetSuggestV2>>()
                {
                    @Override
                    public void accept(List<GourmetSuggestV2> gourmetSuggestList) throws Exception
                    {
                        setSuggestList(gourmetSuggestList);
                        notifyDataSetChanged();

                        getViewInterface().setProgressBarVisible(false);
                        unLockAll();

                        try
                        {
                            boolean hasGourmetSuggestList = gourmetSuggestList != null && gourmetSuggestList.size() > 0;
                            mAnalytics.onSearchSuggestList(getActivity(), keyword, hasGourmetSuggestList);
                        } catch (Exception e)
                        {
                            ExLog.d(e.getMessage());
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        setSuggestList(null);
                        notifyDataSetChanged();

                        getViewInterface().setProgressBarVisible(false);
                        unLockAll();

                        try
                        {
                            mAnalytics.onSearchSuggestList(getActivity(), keyword, false);
                        } catch (Exception e)
                        {
                            ExLog.d(e.getMessage());
                        }
                    }
                });
        }
    }

    @Override
    public void onSuggestClick(GourmetSuggestV2 gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            return;
        }

        if (gourmetSuggest.suggestItem == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        final String displayName;
        if (gourmetSuggest.suggestItem instanceof GourmetSuggestV2.Province)
        {
            displayName = ((GourmetSuggestV2.Province) gourmetSuggest.suggestItem).getProvinceName();
        } else
        {
            displayName = gourmetSuggest.suggestItem.name;
        }

        addCompositeDisposable(mSuggestLocalImpl.addRecentlyGourmetSuggest(gourmetSuggest, mKeyword) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    getViewInterface().setSuggest(displayName);
                    startFinishAction(gourmetSuggest, mKeyword);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    getViewInterface().setSuggest(displayName);
                    startFinishAction(gourmetSuggest, mKeyword);
                }
            }));
    }

    @Override
    public void onRecentlySuggestClick(GourmetSuggestV2 gourmetSuggest)
    {
        if (gourmetSuggest == null)
        {
            return;
        }

        if (gourmetSuggest.suggestItem == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        final String displayName;
        if (gourmetSuggest.suggestItem instanceof GourmetSuggestV2.Province)
        {
            displayName = ((GourmetSuggestV2.Province) gourmetSuggest.suggestItem).getProvinceName();
        } else
        {
            displayName = gourmetSuggest.suggestItem.name;
        }

        addCompositeDisposable(mSuggestLocalImpl.addRecentlyGourmetSuggest(gourmetSuggest, mKeyword) //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    getViewInterface().setSuggest(displayName);
                    startFinishAction(gourmetSuggest, mKeyword);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    getViewInterface().setSuggest(displayName);
                    startFinishAction(gourmetSuggest, mKeyword);
                }
            }));
    }

    void startFinishAction(GourmetSuggestV2 gourmetSuggest, String keyword)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(gourmetSuggest));
        intent.putExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD, keyword);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDeleteRecentlySuggest(int position, GourmetSuggestV2 gourmetSuggest)
    {
        if (getViewInterface() == null || gourmetSuggest == null || position < 0)
        {
            return;
        }

        GourmetSuggestV2.SuggestItem suggestItem = gourmetSuggest.suggestItem;
        if (suggestItem == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        getViewInterface().removeRecentlyItem(position);

        if (getViewInterface().getRecentlySuggestAllEntryCount() == 0)
        {
            setRecentlySuggestList(null);
            notifyDataSetChanged();
        }

        if (GourmetSuggestV2.MENU_TYPE_RECENTLY_GOURMET == gourmetSuggest.menuType)
        {
            GourmetSuggestV2.Gourmet gourmet = (GourmetSuggestV2.Gourmet) suggestItem;

            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(Constants.ServiceType.GOURMET, gourmet.index) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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

            try
            {
                mAnalytics.onDeleteRecentlyGourmet(getActivity());
            } catch (Exception e)
            {
                ExLog.d(e.getMessage());
            }
        } else
        {
            // 최근 검색어
            addCompositeDisposable(mSuggestLocalImpl.deleteRecentlyGourmetSuggest(gourmetSuggest) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        unLockAll();

                        try
                        {
                            mAnalytics.onDeleteRecentlySearch(getActivity(), suggestItem.name);
                        } catch (Exception e)
                        {
                            ExLog.d(e.getMessage());
                        }
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
            startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a)
        {
            DailyToast.showToast(getActivity(), R.string.message_search_suggest_voice_search_error, DailyToast.LENGTH_SHORT);
            getViewInterface().setVoiceSearchEnabled(false);
        }

        try
        {
            mAnalytics.onVoiceSearchClick(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
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
    public void onNearbyClick(GourmetSuggestV2 gourmetSuggest)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void startSearchMyLocation(boolean isUserClick)
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

        if (mLocationSuggest.suggestItem == null)
        {
            mLocationSuggest.suggestItem = new GourmetSuggestV2.Location();
        }

        GourmetSuggestV2.Location itemLocation = (GourmetSuggestV2.Location) mLocationSuggest.suggestItem;

        addCompositeDisposable(observable.subscribe(new Consumer<Location>()
        {
            @Override
            public void accept(Location location) throws Exception
            {
                itemLocation.name = getString(R.string.label_search_nearby_empty_address);
                itemLocation.address = getString(R.string.label_search_nearby_empty_address);
                itemLocation.latitude = location.getLatitude();
                itemLocation.longitude = location.getLongitude();

                addCompositeDisposable(mGoogleAddressRemoteImpl.getLocationAddress(location.getLatitude(), location.getLongitude()) //
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GoogleAddress>()
                    {
                        @Override
                        public void accept(GoogleAddress address) throws Exception
                        {
                            itemLocation.address = address.address;
                            itemLocation.name = address.shortAddress;

                            getViewInterface().setNearbyGourmetSuggest(mLocationSuggest);

                            if (isUserClick == false)
                            {
                                return;
                            }

                            addCompositeDisposable(mSuggestLocalImpl.addRecentlyGourmetSuggest(mLocationSuggest, mKeyword) //
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception
                                    {
                                        unLockAll();

                                        getViewInterface().setSuggest(itemLocation.address);
                                        startFinishAction(mLocationSuggest, mKeyword);
                                    }
                                }, new Consumer<Throwable>()
                                {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception
                                    {
                                        unLockAll();

                                        getViewInterface().setSuggest(itemLocation.address);
                                        startFinishAction(mLocationSuggest, mKeyword);
                                    }
                                }));
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            getViewInterface().setNearbyGourmetSuggest(mLocationSuggest);

                            if (isUserClick == false)
                            {
                                return;
                            }

                            addCompositeDisposable(mSuggestLocalImpl.addRecentlyGourmetSuggest(mLocationSuggest, mKeyword) //
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception
                                    {
                                        unLockAll();

                                        getViewInterface().setSuggest(itemLocation.address);
                                        startFinishAction(mLocationSuggest, mKeyword);
                                    }
                                }, new Consumer<Throwable>()
                                {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception
                                    {
                                        unLockAll();

                                        getViewInterface().setSuggest(itemLocation.address);
                                        startFinishAction(mLocationSuggest, mKeyword);
                                    }
                                }));

                            try
                            {
                                mAnalytics.onLocationSearchNoAddressClick(getActivity());
                            } catch (Exception e)
                            {
                                ExLog.d(e.getMessage());
                            }
                        }
                    }));

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                String address = null;

                if (throwable instanceof PermissionException)
                {
                    address = getString(R.string.label_search_nearby_description);
                }

                itemLocation.address = address;

                getViewInterface().setNearbyGourmetSuggest(mLocationSuggest);

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
                startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION);
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
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), SearchGourmetSuggestActivity.REQUEST_CODE_SETTING_LOCATION);
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
