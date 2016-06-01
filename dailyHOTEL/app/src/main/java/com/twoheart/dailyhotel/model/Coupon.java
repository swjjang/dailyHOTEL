package com.twoheart.dailyhotel.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Date;

/**
 * Created by Sam Lee on 2016. 5. 19..
 * <p/>
 * 현재 임의로 만든 모델로 실제 서버에서 내려오는 값에 따라 변경 될 수 있음
 */
public class Coupon implements Parcelable
{
    // 하루
    public static final long MILLISECOND_IN_A_DAY = 3600 * 24 * 1000;

    // coupon object type
    public static final String CODE = "code";

    // coupon object type
    public static final String VALID_TO = "validTo";

    // coupon object type
    public static final String VALID_FROM = "validFrom";

    // coupon object type
    public static final String TITLE = "title";

    // coupon object type
    public static final String WARNING = "warning";

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

    private String code; // 쿠폰 별칭 코드
    private int amount; // 쿠폰금액
    private String title; //설명 ??  있을지수도 있고 없을수도 있음???
    private String validFrom; // 시작시간
    private String validTo; // 만료시간
    private int amountMinimum; // 최소주문금액
    private String isDownloaded; // 상태표시
    private String availableItem; // 사용가능처
    private String warring; // 유의사항 , 노출여부로만 사용될수도...
    private String serverDate; // 서버시간

    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon(String code, int amount, String title, String validFrom, String validTo, //
                  int amountMinimum, String isDownloaded, String availableItem, String warring, //
                  String serverDate)
    {
        this.code = code;
        this.amount = amount;
        this.title = title;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.amountMinimum = amountMinimum;
        this.isDownloaded = isDownloaded;
        this.availableItem = availableItem;
        this.warring = warring;
        this.serverDate = serverDate;
    }

    public String getCode()
    {
        return code;
    }

    public int getAmount()
    {
        return amount;
    }

    public String getTitle()
    {
        return title;
    }

    public String getValidFrom()
    {
        return validFrom;
    }

    public String getValidTo()
    {
        return validTo;
    }

    public int getAmountMinimum()
    {
        return amountMinimum;
    }

    public String isDownloaded()
    {
        return isDownloaded;
    }

    public String getAvailableItem()
    {
        return availableItem;
    }

    public String getWarring()
    {
        return warring;
    }

    public String getServerDate()
    {
        return serverDate;
    }

    public String getExpiredString(String startTime, String endTime)
    {
        String expireString = "";

        try
        {
            String strStart = Util.simpleDateFormatISO8601toFormat(startTime, "yyyy.MM.dd");
            String strEnd = Util.simpleDateFormatISO8601toFormat(endTime, "yyyy.MM.dd");
            expireString = String.format("%s ~ %s", strStart, strEnd);

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return expireString;
    }

    /**
     * 남은 날 수를 리턴하는 메소드
     *
     * @param coupon
     * @return -1 기간만료, 0 당일만료, 그 이외 숫자 남은 일자
     */
    public int getDueDate(Coupon coupon)
    {
        int dayCount = -1;
        Date serverDate;
        Date endDate;
        try
        {
            serverDate = Util.getISO8601Date(coupon.serverDate);
            endDate = Util.getISO8601Date(coupon.validTo);
        } catch (Exception e)
        {
            ExLog.e(e.getMessage());

            serverDate = new Date();
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
            dayCount = (int) (gap / MILLISECOND_IN_A_DAY);
            return dayCount;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(code);
        dest.writeInt(amount);
        dest.writeString(title);
        dest.writeString(validFrom);
        dest.writeString(validTo);
        dest.writeInt(amountMinimum);
        dest.writeString(isDownloaded);
        dest.writeString(availableItem);
        dest.writeString(warring);
        dest.writeString(serverDate);
    }

    private void readFromParcel(Parcel in)
    {
        code = in.readString();
        amount = in.readInt();
        title = in.readString();
        validFrom = in.readString();
        validTo = in.readString();
        amountMinimum = in.readInt();
        isDownloaded = in.readString();
        availableItem = in.readString();
        warring = in.readString();
        serverDate = in.readString();
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
