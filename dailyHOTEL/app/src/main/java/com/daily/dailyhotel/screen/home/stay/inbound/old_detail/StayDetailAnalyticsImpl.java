package com.daily.dailyhotel.screen.home.stay.inbound.old_detail;

import android.app.Activity;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.old_StayDetail;
import com.daily.dailyhotel.entity.StayRoom;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayPaymentAnalyticsParam;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StayDetailAnalyticsImpl implements StayDetailPresenter.StayDetailAnalyticsInterface
{
    StayDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(StayDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public Disposable onScreen(Activity activity, StayBookDateTime stayBookDateTime, old_StayDetail stayDetail, int priceFromList)
    {
        if (activity == null || stayDetail == null)
        {
            return null;
        }

        // Completable.create를 사용하면 내부적으로 예외 처리를 받을수 있으나 결국 내부 소스에서 try ~ catch로 구현되어있어서
        // 그냥 try ~ catch를 둘러싸는 것과 같은 내용이여서 예외 처리만 추가 함.
        return new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
            {
                try
                {
                    Map<String, String> params = new HashMap<>();
                    params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
                    params.put(AnalyticsManager.KeyType.GRADE, stayDetail.grade.getName(activity)); // 14
                    params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetail.benefit) ? "no" : "yes"); // 3

                    if (stayDetail.getRoomList() == null || stayDetail.getRoomList().size() == 0)
                    {
                        params.put(AnalyticsManager.KeyType.PRICE, "0");
                    } else
                    {
                        params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayDetail.getRoomList().get(0).discountAverage));
                    }

                    int nights = stayBookDateTime.getNights();

                    params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index)); // 15

                    params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")); // 1
                    params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")); // 2

                    params.put(AnalyticsManager.KeyType.ADDRESS, stayDetail.address);

                    if (DailyTextUtils.isTextEmpty(stayDetail.category) == true) //
                    {
                        params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
                    } else
                    {
                        params.put(AnalyticsManager.KeyType.CATEGORY, stayDetail.category);
                    }

                    params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAreaGroupName());
                    params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAreaName());
                    params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

                    params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(priceFromList));
                    params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookDateTime.getCheckInDateTime("yyyyMMdd"));

                    String listIndex = mAnalyticsParam.entryPosition == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

                    params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

                    String placeCount = mAnalyticsParam.totalListCount == -1 //
                        ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.totalListCount);

                    params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

                    params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetail.ratingValue));
                    params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.getShowOriginalPriceYn());
                    params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
                    params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                    params.put(AnalyticsManager.KeyType.COUNTRY, stayDetail.overseas == false ? AnalyticsManager.ValueType.DOMESTIC : AnalyticsManager.ValueType.OVERSEAS);

                    AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL, null, params);

                    observer.onComplete();
                } catch (Exception e)
                {
                    Crashlytics.log(EXCEPTION_TAG);
                    Crashlytics.logException(e);

                    ExLog.e(EXCEPTION_TAG + " : " + e.toString());
                }
            }
        }.subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void onScreenRoomList(Activity activity, StayBookDateTime stayBookDateTime, old_StayDetail stayDetail, int priceFromList)
    {
        if (activity == null || stayDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
            params.put(AnalyticsManager.KeyType.GRADE, stayDetail.grade.getName(activity)); // 14
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetail.benefit) ? "no" : "yes"); // 3

            if (stayDetail.getRoomList() == null || stayDetail.getRoomList().size() == 0)
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(stayDetail.getRoomList().get(0).discountAverage));
            }

            int nights = stayBookDateTime.getNights();

            params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index)); // 15

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd")); // 1
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd")); // 2

            params.put(AnalyticsManager.KeyType.ADDRESS, stayDetail.address);

            if (DailyTextUtils.isTextEmpty(stayDetail.category) == true) //
            {
                params.put(AnalyticsManager.KeyType.CATEGORY, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetail.category);
            }

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAreaGroupName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAreaName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, Integer.toString(priceFromList));
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookDateTime.getCheckInDateTime("yyyyMMdd"));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = mAnalyticsParam.totalListCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.totalListCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetail.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.getShowOriginalPriceYn());
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYHOTEL_DETAIL_ROOMTYPE, null, params);


            if (DailyRemoteConfigPreference.getInstance(activity).isKeyRemoteConfigRewardStickerEnabled() && stayDetail.provideRewardSticker == true)
            {
                AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                    , AnalyticsManager.Action.ROOM_SELECTION, Integer.toString(stayDetail.index), null);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventRoomListOpenClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_CLICKED, stayName, null);
    }

    @Override
    public void onEventRoomListCloseClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_CANCEL_CLICKED, stayName, null);
    }

    @Override
    public void onEventRoomClick(Activity activity, String roomName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.ROOM_TYPE_ITEM_CLICKED, roomName, null);
    }

    @Override
    public void onEventShareKakaoClick(Activity activity, boolean login, String userType, boolean benefitAlarm//
        , int stayIndex, String stayName, boolean overseas)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        try
        {
            HashMap<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.COUNTRY, overseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAreaGroupName());

            if (login == true)
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.MEMBER);

                switch (userType)
                {
                    case Constants.DAILY_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.EMAIL);
                        break;

                    case Constants.KAKAO_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.KAKAO);
                        break;

                    case Constants.FACEBOOK_USER:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.UserType.FACEBOOK);
                        break;

                    default:
                        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
                        break;
                }
            } else
            {
                params.put(AnalyticsManager.KeyType.USER_TYPE, AnalyticsManager.ValueType.GUEST);
                params.put(AnalyticsManager.KeyType.MEMBER_TYPE, AnalyticsManager.ValueType.EMPTY);
            }

            params.put(AnalyticsManager.KeyType.PUSH_NOTIFICATION, benefitAlarm ? "on" : "off");
            params.put(AnalyticsManager.KeyType.SHARE_METHOD, AnalyticsManager.ValueType.KAKAO);
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(stayIndex));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, stayName);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
                , AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.KAKAO, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventLinkCopyClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE //
            , AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.LINK_COPY, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE //
            , AnalyticsManager.Action.STAY_ITEM_SHARE, AnalyticsManager.ValueType.ETC, null);
    }

    @Override
    public void onEventDownloadCoupon(Activity activity, String stayName)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.HOTEL_COUPON_DOWNLOAD, stayName, null);
    }

    @Override
    public void onEventDownloadCouponByLogin(Activity activity, boolean login)
    {
        if (activity == null)
        {
            return;
        }

        if (login == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.LOGIN_, null);

        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES, AnalyticsManager.Action.COUPON_LOGIN, AnalyticsManager.Label.CLOSED, null);
        }
    }

    @Override
    public void onEventShare(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE,//
            AnalyticsManager.Action.ITEM_SHARE, AnalyticsManager.Label.STAY, null);
    }

    @Override
    public void onEventChangedPrice(Activity activity, boolean deepLink, String stayName, boolean soldOut)
    {
        if (activity == null)
        {
            return;
        }

        if (soldOut == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                deepLink ? AnalyticsManager.Action.SOLDOUT_DEEPLINK : AnalyticsManager.Action.SOLDOUT, stayName, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, stayName, null);
        }
    }

    @Override
    public void onEventCalendarClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    @Override
    public void onEventBookingClick(Activity activity, StayBookDateTime stayBookDateTime, int stayIndex//
        , String stayName, String roomName, int discountPrice, String category, boolean provideRewardSticker //
        , boolean isOverseas)
    {
        if (activity == null || mAnalyticsParam == null || stayBookDateTime == null)
        {
            return;
        }

        String label = String.format(Locale.KOREA, "%s-%s", stayName, roomName);

        int nights = stayBookDateTime.getNights();

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, stayName);
        params.put(AnalyticsManager.KeyType.QUANTITY, Integer.toString(nights));
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayIndex));
        params.put(AnalyticsManager.KeyType.CATEGORY, category);

        params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAreaGroupName());
        params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAreaName());
        params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

        params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_ROOM, Integer.toString(discountPrice));
        params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookDateTime.getCheckInDateTime("yyyyMMdd"));
        params.put(AnalyticsManager.KeyType.COUNTRY, isOverseas == false ? AnalyticsManager.ValueType.DOMESTIC : AnalyticsManager.ValueType.OVERSEAS);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , AnalyticsManager.Action.BOOKING_CLICKED, label, params);

        if (provideRewardSticker == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.REWARD//
                , AnalyticsManager.Action.ORDER_PROCEED, Integer.toString(stayIndex), null);
        }
    }

    @Override
    public void onEventTrueReviewClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.TRUE_REVIEW_CLICK, AnalyticsManager.Label.STAY, null);
    }

    @Override
    public void onEventTrueVRClick(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION,//
            AnalyticsManager.Action.TRUE_VR_CLICK, Integer.toString(stayIndex), null);
    }

    @Override
    public void onEventImageClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
            AnalyticsManager.Action.HOTEL_IMAGE_CLICKED, stayName, null);
    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.STAY_DETAIL, null);
    }

    @Override
    public void onEventMapClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS,//
            AnalyticsManager.Action.HOTEL_DETAIL_MAP_CLICKED, stayName, null);
    }

    @Override
    public void onEventClipAddressClick(Activity activity, String stayName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, //
            AnalyticsManager.Action.HOTEL_DETAIL_ADDRESS_COPY_CLICKED, stayName, null);
    }

    @Override
    public void onEventWishClick(Activity activity, StayBookDateTime stayBookDateTime, old_StayDetail stayDetail, int priceFromList, boolean myWish)
    {
        if (activity == null || stayBookDateTime == null || stayDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            params.put(AnalyticsManager.KeyType.NAME, stayDetail.name);
            params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(priceFromList));
            params.put(AnalyticsManager.KeyType.COUNTRY, stayDetail.overseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.CATEGORY, stayDetail.category);

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getAreaGroupName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getAreaName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.GRADE, stayDetail.grade.getName(activity));
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(stayDetail.index));
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(stayDetail.ratingValue));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(stayDetail.benefit) ? "no" : "yes");

            int nights = stayBookDateTime.getNights();

            params.put(AnalyticsManager.KeyType.CHECK_IN, stayBookDateTime.getCheckInDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT, stayBookDateTime.getCheckOutDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.getShowOriginalPriceYn());

            AnalyticsManager.getInstance(activity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_,//
                myWish ? AnalyticsManager.Action.WISHLIST_ON : AnalyticsManager.Action.WISHLIST_OFF, stayDetail.name, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventCallClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
    }

    @Override
    public void onEventFaqClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
    }

    @Override
    public void onEventHappyTalkClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.STAY_DETAIL, null);
    }

    @Override
    public void onEventShowTrueReview(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_TRUE_REVIEW,//
            AnalyticsManager.Label.STAY, Integer.toString(stayIndex), null);
    }

    @Override
    public void onEventShowCoupon(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_COUPON,//
            AnalyticsManager.Label.STAY, Integer.toString(stayIndex), null);
    }

    @Override
    public StayPaymentAnalyticsParam getStayPaymentAnalyticsParam(old_StayDetail stayDetail, StayRoom stayRoom)
    {
        StayPaymentAnalyticsParam analyticsParam = new StayPaymentAnalyticsParam();

        if (stayDetail == null || stayRoom == null)
        {
            return analyticsParam;
        }

        if (mAnalyticsParam != null)
        {
            analyticsParam.showOriginalPrice = mAnalyticsParam.getShowOriginalPriceYn();
            analyticsParam.rankingPosition = mAnalyticsParam.entryPosition;
            analyticsParam.totalListCount = mAnalyticsParam.totalListCount;
            analyticsParam.dailyChoice = mAnalyticsParam.isDailyChoice;
            analyticsParam.setRegion(mAnalyticsParam.getRegion());
            analyticsParam.addressAreaName = mAnalyticsParam.getAddressAreaName();
        }

        analyticsParam.ratingValue = stayDetail.ratingValue;
        analyticsParam.benefit = DailyTextUtils.isTextEmpty(stayDetail.benefit) == false;
        analyticsParam.averageDiscount = stayRoom.discountAverage;
        analyticsParam.address = stayDetail.address;
        analyticsParam.nrd = stayRoom.nrd;
//        analyticsParam.grade = stayDetail.grade;
        analyticsParam.provideRewardSticker = stayDetail.provideRewardSticker;

        return analyticsParam;
    }

    @Override
    public void onEventTrueAwards(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.TRUE_AWARDS, //
            AnalyticsManager.Action.DETAIL_PAGE, Integer.toString(stayIndex), null);
    }

    @Override
    public void onEventTrueAwardsClick(Activity activity, int stayIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.TRUE_AWARDS, //
            AnalyticsManager.Action.QUESTION_MARK, Integer.toString(stayIndex), null);
    }
}
