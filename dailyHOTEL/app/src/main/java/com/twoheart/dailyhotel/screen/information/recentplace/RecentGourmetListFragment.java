package com.twoheart.dailyhotel.screen.information.recentplace;

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
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;

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
    protected void requestRecentPlacesList()
    {
        lockUI();

        int count = mRecentPlaces != null ? mRecentPlaces.size() : 0;
        if (count == 0)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null);
            }
            return;
        }

        RecentGourmetParams params = new RecentGourmetParams();
        params.setSaleTime(mSaleTime);
        params.setTargetIndices(mRecentPlaces.toString());

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

            ArrayList<Gourmet> resultList = new ArrayList<>();

            if (mRecentPlaces != null && list != null && list.size() > 0)
            {
                ArrayList<Gourmet> cloneList = (ArrayList<Gourmet>) list.clone();

                for (String stringIndex : mRecentPlaces.getList())
                {
                    if (Util.isTextEmpty(stringIndex) == true)
                    {
                        continue;
                    }

                    int index = -1;
                    try
                    {
                        index = Integer.parseInt(stringIndex);
                    } catch (NumberFormatException e)
                    {
                        ExLog.d(e.getMessage());
                    }

                    if (index > 0)
                    {
                        for (Gourmet gourmet : cloneList)
                        {
                            if (index == gourmet.index)
                            {
                                resultList.add(gourmet);
                                cloneList.remove(gourmet);
                                break;
                            }
                        }
                    }
                }
            }

            mListLayout.setData(resultList);
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
            if (position < 0 || mRecentPlaces.size() - 1 < position)
            {
                return;
            }

            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);

            Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                mSaleTime, gourmet, 0);

            if (Util.isUsedMutilTransition() == true)
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

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENT_VIEW_CLICKED, //
                gourmet.name, null);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mRecentPlaces.size() - 1 < position)
            {
                return;
            }

            mRecentPlaces.remove(position);

            Place place = mListLayout.removeItem(position);
            ExLog.d("isRemove : " + (place != null));

            if (place != null)
            {
                DailyPreference.getInstance(mBaseActivity).setGourmetRecentPlaces(mRecentPlaces.toString());
            }

            mListLayout.setData(mListLayout.getList());
            mRecentPlaceListFragmentListener.onDeleteItemClick(PlaceType.FNB, mRecentPlaces);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.RECENT_VIEW_DELETE, //
                place.name, null);
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
