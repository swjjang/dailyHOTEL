package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CouponHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by android_sam on 2016. 6. 20..
 */
public class CouponUtil
{
    // 하루
    public static final long MILLISECOND_IN_A_DAY = 3600 * 24 * 1000;

    public static final class Type
    {
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

        // coupon object type
        public static final String STAY_FROM = "stayFrom";

        // coupon object type
        public static final String STAY_TO = "stayTo";

        // coupon object type
        public static final String DOWNLOADED_AT = "downloadedAt";

        // coupon object type
        public static final String DISABLE_AT = "disableAt";

        // coupon object type
        public static final String AVAILABLE_IN_DOMESTIC = "availableInDomestic";

        // coupon object type
        public static final String AVAILABLE_IN_OVERSEAS = "availableInOverseas";

        // coupon object type
        public static final String AVAILABLE_IN_HOTEL = "availableInHotel";

        // coupon object type
        public static final String AVAILABLE_IN_GOURMET = "availableInGourmet";
    }

    public static ArrayList<Coupon> getCouponList(JSONObject response)
    {
        ArrayList<Coupon> list = new ArrayList<>();

        try
        {
            boolean hasData = response.has("data");
            if (hasData == true)
            {
                JSONObject data = response.getJSONObject("data");
                if (data != null)
                {
                    String serverDate;
                    if (data.has(Type.SERVER_DATE) == true)
                    {
                        serverDate = data.getString(Type.SERVER_DATE);
                    } else
                    {
                        serverDate = "";
                    }

                    JSONArray couponList = data.getJSONArray(Type.COUPON_LIST);

                    int length = couponList.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = couponList.getJSONObject(i);

                        Coupon coupon = getCoupon(jsonObject, serverDate);
                        list.add(coupon);
                    }
                }
            } else
            {
                ExLog.d("response has not data");
            }

        } catch (Exception e)
        {
            ExLog.e(e.toString());

        }

        return list;
    }

    private static Coupon getCoupon(JSONObject jsonObject, String serverDate)
    {
        Coupon coupon = null;

        String couponCode = null; // 이벤트 웹뷰, 쿠폰사용주의사항 용 쿠폰 코드
        String stayFrom = null;
        String stayTo = null;
        String downloadedAt = null;
        //        String disableAt = null;
        boolean availableInDomestic = false;
        boolean availableInOverseas = false;
        boolean availableInHotel = false;
        boolean availableInGourmet = false;
        //        boolean isRedeemed = false;
        //        boolean isExpired = false;

        try
        {
            boolean isDownloaded = jsonObject.getBoolean(Type.IS_DOWNLOADED); // 다운로드 여부

            int amount = jsonObject.getInt(Type.AMOUNT); // 쿠폰가격
            int amountMinimum = jsonObject.getInt(Type.AMOUNT_MINIMUM); // 최소 결제 금액

            String validFrom = jsonObject.getString(Type.VALID_FROM); // 쿠폰 시작 시간
            String validTo = jsonObject.getString(Type.VALID_TO); // 유효기간, 만료일, 쿠폰 만료시간
            String title = jsonObject.getString(Type.TITLE); // 쿠폰이름
            String userCouponCode = jsonObject.getString(Type.USER_COUPON_CODE); // 쿠폰 별칭 코드
            String availableItem = jsonObject.getString(Type.AVAILABLE_ITEM); // 사용가능처

            // 쿠폰 주의사항용 쿠폰 코드
            if (jsonObject.has(Type.COUPON_CODE))
            {
                couponCode = jsonObject.getString(Type.COUPON_CODE); // 이벤트 웹뷰, 쿠폰 사용주의사항용 쿠폰코드
            }

            if (jsonObject.has(Type.STAY_FROM))
            {
                stayFrom = jsonObject.getString(Type.STAY_FROM);
            }

            if (jsonObject.has(Type.STAY_TO))
            {
                stayTo = jsonObject.getString(Type.STAY_TO);
            }

            if (jsonObject.has(Type.DOWNLOADED_AT))
            {
                downloadedAt = jsonObject.getString(Type.DOWNLOADED_AT);
            }

            //            if (jsonObject.has(Type.DISABLE_AT))
            //            {
            //                disableAt = jsonObject.getString(Type.DISABLE_AT);
            //            }

            if (jsonObject.has(Type.AVAILABLE_IN_DOMESTIC))
            {
                availableInDomestic = jsonObject.getBoolean(Type.AVAILABLE_IN_DOMESTIC);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_OVERSEAS))
            {
                availableInOverseas = jsonObject.getBoolean(Type.AVAILABLE_IN_OVERSEAS);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_HOTEL))
            {
                availableInHotel = jsonObject.getBoolean(Type.AVAILABLE_IN_HOTEL);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_GOURMET))
            {
                availableInGourmet = jsonObject.getBoolean(Type.AVAILABLE_IN_GOURMET);
            }

            //            if (jsonObject.has(Type.IS_REDEEMED))
            //            {
            //                isRedeemed = jsonObject.getBoolean(Type.IS_REDEEMED);
            //            }
            //
            //            if (jsonObject.has(Type.IS_EXPIRED))
            //            {
            //                isExpired = jsonObject.getBoolean(Type.IS_EXPIRED);
            //            }

            coupon = new Coupon(userCouponCode, amount, title, validFrom, //
                validTo, amountMinimum, isDownloaded, availableItem, //
                serverDate, couponCode, stayFrom, stayTo, //
                downloadedAt, availableInDomestic, availableInOverseas, //
                availableInHotel, availableInGourmet);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return coupon;
    }

    public static ArrayList<CouponHistory> getCouponHistoryList(JSONObject response)
    {
        ArrayList<CouponHistory> list = new ArrayList<>();

        try
        {
            boolean hasData = response.has("data");
            if (hasData == true)
            {
                JSONObject data = response.getJSONObject("data");
                if (data != null)
                {
                    JSONArray couponList = data.getJSONArray(Type.COUPON_LIST);

                    int length = couponList.length();
                    for (int i = 0; i < length; i++)
                    {
                        JSONObject jsonObject = couponList.getJSONObject(i);

                        CouponHistory couponHistory = getCouponHistory(jsonObject);
                        if (couponHistory != null)
                        {
                            list.add(couponHistory);
                        }
                    }
                }
            } else
            {
                ExLog.d("response has not data");
            }

        } catch (JSONException | NullPointerException e)
        {
            ExLog.e(e.getMessage());

        }

        return list;
    }

    private static CouponHistory getCouponHistory(JSONObject jsonObject)
    {
        CouponHistory couponHistory = null;

        boolean availableInDomestic = false;
        boolean availableInOverseas = false;
        boolean availableInHotel = false;
        boolean availableInGourmet = false;

        try
        {
            boolean isExpired = jsonObject.getBoolean(Type.IS_EXPIRED); // 유효기간 만료 여부
            boolean isRedeemed = jsonObject.getBoolean(Type.IS_REDEEMED); // 사용 여부

            int amount = jsonObject.getInt(Type.AMOUNT); // 쿠폰가격
            int amountMinimum = jsonObject.getInt(Type.AMOUNT_MINIMUM); // 최소 결제 금액

            String validFrom = jsonObject.getString(Type.VALID_FROM); // 쿠폰 시작 시간
            String validTo = jsonObject.getString(Type.VALID_TO); // 유효기간, 만료일, 쿠폰 만료시간
            String title = jsonObject.getString(Type.TITLE);
            String disabledAt = jsonObject.getString(Type.DISABLED_AT); // 사용한 날짜 (ISO-8601)


            if (jsonObject.has(Type.AVAILABLE_IN_DOMESTIC))
            {
                availableInDomestic = jsonObject.getBoolean(Type.AVAILABLE_IN_DOMESTIC);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_OVERSEAS))
            {
                availableInOverseas = jsonObject.getBoolean(Type.AVAILABLE_IN_OVERSEAS);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_HOTEL))
            {
                availableInHotel = jsonObject.getBoolean(Type.AVAILABLE_IN_HOTEL);
            }

            if (jsonObject.has(Type.AVAILABLE_IN_GOURMET))
            {
                availableInGourmet = jsonObject.getBoolean(Type.AVAILABLE_IN_GOURMET);
            }

            couponHistory = new CouponHistory(amount, title, validFrom, validTo, //
                amountMinimum, isExpired, isRedeemed, disabledAt, availableInDomestic, //
                availableInOverseas, availableInHotel, availableInGourmet);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return couponHistory;
    }

    public static String getAvailableDatesString(String startTime, String endTime)
    {
        String availableDatesString = "";

        try
        {
            //            String strStart = Util.simpleDateFormatISO8601toFormat(startTime, "yyyy.MM.dd");
            //            String strEnd = Util.simpleDateFormatISO8601toFormat(endTime, "yyyy.MM.dd");

            String strStart = DailyCalendar.convertDateFormatString(startTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            String strEnd = DailyCalendar.convertDateFormatString(endTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            availableDatesString = String.format("%s - %s", strStart, strEnd);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return availableDatesString;
    }

    /**
     * 남은 날 수를 리턴하는 메소드
     *
     * @param serverDate 리스트에서 받은 현재 서버시간 serverDate
     * @param validTo    리스트에서 받은 종료 일자
     * @return -1 기간만료, 0 당일만료, 그 이외 숫자 남은 일자
     */
    public static int getDueDateCount(String serverDate, String validTo)
    {
        int dayCount = -1;
        Date currentDate;
        Date endDate;

        try
        {
            //            currentDate = Util.getISO8601Date(serverDate);
            currentDate = DailyCalendar.convertDate(serverDate, DailyCalendar.ISO_8601_FORMAT);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            currentDate = new Date();
        }

        try
        {
            //            endDate = Util.getISO8601Date(validTo);
            endDate = DailyCalendar.convertDate(validTo, DailyCalendar.ISO_8601_FORMAT);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            endDate = new Date();
        }

        long gap = endDate.getTime() - currentDate.getTime();
        if (gap <= 0)
        {
            // 기간 만료 상품
            ExLog.d("already expired");
            return dayCount;
        } else
        {
            // 금일 만료를 제외한 날짜의 경우 내일이 2일 남음이기때문에 1을 더해줘야 함
            dayCount = (int) (gap / MILLISECOND_IN_A_DAY) + 1;
            return dayCount;
        }
    }

    public static String getDateOfStayAvailableString(Context context, String startTime, String endTime)
    {
        String availableDatesString = "";

        try
        {
            String strStart = DailyCalendar.convertDateFormatString(startTime, DailyCalendar.ISO_8601_FORMAT, "MM.dd");
            String strEnd = DailyCalendar.convertDateFormatString(endTime, DailyCalendar.ISO_8601_FORMAT, "MM.dd");

            availableDatesString = context.getString(R.string.coupon_date_of_stay_available_text, strStart, strEnd);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return availableDatesString;
    }
}
