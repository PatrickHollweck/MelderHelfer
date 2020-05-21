package de.patrickable.melderhelfer.core.util

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

class EasyTextToSpeach(
    private val activity: Activity,
    private val message: String
) : TextToSpeech.OnInitListener {
    companion object {
        fun say(activity: Activity, message: String) {
            EasyTextToSpeach(activity, message)
        }

        fun unMuteSounds(activity: Activity) {
            val audioService = activity.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            audioService.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_UNMUTE,
                AudioManager.FLAG_VIBRATE
            )

            audioService.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioService.getStreamMaxVolume(
                    AudioManager.STREAM_MUSIC
                ),
                AudioManager.FLAG_VIBRATE
            )
        }
    }

    private val tts: TextToSpeech = TextToSpeech(activity, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = setLocale(
                listOf(Locale.GERMANY, Locale.GERMAN, Locale.ENGLISH, Locale.US)
            )

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(activity, "Could not activate Text-To-Speech (Install language packets!)", Toast.LENGTH_SHORT).show()
            } else {
                tts.setSpeechRate(0.8f)
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } else {
            Toast.makeText(activity, "Text-To-Speach Initilization Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLocale(locales: List<Locale>): Int {
        for (locale in locales) {
            if (tts.availableLanguages.contains(locale)) {
                return tts.setLanguage(locale)
            }
        }

        return TextToSpeech.LANG_NOT_SUPPORTED
    }
}