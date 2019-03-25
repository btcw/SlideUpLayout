package top.iwill.mylib

import android.content.Context
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager

/**
 * @description:屏幕参数工具类
 * @author: btcw
 * @date: 2019/3/21
 */
fun Window.getDisplayMetrics(): DisplayMetrics {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm
}

fun Context.getDisplayMetrics(): DisplayMetrics {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)
    return dm
}