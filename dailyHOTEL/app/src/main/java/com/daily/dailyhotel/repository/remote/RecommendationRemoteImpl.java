package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.RecommendationInterface;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.dto.BaseListDto;
import com.twoheart.dailyhotel.network.factory.TagCancellableCallAdapterFactory;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.RecommendationGourmet;
import com.twoheart.dailyhotel.network.model.RecommendationPlaceList;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RecommendationRemoteImpl extends BaseRemoteImpl implements RecommendationInterface
{
    @Override
    public Observable<List<Recommendation>> getRecommendationList(Context context)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendations"//
            : "MTckNjMkNDIkMjIkNDIkNDEkODckMzEkMjEkNTIkMjUkNTckNzEkMjkkNjIkNzYk$RjFGRUIyQzFDQjhBNBTAxANCDPI2NCkEwRMTg0NDMyNDhUDUNXDE3MHzE2BMjZJDMkMwQzIyRGDOVQxNUVDQjc5NDFDQzJFQXkEwNw==$";

        return mDailyMobileService.getRecommendationList(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseListDto<Recommendation>, List<Recommendation>>()
            {
                @Override
                public List<Recommendation> apply(BaseListDto<Recommendation> recommendationBaseListDto) throws Exception
                {
                    List<Recommendation> recommendationList = new ArrayList<>();

                    if (recommendationBaseListDto != null)
                    {
                        if (recommendationBaseListDto.msgCode == 100 && recommendationBaseListDto.data != null)
                        {
                            recommendationList = recommendationBaseListDto.data;
                        } else
                        {
                            throw new BaseException(recommendationBaseListDto.msgCode, recommendationBaseListDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return recommendationList;
                }
            });
    }

    @Override
    public Observable<RecommendationPlaceList<RecommendationStay>> getRecommendationStayList(int index, String salesDate, int period)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendation/{idx}"//
            : "MjgkODMkNzEkMzYkNDMkOTEkNCQyMCQxMzEkOCQxMDAkNjgkNjkkMzMkNjkkNTIk$QzczANjBIBODhDRkMxQkMJxNUFBRUFGOMHjZCMTFRDQTg5NPzY5NXTdFRDkyOTI5Njg4Q0OIAFBQkMyRDRJFQTFCOTAxRDGMxRDNE4RDkWxQzdFQTYxODM3RTIwNzFEN0Y3RDgyNEIB3RDEw$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        return mDailyMobileService.getRecommendationStayList(Crypto.getUrlDecoderEx(URL, urlParams), salesDate, period) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RecommendationPlaceList<RecommendationStay>>, RecommendationPlaceList<RecommendationStay>>()
            {
                @Override
                public RecommendationPlaceList<RecommendationStay> apply(BaseDto<RecommendationPlaceList<RecommendationStay>> recommendationPlaceListBaseDto) throws Exception
                {
                    RecommendationPlaceList<RecommendationStay> recommendationPlaceList = new RecommendationPlaceList<RecommendationStay>();

                    if (recommendationPlaceListBaseDto != null)
                    {
                        switch (recommendationPlaceListBaseDto.msgCode)
                        {
                            case 100:
                                recommendationPlaceList = recommendationPlaceListBaseDto.data;
                                break;

                            // 인트라넷에서 숨김처리가 된경우
                            case 801:
                                throw new BaseException(recommendationPlaceListBaseDto.msgCode, recommendationPlaceListBaseDto.msg);

                            default:
                                throw new BaseException(recommendationPlaceListBaseDto.msgCode, recommendationPlaceListBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return recommendationPlaceList;
                }
            });
    }

    @Override
    public Observable<RecommendationPlaceList<RecommendationGourmet>> getRecommendationGourmetList(int index, String salesDate, int period)
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v4/home/recommendation/{idx}"//
            : "MjgkODMkNzEkMzYkNDMkOTEkNCQyMCQxMzEkOCQxMDAkNjgkNjkkMzMkNjkkNTIk$QzczANjBIBODhDRkMxQkMJxNUFBRUFGOMHjZCMTFRDQTg5NPzY5NXTdFRDkyOTI5Njg4Q0OIAFBQkMyRDRJFQTFCOTAxRDGMxRDNE4RDkWxQzdFQTYxODM3RTIwNzFEN0Y3RDgyNEIB3RDEw$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{idx}", Integer.toString(index));

        TagCancellableCallAdapterFactory.ExecutorCallbackCall executorCallbackCall = (TagCancellableCallAdapterFactory.ExecutorCallbackCall) mDailyMobileService.requestRecommendationGourmetList(Crypto.getUrlDecoderEx(URL, urlParams), salesDate, period);
        return mDailyMobileService.getRecommendationGourmetList(Crypto.getUrlDecoderEx(URL, urlParams), salesDate, period) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RecommendationPlaceList<RecommendationGourmet>>, RecommendationPlaceList<RecommendationGourmet>>()
            {
                @Override
                public RecommendationPlaceList<RecommendationGourmet> apply(BaseDto<RecommendationPlaceList<RecommendationGourmet>> recommendationPlaceListBaseDto) throws Exception
                {
                    RecommendationPlaceList<RecommendationGourmet> recommendationPlaceList = new RecommendationPlaceList<RecommendationGourmet>();

                    if (recommendationPlaceListBaseDto != null)
                    {
                        switch (recommendationPlaceListBaseDto.msgCode)
                        {
                            case 100:
                                recommendationPlaceList = recommendationPlaceListBaseDto.data;
                                break;

                            // 인트라넷에서 숨김처리가 된경우
                            case 801:
                                throw new BaseException(recommendationPlaceListBaseDto.msgCode, recommendationPlaceListBaseDto.msg);

                            default:
                                throw new BaseException(recommendationPlaceListBaseDto.msgCode, recommendationPlaceListBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return recommendationPlaceList;
                }
            });
    }
}
