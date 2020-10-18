package br.com.apsmobile.tarefasonline.authentication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.com.apsmobile.tarefasonline.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.edtEmail
import kotlinx.android.synthetic.main.activity_forgot_password.progressBar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar_voltar.*

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        ibBack.setOnClickListener { finish() }
        txtTitle.text = getString(R.string.form_label_forgot_password)

        auth = FirebaseAuth.getInstance()

        btnSend.setOnClickListener {

            val email = edtEmail.text.toString()

            if(email.isNotBlank()){

                progressBar.visibility = View.VISIBLE

                hideKeyboard()

                sendEmail(email)
            }else {
                Toast.makeText(this, "Informe o email", Toast.LENGTH_SHORT).show()

            }

        }

    }

    private fun sendEmail(email: String){

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Pronto, já te enviamos!", Toast.LENGTH_SHORT).show()
            }else {
                validateLogin(task.exception?.message.toString())
            }

            progressBar.visibility = View.GONE

        }

    }

    private fun validateLogin(error: String){
        showDialog(when(error){
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

    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
    }

}