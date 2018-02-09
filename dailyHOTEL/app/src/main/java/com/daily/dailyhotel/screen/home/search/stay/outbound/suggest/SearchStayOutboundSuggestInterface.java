package com.daily.dailyhotel.screen.home.search.stay.outbound.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.StayOutboundSuggest;

import java.util.List;

public interface SearchStayOutboundSuggestInterface extends BaseDialogViewInterface
{
    void setSuggests(List<StayOutboundSuggest> stayOutboundSuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setEmptySuggestsVisible(boolean visible);

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList);

    void setPopularAreaSuggests(StayOutboundSuggest locationSuggest, List<StayOutboundSuggest> stayOutboundSuggestList);

    int getRecentlySuggestAllEntryCount();

    void setKeywordEditHint(String hint);

    void setKeywordEditText(String text);

    void setVoiceSearchEnabled(boolean enabled);

    void removeRecentlyItem(int position);

    void setNearbyStaySuggest(StayOutboundSuggest nearbyStayOutboundSuggest);
}
