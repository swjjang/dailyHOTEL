package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayInterface;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.repository.remote.model.TrueReviewsData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class StayRemoteImpl implements StayInterface
{
    private Context mContext;

    public StayRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<TrueReviews> getStayTrueReviews(int stayIndex, int page, int limit)
    {
        return DailyMobileAPI.getInstance(mContext).getStayTrueReviews(stayIndex, page, limit).map(new Function<BaseDto<TrueReviewsData>, TrueReviews>()
        {
            @Override
            public TrueReviews apply(@io.reactivex.annotations.NonNull BaseDto<TrueReviewsData> trueReviewsDataBaseDto) throws Exception
            {
                TrueReviews trueReviews;

                if (trueReviewsDataBaseDto != null)
                {
                    if (trueReviewsDataBaseDto.msgCode == 100 && trueReviewsDataBaseDto.data != null)
                    {
                        trueReviews = trueReviewsDataBaseDto.data.getTrueReviews();
                    } else
                    {
                        throw new BaseException(trueReviewsDataBaseDto.msgCode, trueReviewsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return trueReviews;
            }
        });
    }
}
