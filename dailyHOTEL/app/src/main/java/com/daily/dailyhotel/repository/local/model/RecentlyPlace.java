package com.daily.dailyhotel.repository.local.model;

import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.util.Constants;

/**
 * Created by android_sam on 2017. 8. 3..
 */

public class RecentlyPlace
{
    public int index;
    public Constants.ServiceType serviceType;
    public long savingTime; // GMT+9 시간대 korea time
    public String name;
    public String englishName;
    //    public Province province;
    public String imageUrl;
}
