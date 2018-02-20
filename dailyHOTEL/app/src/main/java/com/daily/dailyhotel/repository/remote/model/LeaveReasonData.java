package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.LeaveReason;

/**
 * Created by android_sam on 2018. 2. 19..
 */
@JsonObject
public class LeaveReasonData
{
    @JsonField(name = "idx")
    public int index;

    @JsonField(name = "reason")
    public String reason;

    public LeaveReason getLeaveReason()
    {
        LeaveReason leaveReason = new LeaveReason();

        leaveReason.index = this.index;
        leaveReason.reason = this.reason;

        return leaveReason;
    }
}
