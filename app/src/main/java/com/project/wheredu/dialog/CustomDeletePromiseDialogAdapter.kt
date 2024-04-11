package com.project.wheredu.dialog

import android.app.Dialog
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.databinding.CustomDeletepromiseBinding


class CustomDeletePromiseDialogAdapter(private val context : AppCompatActivity) {

    private lateinit var listener: CustomDialogAcceptButtonClick
    private lateinit var binding : CustomDeletepromiseBinding
    private val dlg = Dialog(context)

    fun show() {
        binding = CustomDeletepromiseBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.promiseDeleteBTN.setOnClickListener {
            listener.onAcceptClick("pass")
            dlg.dismiss()
        }

        binding.promiseDeleteCancelBTN.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    fun setOnAcceptClickedListener(listener: (String) -> Unit) {
        this.listener = object: CustomDialogAcceptButtonClick {
            override fun onAcceptClick(friendNickname: String) {
                listener(friendNickname)
            }

        }
    }

    interface CustomDialogAcceptButtonClick {
        fun onAcceptClick(friendNickname: String)
    }
}