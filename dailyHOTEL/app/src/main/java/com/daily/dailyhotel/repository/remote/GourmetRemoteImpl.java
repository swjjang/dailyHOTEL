package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.GourmetInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.WishResult;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GourmetRemoteImpl implements GourmetInterface
{
    Context mContext;

    public GourmetRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Gourmet>> getList(GourmetParams gourmetParams)
    {
        return DailyMobileAPI.getInstance(mContext) //
            .getGourmetList(gourmetParams.toParamsMap(), gourmetParams.getCategoryList(), gourmetParams.getTimeList(), gourmetParams.getLuxuryList()).map(baseDto ->
            {
                List<Gourmet> gourmetList = new ArrayList<>();

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        gourmetList.addAll(baseDto.data.getGourmetList(mContext));
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetList;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GourmetDetail> getDetail(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetDetail(gourmetIndex, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd")).map(baseDto ->
        {
            GourmetDetail gourmetDetail;

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
                            gourmetDetail = baseDto.data.getGourmetDetail();
                            gourmetDetail.setGourmetMenuList(null);
                            break;

                        case 100:
                            gourmetDetail = baseDto.data.getGourmetDetail();
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

            return gourmetDetail;
        });
    }

    @Override
    public Observable<Boolean> getHasCoupon(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetHasCoupon(gourmetIndex, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd")).map(baseDto ->
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
        return DailyMobileAPI.getInstance(mContext).addGourmetWish(gourmetIndex).map(baseDto ->
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
        return DailyMobileAPI.getInstance(mContext).removeGourmetWish(gourmetIndex).map(baseDto ->
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
        return DailyMobileAPI.getInstance(mContext).getGourmetReviewScores(gourmetIndex).map(baseDto ->
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
    public Observable<TrueReviews> getTrueReviews(int gourmetIndex, int page, int limit)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetTrueReviews(gourmetIndex, page, limit).map(baseDto ->
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
}
