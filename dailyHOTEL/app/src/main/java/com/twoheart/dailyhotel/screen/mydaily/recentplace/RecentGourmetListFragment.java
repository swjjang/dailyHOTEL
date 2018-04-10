package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.preview.GourmetPreviewActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentGourmetListFragment extends RecentPlacesListFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setPlaceBookingDay(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        try
        {
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(commonDateTime.dailyDateTime);

            mPlaceBookingDay = gourmetBookingDay;
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            {
                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_REFRESH && data != null)
                {
                    if (data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                    {
                        onChangedWish(mWishPosition, data.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                mEventListener.onListItemClick(mViewByLongPress, mPositionByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data != null && data.hasExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case com.daily.base.BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false));
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        requestRecentPlacesList();
                        break;
                }
                break;
        }
    }

    @Override
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentGourmetListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected void requestRecentPlacesList()
    {
        lockUI();

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject(mBaseActivity, DailyDb.MAX_RECENT_PLACE_COUNT, ServiceType.GOURMET) //
            .observeOn(Schedulers.io()).flatMap(new Function<JSONObject, ObservableSource<ArrayList<RecentlyPlace>>>()
            {
                @Override
                public ObservableSource<ArrayList<RecentlyPlace>> apply(@NonNull JSONObject jsonObject) throws Exception
                {
                    if (jsonObject == null || jsonObject.has("keys") == false)
                    {
                        return Observable.just(new ArrayList<>());
                    }

                    return mRecentlyRemoteImpl.getInboundRecentlyList(jsonObject);
                }
            });

        addCompositeDisposable(ibObservable //
            .observeOn(Schedulers.io()).map(new Function<ArrayList<RecentlyPlace>, ArrayList<PlaceViewItem>>()
            {
                @Override
                public ArrayList<PlaceViewItem> apply(@NonNull ArrayList<RecentlyPlace> recentlyPlaceList) throws Exception
                {
                    return makePlaceViewItemList(recentlyPlaceList);
                }
            }).flatMap(new Function<ArrayList<PlaceViewItem>, ObservableSource<ArrayList<PlaceViewItem>>>()
            {
                @Override
                public ObservableSource<ArrayList<PlaceViewItem>> apply(@NonNull ArrayList<PlaceViewItem> placeViewItems) throws Exception
                {
                    return sortList(placeViewItems, true);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
            {
                @Override
                public void accept(@NonNull ArrayList<PlaceViewItem> list) throws Exception
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    mListLayout.setData(list, mPlaceBookingDay);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));

        //        addCompositeDisposable(ibObservable //
        //            .observeOn(Schedulers.io()).map(new Function<ArrayList<RecentlyPlace>, ArrayList<PlaceViewItem>>()
        //            {
        //                @Override
        //                public ArrayList<PlaceViewItem> apply(@NonNull ArrayList<RecentlyPlace> recentlyPlaceList) throws Exception
        //                {
        //                    return makePlaceViewItemList(recentlyPlaceList);
        //                }
        //            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        //            {
        //                @Override
        //                public void accept(@NonNull ArrayList<PlaceViewItem> list) throws Exception
        //                {
        //                    unLockUI();
        //
        //                    if (isFinishing() == true)
        //                    {
        //                        return;
        //                    }
        //
        //                    mListLayout.setData(list, mPlaceBookingDay);
        //                }
        //            }, new Consumer<Throwable>()
        //            {
        //                @Override
        //                public void accept(@NonNull Throwable throwable) throws Exception
        //                {
        //                    onHandleError(throwable);
        //                }
        //            }));

    }

    ArrayList<PlaceViewItem> makePlaceViewItemList(ArrayList<RecentlyPlace> gourmetList)
    {
        if (gourmetList == null || gourmetList.size() == 0)
        {
            return new ArrayList<>();
        }

        ArrayList<PlaceViewItem> list = new ArrayList<>();
        for (RecentlyPlace recentlyPlace : gourmetList)
        {
            list.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, recentlyPlace));
        }

        return list;
    }

    Observable<ArrayList<PlaceViewItem>> sortList(ArrayList<PlaceViewItem> actualList, boolean isAddFooter)
    {
        if (actualList == null || actualList.size() == 0)
        {
            return Observable.just(new ArrayList<>());
        }

        return mRecentlyLocalImpl.getRecentlyIndexList(mBaseActivity, Constants.ServiceType.GOURMET) //
            .flatMap(new Function<ArrayList<Integer>, ObservableSource<ArrayList<PlaceViewItem>>>()
            {
                @Override
                public ObservableSource<ArrayList<PlaceViewItem>> apply(@NonNull ArrayList<Integer> expectedList) throws Exception
                {
                    if (expectedList == null || expectedList.size() == 0)
                    {
                        return Observable.just(actualList);
                    }

                    Collections.sort(actualList, new Comparator<PlaceViewItem>()
                    {
                        @Override
                        public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
                        {
                            Integer position1 = expectedList.indexOf(getPlaceViewItemIndex(placeViewItem1));
                            Integer position2 = expectedList.indexOf(getPlaceViewItemIndex(placeViewItem2));
                            return position1.compareTo(position2);
                        }
                    });

                    if (isAddFooter == true)
                    {
                        actualList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));
                    }

                    return Observable.just(actualList);
                }
            }).subscribeOn(Schedulers.io());
    }

    int getPlaceViewItemIndex(PlaceViewItem placeViewItem)
    {
        int index;

        Object object = placeViewItem.getItem();
        if (object == null)
        {
            return -1;
        }

        if (object instanceof RecentlyPlace)
        {
            index = ((RecentlyPlace) object).index;
        } else
        {
            index = -1;
        }

        return index;
    }

    void onChangedWish(int position, boolean wish)
    {
        if (position < 0)
        {
            return;
        }

        if (mListLayout == null)
        {
            Util.restartApp(getContext());
            return;
        }

        PlaceViewItem placeViewItem = mListLayout.getItem(position);

        if (placeViewItem == null)
        {
            return;
        }

        Object object = placeViewItem.getItem();

        if (object == null)
        {
            return;
        }

        mWishPosition = position;

        if (object instanceof RecentlyPlace)
        {
            RecentlyPlace recentlyPlace = (RecentlyPlace) object;
            recentlyPlace.myWish = wish;
            mListLayout.notifyWishChanged(position, wish);
        }
    }

    RecentPlacesListLayout.OnEventListener mEventListener = new RecentPlacesListLayout.OnEventListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onListItemClick(View view, int position)
        {
            if (position < 0 || mListLayout.getItemCount() - 1 < position)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            RecentlyPlace recentlyPlace = placeViewItem.getItem();

            mWishPosition = position;

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();

            if (recentlyPlace.prices != null)
            {
                analyticsParam.price = recentlyPlace.prices.normalPrice;

                if (recentlyPlace.prices.discountPrice > 0)
                {
                    analyticsParam.discountPrice = recentlyPlace.prices.discountPrice;
                }
            } else
            {
                analyticsParam.price = 0;
                analyticsParam.discountPrice = 0;
            }

            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = position + 1;
            analyticsParam.totalListCount = mListLayout.getItemCount();
            analyticsParam.isDailyChoice = false;
            analyticsParam.setAddressAreaName(recentlyPlace.addrSummary);

            // <-- 추후에 정리되면 메소드로 수정

            if (Util.isUsedMultiTransition() == true)
            {
                mBaseActivity.setExitSharedElementCallback(new SharedElementCallback()
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

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity, ((DailyGourmetCardView) view).getOptionsCompat());

                Intent intent = GourmetDetailActivity.newInstance(mBaseActivity //
                    , recentlyPlace.index, recentlyPlace.title, recentlyPlace.imageUrl//
                    , recentlyPlace.prices != null ? recentlyPlace.prices.discountPrice : GourmetDetailActivity.NONE_PRICE//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , recentlyPlace.details != null ? recentlyPlace.details.category : null//
                    , recentlyPlace.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                    , analyticsParam);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(mBaseActivity //
                    , recentlyPlace.index, recentlyPlace.title, recentlyPlace.imageUrl, recentlyPlace.prices != null ? recentlyPlace.prices.discountPrice : GourmetDetailActivity.NONE_PRICE//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , recentlyPlace.details != null ? recentlyPlace.details.category : null//
                    , recentlyPlace.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);
                mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
                recentlyPlace.title, null);
        }

        @Override
        public void onListItemLongClick(View view, int position)
        {
            if (position < 0 || mListLayout.getItemCount() < position)
            {
                return;
            }

            mListLayout.setBlurVisibility(mBaseActivity, true);

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            RecentlyPlace recentlyPlace = placeViewItem.getItem();

            mWishPosition = position;

            mViewByLongPress = view;
            mPositionByLongPress = position;

            String visitDateTime = ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT);

            Intent intent = GourmetPreviewActivity.newInstance(mBaseActivity, visitDateTime//
                , recentlyPlace.index, recentlyPlace.title, recentlyPlace.details.category, GourmetPreviewActivity.SKIP_CHECK_PRICE_VALUE);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }


        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mListLayout.getItemCount() - 1 < position)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.removeItem(position);

            if (placeViewItem == null)
            {
                return;
            }

            RecentlyPlace recentlyPlace = placeViewItem.getItem();
            ExLog.d("isRemove : " + (recentlyPlace != null));

            if (recentlyPlace == null)
            {
                return;
            }

            addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem(getActivity() //
                , Constants.ServiceType.GOURMET, recentlyPlace.index).observeOn(Schedulers.io()).subscribe());

            mListLayout.setData(mListLayout.getList(), mPlaceBookingDay);
            mRecentPlaceListFragmentListener.onDeleteItemClickAnalytics();

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
                recentlyPlace.title, null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENTVIEW_ITEM_DELETE, Integer.toString(recentlyPlace.index), null);
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
            finish();
        }

        @Override
        public void onRecordAnalyticsList(ArrayList<PlaceViewItem> list)
        {
            if (list == null || list.isEmpty() == true || mPlaceBookingDay == null)
            {
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();
            String placeTypeString = AnalyticsManager.ValueType.GOURMET;
            int size = list.size();

            StringBuilder stringBuilder = new StringBuilder("[");
            int repeatCount = Math.min(5, size);
            for (int i = 0; i < repeatCount; i++)
            {
                if (i != 0)
                {
                    stringBuilder.append(",");
                }

                PlaceViewItem placeViewItem = list.get(i);
                RecentlyPlace recentlyPlace = placeViewItem.getItem();

                if (recentlyPlace != null)
                {
                    stringBuilder.append(recentlyPlace.index);
                }
            }

            stringBuilder.append("]");

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;
            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LIST_TOP5_PLACE_INDEXES, stringBuilder.toString());
            params.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(size));

            AnalyticsManager.getInstance(baseActivity).recordScreen(getActivity(), AnalyticsManager.Screen.MENU_RECENT_VIEW, null, params);
        }

        @Override
        public void onHomeClick()
        {
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null || baseActivity.isFinishing() == true)
            {
                return;
            }

            baseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
            baseActivity.finish();
        }

        @Override
        public void onWishClick(int position, PlaceViewItem placeViewItem)
        {
            if (placeViewItem == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            RecentlyPlace recentlyPlace = placeViewItem.getItem();

            if (recentlyPlace == null)
            {
                return;
            }

            mWishPosition = position;

            boolean currentWish = recentlyPlace.myWish;

            if (DailyHotel.isLogin() == true)
            {
                onChangedWish(position, !currentWish);
            }

            mBaseActivity.startActivityForResult(WishDialogActivity.newInstance(mBaseActivity, ServiceType.GOURMET//
                , recentlyPlace.index, !currentWish, AnalyticsManager.Screen.DAILYGOURMET_LIST), Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG);
        }

        @Override
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
