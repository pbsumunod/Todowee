package com.doobidapp.todowee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_todo_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.doobidapp.todowee.models.AppDatabase
import com.doobidapp.todowee.models.ToDo
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), ToDoAdapterDelegate {

    private var database: AppDatabase? = null
    private var toDoAdapter: ToDoAdapter? = null
    private var toDoAdapterLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getInstance(this)
        toDoAdapter = ToDoAdapter(database?.toDoDao()?.getAll(), this)
        toDoAdapterLayoutManager = LinearLayoutManager(this)
        toDoAdapter?.delegate = this

        val pattern = "d MMMM"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = simpleDateFormat.format(Date())

        lb_date.setText(date)
        setupGreetings()

        rv_todo_list.layoutManager = toDoAdapterLayoutManager
        rv_todo_list.adapter = toDoAdapter!!

        fab_add_todo.setScaleType(ImageView.ScaleType.CENTER)
        fab_add_todo.setOnClickListener {
            this.showAddTodoDialog()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
    }

    override fun reloadData() {
        rv_todo_list.post {
            this.toDoAdapter?.reloadData(this.database?.toDoDao()?.getAll())
            this.setupGreetings()
            rv_todo_list.adapter = this.toDoAdapter
        }
    }

    override fun editToDo(toDo: ToDo) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(getString(R.string.edit_todo_dialog_title))
        val dialogLayout = inflater.inflate(R.layout.add_todo_dialog, null)
        val editText  = dialogLayout.et_todo_name
        editText.setText(toDo.taskName)
        builder.setView(dialogLayout)
        builder.setPositiveButton(getString(R.string.add_todo_dialog_save)) {  _, _ ->
            toDo.taskName = editText.text.toString()
            this.database?.toDoDao()?.updateAll(toDo)
            this.reloadData()
        }
        builder.setNeutralButton(getString(R.string.edit_todo_dialog_delete)) { _, _ ->
            this.database?.toDoDao()?.deleteTodo(toDo)
            this.reloadData()
        }
        builder.setNegativeButton(getString(R.string.add_todo_dialog_cancel)) { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        builder.show()
    }

    private fun showAddTodoDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(getString(R.string.add_todo_dialog_title))
        val dialogLayout = inflater.inflate(R.layout.add_todo_dialog, null)
        val editText  = dialogLayout.et_todo_name
        builder.setView(dialogLayout)
        builder.setPositiveButton(getString(R.string.add_todo_dialog_save)) {  _, _ ->
            val newTodo = ToDo()
            newTodo.taskName = editText.text.toString()
            this.database?.toDoDao()?.insertAll(newTodo)
            this.reloadData()
        }
        builder.setNegativeButton(getString(R.string.add_todo_dialog_cancel)) { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        builder.show()
    }

    private fun setupGreetings() {
        val taskCount = database?.toDoDao()?.getTasksCount()
        if(taskCount!! > 1) {
            lb_greeting.setText(getString(R.string.greetings, taskCount.toString(), "tasks"))
        } else {
            lb_greeting.setText(getString(R.string.greetings, taskCount.toString(), "task"))
        }


    }
}