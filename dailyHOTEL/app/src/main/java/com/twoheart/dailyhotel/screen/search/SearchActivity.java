package com.twoheart.dailyhotel.screen.search;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceSearchFragment;
import com.twoheart.dailyhotel.screen.search.gourmet.GourmetSearchFragment;
import com.twoheart.dailyhotel.screen.search.stay.StaySearchFragment;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity
{
    private SearchFragmentPagerAdapter mSearchFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        initLayout();
    }

    protected void initLayout()
    {
        ArrayList<PlaceSearchFragment> fragmentList = new ArrayList<>();

        StaySearchFragment staySearchFragment = new StaySearchFragment();
        fragmentList.add(staySearchFragment);

        GourmetSearchFragment gourmetSearchFragment = new GourmetSearchFragment();
        fragmentList.add(gourmetSearchFragment);

        mSearchFragmentPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);

        initToolbar();
    }

    private void initToolbar()
    {
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }
}