package com.example.cleanuper.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.cleanuper.MainActivity
import com.example.cleanuper.task.*
import com.example.cleanuper.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Подождите")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backButton.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.signUpButton.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        email = binding.emailForm.text.toString().trim()
        password = binding.passwordForm.text.toString().trim()
        val confirmedPassword = binding.passwordConfirmForm.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Почта неверно заполнена!", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Пароль должен состоять хотя бы из 6 символов!", Toast.LENGTH_SHORT).show()
        } else if (password != confirmedPassword) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
        } else {
            createAccount()
        }
    }

    private fun createAccount() {
        progressDialog.setMessage("Создаем аккаунт...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Не удалось создать аккаунт\n${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Сохранение...")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val usersData : HashMap<String, Any?> = HashMap()
        usersData["uid"] = uid
        usersData["email"] = email
        usersData["password"] = password
        usersData["timestamp"] = timestamp
        usersData["tasks"] = arrayListOf<Task>()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(usersData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Не удалось сохранить пользователя\n${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}