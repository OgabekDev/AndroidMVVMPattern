package dev.ogabek.androidmvvmpattern.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.ogabek.androidmvvmpattern.R
import dev.ogabek.androidmvvmpattern.adapter.PostAdapter
import dev.ogabek.androidmvvmpattern.model.Post
import dev.ogabek.androidmvvmpattern.networking.RetrofitHttp
import dev.ogabek.androidmvvmpattern.utils.Utils
import dev.ogabek.androidmvvmpattern.viewmodel.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btn_create: FloatingActionButton
    lateinit var viewModel: MainViewModel

    private val items: ArrayList<Post> = ArrayList()
    private var adapter = PostAdapter(this, items)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }

    private fun initViews() {

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        btn_create = findViewById(R.id.btn_create)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = adapter

        btn_create.setOnClickListener {
            openCreateActivity()
        }


        viewModel.apiPostList().observe(this, Observer {
            refreshAdapter(it)
        })

        setItemTouchHelper()

    }

    private fun openCreateActivity() {
        val intent = Intent(this, CreateActivity::class.java)
        resultActivity.launch(intent)
    }

    fun openUpdateActivity(post: Post) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("post", post)
        resultActivity.launch(intent)
    }

    private var resultActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            if (data!!.getBooleanExtra("status", false)) {
                viewModel.apiPostList().observe(this, Observer {
                    refreshAdapter(it)
                })
            }
        }
    }

    fun refreshAdapter(posters: ArrayList<Post>) {
        this.adapter = PostAdapter(this, posters)
        recyclerView.adapter = adapter
    }

    private fun setItemTouchHelper() {

        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            private val limitScrollX = dpToPx(-75F, this@MainActivity)
            private var currentScrollX = 0
            private var currentScrollWhenInActive = 0
            private var initWhenInActive = 0f
            private var firstInActive = false

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }


            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX == 0f) {
                        currentScrollX = viewHolder.itemView.scrollX
                    }

                    if (isCurrentlyActive) {
                        var scrollOffset = currentScrollX + (-dX).toInt()
                        if (scrollOffset < limitScrollX) {
                            scrollOffset = limitScrollX
                        } else if (scrollOffset > 0) {
                            scrollOffset = 0
                        }

                        viewHolder.itemView.scrollTo(scrollOffset, 0)
                    } else {
                        //slide with auto anim
                        if (firstInActive) {
                            firstInActive = false
                            currentScrollWhenInActive = viewHolder.itemView.scrollX
                            initWhenInActive = dX
                        }

                        if (viewHolder.itemView.scrollX > limitScrollX) {
                            viewHolder.itemView.scrollTo(
                                (currentScrollWhenInActive * dX / initWhenInActive).toInt(),
                                0
                            )
                        }
                    }
                }
            }
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (viewHolder.itemView.scrollX < limitScrollX) {
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                } else if (viewHolder.itemView.scrollX > 0) {
                    viewHolder.itemView.scrollTo(0, 0)
                }
            }

        }).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    private fun dpToPx(dpValue: Float, context: Context): Int {
        return (dpValue * context.resources.displayMetrics.density).toInt()
    }

}