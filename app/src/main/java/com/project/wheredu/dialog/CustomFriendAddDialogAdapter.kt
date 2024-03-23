package com.project.wheredu.dialog

import android.app.Dialog
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.databinding.CustomFriendAddDialogBinding


class CustomFriendAddDialogAdapter(private val context : AppCompatActivity) {

    private lateinit var listener: CustomDialogAcceptButtonClick
    private lateinit var binding : CustomFriendAddDialogBinding
    private val dlg = Dialog(context)

    fun show() {
        binding = CustomFriendAddDialogBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.changeAcceptBTN.setOnClickListener {
            val friendNickname = binding.friendAddET.text.toString()
            if(friendNickname.isNotBlank()){
                listener.onAcceptClick(friendNickname)
            }
            else {
                Toast.makeText(context, "친구의 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            dlg.dismiss()
        }

        binding.changeCancelBTN.setOnClickListener {
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