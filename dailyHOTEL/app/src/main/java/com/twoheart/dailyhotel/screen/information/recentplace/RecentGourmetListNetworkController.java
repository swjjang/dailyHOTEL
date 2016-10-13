package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Context;

import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.place.base.OnBaseNetworkControllerListener;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentGourmetListNetworkController extends BaseNetworkController
{
    public RecentGourmetListNetworkController(Context context, String networkTag, OnBaseNetworkControllerListener listener)
    {
        super(context, networkTag, listener);
    }

    public interface OnNetworkControllerListener extends OnBaseNetworkControllerListener
    {
        void onRecentGourmetList();
    }

    public void requestRecentGourmetList() {
        ((RecentGourmetListNetworkController.OnNetworkControllerListener) mOnNetworkControllerListener).onRecentGourmetList();
    }
}
