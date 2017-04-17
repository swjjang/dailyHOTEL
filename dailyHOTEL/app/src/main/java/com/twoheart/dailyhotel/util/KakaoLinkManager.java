package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.daily.base.util.ExLog;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;

import java.net.URLEncoder;
import java.util.Locale;

public class KakaoLinkManager implements Constants
{
    private KakaoLink mKakaoLink;
    private Context mContext;

    private KakaoLinkManager(Context context)
    {
        try
        {
            mContext = context;
            mKakaoLink = KakaoLink.getKakaoLink(mContext);
        } catch (KakaoParameterException e)
        {
            ExLog.e(e.toString());
        }
    }

    public static KakaoLinkManager newInstance(Context context)
    {
        return new KakaoLinkManager(context);
    }

    public void sendInviteKakaoLink(String text, String recommendCode)
    {
        final String URL = "https://app.adjust.com/lkhiuk?campaign=referral-in_app&adgroup=invite_friend&creative=app_download&deep_link=dailyhotel%3A%2F%2Fdailyhotel.co.kr%3Fvc%3D6%26v%3Dsu%26rc%3D" + recommendCode;

        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
            messageBuilder.addImage("http://img.dailyhotel.me/app_static/kakao01.jpg", 300, 400);
            messageBuilder.addText(text);
            messageBuilder.addWebButton(mContext.getString(R.string.kakao_btn_invited_friend), URL);
            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (KakaoParameterException e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareStay(String name, String hotelName, String address, int hotelIndex, String imageUrl, StayBookingDay stayBookingDay)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String checkInDay = stayBookingDay.getCheckInDay("yyyyMMdd");
            int nights = stayBookingDay.getNights();
            String schemeParams = String.format(Locale.KOREA, "vc=5&v=hd&i=%d&d=%s&n=%d", hotelIndex, checkInDay, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_hotel), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_hotel, name, hotelName//
                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)"), stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"), nights, nights + 1, address);

            if (com.daily.base.util.TextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(text);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareBookingStay(String message, int stayIndex, String imageUrl, String checkInDate, int nights)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String schemeParams = String.format(Locale.KOREA, "vc=5&v=hd&i=%d&d=%s&n=%d", stayIndex, checkInDate, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_reservation_stay), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            if (com.daily.base.util.TextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(message);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareGourmet(String name, String placeName, String address, int index, String imageUrl, GourmetBookingDay gourmetBookingDay)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String date = gourmetBookingDay.getVisitDay("yyyyMMdd");
            String schemeParams = String.format(Locale.KOREA, "vc=5&v=gd&i=%d&d=%s", index, date);

            messageBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_fnb)//
                , new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()//
                    .setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_fnb, name, placeName//
                , gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)"), address);

            if (com.daily.base.util.TextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(text);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareBookingGourmet(String message, int index, String imageUrl, String reservationDate)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String schemeParams = String.format(Locale.KOREA, "vc=5&v=gd&i=%d&d=%s", index, reservationDate);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_reservation_gourmet)//
                , new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()//
                    .setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            if (com.daily.base.util.TextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(message);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
