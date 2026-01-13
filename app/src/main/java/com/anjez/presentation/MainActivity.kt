package com.anjez.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.anjez.databinding.ActivityMainBinding
import com.anjez.presentation.adapter.TaskAdapter
import com.anjez.presentation.viewmodel.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
        setupObservers()
        setupDragAndDrop()
    }
    
    private fun setupViewModel() {
        val repository = (application as com.anjez.AnjezApp).repository
        viewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(repository)
        )[TaskViewModel::class.java]
    }
    
    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "أنجز - مدير المهام"
        
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                showTaskDetails(task)
            },
            onTaskLongClick = { task ->
                showTaskOptions(task)
            },
            onTaskToggle = { task ->
                viewModel.toggleTaskStatus(task)
            }
        )
        
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
        
        binding.fabAddTask.setOnClickListener {
            startActivity(AddTaskActivity.newIntent(this))
        }
        
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.anjez.R.id.menu_all -> viewModel.setFilter(TaskFilter.ALL)
                com.anjez.R.id.menu_completed -> viewModel.setFilter(TaskFilter.COMPLETED)
                com.anjez.R.id.menu_in_progress -> viewModel.setFilter(TaskFilter.IN_PROGRESS)
                com.anjez.R.id.menu_high_priority -> viewModel.setFilter(TaskFilter.HIGH_PRIORITY)
                com.anjez.R.id.menu_overdue -> viewModel.setFilter(TaskFilter.OVERDUE)
            }
            true
        }
        
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                com.anjez.R.id.menu_settings -> {
                    startActivity(SettingsActivity.newIntent(this))
                    true
                }
                com.anjez.R.id.menu_search -> {
                    toggleSearchView()
                    true
                }
                else -> false
            }
        }
        
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            viewModel.setSearchQuery(query)
            binding.searchView.hide()
            false
        }
    }
    
    private fun setupObservers() {
        viewModel.filteredTasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
            binding.textEmpty.isVisible = tasks.isEmpty()
            binding.imageEmpty.isVisible = tasks.isEmpty()
        }
        
        viewModel.statistics.observe(this) { stats ->
            updateStatisticsUI(stats)
        }
    }
    
    private fun setupDragAndDrop() {
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                target: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                taskAdapter.moveItem(from, to)
                return true
            }
            
            override fun onSwiped(
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                direction: Int
            ) {
                // No swipe actions
            }
            
            override fun clearView(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                val tasks = taskAdapter.currentList
                viewModel.updateTaskOrder(tasks)
            }
        })
        
        touchHelper.attachToRecyclerView(binding.recyclerViewTasks)
    }
    
    private fun updateStatisticsUI(stats: com.anjez.data.repository.TaskStatistics) {
        binding.apply {
            textTotalTasks.text = "إجمالي المهام: ${stats.total}"
            textCompletedTasks.text = "المنجزة: ${stats.completed}"
            textInProgressTasks.text = "قيد التنفيذ: ${stats.inProgress}"
            textOverdueTasks.text = "المتأخرة: ${stats.overdue}"
            
            progressBar.progress = stats.progressPercentage
            textProgress.text = "${stats.progressPercentage}%"
        }
    }
    
    private fun showTaskDetails(task: com.anjez.data.local.Task) {
        // TODO: Implement task details dialog
    }
    
    private fun showTaskOptions(task: com.anjez.data.local.Task) {
        val options = arrayOf(
            "تعديل",
            task.status.getArabicName(),
            "حذف",
            "إلغاء"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle(task.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editTask(task)
                    1 -> viewModel.toggleTaskStatus(task)
                    2 -> deleteTask(task)
                }
            }
            .show()
    }
    
    private fun editTask(task: com.anjez.data.local.Task) {
        // TODO: Implement edit task
    }
    
    private fun deleteTask(task: com.anjez.data.local.Task) {
        MaterialAlertDialogBuilder(this)
            .setTitle("حذف المهمة")
            .setMessage("هل أنت متأكد من حذف '${task.title}'؟")
            .setPositiveButton("نعم") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("لا", null)
            .show()
    }
    
    private fun toggleSearchView() {
        if (binding.searchBar.isVisible) {
            binding.searchBar.hide()
            viewModel.setSearchQuery("")
        } else {
            binding.searchBar.show()
        }
    }
}
