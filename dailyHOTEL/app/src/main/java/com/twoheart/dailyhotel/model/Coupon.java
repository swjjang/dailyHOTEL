package com.twoheart.dailyhotel.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.text.SimpleDateFormat;
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

    public String code; // 쿠폰 별칭 코드
    public int amount; // 쿠폰금액
    public String title; //설명 ??  있을지수도 있고 없을수도 있음???
    public String validFrom; // 시작시간
    public String validTo; // 만료시간
    public int amountMinimum; // 최소주문금액
    public String isDownloaded; // 상태표시
    public String useablePlace; // 사용가능처
    public String warring; // 유의사항 , 노출여부로만 사용될수도...

    public Coupon(Parcel in)
    {
        readFromParcel(in);
    }

    public Coupon(String code, int amount, String title, String validFrom, String validTo, int amountMinimum, String isDownloaded, String useablePlace, String warring)
    {
        this.code = code;
        this.amount = amount;
        this.title = title;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.amountMinimum = amountMinimum;
        this.isDownloaded = isDownloaded;
        this.useablePlace = useablePlace;
        this.warring = warring;
    }

    public String getExpiredString(String startTime, String endTime)
    {
        String expireString = "";
        try
        {
            Date startDate = Util.getISO8601Date(startTime);
            Date endDate = Util.getISO8601Date(startTime);

            //        Date startDate = new Date(startTime);
            //        Date endDate = new Date(endTime);


            String strStart = getTimezoneDateFormat("yyyy.MM.dd").format(startDate);
            String strEnd = getTimezoneDateFormat("yyyy.MM.dd").format(endDate);
            expireString = strStart + " ~ " + strEnd;

        } catch (Exception e)
        {
            ExLog.e(e.getMessage());
        }

        return expireString;
    }

    /**
     * 남은 날 수를 리턴하는 메소드
     *
     * @param context
     * @param currentTime 현재시간
     * @param endTime     쿠폰 만료일
     * @return -1 기간만료, 0 당일만료, 그 이외 숫자 남은 일자
     */
    public int getDueDate(Context context, String currentTime, String endTime)
    {
        int dayCount = -1;
        Date currentDate;
        Date endDate;
        try
        {
            currentDate = Util.getISO8601Date(currentTime);
            endDate = Util.getISO8601Date(endTime);
        } catch (Exception e) {
            ExLog.e(e.getMessage());

            currentDate = new Date();
            endDate = new Date();
        }

        long gap = endDate.getTime() - currentDate.getTime();

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


    public SimpleDateFormat getTimezoneDateFormat(String datePattern)
    {

        SimpleDateFormat format = new SimpleDateFormat(datePattern);
        return format;
//        SimpleDateFormat sFormat = new SimpleDateFormat(datePattern, Locale.KOREA);
//        sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        return sFormat;
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
        dest.writeString(useablePlace);
        dest.writeString(warring);
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
        useablePlace = in.readString();
        warring = in.readString();
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
