package com.daily.dailyhotel.screen.common.images;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.parcel.DetailImageInformationParcel;
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ImageListActivity extends BaseActivity<ImageListPresenter>
{
    static final String INTENT_EXTRA_DATA_TITLE = "title";
    static final String INTENT_EXTRA_DATA_BASE_IMAGE_URL = "baseImageUrl";
    static final String INTENT_EXTRA_DATA_IMAGE_LIST = "imageList";
    static final String INTENT_EXTRA_DATA_INDEX = "index";

    public static Intent newInstance(Context context, String title, List<DetailImageInformation> imageList//
        , int index, ImageListAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);

        if (imageList != null && imageList.size() > 0)
        {
            ArrayList<DetailImageInformationParcel> list = new ArrayList<>();

            for (DetailImageInformation detailImageInformation : imageList)
            {
                list.add(new DetailImageInformationParcel(detailImageInformation));
            }

            intent.putExtra(INTENT_EXTRA_DATA_IMAGE_LIST, list);
        }

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    public static Intent newInstance(Context context, String title, String baseImageUrl, List<DetailImageInformation> imageList//
        , int index, ImageListAnalyticsParam analyticsParam)
    {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);
        intent.putExtra(INTENT_EXTRA_DATA_BASE_IMAGE_URL, baseImageUrl);

        if (imageList != null && imageList.size() > 0)
        {
            ArrayList<DetailImageInformationParcel> list = new ArrayList<>();

            for (DetailImageInformation detailImageInformation : imageList)
            {
                list.add(new DetailImageInformationParcel(detailImageInformation));
            }

            intent.putExtra(INTENT_EXTRA_DATA_IMAGE_LIST, list);
        }

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);
        intent.putExtra(INTENT_EXTRA_DATA_ANALYTICS, analyticsParam);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected ImageListPresenter createInstancePresenter()
    {
        return new ImageListPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
