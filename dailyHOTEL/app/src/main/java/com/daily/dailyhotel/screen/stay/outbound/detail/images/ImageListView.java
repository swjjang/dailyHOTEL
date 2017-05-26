package com.daily.dailyhotel.screen.stay.outbound.detail.images;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityCopyDataBinding;

public class ImageListView extends BaseView<ImageListView.OnEventListener, ActivityCopyDataBinding> implements ImageListInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public ImageListView(BaseActivity baseActivity, ImageListView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityCopyDataBinding viewDataBinding)
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
}
