package com.jatin.template.common.utils

import android.R
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

object StatusBarUtil {
    const val TYPE_MIUI = 0
    const val TYPE_FLYME = 1
    const val TYPE_M = 3 //6.0

    /**
     * Modify the status bar color, support version 4.4 and above
     *
     * @param colorId color
     */
    fun setStatusBarColor(activity: Activity, colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.statusBarColor = colorId
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //To use SystemBarTintManager, you need to set the status bar to transparent first
            setTranslucentStatus(activity)
            val systemBarTintManager = SystemBarTintManager(activity)
            systemBarTintManager.isStatusBarTintEnabled = true // show the status bar
            systemBarTintManager.setStatusBarTintColor(colorId) //Set status bar color
        }
    }

    fun goneStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            WindowInsetsController ic = activity.getWindow().getDecorView().getWindowInsetsController();
//            if (ic != null) {
//                ic.hide(WindowInsets.Type.statusBars());
//                ic.hide(WindowInsets.Type.navigationBars());
//            }
//            View decorView = activity.getWindow().getDecorView();
//            //Set the application theme content to occupy the status bar and navigation bar
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            //Set the status bar and navigation bar color to transparent
//            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            val lp = activity.window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            activity.window.attributes = lp
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val uiFlags = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.INVISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            activity.window.decorView.systemUiVisibility = uiFlags
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * Set the status bar to be transparent
     */
    @TargetApi(19)
    fun setTranslucentStatus(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //From 5.x, you need to set the color to transparent, otherwise the navigation bar will show the system default light gray
            val window = activity.window
            val decorView = window.decorView
            //
            //The two flags should be used in combination to indicate that the main content of the application occupies the space of the system status bar
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            //The color of the navigation bar can also be set normally
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = activity.window
            val attributes = window.attributes
            val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            attributes.flags = attributes.flags or flagTranslucentStatus
            window.attributes = attributes
        }
    }

    /**
     * Code implementation android:fitsSystemWindows
     *
     * @param activity
     */
    fun setRootViewFitsSystemWindows(activity: Activity, fitSystemWindows: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val winContent = activity.findViewById<View>(R.id.content) as ViewGroup
            if (winContent.childCount > 0) {
                val rootView = winContent.getChildAt(0) as ViewGroup
                if (rootView != null) {
                    rootView.fitsSystemWindows = fitSystemWindows
                }
            }
        }
    }

    /**
     * Set the status bar to switch between dark and light
     */
    fun setStatusBarDarkTheme(activity: Activity, dark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarFontIconDark(activity, TYPE_M, dark)
            } else { //Other cases
                return false
            }
            return true
        }
        return false
    }

    /**
     * Set the status bar to switch between dark and light
     */
    fun setStatusBarFontIconDark(activity: Activity, @ViewType type: Int, dark: Boolean): Boolean {
        return when (type) {
            TYPE_MIUI -> setMiuiUI(activity, dark)
            TYPE_FLYME -> setFlymeUI(activity, dark)
            TYPE_M -> setCommonUI(activity, dark)
            else -> setCommonUI(activity, dark)
        }
    }

    //Setting 6.0 status bar dark color light switch
    fun setCommonUI(activity: Activity, dark: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                vis = if (dark) {
                    vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                if (decorView.systemUiVisibility != vis) {
                    decorView.systemUiVisibility = vis
                }
                return true
            }
        }
        return false
    }

    //Set the Flyme status bar to switch between dark and light
    fun setFlymeUI(activity: Activity, dark: Boolean): Boolean {
        return try {
            val window = activity.window
            val lp = window.attributes
            val darkFlag =
                WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //Set the status bar to switch between dark and light
    fun setMiuiUI(activity: Activity, dark: Boolean): Boolean {
        return try {
            val window = activity.window
            val clazz: Class<*> = activity.window.javaClass
            @SuppressLint("PrivateApi") val layoutParams =
                Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getDeclaredMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            extraFlagField.isAccessible = true
            if (dark) {    //Bright color and black font in the status bar
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //Get the height of the status bar
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    @IntDef(TYPE_MIUI, TYPE_FLYME, TYPE_M)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class ViewType
}