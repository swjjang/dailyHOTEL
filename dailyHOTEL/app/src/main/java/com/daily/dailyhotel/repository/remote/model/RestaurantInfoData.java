package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.dailyhotel.entity.RestaurantInfo;

/**
 * Created by android_sam on 2017. 11. 29..
 */
@JsonObject
public class RestaurantInfoData
{
    @JsonField(name = "address")
    public String address; // (string, optional): 주소 ,

    @JsonField(name = "category")
    public String category; // (string, optional): 카테고리 ,

    @JsonField(name = "categorySub")
    public String categorySub; // (string, optional): 2차카테고리 ,

    @JsonField(name = "idx")
    public int index; // (integer): 레스토랑IDX ,

    @JsonField(name = "latitude")
    public double latitude; // (number, optional): 위도 ,

    @JsonField(name = "longitude")
    public double longitude; // (number, optional): 경도 ,

    @JsonField(name = "name")
    public String name; // (string): 레스토랑이름

    public RestaurantInfo getRestaurantInfo()
    {
        RestaurantInfo restaurantInfo = new RestaurantInfo();

        restaurantInfo.address = this.address;
        restaurantInfo.category = this.category;
        restaurantInfo.categorySub = this.categorySub;
        restaurantInfo.index = this.index;
        restaurantInfo.latitude = this.latitude;
        restaurantInfo.longitude = this.longitude;
        restaurantInfo.name = this.name;

        return restaurantInfo;
    }
}
