package com.twoheart.dailyhotel.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONObject;

import android.content.Context;

import com.kakao.AppActionBuilder;
import com.kakao.AppActionInfoBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;

public class KakaoLinkManager implements Constants
{
	private static final String TAG = "KakaloLinkManager";

	public static KakaoLinkManager newInstance(Context context)
	{
		return new KakaoLinkManager(context);
	}

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

	public void sendInviteMsgKakaoLink(String text)
	{
		try
		{
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
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("hotelIndex", hotelIndex);
			jsonObject.put("dailyTime", String.valueOf(dailyTime));
			jsonObject.put("dailyDayOfDays", dailyDayOfDays);
			jsonObject.put("nights", nights);

			String schemeParams = jsonObject.toString();

			kkMsgBuilder.addAppButton(mContext.getString(R.string.kakao_btn_go_hotel), new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build()).addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());
			kkMsgBuilder.addImage(imageUrl, 300, 200);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd", Locale.KOREA);
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Date checkInDate = new Date(dailyTime + SaleTime.SECONDS_IN_A_DAY * dailyDayOfDays * 1000);
			Date chekcOutDate = new Date(dailyTime + SaleTime.SECONDS_IN_A_DAY * (dailyDayOfDays + nights) * 1000);

			String text = String.format("%s\n날짜 : %s - %s", hotelName, simpleDateFormat.format(checkInDate), simpleDateFormat.format(chekcOutDate));

			kkMsgBuilder.addText(text);
			kkLink.sendMessage(kkMsgBuilder.build(), mContext);
		} catch (Exception e)
		{
			ExLog.e(e.toString());
		}
	}

}
