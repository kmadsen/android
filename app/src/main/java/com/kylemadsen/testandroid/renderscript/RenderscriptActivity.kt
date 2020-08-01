package com.kylemadsen.testandroid.renderscript

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.renderscript.*
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.kylemadsen.testandroid.R
import kotlinx.android.synthetic.main.activity_renderscript.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class RenderscriptActivity : AppCompatActivity() {

    private var originalBitmap: Bitmap? = null
    private var blurredBitmap: Bitmap? = null
    private val seekProgressChannel = Channel<Int>(1)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renderscript)

        CoroutineScope(Dispatchers.Main).launch {
            loadBitmap()?.let { bitmap ->
                originalBitmap = bitmap
                blurredBitmap = bitmap.copy(bitmap.config, true)
                imageView.setImageBitmap(bitmap)
                seekProgressChannel.consumeAsFlow()
                    .collectLatest { blurBitmap(bitmap, blurredBitmap!!, it) }
            }
        }

        initBlurControls()
    }

    override fun onDestroy() {
        originalBitmap?.recycle()
        blurredBitmap?.recycle()
        super.onDestroy()
    }

    private fun initBlurControls() {
        seekBar.max = 24
        seekBar.progress = 0
        seekBarText.text = "Blur radius: ${seekBar.progress}"
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                seekBarText.text = "Blur radius: ${seekBar.progress}"
                seekProgressChannel.offer(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) { }
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    private suspend fun loadBitmap(): Bitmap? = withContext(Dispatchers.IO) {
        assets.open("st_anton_2019.jpg")
            .use { BitmapFactory.decodeStream(it) }
    }

    private suspend fun blurBitmap(
        originalBitmap: Bitmap,
        blurredBitmap: Bitmap,
        progress: Int
    ) = withContext(Dispatchers.Main) {
        blurBitmap(originalBitmap, blurredBitmap, progress + 1.0f)
        imageView.setImageBitmap(blurredBitmap)
    }

    private suspend fun blurBitmap(
        bitmap: Bitmap,
        outputBitmap: Bitmap,
        radius: Float
    ) = withContext(Dispatchers.IO) {
        //Create renderscript
        val rs: RenderScript = RenderScript.create(this@RenderscriptActivity)

        //Create allocation from Bitmap
        val allocation: Allocation = Allocation.createFromBitmap(rs, bitmap)

        //Create allocation with the same type
        val blurredAllocation: Allocation = Allocation.createTyped(rs, allocation.type)

        //Create script
        val blurScript: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius)
        //Set input for script
        blurScript.setInput(allocation)
        //Call script for output allocation
        blurScript.forEach(blurredAllocation)

        //Copy script result into bitmap
        blurredAllocation.copyTo(outputBitmap)

        //Destroy everything to free memory
        allocation.destroy()
        blurredAllocation.destroy()
        blurScript.destroy()
        rs.destroy()
    }
}
