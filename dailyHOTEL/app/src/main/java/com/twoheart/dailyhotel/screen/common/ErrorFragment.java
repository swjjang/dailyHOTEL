/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * ErrorFragment (오류 화면)
 * <p>
 * 네트워크 문제 등 오류가 발생했을 시 보여지는 화면이다. 이 화면은 메인 화
 * 면 단위(MainActivity)에서 사용되는 작은 화면 단위(Fragment)이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.main.MainFragmentManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class ErrorFragment extends BaseFragment implements OnClickListener
{
    private MainFragmentManager mMainFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return null;
        }

        baseActivity.unLockUI();

        View view = inflater.inflate(R.layout.fragment_error, container, false);

        initToolbar(baseActivity, view, getString(R.string.actionbar_title_error_frag));

        TextView retryTextView = (TextView) view.findViewById(R.id.btn_error);
        retryTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getContext()).recordScreen(AnalyticsManager.Screen.NETWORK_ERROR);

        super.onStart();
    }

    public void setMenuManager(MainFragmentManager mainFragmentManager)
    {
        mMainFragmentManager = mainFragmentManager;
    }

    private void initToolbar(final BaseActivity baseActivity, View view, String title)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(baseActivity, toolbar);
        dailyToolbarLayout.initToolbar(title, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                baseActivity.finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        // network 연결이 안되있으면
        if (VolleyHttpClient.isAvailableNetwork(baseActivity) == false)
        {
            DailyToast.showToast(baseActivity, getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi), Toast.LENGTH_SHORT);
        } else
        {
            if (mMainFragmentManager != null)
            {
                mMainFragmentManager.select(mMainFragmentManager.getLastIndexFragment(), false);
            }
        }
    }
}
