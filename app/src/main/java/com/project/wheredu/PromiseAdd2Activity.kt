package com.project.wheredu

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.recycler.FriendItem
import com.project.wheredu.recycler.PromiseFriendAdapter


class PromiseAdd2Activity : AppCompatActivity(), PromiseFriendAdapter.CheckBoxStateListener {

    private lateinit var promiseFriendRv: RecyclerView
    private var datas = mutableListOf<FriendItem>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add2)

        promiseFriendRv = findViewById(R.id.promiseFriendRV)
        val adapter = PromiseFriendAdapter(this)
        adapter.listener = this@PromiseAdd2Activity
        promiseFriendRv.adapter = adapter

        datas.apply {
            add(FriendItem(BitmapFactory.decodeResource(resources, R.drawable.free), "Test1"))
            add(FriendItem(BitmapFactory.decodeResource(resources, R.drawable.free), "Test2"))
            add(FriendItem(BitmapFactory.decodeResource(resources, R.drawable.free), "Test3"))
        }
        promiseFriendRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        promiseFriendRv.setHasFixedSize(true)
        adapter.datas = datas
        adapter.notifyDataSetChanged()
    }

    override fun onCheckBoxStateChanged(position: Int, isChecked: Boolean, nickname: String) {
        Log.e("RRRRRRRRRR", "${nickname}/${isChecked}")
    }
}