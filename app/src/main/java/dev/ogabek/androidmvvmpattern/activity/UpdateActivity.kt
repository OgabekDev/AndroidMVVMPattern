package dev.ogabek.androidmvvmpattern.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.ogabek.androidmvvmpattern.R
import dev.ogabek.androidmvvmpattern.model.Post
import dev.ogabek.androidmvvmpattern.networking.RetrofitHttp
import dev.ogabek.androidmvvmpattern.viewmodel.UpdateViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateActivity : AppCompatActivity() {
    private lateinit var et_title: EditText
    private lateinit var et_body: EditText
    private lateinit var btn_update: Button
    lateinit var post: Post

    lateinit var viewModel: UpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        initViews()

        getData()

    }

    private fun getData() {
        post = intent.getSerializableExtra("post") as Post
        et_title.setText(post.title)
        et_body.setText(post.body)
    }

    private fun initViews() {

        viewModel = ViewModelProvider(this).get(UpdateViewModel::class.java)

        et_title = findViewById(R.id.et_title_update)
        et_body = findViewById(R.id.et_body_update)
        btn_update = findViewById(R.id.btn_update)

        btn_update.setOnClickListener {
            if (!et_title.text.isNullOrEmpty() && !et_body.text.isNullOrEmpty()) {
                viewModel.updatePost(Post(post.id, post.userId, et_title.text.toString(), et_body.text.toString())).observe(this, Observer {
                    sendResult(it)
                })
            }
        }

    }

    private fun sendResult(isDone: Boolean) {
        val intent = Intent()
        intent.putExtra("status", isDone)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        sendResult(false)
    }
}