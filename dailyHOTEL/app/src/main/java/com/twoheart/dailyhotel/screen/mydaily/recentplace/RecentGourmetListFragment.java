package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.RecentGourmetParams;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.preview.GourmetPreviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Response;

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
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentGourmetListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new RecentGourmetListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void requestRecentPlacesList(PlaceBookingDay placeBookingDay)
    {
        lockUI();

        int count = mRecentPlaceList != null ? mRecentPlaceList.size() : 0;
        if (count == 0)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null, placeBookingDay);
            }
            return;
        }

        RecentGourmetParams params = new RecentGourmetParams();
        params.setGourmetBookingDay((GourmetBookingDay) placeBookingDay);
        params.setTargetIndices(getPlaceIndexList());

        ((RecentGourmetListNetworkController) mNetworkController).requestRecentGourmetList(params);
    }

    private RecentGourmetListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentGourmetListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentGourmetList(ArrayList<Gourmet> list)
        {
            unLockUI();

            if (isFinishing() == true)
            {
                return;
            }

            sortList(mRecentPlaceList, list);

            ArrayList<PlaceViewItem> viewItemList = mListLayout.makePlaceViewItemList(list);

            mListLayout.setData(viewItemList, mPlaceBookingDay);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            RecentGourmetListFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            RecentGourmetListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            RecentGourmetListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            RecentGourmetListFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            RecentGourmetListFragment.this.onErrorResponse(call, response);
        }
    };

    RecentPlacesListLayout.OnEventListener mEventListener = new RecentPlacesListLayout.OnEventListener()
    {
        @Override
        public void onListItemClick(View view, int position)
        {
            if (position < 0 || mRecentPlaceList.size() - 1 < position)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            Gourmet gourmet = placeViewItem.getItem();

            if (Util.isUsedMultiTransition() == true)
            {
                //                mBaseActivity.setExitSharedElementCallback(new SharedElementCallback()
                //                {
                //                    @Override
                //                    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                //                    {
                //                        super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                //
                //                        for (View view : sharedElements)
                //                        {
                //                            if (view instanceof SimpleDraweeView)
                //                            {
                //                                view.setVisibility(View.VISIBLE);
                //                                break;
                //                            }
                //                        }
                //                    }
                //                });

                Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                    (GourmetBookingDay) mPlaceBookingDay, gourmet, 0, true);

                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
            } else
            {
                Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                    (GourmetBookingDay) mPlaceBookingDay, gourmet, 0, false);

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
                gourmet.name, null);
        }

        @Override
        public void onListItemLongClick(View view, int position)
        {
            if (position < 0 || mRecentPlaceList.size() - 1 < position)
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
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mRecentPlaceList.size() - 1 < position)
            {
                return;
            }

            PlaceViewItem placeViewItem = mListLayout.removeItem(position);
            Place place = placeViewItem.getItem();
            ExLog.d("isRemove : " + (place != null));

            Pair<Integer, String> deleteItem = new Pair<>(place.index, RecentPlaces.getServiceType(PlaceType.FNB));

            mRecentPlaceList.remove(deleteItem);

            mListLayout.setData(mListLayout.getList(), mPlaceBookingDay);
            mRecentPlaceListFragmentListener.onDeleteItemClick(deleteItem);

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
                Place place = placeViewItem.getItem();

                if (place != null)
                {
                    stringBuilder.append(place.index);
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
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
