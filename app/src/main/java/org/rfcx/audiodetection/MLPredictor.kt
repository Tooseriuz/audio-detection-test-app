package org.rfcx.guardian.guardian.audio.detect.pipeline

import android.content.Context
import android.os.Environment
import android.util.Log
import org.rfcx.audiodetection.interfaces.Predictor
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.File
import java.lang.Exception

/**
 * A predictor for tflite models (based on MLKit)
 */
class MLPredictor: Predictor {

    private var interpreter: Interpreter? = null

    override val isLoaded: Boolean
        get() = interpreter != null

    override fun load(context: Context) {
        if (interpreter != null) return
        val compatList = CompatibilityList()

        val options = Interpreter.Options().apply{
            if(compatList.isDelegateSupportedOnThisDevice){
                // if the device has a supported GPU, add the GPU delegate
                val delegateOptions = compatList.bestOptionsForThisDevice
                this.addDelegate(GpuDelegate(delegateOptions))
            } else {
                // if the GPU is not supported, run on 4 threads
                this.setNumThreads(4)
            }
        }
        try {
            interpreter = Interpreter(File(Environment.getExternalStorageDirectory(), "yamnet.tflite"), options)
        } catch (e: Exception) {
            Log.e("Rfcx", e.message)
        }
    }

    override fun run(input: FloatArray) {
        if (interpreter == null) {
            return
        }
        val outputShape: Array<FloatArray> = arrayOf(FloatArray(521))
        Log.d("Rfcx", input.size.toString())
        try {
            input.toList().chunked(15600).forEach {
                if (it.size == 15600) {
                    interpreter?.run(it.toFloatArray(), outputShape)
                    outputShape[0].forEachIndexed { index, fl ->
                        if (fl != 0f && fl >= 0.01f) {
                            Log.d("Rfcx", "$index : $fl")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Rfcx", e.message)
        }

//        outputShape[0].filter { it != 0f }.forEach {
//            Log.d("Rfcx", it.toString())
//        }
    }

    private fun FloatArray.toSmallChunk(number: Int): FloatArray {
        return this.copyOfRange(0, number)
    }

}
