package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.remote.RecentlyRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public abstract class RecentPlacesListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected RecentPlacesListLayout mListLayout;

    protected PlaceBookingDay mPlaceBookingDay;

    protected boolean mDontReload = false;

    protected View mViewByLongPress;
    protected int mPositionByLongPress;

    protected RecentlyRemoteImpl mRecentlyRemoteImpl;
    protected RecentlyLocalImpl mRecentlyLocalImpl;

    protected int mWishPosition;

    /**
     * 해당 데이터는 리퀘스트 및 저장 용도로만 사용해야 합니다. emptyList 의 판단은 listAdapter의 갯수 또는 서버 전달 데이터 갯수로 판단해야 합니다.
     */
    //    protected ArrayList<Pair<Integer, String>> mRecentPlaceList;
    protected OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener;

    protected abstract void setPlaceBookingDay(CommonDateTime commonDateTime);

    protected abstract RecentPlacesListLayout getListLayout();

    protected abstract void requestRecentPlacesList();

    public interface OnRecentPlaceListFragmentListener
    {
        void onDeleteItemClickAnalytics();
    }

    public void setRecentPlaceListFragmentListener(OnRecentPlaceListFragmentListener listener)
    {
        mRecentPlaceListFragmentListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mRecentlyRemoteImpl = new RecentlyRemoteImpl();
        mRecentlyLocalImpl = new RecentlyLocalImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mListLayout = getListLayout();

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
                requestRecentPlacesList();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        clearCompositeDisposable();

        super.onDestroy();
    }

    public void setDontReload(boolean dontReload)
    {
        mDontReload = dontReload;
    }
}
