package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.GuestInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class GuestInfoData
{
    @JsonField(name = "arrivalDateTime")
    public String arrivalDateTime; // (string): 이용시간 ,

    @JsonField(name = "email")
    public String email; // (string): 이메일 ,

    @JsonField(name = "name")
    public String name; // (string): 이름 ,

    @JsonField(name = "numberOfGuest")
    public int numberOfGuest; // (integer): 방문자수 ,

    @JsonField(name = "phone")
    public String phone; // (string): 전화번호

    public GuestInfo getGuestInfo()
    {
        GuestInfo guestInfo = new GuestInfo();
        guestInfo.arrivalDateTime = this.arrivalDateTime;
        guestInfo.email = this.email;
        guestInfo.name = this.name;
        guestInfo.numberOfGuest = this.numberOfGuest;
        guestInfo.phone = this.phone;

        return guestInfo;
    }
}
