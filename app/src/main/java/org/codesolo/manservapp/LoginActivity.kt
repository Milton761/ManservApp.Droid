package org.codesolo.manservapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val mAuth = firemanager.authentication
        val mUser = mAuth!!.currentUser

        if(mUser != null)
        {
            finish()
            startActivity(Intent(this, StartReportActivity::class.java))
        }else{


            login_btn_signup.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
            }

            login_btn_resetpass.setOnClickListener {
                startActivity(Intent(this, ResetPasswordActivity::class.java))
            }

            login_btn_login.setOnClickListener {

                val email = login_etx_email.text.toString()
                val pass = login_etx_pass.text.toString()

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(this, "Enter pass", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                progressBar.visibility = View.VISIBLE


//test
                mAuth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {

                        val user: FirebaseUser? = mAuth.currentUser
                        if (user!!.isEmailVerified) {
                            Log.i(LoginActivity::class.java.name, "task complete")
                            finish()
                            startActivity(Intent(this, StartReportActivity::class.java))
                        } else {
                            Log.i(LoginActivity::class.java.name, "email not verified")
                        }
                    } else {
                        Log.i(LoginActivity::class.java.name, "task incomplete")
                    }
                }

            }

        }




    }
}
