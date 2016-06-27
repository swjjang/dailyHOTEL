package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class GourmetDaysListFragment extends GourmetListFragment
{
    @Override
    public void onPageSelected(String tabText)
    {
        super.onPageSelected(tabText);

        boolean isSelected = true;

        if (getString(R.string.label_selecteday).equalsIgnoreCase(tabText) == true)
        {
            isSelected = false;
        } else
        {
            AnalyticsManager.getInstance(getContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.LIST, null);
        }

        Intent intent = GourmetCalendarActivity.newInstance(getContext(), mSaleTime, AnalyticsManager.ValueType.LIST, isSelected, true);
        getParentFragment().startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    mSaleTime = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_SALETIME);

                    mOnCommunicateListener.selectDay(mSaleTime, true);
                } else
                {
                    if (mGourmetRecyclerView == null)
                    {
                        Util.restartApp(getContext());
                        return;
                    }

                    if (mGourmetRecyclerView.getVisibility() == View.VISIBLE && mGourmetRecyclerView.getAdapter() != null)
                    {
                        if (mGourmetRecyclerView.getAdapter().getItemCount() == 0)
                        {
                            fetchList();
                        }
                    }
                }
                break;
            }
        }
    }
}
