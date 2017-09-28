package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.StayInterface;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.TrueVRData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class StayRemoteImpl implements StayInterface
{
    private Context mContext;

    public StayRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<StayDetail> getDetail(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getStayDetail(stayIndex, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getNights()).map(baseDto ->
        {
            StayDetail stayDetail;

            if (baseDto != null)
            {
                if (baseDto.data != null)
                {
                    // 100	성공
                    // 4	데이터가 없을시
                    // 5	판매 마감시
                    switch (baseDto.msgCode)
                    {
                        case 5:
                            stayDetail = baseDto.data.getStayDetail();
                            stayDetail.setRoomList(null);
                            break;

                        case 100:
                            stayDetail = baseDto.data.getStayDetail();
                            break;

                        default:
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(baseDto.msgCode, baseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return stayDetail;
        });
    }

    @Override
    public Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getStayHasCoupon(stayIndex, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")//
            , stayBookDateTime.getNights()).map(baseDto ->
        {
            boolean hasCoupon = false;

            if (baseDto != null)
            {
                if (baseDto.msgCode == 100 && baseDto.data != null)
                {
                    hasCoupon = baseDto.data.existCoupons;
                }
            }

            return hasCoupon;
        });
    }

    @Override
    public Observable<WishResult> addWish(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).addStayWish(gourmetIndex).map(baseDto ->
        {
            WishResult wishResult = new WishResult();

            if (baseDto != null)
            {
                wishResult.success = baseDto.msgCode == 100;
                wishResult.message = baseDto.msg;
            } else
            {
                throw new BaseException(-1, null);
            }

            return wishResult;
        });
    }

    @Override
    public Observable<WishResult> removeWish(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).removeStayWish(gourmetIndex).map(baseDto ->
        {
            WishResult wishResult = new WishResult();

            if (baseDto != null)
            {
                wishResult.success = baseDto.msgCode == 100;
                wishResult.message = baseDto.msg;
            } else
            {
                throw new BaseException(-1, null);
            }

            return wishResult;
        });
    }

    @Override
    public Observable<ReviewScores> getReviewScores(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayReviewScores(gourmetIndex).map(baseDto ->
        {
            ReviewScores reviewScores;

            if (baseDto != null)
            {
                if (baseDto.msgCode == 100 && baseDto.data != null)
                {
                    reviewScores = baseDto.data.getReviewScores();
                } else
                {
                    reviewScores = new ReviewScores();
                }
            } else
            {
                reviewScores = new ReviewScores();
            }

            return reviewScores;
        });
    }

    @Override
    public Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit)
    {
        return DailyMobileAPI.getInstance(mContext).getStayTrueReviews(stayIndex, page, limit).map(baseDto ->
        {
            TrueReviews trueReviews;

            if (baseDto != null)
            {
                if (baseDto.msgCode == 100 && baseDto.data != null)
                {
                    trueReviews = baseDto.data.getTrueReviews();
                } else
                {
                    throw new BaseException(baseDto.msgCode, baseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return trueReviews;
        });
    }

    @Override
    public Observable<List<TrueVR>> getTrueVR(int stayIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getStayTrueVRList(stayIndex).map(baseDto ->
        {
            List<TrueVR> trueVR = new ArrayList<>();

            if (baseDto != null)
            {
                if (baseDto.msgCode == 100 && baseDto.data != null)
                {
                    for(TrueVRData trueVRData : baseDto.data)
                    {
                        trueVR.add(trueVRData.getTrueVR());
                    }
                } else
                {
                    throw new BaseException(baseDto.msgCode, baseDto.msg);
                }
            } else
            {
                throw new BaseException(-1, null);
            }

            return trueVR;
        });
    }
}
