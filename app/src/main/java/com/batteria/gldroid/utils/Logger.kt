package com.batteria.gldroid.utils

import android.util.Log

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description: [Logger] 辅助类，省去打日志时的tag参数
*/

interface Logger {
    val logTag: String
}

inline fun Logger.logInfo(message: String, throwable: Throwable? = null) {
    Log.i(logTag, message, throwable)
}

inline fun Logger.logDebug(message: String, throwable: Throwable? = null) {
    Log.d(logTag, message, throwable)
}

inline fun Logger.logWarn(message: String, throwable: Throwable? = null) {
    Log.w(logTag, message, throwable)
}

inline fun Logger.logError(message: String, throwable: Throwable? = null) {
    Log.e(logTag, message, throwable)
}
