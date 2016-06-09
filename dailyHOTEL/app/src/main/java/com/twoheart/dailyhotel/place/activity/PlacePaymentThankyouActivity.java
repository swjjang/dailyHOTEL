package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public abstract class PlacePaymentThankyouActivity extends BaseActivity implements OnClickListener
{
    protected static final String INTENT_EXTRA_DATA_IMAGEURL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_PLACE = "place";
    protected static final String INTENT_EXTRA_DATA_PLACE_TYPE = "placeType";
    protected static final String INTENT_EXTRA_DATA_DATEL = "date";

    protected abstract void recordEvent(String action, String label);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_result);

        Intent intent = getIntent();

        if (intent == null)
        {
            Util.restartApp(this);
            return;
        }

        String imageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGEURL);
        String place = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE);
        String placeType = intent.getStringExtra(INTENT_EXTRA_DATA_PLACE_TYPE);
        String date = intent.getStringExtra(INTENT_EXTRA_DATA_DATEL);

        initToolbar();
        initLayout(imageUrl, place, placeType, date);
    }

    private void initToolbar()
    {
        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);
    }

    private void initLayout(String imageUrl, String place, String placeType, String date)
    {
        if (Util.isTextEmpty(place) == true || Util.isTextEmpty(placeType) == true || Util.isTextEmpty(date) == true)
        {
            Util.restartApp(this);
            return;
        }

        com.facebook.drawee.view.SimpleDraweeView simpleDraweeVie = (com.facebook.drawee.view.SimpleDraweeView) findViewById(R.id.placeImageView);
        TextView placeTextView = (TextView) findViewById(R.id.bookingPlaceTextView);
        TextView placeTypeTextView = (TextView) findViewById(R.id.placeTypeTextView);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        View confirmView = findViewById(R.id.confirmView);

        Util.requestImageResize(this, simpleDraweeVie, imageUrl);
        placeTextView.setText(place);
        placeTypeTextView.setText(placeType);
        dateTextView.setText(date);

        confirmView.setOnClickListener(this);
    }

    @Override
    public void finish()
    {
        setResult(RESULT_OK);

        super.finish();

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.CLOSE_BUTTON_CLICKED);
                finish();
                break;
            case R.id.confirmView:
                recordEvent(AnalyticsManager.Action.THANKYOU_SCREEN_BUTTON_CLICKED, AnalyticsManager.Label.VIEW_BOOKING_STATUS_CLICKED);
                finish();
                break;
        }
    }
}
