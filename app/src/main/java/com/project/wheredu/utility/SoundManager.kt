package com.project.wheredu.utility

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import com.project.wheredu.R

class SoundManager(private val context: Context) {
    private val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    private var soundEnabled = preferences.getBoolean("soundEnabled", true)

    private val soundPool: SoundPool
    private val fingerUpSound: Int
    private val fingerDownSound: Int
    private val fingerChosenSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()

        fingerUpSound = soundPool.load(context, R.raw.finger_up, 1)
        fingerDownSound = soundPool.load(context, R.raw.finger_down, 1)
        fingerChosenSound = soundPool.load(context, R.raw.finger_chosen, 1)
    }

    private fun playSound(soundId: Int) {
        if (soundEnabled) soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun playFingerUp() {
        playSound(fingerUpSound)
    }

    fun playFingerDown() {
        playSound(fingerDownSound)
    }

    fun playFingerChosen() {
        playSound(fingerChosenSound)
    }

    fun toggleSound() {
        soundEnabled = !soundEnabled
        preferences.edit().putBoolean("soundEnabled", soundEnabled).apply()
        val text = "효과음 ${if (soundEnabled) "활성화" else "비 활설화"}"
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}