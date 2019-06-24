package com.kmadsen.compass.native

import com.kylemadsen.core.logger.L

object NativeLib {
    init {
        L.i("load library")
        System.loadLibrary("native-lib")
        init()
        L.i("load library done")
    }

    private external fun init()

    external fun multiply(num1: Int, num2: Int): Int
}
