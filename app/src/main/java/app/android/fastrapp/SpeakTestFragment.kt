package app.android.fastrapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pwnetics.metric.WordSequenceAligner
import java.util.*


class SpeakTestFragment : Fragment() {
    lateinit var helper: UserPrefsHelper

    fun initialize(helper: UserPrefsHelper) {
        this.helper = helper
    }

    private val REQ_CODE = 100
    private val RESULT_OK = 200
    private val WER_THRESHOLD = 0.25

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.speak_test_page_fragment, container, false)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak")
        val toggleRecordingButton = view.findViewById<ImageButton>(R.id.mic_button)

        toggleRecordingButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.RECORD_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                requestPermissions(permissions, 0)
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                val testSentence = getString(R.string.speech_test_case)
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please repeat: '$testSentence'")
                startActivityForResult(intent, REQ_CODE)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE -> {
                if (data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val test_case = getString(R.string.speech_test_case).split(" ").toTypedArray()
                    for (r in result) {
                        val wsa = WordSequenceAligner()
                        val arrayResult = r.split(" ").toTypedArray()
                        val alignment = wsa.align(arrayResult, test_case)
                        val wer = wsa.SummaryStatistics(listOf(alignment)).wordErrorRate

                        Log.d("alignment", alignment.toString())
                        Log.d("wer", wer.toString())

                        if (wer < WER_THRESHOLD) {
                            // TODO
                        }
                    }
                }
            }
        }
    }
}
