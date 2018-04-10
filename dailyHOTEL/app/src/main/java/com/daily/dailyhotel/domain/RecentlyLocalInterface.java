package com.daily.dailyhotel.domain;

import android.content.Context;

import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.twoheart.dailyhotel.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface RecentlyLocalInterface
{
    Observable addRecentlyItem(Context context, Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, String areaGroupName, boolean isUpdateDate);

    Observable<Boolean> deleteRecentlyItem(Context context, Constants.ServiceType serviceType, int index);

    Observable<Boolean> clearRecentlyItems(Context context, Constants.ServiceType serviceType);

    Observable<ArrayList<RecentlyDbPlace>> getRecentlyTypeList(Context context, Constants.ServiceType... serviceTypes);

    Observable<ArrayList<CarouselListItem>> sortCarouselListItemList(Context context, ArrayList<CarouselListItem> actualList, Constants.ServiceType... serviceTypes);

    Observable<String> getTargetIndices(Context context, Constants.ServiceType serviceType, int maxSize);

    Observable<JSONObject> getRecentlyJSONObject(Context context, int maxSize, Constants.ServiceType... serviceTypes);

    Observable<ArrayList<Integer>> getRecentlyIndexList(Context context, Constants.ServiceType... serviceTypes);
}
