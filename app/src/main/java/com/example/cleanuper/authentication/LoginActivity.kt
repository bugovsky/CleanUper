package com.example.cleanuper.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.cleanuper.MainActivity
import com.example.cleanuper.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding;
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Подождите")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.loginButton.setOnClickListener {
            validateData()
        }
        binding.notSignedUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateData() {
        email = binding.emailForm.text.toString().trim()
        password = binding.passwordForm.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Почта неверно заполнена!", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Пропущено поле с паролем!", Toast.LENGTH_SHORT).show()
        } else {
            checkIfExists()
        }
    }

    private fun checkIfExists() {
        progressDialog.setMessage("Входим в аккаунт...")
        progressDialog.show()

        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { result ->
                val signInMethods = result.signInMethods
                if (signInMethods != null && signInMethods.isEmpty()) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Аккаунт не найден", Toast.LENGTH_SHORT).show()
                } else {
                    login()
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Ошибка при проверке аккаунта: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun login() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Введенный пароль неверен!", Toast.LENGTH_SHORT).show()
            }

    }
}