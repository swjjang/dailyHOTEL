package com.daily.dailyhotel.screen.stay.outbound;

import com.daily.base.BaseViewInterface;
import com.daily.dailyhotel.entity.Suggest;

import java.util.List;

public interface OutboundViewInterface extends BaseViewInterface
{
    void onReset();

    void setRecentlySuggests(List<Suggest> suggestList);

    void setRecentlySuggestsVisibility(boolean visibility);
}
