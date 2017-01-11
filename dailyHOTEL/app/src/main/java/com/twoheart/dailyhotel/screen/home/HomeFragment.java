package com.twoheart.dailyhotel.screen.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeFragment extends BaseFragment
{
    private HomeLayout mHomeLayout;
    private BaseActivity mBaseActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mHomeLayout = new HomeLayout(mBaseActivity, mOnEventListener);
        return mHomeLayout.onCreateView(R.layout.fragment_home_main, container);
    }







    private HomeLayout.OnEventListener mOnEventListener = new HomeLayout.OnEventListener() {
        @Override
        public void finish()
        {

        }
    };
}
