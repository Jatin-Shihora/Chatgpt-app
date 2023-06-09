package com.jatin.template.common.utils

import com.jatin.template.common.BuildConfig
import com.orhanobut.logger.Printer

/**
 * logger
 *
 * @author Jatin
 * @time 2022/11/23
 */
object L {


    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            com.orhanobut.logger.Logger.d(msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            com.orhanobut.logger.Logger.t(tag).d(msg)
        }
    }

    fun e(tagName: String, msg: String) {
        if (BuildConfig.DEBUG) {
            com.orhanobut.logger.Logger.t(tagName).e(msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            com.orhanobut.logger.Logger.e(msg)
        }
    }

    fun t(tagName: String): Printer? {
        return if (BuildConfig.DEBUG) {
            com.orhanobut.logger.Logger.t(tagName)
        } else {
            null
        }
    }
}