package com.daily.dailyhotel.screen.home.search.gourmet.result;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetResultTabActivity extends BaseActivity<SearchGourmetResultTabPresenter>
{
    public static final int REQUEST_CODE_CALENDAR = 10000;
    public static final int REQUEST_CODE_FILTER = 10001;
    public static final int REQUEST_CODE_RESEARCH = 10002;
    public static final int REQUEST_CODE_DETAIL = 10005;
    public static final int REQUEST_CODE_PREVIEW = 10006;
    public static final int REQUEST_CODE_PERMISSION_MANAGER = 10007;
    public static final int REQUEST_CODE_SETTING_LOCATION = 10008;
    public static final int REQUEST_CODE_WISH_DIALOG = 10009;
    public static final int REQUEST_CODE_CALL = 10010;

    static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    static final String INTENT_EXTRA_DATA_SUGGEST = "suggest";
    static final String INTENT_EXTRA_DATA_INPUT_KEYWORD = "inputKeyword";

    public static Intent newInstance(Context context, String visitDateTime, GourmetSuggestV2 suggest, String inputKeyWord)
    {
        Intent intent = new Intent(context, SearchGourmetResultTabActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(suggest));
        intent.putExtra(INTENT_EXTRA_DATA_INPUT_KEYWORD, inputKeyWord);

        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, SearchGourmetResultTabActivity.class);

        if (DailyTextUtils.isTextEmpty(deepLink) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected SearchGourmetResultTabPresenter createInstancePresenter()
    {
        return new SearchGourmetResultTabPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
