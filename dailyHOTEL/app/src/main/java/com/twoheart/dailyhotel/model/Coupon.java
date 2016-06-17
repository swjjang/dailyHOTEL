package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sam Lee on 2016. 5. 19..
 * <p>
 * 현재 임의로 만든 모델로 실제 서버에서 내려오는 값에 따라 변경 될 수 있음
 */
public class Coupon implements Parcelable
{
    // 하루
    public static final long MILLISECOND_IN_A_DAY = 3600 * 24 * 1000;

    // coupon list
    public static final String COUPON_LIST = "coupons";

    // coupon object type
    public static final String USER_COUPON_CODE = "userCouponCode";

    // coupon object type
    public static final String VALID_TO = "validTo";

    // coupon object type
    public static final String VALID_FROM = "validFrom";

    // coupon object type
    public static final String TITLE = "title";

    // coupon object type
    public static final String AMOUNT = "amount";

    // coupon object type
    public static final String AMOUNT_MINIMUM = "amountMinimum";

    // coupon object type
    public static final String IS_DOWNLOADED = "isDownloaded";

    // coupon object type
    public static final String SERVER_DATE = "serverDate";

    // coupon object type
    public static final String AVAILABLE_ITEM = "availableItem";

    // coupon object type
    public static final String IS_EXPIRED = "isExpired";

    // coupon object type
    public static final String IS_REDEEMED = "isRedeemed";

    // coupon object type
    public static final String DISABLED_AT = "disabledAt";

    // coupon object type
    public static final String COUPON_CODE = "couponCode";


    public String userCouponCode; // 유저 쿠폰 코드 (이벤트 페이지의 쿠폰코드와 틀림),,
    public int amount; // 쿠폰금액 ,,,
    public String title; // 쿠폰명 ,,,
    public String validFrom; // 시작시간 ,,,
    public String validTo; // 종료시간 ,,,
    public int amountMinimum; // 최소주문금액 ,,,
    public boolean isDownloaded; // 상태표시 ,,
    public String availableItem; // 사용가능처 ,,
    public String serverDate; // 서버시간 ,,
    public boolean isExpired; // 유효기간 만료 여부 ,
    public boolean isRedeemed; // 사용 여부 ,
    public String disabledAt; // 사용한 날짜 (ISO-8601) ,
    public String couponCode; // 이벤트 웹뷰, 쿠폰주의사항 사용용 쿠폰 코드


    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon(String userCouponCode, int amount, String title, String validFrom, //
                  String validTo, int amountMinimum, boolean isDownloaded, String availableItem, //
                  String serverDate, boolean isExpired, boolean isRedeemed, String disabledAt, String couponCode)
    {
        this.userCouponCode = userCouponCode;
        this.amount = amount;
        this.title = title;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.amountMinimum = amountMinimum;
        this.isDownloaded = isDownloaded;
        this.availableItem = availableItem;
        this.serverDate = serverDate;
        this.isExpired = isExpired;
        this.isRedeemed = isRedeemed;
        this.disabledAt = disabledAt;
        this.couponCode = couponCode;
    }

    public static String getAvailableDatesString(String startTime, String endTime)
    {
        String availableDatesString = "";

        try
        {
            String strStart = Util.simpleDateFormatISO8601toFormat(startTime, "yyyy.MM.dd");
            String strEnd = Util.simpleDateFormatISO8601toFormat(endTime, "yyyy.MM.dd");

            availableDatesString = String.format("%s ~ %s", strStart, strEnd);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return availableDatesString;
    }

    /**
     * 남은 날 수를 리턴하는 메소드
     *
     * @param coupon
     * @return -1 기간만료, 0 당일만료, 그 이외 숫자 남은 일자
     */
    public static int getDueDateCount(Coupon coupon)
    {
        int dayCount = -1;
        Date serverDate;
        Date endDate;

        try
        {
            serverDate = Util.getISO8601Date(coupon.serverDate);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            serverDate = new Date();
        }

        try
        {
            endDate = Util.getISO8601Date(coupon.validTo);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            endDate = new Date();
        }

        long gap = endDate.getTime() - serverDate.getTime();

        if (gap <= 0)
        {
            // 기간 만료 상품
            ExLog.d("aready expired");
            return dayCount;
        } else
        {
            // 금일 만료를 제외한 날짜의 경우 내일이 2일 남음이기때문에 1을 더해줘야 함
            dayCount = (int) (gap / MILLISECOND_IN_A_DAY) + 1;
            return dayCount;
        }
    }

    public static ArrayList<Coupon> makeCouponList(JSONObject data)
    {
        ArrayList<Coupon> list = new ArrayList<>();

        try
        {
            String serverDate = "";

            if (data.has(Coupon.SERVER_DATE) == true)
            {
                serverDate = data.getString(Coupon.SERVER_DATE);
            }

            JSONArray couponList = data.getJSONArray(COUPON_LIST);

            int length = couponList.length();

            for (int i = 0; i < length; i++)
            {
                JSONObject jsonObject = couponList.getJSONObject(i);

                Coupon coupon = makeCoupon(jsonObject, serverDate);
                list.add(coupon);
            }
        } catch (JSONException e)
        {
            ExLog.e(e.getMessage());

        } catch (NullPointerException e)
        {
            ExLog.e(e.getMessage());
        }

        return list;
    }

    private static Coupon makeCoupon(JSONObject jsonObject, String serverDate)
    {
        Coupon coupon = null;

        String userCouponCode = null; // 유저 쿠폰 코드 (이벤트 페이지의 쿠폰코드와 틀림),,
        boolean isDownloaded = false; // 상태표시 ,,
        String availableItem = null; // 사용가능처 ,,
        boolean isExpired = false; // 유효기간 만료 여부 ,
        boolean isRedeemed = false; // 사용 여부 ,
        String disabledAt = null; // 사용한 날짜 (ISO-8601) ,
        String couponCode = null; // 이벤트 웹뷰, 쿠폰사용주의사항 용 쿠폰 코드

        try
        {
            String validFrom = jsonObject.getString(Coupon.VALID_FROM); // 쿠폰 시작 시간

            String validTo = jsonObject.getString(Coupon.VALID_TO); // 유효기간, 만료일, 쿠폰 만료시간

            String title = jsonObject.getString(Coupon.TITLE);

            int amount = jsonObject.getInt(Coupon.AMOUNT); // 쿠폰가격

            int amountMinimum = jsonObject.getInt(Coupon.AMOUNT_MINIMUM); // 최소 결제 금액

            // 쿠폰 사용내역 미사용
            if (jsonObject.has(Coupon.USER_COUPON_CODE))
            {
                userCouponCode = jsonObject.getString(Coupon.USER_COUPON_CODE); // 쿠폰 별칭 코드
            }

            // 쿠폰 사용내역 미사용
            if (jsonObject.has(Coupon.IS_DOWNLOADED))
            {
                isDownloaded = jsonObject.getBoolean(Coupon.IS_DOWNLOADED); // 다운로드 여부
            }

            // 쿠폰 사용내역 미사용
            if (jsonObject.has(Coupon.AVAILABLE_ITEM))
            {
                availableItem = jsonObject.getString(Coupon.AVAILABLE_ITEM); // 사용가능처
            }

            // 쿠폰 사용내역만 사용
            if (jsonObject.has(Coupon.IS_EXPIRED))
            {
                isExpired = jsonObject.getBoolean(Coupon.IS_EXPIRED); // 유효기간 만료 여부
            }

            // 쿠폰 사용내역만 사용
            if (jsonObject.has(Coupon.IS_REDEEMED))
            {
                isRedeemed = jsonObject.getBoolean(Coupon.IS_REDEEMED); // 사용 여부
            }

            // 쿠폰 사용내역만 사용
            if (jsonObject.has(Coupon.DISABLED_AT))
            {
                disabledAt = jsonObject.getString(Coupon.DISABLED_AT); // 사용한 날짜 (ISO-8601)
            }

            //
            if (jsonObject.has(Coupon.COUPON_CODE))
            {
                couponCode = jsonObject.getString(Coupon.COUPON_CODE); // 이벤트 웹뷰, 쿠폰 사용주의사항용 쿠폰코드
            }

            coupon = new Coupon(userCouponCode, amount, title, validFrom, validTo, amountMinimum, //
                isDownloaded, availableItem, serverDate, isExpired, //
                isRedeemed, disabledAt, couponCode);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return coupon;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(userCouponCode);
        dest.writeInt(amount);
        dest.writeString(title);
        dest.writeString(validFrom);
        dest.writeString(validTo);
        dest.writeInt(amountMinimum);
        dest.writeInt(isDownloaded == true ? 1 : 0);
        dest.writeString(availableItem);
        dest.writeString(serverDate);
        dest.writeInt(isExpired == true ? 1 : 0);
        dest.writeInt(isRedeemed == true ? 1 : 0);
        dest.writeString(disabledAt);
        dest.writeString(couponCode);
    }

    private void readFromParcel(Parcel in)
    {
        userCouponCode = in.readString();
        amount = in.readInt();
        title = in.readString();
        validFrom = in.readString();
        validTo = in.readString();
        amountMinimum = in.readInt();
        isDownloaded = in.readInt() == 1 ? true : false;
        availableItem = in.readString();
        serverDate = in.readString();
        isExpired = in.readInt() == 1 ? true : false;
        isRedeemed = in.readInt() == 1 ? true : false;
        disabledAt = in.readString();
        couponCode = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Coupon createFromParcel(Parcel in)
        {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size)
        {
            return new Coupon[size];
        }
    };
}
