package com.geekstudio.sentrytest

import android.app.Application
import android.util.Log
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid

class AppApplication : Application() {
    private val TAG = javaClass.simpleName

    override fun onCreate() {
        super.onCreate()

        SentryAndroid.init(this) { options ->
            options.setBeforeViewHierarchyCaptureCallback { event, hint, debounce ->
                Log.d(TAG, "options > BeforeViewHierarchyCaptureCallback event = $event")
                Log.d(TAG, "options > BeforeViewHierarchyCaptureCallback hint = $hint")
                Log.d(TAG, "options > BeforeViewHierarchyCaptureCallback debounce = $debounce")
                // always capture crashed events
                if (event.isCrashed) {
                    return@setBeforeViewHierarchyCaptureCallback true
                }

                // if debounce is active, skip capturing
                if (debounce) {
                    return@setBeforeViewHierarchyCaptureCallback false
                } else {
                    // also capture fatal events
                    return@setBeforeViewHierarchyCaptureCallback event.level == SentryLevel.FATAL
                }
            }

            options.isEnableUserInteractionTracing = true
            options.isEnableUserInteractionBreadcrumbs = true
        }
    }
}