package com.project.wheredu

import android.content.Context
import android.widget.Toast

class ToastMessage() {
    companion object {
        fun show(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}