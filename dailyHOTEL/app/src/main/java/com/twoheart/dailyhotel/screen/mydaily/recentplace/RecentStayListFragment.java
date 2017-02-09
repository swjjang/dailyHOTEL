package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.RecentStayParams;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

import static com.twoheart.dailyhotel.model.RecentPlaces.getServiceType;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentStayListFragment extends RecentPlacesListFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentStayListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new RecentStayListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void requestRecentPlacesList()
    {
        lockUI();

        int count = mRecentPlaceList != null ? mRecentPlaceList.size() : 0;
        if (count == 0)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null);
            }
            return;
        }

        RecentStayParams recentStayParams = new RecentStayParams();
        recentStayParams.setCheckInTime(mSaleTime);
        recentStayParams.setTargetIndices(getPlaceIndexList());

        ((RecentStayListNetworkController) mNetworkController).requestRecentStayList(recentStayParams);
        //        DailyToast.showToast(mBaseActivity, "recent Stay", Toast.LENGTH_SHORT);
    }

    private RecentStayListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentStayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentStayList(ArrayList<Stay> list)
        {
            unLockUI();

            if (isFinishing() == true)
            {
                return;
            }

            sortList(mRecentPlaceList, list);

            mListLayout.setData(list);
        }

        @Override
        public void onError(Throwable e)
        {
            unLockUI();
            RecentStayListFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            RecentStayListFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            RecentStayListFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            unLockUI();
            RecentStayListFragment.this.onErrorResponse(call, response);
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

            Stay stay = (Stay) mListLayout.getItem(position);

            Intent intent = StayDetailActivity.newInstance(mBaseActivity, mSaleTime, stay, 0);

            if (Util.isUsedMultiTransition() == true)
            {
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
                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_, //
                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
                stay.name, null);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mRecentPlaceList.size() - 1 < position)
            {
                ExLog.d("position Error Stay");
                return;
            }

            Place place = mListLayout.removeItem(position);
            ExLog.d("isRemove : " + (place != null));

            Pair<Integer, String> deleteItem = new Pair<>(place.index, RecentPlaces.getServiceType(PlaceType.HOTEL));

            mRecentPlaceList.remove(deleteItem);

            mListLayout.setData(mListLayout.getList());
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
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
            finish();
        }

        @Override
        public void onRecordAnalyticsList(ArrayList<? extends Place> list)
        {
            if (list == null || list.isEmpty() == true || mSaleTime == null)
            {
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();
            String placeTypeString = AnalyticsManager.ValueType.STAY;
            int dayOfDays = mSaleTime.getOffsetDailyDay();
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

            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.PLACE_HIT_TYPE, placeTypeString);
            params.put(AnalyticsManager.KeyType.CHECK_IN, mSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, mSaleTime.getClone(dayOfDays + 1).getDayOfDaysDateFormat("yyyy-MM-dd"));
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
