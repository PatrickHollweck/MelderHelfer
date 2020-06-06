package de.patrickable.melderhelfer.core

import android.content.Context
import android.content.SharedPreferences

class Settings
{
    companion object {
        private const val KEY_TARGET_PHONE_NUMBERS = "TARGET_PHONE_NUMBER"
        private const val KEY_WHATSAPP_GROUP_LINK = "WHATSAPP_GROUP_LINK"
        private const val KEY_IS_TTS_MUTE = "ENABLE_TTS"
        private const val KEY_IS_MUTE = "IS_MUTED"

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences("preferences-v1", 0)
        }

        fun setPhoneNumber(context: Context, phoneNumber: String) {
            getSharedPreferences(context).edit().putString(KEY_TARGET_PHONE_NUMBERS, phoneNumber).apply()
        }

        fun getPhoneNumber(context: Context): String {
            return getSharedPreferences(context).getString(KEY_TARGET_PHONE_NUMBERS, "") ?: ""
        }

        fun setWhatsappGroupLink(context: Context, groupLink: String) {
            getSharedPreferences(context).edit().putString(KEY_WHATSAPP_GROUP_LINK, groupLink).apply()
        }

        fun getWhatsappGroupLink(context: Context): String {
            return getSharedPreferences(context).getString(KEY_WHATSAPP_GROUP_LINK, "") ?: ""
        }

        fun setIsTTSMuted(context: Context, enable: Boolean) {
            getSharedPreferences(context).edit().putBoolean(KEY_IS_TTS_MUTE, enable).apply()
        }

        fun getIsTTSMuted(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(KEY_IS_TTS_MUTE, false);
        }

        fun setIsMute(context: Context, enable: Boolean) {
            getSharedPreferences(context).edit().putBoolean(KEY_IS_MUTE, enable).apply()
        }

        fun getIsMute(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(KEY_IS_MUTE, false)
        }
    }
}