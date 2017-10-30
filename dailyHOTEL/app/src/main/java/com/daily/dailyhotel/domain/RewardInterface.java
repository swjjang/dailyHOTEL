package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistoryDetail;
import com.daily.dailyhotel.entity.RewardInformation;

import io.reactivex.Observable;

public interface RewardInterface
{
    Observable<RewardInformation> getRewardStickerCount();

    Observable<RewardDetail> getRewardDetail();

    Observable<RewardHistoryDetail> getRewardHistoryDetail();
}
