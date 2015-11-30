package android.support.v7.internal.view.menu;

import android.graphics.drawable.Drawable;

public abstract interface MenuView
{
    public abstract void initialize(MenuBuilder paramMenuBuilder);

    public abstract int getWindowAnimations();

    public static abstract interface ItemView
    {
        public abstract void initialize(MenuItemImpl paramMenuItemImpl, int paramInt);

        public abstract MenuItemImpl getItemData();

        public abstract void setTitle(CharSequence paramCharSequence);

        public abstract void setEnabled(boolean paramBoolean);

        public abstract void setCheckable(boolean paramBoolean);

        public abstract void setChecked(boolean paramBoolean);

        public abstract void setShortcut(boolean paramBoolean, char paramChar);

        public abstract void setIcon(Drawable paramDrawable);

        public abstract boolean prefersCondensedTitle();

        public abstract boolean showsIcon();
    }
}
