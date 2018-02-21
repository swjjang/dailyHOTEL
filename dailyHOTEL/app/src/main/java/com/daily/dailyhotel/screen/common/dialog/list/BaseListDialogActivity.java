package com.daily.dailyhotel.screen.common.dialog.list;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.base.BaseMultiWindowActivity;
import com.daily.dailyhotel.parcel.ListDialogItemParcel;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class BaseListDialogActivity extends BaseMultiWindowActivity<BaseListDialogPresenter>
{
    protected static final String INTENT_EXTRA_DATA_TITLE = "title";
    protected static final String INTENT_EXTRA_DATA_LIST = "list";
    public static final String INTENT_EXTRA_DATA_SELECTED_DATA = "selected";

    public static Intent newInstance(Context context, String titleText //
        , ListDialogItemParcel selectedItem, ArrayList<ListDialogItemParcel> parcelList)
    {
        Intent intent = new Intent(context, BaseListDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, titleText);
        intent.putExtra(INTENT_EXTRA_DATA_SELECTED_DATA, selectedItem);
        intent.putParcelableArrayListExtra(INTENT_EXTRA_DATA_LIST, parcelList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected BaseListDialogPresenter createInstancePresenter()
    {
        return new BaseListDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }
}
