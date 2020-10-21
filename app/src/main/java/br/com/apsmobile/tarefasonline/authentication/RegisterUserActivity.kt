package br.com.apsmobile.tarefasonline.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.com.apsmobile.tarefasonline.MainActivity
import br.com.apsmobile.tarefasonline.R
import br.com.apsmobile.tarefasonline.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register_user.*
import kotlinx.android.synthetic.main.activity_register_user.edtEmail
import kotlinx.android.synthetic.main.activity_register_user.edtPassword
import kotlinx.android.synthetic.main.activity_register_user.progressBar
import kotlinx.android.synthetic.main.toolbar_voltar.*

class RegisterUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        ibBack.setOnClickListener { finish() }
        txtTitle.text = getString(R.string.form_label_register)

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener { validateRegister() }

    }

    // Valida as informações inseridas
    private fun validateRegister(){

        val name = edtName.text.toString()
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        if(!name.isBlank() && !email.isBlank() && !password.isBlank()){

            hideKeyboard()
            progressBar.visibility = View.VISIBLE

            registerUser(User(name = name, email = email, password = password))

        }else {
            Toast.makeText(this, "Preencha todos os dados", Toast.LENGTH_SHORT).show()
        }

    }

    // Criar a conta do Usuário no Firebase Autentication
    private fun registerUser(user: User){
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){

                    // Recupera o ID do cadastro
                    user.id = auth.currentUser!!.uid

                    // Salva os dados do Usuário no Firebase Data Base
                    saveUser(user)

                    // Fecha a Activity
                    finish()

                    // Leva o Usuário para página home do app
                    startActivity(Intent(this, MainActivity::class.java))
                }else {
                    validateRegister(task.exception?.message.toString())
                }
            }
    }

    // Salva os dados do Usuário no Firebase Data Base
    private fun saveUser(user: User){
        val firebaseRef = FirebaseDatabase.getInstance().reference
        val userRef = firebaseRef
            .child("users")
            .child(user.id)
        userRef.setValue(user)
    }

    // Oculta o teclado do dispositivo
    private fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

    // Exibe mensagem para o Usuário em caso de erro
    private fun validateRegister(error: String){
        showDialog(when(error){
            "The email address is already in use by another account." -> {
                getString(R.string.email_is_already_used)
            }
            "The given password is invalid. [ Password should be at least 6 characters ]" -> {
                getString(R.string.password_is_invalid)
            }
            "The email address is badly formatted." -> {
                getString(R.string.email_incorrect)
            }
            else -> {
                getString(R.string.register_other_error)
            }
        })
    }

    // Exibe dialog com a mensagem
    private fun showDialog(msg: String){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage(msg)

        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
        }

        alert = builder.create()
        alert.show()

        progressBar.visibility = View.GONE

    }

}