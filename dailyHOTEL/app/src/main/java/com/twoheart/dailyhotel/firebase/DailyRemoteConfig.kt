package com.twoheart.dailyhotel.firebase

import android.content.Context
import android.util.Pair
import com.crashlytics.android.Crashlytics
import com.daily.base.util.DailyTextUtils
import com.daily.base.util.ExLog
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference
import com.daily.dailyhotel.util.takeNotEmpty
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.firebase.model.*
import com.twoheart.dailyhotel.util.Constants
import com.twoheart.dailyhotel.util.Util
import org.json.JSONObject
import java.io.File

class DailyRemoteConfig(private val context: Context) {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    interface OnCompleteListener {
        fun onComplete(currentVersion: String?, forceVersion: String?)
    }

    init {
        remoteConfig.setConfigSettings(FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(Constants.DEBUG).build())
    }

    fun requestRemoteConfig(listener: OnCompleteListener) {
        if (DailyTextUtils.isTextEmpty(DailyRemoteConfigPreference.getInstance(context).remoteConfigCompanyName)) {
            setCompany(context, context.getString(R.string.default_company_information))
        }

        var fetchTime = if (Constants.DEBUG) 0L else 600L

        remoteConfig.fetch(fetchTime).addOnCompleteListener(com.google.android.gms.tasks.OnCompleteListener {
            if (it.isSuccessful) {
                remoteConfig.activateFetched()
                setRemoteConfig(listener)
            }
        }).addOnFailureListener(OnFailureListener {
            if (it is FirebaseRemoteConfigFetchThrottledException) {
                try {
                    setRemoteConfig(listener)
                    return@OnFailureListener
                } catch (e: Exception) {
                    Crashlytics.logException(e)
                }
            } else {
                Crashlytics.logException(it)
            }

            listener.onComplete(null, null)
        })
    }

    internal fun setCompany(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val company = CompanyDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).setRemoteConfigCompanyInformation(company.name
                        , company.ceo, company.bizRegNumber, company.itcRegNumber, company.address1
                        , company.phoneNumber1, company.fax1, company.privacyManager)
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    private fun setRemoteConfig(listener: OnCompleteListener) {
        updateSplash(context, remoteConfig.getString("Marketing_ANDSplash"))
        setCompany(context, remoteConfig.getString("companyInfo"))
        setPayment(context, remoteConfig.getString("ANDPayment"))
        setMessages(context, remoteConfig.getString("Marketing_ANDMessages"))
        setHomeDefaultEvent(context, remoteConfig.getString("Marketing_ANDHomeDefaultEvent"))
        setConfig(context, remoteConfig.getString("ANDConfig"))
        setStaticUlr(context, remoteConfig.getString("androidStaticUrl"))
        setSearch(context, remoteConfig.getString("ANDSearch"))
        setReward(context, remoteConfig.getString("Marketing_ANDRewardSticker"))
        setAppResearch(context, remoteConfig.getString("androidAppResearch"))
        setPaymentCardEvent(context, remoteConfig.getString("Marketing_ANDPaymentCardEvent"))

        val versionPair = getVersionNsetMessages(context, remoteConfig.getString("ANDVersion"))
        listener.onComplete(versionPair?.first, versionPair?.second)
    }

    private fun updateSplash(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            val splashDelegate = SplashDelegate(it)
            val currentUpdateTime = DailyRemoteConfigPreference.getInstance(context).remoteConfigIntroImageVersion

            splashDelegate.updateTime?.let {
                if (DailyTextUtils.isTextEmpty(currentUpdateTime)) {
                    if (Constants.DAILY_INTRO_CURRENT_VERSION < it) {
                        SplashImageDownloadAsyncTask(context).execute(splashDelegate.getUrl(context), it)
                    }
                } else {
                    if (Constants.DAILY_INTRO_CURRENT_VERSION < currentUpdateTime && currentUpdateTime < it) {
                        SplashImageDownloadAsyncTask(context).execute(splashDelegate.getUrl(context), it)
                    }
                }
            }
        }
    }

    internal fun setPayment(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val paymentDelegate = PaymentDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStaySimpleCardPaymentEnabled = paymentDelegate.stayEasyCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayCardPaymentEnabled = paymentDelegate.stayCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayPhonePaymentEnabled = paymentDelegate.stayPhone
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayVirtualPaymentEnabled = paymentDelegate.stayVirtual

                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayOutboundSimpleCardPaymentEnabled = paymentDelegate.stayOutboundEasyCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayOutboundCardPaymentEnabled = paymentDelegate.stayOutboundCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigStayOutboundPhonePaymentEnabled = paymentDelegate.stayOutboundPhone

                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigGourmetSimpleCardPaymentEnabled = paymentDelegate.gourmetEasyCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigGourmetCardPaymentEnabled = paymentDelegate.gourmetCard
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigGourmetPhonePaymentEnabled = paymentDelegate.gourmetPhone
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigGourmetVirtualPaymentEnabled = paymentDelegate.gourmetVirtual
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setMessages(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val messageDelegate = MessagesDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).remoteConfigTextLoginText01 = messageDelegate.loginText01
                DailyRemoteConfigPreference.getInstance(context).remoteConfigTextSignUpText01 = messageDelegate.signupText01
                DailyRemoteConfigPreference.getInstance(context).remoteConfigTextSignUpText02 = messageDelegate.signupText02
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigHomeMessageAreaLoginEnabled = messageDelegate.homeMessageAreaLoginEnabled
                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigHomeMessageAreaLogoutEnabled = messageDelegate.homeMessageAreaLogoutEnabled
                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeMessageAreaLogoutTitle = messageDelegate.homeMessageAreaLogoutTitle
                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeMessageAreaLogoutCallToAction = messageDelegate.homeMessageAreaLogoutCallToAction
                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeCategoryEnabled = messageDelegate.homeCategoryAreaEnabled

            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setHomeDefaultEvent(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val homeDefaultEvent = HomeDefaultEventDelegate(it)
                val clientHomeEventCurrentUpdateTime = DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeEventCurrentVersion

                homeDefaultEvent.updateTime?.let {
                    if (clientHomeEventCurrentUpdateTime == null || it > clientHomeEventCurrentUpdateTime) {
                        ImageDownloadAsyncTask(context, it, ImageDownloadAsyncTask.OnCompletedListener { result, updateTime ->
                            if (result) {
                                val file = File(context.cacheDir, Util.makeImageFileName(clientHomeEventCurrentUpdateTime))

                                if (file.exists() && !file.delete()) {
                                    file.deleteOnExit()
                                }

                                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeEventCurrentVersion = updateTime
                                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeEventIndex = homeDefaultEvent.index
                                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeEventTitle = homeDefaultEvent.title
                                DailyRemoteConfigPreference.getInstance(context).remoteConfigHomeEventUrl = homeDefaultEvent.eventUrl
                            }
                        }).execute(homeDefaultEvent.getImageUrl(context))
                    }
                }
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setConfig(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val configDelegate = ConfigDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).isRemoteConfigBoutiqueBMEnabled = configDelegate.boutiqueBusinessModelEnabled

                DailyRemoteConfigPreference.getInstance(context).remoteConfigOperationLunchTime = "${configDelegate.operationLunchStartTime},${configDelegate.operationLunchEndTime}"

                DailyRemoteConfigPreference.getInstance(context).isKeyRemoteConfigStayDetailTrueReviewProductVisible = configDelegate.stayDetailTrueReviewProductNameVisible
                DailyRemoteConfigPreference.getInstance(context).isKeyRemoteConfigStayOutboundDetailTrueReviewProductVisible = configDelegate.stayOutboundDetailTrueReviewProductNameVisible
                DailyRemoteConfigPreference.getInstance(context).isKeyRemoteConfigGourmetDetailTrueReviewProductVisible = configDelegate.gourmetDetailTrueReviewProductNameVisible
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setStaticUlr(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val staticUrl = StaticUrlDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlPrivacy = staticUrl.privacy
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlTerms = staticUrl.terms
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlAbout = staticUrl.about
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlLocation = staticUrl.location
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlChildProtect = staticUrl.childProtect
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlBonus = staticUrl.bonus
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlCoupon = staticUrl.coupon
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlProdCouponNote = staticUrl.prodCouponNote
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlDevCouponNote = staticUrl.devCouponNote
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlFaq = staticUrl.faq
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlLicense = staticUrl.license
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlReview = staticUrl.review
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlLifeStyleProject = staticUrl.lifeStyleProject
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlCollectPersonalInformation = staticUrl.collectPersonalInformation
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlDailyReward = staticUrl.dailyReward
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlDailyRewardTerms = staticUrl.dailyRewardTerms
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlDailyRewardCouponTerms = staticUrl.dailyRewardCouponTerms
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigStaticUrlDailyTrueAwards = staticUrl.dailyTrueAwards
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setSearch(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val searchDelegate = SearchDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigSearchStaySuggestHint = searchDelegate.suggestHintStay
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigSearchStayOutboundSuggestHint = searchDelegate.suggestHintStayOutbound
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigSearchGourmetSuggestHint = searchDelegate.suggestHintGourmet

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigObSearchKeyword = searchDelegate.stayOutboundRelatedKeywords
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigGourmetSearchKeyword = searchDelegate.gourmetRelatedKeywords
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setReward(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val rewardDelegate = RewardDelegate(it)

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerCardTitleMessage = rewardDelegate.cardTitleMessage
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerRewardTitleMessage = rewardDelegate.rewardTitleMessage
                DailyRemoteConfigPreference.getInstance(context).isKeyRemoteConfigRewardStickerCampaignEnabled = rewardDelegate.campaignEnabled
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerGuides = rewardDelegate.guides
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerNonMemberDefaultMessage = rewardDelegate.nonMemberMessageDefault
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerNonMemberCampaignMessage = rewardDelegate.nonMemberMessageCampaign
                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigRewardStickerNonMemberCampaignFreeNights = rewardDelegate.nonMemberCampaignFreeNights

                rewardDelegate.memberMessagesNights.forEachIndexed { index, message -> DailyRemoteConfigPreference.getInstance(context).setKeyRemoteConfigRewardStickerMemberMessage(index, message) }
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }
    }

    internal fun setAppResearch(context: Context, jsonString: String) {
        jsonString.takeNotEmpty { DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigAppResearch = it }
    }

    internal fun setPaymentCardEvent(context: Context, jsonString: String) {
        jsonString.takeNotEmpty {
            try {
                val jsonObject = JSONObject(it)

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigPaymentCardEvent =
                        if (jsonObject.getBoolean("enabled")) jsonObject.getJSONArray("events")?.toString() else null
            } catch (e: Exception) {
                ExLog.e(e.toString())

                DailyRemoteConfigPreference.getInstance(context).keyRemoteConfigPaymentCardEvent = null
            }
        }
    }

    internal fun getVersionNsetMessages(context: Context, jsonString: String): Pair<String?, String?>? {
        jsonString.takeNotEmpty {
            try {
                val versionDelegate = VersionDelegate(it)

                val optionalJSONObject = JSONObject()
                val optionalMessage = versionDelegate.optionalMessage
                optionalJSONObject.put("title", optionalMessage.first)
                optionalJSONObject.put("message", optionalMessage.second)

                val forceJSONObject = JSONObject()
                val forceMessage = versionDelegate.forceMessage
                forceJSONObject.put("title", forceMessage.first)
                forceJSONObject.put("message", forceMessage.second)

                DailyRemoteConfigPreference.getInstance(context).remoteConfigUpdateOptional = optionalJSONObject.toString()
                DailyRemoteConfigPreference.getInstance(context).remoteConfigUpdateForce = forceJSONObject.toString()

                return Pair(versionDelegate.optional, versionDelegate.force)
            } catch (e: Exception) {
                ExLog.e(e.toString())
            }
        }

        return null
    }


}