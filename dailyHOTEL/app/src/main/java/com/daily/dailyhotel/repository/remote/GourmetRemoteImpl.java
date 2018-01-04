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
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GourmetRemoteImpl extends BaseRemoteImpl implements GourmetInterface
{
    public GourmetRemoteImpl(@NonNull Context context)
    {
        super(context);
    }

    @Override
    public Observable<List<Gourmet>> getList(GourmetParams gourmetParams)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/sales"//
            : "NjYkNjMkMzEkNzYkMzckODEkODUkNCQ2NyQ5NiQ2MSQxMyQ0MSQ0MCQ5MSQ2MCQ=$N0M0VNTRCQUIxYMDIzRDdEQTJBODI3QjZFCOEE4NEQVTdBMUVDOUM3QzlDOTRg1MzLBERDYEOzRTKNBQzk2QYUFBIMDAMGwRjNBQw=U=$";

        return mDailyMobileService.getGourmetList(Crypto.getUrlDecoderEx(URL), gourmetParams.toParamsMap() //
            , gourmetParams.getCategoryList(), gourmetParams.getTimeList(), gourmetParams.getLuxuryList()) //
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}"//
            : "MTckNjMkNTQkNyQ2NSQ1MyQyNSQyMiQ0JDk0JDYxJDM1JDgkNjckMzckMTAxJA==$QkJCSMjBPVENkQ3RTU4MTjkyDOTQVyODZBQjSZFBNTMwMDI3MDQ5N0RDMDYGxRFDFg4NZEI4NTDXM2OUNGRUQ0QTY4N0MyNjVEMRgJ==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getGourmetDetail(Crypto.getUrlDecoderEx(API, urlParams), gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"))//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/gourmet/{restaurantIdx}/coupons/exist"//
            : "NzEkNTUkMTAxJDc4JDExNyQ1MSQxOSQzNyQ0NiQyJDEzMCQxMDgkMzEkMTIwJDc5JDE5JA==$MTMExNTdFQkU1QjUxNUUQB0MzQ3QjkxRVTJEOEQ0TNzAzOUFDCNTg5NTlBBNUJZCODY0RjY0NkM1MTQNSwNzMxUNDI3MDU2MjE1NDcwRERCNDYTCxMkNBQzM3RCDY4OKDU4QjUxVNDQ2MkIz$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getGourmetHasCoupon(Crypto.getUrlDecoderEx(API, urlParams), gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"))//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/gourmet/add/{restaurantIdx}"//
            : "NyQxJDYxJDExMiQ4MyQxMDAkMjckNSQxMzUkMzgkMzUkMTA2JDQ5JDEwMSQxMzMkNDYk$NNTE1EN0UP2M0YzNjA5NjNCQUY1MOjk2RTRGFMkWJCMzgwCQUNGFMTNGRUNEMDlDRjALwRUU1NTQwQkNFNUQxNDU0NNkYyQTI5RjZCJQkJFMQEkE1REZGOTgxMODE4QkI4QkZFERERDNjBWE$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.addWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/gourmet/remove/{restaurantIdx}"//
            : "ODAkMTkkNTUkNDkkODMkNjckMTEkOTMkMTEkMTUkNzgkMyQ5OCQxMDIkNDIkMTAyJA==$MEYYwQTUzNzBSTENOjkyMEUQxNDhFNDRCMkJCMDc2QNjVFNDAyQ0MyXRTk3MDJk3QUM5REU2NQzEwMDcF0QTQyNkZEHDRDQ2RTRQkzCRNDcxRTJGQjlBQUZGREQyMUQzMjE1RTk0RDE1MUM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.removeWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/{restaurantIdx}/statistic"//
            : "NDQkMTI0JDY0JDY5JDExJDExNCQzNyQyOSQxNyQ2MiQ5OSQ3NyQxMjYkMjAkMTckMTYk$NTA0NEJBRTkAzN0ILzBLMjDgxMTgyNkI4UQUM5QkQxYMzY1OTQ2EN0NERDUxMDBFQYjM5REVQGMjEKyNJTUyQTEwMDYwOEMxOUI5MzgB0MDdCOEJEMDFFMDY4MEAY5NjhZGMjU5MUYJyMEFD$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getReviewScores(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/{restaurantIdx}"//
            : "ODQkOTckMTE0JDQkMTMwJDQ0JDU2JDkyJDczJDcwJDEyOSQxMDUkNTAkMTQkODAkMTgk$NzcyXQkYxOERFRNkUzLOEVFN0JGRkMxMkJCNDhGQ0E2RTgWxRDYxUOTA5Q0QZBMkZENDU3NDYDwQkDMxMB0E5NkY0REE4JQjk1CM0RDN0VBZOXENEQTA0NUYwMzBEOREFBOTgyPRDVEMDXMz$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{restaurantIdx}", Integer.toString(gourmetIndex));

        return mDailyMobileService.getTrueReviews(Crypto.getUrlDecoderEx(API, urlParams), page, limit, "createdAt", "DESC")//
            .subscribeOn(Schedulers.io()).map(baseDto ->
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
