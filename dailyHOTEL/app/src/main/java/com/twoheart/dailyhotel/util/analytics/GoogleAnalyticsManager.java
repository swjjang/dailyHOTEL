package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.Map;

public class GoogleAnalyticsManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[GoogleAnalyticsManager]";
    private static final String GA_PROPERTY_ID = "UA-43721645-6";

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

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mClientId = googleAnalytics.getClientId();

                if (listener != null)
                {
                    listener.onResponseClientId(mClientId);
                }
            }
        }).start();

        mGoogleAnalyticsTracker = googleAnalytics.newTracker(GA_PROPERTY_ID);
        mGoogleAnalyticsTracker.enableAdvertisingIdCollection(true);
        mGoogleAnalyticsTracker.set("&cu", "KRW");
    }

    public String getClientId()
    {
        return mClientId;
    }

    @Override
    void recordScreen(String screen)
    {
        mGoogleAnalyticsTracker.setScreenName(screen);
        mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Screen : " + screen);
        }
    }

    @Override
    public void recordScreen(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screen) == true || AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screen) == true)
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
        }
    }

    @Override
    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        long value = 0L;

        if (params != null)
        {

        }

        mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
            .setCategory(category).setAction(action)//
            .setLabel(label).setValue(value).build());

        if (DEBUG == true)
        {
            ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setUserIndex(String index)
    {
        if (Util.isTextEmpty(index) == true)
        {
            mGoogleAnalyticsTracker.set("&uid", "");
        } else
        {
            mGoogleAnalyticsTracker.set("&uid", index);
        }
    }

    @Override
    public void onStart(Activity activity)
    {

    }

    @Override
    public void onStop(Activity activity)
    {

    }

    @Override
    public void onResume(Activity activity)
    {
    }

    @Override
    public void onPause(Activity activity)
    {
    }

    @Override
    public void addCreditCard(String cardType)
    {
    }

    @Override
    public void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    public void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType)
    {
    }

    @Override
    public void purchaseCompleteHotel(String transId, Map<String, String> params)
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
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());
        //
        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
            .setCheckoutStep(5)//
            .setTransactionId(transId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format("credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String ticketName = params.get(AnalyticsManager.KeyType.TICKET_NAME);

        recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS, AnalyticsManager.Action.HOTEL_PAYMENT_COMPLETED, placeName + "-" + ticketName, null);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "checkoutStep : 5 | " + transId + " | " + productAction.toString());
        }
    }

    @Override
    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
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
        mGoogleAnalyticsTracker.send(screenViewBuilder.build());

        ProductAction productCheckoutAction = new ProductAction(ProductAction.ACTION_CHECKOUT)//
            .setCheckoutStep(5)//
            .setTransactionId(transId)//
            .setTransactionRevenue(paymentPrice)//
            .setTransactionCouponCode(String.format("credit_%s", credit));

        HitBuilders.ScreenViewBuilder screenCheckoutViewBuilder = getScreenViewBuilder(params, product, productCheckoutAction);

        mGoogleAnalyticsTracker.set("&cu", "KRW");
        mGoogleAnalyticsTracker.send(screenCheckoutViewBuilder.build());

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

    private Product getProcuct(Map<String, String> params)
    {
        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX);

        String placeName = params.get(AnalyticsManager.KeyType.NAME);
        String ticketName = params.get(AnalyticsManager.KeyType.TICKET_NAME);

        String grade = params.get(AnalyticsManager.KeyType.GRADE);
        String category = params.get(AnalyticsManager.KeyType.CATEGORY);

        String price = params.get(AnalyticsManager.KeyType.PRICE);
        String paymentPrice = params.get(AnalyticsManager.KeyType.PAYMENT_PRICE);
        String quantity = params.get(AnalyticsManager.KeyType.QUANTITY);

        String credit = params.get(AnalyticsManager.KeyType.USED_BOUNS);

        String id;

        if (Util.isTextEmpty(placeIndex) == false && Util.isTextEmpty(ticketIndex) == false)
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

        if (Util.isTextEmpty(placeName) == false && Util.isTextEmpty(ticketName) == false)
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
        String address = params.get(AnalyticsManager.KeyType.ADDRESS);
        String hotelCategory = params.get(AnalyticsManager.KeyType.HOTEL_CATEGORY);

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
            screenViewBuilder.setCustomDimension(3, dBenefit);
        }

        if (Util.isTextEmpty(paymentType) == false)
        {
            screenViewBuilder.setCustomDimension(4, paymentType);
        }

        if (Util.isTextEmpty(address) == false)
        {
            screenViewBuilder.setCustomDimension(9, address);
        }

        if (Util.isTextEmpty(hotelCategory) == false)
        {
            screenViewBuilder.setCustomDimension(10, hotelCategory);
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
            ExLog.d(TAG + "checkoutStep : " + step + " | " + transId + " | " + productAction.toString());
        }
    }
}
