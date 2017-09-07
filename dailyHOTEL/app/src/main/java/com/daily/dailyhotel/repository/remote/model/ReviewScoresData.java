package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.ReviewScores;

import java.util.ArrayList;
import java.util.List;

@JsonObject
public class ReviewScoresData
{
    @JsonField(name = "reviewScoreAvgs")
    public List<ReviewScoreData> reviewScoreAvgs;

    @JsonField(name = "reviewScoreTotalCount")
    public int reviewScoreTotalCount;

    public ReviewScoresData()
    {

    }

    public ReviewScores getReviewScores()
    {
        ReviewScores reviewScores = new ReviewScores();

        reviewScores.reviewScoreTotalCount = reviewScoreTotalCount;

        List<ReviewScore> reviewScoreList = new ArrayList<>();

        if (reviewScoreAvgs != null && reviewScoreAvgs.size() > 0)
        {
            for (ReviewScoreData reviewScoreData : reviewScoreAvgs)
            {
                reviewScoreList.add(reviewScoreData.getReviewScore());
            }
        }

        reviewScores.setReviewScoreList(reviewScoreList);

        return reviewScores;
    }

    @JsonObject
    static class ReviewScoreData
    {
        @JsonField(name = "type")
        public String type;

        @JsonField(name = "scoreAvg")
        public float scoreAvg;

        public ReviewScore getReviewScore()
        {
            ReviewScore reviewScore = new ReviewScore();
            reviewScore.type = type;
            reviewScore.scoreAverage = scoreAvg;

            return reviewScore;
        }
    }
}
