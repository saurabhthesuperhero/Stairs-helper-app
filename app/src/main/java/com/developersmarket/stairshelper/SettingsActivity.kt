package com.developersmarket.stairshelper

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.VibrationEffect
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.developersmarket.stairshelper.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var vibrationDuration: Long = 200
    private var vibrationStrength: Int = 50
    private var vibrationFrequency: Int = 0
    private var vibrationEffect: VibrationEffect? = null

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("vibration_settings", Context.MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.spinnerVibrationPattern.adapter = ArrayAdapter.createFromResource(
            this, R.array.vibration_patterns,  android.R.layout.simple_spinner_dropdown_item
        )
        binding.seekbarVibrationDuration.setOnSeekBarChangeListener(durationChangeListener)
        binding.seekbarVibrationStrength.setOnSeekBarChangeListener(strengthChangeListener)
        binding.seekbarVibrationFrequency.setOnSeekBarChangeListener(frequencyChangeListener)
        binding.spinnerVibrationPattern.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createVibrationEffect(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private val durationChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            vibrationDuration = progress.toLong()
            binding.textviewVibrationDuration.text = "Vibration Duration: $vibrationDuration ms"
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
    private val strengthChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            vibrationStrength = progress
            binding.textviewVibrationStrength.text = "Vibration Strength: $vibrationStrength"
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private val frequencyChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            vibrationFrequency = progress
            binding.textviewVibrationFrequency.text = "Vibration Frequency: $vibrationFrequency"
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }


    private fun createVibrationEffect(pattern: Int) {
        when (pattern) {
            0 -> vibrationEffect =
                VibrationEffect.createOneShot(vibrationDuration, vibrationStrength)
            1 -> vibrationEffect = VibrationEffect.createWaveform(
                longArrayOf(0, vibrationDuration),
                intArrayOf(0, vibrationStrength),
                vibrationFrequency
            )
            2 -> vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        }
    }

    private fun saveVibrationSettings() {
        with(sharedPreferences.edit()) {
            putLong("duration", vibrationDuration)
            putInt("strength", vibrationStrength)
            putInt("frequency", vibrationFrequency)
            putInt("pattern", binding.spinnerVibrationPattern.selectedItemPosition)
            apply()
        }
    }

    private fun loadVibrationSettings() {
        vibrationDuration = sharedPreferences.getLong("duration", 200)
        vibrationStrength = sharedPreferences.getInt("strength", 50)
        vibrationFrequency = sharedPreferences.getInt("frequency", 0)
        val pattern = sharedPreferences.getInt("pattern", 0)
        binding.spinnerVibrationPattern.setSelection(pattern)
        createVibrationEffect(pattern)
        binding.seekbarVibrationDuration.progress = vibrationDuration.toInt()
        binding.seekbarVibrationStrength.progress = vibrationStrength
        binding.textviewVibrationDuration.text = "Vibration Duration: $vibrationDuration ms"
        binding.textviewVibrationStrength.text = "Vibration Strength: $vibrationStrength"
    }

    override fun onPause() {
        super.onPause()
        binding.spinnerVibrationPattern.onItemSelectedListener = null
        saveVibrationSettings()
    }

    override fun onResume() {
        super.onResume()
        binding.spinnerVibrationPattern.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                createVibrationEffect(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        loadVibrationSettings()
    }


}
