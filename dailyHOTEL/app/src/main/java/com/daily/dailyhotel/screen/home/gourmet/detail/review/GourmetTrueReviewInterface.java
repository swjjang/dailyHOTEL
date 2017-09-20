package com.daily.dailyhotel.screen.home.gourmet.detail.review;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.TrueReview;

import java.util.List;

import io.reactivex.Observable;

public interface GourmetTrueReviewInterface extends BaseDialogViewInterface
{
    void setReviewScores(List<ReviewScore> reviewScoreList);

    void showReviewScoresAnimation();

    void addLastFooter();

    void addLoadingFooter();

    void removeLoadingFooter();

    void addReviewList(List<TrueReview> trueReviewList, int totalCount);

    Observable<Boolean> smoothScrollTop();

    void setTopButtonVisible(boolean visible);
}
