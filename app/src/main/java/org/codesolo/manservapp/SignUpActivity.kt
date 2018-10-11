package org.codesolo.manservapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val tag = SignUpActivity::class.java.name

        signup_btn_signup.setOnClickListener {

            val mAuth = firemanager.authentication
            val email = signup_etx_email.text.toString()
            val pass  = signup_etx_pass.text.toString()

            mAuth!!.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in: success
                            // update UI for current User
                            val user:FirebaseUser? = mAuth.currentUser
                            finish()
                            Log.i(tag,"complete")
                            startActivity(Intent(this, LoginActivity::class.java))
                            user!!.sendEmailVerification()

                        } else {
                            // Sign in: fail
                            Log.i(tag,"fail")
                        }
                    }

        }

        signup_btn_resetpass.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        signup_btn_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }




    }
}
