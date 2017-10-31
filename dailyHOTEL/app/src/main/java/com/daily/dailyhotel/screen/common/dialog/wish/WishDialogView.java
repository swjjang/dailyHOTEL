package com.daily.dailyhotel.screen.common.dialog.wish;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.databinding.ActivityWishDialogDataBinding;

import io.reactivex.Observable;

public class WishDialogView extends BaseDialogView<WishDialogView.OnEventListener, ActivityWishDialogDataBinding> implements WishDialogInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public WishDialogView(BaseActivity baseActivity, WishDialogView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityWishDialogDataBinding viewDataBinding)
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
    public Observable<Boolean> showWishView(boolean myWish)
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return myWish ? getViewDataBinding().wishAnimationView.addWishAnimation() : getViewDataBinding().wishAnimationView.removeWishAnimation();
    }
}
