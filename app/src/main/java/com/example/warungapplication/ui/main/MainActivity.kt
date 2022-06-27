package com.example.warungapplication.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warungapplication.MyApplication
import com.example.warungapplication.R
import com.example.warungapplication.ViewModelFactory
import com.example.warungapplication.databinding.ActivityMainBinding
import com.example.warungapplication.ui.add.AddActivity
import com.example.warungapplication.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory : ViewModelFactory

    private val viewModel : MainViewModel by viewModels {
        factory
    }

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val mainAdapter : MainAdapter by lazy {
        MainAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.apply {
            title = "Daftar Warung"
            overflowIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_logout)
            inflateMenu(R.menu.logout)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.logout ->{
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        viewModel.signOut()
                        finish()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
        binding.floatingActionButton.setOnClickListener { moveToAdd() }
        initAction()
        initRecyclerView()
        observe()
    }
    private fun moveToAdd(){
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun observe(){
        viewModel.getAllWarung().observe(this){
            if (it != null) {
                binding.progressBar.visibility = View.VISIBLE
                activityScope.launch {
                    delay(3000)
                    binding.progressBar.visibility = View.GONE
                    mainAdapter.setData(it)
                }
            }
            else{
                binding.progressBar.visibility = View.GONE
            }
        }
    }
    private fun initRecyclerView(){
        with(binding.rvWarung) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = mainAdapter
        }
    }
    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val warung = (viewHolder as MainAdapter.MyViewHolder).getWarung
                viewModel.deleteWarung(warung)
            }

        })
        itemTouchHelper.attachToRecyclerView(binding.rvWarung)
    }
}