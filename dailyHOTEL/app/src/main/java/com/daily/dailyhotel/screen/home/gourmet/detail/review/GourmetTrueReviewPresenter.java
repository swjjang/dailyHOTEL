package com.daily.dailyhotel.screen.home.gourmet.detail.review;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.parcel.ReviewScoresParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetTrueReviewAnalyticsParam;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.ReviewTermsActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetTrueReviewPresenter extends BaseExceptionPresenter<GourmetTrueReviewActivity, GourmetTrueReviewInterface> implements GourmetTrueReviewView.OnEventListener
{
    private static final int TRUE_REVIEW_MAX_COUNT = 50;

    private GourmetTrueReviewAnalyticsInterface mAnalytics;

    private GourmetRemoteImpl mGourmetRemoteImpl;

    private int mGourmetIndex;
    private ReviewScores mReviewScores;
    private int mPage;
    private int mLoadingPage;
    private int mTotalElements;
    private int mTotalPages;
    private int mNumberOfElements;

    private int mScrollDistance;
    private int mPrevScrollDistance;

    public interface GourmetTrueReviewAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetTrueReviewAnalyticsParam analyticsParam);

        void onScreen(Activity activity);
    }

    public GourmetTrueReviewPresenter(@NonNull GourmetTrueReviewActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetTrueReviewInterface createInstanceViewInterface()
    {
        return new GourmetTrueReviewView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetTrueReviewActivity activity)
    {
        setContentView(R.layout.activity_true_review_data);

        setAnalytics(new GourmetGourmetTrueReviewAnalyticsImpl());

        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetTrueReviewAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mGourmetIndex = intent.getIntExtra(GourmetTrueReviewActivity.INTENT_EXTRA_DATA_GOURMET_INDEX, 0);
        ReviewScoresParcel reviewScoresParcel = intent.getParcelableExtra(GourmetTrueReviewActivity.INTENT_EXTRA_DATA_REVIEW_SCORES);

        if (mGourmetIndex == 0 || reviewScoresParcel == null)
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
        mPage = 1;
        mLoadingPage = 1;

        getViewInterface().setReviewScores(mReviewScores.getReviewScoreList());

        addCompositeDisposable(Observable.timer(300, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>()
        {
            @Override
            public void accept(Long aLong) throws Exception
            {
                getViewInterface().showReviewScoresAnimation();
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

        addCompositeDisposable(mGourmetRemoteImpl.getGourmetTrueReviews(mGourmetIndex, mLoadingPage, TRUE_REVIEW_MAX_COUNT).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<TrueReviews>()
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

        startActivityForResult(ReviewTermsActivity.newInstance(getActivity()), GourmetTrueReviewActivity.REQUEST_CODE_REVIEW_TERMS);
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
    public void onScroll(RecyclerView recyclerView, int dx, int dy)
    {
        if (recyclerView == null || mTotalElements == 0)
        {
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (linearLayoutManager == null)
        {
            return;
        }

        final int LOAD_MORE_POSITION_GAP = TRUE_REVIEW_MAX_COUNT / 3;

        boolean isLoading = false;

        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        int itemCount = linearLayoutManager.getItemCount();

        int loadMorePosition = itemCount > LOAD_MORE_POSITION_GAP //
            ? lastVisibleItemPosition + LOAD_MORE_POSITION_GAP //
            : lastVisibleItemPosition + (itemCount / 3);

        if (itemCount > 0 && (itemCount - 1) <= loadMorePosition)
        {
            if (mPage <= mTotalPages && mPage + 1 != mLoadingPage)
            {
                isLoading = true;

                getViewInterface().addLoadingFooter();
                mLoadingPage = mPage + 1;

                setRefresh(true);
                onRefresh(false);
            }
        }

        if (linearLayoutManager.findFirstVisibleItemPosition() == 0 //
            && recyclerView.getChildAt(0).getTop() == 0)
        {
            getViewInterface().setTopButtonVisible(false);
            return;
        }

        mScrollDistance += dy;

        if (isLoading == false && lastVisibleItemPosition > mTotalElements)
        {
            getViewInterface().setTopButtonVisible(true);
            return;
        }

        final int visibleDistance = recyclerView.getHeight() / 6;
        int moveDistance = mScrollDistance - mPrevScrollDistance;

        if (moveDistance > 0 && moveDistance > visibleDistance)
        {
            getViewInterface().setTopButtonVisible(false);
            mPrevScrollDistance = mScrollDistance;
        } else if (moveDistance < 0 && -moveDistance > visibleDistance)
        {
            getViewInterface().setTopButtonVisible(true);
            mPrevScrollDistance = mScrollDistance;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        switch (newState)
        {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                break;

            case RecyclerView.SCROLL_STATE_IDLE:
                mScrollDistance = 0;
                mPrevScrollDistance = 0;
                break;

            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
        }
    }

    private void addTrueReviews(TrueReviews trueReviews)
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

        if (mNumberOfElements == 0)
        {
            getViewInterface().addLastFooter();
        } else
        {
            getViewInterface().addReviewList(trueReviews.getTrueReviewList(), mTotalElements);
        }
    }
}
