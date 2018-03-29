package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ReviewInterface;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class ReviewRemoteImpl extends BaseRemoteImpl implements ReviewInterface
{
    public ReviewRemoteImpl(Context context)
    {
        super(context);
    }

    @Override
    public Observable<Review> getStayReview(int reservationIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{reserveIdx}/question"//
            : "MjckOTQkNDMkNTQkNjUkNjUkMTMxJDk3JDQzJDEwOCQ4NiQxMjgkODIkNzkkMjEkMTEk$RkM5NTRBODRMBMTU1NDIxQYURBMjNZBNkMzMzQ2NEJGQkEQZBMEMzQTY5CMjczOERCMTXLlCRDE0NUM0QWjFGXNTVCEMDI5OEI3MDY0IQQzAwNTkwFMEJBQjdBRjlDRUU4OTYA0NUFFONEY1$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getReview(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) -> {
                Review review;

                if (reviewDataBaseDto != null)
                {
                    if (reviewDataBaseDto.msgCode == 100 && reviewDataBaseDto.data != null)
                    {
                        review = reviewDataBaseDto.data.getReview();
                    } else
                    {
                        throw new BaseException(reviewDataBaseDto.msgCode, reviewDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return review;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Review> getGourmetReview(int reservationIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/gourmet/{reserveIdx}/question"//
            : "MTAwJDkwJDY0JDEyMyQxJDU0JDckMTQkMTEkNzUkMzEkNDQkNDAkMjQkMTMzJDM5JA==$NLzY4RDJkzNAkJDAMDYzNEI4JMTI3QUVOGNzA5RTjMKwQTUI2RTJBMzBCRjc1RGjY2RTczN0VDLNTc1QWzU4RUZGQUM4NDRCMkQzOTJM2MTlDQ0VEAMjdFMEJENDEwMUREMjM1NZMTczOUJG$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getReview(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) -> {
                Review review;

                if (reviewDataBaseDto != null)
                {
                    if (reviewDataBaseDto.msgCode == 100 && reviewDataBaseDto.data != null)
                    {
                        review = reviewDataBaseDto.data.getReview();
                    } else
                    {
                        throw new BaseException(reviewDataBaseDto.msgCode, reviewDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return review;
            }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Review> getStayOutboundReview(int reservationIndex)
    {
        final String URL = Constants.DEBUG ? DailyPreference.getInstance(mContext).getBaseOutBoundUrl() : Setting.getOutboundServerUrl();

        final String API = Constants.UNENCRYPTED_URL ? "api/v1/outbound/reservations/{reserveIdx}/reviewable-question"//
            : "NzYkMCQ4MiQxMCQyMCQ0MiQ0MiQ1OCQxMjkkODIkNjEkMTI1JDE2NiQxMDckMjIkMTM4JA==$XN0RBNENCMMDZEQTkwMjFRHFMzQ5QUFDMzdEOTNGRjYBE4ODZFMjhBQkE2MQEYK3RUVBMDdFQTM2QTQ1MjM1RTMzU5IOUU4RjlFNjYzQ0VGRDDVGODA4RTFFOEVBREZBCMjZFNRTg4GNTdERkY3QUYxMTRCQTk0RTdBQjMxQjUE0MzBCQ0E2MkU3NjU=$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getStayOutboundReview(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) -> {
                Review review;

                if (reviewDataBaseDto != null)
                {
                    if (reviewDataBaseDto.msgCode == 100 && reviewDataBaseDto.data != null)
                    {
                        review = reviewDataBaseDto.data.getReview();
                    } else
                    {
                        throw new BaseException(reviewDataBaseDto.msgCode, reviewDataBaseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return review;
            }).observeOn(AndroidSchedulers.mainThread());
    }
}
