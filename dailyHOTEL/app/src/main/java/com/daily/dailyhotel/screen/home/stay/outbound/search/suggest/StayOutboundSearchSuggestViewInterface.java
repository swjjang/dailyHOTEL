package com.daily.dailyhotel.screen.home.stay.outbound.search.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

public interface StayOutboundSearchSuggestViewInterface extends BaseDialogViewInterface
{
    void setSuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList);

    void setPopularAreaSuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList);

    int getRecentlySuggestEntryCount();

    void setKeywordEditHint(String hint);

    void setKeywordEditText(String text);

    void setVoiceSearchEnabled(boolean enabled);

    void removeRecentlyItem(int position);

    void removeRecentlySection(int menuType);

    void setNearbyStaySuggest(StayOutboundSuggest nearbyStayOutboundSuggest);
}
