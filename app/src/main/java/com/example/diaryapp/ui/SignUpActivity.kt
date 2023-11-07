package com.example.diaryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.diaryapp.FirebaseHandler
import com.example.diaryapp.R
import com.example.diaryapp.model.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseHandler: FirebaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseHandler = FirebaseHandler()

        val edtNama = findViewById<EditText>(R.id.edt_daftar_nama)
        val edtEmail = findViewById<EditText>(R.id.edt_daftar_email)
        val edtPass = findViewById<EditText>(R.id.edt_daftar_password)
        val btnDaftar = findViewById<Button>(R.id.btn_daftar_daftar)
        val btnMasuk = findViewById<Button>(R.id.btn_daftar_masuk)

        btnDaftar.setOnClickListener {
            val nama = edtNama.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPass.text.toString()
            when{
                nama.isEmpty() -> edtNama.error = resources.getString(R.string.not_empty)
                email.isEmpty() -> edtEmail.error = resources.getString(R.string.email_empty)
                password.isEmpty() -> edtPass.error = resources.getString(R.string.pass_empty)
                edtEmail.error==null && edtPass.error==null -> {
                    signUpUser(nama, email, password)
                }
            }
        }

        btnMasuk.setOnClickListener {
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser(nama : String, email : String, password : String){
        firebaseHandler.signUp(email,password){success, error ->
            if(success){
                Toast.makeText(
                    this@SignUpActivity,
                    resources.getString(R.string.signup_success),
                    Toast.LENGTH_SHORT
                ).show()
                firebaseHandler.insertUser(User(nama, email))
                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(
                    this@SignUpActivity,
                    resources.getString(R.string.signup_failed, error.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}