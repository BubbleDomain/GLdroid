package com.batteria.gldroid.utils

import android.opengl.GLES30.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/8/2
 * @description:
 */
object GLUtils : Logger {
    fun loadProgramFromAssets(
        vsName: String?,
        fsName: String?,
        resources: Resources
    ): Int {
        val vertexSource = loadFromAssetsFile(vsName, resources)
        val fragmentSource = loadFromAssetsFile(fsName, resources)
        return createProgram(vertexSource, fragmentSource)
    }

    fun getImageBuffer(fileName: String, resources: Resources): BitmapBufferData {
        // 加载纹理
        val bitmapSource = BufferedInputStream(resources.assets.open(fileName)).readBytes()
        val bitmapData = BitmapFactory.decodeByteArray(bitmapSource, 0, bitmapSource.size)
        val bitmapBuffer = ByteBuffer.allocateDirect(bitmapData.width * bitmapData.height * 4)
            .order(ByteOrder.nativeOrder())
        bitmapData.copyPixelsToBuffer(bitmapBuffer)

        return BitmapBufferData(bitmapData, bitmapBuffer, bitmapData.width, bitmapData.height)
    }

    fun checkGLError(op: String) {
        var error: Int
        while (glGetError().also { error = it } != GL_NO_ERROR) {
            logError("$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }

    private fun createProgram(
        vertexSource: String?,
        fragmentSource: String?
    ): Int {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val fragShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource)
        if (fragShader == 0) {
            return 0
        }
        var program = glCreateProgram()
        if (program != 0) {
            glAttachShader(program, vertexShader)
            glAttachShader(program, fragShader)
            glLinkProgram(program)
            val linkStatus = IntArray(1)
            glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GL_TRUE) {
                logError("program link failed\nERROR: " + glGetProgramInfoLog(program))
                glDeleteProgram(program)
                program = 0
            }
            /**
             * 链接成功之后就可以删除之前的Shader了
             */
            glDeleteShader(vertexShader)
            glDeleteShader(fragShader)
        } else {
            logError("glCreateProgram Failed: $program")
        }
        return program
    }

    private fun loadShader(
        shaderType: Int,
        source: String?
    ): Int {
        var shader = glCreateShader(shaderType)
        if (shader != 0) {
            glShaderSource(shader, source)
            glCompileShader(shader)
            val compiled = IntArray(1)
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                logError("Could not compile shader $shaderType:")
                logError("ERROR: " + glGetShaderInfoLog(shader))
                glDeleteShader(shader)
                shader = 0
            }
        } else {
            logError("Could not Create shader $shaderType:Error:$shader")
        }
        return shader
    }

    private fun loadFromAssetsFile(
        fileName: String?,
        resources: Resources
    ): String? {
        var result: String? = null
        try {
            val inputStream = resources.assets.open(fileName!!)
            var t: Int
            val outputStream = ByteArrayOutputStream()
            while (inputStream.read().also { t = it } != -1) {
                outputStream.write(t)
            }
            val buffer = outputStream.toByteArray()
            outputStream.close()
            inputStream.close()
            result = String(buffer)
            result = result.replace("\\r\\n".toRegex(), "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    override val logTag: String
        get() = "GLUtils"
}

data class BitmapBufferData(
    val bitmapData: Bitmap,
    val buffer: Buffer,
    val width: Int,
    val height: Int
) {
    fun recycle() {
        bitmapData.recycle()
    }
}