package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetBookingDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonObject
public class GourmetBookingDetailData
{
    @JsonField(name = "fnbReservationIdx")
    public int reservationIndex;

    @JsonField(name = "restaurantIdx")
    public int restaurantIdx;

    @JsonField(name = "userIdx")
    public int userIdx;

    @JsonField(name = "userCouponIdx")
    public int userCouponIdx;

    @JsonField(name = "fnbRegionProvinceIdx")
    public int fnbRegionProvinceIdx;

    @JsonField(name = "restaurantName")
    public String restaurantName;

    @JsonField(name = "restaurantAddress")
    public String restaurantAddress;

    @JsonField(name = "addrSummary")
    public String addressSummary;

    @JsonField(name = "restaurantPhone1")
    public String restaurantPhone1;

    @JsonField(name = "restaurantPhone2")
    public String restaurantPhone2;

    @JsonField(name = "restaurantPhone3")
    public String restaurantPhone3;

    @JsonField(name = "category")
    public String category;

    @JsonField(name = "categorySub")
    public String categorySub;

    @JsonField(name = "arrivalTime")
    public String arrivalDateTime;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "customerEmail")
    public String customerEmail;

    @JsonField(name = "customerName")
    public String customerName;

    @JsonField(name = "customerPhone")
    public String customerPhone;

    @JsonField(name = "ticketName")
    public String ticketName;

    @JsonField(name = "ticketCount")
    public int ticketCount;

    @JsonField(name = "paidAt")
    public String paidAt;

    @JsonField(name = "paymentAmount")
    public int paymentAmount;

    @JsonField(name = "discountTotal")
    public int discountTotal;

    @JsonField(name = "couponAmount")
    public int couponAmount;

    @JsonField(name = "reviewStatusType")
    public String reviewStatusType;

    @JsonField(name = "description")
    public String description;

    @JsonField(name = "overseas")
    public boolean overseas;

    public GourmetBookingDetailData()
    {

    }

    public GourmetBookingDetail getGourmetBookingDetail()
    {
        GourmetBookingDetail gourmetBookingDetail = new GourmetBookingDetail();

        gourmetBookingDetail.reservationIndex = reservationIndex;
        gourmetBookingDetail.gourmetIndex = restaurantIdx;
        gourmetBookingDetail.userIndex = userIdx;
        gourmetBookingDetail.userCouponIndex = userCouponIdx;
        gourmetBookingDetail.regionProvinceIndex = fnbRegionProvinceIdx;
        gourmetBookingDetail.gourmetName = restaurantName;
        gourmetBookingDetail.gourmetAddress = restaurantAddress;
        gourmetBookingDetail.addressSummary = addressSummary;
        gourmetBookingDetail.phone1 = restaurantPhone1;
        gourmetBookingDetail.phone2 = restaurantPhone2;
        gourmetBookingDetail.phone3 = restaurantPhone3;
        gourmetBookingDetail.category = category;
        gourmetBookingDetail.categorySub = categorySub;
        gourmetBookingDetail.arrivalDateTime = arrivalDateTime;
        gourmetBookingDetail.latitude = latitude;
        gourmetBookingDetail.longitude = longitude;
        gourmetBookingDetail.guestEmail = customerEmail;
        gourmetBookingDetail.guestName = customerName;
        gourmetBookingDetail.guestPhone = customerPhone;
        gourmetBookingDetail.ticketName = ticketName;
        gourmetBookingDetail.ticketCount = ticketCount;
        gourmetBookingDetail.pamentDateTime = paidAt;
        gourmetBookingDetail.discountTotal = discountTotal;
        gourmetBookingDetail.priceTotal = paymentAmount;
        gourmetBookingDetail.couponAmount = couponAmount;
        gourmetBookingDetail.reviewStatusType = reviewStatusType;
        gourmetBookingDetail.overseas = overseas;
        gourmetBookingDetail.setDescriptionMap(getDescription(description));

        return gourmetBookingDetail;
    }

    private LinkedHashMap<String, List<String>> getDescription(String description)
    {
        if (DailyTextUtils.isTextEmpty(description) == true)
        {
            return null;
        }

        LinkedHashMap<String, List<String>> specificationMap = new LinkedHashMap<>();

        try
        {
            JSONObject wrapJSONObject = new JSONObject(description);
            JSONArray jsonArray = wrapJSONObject.getJSONArray("wrap"); // 해당 코드 없음

            int length = jsonArray.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject specObj = jsonArray.getJSONObject(i);

                if (specObj == null || specObj.has("key") == false || specObj.has("value") == false)
                {
                    continue;
                }

                String key = specObj.getString("key");
                JSONArray valueArr = specObj.getJSONArray("value");
                List<String> valueList = new ArrayList<>(valueArr.length());

                for (int j = 0; j < valueArr.length(); j++)
                {
                    JSONObject valueObj = valueArr.getJSONObject(j);

                    if (valueObj == null || valueObj.has("value") == false)
                    {
                        continue;
                    }

                    String value = valueObj.getString("value");
                    valueList.add(value);
                }

                specificationMap.put(key, valueList);
            }
        } catch (Exception e)
        {
            return null;
        }

        return specificationMap;
    }
}
