package com.kmadsen.compass.sensors

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.kmadsen.compass.Cube

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SensorGLRenderer constructor(context: Context) : GLSurfaceView.Renderer {

    private val positionSensors: PositionSensors = PositionSensors(context)

    private val mMVPMatrix: FloatArray = FloatArray(16)
    private val mProjectionMatrix: FloatArray = FloatArray(16)
    private val mViewMatrix: FloatArray = FloatArray(16)
    private val mRotationMatrix: FloatArray = FloatArray(16)
    private val mFinalMVPMatrix: FloatArray = FloatArray(16)

    private var mCube: Cube? = null

    init {
        // Set the fixed camera position (View matrix).
        Matrix.setLookAtM(mViewMatrix, 0, 0.0f, 0.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    }

    fun start() {
        positionSensors.start()
    }

    fun stop() {
        positionSensors.stop()
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glClearDepthf(1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        mCube = Cube()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        val ratio = width.toFloat() / height

        GLES20.glViewport(0, 0, width, height)
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f)
        // modelView = projection x view
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        positionSensors.setRotation(mRotationMatrix)

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mFinalMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // Draw cube.
        mCube!!.draw(mFinalMVPMatrix)
    }
}
