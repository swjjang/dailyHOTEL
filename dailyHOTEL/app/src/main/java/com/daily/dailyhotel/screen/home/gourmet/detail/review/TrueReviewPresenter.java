package com.daily.dailyhotel.screen.home.gourmet.detail.review;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.parcel.ReviewScoresParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetTrueReviewAnalyticsParam;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.twoheart.dailyhotel.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class TrueReviewPresenter extends BaseExceptionPresenter<TrueReviewActivity, TrueReviewInterface> implements TrueReviewView.OnEventListener
{
    private static final int TRUE_REVIEW_MAX_COUNT = 50;

    private TrueReviewAnalyticsInterface mAnalytics;

    private GourmetRemoteImpl mGourmetRemoteImpl;

    private int mGourmetIndex;
    private ReviewScores mReviewScores;
    private TrueReviews mTrueReviews;
    private int mPage;

    public interface TrueReviewAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetTrueReviewAnalyticsParam analyticsParam);

        void onScreen(Activity activity);
    }

    public TrueReviewPresenter(@NonNull TrueReviewActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected TrueReviewInterface createInstanceViewInterface()
    {
        return new TrueReviewView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(TrueReviewActivity activity)
    {
        setContentView(R.layout.activity_true_review_data);

        setAnalytics(new TrueReviewAnalyticsImpl());

        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (TrueReviewAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mGourmetIndex = intent.getIntExtra(TrueReviewActivity.INTENT_EXTRA_DATA_GOURMET_INDEX, 0);
        ReviewScoresParcel reviewScoresParcel = intent.getParcelableExtra(TrueReviewActivity.INTENT_EXTRA_DATA_REVIEW_SCORES);

        if(mGourmetIndex == 0 || reviewScoresParcel == null)
        {
            return false;
        }

        mReviewScores = reviewScoresParcel.getReviewScores();
        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        return true;
    }

    @Override
    public void onPostCreate()
    {
        addCompositeDisposable(mGourmetRemoteImpl.getGourmetTrueReviews(mGourmetIndex, mPage, TRUE_REVIEW_MAX_COUNT).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TrueReviews>()
        {
            @Override
            public void accept(TrueReviews trueReviews) throws Exception
            {

            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {

            }
        }));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
