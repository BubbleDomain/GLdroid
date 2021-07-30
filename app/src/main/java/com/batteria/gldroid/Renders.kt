package com.batteria.gldroid

import android.opengl.GLSurfaceView
import androidx.annotation.DrawableRes
import java.lang.Exception

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description:
 */
object Renders {
    val map = mutableMapOf<String, RenderData<out GLSurfaceView.Renderer>>()

    fun init(data: Map<String, RenderData<out GLSurfaceView.Renderer>>) {
        map.putAll(data)
    }

    fun createRender(clazz: Class<out GLSurfaceView.Renderer>): GLSurfaceView.Renderer? {
        return try {
            clazz.constructors[0].newInstance() as? GLSurfaceView.Renderer
        } catch (e: Exception) {
            null
        }
    }
}

class RenderData<T>(@DrawableRes val resId: Int, val clazz: Class<T>)