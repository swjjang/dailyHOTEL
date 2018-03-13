package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggestV2;

import java.util.List;

public interface SearchStaySuggestInterface extends BaseDialogViewInterface
{
    void setStaySuggests(List<StaySuggestV2> staySuggestList);

    void setGourmetSuggests(List<GourmetSuggest> gourmetSuggestList);

    void setStayOutboundSuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(StaySuggestV2 locationSuggest, List<StaySuggestV2> staySuggestList);

    void setPopularAreaSuggests(StaySuggestV2 locationSuggest, List<StaySuggestV2> staySuggestList);

    int getRecentlySuggestAllEntryCount();

    void setKeywordEditHint(String hint);

    void setKeywordEditText(String text);

    void setVoiceSearchEnabled(boolean enabled);

    void removeRecentlyItem(int position);

    void setNearbyStaySuggest(StaySuggestV2 nearbyStaySuggest);
}
