package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.DownloadCouponResult;

/**
 * Created by iseung-won on 2018. 3. 29..
 */
@JsonObject
public class DownloadCouponResultData
{
    @JsonField(name = "validFrom")
    public String validFrom;

    @JsonField(name = "validTo")
    public String validTo;

    public DownloadCouponResult getResult()
    {
        DownloadCouponResult result = new DownloadCouponResult();
        result.validFrom = validFrom;
        result.validTo = validTo;
        return result;
    }
}
