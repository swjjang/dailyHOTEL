package com.daily.dailyhotel.screen.copy.kotlin

import com.daily.base.BaseAnalyticsInterface
import com.daily.base.BaseDialogViewInterface
import com.daily.base.OnBaseEventListener

interface CopyInterface {
    interface ViewInterface : BaseDialogViewInterface {
    }

    interface OnEventListener : OnBaseEventListener {
    }

    interface AnalyticsInterface : BaseAnalyticsInterface {
    }
}
