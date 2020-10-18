package br.com.apsmobile.tarefasonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.apsmobile.tarefasonline.R
import br.com.apsmobile.tarefasonline.model.Task
import kotlinx.android.synthetic.main.task_item.view.*

class AdapterTask(
    private val tasks: List<Task>,
    private var clickListener: OnTaskItemClickListener
) : RecyclerView.Adapter<AdapterTask.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.cbCompleted.isChecked = task.completed

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(task)
        }

        holder.cbCompleted.setOnClickListener {
            task.completed = holder.cbCompleted.isChecked
            clickListener.onItemClick(task)
        }

    }

    override fun getItemCount(): Int = tasks.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.txtTitle
        val description: TextView = itemView.txtDescription
        val cbCompleted: CheckBox = itemView.cbCompleted
    }

    interface OnTaskItemClickListener {
        fun onItemClick(task: Task)
    }

}