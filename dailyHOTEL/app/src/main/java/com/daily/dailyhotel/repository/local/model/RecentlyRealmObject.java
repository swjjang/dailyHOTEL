package com.daily.dailyhotel.repository.local.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by android_sam on 2017. 6. 8..
 */

public class RecentlyRealmObject extends RealmObject
{
    @PrimaryKey
    public int index;
    public String serviceType;
    public Date date; // GMT+9 시간대 korea time
    public String name;
    public String englishName;
//    public Province province;
    public String imageUrl;
}
