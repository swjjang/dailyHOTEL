package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GourmetReceipt;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 12. 7..
 */

public interface ReceiptInterface
{
    Observable<GourmetReceipt> getGourmetReceipt(int reservationIdx);

    Observable<GourmetReceipt> getGourmetReceipt(String aggregationId);

    Observable<String> getGourmetReceiptByEmail(int reservationIdx, String email);

    Observable<String> getGourmetReceiptByEmail(String aggregationId, String email);
}
