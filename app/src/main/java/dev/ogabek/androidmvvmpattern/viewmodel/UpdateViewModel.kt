package dev.ogabek.androidmvvmpattern.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.ogabek.androidmvvmpattern.model.Post
import dev.ogabek.androidmvvmpattern.networking.RetrofitHttp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateViewModel: ViewModel() {

    var isDone: MutableLiveData<Boolean> = MutableLiveData()

    fun updatePost(post: Post): LiveData<Boolean> {
        RetrofitHttp.postService.updatePost(post.id, post).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("RetrofitHttps", "createPost::onSuccess - $post")
                isDone.value = true
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("RetrofitHttps", "createPost::onError - $t")
                isDone.value = false
            }
        })
        return isDone
    }

}