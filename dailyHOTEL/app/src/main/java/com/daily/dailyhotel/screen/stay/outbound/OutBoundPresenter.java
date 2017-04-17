package com.daily.dailyhotel.screen.stay.outbound;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.UserBenefit;
import com.daily.dailyhotel.parcel.UserParcel;
import com.daily.dailyhotel.repository.remote.FacebookImpl;
import com.daily.dailyhotel.repository.remote.KakaoImpl;
import com.daily.dailyhotel.screen.mydaily.profile.ProfileActivity;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class OutBoundPresenter extends BaseExceptionPresenter<OutBoundActivity, OutBoundViewInterface> implements OutBoundView.OnEventListener
{
    private OutBoundAnalyticsInterface mAnalytics;

    public interface OutBoundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public OutBoundPresenter(@NonNull OutBoundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected OutBoundViewInterface createInstanceViewInterface()
    {
        return new OutBoundView(getActivity(), this);
    }

    @Override
    public void initialize(OutBoundActivity activity)
    {
        setContentView(R.layout.activity_outbound_data);

        setAnalytics(new OutBoundAnalyticsImpl());



    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (OutBoundAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {
        onBackPressed();
    }

    @Override
    public void onIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }
}
