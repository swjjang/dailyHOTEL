package com.daily.dailyhotel.screen.home.search.gourmet.suggest;

import com.daily.base.BaseDialogViewInterface;
import com.daily.dailyhotel.entity.GourmetSuggestV2;

import java.util.List;

public interface SearchGourmetSuggestInterface extends BaseDialogViewInterface
{
    void setSuggests(List<GourmetSuggestV2> gourmetSuggestList);

    void setSuggest(String suggest);

    void showKeyboard();

    void hideKeyboard();

    void setProgressBarVisible(boolean visible);

    void setRecentlySuggests(GourmetSuggestV2 locationSuggest, List<GourmetSuggestV2> gourmetSuggestList);

    void setPopularAreaSuggests(GourmetSuggestV2 locationSuggest, List<GourmetSuggestV2> gourmetSuggestList);

    int getRecentlySuggestAllEntryCount();

    void setKeywordEditHint(String hint);

    void setKeywordEditText(String text);

    void setVoiceSearchEnabled(boolean enabled);

    void removeRecentlyItem(int position);

    void setNearbyGourmetSuggest(GourmetSuggestV2 nearbyGourmetSuggest);
}
