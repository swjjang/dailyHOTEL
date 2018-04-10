package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.GourmetCampaignTags;
import com.daily.dailyhotel.entity.StayCampaignTags;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 8. 8..
 */

public interface CampaignTagInterface
{
    Observable<ArrayList<CampaignTag>> getCampaignTagList(String serviceType);

    Observable<StayCampaignTags> getStayCampaignTags(int index, String checkInDate, int nights);

    Observable<GourmetCampaignTags> getGourmetCampaignTags(Context context, int index, String visitDate);
}
