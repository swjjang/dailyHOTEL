package com.daily.dailyhotel.domain;

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
    Observable addRecentlyItem(Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, String areaGroupName, boolean isUpdateDate);

    Observable<Boolean> deleteRecentlyItem(Constants.ServiceType serviceType, int index);

    Observable<Boolean> clearRecentlyItems(Constants.ServiceType serviceType);

    Observable<ArrayList<RecentlyDbPlace>> getRecentlyTypeList(Constants.ServiceType... serviceTypes);

    Observable<ArrayList<CarouselListItem>> sortCarouselListItemList(ArrayList<CarouselListItem> actualList, Constants.ServiceType... serviceTypes);

    Observable<String> getTargetIndices(Constants.ServiceType serviceType, int maxSize);

    Observable<JSONObject> getRecentlyJSONObject(int maxSize, Constants.ServiceType... serviceTypes);

    Observable<ArrayList<Integer>> getRecentlyIndexList(Constants.ServiceType... serviceTypes);
}
