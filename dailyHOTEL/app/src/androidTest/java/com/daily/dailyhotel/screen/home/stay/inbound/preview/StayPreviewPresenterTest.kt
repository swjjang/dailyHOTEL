package com.daily.dailyhotel.screen.home.stay.inbound.preview

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.text.SpannableStringBuilder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class StayPreviewPresenterTest {

    private lateinit var presenter : StayPreviewPresenter

    @Rule
    lateinit var activityTestRule : ActivityTestRule<StayPreviewActivity>

    @Before
    fun setUp() {
        activityTestRule = ActivityTestRule(StayPreviewActivity::class.java)
        presenter = StayPreviewPresenter(activityTestRule.activity)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun getTrueReviewCountText() {
        assertTrue { presenter.getTrueReviewCountText(0).toString() == "0개 트루리뷰" }
        assertTrue { presenter.getTrueReviewCountText(1).toString() == "1개 트루리뷰" }
        assertTrue { presenter.getTrueReviewCountText(1000).toString() == "1,000개 트루리뷰" }
    }
}