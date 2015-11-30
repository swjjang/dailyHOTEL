package android.support.v7.internal.view.menu;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;

public final class MenuItemImpl implements SupportMenuItem
{
    private static final String TAG = "MenuItemImpl";
    private static final int SHOW_AS_ACTION_MASK = 3;
    private final int mId;
    private final int mGroup;
    private final int mCategoryOrder;
    private final int mOrdering;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private Intent mIntent;
    private char mShortcutNumericChar;
    private char mShortcutAlphabeticChar;
    private Drawable mIconDrawable;
    private int mIconResId = 0;
    private MenuBuilder mMenu;
    private SubMenuBuilder mSubMenu;
    private Runnable mItemCallback;
    private MenuItem.OnMenuItemClickListener mClickListener;
    private int mFlags = 16;
    private static final int CHECKABLE = 1;
    private static final int CHECKED = 2;
    private static final int EXCLUSIVE = 4;
    private static final int HIDDEN = 8;
    private static final int ENABLED = 16;
    private static final int IS_ACTION = 32;
    private int mShowAsAction = 0;
    private View mActionView;
    private android.support.v4.view.ActionProvider mActionProvider;
    private MenuItemCompat.OnActionExpandListener mOnActionExpandListener;
    private boolean mIsActionViewExpanded = false;
    static final int NO_ICON = 0;
    private ContextMenu.ContextMenuInfo mMenuInfo;
    private static String sPrependShortcutLabel;
    private static String sEnterShortcutLabel;
    private static String sDeleteShortcutLabel;
    private static String sSpaceShortcutLabel;

    MenuItemImpl(MenuBuilder menu, int group, int id, int categoryOrder, int ordering, CharSequence title, int showAsAction)
    {
        this.mMenu = menu;
        this.mId = id;
        this.mGroup = group;
        this.mCategoryOrder = categoryOrder;
        this.mOrdering = ordering;
        this.mTitle = title;
        this.mShowAsAction = showAsAction;
    }

    public boolean invoke()
    {
        if ((this.mClickListener != null) && (this.mClickListener.onMenuItemClick(this)))
        {
            return true;
        }
        if (this.mMenu.dispatchMenuItemSelected(this.mMenu.getRootMenu(), this))
        {
            return true;
        }
        if (this.mItemCallback != null)
        {
            this.mItemCallback.run();
            return true;
        }
        if (this.mIntent != null)
        {
            try
            {
                this.mMenu.getContext().startActivity(this.mIntent);
                return true;
            } catch (ActivityNotFoundException e)
            {
                Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", e);
            }
        }
        if ((this.mActionProvider != null) && (this.mActionProvider.onPerformDefaultAction()))
        {
            return true;
        }
        return false;
    }

    public boolean isEnabled()
    {
        return (this.mFlags & 0x10) != 0;
    }

    public MenuItem setEnabled(boolean enabled)
    {
        if (enabled)
        {
            this.mFlags |= 0x10;
        } else
        {
            this.mFlags &= 0xFFFFFFEF;
        }
        this.mMenu.onItemsChanged(false);

        return this;
    }

    public int getGroupId()
    {
        return this.mGroup;
    }

    public int getItemId()
    {
        return this.mId;
    }

    public int getOrder()
    {
        return this.mCategoryOrder;
    }

    public int getOrdering()
    {
        return this.mOrdering;
    }

    public Intent getIntent()
    {
        return this.mIntent;
    }

    public MenuItem setIntent(Intent intent)
    {
        this.mIntent = intent;
        return this;
    }

    Runnable getCallback()
    {
        return this.mItemCallback;
    }

    public MenuItem setCallback(Runnable callback)
    {
        this.mItemCallback = callback;
        return this;
    }

    public char getAlphabeticShortcut()
    {
        return this.mShortcutAlphabeticChar;
    }

    public MenuItem setAlphabeticShortcut(char alphaChar)
    {
        if (this.mShortcutAlphabeticChar == alphaChar)
        {
            return this;
        }
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);

        this.mMenu.onItemsChanged(false);

        return this;
    }

    public char getNumericShortcut()
    {
        return this.mShortcutNumericChar;
    }

    public MenuItem setNumericShortcut(char numericChar)
    {
        if (this.mShortcutNumericChar == numericChar)
        {
            return this;
        }
        this.mShortcutNumericChar = numericChar;

        this.mMenu.onItemsChanged(false);

        return this;
    }

    public MenuItem setShortcut(char numericChar, char alphaChar)
    {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);

        this.mMenu.onItemsChanged(false);

        return this;
    }

    char getShortcut()
    {
        return this.mShortcutAlphabeticChar;
    }

    String getShortcutLabel()
    {
        char shortcut = getShortcut();
        if (shortcut == 0)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder(sPrependShortcutLabel);
        switch (shortcut)
        {
            case '\n':
                sb.append(sEnterShortcutLabel);
                break;
            case '\b':
                sb.append(sDeleteShortcutLabel);
                break;
            case ' ':
                sb.append(sSpaceShortcutLabel);
                break;
            default:
                sb.append(shortcut);
        }
        return sb.toString();
    }

    boolean shouldShowShortcut()
    {
        return (this.mMenu.isShortcutsVisible()) && (getShortcut() != 0);
    }

    public SubMenu getSubMenu()
    {
        return this.mSubMenu;
    }

    public boolean hasSubMenu()
    {
        return this.mSubMenu != null;
    }

    void setSubMenu(SubMenuBuilder subMenu)
    {
        this.mSubMenu = subMenu;

        subMenu.setHeaderTitle(getTitle());
    }

    public CharSequence getTitle()
    {
        return this.mTitle;
    }

    CharSequence getTitleForItemView(MenuView.ItemView itemView)
    {
        return (itemView != null) && (itemView.prefersCondensedTitle()) ? getTitleCondensed() : getTitle();
    }

    public MenuItem setTitle(CharSequence title)
    {
        this.mTitle = title;

        this.mMenu.onItemsChanged(false);
        if (this.mSubMenu != null)
        {
            this.mSubMenu.setHeaderTitle(title);
        }
        return this;
    }

    public MenuItem setTitle(int title)
    {
        return setTitle(this.mMenu.getContext().getString(title));
    }

    public CharSequence getTitleCondensed()
    {
        return this.mTitleCondensed != null ? this.mTitleCondensed : this.mTitle;
    }

    public MenuItem setTitleCondensed(CharSequence title)
    {
        this.mTitleCondensed = title;
        if (title == null)
        {
            title = this.mTitle;
        }
        this.mMenu.onItemsChanged(false);

        return this;
    }

    public Drawable getIcon()
    {
        if (this.mIconDrawable != null)
        {
            return this.mIconDrawable;
        }
        if (this.mIconResId != 0)
        {
            Drawable icon = this.mMenu.getResources().getDrawable(this.mIconResId);
            this.mIconResId = 0;
            this.mIconDrawable = icon;
            return icon;
        }
        return null;
    }

    public MenuItem setIcon(Drawable icon)
    {
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        this.mMenu.onItemsChanged(false);

        return this;
    }

    public MenuItem setIcon(int iconResId)
    {
        this.mIconDrawable = null;
        this.mIconResId = iconResId;

        this.mMenu.onItemsChanged(false);

        return this;
    }

    public boolean isCheckable()
    {
        return (this.mFlags & 0x1) == 1;
    }

    public MenuItem setCheckable(boolean checkable)
    {
        int oldFlags = this.mFlags;
        this.mFlags = (this.mFlags & 0xFFFFFFFE | (checkable ? 1 : 0));
        if (oldFlags != this.mFlags)
        {
            this.mMenu.onItemsChanged(false);
        }
        return this;
    }

    public void setExclusiveCheckable(boolean exclusive)
    {
        this.mFlags = (this.mFlags & 0xFFFFFFFB | (exclusive ? 4 : 0));
    }

    public boolean isExclusiveCheckable()
    {
        return (this.mFlags & 0x4) != 0;
    }

    public boolean isChecked()
    {
        return (this.mFlags & 0x2) == 2;
    }

    public MenuItem setChecked(boolean checked)
    {
        if ((this.mFlags & 0x4) != 0)
        {
            this.mMenu.setExclusiveItemChecked(this);
        } else
        {
            setCheckedInt(checked);
        }
        return this;
    }

    void setCheckedInt(boolean checked)
    {
        int oldFlags = this.mFlags;
        this.mFlags = (this.mFlags & 0xFFFFFFFD | (checked ? 2 : 0));
        if (oldFlags != this.mFlags)
        {
            this.mMenu.onItemsChanged(false);
        }
    }

    public boolean isVisible()
    {
        if ((this.mActionProvider != null) && (this.mActionProvider.overridesItemVisibility()))
        {
            return ((this.mFlags & 0x8) == 0) && (this.mActionProvider.isVisible());
        }
        return (this.mFlags & 0x8) == 0;
    }

    boolean setVisibleInt(boolean shown)
    {
        int oldFlags = this.mFlags;
        this.mFlags = (this.mFlags & 0xFFFFFFF7 | (shown ? 0 : 8));
        return oldFlags != this.mFlags;
    }

    public MenuItem setVisible(boolean shown)
    {
        if (setVisibleInt(shown))
        {
            this.mMenu.onItemVisibleChanged(this);
        }
        return this;
    }

    public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener clickListener)
    {
        this.mClickListener = clickListener;
        return this;
    }

    public String toString()
    {
        return this.mTitle.toString();
    }

    void setMenuInfo(ContextMenu.ContextMenuInfo menuInfo)
    {
        this.mMenuInfo = menuInfo;
    }

    public ContextMenu.ContextMenuInfo getMenuInfo()
    {
        return this.mMenuInfo;
    }

    public void actionFormatChanged()
    {
        this.mMenu.onItemActionRequestChanged(this);
    }

    public boolean shouldShowIcon()
    {
        return this.mMenu.getOptionalIconsVisible();
    }

    public boolean isActionButton()
    {
        return (this.mFlags & 0x20) == 32;
    }

    public boolean requestsActionButton()
    {
        return (this.mShowAsAction & 0x1) == 1;
    }

    public boolean requiresActionButton()
    {
        return (this.mShowAsAction & 0x2) == 2;
    }

    public void setIsActionButton(boolean isActionButton)
    {
        if (isActionButton)
        {
            this.mFlags |= 0x20;
        } else
        {
            this.mFlags &= 0xFFFFFFDF;
        }
    }

    public boolean showsTextAsAction()
    {
        return (this.mShowAsAction & 0x4) == 4;
    }

    public void setShowAsAction(int actionEnum)
    {
        switch (actionEnum & 0x3)
        {
            case 0:
            case 1:
            case 2:
                break;
            default:
                throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
        }
        this.mShowAsAction = actionEnum;
        this.mMenu.onItemActionRequestChanged(this);
    }

    public SupportMenuItem setActionView(View view)
    {
        this.mActionView = view;
        this.mActionProvider = null;
        if ((view != null) && (view.getId() == -1) && (this.mId > 0))
        {
            view.setId(this.mId);
        }
        this.mMenu.onItemActionRequestChanged(this);
        return this;
    }

    public SupportMenuItem setActionView(int resId)
    {
        Context context = this.mMenu.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        setActionView(inflater.inflate(resId, new LinearLayout(context), false));
        return this;
    }

    public View getActionView()
    {
        if (this.mActionView != null)
        {
            return this.mActionView;
        }
        if (this.mActionProvider != null)
        {
            this.mActionView = this.mActionProvider.onCreateActionView(this);
            return this.mActionView;
        }
        return null;
    }

    public MenuItem setActionProvider(android.view.ActionProvider actionProvider)
    {
        throw new UnsupportedOperationException("Implementation should use setSupportActionProvider!");
    }

    public android.view.ActionProvider getActionProvider()
    {
        throw new UnsupportedOperationException("Implementation should use getSupportActionProvider!");
    }

    public android.support.v4.view.ActionProvider getSupportActionProvider()
    {
        return this.mActionProvider;
    }

    public SupportMenuItem setSupportActionProvider(android.support.v4.view.ActionProvider actionProvider)
    {
        if (this.mActionProvider == actionProvider)
        {
            return this;
        }
        this.mActionView = null;
        if (this.mActionProvider != null)
        {
            this.mActionProvider.setVisibilityListener(null);
        }
        this.mActionProvider = actionProvider;
        this.mMenu.onItemsChanged(true);
        if (actionProvider != null)
        {
            actionProvider.setVisibilityListener(new ActionProvider.VisibilityListener()
            {
                public void onActionProviderVisibilityChanged(boolean isVisible)
                {
                    MenuItemImpl.this.mMenu.onItemVisibleChanged(MenuItemImpl.this);
                }
            });
        }
        return this;
    }

    public SupportMenuItem setShowAsActionFlags(int actionEnum)
    {
        setShowAsAction(actionEnum);
        return this;
    }

    public boolean expandActionView()
    {
        if (((this.mShowAsAction & 0x8) == 0) || (this.mActionView == null))
        {
            return false;
        }
        if ((this.mOnActionExpandListener == null) || (this.mOnActionExpandListener.onMenuItemActionExpand(this)))
        {
            return this.mMenu.expandItemActionView(this);
        }
        return false;
    }

    public boolean collapseActionView()
    {
        if ((this.mShowAsAction & 0x8) == 0)
        {
            return false;
        }
        if (this.mActionView == null)
        {
            return true;
        }
        if ((this.mOnActionExpandListener == null) || (this.mOnActionExpandListener.onMenuItemActionCollapse(this)))
        {
            return this.mMenu.collapseItemActionView(this);
        }
        return false;
    }

    public SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener listener)
    {
        this.mOnActionExpandListener = listener;
        return this;
    }

    public boolean hasCollapsibleActionView()
    {
        return ((this.mShowAsAction & 0x8) != 0) && (this.mActionView != null);
    }

    public void setActionViewExpanded(boolean isExpanded)
    {
        this.mIsActionViewExpanded = isExpanded;
        this.mMenu.onItemsChanged(false);
    }

    public boolean isActionViewExpanded()
    {
        return this.mIsActionViewExpanded;
    }

    public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener listener)
    {
        throw new UnsupportedOperationException("Implementation should use setSupportOnActionExpandListener!");
    }
}
