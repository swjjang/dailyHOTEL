package com.daily.dailyhotel.screen.home.search.stay.inbound;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.daily.base.BaseFragmentDialogView;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayDataBinding;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayFragmentView extends BaseFragmentDialogView<SearchStayFragmentInterface.OnEventListener, FragmentSearchStayDataBinding>//
    implements SearchStayFragmentInterface.ViewInterface
{
    public SearchStayFragmentView(SearchStayFragmentInterface.OnEventListener listener)
    {
        super(listener);
    }

    @Override
    protected void setContentView(FragmentSearchStayDataBinding viewDataBinding)
    {

    }

    class TagArrayAdapter extends ArrayAdapter<String>
    {
        public TagArrayAdapter(@NonNull Context context, int resource)
        {
            super(context, resource);
        }

        public void setData()
        {

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            return super.getView(position, convertView, parent);
        }
    }
}
