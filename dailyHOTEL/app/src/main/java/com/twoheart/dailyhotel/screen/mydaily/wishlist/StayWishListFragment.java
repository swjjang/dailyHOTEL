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

import com.daily.base.util.ExLog;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.view.DailyStayCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.preview.StayPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public class StayWishListFragment extends PlaceWishListFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
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
                if (resultCode == Activity.RESULT_OK)
                {
                    Observable.create(new ObservableOnSubscribe<Object>()
                    {
                        @Override
                        public void subscribe(ObservableEmitter<Object> e) throws Exception
                        {
                            mEventListener.onListItemClick(mViewByLongPress, mPositionByLongPress);
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
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
    protected BaseNetworkController getNetworkController()
    {
        return new StayWishListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
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

        ((StayWishListNetworkController) mNetworkController).requestStayWishList();
    }

    @Override
    protected void requestRemoveWishListItem(int placeIndex)
    {
        lockUI();

        ((StayWishListNetworkController) mNetworkController).requestRemoveStayWishListItem(placeIndex);
    }

    private StayWishListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayWishListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayWishList(ArrayList<Stay> list)
        {
            unLockUI();

            if (isFinishing() == true)
            {
                return;
            }

            if (mListLayout == null)
            {
                return;
            }

            mListLayout.setData(list);
        }

        @Override
        public void onRemoveStayWishListItem(boolean isSuccess, String message, int placeIndex)
        {
            unLockUI();

            if (isFinishing() == true)
            {
                return;
            }

            if (mListLayout == null)
            {
                return;
            }

            if (placeIndex < 0)
            {
                return;
            }

            if (isSuccess == false)
            {
                mBaseActivity.showSimpleDialog(getResources().getString(R.string.dialog_notice2) //
                    , message, getResources().getString(R.string.dialog_btn_text_confirm), null);
                return;
            }

            int removePosition = -1;
            int size = mListLayout.getList().size();
            String placeName = "";
            int discountPrice = 0;
            String category = "";

            for (int i = 0; i < size; i++)
            {
                PlaceViewItem placeViewItem = mListLayout.getItem(i);
                if (placeViewItem == null)
                {
                    continue;
                }

                Place place = placeViewItem.getItem();
                if (place == null)
                {
                    continue;
                }

                if (placeIndex == place.index)
                {
                    removePosition = i;
                    placeName = place.name;
                    discountPrice = place.discountPrice;
                    category = ((Stay) place).categoryCode;
                    break;
                }
            }

            if (removePosition != -1)
            {
                mListLayout.removeItem(removePosition);
                mListLayout.notifyDataSetChanged();
            }

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.NAME, placeName);
            params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(discountPrice));
            params.put(AnalyticsManager.KeyType.CATEGORY, category);

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

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayWishListFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayWishListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayWishListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayWishListFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayWishListFragment.this.onErrorResponse(call, response);
        }
    };

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
            Stay stay = placeViewItem.getItem();
            if (stay == null)
            {
                return;
            }

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

                AnalyticsParam analyticsParam = new AnalyticsParam();
                analyticsParam.setParam(mBaseActivity, stay);
                analyticsParam.setProvince(null);
                analyticsParam.setTotalListCount(-1);

                ActivityOptionsCompat optionsCompat;
                Intent intent;

                if (view instanceof DailyStayCardView == true)
                {
                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ((DailyStayCardView) view).getOptionsCompat());

                    intent = StayDetailActivity.newInstance(mBaseActivity //
                        , (StayBookingDay) mPlaceBookingDay, stay.index, stay.name, stay.imageUrl //
                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);
                } else
                {
                    intent = StayDetailActivity.newInstance(mBaseActivity //
                        , (StayBookingDay) mPlaceBookingDay, stay.index, stay.name, stay.imageUrl //
                        , analyticsParam, true, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_LIST);

                    View simpleDraweeView = view.findViewById(R.id.imageView);
                    View nameTextView = view.findViewById(R.id.nameTextView);
                    View gradientTopView = view.findViewById(R.id.gradientTopView);
                    View gradientBottomView = view.findViewById(R.id.gradientView);

                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                        android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                        android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                        android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                        android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));
                }

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, optionsCompat.toBundle());
            } else
            {
                AnalyticsParam analyticsParam = new AnalyticsParam();
                analyticsParam.setParam(mBaseActivity, stay);
                analyticsParam.setProvince(null);
                analyticsParam.setTotalListCount(-1);

                Intent intent = StayDetailActivity.newInstance(mBaseActivity //
                    , (StayBookingDay) mPlaceBookingDay, stay.index, stay.name, stay.imageUrl //
                    , analyticsParam, false, PlaceDetailLayout.TRANS_GRADIENT_BOTTOM_TYPE_NONE);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.WISHLIST_CLICKED, //
                stay.name, null);

            if (stay.truevr == true)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
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
            Stay stay = placeViewItem.getItem();

            if (stay == null)
            {
                return;
            }

            mListLayout.setBlurVisibility(mBaseActivity, true);

            mViewByLongPress = view;
            mPositionByLongPress = position;

            Intent intent = StayPreviewActivity.newInstance(mBaseActivity, (StayBookingDay) mPlaceBookingDay, stay);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
        }

        @Override
        public void onListItemRemoveClick(int position)
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
            Stay stay = placeViewItem.getItem();
            if (stay == null)
            {
                return;
            }

            StayWishListFragment.this.requestRemoveWishListItem(stay.index);
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
            finish();
        }

        @Override
        public void onRecordAnalyticsList(ArrayList<? extends Place> list)
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
                stringBuilder.append(list.get(i).index);
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
