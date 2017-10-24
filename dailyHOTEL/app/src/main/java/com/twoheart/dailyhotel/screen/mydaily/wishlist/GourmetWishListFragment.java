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
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyGourmetCardView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
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

public class GourmetWishListFragment extends PlaceWishListFragment
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
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
            gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime);

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
        return new GourmetWishListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new GourmetWishListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected PlaceType getPlaceType()
    {
        return PlaceType.FNB;
    }

    @Override
    protected void requestWishList()
    {
        lockUI();

        ((GourmetWishListNetworkController) mNetworkController).requestGourmetWishList();
    }

    @Override
    protected void requestRemoveWishListItem(int placeIndex)
    {
        lockUI();

        ((GourmetWishListNetworkController) mNetworkController).requestRemoveGourmetWishListItem(placeIndex);
    }

    private GourmetWishListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetWishListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetWishList(ArrayList<Gourmet> list)
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

            mListLayout.setData(list, DailyRemoteConfigPreference.getInstance(mBaseActivity).isKeyRemoteConfigRewardStickerEnabled());
        }

        @Override
        public void onRemoveGourmetWishListItem(boolean isSuccess, String message, int placeIndex)
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
                    category = ((Gourmet) place).category;
                    break;
                }
            }

            if (removePosition != -1)
            {
                mListLayout.removeItem(removePosition);
                mListLayout.notifyDataSetChanged();
            }

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
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
                mWishListFragmentListener.onRemoveItemClick(PlaceType.FNB, -1);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetWishListFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetWishListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            GourmetWishListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetWishListFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            GourmetWishListFragment.this.onErrorResponse(call, response);
        }
    };

    GourmetWishListLayout.OnEventListener mEventListener = new PlaceWishListLayout.OnEventListener()
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
            Gourmet gourmet = placeViewItem.getItem();

            // --> 추후에 정리되면 메소드로 수정
            GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
            analyticsParam.price = gourmet.price;
            analyticsParam.discountPrice = gourmet.discountPrice;
            analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
            analyticsParam.setProvince(null);
            analyticsParam.entryPosition = gourmet.entryPosition;
            analyticsParam.totalListCount = -1;
            analyticsParam.isDailyChoice = gourmet.isDailyChoice;
            analyticsParam.setAddressAreaName(gourmet.addressSummary);

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
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , gourmet.category, gourmet.isSoldOut, false, false, true//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_LIST//
                    , analyticsParam);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, optionsCompat.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(mBaseActivity //
                    , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                    , ((GourmetBookingDay) mPlaceBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                    , gourmet.category, gourmet.isSoldOut, false, false, false//
                    , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                    , analyticsParam);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.WISHLIST_CLICKED, //
                gourmet.name, null);
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

            mListLayout.setBlurVisibility(mBaseActivity, true);

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            Gourmet gourmet = placeViewItem.getItem();

            mViewByLongPress = view;
            mPositionByLongPress = position;

            Intent intent = GourmetPreviewActivity.newInstance(mBaseActivity, (GourmetBookingDay) mPlaceBookingDay, gourmet);

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

            if (placeViewItem == null)
            {
                Util.restartApp(getContext());
                return;
            }

            Gourmet gourmet = placeViewItem.getItem();
            if (gourmet == null)
            {
                return;
            }

            GourmetWishListFragment.this.requestRemoveWishListItem(gourmet.index);
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
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
                stringBuilder.append(list.get(i).index);
            }

            stringBuilder.append("]");

            GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;
            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookingDay.getVisitDay("yyyy-MM-dd"));
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
