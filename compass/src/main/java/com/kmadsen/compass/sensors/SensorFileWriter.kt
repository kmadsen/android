package com.kmadsen.compass.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import com.kylemadsen.core.logger.L
import com.kylemadsen.core.time.DeviceClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SensorFileWriter(
    private val fileDirectory: File,
    private val fileOutputStream: FileOutputStream
) {

    private val bufferedWriter: BufferedWriter = fileOutputStream.bufferedWriter()

    /**
     * Create a file with more details about each sensor. Note that this
     * is a single operation so you don't need to close() the file streams.
     */
    suspend fun write(sensorList: List<Sensor>) = withContext(Dispatchers.IO) {
        val file = File(fileDirectory, sensorFileName)
        L.i("$sensorFileName ${file.length()}")
        file.outputStream()
            .use { fos ->
                val bw = fos.bufferedWriter()
                sensorList.onEach {
                    bw.write("$it")
                    bw.newLine()
                }
                bw.flush()
            }
    }

    /**
     * Write each sensor event to an open file stream.
     * Stop writing events after {@link #close()} has been called
     */
    suspend fun write(sensorEvent: SensorEvent) = withContext(Dispatchers.IO) {
        sensorEvent.mapToEventRow()
            .let { eventRow ->
                bufferedWriter.write(eventRow)
                bufferedWriter.flush()
            }
    }

    /**
     * Closes the file stream. Stop calling write(sensorEvent)
     */
    fun close() {
        fileOutputStream.close()
    }

    private fun SensorEvent.mapToEventRow(): String {
        val valuesColumn = values.joinToString(",")
        val recordedTime = DeviceClock.elapsedNanos()
        return "$recordedTime ${sensor.name}  $timestamp ${DeviceClock.currentTimeDisplay()} $valuesColumn\n"
    }

    companion object {
        private const val directoryName = "compass"
        private const val dataFileNamePrefix = "sensor_data"
        private const val sensorFileName = "device_sensors"
        private val formatter = SimpleDateFormat("yyy_MM_dd_HH_mm_ss", Locale.ENGLISH)

        suspend fun open(context: Context): SensorFileWriter = withContext(Dispatchers.IO) {
            val directory = File(context.filesDir, directoryName)
            directory.mkdir()

            val now = Date()
            val fileName = String.format("${dataFileNamePrefix}_%s.txt", formatter.format(now))
            val file = File(directory, fileName)
            val fileOutputStream = file.outputStream()
            L.i("freeSpace=${directory.freeSpace}")
            L.i("usableSpace=${directory.usableSpace}")
            L.i("directorySizeBytes=${directory.directorySizeBytes()}")
            return@withContext SensorFileWriter(directory, fileOutputStream)
        }
    }
}

private fun File.directorySizeBytes(): Long {
    var sum = 0L
    val listFiles = this.listFiles() ?: return sum
    for (element: File in listFiles) {
        sum += element.length()
    }
    return sum
}
