package com.twoheart.dailyhotel.screen.information.recentplace;

import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public class RecentPlacesTabActivity extends BaseActivity
{
    private RecentPlaces mStayRecentPlaces;
    private RecentPlaces mGourmetRecentPlaces;

    private StayRecentPlacesListFragment mStayRecentPlacesListFragment;
    private GourmetRecentPlacesListFragment mGourmetRecentPlacesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recent_places);

        String stayString = DailyPreference.getInstance(this).getStayRecentPlaces();
        mStayRecentPlaces = new RecentPlaces(stayString);

        String gourmetString = DailyPreference.getInstance(this).getGourmetRecentPlaces();
        mGourmetRecentPlaces = new RecentPlaces(gourmetString);

        initLayout();
    }

    private void initLayout()
    {
        initToolbar();

        ArrayList<RecentPlacesListFragment> fragmentList = new ArrayList<>();

        if (mStayRecentPlaces.size() > 0) {
            mStayRecentPlacesListFragment = new StayRecentPlacesListFragment();

        }

        if (mGourmetRecentPlaces.size() > 0) {
            mGourmetRecentPlacesListFragment = new GourmetRecentPlacesListFragment();
        }
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        View backView = toolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

}
