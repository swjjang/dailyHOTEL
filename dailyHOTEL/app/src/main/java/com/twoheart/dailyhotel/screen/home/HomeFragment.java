package com.twoheart.dailyhotel.screen.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainActivity;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeFragment extends BaseFragment
{
    private HomeLayout mHomeLayout;
    private BaseActivity mBaseActivity;
    private PlaceType mPlaceType = PlaceType.HOTEL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mHomeLayout = new HomeLayout(mBaseActivity, mOnEventListener);
        return mHomeLayout.onCreateView(R.layout.fragment_home_main, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mBaseActivity.unLockUI();
    }

    private HomeLayout.OnEventListener mOnEventListener = new HomeLayout.OnEventListener()
    {

        @Override
        public void onSearchImageClick()
        {
            if (mBaseActivity == null) {
                return;
            }

//            mBaseActivity.startActivity(SearchActivity.newInstance(getContext(), mPlaceType, ));
        }

        @Override
        public void onStayButtonClick()
        {
            if (mBaseActivity == null) {
                return;
            }

            mBaseActivity.startActivity(StayMainActivity.newInstance(getContext()));
        }

        @Override
        public void onGourmetButtonClick()
        {
            if (mBaseActivity == null) {
                return;
            }

            mBaseActivity.startActivity(GourmetMainActivity.newInstance(getContext()));
        }

        @Override
        public void finish()
        {

        }
    };
}
