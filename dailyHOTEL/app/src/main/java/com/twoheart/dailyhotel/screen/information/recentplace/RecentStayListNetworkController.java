package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentStayListNetworkController extends BaseNetworkController
{
    public RecentStayListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRecentStayList();
    }

    public void requestRecentStayList() {
        ((RecentStayListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentStayList();
    }
}
