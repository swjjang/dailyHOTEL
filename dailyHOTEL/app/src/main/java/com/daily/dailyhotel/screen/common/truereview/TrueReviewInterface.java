package com.daily.dailyhotel.screen.common.truereview;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.TrueReview;

import java.util.List;

import io.reactivex.Observable;

public interface TrueReviewInterface extends BaseDialogViewInterface
{
    void setReviewScores(String title, List<ReviewScore> reviewScoreList);

    void showReviewScoresAnimation();

    void addLastFooter();

    void addLoadingFooter();

    void removeLoadingFooter();

    void addReviewList(List<TrueReview> trueReviewList, int totalCount);

    Observable<Boolean> smoothScrollTop();

    void setTopButtonVisible(boolean visible);

    void setTrueReviewProductVisible(boolean visible);
}
