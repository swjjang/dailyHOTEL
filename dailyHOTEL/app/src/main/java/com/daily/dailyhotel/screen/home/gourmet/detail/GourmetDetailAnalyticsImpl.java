package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.app.Activity;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GourmetDetailAnalyticsImpl implements GourmetDetailPresenter.GourmetDetailAnalyticsInterface
{
    private GourmetDetailAnalyticsParam mAnalyticsParam;

    @Override
    public void setAnalyticsParam(GourmetDetailAnalyticsParam analyticsParam)
    {
        mAnalyticsParam = analyticsParam;
    }

    @Override
    public void onScreen(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int priceFromList)
    {
        if (activity == null || mAnalyticsParam == null || gourmetBookDateTime == null || gourmetDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetail.name);
            params.put(AnalyticsManager.KeyType.GRADE, AnalyticsManager.ValueType.EMPTY); //
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, "gourmet"); //
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetail.category);
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(gourmetDetail.benefit) ? "no" : "yes");

            List<GourmetMenu> gourmetMenuList = gourmetDetail.getMenuList();

            if (gourmetDetail.hasMenus() == true)
            {
                params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(gourmetMenuList.get(0).discountPrice));
            } else
            {
                params.put(AnalyticsManager.KeyType.PRICE, "0");
            }

            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetDetail.index));
            params.put(AnalyticsManager.KeyType.DATE, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.UNIT_PRICE, priceFromList <= 0 ? AnalyticsManager.ValueType.EMPTY : Integer.toString(priceFromList));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookDateTime.getVisitDateTime("yyyyMMdd"));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);

            String placeCount = mAnalyticsParam.totalListCount == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.totalListCount);

            params.put(AnalyticsManager.KeyType.PLACE_COUNT, placeCount);

            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetail.ratingValue));
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.getShowOriginalPriceYn());
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
            params.put(AnalyticsManager.KeyType.NRD, gourmetDetail.getSticker() != null ? "y" : "n");

            AnalyticsManager.getInstance(activity).recordScreen(activity, AnalyticsManager.Screen.DAILYGOURMET_DETAIL, null, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

    }

    @Override
    public void onEventShareKakaoClick(Activity activity, boolean login, String userType, boolean benefitAlarm//
        , int gourmetIndex, String gourmetName)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.SERVICE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());

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
            params.put(AnalyticsManager.KeyType.VENDOR_ID, Integer.toString(gourmetIndex));
            params.put(AnalyticsManager.KeyType.VENDOR_NAME, gourmetName);

            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE//
                , AnalyticsManager.Action.GOURMET_ITEM_SHARE, AnalyticsManager.ValueType.KAKAO, params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventCopyLinkClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE //
            , AnalyticsManager.Action.GOURMET_ITEM_SHARE, AnalyticsManager.ValueType.LINK_COPY, null);
    }

    @Override
    public void onEventMoreShareClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.SHARE //
            , AnalyticsManager.Action.GOURMET_ITEM_SHARE, AnalyticsManager.ValueType.ETC, null);
    }

    @Override
    public void onEventDownloadCoupon(Activity activity, String gourmetName)
    {
        if (mAnalyticsParam == null || activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_COUPON_DOWNLOAD, gourmetName, null);

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
            AnalyticsManager.Action.ITEM_SHARE, AnalyticsManager.Label.GOURMET, null);
    }

    @Override
    public void onEventHasHiddenMenus(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.VIEW_HIDDEN_MENU, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onEventChangedPrice(Activity activity, boolean deepLink, String gourmetName, boolean soldOut)
    {
        if (activity == null)
        {
            return;
        }

        if (soldOut == true)
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                deepLink ? AnalyticsManager.Action.SOLDOUT_DEEPLINK : AnalyticsManager.Action.SOLDOUT, gourmetName, null);
        } else
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.POPUP_BOXES,//
                AnalyticsManager.Action.SOLDOUT_CHANGEPRICE, gourmetName, null);
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
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    @Override
    public void onEventOrderClick(Activity activity, GourmetBookDateTime gourmetBookDateTime//
        , int gourmetIndex, String gourmetName, String category, GourmetCart gourmetCart)
    {
        if (activity == null || mAnalyticsParam == null || gourmetBookDateTime == null || gourmetBookDateTime == null)
        {
            return;
        }

        String label = String.format(Locale.KOREA, "%s_%s", gourmetCart.getMenuCount() == 1 ? "single" : "multi", gourmetName);

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, gourmetName);
        params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetIndex));
        params.put(AnalyticsManager.KeyType.CATEGORY, category);

        params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
        params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
        params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

        params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(gourmetCart.getTotalPrice()));
        params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookDateTime.getVisitDateTime("yyyyMMdd"));

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.BOOKING_CLICKED, label, params);
    }

    @Override
    public void onEventScrollTopMenuClick(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.TICKET_TYPE_CLICKED, gourmetName, null);
    }

    @Override
    public void onEventMenuClick(Activity activity, int menuIndex, int position)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_MENU_DETAIL_CLICK, Integer.toString(menuIndex), null);

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_TICKET_RANK, Integer.toString(position + 1), null);
    }

    @Override
    public void onEventTrueReviewClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.TRUE_REVIEW_CLICK, AnalyticsManager.Label.GOURMET, null);
    }

    @Override
    public void onEventMoreMenuClick(Activity activity, boolean opened, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        try
        {
            AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , opened ? AnalyticsManager.Action.GOURMET_MENU_FOLD : AnalyticsManager.Action.GOURMET_MENU_UNFOLD, Integer.toString(gourmetIndex), null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onEventImageClick(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.GOURMET_IMAGE_CLICKED, gourmetName, null);
    }

    @Override
    public void onEventConciergeClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.CONTACT_DAILY_CONCIERGE, AnalyticsManager.Label.GOURMET_DETAIL, null);
    }

    @Override
    public void onEventMapClick(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.GOURMET_DETAIL_MAP_CLICKED, gourmetName, null);
    }

    @Override
    public void onEventClipAddressClick(Activity activity, String gourmetName)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.GOURMET_DETAIL_ADDRESS_COPY_CLICKED, gourmetName, null);
    }

    @Override
    public void onEventWishClick(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int priceFromList, boolean myWish)
    {
        if (activity == null || gourmetBookDateTime == null || gourmetDetail == null)
        {
            return;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetail.name);
            params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(priceFromList));
            params.put(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.DOMESTIC);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetail.category);

            params.put(AnalyticsManager.KeyType.PROVINCE, mAnalyticsParam.getProvinceName());
            params.put(AnalyticsManager.KeyType.DISTRICT, mAnalyticsParam.getDistrictName());
            params.put(AnalyticsManager.KeyType.AREA, mAnalyticsParam.getAddressAreaName());

            params.put(AnalyticsManager.KeyType.GRADE, AnalyticsManager.ValueType.EMPTY);
            params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(gourmetDetail.index));
            params.put(AnalyticsManager.KeyType.RATING, Integer.toString(gourmetDetail.ratingValue));

            String listIndex = mAnalyticsParam.entryPosition == -1 //
                ? AnalyticsManager.ValueType.EMPTY : Integer.toString(mAnalyticsParam.entryPosition);

            params.put(AnalyticsManager.KeyType.LIST_INDEX, listIndex);
            params.put(AnalyticsManager.KeyType.DAILYCHOICE, mAnalyticsParam.isDailyChoice ? "y" : "n");
            params.put(AnalyticsManager.KeyType.DBENEFIT, DailyTextUtils.isTextEmpty(gourmetDetail.benefit) ? "no" : "yes");

            params.put(AnalyticsManager.KeyType.CHECK_IN, gourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, "1");
            params.put(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE, mAnalyticsParam.getShowOriginalPriceYn());


            AnalyticsManager.getInstance(activity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION_,//
                myWish ? AnalyticsManager.Action.WISHLIST_ON : AnalyticsManager.Action.WISHLIST_OFF, gourmetDetail.name, params);
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
            , AnalyticsManager.Action.CALL_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
    }

    @Override
    public void onEventFaqClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.FNQ_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
    }

    @Override
    public void onEventHappyTalkClick(Activity activity)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.HAPPYTALK_CLICK, AnalyticsManager.Label.GOURMET_DETAIL, null);
    }

    @Override
    public void onEventShowTrueReview(Activity activity, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_TRUE_REVIEW,//
            AnalyticsManager.Label.GOURMET, Integer.toString(gourmetIndex), null);
    }

    @Override
    public void onEventShowCoupon(Activity activity, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.DETAIL_PAGE_COUPON,//
            AnalyticsManager.Label.GOURMET, Integer.toString(gourmetIndex), null);
    }

    @Override
    public void onEventVisitTimeClick(Activity activity, String visitTime)
    {
        if (activity == null)
        {
            return;
        }

        String label = GourmetDetailPresenter.FULL_TIME.equalsIgnoreCase(visitTime) ? AnalyticsManager.Label.FULL_TIME : AnalyticsManager.Label.SELECT_TIME;

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.AVAILABLE_TIME, label, null);
    }

    @Override
    public void onEventToolbarBookingClick(Activity activity, int gourmetIndex)
    {
        if (activity == null)
        {
            return;
        }

        AnalyticsManager.getInstance(activity).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
            AnalyticsManager.Action.SHORTCUT_ORDER, Integer.toString(gourmetIndex), null);
    }

    @Override
    public GourmetPaymentAnalyticsParam getStayPaymentAnalyticsParam(GourmetDetail gourmetDetail, GourmetCart gourmetCart)
    {
        GourmetPaymentAnalyticsParam analyticsParam = new GourmetPaymentAnalyticsParam();

        if (gourmetDetail == null || gourmetCart == null)
        {
            return analyticsParam;
        }

        if (mAnalyticsParam != null)
        {
            analyticsParam.showOriginalPrice = mAnalyticsParam.getShowOriginalPriceYn();
            analyticsParam.rankingPosition = mAnalyticsParam.entryPosition;
            analyticsParam.totalListCount = mAnalyticsParam.totalListCount;
            analyticsParam.dailyChoice = mAnalyticsParam.isDailyChoice;
            analyticsParam.province = mAnalyticsParam.getProvince();
            analyticsParam.addressAreaName = mAnalyticsParam.getAddressAreaName();
        }

        analyticsParam.ratingValue = gourmetDetail.ratingValue;
        analyticsParam.benefit = DailyTextUtils.isTextEmpty(gourmetDetail.benefit) == false;
        analyticsParam.totalPrice = gourmetCart.getTotalPrice();
        analyticsParam.address = gourmetDetail.address;
        analyticsParam.categorySub = gourmetDetail.categorySub;

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
