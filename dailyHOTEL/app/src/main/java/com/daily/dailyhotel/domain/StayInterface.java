package com.daily.dailyhotel.domain;

import android.content.Context;
import android.util.Pair;

import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.RoomImageInformation;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetail;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;
import com.twoheart.dailyhotel.model.DailyCategoryType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

public interface StayInterface
{
    Observable<Stays> getList(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType);

    Observable<Stays> getLocalPlusList(Context context, Map<String, Object> queryMap);

    Observable<StayFilterCount> getListCountByFilter(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType);

    Observable<StayFilterCount> getLocalPlusListCountByFilter(Context context, Map<String, Object> queryMap);

    Observable<StayDetail> getDetail(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<List<RoomImageInformation>> getRoomImages(int stayIndex, int roomIndex);

    Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime);

    Observable<WishResult> addWish(int stayIndex);

    Observable<WishResult> removeWish(int stayIndex);

    Observable<ReviewScores> getReviewScores(int stayIndex);

    Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit);

    Observable<List<TrueVR>> getTrueVR(int stayIndex);

    Observable<List<StayAreaGroup>> getAreaList(Context context, DailyCategoryType categoryType);

    Observable<LinkedHashMap<Area, List<StaySubwayAreaGroup>>> getSubwayAreaList(Context context, DailyCategoryType categoryType);

    Observable<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> getRegionList(Context context, DailyCategoryType categoryType);
}
