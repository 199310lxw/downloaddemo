package com.xwl.downloaddemo

import android.app.Application
import com.orhanobut.logger.Logger

/**
 * @author  lxw
 * @date 2024/6/15
 * descripe
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.init("TAG")
    }
}