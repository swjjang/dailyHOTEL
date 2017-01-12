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
import com.twoheart.dailyhotel.util.Constants;

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

            mBaseActivity.startActivityForResult(StayMainActivity.newInstance(getContext()), Constants.CODE_REQUEST_ACTIVITY_STAY);
        }

        @Override
        public void onGourmetButtonClick()
        {
            if (mBaseActivity == null) {
                return;
            }

            mBaseActivity.startActivityForResult(GourmetMainActivity.newInstance(getContext()), Constants.CODE_REQUEST_ACTIVITY_GOURMET);
        }

        @Override
        public void finish()
        {

        }
    };
}
