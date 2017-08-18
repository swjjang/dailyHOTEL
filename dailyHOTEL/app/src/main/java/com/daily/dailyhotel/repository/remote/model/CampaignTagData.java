package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.CampaignTag;

/**
 * Created by iseung-won on 2017. 8. 8..
 */
@JsonObject
public class CampaignTagData
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "startDate")
    public String startDate; // ISO-8601

    @JsonField(name = "endDate")
    public String endDate; // ISO-8601

    @JsonField(name = "hashtag")
    public String campaignTag;

    @JsonField(name = "serviceType")
    public String serviceType;

    public CampaignTag getCampaignTag()
    {
        CampaignTag campaignTag = new CampaignTag();
        campaignTag.index = index;
        campaignTag.campaignTag = this.campaignTag;
        campaignTag.startDate = startDate;
        campaignTag.endDate = endDate;
        campaignTag.serviceType = serviceType;

        return campaignTag;
    }
}
