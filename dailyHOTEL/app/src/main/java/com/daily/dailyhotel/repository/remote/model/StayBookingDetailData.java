package com.daily.dailyhotel.repository.remote.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.twoheart.dailyhotel.model.Stay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@JsonObject
public class StayBookingDetailData
{
    @JsonField(name = "hotelReservationIdx")
    public int reservationIdx;

    @JsonField(name = "hotelIdx")
    public int hotelIdx;

    @JsonField(name = "userIdx")
    public int userIdx;

    @JsonField(name = "userCouponIdx")
    public int userCouponIdx;

    @JsonField(name = "regionProvinceIdx")
    public int regionProvinceIdx;

    @JsonField(name = "regionDistrictName")
    public String regionDistrictName;

    @JsonField(name = "hotelName")
    public String hotelName;

    @JsonField(name = "hotelAddress")
    public String hotelAddress;

    @JsonField(name = "adressSummary")
    public String addressSummary;

    @JsonField(name = "hotelPhone1")
    public String hotelPhone1;

    @JsonField(name = "hotelPhone2")
    public String hotelPhone2;

    @JsonField(name = "hotelPhone3")
    public String hotelPhone3;

    @JsonField(name = "hotelGrade")
    public String hotelGrade;

    @JsonField(name = "checkIn")
    public String checkInDateTime;

    @JsonField(name = "checkOut")
    public String checkOutDateTime;

    @JsonField(name = "latitude")
    public double latitude;

    @JsonField(name = "longitude")
    public double longitude;

    @JsonField(name = "guestEmail")
    public String guestEmail;

    @JsonField(name = "guestName")
    public String guestName;

    @JsonField(name = "guestPhone")
    public String guestPhone;

    @JsonField(name = "roomIdx")
    public int roomIdx;

    @JsonField(name = "roomName")
    public String roomName;

    @JsonField(name = "guestTransportation")
    public String guestTransportation;

    @JsonField(name = "paidAt")
    public String paidAt;

    @JsonField(name = "priceTotal")
    public int priceTotal;

    @JsonField(name = "discountTotal")
    public int discountTotal;

    @JsonField(name = "bonus")
    public int bonus;

    @JsonField(name = "couponAmount")
    public int couponAmount;

    @JsonField(name = "transactionType")
    public String transactionType;

    @JsonField(name = "reviewStatusType")
    public String reviewStatusType;

    @JsonField(name = "refundType")
    public String refundType;

    @JsonField(name = "hotelSpec")
    public String hotelSpec;

    @JsonField(name = "overseas")
    public boolean overseas;

    @JsonField(name = "readyForRefund")
    public boolean readyForRefund;

    @JsonField(name = "reservationWaiting")
    public boolean reservationWaiting;

    public StayBookingDetailData()
    {

    }

    public StayBookingDetail getStayBookingDetail()
    {
        StayBookingDetail stayBookingDetail = new StayBookingDetail();
        stayBookingDetail.reservationIndex = reservationIdx;
        stayBookingDetail.stayIndex = hotelIdx;
        stayBookingDetail.userIndex = userIdx;
        stayBookingDetail.userCouponIndex = userCouponIdx;
        stayBookingDetail.regionProvinceIndex = regionProvinceIdx;
        stayBookingDetail.regionDistrictName = regionDistrictName;
        stayBookingDetail.stayName = hotelName;
        stayBookingDetail.stayAddress = hotelAddress;
        stayBookingDetail.addressSummary = addressSummary;
        stayBookingDetail.phone1 = hotelPhone1;
        stayBookingDetail.phone2 = hotelPhone2;
        stayBookingDetail.phone3 = hotelPhone3;

        try
        {
            stayBookingDetail.stayGrade = Stay.Grade.valueOf(hotelGrade);
        } catch (Exception e)
        {
            stayBookingDetail.stayGrade = Stay.Grade.etc;
        }

        stayBookingDetail.checkInDateTime = checkInDateTime;
        stayBookingDetail.checkOutDateTime = checkOutDateTime;
        stayBookingDetail.latitude = latitude;
        stayBookingDetail.longitude = longitude;
        stayBookingDetail.guestEmail = guestEmail;
        stayBookingDetail.guestName = guestName;
        stayBookingDetail.guestPhone = guestPhone;
        stayBookingDetail.roomIndex = roomIdx;
        stayBookingDetail.roomName = roomName;
        stayBookingDetail.guestTransportation = guestTransportation;
        stayBookingDetail.paymentDateTime = paidAt;
        stayBookingDetail.priceTotal = priceTotal;
        stayBookingDetail.discountTotal = discountTotal;
        stayBookingDetail.bonusAmount = bonus;
        stayBookingDetail.couponAmount = couponAmount;
        stayBookingDetail.transactionType = transactionType;
        stayBookingDetail.reviewStatusType = reviewStatusType;
        stayBookingDetail.refundType = refundType;
        stayBookingDetail.overseas = overseas;
        stayBookingDetail.readyForRefund = readyForRefund;
        stayBookingDetail.setSpecificationMap(getSpecification(hotelSpec));
        stayBookingDetail.reservationWaiting = reservationWaiting;

        return stayBookingDetail;
    }

    private LinkedHashMap<String, List<String>> getSpecification(String specification)
    {
        if (DailyTextUtils.isTextEmpty(specification) == true)
        {
            return null;
        }

        LinkedHashMap<String, List<String>> specificationMap = new LinkedHashMap<>();

        try
        {
            JSONObject wrapJSONObject = new JSONObject(specification);
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
