package com.daily.dailyhotel.screen.copy.kotlin

import android.content.Intent
import android.os.Bundle
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.entity.ReviewScores
import com.daily.dailyhotel.entity.Stay
import com.daily.dailyhotel.entity.StayBookDateTime
import com.daily.dailyhotel.entity.StayDetail
import com.daily.dailyhotel.repository.remote.StayRemoteImpl
import com.twoheart.dailyhotel.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer

class StayPreviewPresenter(activity: StayPreviewActivity)//
    : BaseExceptionPresenter<StayPreviewActivity, StayPreviewInterface.ViewInterface>(activity), StayPreviewInterface.OnEventListener {

    private lateinit var stayRemoteImpl: StayRemoteImpl

    private lateinit var stayBookDateTime: StayBookDateTime
    private var stayIndex: Int = -1
    private lateinit var stayName: String
    private lateinit var stayGrade: Stay.Grade
    private var viewPrice: Int = StayPreviewActivity.SKIP_CHECK_PRICE_VALUE

    private lateinit var stayDetail: StayDetail
    private var reviewScoreCount: Int = 0

    private val analytics: StayPreviewInterface.AnalyticsInterface by lazy {
        StayPreviewAnalyticsImpl()
    }

    override fun createInstanceViewInterface(): StayPreviewInterface.ViewInterface {
        return StayPreviewView(activity, this)
    }

    override fun constructorInitialize(activity: StayPreviewActivity) {
        setContentView(R.layout.activity_copy_data)

        stayRemoteImpl = StayRemoteImpl(activity)

        isRefresh = true
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }

        var checkInDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        var checkOutDateTime = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        try {
            stayBookDateTime = StayBookDateTime(checkInDateTime, checkOutDateTime)
        } catch (e: Exception) {
            return false
        }

        stayIndex = intent.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_INDEX, 0)

        if (stayIndex <= 0) {
            return false
        }

        stayName = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_NAME)
        var grade = intent.getStringExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_GRADE)

        try {
            stayGrade = Stay.Grade.valueOf(grade)
        } catch (e: Exception) {
            stayGrade = Stay.Grade.etc
        }

        viewPrice = intent.getIntExtra(StayPreviewActivity.INTENT_EXTRA_DATA_STAY_VIEW_PRICE, StayPreviewActivity.SKIP_CHECK_PRICE_VALUE)

        return true
    }

    override fun onNewIntent(intent: Intent?) {
    }

    override fun onStart() {
        super.onStart()

        if (isRefresh) {
            onRefresh(true)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isRefresh) {
            onRefresh(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        unLockAll()
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
        screenLock(showProgress)

        addCompositeDisposable(Observable.zip(stayRemoteImpl.getDetail(stayIndex, stayBookDateTime), stayRemoteImpl.getReviewScores(stayIndex)
                , BiFunction<StayDetail, ReviewScores, Int> { stayDetail, reviewScores ->
            this@StayPreviewPresenter.stayDetail = stayDetail
            reviewScores.reviewScoreTotalCount
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(Consumer<Int> { reviewScoreCount ->
            this@StayPreviewPresenter.reviewScoreCount = reviewScoreCount

            notifyDataSetChanged()
        }, Consumer<Throwable> { throwable -> onHandleError(throwable) }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    override fun onDetailClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun notifyDataSetChanged() {

        if(::stayDetail.isInitialized)
        {

        } else
        {

        }


        viewInterface.setName(stayName)
        viewInterface.set
    }
}