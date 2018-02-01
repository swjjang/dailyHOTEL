package com.daily.dailyhotel.domain;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 1. 31..
 */

public interface GoogleAddressInterface
{
    Observable<String> getLocationAddress(double latitude, double longitude);
}
