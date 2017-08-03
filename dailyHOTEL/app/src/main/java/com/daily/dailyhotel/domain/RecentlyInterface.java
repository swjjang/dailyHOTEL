package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.StayOutbounds;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.HomePlace;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 6. 14..
 */

public interface RecentlyInterface
{
    Observable<StayOutbounds> getStayOutboundRecentlyList(int numberOfResults, boolean useRealm);

    Observable<ArrayList<HomePlace>> getHomeRecentlyList(int maxSize);

    Observable<List<Stay>> getStayInboundRecentlyList(StayBookingDay stayBookingDay, boolean useRealm);

    Observable<List<Gourmet>> getGourmetRecentlyList(GourmetBookingDay gourmetBookingDay, boolean useRealm);
}
