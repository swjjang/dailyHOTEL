package com.daily.dailyhotel.domain;

import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface RecentlyLocalInterface
{
    Observable addRecentlyItem(Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, boolean isUpdateDate);

    Observable deleteRecentlyItem(Constants.ServiceType serviceType, int index);

    Observable<ArrayList<RecentlyDbPlace>> getRecentlyTypeList(Constants.ServiceType... serviceTypes);
}
