package com.project.wheredu.dialog

import android.app.Dialog
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.databinding.CustomFriendDeleteBinding


class CustomFriendDeleteDialogAdapter(private val context : AppCompatActivity) {

    private lateinit var listener: CustomDialogAcceptButtonClick
    private lateinit var binding : CustomFriendDeleteBinding
    private val dlg = Dialog(context)

    fun show(content: String) {
        binding = CustomFriendDeleteBinding.inflate(context.layoutInflater)

        binding.ctNicknameTV.text = content

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.friendDeleteBTN.setOnClickListener {
            listener.onAcceptClick("pass")
            dlg.dismiss()
        }

        binding.friendDeleteCancelBTN.setOnClickListener {
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