package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.RecentStayParams;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class StayWishListFragment extends PlaceWishListFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
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
    protected void requestWishList()
    {
        lockUI();

        //        if (mListLayout == null)
        //        {
        //            unLockUI();
        //            return;
        //        }
        if (mSaleTime == null)
        {
            unLockUI();
            return;
        }

        RecentStayParams recentStayParams = new RecentStayParams();
        recentStayParams.setCheckInTime(mSaleTime);

        ((StayWishListNetworkController) mNetworkController).requestStayWishList(recentStayParams);
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

            mListLayout.setData(list);
        }

        @Override
        public void onDeleteWishItem(int position, int placeIndex)
        {
            // TODO : 삭제 처리 서버 결과 도착시 처리 필요.
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            mBaseActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            mBaseActivity.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            mBaseActivity.onErrorToastMessage(message);
        }
    };

    PlaceWishListLayout.OnEventListener mEventListener = new PlaceWishListLayout.OnEventListener()
    {
        @Override
        public void onListItemClick(View view, int position)
        {
            if (position < 0 || mListLayout == null)
            {
                return;
            }

            int size = mListLayout.getSize();
            if (position < 0 || size - 1 < position)
            {
                return;
            }

            Stay stay = (Stay) mListLayout.getItem(position);

            Intent intent = StayDetailActivity.newInstance(mBaseActivity, //
                mSaleTime, stay, 0);

            if (Util.isOverAPI21() == true)
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

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL, options.toBundle());
            } else
            {
                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
            }

            //            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            //                AnalyticsManager.Category.NAVIGATION, //
            //                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
            //                stay.name, null);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mListLayout == null)
            {
                return;
            }

            int size = mListLayout.getSize();
            if (position < 0 || size - 1 < position)
            {
                return;
            }

            // TODO : 삭제 API 연동

            Place place = mListLayout.removeItem(position);
            ExLog.d("isRemove : " + (place != null));

            mListLayout.setData(mListLayout.getList());
            mWishListFragmentListener.onDeleteItemClick(PlaceType.HOTEL, position);

            //            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
            //                AnalyticsManager.Category.NAVIGATION, //
            //                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
            //                place.name, null);
        }

        @Override
        public void onEmptyButtonClick()
        {
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_STAY_LIST);
            finish();
        }

        @Override
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
