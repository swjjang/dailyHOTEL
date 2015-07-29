package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.twoheart.dailyhotel.R;

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

	//	public void shareHotelInfo(HotelDetail hotel, String region)
	//	{
	//		try
	//		{
	//			/**
	//			 * TODO : 공유하기 시에 정확하게 공유를 하였는지 카운트를 하기 힘듬. 카카오링크에서 콜백을 미지원. 공유버튼을
	//			 * 눌렀을 경우를 카운트하면 정확하지 않음.(중간에 공유 안할수도)
	//			 */
	//			String schemeParams = "hotelIdx=" + hotel.getHotel().getIdx() + "&region=" + region;
	//			ExLog.e("sche : " + schemeParams);
	//
	//			kkMsgBuilder.addAppButton(mContext.getString(R.string.kakao_btn_move), new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam(schemeParams).build()).addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam(schemeParams).build()).build());
	//			kkMsgBuilder.addImage(hotel.getHotel().getImage(), 300, 200);
	//			kkMsgBuilder.addText(hotel.getHotel().getName());
	//			kkLink.sendMessage(kkMsgBuilder.build(), mContext);
	//			ExLog.e("shareHotelInfo : schmeParams = " + schemeParams);
	//
	//		} catch (KakaoParameterException e)
	//		{
	//			ExLog.e(e.toString());
	//		}
	//	}

}
