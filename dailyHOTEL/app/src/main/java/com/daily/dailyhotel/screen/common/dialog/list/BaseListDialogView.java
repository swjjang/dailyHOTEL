package com.daily.dailyhotel.screen.common.dialog.list;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.daily.base.BaseActivity;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseMultiWindowView;
import com.daily.dailyhotel.parcel.ListDialogItemParcel;
import com.twoheart.dailyhotel.databinding.DialogListDataBinding;

import java.util.List;

public class BaseListDialogView extends BaseMultiWindowView<BaseListDialogInterface.OnEventListener, DialogListDataBinding> implements BaseListDialogInterface.ViewInterface
{
    BaseListDialogListAdapter mListAdapter;

    public BaseListDialogView(BaseActivity baseActivity, BaseListDialogInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final DialogListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mListAdapter = new BaseListDialogListAdapter(getContext(), null);
        mListAdapter.setSelectedItem(null);

        viewDataBinding.recyclerView.setAdapter(mListAdapter);

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
                if (getViewDataBinding() == null || mListAdapter == null)
                {
                    return;
                }

                getEventListener().onPositiveButtonClick(mListAdapter.getSelectedItem());
            }
        });

        mListAdapter.setOnItemClickListener(new View.OnClickListener()
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
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().titleTextView.setText(title);
    }

    @Override
    public void setData(ListDialogItemParcel selectedItem, List<ListDialogItemParcel> list)
    {
        if (getViewDataBinding() == null)
        {
            getEventListener().onBackClick();
            return;
        }

        if (mListAdapter == null)
        {
            mListAdapter = new BaseListDialogListAdapter(getContext(), list);
            mListAdapter.setSelectedItem(selectedItem);
            getViewDataBinding().recyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setList(list);
            mListAdapter.setSelectedItem(selectedItem);
            mListAdapter.notifyDataSetChanged();
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
