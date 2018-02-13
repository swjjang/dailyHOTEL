package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.entity.GoogleAddress;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 1. 31..
 */

public interface GoogleAddressInterface
{
    Observable<GoogleAddress> getLocationAddress(double latitude, double longitude);
}
