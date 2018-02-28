package com.daily.dailyhotel.domain;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2018. 2. 28..
 */

public interface TempReviewInterface
{
    Observable<Boolean> addTempReview(int reservationIndex, String serviceType //
        , String startDate, String endDate, String scoreQuestion, String pickQuestion, String comment);

    Observable<ArrayList<String>> getTempReview(int reservationIndex, String serviceType, String startDate, String endDate);

    Observable<Boolean> deleteTempReview(int reservationIndex, String serviceType, String startDate);
}
