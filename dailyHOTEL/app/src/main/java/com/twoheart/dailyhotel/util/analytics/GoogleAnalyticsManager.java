package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.Map;

public class GoogleAnalyticsManager implements IBaseAnalyticsManager
{
    private static final String GA_PROPERTY_ID = "UA-43721645-6";
    private Tracker mGoogleAnalyticsTracker;

    public GoogleAnalyticsManager(Context context)
    {
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setLocalDispatchPeriod(60);

        mGoogleAnalyticsTracker = googleAnalytics.newTracker(GA_PROPERTY_ID);
        mGoogleAnalyticsTracker.enableAdvertisingIdCollection(true);
    }

    @Override
    public void recordScreen(String screenName, Map<String, String> params)
    {
        try
        {
            // Send a screen view.
            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder().setAll(params).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void recordEvent(String category, String action, String label, Long value)
    {
        try
        {
            mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
                .setCategory(category).setAction(action)//
                .setLabel(label).setValue(value).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        try
        {
            mGoogleAnalyticsTracker.send(new HitBuilders.EventBuilder()//
                .setCategory(category).setAction(action)//
                .setLabel(label).setAll(params).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setUserIndex(String index)
    {
        mGoogleAnalyticsTracker.set("userId", index);
    }

    @Override
    public void onResume(Activity activity)
    {
    }

    @Override
    public void eventPaymentCardAdded(String cardType)
    {

    }

    @Override
    public void recordSocialRegistration(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
    }

    @Override
    public void recordRegistration(String userIndex, String email, String name, String phoneNumber, String userType)
    {
    }

    @Override
    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        try
        {
            String hotelName = params.get(AnalyticsManager.KeyType.NAME);
            String roomIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX);
            double price = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));
            int quantity = Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY));

            Product product = new Product().setId(roomIndex).setName(hotelName)//
                .setCategory(AnalyticsManager.Label.HOTEL).setBrand("DAILYHOTEL").setPrice(price).setQuantity(quantity);//
            //                .setCustomDimension(1, "User Index : " + userIndex)//
            //                .setCustomDimension(2, "Check-In : " + checkInTime)//
            //                .setCustomDimension(3, "Check-Out : " + checkOutTime)//
            //                .setCustomDimension(4, "Pay Type" + payType)//
            //                .setCustomDimension(5, "Current Time : " + currentTime);

            ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
                .setTransactionId(transId);

            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction);

            mGoogleAnalyticsTracker.set("&cu", "KRW");
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            recordEvent("Purchase", "PurchaseComplete", "PurchaseComplete", 1L);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        try
        {
            String name = params.get(AnalyticsManager.KeyType.NAME);
            String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX);
            double price = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));
            int quantity = Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY));

            Product product = new Product().setId(ticketIndex).setName(name)//
                .setCategory(AnalyticsManager.Label.GOURMET).setBrand("DAILYHOTEL").setPrice(price).setQuantity(quantity);//
            //                .setCustomDimension(1, "User Index : " + userIndex)//
            //                .setCustomDimension(2, "Check-In : " + checkInTime)//
            //                .setCustomDimension(3, "Check-Out : " + checkOutTime)//
            //                .setCustomDimension(4, "Pay Type" + payType)//
            //                .setCustomDimension(5, "Current Time : " + currentTime);

            ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
                .setTransactionId(transId);

            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction);

            mGoogleAnalyticsTracker.set("&cu", "KRW");
            mGoogleAnalyticsTracker.send(screenViewBuilder.build());

            recordEvent("Purchase", "PurchaseComplete", "PurchaseComplete", 1L);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
