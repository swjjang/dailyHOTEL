package com.twoheart.dailyhotel.screen.information.terms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class TermsNPolicyActivity extends BaseActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms_and_policy);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.frag_terms_and_policy), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        View termsLayout = findViewById(R.id.termsLayout);
        View personalLayout = findViewById(R.id.personalLayout);
        View locationLayout = findViewById(R.id.locationLayout);
        View youthtermsLayout = findViewById(R.id.youthtermsLayout);
        View licenseLayout = findViewById(R.id.licenseLayout);

        termsLayout.setOnClickListener(this);
        personalLayout.setOnClickListener(this);
        locationLayout.setOnClickListener(this);
        youthtermsLayout.setOnClickListener(this);
        licenseLayout.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        unLockUI();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.termsLayout:
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = new Intent(this, TermActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.personalLayout:
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = new Intent(this, PrivacyActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.locationLayout:
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = new Intent(this, LocationTermsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.youthtermsLayout:
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = new Intent(this, ProtectYouthTermsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.licenseLayout:
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                Intent intent = new Intent(this, LicenseActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
