package com.kmadsen.compass.sensors

import android.graphics.PointF
import android.hardware.SensorManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.kmadsen.compass.Cube
import com.kylemadsen.core.logger.L
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SensorGLRenderer: GLSurfaceView.Renderer {

    private val mvpMatrix: FloatArray = FloatArray(16)
    private val orthoMatrix: FloatArray = FloatArray(16)
    private val viewMatrix: FloatArray = FloatArray(16)
    private val rotationMatrix: FloatArray = FloatArray(16)
    private val finalMVPMatrix: FloatArray = FloatArray(16)

    private val modelMatrix: FloatArray = FloatArray(16)

    private var cube: Cube? = null
    private var position = PointF()

    private var width: Int = 1
    private var height: Int = 1

    init {
        // Set the fixed camera position (View matrix).
        Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, -4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
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
        this.width = width
        this.height = height

        val ratio = width.toFloat() / height

        GLES20.glViewport(0, 0, width, height)
        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
        Matrix.orthoM(orthoMatrix, 0, -10f * ratio, 10f * ratio, -10.0f, 10.0f, 3.0f, 7.0f)
        // modelView = projection x view
        Matrix.multiplyMM(mvpMatrix, 0, orthoMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

//        //translate the Matrix
//        Matrix.translateM(finalMVPMatrix, 0,  721f, 1212f, 0f)
        Matrix.setIdentityM(modelMatrix, 0) // initialize to identity matrix

        val translateX = -(position.x - width / 2f) * 20 / height - 1
        val translateY = -(position.y - height / 2f) * 20 / height
        Matrix.translateM(modelMatrix, 0, translateX, translateY, 0f) // translation to the left

        // TODO The rotation matrix flips everything for some reason
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotationMatrix, 0)

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(finalMVPMatrix, 0, mvpMatrix, 0, modelMatrix, 0)


        // Draw cube.
        cube!!.draw(finalMVPMatrix)
    }

//    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
//        val ratio = width.toFloat() / height
//
//        L.i("onSurfaceChanged width $width height $height ratio $ratio")
//
//
//        GLES20.glViewport(0, 0, width, height)
//        // This projection matrix is applied to object coordinates in the onDrawFrame() method.
//        Matrix.orthoM(orthoMatrix, 0,
//            0.0f, width.toFloat(),
//            0.0f, height.toFloat(),
//            3.0f, 7.0f)
//
//        // modelView = projection x view
//        Matrix.multiplyMM(mvpMatrix, 0, orthoMatrix, 0, viewMatrix, 0)
//    }
//
//    override fun onDrawFrame(unused: GL10) {
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//
//        // Combine the rotation matrix with the projection and camera view
//        //translate the Matrix
//        Matrix.translateM(finalMVPMatrix, 0,  721f, 1212f, 0f)
//        // Calculate the projection and view transformation
//        Matrix.multiplyMM(finalMVPMatrix, 0, finalMVPMatrix, 0, finalMVPMatrix, 0);
//
////        Matrix.multiplyMM(finalMVPMatrix, 0, mvpMatrix, 0, rotationMatrix, 0)
//
//        // Draw cube.
//        cube!!.draw(finalMVPMatrix)
//    }


    fun update(rotation: FloatArray) {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotation)
    }

    fun updateLocationPosition(position: PointF) {
        this.position = position
    }
}
