package com.project.wheredu.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.project.wheredu.databinding.CustomFindIdBinding
import com.project.wheredu.login.FindPasswordActivity


class CustomFindIdDialogAdapter(private val context : AppCompatActivity, private val userID: String, private val activity: Activity) {

    private lateinit var binding : CustomFindIdBinding
    private val dlg = Dialog(context)

    fun show() {
        binding = CustomFindIdBinding.inflate(context.layoutInflater)

        binding.ctIdTV.text = userID

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.findIdPasswordChangeBTN.setOnClickListener {
            context.startActivity(Intent(context, FindPasswordActivity::class.java))
            ActivityCompat.finishAffinity(activity)
            dlg.dismiss()
        }

        binding.findIdCancelBTN.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}