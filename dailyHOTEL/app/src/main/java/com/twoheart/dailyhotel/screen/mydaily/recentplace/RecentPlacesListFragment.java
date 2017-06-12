package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public abstract class RecentPlacesListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected RecentPlacesListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected PlaceBookingDay mPlaceBookingDay;

    protected boolean mDontReload = false;

    protected View mViewByLongPress;
    protected int mPositionByLongPress;
    protected PlaceType mPlaceType;

    /**
     * 해당 데이터는 리퀘스트 및 저장 용도로만 사용해야 합니다. emptyList 의 판단은 listAdapter의 갯수 또는 서버 전달 데이터 갯수로 판단해야 합니다.
     */
    //    protected ArrayList<Pair<Integer, String>> mRecentPlaceList;
    protected OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener;

    protected abstract void setPlaceBookingDay(TodayDateTime todayDateTime);

    protected abstract RecentPlacesListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestRecentPlacesList(PlaceBookingDay placeBookingDay);

    public interface OnRecentPlaceListFragmentListener
    {
        void onDeleteItemClickAnalytics();
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

        if (mListLayout != null && mListLayout.getBlurVisibility() == true)
        {
            mListLayout.setBlurVisibility(mBaseActivity, false);
        } else
        {
            if (mDontReload == true)
            {
                mDontReload = false;
            } else
            {
                requestRecentPlacesList(mPlaceBookingDay);
            }
        }
    }

    public void setPlaceType(PlaceType placeType)
    {
        mPlaceType = placeType;
    }

    public void setDontReload(boolean dontReload)
    {
        mDontReload = dontReload;
    }

    public String getPlaceIndexList()
    {
        return RecentlyPlaceUtil.getPlaceIndexList(mPlaceType, RecentlyPlaceUtil.MAX_RECENT_PLACE_COUNT);
    }

    //    public void sortList(final RealmResults<RecentlyRealmObject> expectedList, ArrayList<? extends Place> actual)
    //    {
    //        if (expectedList == null || expectedList.size() == 0)
    //        {
    //            return;
    //        }
    //
    //        final String serviceType = expectedList.get(0).second;
    //
    //        if (actual != null && actual.size() > 0)
    //        {
    //            Collections.sort(actual, new Comparator<Place>()
    //            {
    //                @Override
    //                public int compare(Place place1, Place place2)
    //                {
    //                    RecentlyRealmObject realmObject1 = new RecentlyRealmObject();
    //                    realmObject1.index = place1.index;
    //
    //                    RecentlyRealmObject realmObject2 = new RecentlyRealmObject();
    //                    realmObject2.index = place1.index;
    //
    //                    Pair<Integer, String> pair1 = new Pair<>(place1.index, serviceType);
    //                    Pair<Integer, String> pair2 = new Pair<>(place2.index, serviceType);
    //
    //                    Integer position1 = expectedList.indexOf(place1.index);
    //                    Integer position2 = expectedList.indexOf(place2.index);
    //
    //                    return position1.compareTo(position2);
    //                }
    //            });
    //        }
    //    }
}
