package com.daily.dailyhotel.screen.event.list.kotlin

import android.content.Intent
import android.net.Uri
import com.daily.base.util.DailyTextUtils
import com.daily.dailyhotel.base.BaseExceptionPresenter
import com.daily.dailyhotel.repository.remote.EventRemoteImpl
import com.daily.dailyhotel.screen.common.event.EventWebActivity
import com.daily.dailyhotel.storage.preference.DailyPreference
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.network.model.Event
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.DailyDeepLink
import com.twoheart.dailyhotel.util.DailyExternalDeepLink
import io.reactivex.android.schedulers.AndroidSchedulers

class EventListPresenter(activity: EventListActivity)//
    : BaseExceptionPresenter<EventListActivity, EventListInterface.ViewInterface>(activity), EventListInterface.OnEventListener {
    private val analytics: EventListInterface.AnalyticsInterface by lazy {
        EventListAnalyticsImpl()
    }

    private var dailyDeepLink: DailyDeepLink? = null

    private val eventRemoteImpl: EventRemoteImpl by lazy {
        EventRemoteImpl()
    }

    override fun createInstanceViewInterface(): EventListInterface.ViewInterface {
        return EventListView(activity, this)
    }

    override fun constructorInitialize(activity: EventListActivity) {
        setContentView(R.layout.activity_event_list_data)

        isRefresh = true

        DailyPreference.getInstance(activity).setNewEvent(false)
        DailyPreference.getInstance(activity).viewedEventTime = DailyPreference.getInstance(activity).latestEventTime
    }

    override fun onPostCreate() {
    }

    override fun onIntent(intent: Intent?): Boolean {
        if (intent == null) {
            return true
        }

        initDeepLink(intent)
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let { initDeepLink(it) }
    }

    override fun onStart() {
        super.onStart()

        analytics.onScreen(activity)

        if (dailyDeepLink != null) {
            val externalDeepLink: DailyExternalDeepLink? = dailyDeepLink as? DailyExternalDeepLink
            externalDeepLink?.let { deepLink ->
                deepLink.isEventDetailView.let {
                    startEventWeb(externalDeepLink.url, externalDeepLink.title, externalDeepLink.description, externalDeepLink.imageUrl)
                }
            }

            dailyDeepLink?.clear()
            dailyDeepLink = null
        } else {
            if (isRefresh) {
                onRefresh(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isRefresh) {
            onRefresh(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        unLockAll()

        when (requestCode) {
            Constants.CODE_REQUEST_ACTIVITY_EVENTWEB -> {
                isRefresh = false

                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME) {
                    setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                    finish()
                }
            }
        }
    }

    @Synchronized
    override fun onRefresh(showProgress: Boolean) {
        if (isFinish || !isRefresh) {
            return
        }

        isRefresh = false
        screenLock(showProgress)

        addCompositeDisposable(eventRemoteImpl.eventList
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    viewInterface.onEventList(it)
                    unLockAll()
                }, {
                    onHandleError(it)
                }))
    }

    override fun onBackClick() {
        activity.onBackPressed()
    }

    ////////////////////////
    override fun onItemClick(event: Event) {
        if (lock()) {
            return
        }

        val imageUrl = if (DailyTextUtils.isTextEmpty(event.lowResolutionImageUrl)) event.defaultImageUrl else event.lowResolutionImageUrl
        startEventWeb(event.linkUrl, event.title, event.description, imageUrl)
    }

    override fun onHomeButtonClick() {
        setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME)
        finish()
    }

    ///////////////////

    private fun initDeepLink(intent: Intent) {
        dailyDeepLink = try {
            DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)))
        } catch (exception: Exception) {
            null
        }
    }

    private fun startEventWeb(url: String, eventName: String, eventDescription: String, imageUrl: String) {
        if (DailyTextUtils.isTextEmpty(url)) {
            return
        }

        val intent: Intent = EventWebActivity.newInstance(activity, EventWebActivity.EventType.EVENT, url, eventName, eventDescription, imageUrl)
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_EVENTWEB)
    }
}