package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.ReviewInterface;
import com.daily.dailyhotel.domain.RewardInterface;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.RewardCardHistoryDetail;
import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistoryDetail;
import com.daily.dailyhotel.entity.RewardInformation;
import com.daily.dailyhotel.repository.remote.model.RewardCardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardInformationData;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
            : "NjEkMTI2JDE2JDkyJDgzJDEyNyQ2MCQxMTQkNzMkOTkkMzEkMTkkMTEzJDY2JDI2JDgzJA==$OThEOEU5OTFDOUExRNTJA0NEYyJNkE4MkZMzQkRFRTcwNEQ3MEUyNTM4MjhFRkFOCNAXkYwMzM3OUGYxRDcGxQURDQSkZEMTA4OUXM4QQjRGOUUyMjc5MRDREMVENDMjQ1NEFFMTJCCRMkQx$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getReview(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) ->
            {
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
            : "NjEkMTI2JDE2JDkyJDgzJDEyNyQ2MCQxMTQkNzMkOTkkMzEkMTkkMTEzJDY2JDI2JDgzJA==$OThEOEU5OTFDOUExRNTJA0NEYyJNkE4MkZMzQkRFRTcwNEQ3MEUyNTM4MjhFRkFOCNAXkYwMzM3OUGYxRDcGxQURDQSkZEMTA4OUXM4QQjRGOUUyMjc5MRDREMVENDMjQ1NEFFMTJCCRMkQx$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{reserveIdx}", Integer.toString(reservationIndex));

        return mDailyMobileService.getReview(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) ->
            {
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

        return mDailyMobileService.getReview(Crypto.getUrlDecoderEx(URL) + Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map((reviewDataBaseDto) ->
            {
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
