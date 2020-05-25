package com.eye.cool.adapter.support

import android.graphics.drawable.Drawable

/**
 * Created by ycb on 2019/12/19 0019
 */
data class Empty(
    var drawable: Drawable? = null,
    var text: CharSequence? = null,
    var isClickAble: Boolean = false
)