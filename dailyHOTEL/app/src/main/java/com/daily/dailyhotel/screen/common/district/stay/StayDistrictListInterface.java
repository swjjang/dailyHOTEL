package com.daily.dailyhotel.screen.common.district.stay;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.District;
import com.daily.dailyhotel.entity.StayDistrict;

import java.util.List;

import io.reactivex.Observable;

public interface StayDistrictListInterface extends BaseDialogViewInterface
{
    void setDistrictList(List<StayDistrict> districtList);

    void setLocationTermVisible(boolean visible);

    Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation);

    Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation);
}
