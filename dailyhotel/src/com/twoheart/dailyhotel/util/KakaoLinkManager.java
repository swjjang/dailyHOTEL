package com.twoheart.dailyhotel.util;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.twoheart.dailyhotel.model.HotelDetail;

import android.content.Context;

public class KakaoLinkManager implements Constants{
	
	public static KakaoLinkManager newInstance(Context context) {
		return new KakaoLinkManager(context);
	}
	
	private KakaoLink kkLink;
	private KakaoTalkLinkMessageBuilder kkMsgBuilder;
	private Context mContext;
	
	private KakaoLinkManager(Context context) {
		try {
			mContext = context;
			kkLink = KakaoLink.getKakaoLink(mContext);
			kkMsgBuilder = kkLink.createKakaoTalkLinkMessageBuilder();
			
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	public void sendInviteMsgKakaoLink(String text) {
		try {
			//
			kkMsgBuilder.addText(text);
//			kkMsgBuilder.addText("QWER");
//			kkMsgBuilder.addImage("http://img4.wikia.nocookie.net/__cb20120206123643/pokemon/ko/images/0/04/%EB%A0%88%EB%93%9C%EC%9D%98_%ED%94%BC%EC%B9%B4%EC%B8%84.png", 300, 200);
//			kkMsgBuilder.addText("ASDF");
			kkMsgBuilder.addAppButton("æ€¿∏∑Œ ¿Ãµø");
			kkLink.sendMessage(kkMsgBuilder.build(), mContext);
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	public void shareHotelInfo(HotelDetail hotel) {
		try {
			kkMsgBuilder.addAppButton("æ€¿∏∑Œ ¿Ãµø");
			kkMsgBuilder.addImage(hotel.getHotel().getImage(), 300, 200);
			kkMsgBuilder.addText(hotel.getHotel().getName());
			kkLink.sendMessage(kkMsgBuilder.build(), mContext);
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	
}
