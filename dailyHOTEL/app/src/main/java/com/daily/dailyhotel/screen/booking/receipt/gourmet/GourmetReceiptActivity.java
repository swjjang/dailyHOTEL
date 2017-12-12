package com.daily.dailyhotel.screen.booking.receipt.gourmet;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetReceiptActivity extends BaseActivity<GourmetReceiptPresenter>
{
    public static final String INTENT_EXTRA_RESERVATION_INDEX = "reservationIndex";
    public static final String INTENT_EXTRA_AGGREGATION_ID = "aggregationId";

    public static final int REQUEST_CODE_EMAIL = 10000;

    public static Intent newInstance(Context context, int reservationIndex, String aggregationId)
    {
        Intent intent = new Intent(context, GourmetReceiptActivity.class);

        intent.putExtra(INTENT_EXTRA_RESERVATION_INDEX, reservationIndex);
        intent.putExtra(INTENT_EXTRA_AGGREGATION_ID, aggregationId);

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
    protected GourmetReceiptPresenter createInstancePresenter()
    {
        return new GourmetReceiptPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
