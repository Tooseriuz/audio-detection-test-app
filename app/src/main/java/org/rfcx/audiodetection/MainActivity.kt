package org.rfcx.audiodetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import org.rfcx.audiodetection.AudioConverter.sliceTo
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            MLPredictor().also {
                it.load(this)
                val btn = this.findViewById<Button>(R.id.btnDetect)
                btn.setOnClickListener { s ->
                    val audio = AudioConverter.readAudioSimple(Environment.getExternalStorageDirectory().absolutePath + "/1608282235295.wav").sliceTo(0)
                    audio.forEachIndexed { index, audioChunk ->
                        if (audioChunk.size == 15600) {
                            val output = it.run(audioChunk)
                            Log.d("Rfcx", output)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Rfcx", e.message)
        }
    }
}
