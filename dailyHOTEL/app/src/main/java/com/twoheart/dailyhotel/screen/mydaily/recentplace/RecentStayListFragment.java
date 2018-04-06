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

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.preview.StayPreviewActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.preview.StayOutboundPreviewActivity;
import com.daily.dailyhotel.storage.database.DailyDb;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentStayListFragment extends RecentPlacesListFragment
{
    static final int REQUEST_CODE_DETAIL = 10000;

    private StayBookDateTime mStayBookDateTime;

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
            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(commonDateTime.dailyDateTime);
            stayBookingDay.setCheckOutDay(commonDateTime.dailyDateTime, 1);

            mPlaceBookingDay = stayBookingDay;

            setStayBookDateTime(commonDateTime.currentDateTime);
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
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case RecentStayListFragment.REQUEST_CODE_DETAIL:
            {
                switch (resultCode)
                {
                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data != null && data.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            requestRecentPlacesList();
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_DATA_CHANGED:
                        requestRecentPlacesList();
                        break;
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

                    case com.daily.base.BaseActivity.RESULT_CODE_DATA_CHANGED:
                        if (data != null && data.hasExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION) == true//
                            && data.hasExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_MY_WISH) == true)
                        {
                            int position = data.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION, -1);
                            boolean wish = data.getBooleanExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_MY_WISH, false);

                            onChangedWish(position, wish);
                        }
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        if (data != null && data.hasExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH) == true)
                        {
                            onChangedWish(mWishPosition, data.getBooleanExtra(StayPreviewActivity.INTENT_EXTRA_DATA_WISH, false));
                        } else
                        {
                            requestRecentPlacesList();
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
        return new RecentStayListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected void requestRecentPlacesList()
    {
        lockUI();

        Observable<ArrayList<RecentlyPlace>> ibObservable = mRecentlyLocalImpl.getRecentlyJSONObject(DailyDb.MAX_RECENT_PLACE_COUNT, ServiceType.HOTEL) //
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

        Observable<StayOutbounds> obObservable = mRecentlyLocalImpl.getTargetIndices(Constants.ServiceType.OB_STAY, DailyDb.MAX_RECENT_PLACE_COUNT) //
            .observeOn(Schedulers.io()).flatMap(new Function<String, ObservableSource<StayOutbounds>>()
            {
                @Override
                public ObservableSource<StayOutbounds> apply(@NonNull String targetIndices) throws Exception
                {
                    return mRecentlyRemoteImpl.getStayOutboundRecentlyList(targetIndices, DailyDb.MAX_RECENT_PLACE_COUNT);
                }
            });

        addCompositeDisposable(Observable.zip(ibObservable.observeOn(Schedulers.io()), obObservable.observeOn(Schedulers.io()) //
            , new BiFunction<ArrayList<RecentlyPlace>, StayOutbounds, ArrayList<PlaceViewItem>>()
            {
                @Override
                public ArrayList<PlaceViewItem> apply(@NonNull ArrayList<RecentlyPlace> recentlyPlaceList, @NonNull StayOutbounds stayOutbounds) throws Exception
                {
                    return RecentStayListFragment.this.makePlaceViewItemList(recentlyPlaceList, stayOutbounds);
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
            public void accept(@NonNull ArrayList<PlaceViewItem> viewItemList) throws Exception
            {
                RecentStayListFragment.this.unLockUI();

                if (RecentStayListFragment.this.isFinishing() == true)
                {
                    return;
                }

                mListLayout.setData(viewItemList, mPlaceBookingDay);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception
            {
                RecentStayListFragment.this.onHandleError(throwable);
            }
        }));
    }

    ArrayList<PlaceViewItem> makePlaceViewItemList(ArrayList<RecentlyPlace> stayList, StayOutbounds stayOutbounds)
    {
        ArrayList<PlaceViewItem> list = new ArrayList<>();

        if (stayList != null)
        {
            for (RecentlyPlace recentlyPlace : stayList)
            {
                list.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, recentlyPlace));
            }
        }

        List<StayOutbound> stayOutboundList = null;
        if (stayOutbounds != null)
        {
            stayOutboundList = stayOutbounds.getStayOutbound();
        }

        if (stayOutboundList != null)
        {
            for (StayOutbound stayOutbound : stayOutboundList)
            {
                list.add(new PlaceViewItem(PlaceViewItem.TYPE_OB_ENTRY, stayOutbound));
            }
        }

        return list;
    }

    Observable<ArrayList<PlaceViewItem>> sortList(ArrayList<PlaceViewItem> actualList, boolean isAddFooter)
    {
        if (actualList == null || actualList.size() == 0)
        {
            return Observable.just(new ArrayList<>());
        }

        return mRecentlyLocalImpl.getRecentlyIndexList(Constants.ServiceType.HOTEL, Constants.ServiceType.OB_STAY) //
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
        } else if (object instanceof StayOutbound)
        {
            index = ((StayOutbound) object).index;
        } else
        {
            index = -1;
        }

        return index;
    }

    private void setStayBookDateTime(String currentDateTime)
    {
        if (DailyTextUtils.isTextEmpty(currentDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(currentDateTime);
            mStayBookDateTime.setCheckOutDateTime(currentDateTime, 1);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void onStayItemClick(View view, PlaceViewItem placeViewItem)
    {
        if (view == null || placeViewItem == null)
        {
            return;
        }

        RecentlyPlace recentlyPlace = placeViewItem.getItem();

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        analyticsParam.setAddressAreaName(recentlyPlace.addrSummary);

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
        analyticsParam.setRegion(null);
        analyticsParam.entryPosition = -1;
        analyticsParam.totalListCount = -1;
        analyticsParam.isDailyChoice = false;
        analyticsParam.gradeName = recentlyPlace.details.grade;

        StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

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

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyStayCardView) view).getOptionsCompat());

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , recentlyPlace.index, recentlyPlace.title, recentlyPlace.imageUrl, StayDetailActivity.NONE_PRICE//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , recentlyPlace.index, recentlyPlace.title, recentlyPlace.imageUrl, com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity.NONE_PRICE//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
            recentlyPlace.title, null);

        if (recentlyPlace.reviewCount > 0)
        {
            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(recentlyPlace.index), null);
        }

        if (recentlyPlace.details.isTrueVr == true)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(recentlyPlace.index), null);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void onStayOutboundItemClick(View view, PlaceViewItem placeViewItem)
    {
        StayOutbound stayOutbound = placeViewItem.getItem();

        if (stayOutbound == null)
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

        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        try
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.grade = getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating);
            analyticsParam.rankingPosition = 0;

            for (PlaceViewItem searchPlaceViewItem : mListLayout.getList())
            {
                if (searchPlaceViewItem.mType == PlaceViewItem.TYPE_ENTRY)
                {
                    analyticsParam.rankingPosition++;

                    if (((StayOutbound) searchPlaceViewItem.getItem()).index == stayOutbound.index)
                    {
                        break;
                    }
                }
            }

            analyticsParam.rating = null;
            analyticsParam.listSize = mListLayout.getRealItemCount();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (Util.isUsedMultiTransition() == true)
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

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyStayOutboundCardView) view).getOptionsCompat());

            mBaseActivity.startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, StayOutboundDetailActivity.NONE_PRICE//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , 2, null, true, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam)//
                , REQUEST_CODE_DETAIL, options.toBundle());
        } else
        {
            mBaseActivity.startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, StayOutboundDetailActivity.NONE_PRICE//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , 2, null, false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
                , REQUEST_CODE_DETAIL);

            mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
            stayOutbound.name, null);
    }

    void onStayItemLongClick(View view, int position, RecentlyPlace recentlyPlace)
    {
        if (view == null || recentlyPlace == null)
        {
            return;
        }

        mListLayout.setBlurVisibility(mBaseActivity, true);

        mViewByLongPress = view;
        mPositionByLongPress = position;

        StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

        Intent intent = StayPreviewActivity.newInstance(getActivity()//
            , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
            , recentlyPlace.index, recentlyPlace.title, recentlyPlace.details.grade, StayPreviewActivity.SKIP_CHECK_PRICE_VALUE);

        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    void onStayOutboundItemLongClick(View view, int position, StayOutbound stayOutbound)
    {
        if (view == null || stayOutbound == null)
        {
            return;
        }

        mListLayout.setBlurVisibility(mBaseActivity, true);

        mViewByLongPress = view;
        mPositionByLongPress = position;

        mBaseActivity.startActivityForResult(StayOutboundPreviewActivity.newInstance(getActivity(), stayOutbound.index, position//
            , stayOutbound.name//
            , ((StayBookingDay) mPlaceBookingDay).getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
            , ((StayBookingDay) mPlaceBookingDay).getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
            , People.DEFAULT_ADULTS, null)//
            , CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    void onStayItemDeleteClick(PlaceViewItem placeViewItem)
    {
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

        addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem( //
            Constants.ServiceType.HOTEL, recentlyPlace.index).observeOn(Schedulers.io()).subscribe());

        mListLayout.setData(mListLayout.getList(), mPlaceBookingDay);
        mRecentPlaceListFragmentListener.onDeleteItemClickAnalytics();

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.RECENT_VIEW_DELETE, //
            recentlyPlace.title, null);

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
            AnalyticsManager.Action.RECENTVIEW_ITEM_DELETE, Integer.toString(recentlyPlace.index), null);
    }

    void onStayOutboundItemDeleteClick(PlaceViewItem placeViewItem)
    {
        if (placeViewItem == null)
        {
            return;
        }

        StayOutbound stayOutbound = placeViewItem.getItem();
        ExLog.d("isRemove : " + (stayOutbound != null));

        if (stayOutbound == null)
        {
            return;
        }

        addCompositeDisposable(mRecentlyLocalImpl.deleteRecentlyItem( //
            Constants.ServiceType.OB_STAY, stayOutbound.index).observeOn(Schedulers.io()).subscribe());

        mListLayout.setData(mListLayout.getList(), mPlaceBookingDay);
        mRecentPlaceListFragmentListener.onDeleteItemClickAnalytics();

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.RECENT_VIEW_DELETE, //
            stayOutbound.name, null);

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
            AnalyticsManager.Action.RECENTVIEW_ITEM_DELETE, Integer.toString(stayOutbound.index), null);
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
        } else if (object instanceof StayOutbound)
        {
            StayOutbound stayOutbound = (StayOutbound) object;
            stayOutbound.myWish = wish;
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
            Object object = placeViewItem.getItem();

            if (object == null)
            {
                return;
            }

            mWishPosition = position;

            if (object instanceof RecentlyPlace)
            {
                onStayItemClick(view, placeViewItem);
            } else if (object instanceof StayOutbound)
            {
                onStayOutboundItemClick(view, placeViewItem);
            }
        }

        @Override
        public void onListItemLongClick(View view, int position)
        {
            if (position < 0 || mListLayout.getItemCount() - 1 < position)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);

            Object object = placeViewItem.getItem();

            if (object == null)
            {
                return;
            }

            mWishPosition = position;

            if (object instanceof RecentlyPlace)
            {
                onStayItemLongClick(view, position, (RecentlyPlace) object);
            } else if (object instanceof StayOutbound)
            {
                onStayOutboundItemLongClick(view, position, (StayOutbound) object);
            }
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mListLayout.getItemCount() - 1 < position)
            {
                ExLog.d("position Error Stay");
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.removeItem(position);
            Object object = placeViewItem.getItem();

            if (object == null)
            {
                return;
            }

            if (object instanceof RecentlyPlace)
            {
                onStayItemDeleteClick(placeViewItem);
            } else if (object instanceof StayOutbound)
            {
                onStayOutboundItemDeleteClick(placeViewItem);
            }
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
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
            String placeTypeString = AnalyticsManager.ValueType.STAY;
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
                int index = getPlaceViewItemIndex(placeViewItem);

                if (index >= 0)
                {
                    stringBuilder.append(index);
                }
            }

            stringBuilder.append("]");

            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookingDay.getCheckInDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookingDay.getCheckOutDay("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LIST_TOP5_PLACE_INDEXES, stringBuilder.toString());
            params.put(AnalyticsManager.KeyType.PLACE_COUNT, Integer.toString(size));

            AnalyticsManager.getInstance(baseActivity).recordScreen(baseActivity, AnalyticsManager.Screen.MENU_RECENT_VIEW, null, params);
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

            Object object = placeViewItem.getItem();

            if (object == null)
            {
                return;
            }

            mWishPosition = position;

            if (object instanceof RecentlyPlace)
            {
                RecentlyPlace recentlyPlace = (RecentlyPlace) object;

                boolean currentWish = recentlyPlace.myWish;

                if (DailyHotel.isLogin() == true)
                {
                    onChangedWish(position, !currentWish);
                }

                mBaseActivity.startActivityForResult(WishDialogActivity.newInstance(mBaseActivity, ServiceType.HOTEL//
                    , recentlyPlace.index, !currentWish, AnalyticsManager.Screen.DAILYHOTEL_LIST), Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG);

                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.WISH_STAY, !currentWish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);
            } else if (object instanceof StayOutbound)
            {
                StayOutbound stayOutbound = (StayOutbound) object;

                boolean currentWish = stayOutbound.myWish;

                if (DailyHotel.isLogin() == true)
                {
                    onChangedWish(position, !currentWish);
                }

                mBaseActivity.startActivityForResult(WishDialogActivity.newInstance(mBaseActivity, ServiceType.OB_STAY//
                    , stayOutbound.index, !currentWish, AnalyticsManager.Screen.DAILYHOTEL_LIST), Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG);

                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                    , AnalyticsManager.Action.WISH_STAY, !currentWish ? AnalyticsManager.Label.ON.toLowerCase() : AnalyticsManager.Label.OFF.toLowerCase(), null);

            }
        }

        @Override
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
