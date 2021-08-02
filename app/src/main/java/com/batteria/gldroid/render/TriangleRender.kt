package com.batteria.gldroid.render

import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import com.batteria.gldroid.utils.BufferUtil
import com.batteria.gldroid.utils.Logger
import com.batteria.gldroid.utils.logError
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
class TriangleRender : GLSurfaceView.Renderer, Logger {
    private val vertices = floatArrayOf(
        0f, 0.5f, 0f,
        -0.5f, -0.5f, 0f,
        0.5f, -0.5f, 0f
    )

    private var vertexBuffer: FloatBuffer? = null

    private val vertexShaderSource = """
        #version 300 es
        layout (location = 0) in vec3 av_Position;
        void main() {
           gl_Position = vec4(av_Position, 1.0);
        }""".trimIndent()

    private val fragmentShaderSource = """
        #version 300 es
        out vec4 FragColor;

        void main()
        {
            FragColor = vec4(0f, 1f, 0f, 1.0f);
        }""".trimIndent()

    private val buffer = IntBuffer.allocate(8)

    private var vertexShader = 0
    private var fragmentShader = 0
    private var shaderProgram = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        logInfo("onSurfaceCreated")

        vertexBuffer = BufferUtil.floatToBuffer(vertices)

        if (!initVertexShader()) return
        if (!initFragmentShader()) return
        if (!initProgram()) return

        glUseProgram(shaderProgram)

        val vbo: IntBuffer = IntBuffer.allocate(1)
        glGenBuffers(1, vbo)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        logInfo("onSurfaceChanged")
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        logInfo("onDrawFrame")
        glClear(GL_COLOR_BUFFER_BIT)

        glEnableVertexAttribArray(0)
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glDisableVertexAttribArray(0)
    }

    private fun initVertexShader(): Boolean {
        vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexShaderSource)
        glCompileShader(vertexShader)
        // 检查是否出错
        buffer.clear()
        glGetShaderiv(vertexShader, GL_COMPILE_STATUS, buffer)
        if (buffer[0] == GL_FALSE) {
            logError("vertex shader compile failed. caused by ${glGetShaderInfoLog(vertexShader)}")
            glDeleteShader(vertexShader)
            return false
        }
        return true
    }


    private fun initFragmentShader(): Boolean {
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentShaderSource)
        glCompileShader(fragmentShader)
        // 检查是否出错
        buffer.clear()
        glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, buffer)
        if (buffer[0] != GL_TRUE) {
            logError("fragment shader compile failed. caused by ${glGetShaderInfoLog(fragmentShader)}")
            glDeleteShader(fragmentShader)
            return false
        }
        return true
    }

    private fun initProgram(): Boolean {
        shaderProgram = glCreateProgram()
        glAttachShader(shaderProgram, vertexShader)
        glAttachShader(shaderProgram, fragmentShader)
        glLinkProgram(shaderProgram)
        // 检查是否出错
        buffer.clear()
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, buffer)
        if (buffer[0] != GL_TRUE) {
            logError("program link failed")
            glDeleteProgram(shaderProgram)
            return false
        }
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)

        return true
    }

    override val logTag: String
        get() = TAG

    companion object {
        const val TAG = "Triangle"
    }
}