package com.daily.dailyhotel.domain;

import com.twoheart.dailyhotel.util.Constants;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 9. 29..
 */

public interface RecentlyLocalInterface
{
    Observable addRecentlyItem(Constants.ServiceType serviceType, int index //
        , String name, String englishName, String imageUrl, boolean isUpdateDate);
}
