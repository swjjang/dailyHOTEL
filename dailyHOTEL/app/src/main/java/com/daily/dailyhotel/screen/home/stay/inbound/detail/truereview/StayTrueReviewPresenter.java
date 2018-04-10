package com.daily.dailyhotel.screen.home.stay.inbound.detail.truereview;


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
import com.daily.dailyhotel.parcel.analytics.TrueReviewAnalyticsParam;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.truereview.TrueReviewInterface;
import com.daily.dailyhotel.screen.common.truereview.TrueReviewView;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.ReviewTermsActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTrueReviewPresenter extends BaseExceptionPresenter<StayTrueReviewActivity, TrueReviewInterface> implements TrueReviewView.OnEventListener
{
    private static final int TRUE_REVIEW_MAX_COUNT = 50;

    private StayTrueReviewAnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    private int mStayIndex;
    private ReviewScores mReviewScores;
    private int mPage;
    private int mLoadingPage;
    private int mTotalElements;
    private int mTotalPages;
    private int mNumberOfElements;


    public interface StayTrueReviewAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(TrueReviewAnalyticsParam analyticsParam);

        void onScreen(Activity activity);

        void onEventTermsClick(Activity activity);

        void onEventBackClick(Activity activity);
    }

    public StayTrueReviewPresenter(@NonNull StayTrueReviewActivity activity)
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
    public void constructorInitialize(StayTrueReviewActivity activity)
    {
        setContentView(R.layout.activity_true_review_data);

        mAnalytics = new StayTrueReviewAnalyticsImpl();

        mStayRemoteImpl = new StayRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(StayTrueReviewActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0);
        ReviewScoresParcel reviewScoresParcel = intent.getParcelableExtra(StayTrueReviewActivity.INTENT_EXTRA_DATA_REVIEW_SCORES);

        if (mStayIndex == 0 || reviewScoresParcel == null)
        {
            return false;
        }

        mReviewScores = reviewScoresParcel.getReviewScores();
        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        mPage = 1;
        mLoadingPage = 1;

        getViewInterface().setToolbarTitle(getString(R.string.label_truereview));
        getViewInterface().setReviewScores(getString(R.string.message_detail_review_stay_explain), mReviewScores.getReviewScoreList());
        getViewInterface().setTrueReviewProductVisible(DailyRemoteConfigPreference.getInstance(getActivity()).isKeyRemoteConfigStayDetailTrueReviewProductVisible());
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

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
        mAnalytics.onEventBackClick(getActivity());

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

        setRefresh(false);

        screenLock(showProgress);

        addCompositeDisposable(mStayRemoteImpl.getTrueReviews(mStayIndex, mLoadingPage, TRUE_REVIEW_MAX_COUNT)//
            .observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<TrueReviews, Observable<Long>>()
            {
                @Override
                public Observable<Long> apply(@io.reactivex.annotations.NonNull TrueReviews trueReviews) throws Exception
                {
                    unLockAll();

                    addTrueReviews(trueReviews);

                    return Observable.timer(300, TimeUnit.MILLISECONDS);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>()
            {
                @Override
                public void accept(Long aLong) throws Exception
                {
                    unLockAll();

                    getViewInterface().showReviewScoresAnimation();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
    }

    private synchronized void onMoreRefreshing()
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);

        addCompositeDisposable(mStayRemoteImpl.getTrueReviews(mStayIndex, mLoadingPage, TRUE_REVIEW_MAX_COUNT).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TrueReviews>()
        {
            @Override
            public void accept(TrueReviews trueReviews) throws Exception
            {
                addTrueReviews(trueReviews);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onTermsClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(ReviewTermsActivity.newInstance(getActivity()), StayTrueReviewActivity.REQUEST_CODE_REVIEW_TERMS);

        mAnalytics.onEventTermsClick(getActivity());
    }

    @Override
    public void onTopClick()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(getViewInterface().smoothScrollTop().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onNextPage()
    {
        if (getViewInterface() == null || mTotalElements == 0)
        {
            return;
        }

        if (mPage < mTotalPages && mPage + 1 != mLoadingPage)
        {
            getViewInterface().addLoadingFooter();
            mLoadingPage = mPage + 1;

            setRefresh(true);
            onMoreRefreshing();
        }
    }

    void addTrueReviews(TrueReviews trueReviews)
    {
        if (getViewInterface() == null || trueReviews == null)
        {
            return;
        }

        getViewInterface().removeLoadingFooter();

        mPage = trueReviews.page;
        mTotalElements = trueReviews.totalElements;
        mTotalPages = trueReviews.totalPages;
        mNumberOfElements = trueReviews.numberOfElements;

        getViewInterface().addReviewList(trueReviews.getTrueReviewList(), mTotalElements);

        if (mPage == mTotalPages)
        {
            getViewInterface().addLastFooter();
        }
    }
}
