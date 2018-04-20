package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.GoogleAddress;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 1. 31..
 */

public interface GoogleAddressInterface
{
    Observable<GoogleAddress> getLocationAddress(double latitude, double longitude);

    Observable<String> getLocationRegionName(Context context, String regionName //
        , double latitude, double longitude, boolean isCountryName);
}
