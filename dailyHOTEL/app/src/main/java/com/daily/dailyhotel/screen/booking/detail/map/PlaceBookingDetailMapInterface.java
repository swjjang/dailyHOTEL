package com.daily.dailyhotel.screen.booking.detail.map;

import android.support.v4.app.FragmentManager;

import com.daily.base.BaseDialogViewInterface;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

import java.util.List;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public interface PlaceBookingDetailMapInterface extends BaseDialogViewInterface
{
    void setPlaceList(FragmentManager fragmentManager, List<PlaceViewItem> listItems, PlaceBookingDay placeBookingDay);
}
