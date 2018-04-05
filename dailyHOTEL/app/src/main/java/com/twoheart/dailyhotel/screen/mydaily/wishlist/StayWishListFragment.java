package com.twoheart.dailyhotel.screen.mydaily.wishlist;

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
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.WishRemoteImpl;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.preview.StayPreviewActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.preview.StayOutboundPreviewActivity;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.daily.dailyhotel.view.DailyStayOutboundCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class StayWishListFragment extends PlaceWishListFragment
{
    WishRemoteImpl mWishRemoteImpl;

    List<PlaceViewItem> mWishList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mWishRemoteImpl = new WishRemoteImpl(getContext());

        return view;
    }

    @Override
    protected void setPlaceBookingDay(TodayDateTime todayDateTime)
    {
        if (todayDateTime == null)
        {
            return;
        }

        try
        {
            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
            stayBookingDay.setCheckOutDay(todayDateTime.dailyDateTime, 1);

            mPlaceBookingDay = stayBookingDay;
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
            case CODE_REQUEST_ACTIVITY_PREVIEW:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        addCompositeDisposable(Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                mEventListener.onListItemClick(mViewByLongPress, mPositionByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe());
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                    case com.daily.base.BaseActivity.RESULT_CODE_DATA_CHANGED:
                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                        forceRefreshList();
                        break;
                }
                break;
        }
    }

    @Override
    protected PlaceWishListLayout getListLayout()
    {
        return new StayWishListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected PlaceType getPlaceType()
    {
        return PlaceType.HOTEL;
    }

    @Override
    protected void requestWishList()
    {
        lockUI();

        addCompositeDisposable(Observable.zip(mWishRemoteImpl.getStayWishList(), mWishRemoteImpl.getStayOutboundWishList()//
            , new BiFunction<List<Stay>, List<StayOutbound>, List<PlaceViewItem>>()
            {
                @Override
                public List<PlaceViewItem> apply(List<Stay> stayList, List<StayOutbound> stayOutboundList) throws Exception
                {
                    List<PlaceViewItem> placeViewItemList = new ArrayList<>();

                    for (Stay stay : stayList)
                    {
                        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
                    }

                    for (StayOutbound stayOutbound : stayOutboundList)
                    {
                        placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_OB_ENTRY, stayOutbound));
                    }

                    Comparator<PlaceViewItem> comparator = new Comparator<PlaceViewItem>()
                    {
                        public int compare(PlaceViewItem placeViewItem1, PlaceViewItem placeViewItem2)
                        {
                            String dateTime1 = getDateTime(placeViewItem1);
                            String dateTime2 = getDateTime(placeViewItem2);

                            if (dateTime1 == null || dateTime2 == null || dateTime1.equalsIgnoreCase(dateTime2) == true)
                            {
                                return getIndex(placeViewItem1) - getIndex(placeViewItem2);
                            } else
                            {
                                return dateTime2.compareToIgnoreCase(dateTime1);
                            }
                        }

                        private String getDateTime(PlaceViewItem placeViewItem)
                        {
                            switch (placeViewItem.mType)
                            {
                                case PlaceViewItem.TYPE_ENTRY:
                                    return ((Stay) placeViewItem.getItem()).createdWishDateTime;

                                case PlaceViewItem.TYPE_OB_ENTRY:
                                    return ((StayOutbound) placeViewItem.getItem()).createdWishDateTime;
                            }

                            return null;
                        }

                        private int getIndex(PlaceViewItem placeViewItem)
                        {
                            switch (placeViewItem.mType)
                            {
                                case PlaceViewItem.TYPE_ENTRY:
                                    return ((Stay) placeViewItem.getItem()).index;

                                case PlaceViewItem.TYPE_OB_ENTRY:
                                    return ((StayOutbound) placeViewItem.getItem()).index;
                            }

                            return 0;
                        }
                    };

                    Collections.sort(placeViewItemList, comparator);

                    return placeViewItemList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<PlaceViewItem>>()
        {
            @Override
            public void accept(List<PlaceViewItem> placeViewItemList) throws Exception
            {
                mWishList = placeViewItemList;

                if (placeViewItemList.size() == 0)
                {
                    mListLayout.setData(null, false);
                } else
                {
                    mWishList.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

                    mListLayout.setData(mWishList, false);
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
    protected void requestRemoveWishListItem(int placeIndex)
    {
    }

    void startStayPreview(View view, int position, Stay stay)
    {
        if (stay == null)
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
            , stay.index, stay.name, stay.grade.getName(getContext()), stay.discountPrice);

        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    void startStayOutboundPreview(View view, int position, StayOutbound stayOutbound)
    {
        if (stayOutbound == null)
        {
            return;
        }

        mListLayout.setBlurVisibility(mBaseActivity, true);

        mViewByLongPress = view;
        mPositionByLongPress = position;

        StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

        String checkInDateTime = stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT);
        String checkOutDateTime = stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT);

        mBaseActivity.startActivityForResult(StayOutboundPreviewActivity.newInstance(getActivity(), stayOutbound.index, -1//
            , stayOutbound.name, checkInDateTime, checkOutDateTime, People.DEFAULT_ADULTS, null), CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    void startStayDetail(View view, Stay stay)
    {
        if (stay == null)
        {
            return;
        }

        int rankingPosition = 0;
        for (PlaceViewItem placeViewItem : mWishList)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY//
                && ((Stay) placeViewItem.getItem()).index == stay.index)
            {
                break;
            }

            rankingPosition++;
        }

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();
        analyticsParam.setAddressAreaName(stay.addressSummary);
        analyticsParam.discountPrice = stay.discountPrice;
        analyticsParam.price = stay.price;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.setRegion(null);
        analyticsParam.entryPosition = rankingPosition;
        analyticsParam.totalListCount = -1;
        analyticsParam.isDailyChoice = stay.dailyChoice;
        analyticsParam.gradeName = stay.grade.getName(getActivity());

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
                , stay.index, stay.name, stay.imageUrl, StayDetailActivity.NONE_PRICE//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , true, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST, analyticsParam);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , stay.index, stay.name, stay.imageUrl, StayDetailActivity.NONE_PRICE//
                , stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.WISHLIST_CLICKED, //
            stay.name, null);

        // 할인 쿠폰이 보이는 경우
        if (DailyTextUtils.isTextEmpty(stay.couponDiscountText) == false)
        {
            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.COUPON_STAY, Integer.toString(stay.index), null);
        }

        if (stay.reviewCount > 0)
        {
            AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.TRUE_REVIEW_STAY, Integer.toString(stay.index), null);
        }

        if (stay.trueVR == true)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
        }
    }

    void startStayOutboundDetail(View view, StayOutbound stayOutbound)
    {
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

        int rankingPosition = 0;
        for (PlaceViewItem placeViewItem : mWishList)
        {
            if (placeViewItem.mType == PlaceViewItem.TYPE_OB_ENTRY//
                && ((StayOutbound) placeViewItem.getItem()).index == stayOutbound.index)
            {
                break;
            }

            rankingPosition++;
        }

        StayOutboundDetailAnalyticsParam analyticsParam = new StayOutboundDetailAnalyticsParam();

        if (stayOutbound != null)
        {
            analyticsParam.index = stayOutbound.index;
            analyticsParam.benefit = false;
            analyticsParam.rating = stayOutbound.tripAdvisorRating == 0.0f ? null : Float.toString(stayOutbound.tripAdvisorRating);
        }

        analyticsParam.grade = getString(R.string.label_stay_outbound_filter_x_star_rate, (int) stayOutbound.rating);
        analyticsParam.rankingPosition = rankingPosition;
        analyticsParam.listSize = mWishList.size();

        StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

        String checkInDateTime = stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT);
        String checkOutDateTime = stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT);

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

            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, StayOutboundDetailActivity.NONE_PRICE//
                , checkInDateTime, checkOutDateTime, People.DEFAULT_ADULTS, null, true//
                , StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                , analyticsParam)//
                , CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
        } else
        {
            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, stayOutbound.nameEng, imageUrl, StayOutboundDetailActivity.NONE_PRICE//
                , checkInDateTime, checkOutDateTime, People.DEFAULT_ADULTS, null, false//
                , StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
                , CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.WISHLIST_CLICKED, //
            stayOutbound.name, null);
    }

    StayWishListLayout.OnEventListener mEventListener = new PlaceWishListLayout.OnEventListener()
    {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onListItemClick(View view, int position)
        {
            if (position < 0)
            {
                return;
            }

            if (mListLayout == null)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                    startStayDetail(view, placeViewItem.getItem());
                    break;

                case PlaceViewItem.TYPE_OB_ENTRY:
                    startStayOutboundDetail(view, placeViewItem.getItem());
                    break;
            }
        }

        @Override
        public void onListItemLongClick(View view, int position)
        {
            if (position < 0)
            {
                return;
            }

            if (mListLayout == null)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                    startStayPreview(view, position, placeViewItem.getItem());
                    break;

                case PlaceViewItem.TYPE_OB_ENTRY:
                    startStayOutboundPreview(view, position, placeViewItem.getItem());
                    break;
            }
        }

        @Override
        public void onListItemRemoveClick(int position)
        {
            if (position < 0 || mListLayout == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);

            if (placeViewItem == null)
            {
                Util.restartApp(getContext());
                return;
            }

            Observable<WishResult> observable = null;

            switch (placeViewItem.mType)
            {
                case PlaceViewItem.TYPE_ENTRY:
                    observable = mWishRemoteImpl.removeStayWish(((Stay) placeViewItem.getItem()).index);
                    break;

                case PlaceViewItem.TYPE_OB_ENTRY:
                    observable = mWishRemoteImpl.removeStayOutboundWish(((StayOutbound) placeViewItem.getItem()).index);
                    break;
            }

            if (observable == null)
            {
                unLockUI();
                return;
            }

            lockUI();

            addCompositeDisposable(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
            {
                @Override
                public void accept(WishResult wishResult) throws Exception
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    if (wishResult.success == false)
                    {
                        mBaseActivity.showSimpleDialog(getResources().getString(R.string.dialog_notice2) //
                            , wishResult.message, getResources().getString(R.string.dialog_btn_text_confirm), null);

                        return;
                    }

                    mListLayout.removeItem(position);
                    mListLayout.notifyDataSetChanged();

                    Map<String, String> params = new HashMap<>();

                    String placeName;
                    int placeIndex;

                    switch (placeViewItem.mType)
                    {
                        case PlaceViewItem.TYPE_ENTRY:
                            Stay stay = placeViewItem.getItem();

                            placeIndex = stay.index;
                            placeName = stay.name;

                            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                            params.put(AnalyticsManager.KeyType.NAME, placeName);
                            params.put(AnalyticsManager.KeyType.VALUE, AnalyticsManager.ValueType.EMPTY);
                            params.put(AnalyticsManager.KeyType.CATEGORY, stay.grade.getName(getContext()));
                            break;

                        case PlaceViewItem.TYPE_OB_ENTRY:

                            StayOutbound stayOutbound = placeViewItem.getItem();

                            placeIndex = stayOutbound.index;
                            placeName = stayOutbound.name;

                            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                            params.put(AnalyticsManager.KeyType.NAME, placeName);
                            params.put(AnalyticsManager.KeyType.VALUE, AnalyticsManager.ValueType.EMPTY);
                            params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
                            break;

                        default:
                            placeName = AnalyticsManager.ValueType.EMPTY;
                            placeIndex = 0;
                            break;
                    }

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                        AnalyticsManager.Category.NAVIGATION_, //
                        AnalyticsManager.Action.WISHLIST_DELETE, //
                        placeName, params);

                    AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION,//
                        AnalyticsManager.Action.WISHLIST_ITEM_DELETE, Integer.toString(placeIndex), null);

                    if (mWishListFragmentListener != null)
                    {
                        mWishListFragmentListener.onRemoveItemClick(PlaceType.HOTEL, -1);
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.PRODUCT_LIST//
                , AnalyticsManager.Action.WISH_STAY, AnalyticsManager.Label.OFF.toLowerCase(), null);
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
            finish();
        }

        @Override
        public void onRecordAnalyticsList(List<PlaceViewItem> list)
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

                switch (list.get(i).mType)
                {
                    case PlaceViewItem.TYPE_ENTRY:
                        stringBuilder.append(((Stay) list.get(i).getItem()).index);
                        break;

                    case PlaceViewItem.TYPE_OB_ENTRY:
                        stringBuilder.append(((StayOutbound) list.get(i).getItem()).index);
                        break;
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

            AnalyticsManager.getInstance(baseActivity).recordScreen(baseActivity, AnalyticsManager.Screen.MENU_WISHLIST, null, params);
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
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
