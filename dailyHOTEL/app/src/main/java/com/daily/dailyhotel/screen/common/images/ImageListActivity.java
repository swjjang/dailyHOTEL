package com.daily.dailyhotel.screen.common.images;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.BaseDetailImage;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ImageListActivity extends BaseActivity<ImageListPresenter>
{
    static final String INTENT_EXTRA_DATA_TITLE = "title";
    static final String INTENT_EXTRA_DATA_IMAGE_LIST = "imageList";
    static final String INTENT_EXTRA_DATA_INDEX = "index";

    public static Intent newInstance(Context context, String title, List<? extends BaseDetailImage> arrayList, int index)
    {
        Intent intent = new Intent(context, ImageListActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);

        if (arrayList != null && arrayList.size() > 0)
        {
            ArrayList<Map<String, String>> imageMapList = new ArrayList<>();

            for (BaseDetailImage detailImage : arrayList)
            {
                imageMapList.add(detailImage.getDataMap());
            }

            intent.putExtra(INTENT_EXTRA_DATA_IMAGE_LIST, imageMapList);
        }

        intent.putExtra(INTENT_EXTRA_DATA_INDEX, index);

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
