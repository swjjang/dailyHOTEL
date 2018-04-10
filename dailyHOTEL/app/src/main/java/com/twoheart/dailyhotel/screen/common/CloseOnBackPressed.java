/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * CloseOnBackPressed
 * <p>
 * Android 디바이스의 Back 버튼을 이용한 임의의 어플리케이션 인스턴스 종
 * 료를 방지하기 위한 클래스이다. 이 클래스를 가지는 Activity에서는 Back
 * 버튼을 연속 두 번 눌러야지만 종료가 된다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.common;

import android.widget.Toast;

import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.CartLocalImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.main.MainActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class CloseOnBackPressed
{
    long backPressedTime = 0;
    MainActivity mActivity;

    public CloseOnBackPressed(MainActivity activity)
    {
        mActivity = activity;

    }

    //    public boolean onBackPressed()
    //    {
    //        if (System.currentTimeMillis() <= backPressedTime + 2000)
    //        {
    //            return true;
    //        }
    //
    //        backPressedTime = System.currentTimeMillis();
    //
    //        DailyToast.showToast(mActivity, mActivity.getString(R.string.toast_msg_backpressed), Toast.LENGTH_SHORT);
    //
    //        return false;
    //    }

    public Observable<Boolean> onBackPressed()
    {
        return new CartLocalImpl().hasGourmetCart(mActivity).observeOn(AndroidSchedulers.mainThread()).map(new Function<Boolean, Boolean>()
        {
            @Override
            public Boolean apply(Boolean hasGourmetCart) throws Exception
            {
                if (hasGourmetCart == true)
                {
                    DailyToast.showToast(mActivity, mActivity.getString(R.string.toast_msg_backpressed_has_cart), Toast.LENGTH_LONG);
                } else
                {
                    DailyToast.showToast(mActivity, mActivity.getString(R.string.toast_msg_backpressed), Toast.LENGTH_SHORT);
                }

                if (System.currentTimeMillis() <= backPressedTime + 2000)
                {
                    return true;
                }

                backPressedTime = System.currentTimeMillis();

                return false;
            }
        });
    }
}
