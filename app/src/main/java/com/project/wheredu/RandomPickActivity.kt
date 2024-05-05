package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.databinding.ActivityRandomPickBinding
import com.project.wheredu.utility.Chooser

class RandomPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomPickBinding
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRandomPickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chooser.motionLayout = binding.motionLayout

        preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        loadPreferences()

        binding.btnCount.setOnClickListener { updateCount() }
        binding.btnMode.setOnClickListener { updateMode() }
        binding.btnMode.setOnLongClickListener { toggleSound() }
    }

    private fun loadPreferences() {
        with(binding.chooser) {
            count = preferences.getInt("count", 1)
            mode = Chooser.Mode.valueOf(preferences.getString("mode", "SINGLE")!!)

            updateModeUI()
            updateCountUI()
            motionLayout.progress = if (mode == Chooser.Mode.ORDER) 1f else 0f
        }
    }

    private fun savePreference(key: String, value: Any) {
        with(preferences.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Invalid type for SharedPreferences")
            }
            apply()
        }
    }

    private fun updateMode() {
        with(binding.chooser) {
            if (motionLayout.currentState != -1) {
                mode = when (mode) {
                    Chooser.Mode.SINGLE -> Chooser.Mode.GROUP
                    Chooser.Mode.GROUP -> Chooser.Mode.ORDER
                    Chooser.Mode.ORDER -> Chooser.Mode.SINGLE
                }

                count = if (mode == Chooser.Mode.GROUP) 2 else 1
                updateModeUI()
                updateCountUI()
            }
        }
    }

    private fun updateCount() {
        with(binding.chooser) {
            count = when (mode) {
                Chooser.Mode.SINGLE -> count % 5 + 1
                Chooser.Mode.GROUP -> (count - 1) % 4 + 2
                Chooser.Mode.ORDER -> 1
            }

            updateCountUI()
        }
    }

    private fun toggleSound(): Boolean {
        binding.chooser.soundManager.toggleSound()
        return true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateModeUI() {
        val mode = binding.chooser.mode
        val drawable = when (mode) {
            Chooser.Mode.SINGLE -> R.drawable.single_icon
            Chooser.Mode.GROUP -> R.drawable.group_icon
            Chooser.Mode.ORDER -> R.drawable.order_icon
        }

        binding.btnMode.foreground = getDrawable(drawable)
        savePreference("mode", mode.toString())
        binding.motionLayout.transitionToState(if (mode == Chooser.Mode.ORDER) R.id.hideCounter else R.id.start)
    }

    private fun updateCountUI() {
        val count = binding.chooser.count
        binding.btnCount.text = count.toString()
        savePreference("count", count)
    }
}