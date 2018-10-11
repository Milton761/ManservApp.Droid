package org.codesolo.manservapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        resetpass_btn_back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        resetpass_btn_resetpass.setOnClickListener{

            val mAuth = firemanager.authentication
            val email = resetpass_etx_email.text.toString()

            mAuth!!.sendPasswordResetEmail(email)
        }



    }
}
