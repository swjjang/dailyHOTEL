package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.GourmetInterface;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.ExistCouponsData;
import com.daily.dailyhotel.repository.remote.model.GourmetDetailData;
import com.daily.dailyhotel.repository.remote.model.GourmetListData;
import com.daily.dailyhotel.repository.remote.model.ReviewScoresData;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetParams;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class GourmetRemoteImpl implements GourmetInterface
{
    private Context mContext;

    public GourmetRemoteImpl(@NonNull Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<List<Gourmet>> getGourmetList(GourmetParams gourmetParams)
    {
        return DailyMobileAPI.getInstance(mContext) //
            .getGourmetList(gourmetParams.toParamsMap(), gourmetParams.getCategoryList(), gourmetParams.getTimeList(), gourmetParams.getLuxuryList()) //
            .map(new Function<BaseDto<GourmetListData>, List<Gourmet>>()
            {
                @Override
                public List<Gourmet> apply(@NonNull BaseDto<GourmetListData> gourmetListDataBaseDto) throws Exception
                {
                    List<Gourmet> gourmetList = new ArrayList<>();

                    if (gourmetListDataBaseDto != null)
                    {
                        if (gourmetListDataBaseDto.msgCode == 100 && gourmetListDataBaseDto.data != null)
                        {
                            gourmetList.addAll(gourmetListDataBaseDto.data.getGourmetList(mContext));
                        } else
                        {
                            throw new BaseException(gourmetListDataBaseDto.msgCode, gourmetListDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return gourmetList;
                }
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GourmetDetail> getGourmetDetail(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetDetail(gourmetIndex, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd")).map(new Function<BaseDto<GourmetDetailData>, GourmetDetail>()
        {
            @Override
            public GourmetDetail apply(@io.reactivex.annotations.NonNull BaseDto<GourmetDetailData> gourmetDetailDataBaseDto) throws Exception
            {
                GourmetDetail gourmetDetail = null;

                if (gourmetDetailDataBaseDto != null)
                {
                    if (gourmetDetailDataBaseDto.data != null)
                    {
                        // 100	성공
                        // 4	데이터가 없을시
                        // 5	판매 마감시
                        switch (gourmetDetailDataBaseDto.msgCode)
                        {
                            case 5:
                                gourmetDetail = gourmetDetailDataBaseDto.data.getGourmetDetail();
                                gourmetDetail.setGourmetMenuList(null);
                                break;

                            case 100:
                                gourmetDetail = gourmetDetailDataBaseDto.data.getGourmetDetail();
                                break;

                            default:
                                throw new BaseException(gourmetDetailDataBaseDto.msgCode, gourmetDetailDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(gourmetDetailDataBaseDto.msgCode, gourmetDetailDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return gourmetDetail;
            }
        });
    }

    @Override
    public Observable<Boolean> getGourmetHasCoupon(int gourmetIndex, GourmetBookDateTime gourmetBookDateTime)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetHasCoupon(gourmetIndex, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd")).map(new Function<BaseDto<ExistCouponsData>, Boolean>()
        {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull BaseDto<ExistCouponsData> existCouponsDataBaseDto) throws Exception
            {
                boolean hasCoupon = false;

                if (existCouponsDataBaseDto != null)
                {
                    if (existCouponsDataBaseDto.msgCode == 100 && existCouponsDataBaseDto.data != null)
                    {
                        hasCoupon = existCouponsDataBaseDto.data.existCoupons;
                    } else
                    {
                        throw new BaseException(existCouponsDataBaseDto.msgCode, existCouponsDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return hasCoupon;
            }
        });
    }

    @Override
    public Observable<WishResult> addGourmetWish(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).addGourmetWish(gourmetIndex).map(new Function<BaseDto<String>, WishResult>()
        {
            @Override
            public WishResult apply(@io.reactivex.annotations.NonNull BaseDto<String> baseDto) throws Exception
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
            }
        });
    }

    @Override
    public Observable<WishResult> removeGourmetWish(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).removeGourmetWish(gourmetIndex).map(new Function<BaseDto<String>, WishResult>()
        {
            @Override
            public WishResult apply(@io.reactivex.annotations.NonNull BaseDto<String> baseDto) throws Exception
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
            }
        });
    }

    @Override
    public Observable<ReviewScores> getGourmetReviewScores(int gourmetIndex)
    {
        return DailyMobileAPI.getInstance(mContext).getGourmetReviewScores(gourmetIndex).map(new Function<BaseDto<ReviewScoresData>, ReviewScores>()
        {
            @Override
            public ReviewScores apply(@io.reactivex.annotations.NonNull BaseDto<ReviewScoresData> reviewScoresDataBaseDto) throws Exception
            {
                ReviewScores reviewScores = null;

                if (reviewScoresDataBaseDto != null)
                {
                    if (reviewScoresDataBaseDto.msgCode == 100 && reviewScoresDataBaseDto.data != null)
                    {
                        reviewScores = reviewScoresDataBaseDto.data.getReviewScores();
                    } else
                    {
                        throw new BaseException(reviewScoresDataBaseDto.msgCode, reviewScoresDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return reviewScores;
            }
        });
    }
}
