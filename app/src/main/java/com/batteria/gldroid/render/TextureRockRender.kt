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
 */
class TextureRockRender : GLSurfaceView.Renderer, Logger {
    private val vertices = floatArrayOf(
//     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
        0.8f,  0.4f, 0.0f,   1.0f, 0.0f, 0.0f,   7.0f, 7.0f,   // 右上
        0.8f, -0.4f, 0.0f,   0.0f, 1.0f, 0.0f,   7.0f, 0.0f,   // 右下
        -0.8f, -0.4f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f,   // 左下
        -0.8f,  0.4f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 7.0f    // 左上
    )

    private val indices = intArrayOf(
        0, 1, 3, // 第一个三角形
        1, 2, 3  // 第二个三角形
    )

    private var vertexBuffer: FloatBuffer? = null

    private var indexBuffer: IntBuffer? = null

    private var shaderProgram = 0
    private var vertexShader = 0
    private var fragmentShader = 0

    private var ibo = 0
    private var vao = 0
    private var vbo = 0

    private var texture1 = 0
    private var texture2 = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        logInfo("onSurfaceCreated")

        vertexBuffer = BufferUtil.floatToBuffer(vertices)
        indexBuffer = BufferUtil.intToBuffer(indices)

        val resources = ContextUtils.application?.resources ?: throw NullPointerException()
        val data = GLUtils.loadProgramFromAssets("texture_rock_vs.glsl", "texture_rock_fs.glsl", resources)
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
        // 3. 复制我们的索引数组到一个索引缓冲中，供OpenGL使用
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, indexBuffer, GL_STATIC_DRAW)

        // 4. 设定顶点属性指针
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12)
        glEnableVertexAttribArray(1)

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24)
        glEnableVertexAttribArray(2)

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

        // todo: 需要对图片资源进行解析区分
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, bufferData.width, bufferData.height, 0, GL_RGB, GL_UNSIGNED_BYTE, bufferData.buffer)

        glUseProgram(shaderProgram)
        glUniform1i(glGetUniformLocation(shaderProgram, "texture1"), 0)
        glUniform1i(glGetUniformLocation(shaderProgram, "texture2"), 1)
    }


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture1);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, texture2);

        glUseProgram(shaderProgram)
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    override val logTag: String
        get() = TAG

    companion object {
        const val TAG = "Texture_Rock"
    }
}