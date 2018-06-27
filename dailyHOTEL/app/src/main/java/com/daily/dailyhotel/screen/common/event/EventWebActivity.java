package com.daily.dailyhotel.screen.common.event;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.daily.base.BaseActivity;
import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class EventWebActivity extends BaseActivity<EventWebPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TYPE = "type";
    protected static final String INTENT_EXTRA_DATA_URL = "url";
    protected static final String INTENT_EXTRA_DATA_EVENT_NAME = "eventName";
    protected static final String INTENT_EXTRA_DATA_EVENT_DESCRIPTION = "eventDescription";
    protected static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";
    protected static final String INTENT_EXTRA_DATA_SHARE = "share";

    public enum EventType
    {
        EVENT,
        HOME_EVENT,
    }

    public static Intent newInstance(Context context, EventType eventType, String pageUrl, String eventName //
        , String eventDescription, String imageUrl)
    {
        if (eventType == null || DailyTextUtils.isTextEmpty(pageUrl))
        {
            return null;
        }

        Intent intent = new Intent(context, EventWebActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TYPE, eventType.name());
        intent.putExtra(INTENT_EXTRA_DATA_URL, pageUrl);
        intent.putExtra(INTENT_EXTRA_DATA_EVENT_NAME, DailyTextUtils.isTextEmpty(eventName) ? "" : eventName);
        intent.putExtra(INTENT_EXTRA_DATA_EVENT_DESCRIPTION, DailyTextUtils.isTextEmpty(eventDescription) ? "" : eventDescription);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, DailyTextUtils.isTextEmpty(imageUrl) ? "" : imageUrl);

        return intent;
    }

    public static Intent newInstance(Context context, EventType eventType, String pageUrl, String eventName //
        , String eventDescription, String imageUrl, boolean share)
    {
        if (eventType == null || DailyTextUtils.isTextEmpty(pageUrl))
        {
            return null;
        }

        return newInstance(context, eventType, pageUrl, eventName, eventDescription, imageUrl).putExtra(INTENT_EXTRA_DATA_SHARE, share);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected EventWebPresenter createInstancePresenter()
    {
        return new EventWebPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
