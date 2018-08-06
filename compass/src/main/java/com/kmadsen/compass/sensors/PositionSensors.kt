package com.kmadsen.compass.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.kylemadsen.core.logger.L

class PositionSensors constructor(val context: Context) {

    private val sensorListener: RotationVectorSensorListener = RotationVectorSensorListener()
    private var sensorManager: SensorManager? = null

    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorListener.start(sensorManager!!)
    }

    fun setRotation(matrix: FloatArray): FloatArray {
        for (i in 0..15) {
            matrix[i] = sensorListener.rotationMatrix[i]
        }
        return matrix
    }

    fun stop() {
        sensorListener.stop(sensorManager!!)
    }

    class RotationVectorSensorListener : SensorEventListener {
        internal val rotationMatrix = FloatArray(16)

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            }
        }

        fun start(sensorManager: SensorManager) {
            L.i("subscribe")
            val rotationVectorSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            sensorManager.registerListener(this, rotationVectorSensor, 0)
        }

        fun stop(sensorManager: SensorManager) {
            L.i("unsubscribe")
            sensorManager.unregisterListener(this)
        }
    }
}
