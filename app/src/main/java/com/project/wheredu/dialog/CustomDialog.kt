package com.project.wheredu.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import com.project.wheredu.databinding.DialogCustomBinding


class CustomDialog(context: Context, private val okCallback: (String) -> Unit) : Dialog(context) {
    private lateinit var binding: DialogCustomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCustomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogSb.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                dialogCountTv.text = "${progress}명"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        dialogBtn.setOnClickListener {
            if(dialogSb.progress != 0) {
                dialogCountTv.text = dialogSb.progress.toString()
                okCallback(dialogSb.progress.toString())
                dismiss()
            }
            else {
                Toast.makeText(context, "인원을 지정해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}