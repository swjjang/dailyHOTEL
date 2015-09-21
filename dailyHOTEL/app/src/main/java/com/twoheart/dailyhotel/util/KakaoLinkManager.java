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
    private static final String TAG = "KakaloLinkManager";
    private KakaoLink kkLink;
    private KakaoTalkLinkMessageBuilder kkMsgBuilder;
    private Context mContext;

    private KakaoLinkManager(Context context)
    {
        try
        {
            mContext = context;
            kkLink = KakaoLink.getKakaoLink(mContext);
            kkMsgBuilder = kkLink.createKakaoTalkLinkMessageBuilder();
        } catch (KakaoParameterException e)
        {
            ExLog.e(e.toString());
        }
    }

    public static KakaoLinkManager newInstance(Context context)
    {
        return new KakaoLinkManager(context);
    }

    public void sendInviteMsgKakaoLink(String text)
    {
        try
        {
            kkMsgBuilder.addImage("http://s3-ap-northeast-1.amazonaws.com/weblogfile/kakao.jpg", 300, 200);
            kkMsgBuilder.addText(text);
            kkMsgBuilder.addAppButton(mContext.getString(R.string.kakao_btn_invited_friend));
            kkLink.sendMessage(kkMsgBuilder.build(), mContext);
        } catch (KakaoParameterException e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareHotel(String hotelName, int hotelIndex, String imageUrl, long dailyTime, int dailyDayOfDays, int nights)
    {
        try
        {
            String schemeParams = String.format("hotelIndex=%d&dailyTime=%d&dailyDayOfDays=%d&nights=%d", hotelIndex, dailyTime, dailyDayOfDays, nights);

            kkMsgBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_hotel), new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build()).addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd", Locale.KOREA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date checkInDate = new Date(dailyTime + SaleTime.SECONDS_IN_A_DAY * dailyDayOfDays * 1000);
            Date chekcOutDate = new Date(dailyTime + SaleTime.SECONDS_IN_A_DAY * (dailyDayOfDays + nights) * 1000);

            String text = mContext.getString(R.string.kakao_btn_share_hotel, hotelName, simpleDateFormat.format(checkInDate), simpleDateFormat.format(chekcOutDate), nights, nights + 1);

            if (Util.isTextEmpty(imageUrl) == false)
            {
                kkMsgBuilder.addImage(imageUrl, 300, 200);
            }

            kkMsgBuilder.addText(text);

            kkLink.sendMessage(kkMsgBuilder.build(), mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareFnB(String name, int index, String imageUrl, long dailyTime, int dailyDayOfDays)
    {
        try
        {
            String schemeParams = String.format("fnbIndex=%d&dailyTime=%d&dailyDayOfDays=%d&nights=%d", index, dailyTime, dailyDayOfDays, 0);

            kkMsgBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_fnb), new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build()).addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date checkInDate = new Date(dailyTime + SaleTime.SECONDS_IN_A_DAY * dailyDayOfDays * 1000);

            String text = mContext.getString(R.string.kakao_btn_share_fnb, name, simpleDateFormat.format(checkInDate));

            if (Util.isTextEmpty(imageUrl) == false)
            {
                kkMsgBuilder.addImage(imageUrl, 300, 200);
            }

            kkMsgBuilder.addText(text);

            kkLink.sendMessage(kkMsgBuilder.build(), mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

}
