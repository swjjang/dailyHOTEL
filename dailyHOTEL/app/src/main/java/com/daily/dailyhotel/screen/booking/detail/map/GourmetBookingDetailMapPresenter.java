package com.daily.dailyhotel.screen.booking.detail.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.Pair;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.List;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class GourmetBookingDetailMapPresenter extends PlaceBookingDetailMapPresenter
{
    private GourmetBookingDetailMapAnalyticsInterface mAnalytics;

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    public interface GourmetBookingDetailMapAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onItemClick(Activity activity, Gourmet gourmet, boolean isCallByThankYou);
    }

    public GourmetBookingDetailMapPresenter(@NonNull PlaceBookingDetailMapActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected PlaceBookingDetailMapInterface createInstanceViewInterface()
    {
        return new GourmetBookingDetailMapView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(PlaceBookingDetailMapActivity activity)
    {
        super.constructorInitialize(activity);

        mAnalytics = new GourmetBookingDetailMapAnalyticsImpl();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void startPlaceDetail(View view, PlaceBookingDay placeBookingDay, Place place)
    {
        if (view == null || placeBookingDay == null || place == null)
        {
            return;
        }

        Gourmet gourmet = (Gourmet) place;
        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) placeBookingDay;

        // --> 추후에 정리되면 메소드로 수정
        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();
        analyticsParam.price = gourmet.price;
        analyticsParam.discountPrice = gourmet.discountPrice;
        analyticsParam.setShowOriginalPriceYn(analyticsParam.price, analyticsParam.discountPrice);
        analyticsParam.setProvince(null);
        analyticsParam.entryPosition = gourmet.entryPosition;
        analyticsParam.totalListCount = -1;
        analyticsParam.isDailyChoice = gourmet.isDailyChoice;
        analyticsParam.setAddressAreaName(gourmet.addressSummary);

        if (Util.isUsedMultiTransition() == true)
        {
            getActivity().setExitSharedElementCallback(new SharedElementCallback()
            {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                    for (View view : sharedElements)
                    {
                        if (view instanceof SimpleDraweeView)
                        {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                , gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                , gourmet.category, gourmet.isSoldOut, false, false, true//
                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_MAP//
                , analyticsParam);

            if (intent == null)
            {
                Util.restartApp(getActivity());
                return;
            }

            View simpleDraweeView = view.findViewById(R.id.simpleDraweeView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            Pair[] pairs = new Pair[3];
            pairs[0] = Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
            pairs[1] = Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
            pairs[2] = Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
        } else
        {
            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , gourmet.index, gourmet.name, gourmet.imageUrl, gourmet.discountPrice//
                , gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT)//
                , gourmet.category, gourmet.isSoldOut, false, false, false//
                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                , analyticsParam);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }

        mAnalytics.onItemClick(getActivity(), gourmet, isCallByThankYou());
    }

    @Override
    protected PlaceBookingDetailMapView getBookingDetailMapView()
    {
        return new GourmetBookingDetailMapView(getActivity(), this);
    }
}
