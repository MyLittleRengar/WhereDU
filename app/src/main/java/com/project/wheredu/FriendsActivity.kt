package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.dialog.CustomFriendAddDialogAdapter
import com.project.wheredu.recycler.BookMarkFriendListAdapter
import com.project.wheredu.recycler.FriendItem
import com.project.wheredu.recycler.FriendListAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsAddIv: ImageView
    private lateinit var friendUserProfileIv: ImageView
    private lateinit var friendUserNicknameTv: TextView
    private lateinit var friendsBookmarkRv: RecyclerView
    private lateinit var friendsListRv: RecyclerView
    private lateinit var friendSearchTextEt: EditText
    private lateinit var friendSearchBtn: Button
    private lateinit var friendSearchClearIv: ImageView
    private lateinit var friendsBottomNav: BottomNavigationView

    private lateinit var preferences: SharedPreferences

    private var datas = mutableListOf<FriendItem>()
    private var datas2 = mutableListOf<FriendItem>()
    private lateinit var friendAdapter: FriendListAdapter
    private lateinit var bookmarkFriendAdapter: BookMarkFriendListAdapter

    private val service = Service.getService()

    private lateinit var storeNick: String
    override fun onRestart() {
        super.onRestart()
        datas.clear()
        datas2.clear()
        friendListDataCount(storeNick)
        bookmarkFriendListDataCount(storeNick)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendUserProfileIv = findViewById(R.id.friendUserProfileIV)
        friendUserNicknameTv = findViewById(R.id.friendUserNicknameTV)
        friendsAddIv = findViewById(R.id.friendsAddIv)
        friendsBookmarkRv = findViewById(R.id.friendsBookmarkRV)
        friendsListRv = findViewById(R.id.friendsListRV)
        friendSearchTextEt = findViewById(R.id.friendSearchTextET)
        friendSearchBtn = findViewById(R.id.friendSearchBTN)
        friendSearchClearIv = findViewById(R.id.friendSearchClearIV)
        friendsBottomNav = findViewById(R.id.friends_bottomNav)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        storeNick = preferences.getString("accountNickname", "").toString()
        friendUserNicknameTv.text = storeNick
        downloadImage(storeNick)
        friendListDataCount(storeNick)
        bookmarkFriendListDataCount(storeNick)

        friendSearchClearIv.setOnClickListener {
            if(friendSearchClearIv.visibility == View.VISIBLE) {
                friendSearchTextEt.text.clear()
                friendSearchClearIv.visibility = View.INVISIBLE
            }
        }

        friendSearchTextEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(p0: Editable?) {
                if(friendSearchTextEt.text.isNotBlank()) {
                    friendSearchClearIv.visibility = View.VISIBLE
                }
                else if(friendSearchTextEt.text.isBlank()) {
                    friendSearchClearIv.visibility = View.INVISIBLE
                    datas.clear()
                    friendAdapter.notifyDataSetChanged()
                    friendListDataCount(storeNick)
                }
            }

        })

        friendsAddIv.setOnClickListener {
            val dlg = CustomFriendAddDialogAdapter(this@FriendsActivity)
            dlg.setOnAcceptClickedListener { friendNickname ->
                if(storeNick != friendNickname) {
                    friendAdd(storeNick, friendNickname)
                }
                else {
                    Toast.makeText(this@FriendsActivity, "자기 자신은 영원한 친구입니다", Toast.LENGTH_SHORT).show()
                }
            }
            dlg.show()
        }

        friendSearchBtn.setOnClickListener {
            textSearch(storeNick)
        }

        friendsBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_promise -> {
                    startActivity(Intent(this@FriendsActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_home -> {
                    startActivity(Intent(this@FriendsActivity, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@FriendsActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun textSearch(userNickname: String) {
        val searchText = friendSearchTextEt.text.toString()
        val callPost = service.searchText(searchText, userNickname)
        callPost.enqueue(object: Callback<String?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "NoData") {
                            datas.clear()
                            friendAdapter.notifyDataSetChanged()
                        }
                        else{
                            textReplace(result, userNickname)
                        }

                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun textReplace(text: String, userNickname: String) {
        val textReplace = text.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("friendNickname:","").replace("ownerNickname:","")
        val textSplit = textReplace.split(",")
        loopInt(textSplit.size, userNickname)
    }

    private fun loopInt(data: Int, userNickname:String) {
        for(i in 0 until data) {
            friendListData(userNickname, i)
            if(i == 0) {
                datas.clear()
            }
        }
    }

    private fun loopInt2(data: Int, userNickname:String) {
        for(i in 0 until data) {
            bookmarkFriendListData(userNickname, i)
            if(i == 0) {
                datas2.clear()
            }
        }
    }

    private fun friendListDataCount(userNickname: String) {
        val callPost = service.returnFriendCount(userNickname)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toInt()
                        loopInt(result, userNickname)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun friendListData(userNickname: String, userInt: Int) {
        val callPost = service.friendListData(userNickname, userInt)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        downloadImage2(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun bookmarkFriendListDataCount(userNickname: String) {
        val callPost = service.returnBookMarkFriendCount(userNickname)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toInt()
                        loopInt2(result, userNickname)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun bookmarkFriendListData(userNickname: String, userInt: Int) {
        val callPost = service.bookmarkFriendListData(userNickname, userInt)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        downloadImage3(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun downloadImage3(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        bookmarkInitRecycler(bitmap, userNickname)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }

    private fun downloadImage2(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        initRecycler(bitmap, userNickname)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(img: Bitmap, nickname: String) {
        friendAdapter = FriendListAdapter(this)
        friendsListRv.adapter = friendAdapter

        datas.apply {
            add(FriendItem(img, nickname))
        }
        friendsListRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        friendsListRv.setHasFixedSize(true)
        datas.sortBy { it.nickname }
        friendAdapter.datas = datas
        friendAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun bookmarkInitRecycler(img: Bitmap, nickname: String) {
        bookmarkFriendAdapter = BookMarkFriendListAdapter(this@FriendsActivity)
        friendsBookmarkRv.adapter = bookmarkFriendAdapter

        datas2.apply {
            add(FriendItem(img, nickname))
        }
        friendsBookmarkRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        friendsBookmarkRv.setHasFixedSize(true)
        datas2.sortBy { it.nickname }
        bookmarkFriendAdapter.datas = datas2
        bookmarkFriendAdapter.notifyDataSetChanged()
    }

    private fun downloadImage(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        friendUserProfileIv.setImageBitmap(bitmap)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }

    private fun friendAdd(userNickname: String, friendNickname: String) {
        val callPost = service.friendAdd(userNickname, friendNickname)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@FriendsActivity,"친구 추가가 완료되었습니다", Toast.LENGTH_SHORT).show()
                        }
                        if(result == "nicknameFail"){
                            Toast.makeText(this@FriendsActivity,"친구 닉네임이 틀립니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
}