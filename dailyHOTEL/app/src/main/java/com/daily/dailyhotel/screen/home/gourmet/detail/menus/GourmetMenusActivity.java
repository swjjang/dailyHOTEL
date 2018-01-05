package com.daily.dailyhotel.screen.home.gourmet.detail.menus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.parcel.GourmetMenuParcel;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetMenusActivity extends BaseActivity<GourmetMenusPresenter>
{
    static final int REQUEST_CODE_IMAGE_LIST = 10000;

    static final String INTENT_EXTRA_DATA_MENU_LIST = "menuList";
    static final String INTENT_EXTRA_DATA_GOURMET_INDEX = "gourmetIndex";
    static final String INTENT_EXTRA_DATA_GOURMET_NAME = "gourmetName";
    static final String INTENT_EXTRA_DATA_POSITION = "position";
    static final String INTENT_EXTRA_DATA_OPERATION_TIMES = "operationTimes";
    static final String INTENT_EXTRA_DATA_VISIT_DATE_TIME = "visitDateTime";
    public static final String INTENT_EXTRA_DATA_VISIT_TIME = "visitTime";
    static final String INTENT_EXTRA_DATA_CATEGORY = "category";
    static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";

    public static Intent newInstance(Context context, String visitDateTime, int gourmetIndex, String gourmetName//
        , List<GourmetMenu> gourmetMenuList, int position, ArrayList<Integer> operationTimeList, String visitTime, String category, String imageUrl)
    {
        Intent intent = new Intent(context, GourmetMenusActivity.class);

        ArrayList<GourmetMenuParcel> gourmetMenuParcelList = new ArrayList<>();

        if (gourmetMenuList != null)
        {
            for (GourmetMenu gourmetMenu : gourmetMenuList)
            {
                gourmetMenuParcelList.add(new GourmetMenuParcel(gourmetMenu));
            }
        }

        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE_TIME, visitDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_INDEX, gourmetIndex);
        intent.putExtra(INTENT_EXTRA_DATA_GOURMET_NAME, gourmetName);
        intent.putExtra(INTENT_EXTRA_DATA_MENU_LIST, gourmetMenuParcelList);
        intent.putExtra(INTENT_EXTRA_DATA_POSITION, position);
        intent.putExtra(INTENT_EXTRA_DATA_OPERATION_TIMES, operationTimeList);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_TIME, visitTime);
        intent.putExtra(INTENT_EXTRA_DATA_CATEGORY, category);
        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);

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
    protected GourmetMenusPresenter createInstancePresenter()
    {
        return new GourmetMenusPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}
