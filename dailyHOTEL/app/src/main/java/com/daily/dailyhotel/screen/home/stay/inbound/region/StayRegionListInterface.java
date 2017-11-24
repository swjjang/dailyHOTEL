package com.daily.dailyhotel.screen.home.stay.inbound.region;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Region;

import java.util.List;

import io.reactivex.Observable;

public interface StayRegionListInterface extends BaseDialogViewInterface
{
    void setRegionList(List<Region> regionList);

    void setLocationTermVisible(boolean visible);

    Observable<Boolean> collapseGroupWithAnimation(int groupPosition);

    Observable<Boolean> expandGroupWidthAnimation(int groupPosition);
}
