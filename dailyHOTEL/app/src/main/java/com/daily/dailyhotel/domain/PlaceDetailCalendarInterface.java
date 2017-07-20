package com.daily.dailyhotel.domain;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 7. 12..
 */

public interface PlaceDetailCalendarInterface
{
    Observable<List<String>> getGourmetUnavailableDates(int placeIndex, int dateRange, boolean reverse);

    Observable<List<String>> getStayUnavailableCheckInDates(int placeIndex, int dateRange, boolean reverse);

    Observable<List<String>> getStayAvailableCheckOutDates(int placeIndex, int dateRange, String checkInDate);
}
