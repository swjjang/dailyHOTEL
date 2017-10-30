package com.daily.dailyhotel.repository.remote;

import android.content.Context;

import com.daily.base.exception.BaseException;
import com.daily.dailyhotel.domain.RewardInterface;
import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistoryDetail;
import com.daily.dailyhotel.entity.RewardInformation;
import com.daily.dailyhotel.repository.remote.model.RewardDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardHistoryDetailData;
import com.daily.dailyhotel.repository.remote.model.RewardInformationData;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by android_sam on 2017. 9. 28..
 */

public class RewardRemoteImpl implements RewardInterface
{
    private Context mContext;

    public RewardRemoteImpl(Context context)
    {
        mContext = context;
    }

    @Override
    public Observable<RewardInformation> getRewardStickerCount()
    {
        return DailyMobileAPI.getInstance(mContext).getRewardStickerCount().map(new Function<BaseDto<RewardInformationData>, RewardInformation>()
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
        return DailyMobileAPI.getInstance(mContext).getRewardDetail().map(new Function<BaseDto<RewardDetailData>, RewardDetail>()
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
        return DailyMobileAPI.getInstance(mContext).getRewardHistoryDetail().map(new Function<BaseDto<RewardHistoryDetailData>, RewardHistoryDetail>()
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
}
