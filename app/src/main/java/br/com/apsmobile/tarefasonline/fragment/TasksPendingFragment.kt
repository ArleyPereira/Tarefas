package br.com.apsmobile.tarefasonline.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.apsmobile.tarefasonline.R
import br.com.apsmobile.tarefasonline.adapter.AdapterTask
import br.com.apsmobile.tarefasonline.model.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_tasks_pending.view.*

class TasksPendingFragment : Fragment(), AdapterTask.OnTaskItemClickListener {

    private lateinit var layout: View
    private lateinit var alert: AlertDialog
    private lateinit var taskRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var tasks = mutableListOf<Task>()
    private var adapter = AdapterTask(tasks, this)
    private var taskUpdate: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_tasks_pending, container, false)

        auth = FirebaseAuth.getInstance()
        taskRef = FirebaseDatabase.getInstance().reference.child("tasks")
            .child(auth.uid.toString())

        // Inicia RecyclerView
        initRecyclerView()

        // Recupera as Tarefas do Firebase
        getTasksFirebase()

        layout.fabNewTask.setOnClickListener {
            taskUpdate = null
            showDialog()
        }

        return layout
    }

    // Inicia RecyclerView
    private fun initRecyclerView() {
        layout.rvTask.adapter = adapter
        val layoutManager = LinearLayoutManager(activity!!)
        layout.rvTask.layoutManager = layoutManager
    }

    // Recupera as Tarefas do Firebase
    private fun getTasksFirebase() {
        taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                tasks.clear()
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val task = snap.getValue(Task::class.java) as Task
                        if(!task.completed){
                            tasks.add(task)
                        }
                    }
                }

                // Exibe uma mensagem caso não possuia tarefas
                setInfo()

                tasks.reverse()
                layout.progressBar.visibility = View.GONE
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("INFOTESTE", "onCancelled: $error")
            }

        })
    }

    // Exibe Dialog para Editar / Deleção
    private fun showDialog() {

        val builder = AlertDialog.Builder(activity!!)

        openKeyboard()

        val view = layoutInflater.inflate(R.layout.dialog_new_task, null)

        val edtTitle: EditText = view.findViewById(R.id.edtTitle)
        edtTitle.requestFocus()
        val edtDescription: EditText = view.findViewById(R.id.edtDescription)

        val btnClose: Button = view.findViewById(R.id.btnClose)
        val btnSave: Button = view.findViewById(R.id.btnSave)

        val ibDelete: ImageButton = view.findViewById(R.id.ibDelete)

        taskUpdate?.let {
            edtTitle.setText(it.title)
            edtDescription.setText(it.description)
            ibDelete.visibility = View.VISIBLE
        }

        ibDelete.setOnClickListener {
            taskUpdate?.let { it1 -> deleteTask(it1) }
        }

        btnClose.setOnClickListener {
            alert.dismiss()
            hideKeyboard()
        }

        btnSave.setOnClickListener {

            val title = edtTitle.text.toString()
            val description = edtDescription.text.toString()

            if (title.isNotBlank() && description.isNotBlank()) {

                if (taskUpdate == null) {
                    val task = Task(title = title, description = description)
                    saveTask(task)
                } else {
                    taskUpdate!!.title = title
                    taskUpdate!!.description = description
                    saveTask(taskUpdate!!)
                }

                alert.dismiss()

            } else {
                Toast.makeText(activity!!, "Preencha todos os dados", Toast.LENGTH_SHORT).show()
            }

        }

        builder.setView(view)

        alert = builder.create()
        alert.show()
    }

    // Deleta a Tarefa do Firebase
    private fun deleteTask(task: Task) {
        val taskRef = taskRef
            .child(task.id)
        taskRef.removeValue()

        tasks.remove(task)

        val pos = tasks.indexOf(task)
        adapter.notifyItemRemoved(pos)

        alert.dismiss()

        // Exibe uma mensagem caso não possuia tarefas
        setInfo()

    }

    // Exibe uma mensagem caso não possuia tarefas
    private fun setInfo() {
        if (tasks.size == 0) {
            layout.textInfo.text = "Nenhuma tarefa pendente."
        } else {
            layout.textInfo.text = ""
        }
    }

    private fun openKeyboard() {
//        val imm: InputMethodManager =
//            getSystemService(activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun hideKeyboard() {
//        val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

    // Salva a Tarefa no Firebase
    private fun saveTask(task: Task) {
        val taskRef = taskRef
            .child(task.id)
        taskRef.setValue(task)
    }

    override fun onItemClick(task: Task) {
        if(task.completed){
            saveTask(task)

            Snackbar.make(layout, "Tarefa feita.", Snackbar.LENGTH_LONG).setAction("Desfazer") {
                task.completed = false
                saveTask(task)
            }.show()

        }else {
            taskUpdate = task
            showDialog()
        }
    }

}