package com.daily.dailyhotel.screen.common.dialog.email.receipt;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyEditText;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DialogSendEmailDataBinding;

public class EmailDialogView extends BaseDialogView<EmailDialogView.OnEventListener, ViewDataBinding> implements EmailDialogInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onSendEmailClick(String email);
    }

    public EmailDialogView(BaseActivity baseActivity, EmailDialogView.OnEventListener listener)
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
    public void showEmailDialog(String email, DialogInterface.OnCancelListener cancelListener)
    {
        if (getContext() == null || cancelListener == null)
        {
            return;
        }

        DialogSendEmailDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_send_email_data, null, false);

        dataBinding.emailEditTExt.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dataBinding.emailEditTExt.setText(email);
        dataBinding.emailEditTExt.setSelection(dataBinding.emailEditTExt.length());

        // 버튼
        dataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (cancelListener == null)
                {
                    return;
                }

                cancelListener.onCancel(null);
            }
        });

        dataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onSendEmailClick(dataBinding.emailEditTExt.getText().toString());
            }
        });

        dataBinding.emailEditTExt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable == null || editable.length() == 0)
                {
                    dataBinding.positiveTextView.setEnabled(false);
                } else
                {
                    dataBinding.positiveTextView.setEnabled(true);
                }
            }
        });

        showSimpleDialog(dataBinding.getRoot(), cancelListener, null, false);
    }
}
