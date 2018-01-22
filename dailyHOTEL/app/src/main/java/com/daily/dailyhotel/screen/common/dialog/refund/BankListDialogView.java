package com.daily.dailyhotel.screen.common.dialog.refund;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseMultiWindowView;
import com.daily.dailyhotel.entity.Bank;
import com.twoheart.dailyhotel.databinding.DialogRefundBankListDataBinding;

import java.util.List;

public class BankListDialogView extends BaseMultiWindowView<BankListDialogView.OnEventListener, DialogRefundBankListDataBinding> implements BankListDialogInterface
{
    BankListAdapter mBankListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onNativeButtonClick();

        void onPositiveButtonClick(Bank selectedBank);

        void checkConfigChange();
    }

    public BankListDialogView(BaseActivity baseActivity, BankListDialogView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final DialogRefundBankListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mBankListAdapter = new BankListAdapter(getContext(), null);
        mBankListAdapter.setSelectedBank(null);
        viewDataBinding.recyclerView.setAdapter(mBankListAdapter);

        viewDataBinding.negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onNativeButtonClick();
            }
        });

        viewDataBinding.positiveTextView.setEnabled(false);
        viewDataBinding.positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (getViewDataBinding() == null || mBankListAdapter == null)
                {
                    return;
                }

                getEventListener().onPositiveButtonClick(mBankListAdapter.getSelectedBank());
            }
        });

        mBankListAdapter.setOnItemClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewDataBinding.positiveTextView.setEnabled(true);
            }
        });

        getEventListener().checkConfigChange();
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setBankList(Bank selectedBank, List<Bank> bankList)
    {
        if (getViewDataBinding() == null)
        {
            getEventListener().onBackClick();
            return;
        }

        if (mBankListAdapter == null)
        {
            mBankListAdapter = new BankListAdapter(getContext(), null);
            mBankListAdapter.setSelectedBank(null);
            getViewDataBinding().recyclerView.setAdapter(mBankListAdapter);
        } else
        {
            mBankListAdapter.setBankList(bankList);
            mBankListAdapter.setSelectedBank(selectedBank);
            mBankListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConfigurationChange(int orientation, boolean isInMultiWindowMode)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isTabletDevice() == true)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 10 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 13 / 15;
        }

        if (isInMultiWindowMode == true)
        {
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
        } else
        {
            layoutParams.height = ScreenUtils.getScreenHeight(getContext()) * 56 / 100;
        }

        getViewDataBinding().getRoot().setLayoutParams(layoutParams);
    }
}
