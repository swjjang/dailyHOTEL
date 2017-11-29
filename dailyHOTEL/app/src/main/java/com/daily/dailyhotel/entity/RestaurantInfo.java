package com.daily.dailyhotel.entity;

/**
 * Created by android_sam on 2017. 11. 29..
 */

public class RestaurantInfo
{
    public String address; // (string, optional): 주소 ,
    public String category; // (string, optional): 카테고리 ,
    public String categorySub; // (string, optional): 2차카테고리 ,
    public int index; // (integer): 레스토랑IDX ,
    public double latitude; // (number, optional): 위도 ,
    public double longitude; // (number, optional): 경도 ,
    public String name; // (string): 레스토랑이름
}
