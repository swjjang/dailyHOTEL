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
    public static final String INTENT_EXTRA_DATA_INDEX = "index";

    public static Intent newInstance(Context context, List<GourmetMenu> gourmetMenuList, int index)
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

        intent.putExtra(INTENT_EXTRA_DATA_MENU_LIST, gourmetMenuParcelList);
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
