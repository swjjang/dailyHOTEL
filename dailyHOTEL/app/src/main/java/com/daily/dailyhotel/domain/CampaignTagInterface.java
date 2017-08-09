package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.entity.StayCampaignTags;
import com.daily.dailyhotel.repository.remote.model.GourmetCampaignTagsData;
import com.daily.dailyhotel.repository.remote.model.StayCampaignTagsData;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by iseung-won on 2017. 8. 8..
 */

public interface CampaignTagInterface
{
    Observable<ArrayList<CampaignTag>> getCampaignTagList(String serviceType);

    Observable<StayCampaignTags> getStayCampaignTags(int index, StayBookingDay stayBookingDay);

    Observable<GourmetCampaignTags> getGourmetCampaignTags(int index, GourmetBookingDay gourmetBookingDay);
}
