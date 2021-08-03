package com.batteria.gldroid.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import com.batteria.gldroid.utils.BufferUtil
import com.batteria.gldroid.utils.ContextUtils
import com.batteria.gldroid.utils.GLUtils
import com.batteria.gldroid.utils.Logger
import com.batteria.gldroid.utils.logInfo
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: yaobeihaoyu
 * @version: 1.0
 * @since: 2021/7/30
 * @description:
 *
 * 顶点数组对象：Vertex Array Object，VAO
 * 顶点缓冲对象：Vertex Buffer Object，VBO
 * 索引缓冲对象：Element Buffer Object，EBO或Index Buffer Object，IBO
 */
class TriangleVAORender : GLSurfaceView.Renderer, Logger {
    private val vertices = floatArrayOf(
        0f, 0.5f, 0f,
        -0.5f, -0.5f, 0f,
        0.5f, -0.5f, 0f
    )

    private var vertexBuffer: FloatBuffer? = null

    private var shaderProgram = 0

    private var vao = 0

    private var vbo = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        logInfo("onSurfaceCreated")
        vertexBuffer = BufferUtil.floatToBuffer(vertices)

        val resources = ContextUtils.application?.resources ?: throw NullPointerException()
        shaderProgram = GLUtils.loadProgramFromAssets("triangle_vs.glsl", "triangle_fs.glsl", resources).program
        glUseProgram(shaderProgram)

        // 0. 创建VAO和VBO
        val vaoBuffer: IntBuffer = IntBuffer.allocate(1)
        glGenVertexArrays(1, vaoBuffer)
        vao = vaoBuffer[0]
        val vboBuffer: IntBuffer = IntBuffer.allocate(1)
        glGenBuffers(1, vboBuffer)
        vbo = vboBuffer[0]

        // 1. 绑定VAO
        glBindVertexArray(vao)

        // 2. 把顶点数组复制到缓冲中供OpenGL使用
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GL_STATIC_DRAW)

        // 3. 设置顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0)
        glEnableVertexAttribArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        logInfo("onSurfaceChanged")
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        logInfo("onDrawFrame")
        glClear(GL_COLOR_BUFFER_BIT)

        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glBindVertexArray(0)
    }

    override val logTag: String
        get() = TAG

    companion object {
        const val TAG = "Triangle_VAO"
    }
}