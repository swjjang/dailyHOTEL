package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.RewardDetail;
import com.daily.dailyhotel.entity.RewardHistory;
import com.daily.dailyhotel.entity.RewardHistoryDetail;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public interface RewardInterface
{
    Observable<Integer> getRewardStickerCount();

    Observable<RewardDetail> getRewardDetail();

    Observable<RewardHistoryDetail> getRewardHistoryDetail();
}
