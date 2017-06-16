package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutbound;
import com.daily.dailyhotel.entity.StayOutbounds;
import com.daily.dailyhotel.screen.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentStayListFragment extends RecentPlacesListFragment
{
    private static final int REQUEST_CODE_DETAIL = 10000;

    private StayBookDateTime mStayBookDateTime;

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

            setStayBookDateTime(todayDateTime.currentDateTime);
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
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentStayListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return null;
    }

    @Override
    protected void requestRecentPlacesList(PlaceBookingDay placeBookingDay)
    {
        lockUI();

        boolean isInBound = false;
        boolean isOutBound = false;
        if (RecentlyPlaceUtil.ServiceType.ALL_STAY == mServiceType)
        {
            isInBound = true;
            isOutBound = true;
        } else if (RecentlyPlaceUtil.ServiceType.IB_STAY == mServiceType)
        {
            isInBound = true;
        } else if (RecentlyPlaceUtil.ServiceType.OB_STAY == mServiceType)
        {
            isOutBound = true;
        }

        if (isInBound == false && isOutBound == false)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null, placeBookingDay);
            }
            return;
        }

        requestRecentlyList(placeBookingDay);
    }

    private void requestRecentlyList(PlaceBookingDay placeBookingDay)
    {
        String targetIndices = getTargetIndices(RecentlyPlaceUtil.ServiceType.IB_STAY);
        if (DailyTextUtils.isTextEmpty(targetIndices) == true)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null, placeBookingDay);
            }
            return;
        }

        addCompositeDisposable(Observable.zip(mRecentlyRemoteImpl.getStayInboundRecentlyList((StayBookingDay) placeBookingDay) //
            , mRecentlyRemoteImpl.getStayOutboundRecentlyList(10000) //
            , new BiFunction<List<Stay>, StayOutbounds, ArrayList<PlaceViewItem>>()
            {
                @Override
                public ArrayList<PlaceViewItem> apply(@NonNull List<Stay> stays, @NonNull StayOutbounds stayOutbounds) throws Exception
                {
                    return makePlaceViewItemList(stays, stayOutbounds);
                }
            }).subscribe(new Consumer<ArrayList<PlaceViewItem>>()
        {
            @Override
            public void accept(@NonNull ArrayList<PlaceViewItem> viewItemList) throws Exception
            {
                unLockUI();

                if (isFinishing() == true)
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
                onHandleError(throwable);
            }
        }));
    }

    private ArrayList<PlaceViewItem> makePlaceViewItemList(List<Stay> stayList, StayOutbounds stayOutbounds)
    {
        ArrayList<PlaceViewItem> list = new ArrayList<>();

        if (stayList != null)
        {
            for (Stay stay : stayList)
            {
                list.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
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

        if (list.size() == 0)
        {
            return list;
        }

        sortList(list);

        list.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

        return list;
    }

    private void sortList(ArrayList<PlaceViewItem> actualList)
    {
        if (actualList == null || actualList.size() == 0)
        {
            return;
        }

        ArrayList<Integer> expectedList = RecentlyPlaceUtil.getRecentlyIndexList(RecentlyPlaceUtil.ServiceType.IB_STAY, RecentlyPlaceUtil.ServiceType.OB_STAY);
        if (expectedList == null || expectedList.size() == 0)
        {
            return;
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
    }

    private int getPlaceViewItemIndex(PlaceViewItem placeViewItem)
    {
        int index;

        Object object = placeViewItem.getItem();
        if (object == null)
        {
            return -1;
        }

        if (object instanceof Stay)
        {
            index = ((Stay) object).index;
        } else if (object instanceof StayOutbound)
        {
            index = ((StayOutbound) object).index;
        } else
        {
            index = -1;
        }

        return index;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onStayItemClick(View view, PlaceViewItem placeViewItem)
    {
        if (view == null || placeViewItem == null)
        {
            return;
        }

        Stay stay = placeViewItem.getItem();

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

            Intent intent = StayDetailActivity.newInstance(mBaseActivity, (StayBookingDay) mPlaceBookingDay, stay, 0, true);

            View simpleDraweeView = view.findViewById(R.id.imageView);
            View gradeTextView = view.findViewById(R.id.gradeTextView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                android.support.v4.util.Pair.create(gradeTextView, getString(R.string.transition_place_grade)),//
                android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
        } else
        {
            Intent intent = StayDetailActivity.newInstance(mBaseActivity, (StayBookingDay) mPlaceBookingDay, stay, 0, false);

            mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            AnalyticsManager.Category.NAVIGATION_, //
            AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
            stay.name, null);

        if (stay.truevr == true)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.STAY_ITEM_CLICK_TRUE_VR, Integer.toString(stay.index), null);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onStayOutboundItemClick(View view, PlaceViewItem placeViewItem)
    {
        View simpleDraweeView = view.findViewById(R.id.imageView);
        View nameTextView = view.findViewById(R.id.nameTextView);
        View gradientTopView = view.findViewById(R.id.gradientTopView);
        View gradientBottomView = view.findViewById(R.id.gradientView);

        android.support.v4.util.Pair[] pairs = new Pair[4];
        pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
        pairs[1] = android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name));
        pairs[2] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
        pairs[3] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

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

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);

                mBaseActivity.startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, imageUrl, stayOutbound.total//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , 2, null, true, false)//
                , REQUEST_CODE_DETAIL, options.toBundle());
        } else
        {
            mBaseActivity.startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), stayOutbound.index//
                , stayOutbound.name, imageUrl, stayOutbound.total//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , 2, null, false, false)//
                , REQUEST_CODE_DETAIL);

            mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }
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

    private void onStayItemLongClick(View view, int position, PlaceViewItem placeViewItem)
    {
        Stay stay = placeViewItem.getItem();

        mViewByLongPress = view;
        mPositionByLongPress = position;

        Intent intent = StayPreviewActivity.newInstance(mBaseActivity, (StayBookingDay) mPlaceBookingDay, stay);

        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PREVIEW);
    }

    private void onStayOutboundItemLongClick()
    {
        DailyToast.showToast(getActivity(), getString(R.string.label_stay_outbound_preparing_preview), DailyToast.LENGTH_SHORT);
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

            if (object instanceof Stay)
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

            mListLayout.setBlurVisibility(mBaseActivity, true);

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            Object object = placeViewItem.getItem();

            if (object == null)
            {
                return;
            }

            if (object instanceof Stay)
            {
                onStayItemLongClick(view, position, placeViewItem);
            } else if (object instanceof StayOutbound)
            {
                onStayOutboundItemLongClick();
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
            Place place = placeViewItem.getItem();
            ExLog.d("isRemove : " + (place != null));

            RecentlyPlaceUtil.deleteRecentlyItemAsync(RecentlyPlaceUtil.ServiceType.IB_STAY, place.index);

            mListLayout.setData(mListLayout.getList(), mPlaceBookingDay);
            mRecentPlaceListFragmentListener.onDeleteItemClickAnalytics();

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
                place.name, null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENTVIEW_ITEM_DELETE, Integer.toString(place.index), null);
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
                Place place = placeViewItem.getItem();

                if (place != null)
                {
                    stringBuilder.append(place.index);
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
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
