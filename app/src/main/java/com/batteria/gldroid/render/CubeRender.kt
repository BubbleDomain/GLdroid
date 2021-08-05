package com.batteria.gldroid.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
 */
class CubeRender : GLSurfaceView.Renderer, Logger {
    private val vertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    )

    private var vertexBuffer: FloatBuffer? = null

    private var shaderProgram = 0
    private var vertexShader = 0
    private var fragmentShader = 0

    private var ibo = 0
    private var vao = 0
    private var vbo = 0

    private var texture1 = 0
    private var texture2 = 0

    private var sightWidth = 0
    private var sightHeight = 0

    private val transformMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        logInfo("onSurfaceCreated")

        glEnable(GL_DEPTH_TEST)

        vertexBuffer = BufferUtil.floatToBuffer(vertices)

        val resources = ContextUtils.application?.resources ?: throw NullPointerException()
        val data =
            GLUtils.loadProgramFromAssets("cube_vs.glsl", "cube_fs.glsl", resources)
        shaderProgram = data.program
        vertexShader = data.vertexShader
        fragmentShader = data.fragmentShader
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

        // 4. 设定顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12)
        glEnableVertexAttribArray(1)

        initTexture()
    }

    private fun initTexture() {
        val resources = ContextUtils.application?.resources ?: throw NullPointerException()
        val bufferData1 = GLUtils.getImageBuffer("rock.png", resources)
        val bufferData2 = GLUtils.getImageBuffer("happy.png", resources)

        // 1
        val textureBuffer1 = IntBuffer.allocate(1)
        glGenTextures(1, textureBuffer1)
        texture1 = textureBuffer1[0]

        glBindTexture(GL_TEXTURE_2D, texture1)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        android.opengl.GLUtils.texImage2D(GL_TEXTURE_2D, 0, bufferData1.bitmapData, 0)
        bufferData1.recycle()
        glGenerateMipmap(GL_TEXTURE_2D)

        // 2
        val textureBuffer2 = IntBuffer.allocate(1)
        glGenTextures(1, textureBuffer2)
        texture2 = textureBuffer2[0]

        glBindTexture(GL_TEXTURE_2D, texture2)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        android.opengl.GLUtils.texImage2D(GL_TEXTURE_2D, 0, bufferData2.bitmapData, 0)
        bufferData2.recycle()
        glGenerateMipmap(GL_TEXTURE_2D)

//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, bufferData.width, bufferData.height, 0, GL_RGB, GL_UNSIGNED_BYTE, bufferData.buffer)

        glUseProgram(shaderProgram)
        glUniform1i(glGetUniformLocation(shaderProgram, "texture1"), 0)
        glUniform1i(glGetUniformLocation(shaderProgram, "texture2"), 1)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        sightWidth = width
        sightHeight = height
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT.or(GL_DEPTH_BUFFER_BIT))

        updateMatrix()

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture2)

        glUseProgram(shaderProgram)
        glBindVertexArray(vao)
        glDrawArrays(GL_TRIANGLES, 0, 36)
        glBindVertexArray(0)
    }

    private var scale = 0.01f

    private fun updateMatrix() {
        val m = FloatArray(16)
        Matrix.setIdentityM(m, 0)
        Matrix.translateM(m, 0, 0f, 0f, -2f)

        val degree = (360f * scale) % 360
        scale += 0.01f

        // 旋转
        val m1 = getUnitMatrix()
        Matrix.rotateM(m1, 0, degree, 0.5f, 1f, 0f)

        // 移动
        val m2 = getUnitMatrix()
        Matrix.translateM(m2, 0, 0f, 0f, -5f)

        // 投影
        val m3 = getUnitMatrix()
        Matrix.perspectiveM(m3, 0, 45f, sightWidth.toFloat() / sightHeight, 0.1f, 100f)

        val m4 = FloatArray(16)
        Matrix.multiplyMM(m4, 0, m2, 0, m1, 0)

        Matrix.multiplyMM(transformMatrix, 0, m3, 0, m4, 0)

        glUniformMatrix4fv(
            glGetUniformLocation(shaderProgram, "transform"),
            1,
            false,
            BufferUtil.floatToBuffer(transformMatrix)
        )
    }

    private fun getUnitMatrix() = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    override val logTag: String
        get() = TAG

    companion object {
        const val TAG = "Cube"
    }
}