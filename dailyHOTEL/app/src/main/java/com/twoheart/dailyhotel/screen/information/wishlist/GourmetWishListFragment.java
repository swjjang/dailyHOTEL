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
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailNetworkController;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

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
    protected PlaceWishListLayout getListLayout()
    {
        return new GourmetWishListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new GourmetDetailNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void requestWishList()
    {
        lockUI();

        ((GourmetWishListNetworkController) mNetworkController).requestGourmetWishList();
    }

    @Override
    protected void requestDeleteWishListItem()
    {
        lockUI();

        ((GourmetWishListNetworkController) mNetworkController).requestDeleteGourmetWishListItem();
    }

    private GourmetWishListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetWishListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetWishList(ArrayList<Gourmet> list)
        {
            unLockUI();

            if (isFinishing() == true) {
                return;
            }

            if (mListLayout == null) {
                return;
            }

            mListLayout.setData(list);
        }

        @Override
        public void onDeleteGourmetWishListItem(int position)
        {
            unLockUI();

            if (isFinishing() == true) {
                return;
            }

            if (mListLayout == null) {
                return;
            }

            if (position < 0 || position > mListLayout.getItemCount() -1) {
                return;
            }

            mListLayout.removeItem(position);
            mListLayout.notifyDataSetChanged();

//            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
//                AnalyticsManager.Category.NAVIGATION, //
//                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
//                place.name, null);
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

    private GourmetWishListLayout.OnEventListener mEventListener = new PlaceWishListLayout.OnEventListener()
    {
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
            if (position < 0)
            {
                return;
            }

            if (mListLayout == null)
            {
                return;
            }

            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);
            if (gourmet == null) {
                return;
            }

            GourmetWishListFragment.this.requestDeleteWishListItem();
        }

        @Override
        public void onEmptyButtonClick()
        {
            unLockUI();
            mBaseActivity.setResult(Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST);
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
