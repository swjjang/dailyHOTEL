package com.daily.dailyhotel.repository.remote;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.RewardInterface;
import com.daily.dailyhotel.entity.RewardCardHistoryDetail;
import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistoryDetail;
import com.daily.dailyhotel.entity.RewardInformation;
import com.daily.dailyhotel.repository.remote.model.RewardCardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardInformationData;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class RewardRemoteImpl extends BaseRemoteImpl implements RewardInterface
{
    @Override
    public Observable<RewardInformation> getRewardStickerCount()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/reward"//
            : "MjUkMzYkNiQzMSQ0MCQzMSQyOCQyMyQxMSQzNyQ0OCQ0NiQyNCQ0NCQyMiQ1JA==$NDI5NLEYFDRkHM5ODY1MDEyYQkBGE3RBjLREOKKDANCQjJKCEUGNkGIzNTE=$";

        return mDailyMobileService.getRewardStickerCount(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RewardInformationData>, RewardInformation>()
            {
                @Override
                public RewardInformation apply(@io.reactivex.annotations.NonNull BaseDto<RewardInformationData> rewardCountDataBaseDto) throws Exception
                {
                    RewardInformation rewardInformation;

                    if (rewardCountDataBaseDto != null)
                    {
                        if (rewardCountDataBaseDto.msgCode == 100 && rewardCountDataBaseDto.data != null)
                        {
                            rewardInformation = rewardCountDataBaseDto.data.getRewardInformation();
                        } else
                        {
                            throw new BaseException(rewardCountDataBaseDto.msgCode, rewardCountDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return rewardInformation;
                }
            });
    }

    @Override
    public Observable<RewardDetail> getRewardDetail()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/reward/detail"//
            : "MzMkNDkkMTMkODUkNzckMjckNjMkNDMkNDMkNTUkMzUkNjYkODgkOTEkMTIkMjkk$NDdBRDVBQUM3HQIzk1Q0FEMzY3ODPXM1RTU4RATEQ5NzJBOWRTZDOTVBSMYDUwQTFDNkMEUzRjNCMDE1NkM5MkFY2MVEYV5RTXE0Rg==$";

        return mDailyMobileService.getRewardDetail(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RewardDetailData>, RewardDetail>()
            {
                @Override
                public RewardDetail apply(@io.reactivex.annotations.NonNull BaseDto<RewardDetailData> rewardDetailDataBaseDto) throws Exception
                {
                    RewardDetail rewardDetail;

                    if (rewardDetailDataBaseDto != null)
                    {
                        if (rewardDetailDataBaseDto.msgCode == 100 && rewardDetailDataBaseDto.data != null)
                        {
                            rewardDetail = rewardDetailDataBaseDto.data.getRewardDetail();
                        } else
                        {
                            throw new BaseException(rewardDetailDataBaseDto.msgCode, rewardDetailDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return rewardDetail;
                }
            });
    }

    @Override
    public Observable<RewardHistoryDetail> getRewardHistoryDetail()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/reward/history"//
            : "NjYkNTgkNzUkNzIkMzckODkkNDAkNDQkNDQkOTQkODkkNzEkNzAkNzYkNDckNDUk$RkFEMDdCN0IyODA3Nzk4MTRDODA3QjVDRDU5RMTUI2OEPGYEP3NEYzNkM2NzUzRkHFGQTE4MWkYOFDJOTEYxMJDI0NDlDOKEJGGMZQ==$";

        return mDailyMobileService.getRewardHistoryDetail(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RewardHistoryDetailData>, RewardHistoryDetail>()
            {
                @Override
                public RewardHistoryDetail apply(@io.reactivex.annotations.NonNull BaseDto<RewardHistoryDetailData> rewardHistoryDataBaseDto) throws Exception
                {
                    RewardHistoryDetail rewardHistoryDetail;

                    if (rewardHistoryDataBaseDto != null)
                    {
                        if (rewardHistoryDataBaseDto.msgCode == 100 && rewardHistoryDataBaseDto.data != null)
                        {
                            rewardHistoryDetail = rewardHistoryDataBaseDto.data.getRewardHistoryDetail();
                        } else
                        {
                            throw new BaseException(rewardHistoryDataBaseDto.msgCode, rewardHistoryDataBaseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return rewardHistoryDetail;
                }
            });
    }

    @Override
    public Observable<RewardCardHistoryDetail> getRewardCardHistoryDetail()
    {
        final String URL = Constants.UNENCRYPTED_URL ? "api/v5/reward/card/history"//
            : "NDQkMTEkMzAkNDQkNDUkODAkNjgkNiQ1MyQyMyQ4NCQzJDg5JDMwJDI3JDc3JA==$RUIC1QTJIzMjQDxQUEyOThBQXzkYzQTGEwNTjc5QUIxNTlGQTGYI0QQzMFxMjRDNTNFQUZCN0NKCNOEExREEyQjAKH3NHkRGQkMxOQ==$";

        return mDailyMobileService.getRewardCardHistoryDetail(Crypto.getUrlDecoderEx(URL)) //
            .subscribeOn(Schedulers.io()).map(new Function<BaseDto<RewardCardHistoryDetailData>, RewardCardHistoryDetail>()
            {
                @Override
                public RewardCardHistoryDetail apply(BaseDto<RewardCardHistoryDetailData> baseDto) throws Exception
                {
                    RewardCardHistoryDetail rewardCardHistoryDetail;

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            rewardCardHistoryDetail = baseDto.data.getRewardCardHistoryDetail();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return rewardCardHistoryDetail;
                }
            });
    }
}
