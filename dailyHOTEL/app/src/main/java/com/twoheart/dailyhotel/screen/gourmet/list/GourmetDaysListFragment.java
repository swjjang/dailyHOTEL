/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelListFragment (호텔 목록 화면)
 * <p>
 * 어플리케이션의 가장 주가 되는 화면으로서 호텔들의 목록을 보여주는 화면이다.
 * 호텔 리스트는 따로 커스텀되어 구성되어 있으며, 액션바의 네비게이션을 이용
 * 하여 큰 지역을 분리하고 리스트뷰 헤더를 이용하여 세부 지역을 나누어 표시
 * 한다. 리스트뷰의 맨 첫 아이템은 이벤트 참여하기 버튼이 있으며, 이 버튼은
 * 서버의 이벤트 API에 따라 NEW 아이콘을 붙여주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.gourmet.list;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.screen.common.list.CalendarActivity;
import com.twoheart.dailyhotel.util.Constants;

public class GourmetDaysListFragment extends GourmetListFragment
{
    private SaleTime mSelectedSaleTime;

    @Override
    public void setSaleTime(SaleTime saleTime)
    {
        super.setSaleTime(saleTime);

        if (mSelectedSaleTime == null)
        {
            mSelectedSaleTime = saleTime.getClone(saleTime.getOffsetDailyDay());
        }
    }

    @Override
    public void onPageSelected()
    {
        super.onPageSelected();

        SaleTime saleTime = mSaleTime.getClone(0);

        Intent intent = CalendarActivity.newInstance(getContext(), PlaceType.FNB, saleTime);
        getParentFragment().startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);
    }

    @Override
    protected SaleTime getSelectedSaleTime()
    {
        return mSelectedSaleTime;
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
                    mSelectedSaleTime = data.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_CHECKINDATE);

                    mOnCommunicateListener.selectDay(mSelectedSaleTime, true);
                } else
                {
                    if (mGourmetRecycleView.getVisibility() == View.VISIBLE && mGourmetRecycleView.getAdapter() != null)
                    {
                        if (mGourmetRecycleView.getAdapter().getItemCount() == 0)
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
