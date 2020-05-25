package com.doobidapp.todowee

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.doobidapp.todowee.models.AppDatabase
import com.doobidapp.todowee.models.ToDo
import kotlinx.android.synthetic.main.todo_list_item.view.*

interface ToDoAdapterDelegate {
    fun reloadData()
    fun editToDo(toDo: ToDo)
}

class ToDoAdapter(var items: List<ToDo>?, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var delegate: ToDoAdapterDelegate? = null

    override fun getItemCount(): Int {
        if(items!!.count() == 0) {
            return 1
        } else {
            return items!!.count()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(items!!.count() > 0) {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.todo_list_item,
                    parent,
                    false
                )
            )
        } else {
            return ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.todo_list_item_empty_state,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items!!.count() > 0) {
            val toDoHolder = holder as? ViewHolder
            toDoHolder?.cbToDo?.text = items?.get(position)?.taskName
            toDoHolder?.cbToDo?.isChecked = items?.get(position)?.isDone!!
            if (items!!.get(position).isDone) {
                toDoHolder?.cbToDo?.paintFlags =
                    (toDoHolder?.cbToDo?.paintFlags!! or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                toDoHolder?.cbToDo?.paintFlags =
                    (toDoHolder?.cbToDo?.paintFlags!! and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }

            toDoHolder?.cbToDo?.setOnCheckedChangeListener { _, checked ->
                val database = AppDatabase.getInstance(context)
                val toDo = this.items?.get(position)
                toDo!!.isDone = checked
                database?.toDoDao()?.updateAll(toDo)
                this.delegate?.reloadData()
            }

            toDoHolder?.cbToDo?.setOnLongClickListener { _ ->
                this.delegate?.editToDo(this.items!![position])
                return@setOnLongClickListener true
            }
        }
    }

    fun reloadData(items: List<ToDo>?) {
        this.items = items
        notifyDataSetChanged()
    }
}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val cbToDo: CheckBox? = view.cb_todo
}