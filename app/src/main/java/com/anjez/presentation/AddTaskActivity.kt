package com.anjez.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.anjez.databinding.ActivityAddTaskBinding
import com.anjez.data.local.Priority
import com.anjez.presentation.viewmodel.TaskViewModel
import com.anjez.presentation.viewmodel.TaskViewModelFactory
import java.util.*

class AddTaskActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var viewModel: TaskViewModel
    private var selectedDate: Calendar? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupUI()
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "إضافة مهمة جديدة"
        
        val priorities = listOf(
            "عالية" to Priority.HIGH,
            "متوسطة" to Priority.MEDIUM,
            "منخفضة" to Priority.LOW
        )
        
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            priorities.map { it.first }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter
        binding.spinnerPriority.setSelection(1)
        
        binding.textInputLayoutDueDate.setEndIconOnClickListener {
            showDatePicker()
        }
        
        binding.editTextDueDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.buttonSave.setOnClickListener {
            saveTask()
        }
        
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                selectedDate?.apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                updateDateText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
    
    private fun updateDateText() {
        selectedDate?.let { calendar ->
            val dateFormat = android.text.format.DateFormat.getMediumDateFormat(this)
            val timeFormat = android.text.format.DateFormat.getTimeFormat(this)
            val date = dateFormat.format(calendar.time)
            val time = timeFormat.format(calendar.time)
            binding.editTextDueDate.setText("$date $time")
        }
    }
    
    private fun saveTask() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.editTextTitle.error = "الرجاء إدخال عنوان للمهمة"
            return
        }
        
        val priorityIndex = binding.spinnerPriority.selectedItemPosition
        val priority = when (priorityIndex) {
            0 -> Priority.HIGH
            1 -> Priority.MEDIUM
            else -> Priority.LOW
        }
        
        val dueDate = selectedDate?.time
        
        viewModel.addTask(title, description, priority, dueDate)
        finish()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    companion object {
        fun newIntent(context: android.content.Context): android.content.Intent {
            return android.content.Intent(context, AddTaskActivity::class.java)
        }
    }
}
