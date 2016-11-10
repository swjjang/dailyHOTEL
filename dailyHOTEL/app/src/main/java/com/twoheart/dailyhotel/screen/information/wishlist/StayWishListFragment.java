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
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

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

            mListLayout.setData(makePlaceViewItemList(list));
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
                    break;
                }
            }

            if (removePosition != -1)
            {
                mListLayout.removeItem(removePosition);
                mListLayout.notifyDataSetChanged();
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.WISHLIST_DELETE, //
                placeName, null);
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

        private ArrayList<PlaceViewItem> makePlaceViewItemList(ArrayList<Stay> list)
        {
            if (list == null || list.size() == 0)
            {
                return null;
            }

            ArrayList<PlaceViewItem> placeViewItems = new ArrayList<>();
            for (Stay stay : list)
            {
                placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, stay));
            }

            placeViewItems.add(new PlaceViewItem(PlaceViewItem.TYPE_FOOTER_VIEW, null));

            return placeViewItems;
        }
    };

    StayWishListLayout.OnEventListener mEventListener = new PlaceWishListLayout.OnEventListener()
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

            PlaceViewItem placeViewItem = mListLayout.getItem(position);
            Stay stay = placeViewItem.getItem();
            if (stay == null)
            {
                return;
            }

            Intent intent = StayDetailActivity.newInstance(mBaseActivity, mSaleTime, stay, 0);

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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.WISHLIST_CLICKED, //
                stay.name, null);
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
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };
}
