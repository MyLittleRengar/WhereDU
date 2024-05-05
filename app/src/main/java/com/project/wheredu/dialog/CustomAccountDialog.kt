package com.project.wheredu.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.project.wheredu.R
import com.project.wheredu.databinding.DialogAccountBinding


class CustomAccountDialog(context: Context, private val okCallback: (String) -> Unit) : Dialog(context) {
    private lateinit var binding: DialogAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

        val window = window
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = 400.dpToPx(context)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams
    }

    private fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun initViews() = with(binding) {
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var spinnerData = ""

        val spinnerValue = root.resources.getStringArray(R.array.account)
        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, spinnerValue)
        binding.bankSp.adapter = spinnerAdapter
        binding.bankSp.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerData = when(position) {
                    0 -> "null"
                    1 -> "농협"
                    2 -> "국민은행"
                    3 -> "카카오뱅크"
                    else -> "대구은행"
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        dialogOkBtn.setOnClickListener {
            if(spinnerData != "null") {
                if(accountEt.text.isNotEmpty()) {
                    okCallback(spinnerData +"/"+accountEt.text.toString())
                    dismiss()
                }

            }
            else {
                Toast.makeText(context, "은행을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}