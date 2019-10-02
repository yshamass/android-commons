package com.safeboda.commons.analytics.provider.google

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.safeboda.commons.analytics.entity.AnalyticsEvent
import com.safeboda.commons.analytics.entity.AnalyticsUser
import com.safeboda.commons.analytics.entity.IS_USER_LOGGED_IN
import com.safeboda.commons.analytics.entity.USER_IDENTIFIER
import com.safeboda.commons.analytics.provider.AnalyticsProvider

class GoogleAnalyticsProvider(
    context: Context
) : AnalyticsProvider {

    private var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun setUser(user: AnalyticsUser) {
        firebaseAnalytics.setUserId(user.id.toString())
        firebaseAnalytics.setUserProperty(USER_IDENTIFIER, user.userIdentifier)
    }

    override fun clearUser() {
        firebaseAnalytics.setUserId(null)
        firebaseAnalytics.setUserProperty(USER_IDENTIFIER, null)
    }

    override fun setUserLogged() {
        setLoginEvent(true)
    }

    override fun setUserNotLogged() {
        setLoginEvent(false)
    }

    private fun setLoginEvent(isLogged: Boolean) {
        val bundle = Bundle().apply {
            putBoolean(IS_USER_LOGGED_IN, isLogged)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun track(event: AnalyticsEvent) {
        val bundle = Bundle()

        for ((propertyName, value) in event.getProperties()) {
            when (value.getSafeValue()) {
                is List<*> -> (value.getSafeValue() as List<*>).forEach { propertyValue ->
                    bundle.putString(propertyName, propertyValue.toString())
                }
                is Boolean -> bundle.putBoolean(propertyName, value.getSafeValue() as Boolean)
                is Long -> bundle.putLong(propertyName, value.getSafeValue() as Long)
                is Int -> bundle.putInt(propertyName, value.getSafeValue() as Int)
                is Float -> bundle.putFloat(propertyName, value.getSafeValue() as Float)
                is Double -> bundle.putDouble(propertyName, value.getSafeValue() as Double)
                else -> bundle.putString(propertyName, value.getSafeValue().toString())
            }
        }

        firebaseAnalytics.logEvent(event.name, bundle)
    }

}