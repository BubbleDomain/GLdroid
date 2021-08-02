package com.batteria.gldroid.base

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batteria.gldroid.Renders
import java.lang.NullPointerException

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/29
 * @description:
 */
class OpenglActivity : AppCompatActivity() {
    var glSurfaceView: GLSurfaceView? = null
    var renderSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)

        glSurfaceView?.setEGLContextClientVersion(3)
        initRender()
    }

    private fun initRender() {
        val tag = intent.getStringExtra("TAG") ?: throw NullPointerException()
        val value = Renders.map[tag] ?: throw NoSuchFieldException()
        val render = Renders.createRender(value.clazz) ?: throw ClassNotFoundException()
        glSurfaceView?.setRenderer(render)
        renderSet = true
    }

    override fun onPause() {
        super.onPause()
        if (renderSet) {
            glSurfaceView?.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (renderSet) {
            glSurfaceView?.onResume()
        }
    }
}