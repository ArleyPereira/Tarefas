package br.com.apsmobile.tarefasonline.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import br.com.apsmobile.tarefasonline.MainActivity
import br.com.apsmobile.tarefasonline.R
import br.com.apsmobile.tarefasonline.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.progressBar

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        txtCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
        }

        txtRecoverPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener { validateLogin() }

    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun validateLogin(){

        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        if(email.isNotBlank()){
            if(password.isNotBlank()){

                progressBar.visibility = View.VISIBLE
                hideKeyboard()

                loginIn(User(email = email, password = password))

            }else {
                edtPassword.requestFocus()
                edtPassword.error = "Preencha a senha."
            }
        }else {
            edtEmail.requestFocus()
            edtEmail.error = "Preencha o email."
        }

    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

    private fun loginIn(user: User){
        auth.signInWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }else {
                    validateLogin(task.exception?.message.toString())
                }
            }
    }

    private fun validateLogin(error: String){
        showDialog(when(error){
            "The password is invalid or the user does not have a password." -> {
                "Senha inválida."
            }
            "There is no user record corresponding to this identifier. The user may have been deleted." -> {
                "Nenhuma conta encontrada com este endereço de e-mail."
            }
            "The email address is badly formatted." -> {
                getString(R.string.email_incorrect)
            }
            else -> {
                getString(R.string.register_other_error)
            }
        })

        Log.i("INFOTESTE", "validateLogin: $error")

    }

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