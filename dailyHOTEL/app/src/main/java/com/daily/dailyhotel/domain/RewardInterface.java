package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.RecentlyPlace;
import com.daily.dailyhotel.entity.RewardHistory;
import com.daily.dailyhotel.entity.StayOutbounds;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public interface RewardInterface
{
    Observable<RewardHistory> getRewardHistoryList();
}
