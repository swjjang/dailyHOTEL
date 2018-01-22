package com.daily.dailyhotel.screen.home.search;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.FragmentSearchGourmetDataBinding;

public class SearchGourmetFragment extends Fragment
{
    FragmentSearchGourmetDataBinding mViewDataBinding;

    SearchPresenter.SearchModel mSearchModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_gourmet_data, container, false);

        return mViewDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mSearchModel = ViewModelProviders.of(getActivity()).get(SearchPresenter.SearchModel.class);
    }
}
