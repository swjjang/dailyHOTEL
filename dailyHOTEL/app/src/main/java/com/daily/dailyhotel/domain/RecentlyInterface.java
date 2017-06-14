package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutbounds;
import com.twoheart.dailyhotel.network.model.HomePlace;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public interface RecentlyInterface
{
    Observable<StayOutbounds> getStayOutboundRecentlyList(int numberOfResults);

    Observable<List<HomePlace>> getHomeRecentlyList(int maxSize);
}
