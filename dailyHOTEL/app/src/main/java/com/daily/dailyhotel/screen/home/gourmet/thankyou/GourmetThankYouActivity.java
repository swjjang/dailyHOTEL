package com.daily.dailyhotel.screen.home.gourmet.thankyou;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.parcel.GourmetCartParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetThankYouAnalyticsParam;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetThankYouActivity extends BaseActivity<GourmetThankYouPresenter>
{
    static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";
    static final String INTENT_EXTRA_DATA_GOURMET = "gourmetCart";
    static final String INTENT_EXTRA_DATA_PERSONS = "persons";

    public static Intent newInstance(Context context, GourmetCart gourmetCart, String aggregationId, int persons, GourmetThankYouAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, GourmetThankYouActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_GOURMET, new GourmetCartParcel(gourmetCart));
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);
        intent.putExtra(INTENT_EXTRA_DATA_PERSONS, persons);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected GourmetThankYouPresenter createInstancePresenter()
    {
        return new GourmetThankYouPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.abc_fade_out);
    }
}
