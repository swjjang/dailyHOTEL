package com.daily.dailyhotel.screen.common.call;

import android.app.Dialog;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;

public class CallDialogView extends BaseDialogView<CallDialogView.OnEventListener, ViewDataBinding> implements CallDialogInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onCallClick();
    }

    public CallDialogView(BaseActivity baseActivity, CallDialogView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ViewDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void showCallDialog(String message, Dialog.OnCancelListener onCancelListener)
    {
        showSimpleDialog(getString(R.string.dialog_notice2), message, //
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel) //
            , new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onCallClick();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onCancelListener != null)
                    {
                        onCancelListener.onCancel(null);
                    }
                }
            }, onCancelListener, null, true);
    }

    @Override
    public void showClosedTimeDialog(String message, Dialog.OnDismissListener onDismissListener)
    {
        showSimpleDialog(getString(R.string.dialog_information), message, //
            getString(R.string.dialog_btn_text_confirm), null, onDismissListener);
    }
}
