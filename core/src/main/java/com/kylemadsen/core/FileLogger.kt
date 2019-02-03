package com.kylemadsen.core

import android.content.Context
import android.os.SystemClock
import com.kylemadsen.core.logger.L
import io.reactivex.Observable
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileLogger(private val context: Context) {

    fun observeWritableFile(): Observable<WritableFile> {
        return Observable.create { emitter ->
            val fileContainer = createWritableFile()
            emitter.onNext(fileContainer)
            emitter.setCancellable {
                L.i("DEBUG_FILE close file")
                fileContainer.close()
            }
        }
    }

    private fun createWritableFile(): WritableFile {
        val directory = File(context.filesDir, directoryName)
        directory.mkdir()

        val now = Date()
        val fileName = String.format("${fileNamePrefix}_%s.txt", formatter.format(now))
        val file = File(directory, fileName)
        val fileOutputStream = file.outputStream()
        L.i("DEBUG_FILE freeSpace=${directory.freeSpace}")
        L.i("DEBUG_FILE usableSpace=${directory.usableSpace}")
        L.i("DEBUG_FILE directorySizeBytes=${directory.directorySizeBytes()}")
        return WritableFile(file, fileOutputStream)
    }

    companion object {
        private const val directoryName = "telematics"
        private const val fileNamePrefix = "sensors"
        private val formatter = SimpleDateFormat("yyy_MM_dd_HH_mm_ss", Locale.ENGLISH)
    }
}

fun File.directorySizeBytes(): Long {
    var sum = 0L
    for (element: File in this.listFiles()) {
        sum += element.length()
    }
    return sum
}

class WritableFile(
        private val file: File,
        private val fileOutputStream: FileOutputStream
) {
    private val bufferedWriter: BufferedWriter = fileOutputStream.bufferedWriter()

    fun close() {
        fileOutputStream.close()
    }

    fun writeLine(message: String) {
        bufferedWriter.write(message)
        bufferedWriter.newLine()
    }

    fun flushBuffer() {
        bufferedWriter.flush()
    }
}
