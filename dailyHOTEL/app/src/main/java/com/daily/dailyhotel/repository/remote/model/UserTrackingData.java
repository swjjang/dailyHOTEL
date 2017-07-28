package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.UserTracking;

@JsonObject
public class UserTrackingData
{
    @JsonField(name = "tracking")
    public Tracking tracking;

    public UserTrackingData()
    {

    }

    public UserTracking getUserTracking()
    {
        UserTracking userTracking = new UserTracking();
        userTracking.countOfGourmetPaymentCompleted = tracking.countOfGourmetPaymentCompleted;
        userTracking.countOfStayPaymentCompleted = tracking.countOfHotelPaymentCompleted;

        return userTracking;
    }

    @JsonObject
    static class Tracking
    {
        @JsonField(name = "countOfGourmetPaymentCompleted")
        public int countOfGourmetPaymentCompleted;

        @JsonField(name = "countOfHotelPaymentCompleted")
        public int countOfHotelPaymentCompleted;
    }
}
