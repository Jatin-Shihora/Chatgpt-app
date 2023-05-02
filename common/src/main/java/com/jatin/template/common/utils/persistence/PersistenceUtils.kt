package com.jatin.template.common.utils.persistence

import android.content.Context
import com.tencent.mmkv.MMKV

/**
 * @author Jatin
 * @time 10/11/2022
 */
object PersistenceUtils {

    fun initialize(context: Context): BasePersistence {
        //Use mmkv first, use sp if it fails
        return try {
            MMKV.initialize(context, context.applicationInfo.dataDir + "/mmkv/")
            MMKVPersistence()
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
            SpPersistence()
        } catch (e: Exception) {
            e.printStackTrace()
            SpPersistence()
        }
    }


}