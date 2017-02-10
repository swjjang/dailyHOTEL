package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static com.twoheart.dailyhotel.model.RecentPlaces.RECENT_PLACE_DELIMITER;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public abstract class RecentPlacesListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected RecentPlacesListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected SaleTime mSaleTime;

    protected boolean mDontReload = false;

    /**
     * 해당 데이터는 리퀘스트 및 저장 용도로만 사용해야 합니다. emptyList 의 판단은 listAdapter의 갯수 또는 서버 전달 데이터 갯수로 판단해야 합니다.
     */
    protected ArrayList<Pair<Integer, String>> mRecentPlaceList;
    protected OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener;

    protected abstract RecentPlacesListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestRecentPlacesList();

    public interface OnRecentPlaceListFragmentListener
    {
        void onDeleteItemClick(Pair<Integer, String> deleteItem);
    }

    public void setRecentPlaceListFragmentListener(OnRecentPlaceListFragmentListener listener)
    {
        mRecentPlaceListFragmentListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mListLayout = getListLayout();
        mNetworkController = getNetworkController();

        mDontReload = false;

        return mListLayout.onCreateView(R.layout.fragment_recent_places_list, container);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mDontReload == true)
        {
            mDontReload = false;
        } else
        {
            requestRecentPlacesList();
        }
    }

    public void setSaleTime(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            return;
        }

        mSaleTime = saleTime;
    }


    public void setRecentPlaceList(ArrayList<Pair<Integer, String>> recentPlaceList)
    {
        mRecentPlaceList = recentPlaceList;
    }

    public void setDontReload(boolean dontReload)
    {
        mDontReload = dontReload;
    }

    public String getPlaceIndexList()
    {
        if (mRecentPlaceList == null || mRecentPlaceList.size() == 0)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        Iterator<Pair<Integer, String>> iterator = mRecentPlaceList.iterator();

        while (iterator.hasNext() == true)
        {
            Pair<Integer, String> pair = iterator.next();
            builder.append(pair.first);

            if (iterator.hasNext() == true)
            {
                builder.append(RECENT_PLACE_DELIMITER);
            }
        }

        return builder.toString();
    }

    public void sortList(final ArrayList<Pair<Integer, String>> expectedList, ArrayList<? extends Place> actual)
    {
        if (expectedList == null || expectedList.size() == 0)
        {
            return;
        }

        final String serviceType = expectedList.get(0).second;

        if (actual != null && actual.size() > 0)
        {
            Collections.sort(actual, new Comparator<Place>()
            {
                @Override
                public int compare(Place place1, Place place2)
                {
                    Pair<Integer, String> pair1 = new Pair<>(place1.index, serviceType);
                    Pair<Integer, String> pair2 = new Pair<>(place2.index, serviceType);

                    Integer position1 = expectedList.indexOf(pair1);
                    Integer position2 = expectedList.indexOf(pair2);

                    return position1.compareTo(position2);
                }
            });
        }
    }
}
