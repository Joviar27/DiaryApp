package com.example.diaryapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diaryapp.FirebaseHandler
import com.example.diaryapp.R

class SignInActivity : AppCompatActivity() {

    private lateinit var firebaseHandler: FirebaseHandler

    override fun onStart() {
        super.onStart()
        firebaseHandler.updateUI(this@SignInActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        firebaseHandler = FirebaseHandler()

        val edtEmail = findViewById<EditText>(R.id.edt_masuk_email)
        val edtPass = findViewById<EditText>(R.id.edt_masuk_password)
        val btnMasuk = findViewById<Button>(R.id.btn_masuk_masuk)
        val btnDaftar = findViewById<Button>(R.id.btn_masuk_daftar)

        btnMasuk.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPass.text.toString()
            when{
                email.isEmpty() -> edtEmail.error = resources.getString(R.string.email_empty)
                password.isEmpty() -> edtPass.error = resources.getString(R.string.pass_empty)
                edtEmail.error==null && edtPass.error==null -> {
                    signInUser(email, password)
                }
            }

        }

        btnDaftar.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInUser(email:String, password : String){
        firebaseHandler.signIn(email, password) { success, error ->
            if(success){
                Toast.makeText(
                    this@SignInActivity,
                    resources.getString(R.string.signin_success),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(
                    this@SignInActivity,
                    resources.getString(R.string.signin_failed, error.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}