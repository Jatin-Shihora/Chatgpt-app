package com.jatin.template.common

import android.annotation.SuppressLint
import com.jatin.template.common.utils.persistence.Preference

/**
 * global configuration
 *
 * @author Jatin
 * @time 18/02/2023
 */
@SuppressLint("StaticFieldLeak")
object GlobalConfig {
    var temp: String by Preference(Constants.Config.TEMP, "")
    var apiKey: String by Preference(Constants.Config.API_KEY, "")

}