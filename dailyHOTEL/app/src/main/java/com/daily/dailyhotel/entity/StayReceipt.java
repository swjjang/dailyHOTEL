package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 12. 11..
 */

public class StayReceipt
{
    public int reservationIndex; // "reservation_idx": "2012105",
    public StayReceiptItem receipt; // "receipt" , 하위 클래스 참고
    public StayReceiptProvider provider; // "provider" , 하위 클래스 참고
}
