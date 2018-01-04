package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
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
            String checkInDay = stayBookingDay.getCheckInDay("yyyyMMdd");
            int nights = stayBookingDay.getNights();
            String schemeParams = String.format(Locale.KOREA, "vc=5&v=hd&i=%d&d=%s&n=%d", hotelIndex, checkInDay, nights);
            String text = mContext.getString(R.string.kakao_btn_share_hotel, name, hotelName//
                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)"), stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"), nights, nights + 1, address);

            LocationTemplate params = LocationTemplate.newBuilder(address,
                ContentObject.newBuilder(hotelName,
                    imageUrl,
                    LinkObject.newBuilder()
                        .setWebUrl("https://mobile.dailyhotel.co.kr/stay/" + hotelIndex)
                        .setMobileWebUrl("https://mobile.dailyhotel.co.kr/stay/" + hotelIndex)
                        .setAndroidExecutionParams(schemeParams)
                        .setIosExecutionParams(schemeParams)
                        .build())
                    .setDescrption(text)
                    .build())
                .addButton(new ButtonObject("웹으로 보기", LinkObject.newBuilder()
                    .setWebUrl("https://mobile.dailyhotel.co.kr/stay/" + hotelIndex)
                    .setMobileWebUrl("https://mobile.dailyhotel.co.kr/stay/" + hotelIndex)
                    .build()))
                .setAddressTitle(hotelName)
                .build();

            KakaoLinkService.getInstance().sendDefault(mContext, params, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    ExLog.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {

                }
            });


//
//
//            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
//
////            String checkInDay = stayBookingDay.getCheckInDay("yyyyMMdd");
////            int nights = stayBookingDay.getNights();
////            String schemeParams = String.format(Locale.KOREA, "vc=5&v=hd&i=%d&d=%s&n=%d", hotelIndex, checkInDay, nights);
//
//            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app), //
//                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
//                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());
//
////            String text = mContext.getString(R.string.kakao_btn_share_hotel, name, hotelName//
////                , stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)"), stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)"), nights, nights + 1, address);
//
//            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
//            {
//                int lastSlash = imageUrl.lastIndexOf('/');
//                String fileName = imageUrl.substring(lastSlash + 1);
//                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
//            }
//
//            messageBuilder.addText(text);
//
//            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/stay/" + hotelIndex);
//
//            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareStay(String name, String hotelName, String address, int hotelIndex, String imageUrl, StayBookDateTime stayBookDateTime)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String checkInDay = stayBookDateTime.getCheckInDateTime("yyyyMMdd");
            int nights = stayBookDateTime.getNights();
            String schemeParams = String.format(Locale.KOREA, "vc=5&v=hd&i=%d&d=%s&n=%d", hotelIndex, checkInDay, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_hotel, name, hotelName//
                , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"), stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), nights, nights + 1, address);

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(text);

            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/stay/" + hotelIndex);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareStayOutbound(String name, String hotelName, String address, int hotelIndex, String imageUrl, StayBookDateTime stayBookDateTime)
    {
        if (DailyTextUtils.isTextEmpty(name, hotelName, address) == true || stayBookDateTime == null)
        {
            return;
        }

        try
        {
            FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("딸기 치즈 케익",
                    "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com").build())
                    .setDescrption("#케익 #딸기 #삼평동 #카페 #분위기 #소개팅")
                    .build())
                .setSocial(SocialObject.newBuilder().setLikeCount(286).setCommentCount(45)
                    .setSharedCount(845).build())
                .addButton(new ButtonObject("웹으로 보기", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                    .setWebUrl("https://developers.kakao.com")
                    .setMobileWebUrl("https://developers.kakao.com")
                    .setAndroidExecutionParams("key1=value1")
                    .setIosExecutionParams("key1=value1")
                    .build()))
                .build();


            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String checkInDay = stayBookDateTime.getCheckInDateTime("yyyyMMdd");
            int nights = stayBookDateTime.getNights();
            String schemeParams = String.format(Locale.KOREA, "vc=20&v=pd&pt=stayOutbound&i=%d&d=%s&n=%d", hotelIndex, checkInDay, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_stay_outbound, name, hotelName//
                , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)"), stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), nights, nights + 1, address);

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
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

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(message);

            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/stay/" + stayIndex);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareBookingStayOutbound(String message, int stayIndex, String imageUrl, String checkInDate, int nights)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String schemeParams = String.format(Locale.KOREA, "vc=20&v=pd&pt=stayOutbound&i=%d&d=%s&n=%d", stayIndex, checkInDate, nights);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app), //
                new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
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

    public void shareBookingCancelStay(String message, String imageUrl)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
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

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app)//
                , new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()//
                    .setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_fnb, name, placeName//
                , gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)"), address);

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(text);

            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/gourmet/" + index);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareGourmet(String name, String placeName, String address, int index, String imageUrl, GourmetBookDateTime gourmetBookDateTime)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            String date = gourmetBookDateTime.getVisitDateTime("yyyyMMdd");
            String schemeParams = String.format(Locale.KOREA, "vc=5&v=gd&i=%d&d=%s", index, date);

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app)//
                , new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()//
                    .setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            String text = mContext.getString(R.string.kakao_btn_share_fnb, name, placeName//
                , gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"), address);

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(text);

            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/gourmet/" + index);

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

            messageBuilder.addAppButton(mContext.getString(R.string.label_kakao_mobile_app)//
                , new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()//
                    .setExecuteParam(schemeParams).build())//
                    .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
            {
                int lastSlash = imageUrl.lastIndexOf('/');
                String fileName = imageUrl.substring(lastSlash + 1);
                messageBuilder.addImage(imageUrl.substring(0, lastSlash + 1) + URLEncoder.encode(fileName), 300, 200);
            }

            messageBuilder.addText(message);

            messageBuilder.addWebLink(mContext.getString(R.string.label_kakao_web_link), "https://mobile.dailyhotel.co.kr/gourmet/" + index);

            mKakaoLink.sendMessage(messageBuilder, mContext);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void shareBookingCancelGourmet(String message, String imageUrl)
    {
        try
        {
            KakaoTalkLinkMessageBuilder messageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();

            if (DailyTextUtils.isTextEmpty(imageUrl) == false)
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
