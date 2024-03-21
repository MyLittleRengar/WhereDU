package com.project.wheredu

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class FriendsInfoActivity : AppCompatActivity() {

    private lateinit var friendsProfileIv: CircleImageView
    private var selectedImageUri: Uri? = null
    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_info)

        friendsProfileIv = findViewById(R.id.friends_profileIV)

        friendsProfileIv.setOnClickListener {
            downloadImage()
        }
    }

    private fun downloadImage() {
        val call = service.downloadImage("관리자")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        friendsProfileIv.setImageBitmap(bitmap)
                    }
                } else {
                    Log.e("SSSSSSSSSSS", "다운로드 실패")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SSSSSSSSSSS", "Image download failed: ${t.message}")
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            friendsProfileIv.setImageURI(selectedImageUri)
            selectedImageUri?.let { uri ->
                val newUri = Uri.parse(getPathFromUri(uri))
                uploadImage(newUri)
            }
        }
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 100
    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun uploadImage(imageUri: Uri) {
        val file = imageUri.path?.let { File(it) }
        val requestFile =
            file?.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }
        val body = requestFile?.let { MultipartBody.Part.createFormData("image", file.name, it) }

        val usernameRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "닉네임")

        GlobalScope.launch(Dispatchers.IO) {
            body?.let { service.uploadImage(usernameRequestBody, it) }
                ?.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.e("SSSSSSSSSSS", "업로드 성공")
                        } else {
                            Log.e("SSSSSSSSSSS", "업로드 실패")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("SSSSSSSSSSS", "Image upload failed: ${t.message}")
                    }
                })
        }
    }
    private fun getPathFromUri(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToNext()
        val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        cursor.close()
        return path
    }
}