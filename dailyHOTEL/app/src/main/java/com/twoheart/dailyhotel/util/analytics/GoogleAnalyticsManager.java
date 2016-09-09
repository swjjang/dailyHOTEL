package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Map;

public class GoogleAnalyticsManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[GoogleAnalyticsManager]";
    private static final String GA_PROPERTY_ID = DEBUG ? "UA-80273653-1" : "UA-43721645-6";

    private Tracker mGoogleAnalyticsTracker;
    private String mClientId;

    interface OnClientIdListener
    {
        void onResponseClientId(String cliendId);
    }

    public GoogleAnalyticsManager(Context context, final OnClientIdListener listener)
    {
        final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setLocalDispatchPeriod(60);

        mGoogleAnalyticsTracker = googleAnalytics.newTracker(GA_PROPERTY_ID);
        mGoogleAnalyticsTracker.enableAdvertisingIdCollection(true);
        mGoogleAnalyticsTracker.set("&cu", "KRW");

        mClientId = mGoogleAnalyticsTracker.get("&cid");

        if (listener != null)
        {
            listener.onResponseClientId(mClientId);
        }
    }

    public String getClientId()
    {
        return mClientId;
    }

    @Override
    void recordScreen(String screen)
    {
        if (AnalyticsManager.Screen.MENU_REGISTRATION_CONFIRM.equalsIgnoreCase(screen) == true//
            || AnalyticsManager.Screen.MENU_LOGIN_COMPLETE.equalsIgnoreCase(screen) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
            screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.MEMBER);

            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else if (AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE.equalsIgnoreCase(screen) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
            screenViewBuilder.setCustomDimension(5, AnalyticsManager.ValueType.GUEST);

            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        } else
        {
            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Screen : " + screen);
        }
    }

    @Override
    void recordScreen(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP.equalsIgnoreCase(screen) == true)
        {
            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

            String checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);

            if (Util.isTextEmpty(checkIn) == false)
            {
                screenViewBuilder.setCustomDimension(1, checkIn);
            }

            if (Util.isTextEmpty(checkOut) == false)
            {
                screenViewBuilder.setCustomDimension(2, checkOut);
            }

            screenViewBuilder.setCustomDimension(5, params.get(AnalyticsManager.KeyType.IS_SIGNED));
            screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
            screenViewBuilder.setCustomDimension(19, params.get(AnalyticsManager.KeyType.PLACE_HIT_TYPE));
            screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
            screenViewBuilder.setCustomDimension(8, params.get(AnalyticsManager.KeyType.PROVINCE));

            String district = params.get(AnalyticsManager.KeyType.DISTRICT);
            if (Util.isTextEmpty(district) == false)
            {
                screenViewBuilder.setCustomDimension(12, district);
            }

            String category = params.get(AnalyticsManager.KeyType.CATEGORY);
            if (Util.isTextEmpty(category) == false)
            {
                screenViewBuilder.setCustomDimension(13, category);
            }

            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            if (DEBUG == true)
            {
                ExLog.d(TAG + "recordScreen : " + screen + " | " + screenViewBuilder.build().toString());
            }

        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screen) == true || AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screen) == true)
        {
            checkoutStep(1, screen, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL_ROOMTYPE.equalsIgnoreCase(screen) == true || AnalyticsManager.Screen.DAILYGOURMET_DETAIL_TICKETTYPE.equalsIgnoreCase(screen) == true)
        {
            checkoutStep(2, screen, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENT.equalsIgnoreCase(screen) == true || AnalyticsManager.Screen.DAILYGOURMET_PAYMENT.equalsIgnoreCase(screen) == true)
        {
            checkoutStep(3, screen, null, params);
        } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_AGREEMENT_POPUP.equalsIgnoreCase(screen) == true || AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_AGREEMENT_POPUP.equalsIgnoreCase(screen) == true)
        {
            checkoutStep(4, screen, null, params);
        } else if (AnalyticsManager.Screen.BOOKING_LIST.equalsIgnoreCase(screen) == true)
        {
            recordScreen(screen);
        } else if (AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.SEARCH_RESULT.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.SEARCH_RESULT_EMPTY.equalsIgnoreCase(screen) == true)
        {
            recordSearchAnalytics(screen, params);
        } else if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true //
            || AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            recordScreen(screen);
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        long value = 0L;

        if (Util.isTextEmpty(category, action) == true)
        {
            return;
        }

        if (AnalyticsManager.Category.NAVIGATION.equalsIgnoreCase(category) == true//
            && (AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true//
            || AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED.equalsIgnoreCase(action) == true))
        {
            label = params.get(AnalyticsManager.KeyType.SCREEN) + '-' + label;
        }

        mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
            .setCategory(category).setAction(action)//
            .setLabel(label).setValue(value).build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label);
        }
    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || dailyDeepLink.isValidateLink() == false)
        {
            return;
        }

        String screenName = null;

        if (dailyDeepLink.isHotelListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_LIST;
        } else if (dailyDeepLink.isHotelDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_DETAIL;
        } else if (dailyDeepLink.isHotelRegionListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;
        } else if (dailyDeepLink.isHotelEventBannerWebView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYHOTEL_BANNER_DETAIL;
        } else if (dailyDeepLink.isGourmetListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_LIST;
        } else if (dailyDeepLink.isGourmetDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_DETAIL;
        } else if (dailyDeepLink.isGourmetRegionListView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC;
        } else if (dailyDeepLink.isGourmetEventBannerWebView() == true)
        {
            screenName = AnalyticsManager.Screen.DAILYGOURMET_BANNER_DETAIL;
        } else if (dailyDeepLink.isBookingView() == true)
        {
            screenName = AnalyticsManager.Screen.BOOKING_LIST;
        } else if (dailyDeepLink.isEventView() == true)
        {
            screenName = AnalyticsManager.Screen.EVENT_LIST;
        } else if (dailyDeepLink.isEventDetailView() == true)
        {
            screenName = AnalyticsManager.Screen.EVENT_DETAIL;
        } else if (dailyDeepLink.isBonusView() == true)
        {
            screenName = AnalyticsManager.Screen.BONUS;
        } else if (dailyDeepLink.isSingUpView() == true)
        {
            screenName = AnalyticsManager.Screen.MENU_REGISTRATION;
        } else if (dailyDeepLink.isInformationView() == true)
        {
            if (DailyHotel.isLogin() == true)
            {
                screenName = AnalyticsManager.Screen.INFORMATION_SIGNIN;
            } else
            {
                screenName = AnalyticsManager.Screen.INFORMATION_SIGNOUT;
            }
        } else if (dailyDeepLink.isCouponView() == true)
        {
            screenName = AnalyticsManager.Screen.MENU_COUPON_BOX;
        } else if (dailyDeepLink.isRecommendFriendView() == true)
        {
            screenName = AnalyticsManager.Screen.MENU_INVITE_FRIENDS;
        }

        if (Util.isTextEmpty(screenName) == false)
        {
            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder()//
                .setCampaignParamsFromUrl(dailyDeepLink.getDeepLink()).build());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserIndex(String index)
    {
        if (Util.isTextEmpty(index) == true)
        {
            mGoogleAnalyticsTracker.set("&uid", AnalyticsManager.ValueType.EMPTY);
        } else
        {
            mGoogleAnalyticsTracker.set("&uid", index);
        }
    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {

    }

    @Override
    void onStart(Activity activity)
    {

    }

    @Override
    void onStop(Activity activity)
    {

    }

    @Override
    void onResume(Activity activity)
    {
    }

    @Override
    void onPause(Activity activity)
    {
    }

    @Override
    void currentAppVersion(String version)
    {

    }

    @Override
    void addCreditCard(String cardType)
    {
    }

    @Override
    void updateCreditCard(String cardTypes)
    {

    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender)
    {
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        Product product = getProcuct(params);
        product.setBrand("hotel");

        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
            .setTransactionId(transId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format("credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_COMPLETE);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        //
        //        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
        //            .setCheckoutStep(5)//
        //            .setTransactionId(transId)//
        //            .setTransactionRevenue(paymentPrice)//
        //            .setTransactionCouponCode(String.format("credit_%s", credit));
        //
        //        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);
        //
        //        mGoogleAnalyticsTracker.set("&cu", "KRW");
        //        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String ticketName = params.get(AnalyticsManager.KeyType.TICKET_NAME);

        recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, AnalyticsManager.Action.HOTEL_PAYMENT_COMPLETED, placeName + "-" + ticketName, null);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : 5 | " + transId + " | " + productAction.toString());
        }
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);
        double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));

        Product product = getProcuct(params);
        product.setBrand("gourmet");

        ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
            .setTransactionId(transId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format("credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(AnalyticsManager.Screen.DAILYGOURMET_PAYMENT_COMPLETE);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        //        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
        //            .setCheckoutStep(5)//
        //            .setTransactionId(transId)//
        //            .setTransactionRevenue(paymentPrice)//
        //            .setTransactionCouponCode(String.format("credit_%s", credit));
        //
        //        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);
        //
        //        mGoogleAnalyticsTracker.set("&cu", "KRW");
        //        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String ticketName = params.get(AnalyticsManager.KeyType.TICKET_NAME);
        String ticketCount = params.get(AnalyticsManager.KeyType.QUANTITY);

        String label = String.format("%s-%s(%s)", placeName, ticketName, ticketCount);
        recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, AnalyticsManager.Action.GOURMET_PAYMENT_COMPLETED, label, null);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : 5 | " + transId + " | " + productAction.toString());
        }
    }

    @Override
    void startDeepLink(Uri deepLinkUri)
    {

    }

    @Override
    void startApplication()
    {

    }

    private Product getProcuct(Map<String, String> params)
    {
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX);

        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String ticketName = params.get(AnalyticsManager.KeyType.TICKET_NAME);

        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String category = params.get(AnalyticsManager.KeyType.CATEGORY);

        String price = params.get(AnalyticsManager.KeyType.PRICE);
        //        String paymentPrice = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        String quantity = params.get(AnalyticsManager.KeyType.QUANTITY);

        //        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        String id;

        if (Util.isTextEmpty(placeIndex, ticketIndex) == false)
        {
            id = placeIndex + "_" + ticketIndex;
        } else if (Util.isTextEmpty(placeIndex) == false)
        {
            id = placeIndex;
        } else
        {
            return null;
        }

        Product product = new Product().setId(id);

        String name = null;

        if (Util.isTextEmpty(placeName, ticketName) == false)
        {
            name = placeName + "_" + ticketName;
        } else if (Util.isTextEmpty(placeName) == false)
        {
            name = placeName;
        }

        if (Util.isTextEmpty(name) == false)
        {
            product.setName(name);
        }

        if (Util.isTextEmpty(grade) == false)
        {
            product.setCategory(grade);
        }

        if (Util.isTextEmpty(category) == false)
        {
            product.setCategory(category);
        }

        if (Util.isTextEmpty(price) == false)
        {
            try
            {
                product.setPrice(Double.parseDouble(price));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        if (Util.isTextEmpty(quantity) == false)
        {
            try
            {
                product.setQuantity(Integer.parseInt(quantity));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Product : " + product.toString());
        }

        return product;
    }

    private HitBuilders.ScreenViewBuilder getScreenViewBuilder(Map<String, String> params, Product product, ProductAction productAction)
    {
        HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction);

        String checkIn = null;

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE);
        }

        String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);
        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT);
        String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE);
        String registeredSimpleCard = params.get(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD);
        String nrd = params.get(AnalyticsManager.KeyType.NRD);
        String category = params.get(AnalyticsManager.KeyType.CATEGORY);
        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX);
        String rating = params.get(AnalyticsManager.KeyType.RATING);
        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE);
        String placeCount = params.get(AnalyticsManager.KeyType.PLACE_COUNT);

        if (Util.isTextEmpty(checkIn) == false)
        {
            screenViewBuilder.setCustomDimension(1, checkIn);
        }

        if (Util.isTextEmpty(checkOut) == false)
        {
            screenViewBuilder.setCustomDimension(2, checkOut);
        }

        if (Util.isTextEmpty(dBenefit) == false)
        {
            screenViewBuilder.setCustomDimension(3, "yes".equalsIgnoreCase(dBenefit) == true ? "y" : "n");
        }

        if (Util.isTextEmpty(paymentType) == false)
        {
            screenViewBuilder.setCustomDimension(4, paymentType);
        }

        if (Util.isTextEmpty(registeredSimpleCard) == false)
        {
            screenViewBuilder.setCustomDimension(9, registeredSimpleCard);
        }

        if (Util.isTextEmpty(nrd) == false)
        {
            screenViewBuilder.setCustomDimension(10, nrd);
        }

        if (Util.isTextEmpty(category) == false)
        {
            screenViewBuilder.setCustomDimension(13, category);
        }

        if (Util.isTextEmpty(grade) == false)
        {
            screenViewBuilder.setCustomDimension(14, grade);
        }

        if (Util.isTextEmpty(placeIndex) == false)
        {
            screenViewBuilder.setCustomDimension(15, placeIndex);
        }

        if (Util.isTextEmpty(listIndex) == false && "-1".equalsIgnoreCase(listIndex) == false)
        {
            screenViewBuilder.setCustomDimension(16, listIndex);
        }

        if (Util.isTextEmpty(rating) == false)
        {
            screenViewBuilder.setCustomDimension(17, rating + "%");
        }

        if (Util.isTextEmpty(isShowOriginalPrice) == false)
        {
            screenViewBuilder.setCustomDimension(18, isShowOriginalPrice.toLowerCase());
        }

        if (Util.isTextEmpty(placeCount) == false && "-1".equalsIgnoreCase(placeCount) == false)
        {
            screenViewBuilder.setCustomDimension(20, placeCount.toLowerCase());
        }

        return screenViewBuilder;
    }

    private void checkoutStep(int step, String screen, String transId, Map<String, String> params)
    {
        String paymentPrice = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        Product product = getProcuct(params);

        ProductAction productAction = new ProductAction(ProductAction.ACTION_CHECKOUT).setCheckoutStep(step);

        if (Util.isTextEmpty(transId) == false)
        {
            productAction.setTransactionId(transId);
        }

        if (Util.isTextEmpty(paymentPrice) == false)
        {
            productAction.setTransactionRevenue(Double.parseDouble(paymentPrice));
        }

        if (Util.isTextEmpty(credit) == false)
        {
            productAction.setTransactionCouponCode(String.format("credit_%s", credit));
        }

        HitBuilders.ScreenViewBuilder screenViewBuilder = getScreenViewBuilder(params, product, productAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.setScreenName(screen);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : " + screen + " | " + step + " | " + transId + " | " + productAction.toString() + " | " + screenViewBuilder.build().toString());
        }
    }

    private void recordSearchAnalytics(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.SEARCH_RESULT_EMPTY.equalsIgnoreCase(screen) == false //
            && AnalyticsManager.Screen.SEARCH_RESULT.equalsIgnoreCase(screen) == false)
        {
            return;
        }

        HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();

        String checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN);
        String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT);

        if (Util.isTextEmpty(checkIn) == false)
        {
            screenViewBuilder.setCustomDimension(1, checkIn);
        }

        if (Util.isTextEmpty(checkOut) == false)
        {
            screenViewBuilder.setCustomDimension(2, checkOut);
        }

        screenViewBuilder.setCustomDimension(6, params.get(AnalyticsManager.KeyType.PLACE_TYPE));
        screenViewBuilder.setCustomDimension(19, params.get(AnalyticsManager.KeyType.PLACE_HIT_TYPE));

        if (AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(screen) == true)
        {
            // CD1, CD2, CD6, CD19
            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
            return;
        }

        screenViewBuilder.setCustomDimension(7, params.get(AnalyticsManager.KeyType.COUNTRY));
        screenViewBuilder.setCustomDimension(8, params.get(AnalyticsManager.KeyType.PROVINCE));

        String district = params.get(AnalyticsManager.KeyType.DISTRICT);
        if (Util.isTextEmpty(district) == false)
        {
            screenViewBuilder.setCustomDimension(12, district);
        }

        String category = params.get(AnalyticsManager.KeyType.CATEGORY);
        if (Util.isTextEmpty(category) == false)
        {
            screenViewBuilder.setCustomDimension(13, category);
        }

        if (AnalyticsManager.Screen.SEARCH_RESULT_EMPTY.equalsIgnoreCase(screen) == true)
        {
            // SEARCH_MAIN + CD7, CD8, CD12, CD13
            mGoogleAnalyticsTracker.setScreenName(screen);
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());
            return;
        }

        mGoogleAnalyticsTracker.setScreenName(screen);
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());
    }
}
