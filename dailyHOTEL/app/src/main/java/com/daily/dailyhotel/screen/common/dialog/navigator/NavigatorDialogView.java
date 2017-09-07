package com.daily.dailyhotel.screen.common.dialog.navigator;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DialogNavigatorInboundDataBinding;
import com.twoheart.dailyhotel.databinding.DialogNavigatorOutboundDataBinding;

public class NavigatorDialogView extends BaseDialogView<NavigatorDialogView.OnEventListener, ViewDataBinding> implements NavigatorDialogInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onKakaoMapClick();

        void onNaverMapClick();

        void onGoogleMapClick();

        void onTMapMapClick();

        void onKakaoNaviClick();
    }

    public NavigatorDialogView(BaseActivity baseActivity, NavigatorDialogView.OnEventListener listener)
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
    public void showNavigatorInboundDialog(boolean skTelecomOperation)
    {
        DialogNavigatorInboundDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_navigator_inbound_data, null, false);

        int tMapIconResId;
        if (skTelecomOperation == true)
        {
            tMapIconResId = R.drawable.ic_tmap_red;
        } else
        {
            tMapIconResId = R.drawable.ic_tmap_green;
        }

        viewDataBinding.tMapTextView.setCompoundDrawablesWithIntrinsicBounds(0, tMapIconResId, 0, 0);

        viewDataBinding.kakaoMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onKakaoMapClick();
            }
        });

        viewDataBinding.naverMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onNaverMapClick();
            }
        });

        viewDataBinding.googleMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onGoogleMapClick();
            }
        });

        viewDataBinding.tMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onTMapMapClick();
            }
        });

        viewDataBinding.kakaoNaviTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onKakaoNaviClick();
            }
        });

        showSimpleDialog(viewDataBinding.getRoot(), null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                getEventListener().onBackClick();
            }
        }, true);
    }

    @Override
    public void showNavigatorOutboundDialog()
    {
        DialogNavigatorOutboundDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_navigator_outbound_data, null, false);

        viewDataBinding.googleMapTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onGoogleMapClick();
            }
        });

        showSimpleDialog(viewDataBinding.getRoot(), null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                getEventListener().onBackClick();
            }
        }, true);
    }
}
