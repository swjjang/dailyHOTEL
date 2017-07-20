package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;

import com.daily.base.BaseDialogViewInterface;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public interface PlaceBookingDetailMapInterface extends BaseDialogViewInterface
{
    void initMapLayout(FragmentManager fragmentManager);

    void setPlaceList(ArrayList<Place> placeList, PlaceBookingDay placeBookingDay);

    void setPlaceMapViewPagerList(Context context, ArrayList<Place> placeList);

    void setMapViewPagerVisibility(boolean isVisible);

    boolean isMapViewPagerVisibility();

    Observable<Long> getLocationAnimation();

    void setMyLocation(Location location);

    void addMarker(double lat, double lng, String placeName);
}