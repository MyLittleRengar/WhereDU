package com.project.wheredu.recycler

import java.io.Serializable

data class CheckItem(val nickname: String, val check: Boolean): Serializable {
    override fun toString(): String {
        return "$nickname=$check"
    }
}