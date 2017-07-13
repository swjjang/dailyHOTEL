package com.daily.dailyhotel.domain;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 7. 12..
 */

public interface PlaceDetailCalendarInterface
{
    Observable<List<String>> getGourmetUnavailableDates(int placeIndex, int dateRange, boolean reverse);

    Observable<List<String>> getStayUnavailableDates(int placeIndex, int dateRange, boolean reverse);
}
