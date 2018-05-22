package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.util.Pair;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.domain.StayInterface;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.RoomImageInformation;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDetailk;
import com.daily.dailyhotel.entity.StayFilterCount;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.entity.TrueReviews;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.repository.remote.model.RoomImageInformationData;
import com.daily.dailyhotel.repository.remote.model.SubwayAreasData;
import com.daily.dailyhotel.repository.remote.model.TrueVRData;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class StayRemoteImpl extends BaseRemoteImpl implements StayInterface
{
    @Override
    public Observable<Stays> getList(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType)
    {
        return mDailyMobileService.getStayList(getBaseUrl(context) + getListApiUrl(context, categoryType) + toStringQueryParams(queryMap, abTestType)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                Stays stays;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stays = baseDto.data.getStays();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stays;
            });
    }

    @Override
    public Observable<Stays> getLocalPlusList(Context context, Map<String, Object> queryMap)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/local-plus" //
            : "NjYkNjckMjgkMjQkMzIkNzYkNjIkMjEkNTQkODAkNjYkMTQkNTMkODgkODAkNzQk$MDQzOThGREU2NzXZGMjFGNJjhFWRTg5AOEYI1MTIxRUZBNTdFQUU4FNESRCRDkyRkRNCVQTNCNTzZXlBQZ0RRKDNEYGxRjZBN0VFMQ==$";

        return mDailyMobileService.getStayList(getBaseUrl(context) + Crypto.getUrlDecoderEx(API) + toStringQueryParams(queryMap, null)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                Stays stays;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stays = baseDto.data.getStays();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stays;
            });
    }

    @Override
    public Observable<StayFilterCount> getListCountByFilter(Context context, DailyCategoryType categoryType, Map<String, Object> queryMap, String abTestType)
    {
        return mDailyMobileService.getStayListCountByFilter(getBaseUrl(context) + getListApiUrl(context, categoryType) + toStringQueryParams(queryMap, abTestType)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayFilterCount stayFilterCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stayFilterCount = baseDto.data.getFilterCount();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayFilterCount;
            });
    }

    @Override
    public Observable<StayFilterCount> getLocalPlusListCountByFilter(Context context, Map<String, Object> queryMap)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotels/sales/local-plus" //
            : "NjYkNjckMjgkMjQkMzIkNzYkNjIkMjEkNTQkODAkNjYkMTQkNTMkODgkODAkNzQk$MDQzOThGREU2NzXZGMjFGNJjhFWRTg5AOEYI1MTIxRUZBNTdFQUU4FNESRCRDkyRkRNCVQTNCNTzZXlBQZ0RRKDNEYGxRjZBN0VFMQ==$";

        return mDailyMobileService.getStayListCountByFilter(getBaseUrl(context) + Crypto.getUrlDecoderEx(API) + toStringQueryParams(queryMap, null)) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayFilterCount stayFilterCount;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        stayFilterCount = baseDto.data.getFilterCount();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayFilterCount;
            });
    }

    private String getListApiUrl(Context context, DailyCategoryType categoryType)
    {
        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotels/sales"//
                : "NjAkMzUkMjAkMzQkOSQ2NiQzMiQxMiQ3MSQzNCQ5MyQzOSQ1MyQ5MiQ2NiQzMiQ=$MUEzNjQ3NMUQE2RkFCM0IxHMDFERDA0MYDNLU3OEWTEF2Mjg4MDAxMYDk3QzdENTQ4MWTdDANKzYE5NDhBQUQyQTI5N0FFLOTcN4OA==$";

            return Crypto.getUrlDecoderEx(API);
        } else
        {
            final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotels/category/{categoryAsPath}/sales"//
                : "OTYkNjYkMzAkMTMkNzYkODEkNjQkNiQ0JDEyOSQ1OCQzMiQ3NCQxMTAkNDkkOTIk$MUFCENjEJEQThEOITdBNTZFOUJEMUUzOXUFJDQTI5ODM4RDU0AMTZEOUE0NjOZDMUZGNjJYwQjRGdBQjQ5QOzI1NZ0QzVRDMzNTdFREYwRjZENQkYFFRDQwREQxN0UyQjA2MzMyANTY0NTY3$";

            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("{categoryAsPath}", categoryType.getCodeString(context));

            return Crypto.getUrlDecoderEx(API, urlParams);
        }
    }

    @Override
    public Observable<StayDetailk> getDetail(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        //        String jsonString = "{\"category\":\"hotel\",\"idx\":1158,\"name\":\"롯데호텔 서울\",\"grade\":\"special1\",\"discount\":239500,\"primaryReview\":{\"userId\":\"rob**\",\"comment\":\"편히 쉬었습니다. 조식 가격이 넘 비싸요.\",\"createdAt\":\"2017-03-05T19:34:24+09:00\",\"avgScore\":4.8,\"reviewReply\":null,\"itemName\":\"[DAILY Sweet Berry PKG] 디럭스 싱글 + 싱글\"},\"provideRewardSticker\":false,\"coupon\":{},\"rating\":{\"persons\":769,\"values\":91,\"show\":true},\"configurations\":{\"activeReward\":true},\"checkTime\":{\"checkIn\":\"00:00:00\",\"checkOut\":\"00:00:00\",\"description\":\"\"},\"address\":\"서울특별시 중구 을지로 30\",\"singleStay\":false,\"facilities\":[\"Parking\",\"NoParking\",\"Pool\",\"Fitness\",\"Pet\",\"PrivateBbq\",\"SeminarRoom\",\"SpaMassage\",\"Restaurant\",\"Cafeteria\",\"KidsPlayroom\",\"Sauna\",\"BusinessCenter\",\"ClubLounge\",\"PickupAvailable\",\"WiFi\",\"RentBabyBed\",\"ConvenienceStore\",\"SharedBbq\"],\"benefit\":{\"title\":\"합리적 가격! 봄 맞이 PKG 판매 중!\",\"contents\":[\"총 4인 무료 투숙 제공 (매트리스 2인용 무료 제공)\",\"해당 특전은 DAILY Amazing Party PKG를 구매하신 분께만 제공됩니다.\"]},\"details\":[{\"type\":\"HOTEL_INFORMATION\",\"title\":\"호텔 정보\",\"contents\":[\"Bath Amenity 구비 (칫솔 및 치약 제외)\",\" 전 객실 금연실\",\" 합리적인 가격에 도심속 여유로운 휴식처를 찾는 분들에게 적합\"]},{\"type\":\"HOTEL_INFORMATION\",\"title\":\"인원 정보\",\"contents\":[\"인원 추가 불가\"]},{\"type\":\"HOTEL_INFORMATION\",\"title\":\"부대시설 정보\",\"contents\":[\"비즈니스코너, 체련장 : 투숙객 무료\"]},{\"type\":\"HOTEL_INFORMATION\",\"title\":\"확인사항\",\"contents\":[\"상기 이미지와 다른 객실로 배정될 수 있습니다.\",\" '체크인 시 배정'의 경우 특정 객실과 베드타입을 보장하지 않습니다. \"]}],\"dailyComment\":{\"type\":\"DAILY_COMMENT\",\"title\":\"데일리's comment\",\"contents\":[\"라이프 스타일 비즈니스 호텔, 이비스 스타일로의 리브랜딩\",\" 코엑스, 잠실, 강남역 등 핫플레이스와 근접하게 위치\",\" 합리적인 가격에 도심속 여유로운 휴식처를 찾는 분들에게 적합\"]},\"checkList\":{\"type\":\"CHECKLIST\",\"title\":\"확인사항\",\"contents\":[\"'체크인 시 배정'의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"상기 이미지와 다른 객실로 배정될 수 있습니다.\",\"미성년자의 경우 보호자 동반일때만 투숙이 가능합니다.\",\"내국인 전용요금이라서 외국인은 투숙이 불가합니다\"]},\"refundPolicy\":{\"type\":\"REFUND_POLICY\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"wishCount\":743,\"roomCount\":0,\"waitingForBooking\":true,\"breakfast\":{\"items\":[{\"minAge\":19,\"maxAge\":-1,\"title\":\"전 연령\",\"amount\":10000}]},\"location\":{\"latitude\":37.56499,\"longitude\":126.981369},\"images\":[{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/00_1.jpg\",\"primary\":true},{\"description\":\"Superior 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/03.jpg\"},{\"description\":\"Superior 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/03_1.png\"},{\"description\":\"Superior 싱글+싱글\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/03_2.png\"},{\"description\":\"Superior 싱글+싱글\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/03_3.png\"},{\"description\":\"Deluxe 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/03_4.jpg\"},{\"description\":\"Deluxe Club 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_3.jpg\"},{\"description\":\"Deluxe Club 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_4.jpg\"},{\"description\":\"Deluxe Club 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_5.jpg\"},{\"description\":\"Deluxe Club 싱글+싱글\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_6.jpg\"},{\"description\":\"Ladies Floor Deluxe club 더블\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_7.jpg\"},{\"description\":\"Ladies Floor Deluxe club 싱글+싱글\",\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/04_8.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/07.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/08.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/09.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/10.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/11.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/12.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/14.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/15.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/16.jpg\"},{\"url\":\"https://img.dailyhotel.me/resources/images/1lotteseoul42/17.jpg\"}],\"province\":{\"idx\":5,\"name\":\"서울\"},\"rooms\":[{\"roomIdx\":273400,\"roomName\":\"[Ladies in the city PKG] Ladies Floor Deluxe club 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"description\":\"Ladies Floor Deluxe club 더블\",\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/273400/03_7.jpg\",\"primary\":true,\"count\":2},\"amount\":{\"discountAverage\":314600,\"discountRate\":0,\"discountTotal\":314600,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":true,\"breakfast\":0},\"benefit\":\"클럽 라운지 3인 + 롯데시네마 일반관람권 3매 +릴리앤펀 패디왁스 캔들1개 + 웰컴커티시 (박수 관계없이 일정 당 1회 제공)\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"12:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":39.66,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":274684,\"roomName\":\"[B&B 패키지] Superior 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/274684/03.jpg\",\"primary\":true,\"count\":4},\"amount\":{\"discountAverage\":314600,\"discountRate\":0,\"discountTotal\":314600,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":false,\"breakfast\":2},\"benefit\":\"라세느 조식 2인 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"12:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":26,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":273018,\"roomName\":\"[Sweet Spring PKG] Superior 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"description\":\"Superior 더블\",\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/273018/03.jpg\",\"primary\":true,\"count\":4},\"amount\":{\"discountAverage\":332700,\"discountRate\":0,\"discountTotal\":332700,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":false,\"breakfast\":2},\"benefit\":\"델리카한스 조각 케이크 1ea & Berry Tea 2ea + 레이트체크아웃 14시 가능 시 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"14:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":26,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":274685,\"roomName\":\"[B&B 패키지] Deluxe 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/274685/03_4.jpg\",\"primary\":true,\"count\":1},\"amount\":{\"discountAverage\":363000,\"discountRate\":0,\"discountTotal\":363000,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":false,\"breakfast\":2},\"benefit\":\"라세느 조식 2인 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"12:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":39.66,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":273398,\"roomName\":\"[Together PKG] Deluxe 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/273398/03_4.jpg\",\"primary\":true,\"count\":1},\"amount\":{\"discountAverage\":369000,\"discountRate\":0,\"discountTotal\":369000,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":true,\"breakfast\":3},\"benefit\":\"보조침대 무료 제공 + 라세느 조식 3인 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"14:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":39.66,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":273019,\"roomName\":\"[Sweet Spring PKG] Deluxe 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"description\":\"Deluxe 더블\",\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/273019/03_4.jpg\",\"primary\":true,\"count\":1},\"amount\":{\"discountAverage\":381100,\"discountRate\":0,\"discountTotal\":381100,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":true,\"breakfast\":2},\"benefit\":\"더 라운지 피크닉 BOX + 레이트체크아웃 14시 가능 시 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"14:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":39.66,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}},{\"roomIdx\":273021,\"roomName\":\"[Sweet Spring PKG] Deluxe Club 체크인 시 배정 \",\"bedInfo\":{\"filters\":[\"TWEEN\",\"DOUBLE\"],\"bedTypes\":[{\"bedType\":\"SINGLE\",\"count\":1},{\"bedType\":\"DOUBLE\",\"count\":2},{\"bedType\":\"DOUBLE\",\"count\":1}]},\"attribute\":{\"roomStructure\":\"ONE_ROOM\",\"isEntireHouse\":false,\"isDuplex\":true},\"bedCount\":1,\"image\":{\"description\":\"Deluxe Club 더블\",\"url\":\"https://img.dailyhotel.me/resources/room/000/001/158/273021/04_3.jpg\",\"primary\":true,\"count\":4},\"amount\":{\"discountAverage\":417400,\"discountRate\":0,\"discountTotal\":417400,\"priceAverage\":0},\"persons\":{\"fixed\":2,\"extra\":1,\"extraCharge\":true,\"breakfast\":2},\"benefit\":\"클럽 라운지 혜택 2인 + 더 라운지 피크닉 BOX + 롯데시네마 일반권 2매 + 스위트 콤보권 1매 + 레이트체크아웃 14시 가능 시 제공\",\"provideRewardSticker\":false,\"checkTime\":{\"checkIn\":\"15:00:00\",\"checkOut\":\"14:00:00\"},\"descriptions\":[\"에어컨이 없는 객실입니다. (여름철 에어컨 없이도 선선한 기온을 유지합니다.)\",\"미취학전 아동만 인원추가 가능\",\"주방식기세트 보증금 10,000원 발생\"],\"squareMeter\":39.66,\"needToKnows\":[\"더블 or 더블+싱글 (객실 현장 임의배정)\",\"’체크인 시 배정’의 경우 특정 객실과 베드타입을 보장하지 않습니다.\",\"고급어메니티 제공\",\"일부 객실 욕조 있음 (객실 현장 임의배정)\"],\"vrs\":{\"vr\":[]},\"refundPolicy\":{\"warning\":\"취소 및 환불 불가 문구\",\"type\":\"nrd\",\"title\":\"취소 및 환불규정\",\"contents\":[\"본 상품은 특가 예약 상품이므로 교환과 취소 및 환불이 불가합니다.\"]},\"amenities\":[\"WiFi\",\"Cooking\",\"Pc\",\"Bath\",\"Tv\",\"SpaWallpool\",\"PrivateBbq\",\"Smokeable\",\"Karaoke\",\"PartyRoom\",\"Amenity\",\"ShowerGown\",\"ToothbrushSet\",\"DisabledFacilities\"],\"roomCharge\":{\"extra\":{\"extraBedEnable\":true,\"extraBed\":20000,\"extraBeddingEnable\":true,\"extraBedding\":18000,\"descriptions\":[]},\"persons\":[{\"minAge\":-1,\"maxAge\":-1,\"title\":\"전연령\",\"amount\":15000,\"maxPersons\":2}],\"consecutive\":{\"enable\":true,\"charge\":10000}}}],\"vrs\":{\"vr\":[{\"type\":\"HOTEL\",\"typeIdx\":1158,\"name\":\"디럭스 더블\",\"url\":\"https://players.cupix.com/p/S80dnHGJ\"},{\"type\":\"HOTEL\",\"typeIdx\":1158,\"name\":\"슈페리어 더블\",\"url\":\"https://players.cupix.com/p/ia8kZxnw\"},{\"type\":\"HOTEL\",\"typeIdx\":1158,\"name\":\"디럭스 트리플\",\"url\":\"https://players.cupix.com/p/awqfT6z4\"},{\"type\":\"HOTEL\",\"typeIdx\":1158,\"name\":\"디럭스 트윈\",\"url\":\"https://players.cupix.com/p/chdM7gGE\"},{\"type\":\"HOTEL\",\"typeIdx\":1158,\"name\":\"주니어 스위트 더블\",\"url\":\"https://players.cupix.com/p/OMqO5ksO\"}]},\"statistic\":{\"reviewScoreAvgs\":[{\"type\":\"청결\",\"scoreAvg\":4.5},{\"type\":\"위치\",\"scoreAvg\":4.6},{\"type\":\"서비스\",\"scoreAvg\":4.5},{\"type\":\"시설\",\"scoreAvg\":4.4}],\"reviewScoreTotalCount\":229}}";
        //        StayDetailData stayDetailData = null;
        //
        //        try
        //        {
        //            stayDetailData = LoganSquare.parse(jsonString, StayDetailData.class);
        //        } catch (IOException e)
        //        {
        //            ExLog.e(e.toString());
        //        }
        //
        //        return Observable.just(stayDetailData.getStayDetail()).subscribeOn(Schedulers.io());


        final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotel/{stayIndex}"//
            : "MzkkMTckNTgkNDMkOTAkNDgkMTckMzMkOSQ3MyQ3OCQyNSQyOCQyJDc1JDUwJA==$NkVMxNTg1NUzg0MzU4QIL0U1OUJY2DOEYyOTgS0MkVBMjkYwMCFzIyMJ0RBNTMzQzZFYMzlCNzA4BRNThCQDkEwN0I1M0JFQzI3MAW==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getStayDetail(Crypto.getUrlDecoderEx(API, urlParams) //
            , stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"), stayBookDateTime.getNights()) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                StayDetailk stayDetail;

                if (baseDto != null)
                {
                    if (baseDto.data != null)
                    {
                        // 100	성공
                        // 4	데이터가 없을시
                        // 5	판매 마감시
                        switch (baseDto.msgCode)
                        {
                            case 5:
                                stayDetail = baseDto.data.getStayDetail();
                                break;

                            case 100:
                                stayDetail = baseDto.data.getStayDetail();
                                break;

                            default:
                                throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return stayDetail;
            });
    }

    @Override
    public Observable<List<RoomImageInformation>> getRoomImages(int stayIndex, int roomIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/hotel/{stayIndex}/{roomIndex}/images"//
            : "MTAwJDEwOSQyNCQxMTIkNTIkODUkNzgkMTUkMzYkMTE1JDEwNSQxMjEkMzAkMTIwJDcwJDExMCQ=$QkQ4RTcyQzdGODgU0NERDODhFQQUExRNkVCQjNg4RTc3NzI4MEMyNkZLDNDU5QzNGMzk4MLEE2QzYxQjY1KOEQyODkKwNUUwNUQ5RjhCODJEBECMTcxOEJDEFNSBEVU2QkM4QUREREI2REM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));
        urlParams.put("{roomIndex}", Integer.toString(roomIndex));

        return mDailyMobileService.getRoomImages(Crypto.getUrlDecoderEx(API, urlParams)) //
            .subscribeOn(Schedulers.io()).map(baseListDto -> {
                List<RoomImageInformation> roomImageInformationList = new ArrayList<>();

                if (baseListDto != null)
                {
                    if (baseListDto.msgCode == 100 && baseListDto.data != null)
                    {
                        for (RoomImageInformationData roomImageInformationData : baseListDto.data)
                        {
                            roomImageInformationList.add(roomImageInformationData.getRoomImageInformation());
                        }
                    } else
                    {
                        throw new BaseException(baseListDto.msgCode, baseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return roomImageInformationList;
            });
    }

    @Override
    public Observable<Boolean> getHasCoupon(int stayIndex, StayBookDateTime stayBookDateTime)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{stayIndex}/coupons/exist"//
            : "OTgkMTI0JDExMyQ4NCQ4NCQyMSQxMjAkMTMxJDM0JDU1JDcwJDUyJDEzMiQxMTEkMzkkMTAwJA==$QTRFMkM0NkU2MjhBRjc1NTTIyODUxREQ3RFTIyMZjVENzA0Q0VCNDZk1QK0E2MEFEN0E4N0MW1MEMzNEE3QzJDQ0REXXQzM2NjYwYMzE5MkMVGN0NTBMERDMjFCOOEVQGMzlFMUGVLCXMDM4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getStayHasCoupon(Crypto.getUrlDecoderEx(API, urlParams) //
            , stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"), stayBookDateTime.getNights()) //
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                boolean hasCoupon = false;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        hasCoupon = baseDto.data.existCoupons;
                    }
                }

                return hasCoupon;
            });
    }

    @Override
    public Observable<WishResult> addWish(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/add/{stayIndex}"//
            : "NjkkNzkkNzkkOTAkNzckMTE4JDY1JDE3JDEzMyQxMDEkNDgkMTEkOTAkMTYkOTIkNDgk$OTM0RTg5QzkA0RkVBDOMDZFNzRFQkFGNzYxMTcwMjU5NzEzMCzAF3NjgyQzZGOUQxNTFCRAEMyNITQ1M0I3UNjGEE0QjHXI3RUFVFRDc0OEWQxQjlBNDNEQzc4NzI2RATYyRjM0MEE2RNDE4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.addWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<WishResult> removeWish(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/wishes/hotel/remove/{stayIndex}"//
            : "MTA3JDgxJDk4JDY2JDQkODMkODMkMjQkNTUkNzUkMzUkMjckMTM4JDEwMSQxMzkkMTM1JA==$QjQzUN0UzODUyNkFBM0FBQkJECMZDlFMTNFMKUJDMTE2MzE0MDYyMTZGMBkQ1QkQ5NDU1NTUc3M0QTyOEVGNkI4NQKCTQ4Qjc1QzUGwMjYyMM0U3REY1QTgX2MkYwMkQyRTkxNENM4NzDMQ3$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.removeWish(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                WishResult wishResult = new WishResult();

                if (baseDto != null)
                {
                    wishResult.success = baseDto.msgCode == 100;
                    wishResult.message = baseDto.msg;
                } else
                {
                    throw new BaseException(-1, null);
                }

                return wishResult;
            });
    }

    @Override
    public Observable<ReviewScores> getReviewScores(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{stayIndex}/statistic"//
            : "NzckNTkkMTgkODgkMTA4JDExOSQyMyQ2JDE4JDI3JDI3JDEzJDE2JDEzMyQxMTckMTAwJA==$QzQ2OTOMxNUNBJNTBBDQNTDg2RUPFGYGMzZDNzMyNzQxMzA5MzdBM0EzNkI3NEVBNTkE1MjUyMjY5RjA4QUE4MYjMzQjgxQV0Y2NBjk2RjI4MEFCOTU4TOXERCRTI2MzYEwOERDRMzkxQkRC$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getReviewScores(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                ReviewScores reviewScores;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        reviewScores = baseDto.data.getReviewScores();
                    } else
                    {
                        reviewScores = new ReviewScores();
                    }
                } else
                {
                    reviewScores = new ReviewScores();
                }

                return reviewScores;
            });
    }

    @Override
    public Observable<TrueReviews> getTrueReviews(int stayIndex, int page, int limit)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v4/review/hotel/{stayIndex}"//
            : "MjUkNjUkNjAkNjYkMzIkMzYkMjIkNzMkOTEkODkkNjMkNSQzOCQzNSQ1NyQ1NSQ=$ODhEOWTEyMTk0RDU1ODkxODSY0OOEVGQzVRDCRUXEM3MTY4QkNGNDc2UN0DU2NzRGODkGVzM0MwXAMzMM4QTg0MDJEODc0MZTVXCNg==$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getTrueReviews(Crypto.getUrlDecoderEx(API, urlParams), page, limit, "createdAt", "DESC")//
            .subscribeOn(Schedulers.io()).map(baseDto -> {
                TrueReviews trueReviews;

                if (baseDto != null)
                {
                    if (baseDto.msgCode == 100 && baseDto.data != null)
                    {
                        trueReviews = baseDto.data.getTrueReviews();
                    } else
                    {
                        throw new BaseException(baseDto.msgCode, baseDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return trueReviews;
            });
    }

    @Override
    public Observable<List<TrueVR>> getTrueVR(int stayIndex)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/{stayIndex}/vr-list"//
            : "NzAkNDIkOCQ2JDg0JDQyJDY5JDU2JDExNSQxMzUkMTEyJDY0JDQxJDk1JDEzNCQxMTIk$NEM4MzEUzSMTg4MzU3NDA4RDQ0NjAxNzVBRkM2RDkN3ZQzJQ4QjAzOThFFREREQ0QAwRTgyRIDU5M0BExNjI4ODI4WMUI2NIjYzNUM3ODQxNEU2NLTFFHOTFWCNkY4RTJEMjA0MKzVFRERQ4$";

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{stayIndex}", Integer.toString(stayIndex));

        return mDailyMobileService.getTrueReviews(Crypto.getUrlDecoderEx(API, urlParams))//
            .subscribeOn(Schedulers.io()).map(baseListDto -> {
                List<TrueVR> trueVR = new ArrayList<>();

                if (baseListDto != null)
                {
                    if (baseListDto.msgCode == 100 && baseListDto.data != null)
                    {
                        for (TrueVRData trueVRData : baseListDto.data)
                        {
                            trueVR.add(trueVRData.getTrueVR());
                        }
                    } else
                    {
                    }
                } else
                {
                }

                return trueVR;
            });
    }

    @Override
    public Observable<List<StayAreaGroup>> getAreaList(Context context, DailyCategoryType categoryType)
    {
        final String API;

        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            API = Constants.UNENCRYPTED_URL ? "api/v3/hotel/region"//
                : "MjMkNjQkMjEkMCQ2MCQ1MiQ0NCQzMiQzMSQyMiQ3MSQ4NiQ2OCQxMyQ0NyQ2OCQ=$PRUM3NTRGQzA5RMEVBMjZFNPQEEN0MTgzYMVzcyQ0VERDUzOOJDQyRTQ1NYzkxNkM0MBNEUG1RUTFOGMDExRDVEMEMExRTEwMDExNw==$";

            return mDailyMobileService.getStayAreaList(Crypto.getUrlDecoderEx(API))//
                .subscribeOn(Schedulers.io()).map(baseDto -> {
                    List<StayAreaGroup> areaGroupList;

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            areaGroupList = baseDto.data.getAreaGroupList();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return areaGroupList;
                });
        } else
        {
            API = Constants.UNENCRYPTED_URL ? "api/v4/hotels/category/{category}/regions"//
                : "OTkkNTIkMTIyJDEzJDI3JDE0JDg2JDcwJDUyJDM5JDI3JDExOSQxMjUkODkkMTIwJDExMSQ=$QjAyMTQ1MUIzQKLkE0OTAzNUQ3MXjIhENTQwQjY1KQjZFQTIyMDFFRWDk0PQUZGNEUyMUZBODVI5QjcxMDg4ODU1OLEVZGRUU3MThGNTQ4OUJCGPMTQ4REVDMLEUJCMDENCQ0ZCWRDZFQUM4$";

            Map<String, String> urlParams = new HashMap<>();
            urlParams.put("{category}", categoryType.getCodeString(context));

            return mDailyMobileService.getStayCategoryAreaList(Crypto.getUrlDecoderEx(API, urlParams))//
                .subscribeOn(Schedulers.io()).map(baseDto -> {
                    List<StayAreaGroup> areaGroupList;

                    if (baseDto != null)
                    {
                        if (baseDto.msgCode == 100 && baseDto.data != null)
                        {
                            areaGroupList = baseDto.data.getAreaGroupList();
                        } else
                        {
                            throw new BaseException(baseDto.msgCode, baseDto.msg);
                        }
                    } else
                    {
                        throw new BaseException(-1, null);
                    }

                    return areaGroupList;
                });
        }
    }

    @Override
    public Observable<LinkedHashMap<Area, List<StaySubwayAreaGroup>>> getSubwayAreaList(Context context, DailyCategoryType categoryType)
    {
        final String API = Constants.UNENCRYPTED_URL ? "api/v6/hotels/subway"//
            : "NTkkMjQkODkkMTgkNzMkMSQzMSQzOCQzMCQ3NCQyMyQ1MiQ0OCQ5JDEwMCQ4MSQ=$NP0RCOUIyZN0JEMjNDRjKVEMYDgwBQzYS3HN0Y2MTMVERkJFQPzJCQFjY0NTlBNzMzMzNUGOUFGQTgHyNCDVhEMjAzQjA1MTNBQg=SS=$";

        String category;

        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            category = null;
        } else
        {
            category = categoryType.getCodeString(context);
        }

        return mDailyMobileService.getStaySubwayAreaList(Crypto.getUrlDecoderEx(API), category)//
            .subscribeOn(Schedulers.io()).map(baseListDto -> {
                LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayHashMap = new LinkedHashMap<>();

                if (baseListDto != null)
                {
                    if (baseListDto.msgCode == 100 && baseListDto.data != null)
                    {
                        for (SubwayAreasData subwayAreasData : baseListDto.data)
                        {
                            Pair<Area, List<StaySubwayAreaGroup>> pair = subwayAreasData.getAreaGroup();

                            subwayHashMap.put(pair.first, pair.second);
                        }
                    } else
                    {
                        throw new BaseException(baseListDto.msgCode, baseListDto.msg);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return subwayHashMap;
            });
    }

    @Override
    public Observable<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> getRegionList(Context context, DailyCategoryType categoryType)
    {
        return Observable.zip(getAreaList(context, categoryType), getSubwayAreaList(context, categoryType), new BiFunction<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>, Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>()
        {
            @Override
            public Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> apply(List<StayAreaGroup> areaGroupList, LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaListLinkedHashMap) throws Exception
            {
                return new Pair(areaGroupList, areaListLinkedHashMap);
            }
        });
    }

    private String toStringQueryParams(Map<String, Object> queryMap, String abTestType)
    {
        StringBuilder stringBuilder = new StringBuilder(1024);
        stringBuilder.append('?');

        for (Map.Entry<String, Object> entry : queryMap.entrySet())
        {
            String entryKey = entry.getKey();
            if (DailyTextUtils.isTextEmpty(entryKey) == true)
            {
                continue;
            }

            Object entryValue = entry.getValue();
            if (entryValue == null)
            {
                continue;
            }

            if (entryValue instanceof List)
            {
                if (stringBuilder.length() > 1)
                {
                    stringBuilder.append('&');
                }

                stringBuilder.append(toStringListQueryParams(entryKey, (List) entryValue, stringBuilder.length() > 1));
            } else
            {
                String convertedEntryValue = entryValue.toString();

                if (DailyTextUtils.isTextEmpty(convertedEntryValue) == true)
                {
                    continue;
                }

                stringBuilder.append(toStringQueryParams(entryKey, convertedEntryValue, stringBuilder.length() > 1));
            }
        }

        if (DailyTextUtils.isTextEmpty(abTestType) == false)
        {
            toStringQueryParams("abtest", abTestType, stringBuilder.length() > 1);
        }

        return stringBuilder.toString();
    }

    private String toStringListQueryParams(String entryKey, List entryValue, boolean addBeginAmpersand)
    {
        if (entryKey == null || entryValue == null)
        {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        int size = entryValue.size();

        for (int i = 0; i < size; i++)
        {
            String convertedEntryValue = entryValue.get(i).toString();

            if (DailyTextUtils.isTextEmpty(convertedEntryValue) == true)
            {
                continue;
            }

            if (i > 0)
            {
                stringBuilder.append('&');
            }

            stringBuilder.append(entryKey);
            stringBuilder.append("=");
            stringBuilder.append(convertedEntryValue);
        }

        if (addBeginAmpersand && stringBuilder.length() > 0)
        {
            stringBuilder.insert(0, '&');
        }

        return stringBuilder.toString();
    }

    private String toStringQueryParams(String entryKey, String entryValue, boolean addAmpersand)
    {
        if (entryKey == null || entryValue == null)
        {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (addAmpersand == true)
        {
            stringBuilder.append('&');
        }

        stringBuilder.append(entryKey);
        stringBuilder.append("=");
        stringBuilder.append(entryValue);

        return stringBuilder.toString();
    }
}
