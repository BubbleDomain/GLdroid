package com.batteria.gldroid.render

import android.opengl.GLES20
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
class RectangleRender : GLSurfaceView.Renderer, Logger {
    private val vertices = floatArrayOf(
        0.5f, 0.5f, 0.0f,   // 右上角
        0.5f, -0.5f, 0.0f,  // 右下角
        -0.5f, -0.5f, 0.0f, // 左下角
        -0.5f, 0.5f, 0.0f   // 左上角
    )

    private val indices = intArrayOf(
        0, 1, 3, // 第一个三角形
        1, 2, 3  // 第二个三角形
    )

    private var vertexBuffer: FloatBuffer? = null

    private var indexBuffer: IntBuffer? = null

    private var shaderProgram = 0

    private var ibo = 0
    private var vao = 0
    private var vbo = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        logInfo("onSurfaceCreated")

        vertexBuffer = BufferUtil.floatToBuffer(vertices)
        indexBuffer = BufferUtil.intToBuffer(indices)

        val resources = ContextUtils.application?.resources ?: throw NullPointerException()
        shaderProgram = GLUtils.loadProgramFromAssets("triangle_vs.glsl", "triangle_fs.glsl", resources)
        glUseProgram(shaderProgram)

        // 0. 创建VAO和VBO
        val vaoBuffer: IntBuffer = IntBuffer.allocate(1)
        glGenVertexArrays(1, vaoBuffer)
        vao = vaoBuffer[0]
        val vboBuffer: IntBuffer = IntBuffer.allocate(1)
        glGenBuffers(1, vboBuffer)
        vbo = vboBuffer[0]

        val iboBuffer: IntBuffer = IntBuffer.allocate(1)
        glGenBuffers(1, iboBuffer)
        ibo = iboBuffer[0]

        // 1. 绑定VAO
        glBindVertexArray(vao)
        // 2. 把我们的顶点数组复制到一个顶点缓冲中，供OpenGL使用
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GL_STATIC_DRAW)
        // 3. 复制我们的索引数组到一个索引缓冲中，供OpenGL使用
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, indexBuffer, GL_STATIC_DRAW)

        // 4. 设定顶点属性指针
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
        glClearColor(1f,0f,0f,1f)

        glBindVertexArray(vao)
        glDrawElements(GLES20.GL_LINE_LOOP, 6, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    override val logTag: String
        get() = TAG

    companion object {
        const val TAG = "Rectangle"
    }
}