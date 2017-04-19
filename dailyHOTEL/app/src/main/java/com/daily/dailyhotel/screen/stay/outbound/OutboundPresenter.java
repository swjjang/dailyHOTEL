package com.daily.dailyhotel.screen.stay.outbound;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.twoheart.dailyhotel.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class OutboundPresenter extends BaseExceptionPresenter<OutboundActivity, OutboundViewInterface> implements OutboundView.OnEventListener
{
    private OutboundAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;

    public interface OutboundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public OutboundPresenter(@NonNull OutboundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected OutboundViewInterface createInstanceViewInterface()
    {
        return new OutboundView(getActivity(), this);
    }

    @Override
    public void initialize(OutboundActivity activity)
    {
        setContentView(R.layout.activity_outbound_data);

        setAnalytics(new OutboundAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);

    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (OutboundAnalyticsInterface) analytics;
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

    @Override
    public void onRequestSuggests(String keyword)
    {
        getViewInterface().setRecentlySuggestsVisibility(false);

        clearCompositeDisposable();

        addCompositeDisposable(Observable.timer(500, TimeUnit.MILLISECONDS).doOnNext(timer -> addCompositeDisposable(//
            mSuggestRemoteImpl.getSuggestsByStayOutBound(keyword).doOnNext(this::onSuggests).subscribe(suggests -> onSuggests(suggests), throwable -> onSuggests(null)))//
        ).subscribe());
    }

    @Override
    public void onSuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }


    }

    @Override
    public void onReset()
    {
        getViewInterface().setSuggests(null);
    }

    private void onSuggests(List<Suggest> suggestList)
    {
        if (suggestList == null)
        {
            return;
        }

        getViewInterface().setSuggests(suggestList);
        getViewInterface().setSuggestsVisibility(true);
    }
}
