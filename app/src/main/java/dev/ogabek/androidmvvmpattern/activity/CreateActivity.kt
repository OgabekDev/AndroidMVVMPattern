package dev.ogabek.androidmvvmpattern.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.ogabek.androidmvvmpattern.R
import dev.ogabek.androidmvvmpattern.model.Post
import dev.ogabek.androidmvvmpattern.networking.RetrofitHttp
import dev.ogabek.androidmvvmpattern.viewmodel.CreateViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateActivity : AppCompatActivity() {

    private lateinit var et_title: EditText
    private lateinit var et_body: EditText
    private lateinit var btn_create: Button

    lateinit var viewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        initViews()

    }

    private fun initViews() {

        viewModel = ViewModelProvider(this).get(CreateViewModel::class.java)

        et_title = findViewById(R.id.et_title)
        et_body = findViewById(R.id.et_body)
        btn_create = findViewById(R.id.btn_create)

        btn_create.setOnClickListener {
            if (!et_title.text.isNullOrEmpty() && !et_body.text.isNullOrEmpty()) {
                viewModel.createPost(Post(0, 0, et_title.text.toString(), et_body.text.toString())).observe(this, Observer {
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