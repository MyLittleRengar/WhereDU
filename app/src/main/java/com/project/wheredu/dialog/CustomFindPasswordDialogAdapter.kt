package com.project.wheredu.dialog

import android.app.Dialog
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.databinding.CustomFindPwBinding


class CustomFindPasswordDialogAdapter(private val context : AppCompatActivity) {

    private lateinit var listener: CustomDialogAcceptButtonClick
    private lateinit var binding : CustomFindPwBinding
    private val dlg = Dialog(context)

    fun show() {
        binding = CustomFindPwBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.changeAcceptBTN.setOnClickListener {
            val changePwText = binding.findPasswordET.text.toString()
            val changePwCheckText = binding.findPasswordCheckET.text.toString()
            if(changePwText.isNotBlank()){
                if(changePwText == changePwCheckText) {
                    listener.onAcceptClick(changePwText)
                }
                else {
                    Toast.makeText(context, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(context, "변경할 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            dlg.dismiss()
        }

        dlg.show()
    }

    fun setOnAcceptClickedListener(listener: (String) -> Unit) {
        this.listener = object: CustomDialogAcceptButtonClick {
            override fun onAcceptClick(pw: String) {
                listener(pw)
            }

        }
    }

    interface CustomDialogAcceptButtonClick {
        fun onAcceptClick(pw: String)
    }
}