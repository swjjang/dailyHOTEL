package com.daily.dailyhotel.screen.booking.detail.map;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityPlaceBookingDetailMapDataBinding;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class PlaceBookingDetailMapView extends BaseDialogView<PlaceBookingDetailMapView.OnEventListener, ActivityPlaceBookingDetailMapDataBinding> //
    implements PlaceBookingDetailMapInterface
{

    public interface OnEventListener extends OnBaseEventListener
    {

    }

    public PlaceBookingDetailMapView(BaseActivity baseActivity, PlaceBookingDetailMapView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    public void setToolbarTitle(String title)
    {

    }

    @Override
    protected void setContentView(ActivityPlaceBookingDetailMapDataBinding viewDataBinding)
    {

    }
}
