package org.rfcx.audiodetection

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

    override fun run(input: FloatArray): String {
        if (interpreter == null) {
            return ""
        }
        // fix output size to 521
        val outputShape: Array<FloatArray> = arrayOf(FloatArray(521))
        try {
            interpreter?.run(arrayOf(input), outputShape)
        } catch (e: Exception) {
            Log.e("Rfcx", e.message)
        }

        val filteredOutput = arrayListOf<String>()
        outputShape[0].forEachIndexed { index, fl ->
            // pick only confidence more than 0.1
            if (fl != 0f && fl >= 0.1f) {
                filteredOutput.add("$index-$fl")
            }
        }

        return filteredOutput.joinToString("*")
    }

}
