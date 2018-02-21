package com.daily.dailyhotel.screen.mydaily.profile.password;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityCheckPasswordDataBinding;

public class CheckPasswordView extends BaseDialogView<CheckPasswordView.OnEventListener, ActivityCheckPasswordDataBinding> implements CheckPasswordInterface, View.OnFocusChangeListener
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onConfirmClick(String password);
    }

    public CheckPasswordView(BaseActivity baseActivity, CheckPasswordView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCheckPasswordDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initLayout(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityCheckPasswordDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setTitleText(R.string.label_leave_daily);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initLayout(ActivityCheckPasswordDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.confirmView.setEnabled(false);

        viewDataBinding.passwordEditText.setDeleteButtonVisible(null);
        viewDataBinding.passwordEditText.setOnFocusChangeListener(this);
        viewDataBinding.passwordEditText.addTextChangedListener(new TextWatcher()
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
                viewDataBinding.confirmView.setEnabled(editable.length() > 0);

                if (editable.length() > getContext().getResources().getInteger(R.integer.max_password))
                {
                    editable.delete(editable.length() - 1, editable.length());

                    DailyToast.showToast(getContext(), getString(R.string.toast_msg_wrong_max_password_length), Toast.LENGTH_SHORT);
                }
            }
        });

        viewDataBinding.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        hideKeyboard();
                        viewDataBinding.confirmView.performClick();
                        return true;

                    default:
                        return false;
                }
            }
        });

        viewDataBinding.confirmView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                int length = getViewDataBinding().passwordEditText.getText().length();
                if (length == 0)
                {
                    return;
                }

                getEventListener().onConfirmClick(getViewDataBinding().passwordEditText.getText().toString());
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus)
    {
        switch (view.getId())
        {
            case R.id.passwordEditText:
                setFocusLabelView(getViewDataBinding().passwordTextView, getViewDataBinding().passwordEditText, hasFocus);
                break;
        }
    }

    private void setFocusLabelView(View labelView, EditText editText, boolean hasFocus)
    {
        if (hasFocus == true)
        {
            labelView.setActivated(false);
            labelView.setSelected(true);
        } else
        {
            if (editText.length() > 0)
            {
                labelView.setActivated(true);
            }

            labelView.setSelected(false);
        }
    }

    @Override
    public void showKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().passwordEditText.setFocusable(true);
        getViewDataBinding().passwordEditText.setFocusableInTouchMode(true);
        getViewDataBinding().passwordEditText.requestFocus();
        getViewDataBinding().passwordEditText.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(getViewDataBinding().passwordEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    @Override
    public void hideKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getViewDataBinding().passwordEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
