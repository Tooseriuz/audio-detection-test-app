package org.rfcx.audiodetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import org.rfcx.guardian.guardian.audio.detect.AudioConverter
import org.rfcx.guardian.guardian.audio.detect.pipeline.MLPredictor
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val predictor = MLPredictor().also {
                it.load(this)
                val btn = this.findViewById<Button>(R.id.btnDetect)
                btn.setOnClickListener { s ->
                    it.run(AudioConverter.readAudioSimple(Environment.getExternalStorageDirectory().absolutePath + "/1608282235295.wav"))
                }
            }
        } catch (e: Exception) {
            Log.e("Rfcx", e.message)
        }
    }
}
