package com.project.wheredu

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.ActivityCompat
import com.project.wheredu.login.FindPasswordActivity

object CustomDialog {

    fun CustomFindDialog(context: Context, activity: Activity, title:String, content:String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(content)
            .setCancelable(false)
            .setPositiveButton("비밀번호 찾기") { _, _ ->
                context.startActivity(Intent(context, FindPasswordActivity::class.java))
                ActivityCompat.finishAffinity(activity)
            }
            .setNeutralButton("닫기") { _, _ -> }
        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#ea4e3d"))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#3478f6"))
    }
}