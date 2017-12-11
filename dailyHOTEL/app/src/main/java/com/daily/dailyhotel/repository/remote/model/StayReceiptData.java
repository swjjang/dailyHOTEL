package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.StayReceipt;

/**
 * Created by android_sam on 2017. 12. 11..
 */
@JsonObject
public class StayReceiptData
{
    @JsonField(name = "reservation_idx")
    public int reservationIndex; // "reservation_idx": "2012105",

    @JsonField(name = "receipt")
    public StayReceiptItemData receipt; // "receipt" , 하위 클래스 참고

    @JsonField(name = "provider")
    public StayReceiptProviderData provider; // "provider" , 하위 클래스 참고

    public StayReceipt getStayReceipt()
    {
        StayReceipt receipt = new StayReceipt();

        receipt.reservationIndex = this.reservationIndex;
        receipt.receipt = this.receipt.getStayReceiptItem();
        receipt.provider = this.provider.getProvider();

        return receipt;
    }
}
