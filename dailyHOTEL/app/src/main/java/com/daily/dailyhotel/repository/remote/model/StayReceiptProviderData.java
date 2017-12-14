package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayReceiptProvider;

/**
 * Created by android_sam on 2017. 12. 11..
 */
@JsonObject
public class StayReceiptProviderData
{
    @JsonField(name = "ceoName")
    public String ceoName; // "ceoName": "신인식",

    @JsonField(name = "phone")
    public String phone; // "phone": "1800-9120",

    @JsonField(name = "fax")
    public String fax; // "fax": "02-6455-9331",

    @JsonField(name = "registrationNo")
    public String registrationNo; // "registrationNo": "144-81-15781",

    @JsonField(name = "memo")
    public String memo; // "memo": "본 영수증은 세금계산서가 아니므로 \n지출 증빙 목적으로 사용될 수 없으며 \n참고목적으로 제공됩니다.",

    @JsonField(name = "companyName")
    public String companyName; // "companyName": "(주) 데일리",

    @JsonField(name = "address")
    public String address; // "address": "서울시 강남구 테헤란로 20길 20"

    public StayReceiptProvider getProvider()
    {
        StayReceiptProvider provider = new StayReceiptProvider();

        provider.ceoName = this.ceoName;
        provider.phone = this.phone;
        provider.fax = this.fax;
        provider.registrationNo = this.registrationNo;
        provider.memo = this.memo;
        provider.companyName = this.companyName;
        provider.address = this.address;

        return provider;
    }
}
