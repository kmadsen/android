package com.kylemadsen.testandroid.worldview

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.kmadsen.compass.Cube
import com.kylemadsen.core.logger.L
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WorldGLRenderer: GLSurfaceView.Renderer {

    private val mvpMatrix: FloatArray = FloatArray(16)
    private val orthoMatrix: FloatArray = FloatArray(16)
    private val viewMatrix: FloatArray = FloatArray(16)
    private val finalMVPMatrix: FloatArray = FloatArray(16)

    private val modelMatrix: FloatArray = FloatArray(16)

    private var cube: Cube? = null

    private var width: Int = 1
    private var height: Int = 1

    init {
        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClearDepthf(1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        cube = Cube()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        L.i("onSurfaceChanged $width $height")

        this.width = width
        this.height = height

        val ratio = width.toFloat() / height

        GLES20.glViewport(0, 0, width, height)
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
        Matrix.orthoM(orthoMatrix, 0, -10f * ratio, 10f * ratio, -10.0f, 10.0f, 1.0f, 70.0f)
        // modelView = projection x view
        Matrix.multiplyMM(mvpMatrix, 0, orthoMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.multiplyMM(finalMVPMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        cube!!.draw(finalMVPMatrix)
    }
}
