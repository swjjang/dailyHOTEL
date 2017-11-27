package com.daily.dailyhotel.screen.common.region.stay;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.Region;

import java.util.List;

import io.reactivex.Observable;

public interface StayRegionListInterface extends BaseDialogViewInterface
{
    void setRegionList(List<Region> regionList);

    void setLocationTermVisible(boolean visible);

    Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation);

    Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation);
}
