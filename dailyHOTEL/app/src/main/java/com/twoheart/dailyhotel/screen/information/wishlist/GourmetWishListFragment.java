package com.twoheart.dailyhotel.screen.information.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.RecentGourmetParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.information.recentplace.RecentPlacesListLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class GourmetWishListFragment extends PlaceWishListFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
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
    protected void requestWishList()
    {
        lockUI();

        //        if (mListLayout == null) {
        //            unLockUI();
        //            return;
        //        }
        if (mSaleTime == null)
        {
            unLockUI();
            return;
        }

        RecentGourmetParams params = new RecentGourmetParams();
        params.setSaleTime(mSaleTime);

        ((GourmetWishListNetworkController) mNetworkController).requestGourmetWishList(params);
    }

    private GourmetWishListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetWishListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentGourmetList(ArrayList<Gourmet> list)
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

    RecentPlacesListLayout.OnEventListener mEventListener = new RecentPlacesListLayout.OnEventListener()
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

            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);

            Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                mSaleTime, gourmet, 0);

            if (Util.isOverAPI21() == true)
            {
                View simpleDraweeView = view.findViewById(R.id.imageView);
                View nameTextView = view.findViewById(R.id.nameTextView);
                View gradientTopView = view.findViewById(R.id.gradientTopView);
                View gradientBottomView = view.findViewById(R.id.gradientView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name)),//
                    android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view)),//
                    android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view)));

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL, options.toBundle());
            } else
            {
                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
            }

//            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
//                AnalyticsManager.Category.NAVIGATION, //
//                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
//                gourmet.name, null);
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
            mWishListFragmentListener.onDeleteItemClick(PlaceType.FNB, position);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
                place.name, null);
        }

        @Override
        public void onEmptyButtonClick()
        {
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
            finish();
        }

        @Override
        public void finish()
        {
            mBaseActivity.finish();
        }
    };
}
