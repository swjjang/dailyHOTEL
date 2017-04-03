package com.daily.base;

import android.content.Intent;
import android.os.Bundle;

public interface BaseActivityInterface
{
    void onIntent(Intent intent);

    void onResume();

    void onPause();

    void onDestroy();

    boolean onBackPressed();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedInstanceState);
}
