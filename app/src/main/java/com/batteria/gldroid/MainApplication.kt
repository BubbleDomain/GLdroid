package com.batteria.gldroid

import android.app.Application
import com.batteria.gldroid.utils.ContextUtils

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2020/10/16 5:35 PM
 * @description:
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextUtils.application = this
    }
}