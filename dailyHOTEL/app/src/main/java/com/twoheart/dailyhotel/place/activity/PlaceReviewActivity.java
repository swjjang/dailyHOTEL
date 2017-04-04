package com.twoheart.dailyhotel.place.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.PlaceReviewScore;
import com.twoheart.dailyhotel.network.model.PlaceReviews;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceReviewLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceReviewNetworkController;
import com.twoheart.dailyhotel.screen.common.ReviewTermsActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class PlaceReviewActivity extends BaseActivity
{
    private static final int MAX_COUNT = 50;

    PlaceReviewLayout mPlaceReviewLayout;
    PlaceReviewNetworkController mPlaceReviewNetworkController;
    PlaceReviews mPlaceReviews;
    int mPlaceIndex;

    protected abstract PlaceType getPlaceType();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mPlaceReviewLayout = new PlaceReviewLayout(this, mOnEventListener);
        mPlaceReviewNetworkController = new PlaceReviewNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mPlaceReviewLayout.onCreateView(R.layout.activity_place_review));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mPlaceReviews == null)
        {
            mPlaceReviewNetworkController.requestPlaceReviews(getPlaceType(), mPlaceIndex, 1, MAX_COUNT);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void setReviewScores(List<PlaceReviewScore> placeReviewScoreList)
    {
        if (mPlaceReviewLayout == null)
        {
            return;
        }

        mPlaceReviewLayout.setReviewScores(placeReviewScoreList);
    }

    public void addReviewList(PlaceReviews placeReviews)
    {
        if (mPlaceReviewLayout == null || placeReviews == null)
        {
            return;
        }

        mPlaceReviewLayout.removeLoadingFooter();

        mPlaceReviews = placeReviews;

        if (placeReviews.numberOfElements == 0)
        {
            mPlaceReviewLayout.addDailyFooter();
        } else
        {
            mPlaceReviewLayout.addReviewList(placeReviews.content, placeReviews.totalElements);
        }
    }

    protected void setPlaceIndex(int placeIndex)
    {
        mPlaceIndex = placeIndex;
    }

    private PlaceReviewLayout.OnEventListener mOnEventListener = new PlaceReviewLayout.OnEventListener()
    {
        @Override
        public void onTermsClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(ReviewTermsActivity.newInstance(PlaceReviewActivity.this), Constants.CODE_REQUEST_ACTIVITY_REVIEW_TERMS);
        }

        @Override
        public void onScroll(RecyclerView recyclerView, int dx, int dy)
        {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (linearLayoutManager == null || mPlaceReviews == null)
            {
                return;
            }

            final int LOAD_MORE_POSITION_GAP = Constants.PAGENATION_LIST_SIZE / 3;

            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            int itemCount = linearLayoutManager.getItemCount();

            int loadMorePosition = itemCount > LOAD_MORE_POSITION_GAP //
                ? lastVisibleItemPosition + LOAD_MORE_POSITION_GAP //
                : lastVisibleItemPosition + (itemCount / 3);

            if (itemCount > 0 && (itemCount - 1) <= loadMorePosition)
            {
                if (mPlaceReviews.page <= mPlaceReviews.totalPages && mPlaceReviews.page + 1 != mPlaceReviews.loadingPage)
                {
                    mPlaceReviewLayout.addLoadingFooter();

                    mPlaceReviews.loadingPage = mPlaceReviews.page + 1;
                    mPlaceReviewNetworkController.requestPlaceReviews(getPlaceType(), mPlaceIndex, mPlaceReviews.page + 1, MAX_COUNT);
                }
            }
        }

        @Override
        public void finish()
        {
            PlaceReviewActivity.this.onBackPressed();
        }
    };

    private PlaceReviewNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new PlaceReviewNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onReviews(PlaceReviews placeReviews)
        {
            unLockUI();

            addReviewList(placeReviews);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            PlaceReviewActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            PlaceReviewActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            PlaceReviewActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            PlaceReviewActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            PlaceReviewActivity.this.onErrorResponse(call, response);
        }
    };
}
