package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public void sendInviteKakaoLink(String text)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
            messageBuilder.addImage("http://s3-ap-northeast-1.amazonaws.com/weblogfile/kakao01.jpg", 300, 400);
            messageBuilder.addText(text);
            messageBuilder.addAppButton(mContext.getString(R.string.kakao_btn_invited_friend));
            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (KakaoParameterException e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareHotel(String name, String hotelName, String address, int hotelIndex, String imageUrl, SaleTime checkInSaleTime, int nights)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String date = checkInSaleTime.getDailyDateFormat("yyyyMMdd");
            String schemeParams = String.format("view=hotel&idx=%d&date=%s&nights=%d", hotelIndex, date, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_hotel), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.KOREA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date checkInDate = checkInSaleTime.getDayOfDaysDate();
            Date chekcOutDate = new Date(checkInSaleTime.getDayOfDaysDate().getTime() + SaleTime.MILLISECOND_IN_A_DAY * nights);

            String text = mContext.getString(R.string.kakao_btn_share_hotel, name, hotelName//
                , simpleDateFormat.format(checkInDate), simpleDateFormat.format(chekcOutDate)//
                , nights, nights + 1, address);

            if (Util.isTextEmpty(imageUrl) == false)
            {
                messageBuilder.addImage(imageUrl, 300, 200);
            }

            messageBuilder.addText(text);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareGourmet(String name, String planceName, String address, int index, String imageUrl, SaleTime saleTime)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String date = saleTime.getDailyDateFormat("yyyyMMdd");
            String schemeParams = String.format("view=gourmet&idx=%d&date=%s&nights=1", index, date);

            messageBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_fnb), new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build()).addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date checkInDate = saleTime.getDayOfDaysDate();

            String text = mContext.getString(R.string.kakao_btn_share_fnb, name, planceName//
                , simpleDateFormat.format(checkInDate), address);

            if (Util.isTextEmpty(imageUrl) == false)
            {
                messageBuilder.addImage(imageUrl, 300, 200);
            }

            messageBuilder.addText(text);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
