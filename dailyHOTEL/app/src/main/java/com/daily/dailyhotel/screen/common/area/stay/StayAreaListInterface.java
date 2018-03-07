package com.daily.dailyhotel.screen.common.area.stay;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayAreaGroup;

import java.util.List;

import io.reactivex.Observable;

@Deprecated
public interface StayAreaListInterface extends BaseDialogViewInterface
{
    void setAreaList(List<StayAreaGroup> areaList);

    void setLocationText(String locationText);

    void setLocationTermVisible(boolean visible);

    Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation);

    Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation);

    void setSelectedAreaGroup(int groupPosition);
}
